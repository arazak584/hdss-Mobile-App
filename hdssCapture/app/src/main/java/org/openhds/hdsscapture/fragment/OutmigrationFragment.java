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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutmigrationBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
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
 * Use the {@link OutmigrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutmigrationFragment extends DialogFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OMG.TAG";
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentOutmigrationBinding binding;

    public OutmigrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment OutmigrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutmigrationFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        OutmigrationFragment fragment = new OutmigrationFragment();
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
        binding = FragmentOutmigrationBinding.inflate(inflater, container, false);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (bundle.containsKey((OutmigrationFragment.DATE_BUNDLES.DATE.getBundleKey()))) {
                final String result = bundle.getString(OutmigrationFragment.DATE_BUNDLES.DATE.getBundleKey());
                binding.omgDate.setText(result);
            }
        });

        binding.buttonOmgImgDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.omgDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.omgDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(OutmigrationFragment.DATE_BUNDLES.DATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(OutmigrationFragment.DATE_BUNDLES.DATE.getBundleKey(), c);
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

        OutmigrationViewModel viewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        try {
            Outmigration data = viewModel.edit(HouseMembersFragment.selectedIndividual.uuid,ClusterFragment.selectedLocation.uuid);
            VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            if (data != null) {
                binding.setOutmigration(data);
            } else {
                data = new Outmigration();

                Visit dts = visitViewModel.find(socialgroup.uuid);
                if (dts != null){
                    data.visit_uuid = dts.uuid;
                }

                ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
                Residency dataRes = resModel.findRes(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.uuid);
                if (dataRes != null){
                    data.startDate = dataRes.startDate;
                    data.residency_uuid = dataRes.uuid;
                }

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                data.uuid=uuidString;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.complete = 1;
                data.socialgroup_uuid = socialgroup.uuid;
                data.location_uuid = ClusterFragment.selectedLocation.uuid;

                Date currentDate = new Date(); // Get the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;


                binding.setOutmigration(data);
                binding.getOutmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.reasonOut,  "reasonForOutMigration");
        loadCodeData(binding.destination,  "whereoutside");

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

    private void save(boolean save, boolean close, OutmigrationViewModel viewModel) {

        if (save) {
            Outmigration finalData = binding.getOutmigration();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.omgDate.getText().toString().trim().isEmpty()
                        && !binding.startdate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.omgDate.getText().toString().trim());
                    Date start = f.parse(binding.startdate.getText().toString().trim());
                    Date currentDate = new Date();
                    if (edate.before(stdate)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Date of Outmigration  Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.before(start)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "Date of Outmigration Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.after(currentDate)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Future Date");
                        Toast.makeText(getActivity(), "Date of Outmigration Cannot Be Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.omgDate.setError(null);
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
            finalData.complete = 1;

            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                //1==No
                //End Residency In residency entity
                try {
                    Residency data = resModel.dth(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                        residencyAmendment.endType = 2;
                        residencyAmendment.endDate = binding.getOutmigration().recordedDate;
                        residencyAmendment.uuid = binding.getOutmigration().residency_uuid;
                        residencyAmendment.complete = 1;

                        resModel.update(residencyAmendment, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("OmgFragment", "Residency Update successful!");
                                    } else {
                                        Log.d("OmgFragment", "Residency Update Failed!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("OmgFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //End Residency In individual entity
                try {
                    Individual data = individualViewModel.find(HouseMembersFragment.selectedIndividual.uuid);
                    if (data != null) {
                        IndividualEnd endInd = new IndividualEnd();
                        endInd.endType = 2;
                        endInd.uuid = binding.getOutmigration().individual_uuid;
                        endInd.complete = 1;

                        individualViewModel.dthupdate(endInd, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("OmgFragment", "Death Update successful!");
                                    } else {
                                        Log.d("OmgFragment", "Death Update Failed!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("OmgFragment", "Error in update", e);
                    e.printStackTrace();
                }




            });

            executor.shutdown();

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
        DATE("DATE");

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