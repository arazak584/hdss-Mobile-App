package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.Expose;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentDeathBinding;
import org.openhds.hdsscapture.databinding.FragmentMembershipBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeathFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeathFragment extends DialogFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "DEATH.TAG";
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDeathBinding binding;

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

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
                    if (bundle.containsKey((DeathFragment.DATE_BUNDLES.DEATHDATE.getBundleKey()))) {
                        final String result = bundle.getString(DeathFragment.DATE_BUNDLES.DEATHDATE.getBundleKey());
                        binding.dthDeathDate.setText(result);
                    }
                });

        binding.buttonDeathDod.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.dthDeathDate.getText())) {
                // If replDob is not empty, parse the date and use it as the initial date
                String currentDate = binding.dthDeathDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(DeathFragment.DATE_BUNDLES.DEATHDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(DeathFragment.DATE_BUNDLES.DEATHDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

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
        try {
            Death data = viewModel.finds(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setDeath(data);
            } else {
                data = new Death();

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dts = visitViewModel.find(socialgroup.uuid);
                if (dts != null){
                    data.visit_uuid = dts.uuid;
                }

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.dob = HouseMembersFragment.selectedIndividual.startDate;
                data.firstName = HouseMembersFragment.selectedIndividual.getFirstName();
                data.lastName = HouseMembersFragment.selectedIndividual.getLastName();
                data.gender = HouseMembersFragment.selectedIndividual.getGender();
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.extId = HouseMembersFragment.selectedIndividual.getExtId();
                data.compname = ClusterFragment.selectedLocation.getLocationName();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.villname = level6Data.getName();
                data.villcode = level6Data.getExtId();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.complete = 1;
                data.househead = socialgroup.getGroupName();
                data.residency_uuid = HouseMembersFragment.selectedIndividual.getResidency();
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

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, DeathViewModel viewModel) {

        if (save) {
            Death finalData = binding.getDeath();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
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
            v.uuid = finalData.uuid;
            v.individual_uuid = finalData.individual_uuid;
            v.deathDate = finalData.deathDate;
            v.dob = finalData.dob;
            v.insertDate =finalData.insertDate;
            v.firstName = finalData.firstName;
            v.lastName = finalData.lastName;
            v.gender = finalData.gender;
            v.compno = finalData.compno;
            v.visit_uuid = finalData.visit_uuid;
            v.deathCause = finalData.deathCause;
            v.deathPlace = finalData.deathPlace;
            v.deathPlace_oth = finalData.deathPlace_oth;
            v.respondent = finalData.respondent;
            v.fw_uuid = finalData.fw_uuid;
            v.extId = finalData.extId;
            v.househead = finalData.househead;
            v.compname = finalData.compname;
            v.villname = finalData.villname;
            v.villcode = finalData.villcode;
            vpmViewModel.add(v);

            viewModel.add(finalData);

            //Set Relationship to Widowed
            RelationshipViewModel relbModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            try {
                Relationship data = relbModel.finds(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null && !binding.dthDeathDate.getText().toString().trim().isEmpty()) {

                    RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                    relationshipUpdate.endType = 2;
                    relationshipUpdate.endDate = binding.getDeath().deathDate;
                    relationshipUpdate.individualA_uuid = HouseMembersFragment.selectedIndividual.uuid;
                    relationshipUpdate.complete = 1;
                    relbModel.update(relationshipUpdate);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Set Relationship to Dead
            RelationshipViewModel relModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            try {
                Relationship data = relModel.find(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null && !binding.dthDeathDate.getText().toString().trim().isEmpty()) {

                    RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                    relationshipUpdate.endType = 4;
                    relationshipUpdate.endDate = binding.getDeath().deathDate;
                    relationshipUpdate.individualA_uuid = HouseMembersFragment.selectedIndividual.uuid;
                    relationshipUpdate.complete = 1;
                    relModel.update(relationshipUpdate);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //End Residency In residency entity
            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            try {
                Residency data = resModel.dth(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                    residencyAmendment.endType = 3;
                    residencyAmendment.endDate = binding.getDeath().deathDate;
                    residencyAmendment.uuid = binding.getDeath().residency_uuid;
                    residencyAmendment.complete = 1;

                    resModel.update(residencyAmendment);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //End Residency In individual entity
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual data = individualViewModel.find(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    IndividualEnd endInd = new IndividualEnd();
                    endInd.endType = 3;
                    endInd.endDate = binding.getDeath().deathDate;
                    endInd.uuid = binding.getDeath().individual_uuid;
                    endInd.complete = 1;

                    individualViewModel.dthupdate(endInd);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
}