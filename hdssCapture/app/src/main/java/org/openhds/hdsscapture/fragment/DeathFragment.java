package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentDeathBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeathFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeathFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "DEATH.TAG";
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDeathBinding binding;
    private Individual selectedIndividual;
    private Locations selectedLocation;

    public DeathFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment DeathFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeathFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        DeathFragment fragment = new DeathFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeathBinding.inflate(inflater, container, false);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        //CHOOSING THE DATE
        setupDatePickers();

        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = configViewModel.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                String formattedDate = dateFormat.format(dt);
                editText.setText(formattedDate);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        DeathViewModel viewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Death data = viewModel.finds(selectedIndividual.uuid);
            Residency dataRes = resModel.findRes(selectedIndividual.uuid, selectedLocation.uuid);
            if (data != null) {
                if (dataRes != null){
                    //data.dob = dataRes.startDate;
                    binding.setRes(dataRes);
                    data.residency_uuid = dataRes.uuid;
                }
                binding.setDeath(data);

            } else {
                data = new Death();

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dts = visitViewModel.find(socialgroup.getUuid());
                if (dts != null){
                    data.visit_uuid = dts.uuid;
                }



                if (dataRes != null){
                    //data.dob = dataRes.startDate;
                    binding.setRes(dataRes);
                    data.residency_uuid = dataRes.uuid;
                }

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.firstName = selectedIndividual.getFirstName();
                data.lastName = selectedIndividual.getLastName();
                data.gender = selectedIndividual.getGender();
                data.compno = selectedLocation.compno;
                data.extId = selectedIndividual.getExtId();
                data.compname = selectedLocation.locationName;
                data.individual_uuid = selectedIndividual.getUuid();
                data.dob = selectedIndividual.dob;
                data.villname = level6Data.getName();
                data.villcode = level6Data.getExtId();
                data.complete = 1;
                data.edit = 1;
                //data.edit = null;
                //data.visit_uuid = dts.uuid;
                data.househead = socialgroup.getGroupName();
                data.socialgroup_uuid = socialgroup.uuid;

                Date currentDate = new Date(); // Get the current date and time
                // Create a Calendar instance and set it to the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;


                binding.setDeath(data);
                binding.getDeath().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.dthDeathPlace, "deathPlace");
        loadCodeData(binding.dthDeathCause, "deathCause");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, DeathViewModel viewModel) {

        if (save) {
            Death finalData = binding.getDeath();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.dthDeathDate.getText().toString().trim().isEmpty()
                        && !binding.dthDob.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.dthDeathDate.getText().toString().trim());
                    Date dob = f.parse(binding.dthDob.getText().toString().trim());
                    Date currentDate = new Date();
                    if (edate.before(stdate)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.before(dob)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.after(currentDate)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Future Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.dthDeathDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            Date end = new Date(); // Get the current date and time
            // Create a Calendar instance and set it to the current date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            // Extract the hour, minute, and second components
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            // Format the components into a string with leading zeros
            String endtime = String.format("%02d:%02d:%02d", hh, mm, ss);

            if (finalData.sttime !=null && finalData.edtime==null){
                finalData.edtime = endtime;
            }

            VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
            Vpm v = new Vpm();
            v.complete = 1;
            v.uuid = binding.getDeath().uuid;
            v.individual_uuid = binding.getDeath().individual_uuid;
            v.deathDate = binding.getDeath().deathDate;
            v.dob = binding.getDeath().dob;
            v.insertDate =binding.getDeath().insertDate;
            v.firstName = binding.getDeath().firstName;
            v.lastName = binding.getDeath().lastName;
            v.gender = binding.getDeath().gender;
            v.compno = binding.getDeath().compno;
            v.visit_uuid = binding.getDeath().visit_uuid;
            v.deathCause = binding.getDeath().deathCause;
            v.deathPlace = binding.getDeath().deathPlace;
            v.deathPlace_oth = binding.getDeath().deathPlace_oth;
            v.respondent = binding.getDeath().respondent;
            v.fw_uuid = binding.getDeath().fw_uuid;
            v.extId = binding.getDeath().extId;
            v.househead = binding.getDeath().househead;
            v.compname = binding.getDeath().compname;
            v.villname = binding.getDeath().villname;
            v.villcode = binding.getDeath().villcode;
            vpmViewModel.add(v);

            RelationshipViewModel relModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                //Set Relationship to Widowed
                try {
                    // Second block - visited update (with different variable name)
                    Relationship reldata = relModel.finds(selectedIndividual.uuid);
                    if (reldata != null) {
                        RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                        relationshipUpdate.endType = 2;
                        relationshipUpdate.endDate = binding.getDeath().deathDate;
                        relationshipUpdate.individualA_uuid = selectedIndividual.uuid;
                        relationshipUpdate.complete = 1;

                        relModel.update(relationshipUpdate, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Relationship Update successful (widowed)!");
                                    } else {
                                        Log.d("DeathFragment", "Relationship Update Failed (widowed)!");
                                    }
                                })
                        );
                    }
                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //Set Relationship to Dead
                try {
                    Relationship dthdata = relModel.find(selectedIndividual.uuid);
                    if (dthdata != null && !binding.dthDeathDate.getText().toString().trim().isEmpty()) {

                        RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                        relationshipUpdate.endType = 4;
                        relationshipUpdate.endDate = binding.getDeath().deathDate;
                        relationshipUpdate.individualA_uuid = selectedIndividual.uuid;
                        relationshipUpdate.complete = 1;

                        relModel.update(relationshipUpdate, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Relationship Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Relationship Update Failed (dead)!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //End Residency In residency entity
                try {
                    Residency resdata = resModel.dth(selectedIndividual.uuid, selectedLocation.uuid);
                    if (resdata != null) {
                        ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                        residencyAmendment.endType = 3;
                        residencyAmendment.endDate = binding.getDeath().deathDate;
                        residencyAmendment.uuid = binding.getDeath().residency_uuid;
                        residencyAmendment.complete = 1;

                        resModel.update(residencyAmendment, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Residency Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Residency Update Failed (dead)!");
                                    }
                                })
                        );

                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }


                //End Residency In individual entity
                try {
                    Individual inddata = individualViewModel.find(selectedIndividual.uuid);
                    if (inddata != null) {
                        IndividualEnd endInd = new IndividualEnd();
                        endInd.endType = 3;
                        endInd.uuid = binding.getDeath().individual_uuid;
                        endInd.complete = 1;

                        individualViewModel.dthupdate(endInd, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Individual Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Individual Update Failed (dead)!");
                                    }
                                })
                        );

                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }


            });

            executor.shutdown();


            finalData.complete = 1;
            viewModel.add(finalData);

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private <T> void callable(Spinner spinner, T[] array) {

        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void loadCodeData(Spinner spinner, final String codeFeature) {
        final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        try {
            List<KeyValuePair> list = viewModel.findCodesOfFeature(codeFeature);
            KeyValuePair kv = new KeyValuePair();
            kv.codeValue = AppConstants.NOSELECT;
            kv.codeLabel = AppConstants.SPINNER_NOSELECT;
            if (list != null && !list.isEmpty()) {
                list.add(0, kv);
                callable(spinner, list.toArray(new KeyValuePair[0]));
            } else {
                list = new ArrayList<KeyValuePair>();
                list.add(kv);

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    
    private enum DATE_BUNDLES {
        DEATHDATE("DEATHDATE");

        private final String bundleKey;

        DATE_BUNDLES(String bundleKey) {
            this.bundleKey = bundleKey;

        }

        public String getBundleKey() {
            return bundleKey;
        }

        @Override
        public String toString() {
            return bundleKey;
        }
    }

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, DATE_BUNDLES.DEATHDATE, binding.dthDeathDate);

        });

        binding.buttonDeathDod.setEndIconOnClickListener(v ->
                showDatePicker(DeathFragment.DATE_BUNDLES.DEATHDATE, binding.dthDeathDate));


    }

    private void handleDateResult(Bundle bundle, DeathFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(DeathFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        Calendar calendar = parseCurrentDate(editText.getText().toString());
        DialogFragment datePickerFragment = new DatePickerFragment(
                dateType.getBundleKey(),
                calendar
        );
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), TAG);
    }

    private Calendar parseCurrentDate(String dateString) {
        Calendar calendar = Calendar.getInstance();

        if (!TextUtils.isEmpty(dateString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date date = sdf.parse(dateString);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }
}