package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentDemographicBinding;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualPhone;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemographicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemographicFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    public static final String DENO_INFO = "file:///android_asset/deno_views.html";

    // TODO: Rename and change types of parameters
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDemographicBinding binding;
    private Demographic demographic;
    private Individual selectedIndividual;
    private Locations selectedLocation;

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog simpleDialog = SimpleDialog.newInstance(message, codeFragment);
        simpleDialog.show(getChildFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }

    public DemographicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment DemographicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemographicFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        DemographicFragment fragment = new DemographicFragment();
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
        binding = FragmentDemographicBinding.inflate(inflater, container, false);
        binding.setDemographic(demographic);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        ImageButton appInfoButton = binding.getRoot().findViewById(R.id.deno_button);
        appInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denoInfo(v);
            }
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        DemographicViewModel viewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        try {
            Demographic data = viewModel.find(selectedIndividual.uuid);
            if (data != null) {
                binding.setDemographic(data);
                binding.getDemographic().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.location_uuid = selectedLocation.getUuid();

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

//                if (data.phone1 != null){
//                    data.phone =1;
//                }

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

            } else {
                data = new Demographic();

                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.individual_uuid = selectedIndividual.getUuid();
                data.location_uuid = selectedLocation.getUuid();
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

                binding.setDemographic(data);
                binding.getDemographic().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        IndividualViewModel indage = new ViewModelProvider(this).get(IndividualViewModel.class);
        try {
            Individual datae = indage.find(selectedIndividual.uuid);
            if (datae != null) {
                binding.setIndividual(datae);

                AppCompatEditText age = binding.getRoot().findViewById(R.id.age);
                age.setText(String.valueOf(datae.getAge()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //LOAD SPINNERS
        loadCodeData(binding.tribe, "tribe");
        loadCodeData(binding.religion,  "religion");
        loadCodeData(binding.education, "education");
        loadCodeData(binding.occupation, "occupation");
        loadCodeData(binding.marital, "marital");
        loadCodeData(binding.demoComplete, "submit");
        loadCodeData(binding.demoPhone, "complete");
        loadCodeData(binding.akan, "akan");
        loadCodeData(binding.denomination, "denomination");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.DEMOGRAPHICLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, DemographicViewModel viewModel) {

        if (save) {
            Demographic finalData = binding.getDemographic();

            boolean compyrs = false;
            if (!binding.compYrs.getText().toString().trim().isEmpty()) {
                int yrs = Integer.parseInt(binding.compYrs.getText().toString().trim());
                if (yrs < 0 || yrs > 6) {
                    compyrs = true;
                    binding.compYrs.setError("Cannot be less than 1 or More than 6");
                    Toast.makeText(getActivity(), "Cannot be less than 1 or More than 6", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean ph = false;
            if (!binding.phone1.getText().toString().trim().isEmpty()) {
                String input = binding.phone1.getText().toString().trim();
                String regex = "[0-9]{10}";

                if (!input.matches(regex)) {
                    ph = true;
                    Toast.makeText(getActivity(), "Phone Number is incorrect", Toast.LENGTH_LONG).show();
                    binding.phone1.setError("Phone Number is incorrect");
                    return;
                }
            }

            // Nullify fields for individuals younger than 11
            if (!binding.age.getText().toString().trim().isEmpty() && binding.getIndividual() != null && binding.getIndividual().age < 12) {
                Log.d("Demo", "Set Demographic to null");
                finalData.phone1 = null;
                finalData.occupation = null;
                finalData.marital = null;
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.DEMOGRAPHICLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {

                Individual visitedData = iview.visited(selectedIndividual.uuid);
                if (visitedData != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = binding.getDemographic().individual_uuid;
                    visited.complete = 1;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {

                Individual data = iview.find(selectedIndividual.uuid);
                String phone1 = binding.getDemographic().phone1;
                if (data != null && phone1 != null && phone1.length() == 10) {
                    IndividualPhone cnt = new IndividualPhone();
                    cnt.uuid = finalData.individual_uuid;
                    cnt.phone1 = binding.getDemographic().phone1;
                    iview.contact(cnt);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(() -> {
//
//                try {
//                    // Second block - visited update (with different variable name)
//                    Individual visitedData = iview.visited(selectedIndividual.uuid);
//                    if (visitedData != null) {
//                        IndividualVisited visited = new IndividualVisited();
//                        visited.uuid = binding.getDemographic().individual_uuid;
//                        visited.complete = 1;
//
//                        iview.visited(visited, result ->
//                                new Handler(Looper.getMainLooper()).post(() -> {
//                                    if (result > 0) {
//                                        Log.d("DemographicFragment", "Visit Update successful!");
//                                    } else {
//                                        Log.d("DemographicFragment", "Visit Update Failed!");
//                                    }
//                                })
//                        );
//                    }
//                } catch (Exception e) {
//                    Log.e("DemographicFragment", "Error in visited update", e);
//                    e.printStackTrace();
//                }
//
//
//                //Update Phone Number for Individual
//                try {
//                    Individual data = iview.find(selectedIndividual.uuid);
//                    String phone1 = binding.getDemographic().phone1;
//                    if (data != null && phone1 != null && phone1.length() == 10) {
//                        IndividualPhone cnt = new IndividualPhone();
//                        cnt.uuid = finalData.individual_uuid;
//                        cnt.phone1 = binding.getDemographic().phone1;
//
//                        iview.contact(cnt, result ->
//                                new Handler(Looper.getMainLooper()).post(() -> {
//                                    if (result > 0) {
//                                        Log.d("DemographicFragment", "Phone Update successful!");
//                                    } else {
//                                        Log.d("DemographicFragment", "Phone Update Failed!");
//                                    }
//                                })
//                        );
//                    }
//
//                } catch (Exception e) {
//                    Log.e("DemographicFragment", "Error in update", e);
//                    e.printStackTrace();
//                }
//
//            });
//
//            executor.shutdown();

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

            if (finalData.sttime !=null){
                finalData.edtime = endtime;
            }

            finalData.complete=1;
            viewModel.add(finalData);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();


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


    public void denoInfo(View view) {
        showDialogInfo(null, DENO_INFO);
    }
}