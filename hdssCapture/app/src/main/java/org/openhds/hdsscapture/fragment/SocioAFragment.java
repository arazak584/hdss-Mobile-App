package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocioBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocioAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocioAFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentSocioBinding binding;
    private Locations selectedLocation;
    private Individual selectedIndividual;

    public SocioAFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment SocioFragment.
     */
    //
    public static SocioAFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        SocioAFragment fragment = new SocioAFragment();
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
        //return inflater.inflate(R.layout.fragment_socio_b, container, false);
        binding = FragmentSocioBinding.inflate(inflater, container, false);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        HdssSociodemoViewModel viewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        try {
            HdssSociodemo data = viewModel.findses(socialgroup.uuid);
            if (data != null) {
                binding.setSociodemo(data);

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                }

                data.fw_uuid = fieldworkerData.getFw_uuid();
                binding.getSociodemo().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }else {
                data = new HdssSociodemo();

                //binding.formcompldate.setVisibility(View.GONE);

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                data.uuid = uuidString;
                data.individual_uuid = selectedIndividual.getUuid();
                data.location_uuid = selectedLocation.getUuid();
                data.socialgroup_uuid = socialgroup.getUuid();
                data.fw_uuid = fieldworkerData.getFw_uuid();

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


                binding.setSociodemo(data);
                binding.getSociodemo().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.getSociodemo().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.MARITALSCORRES, "MARITAL_SCORRES");
        loadCodeData(binding.RELIGIONSCORRES, "religion");
        loadCodeData(binding.CETHNIC, "tribe");
        loadCodeData(binding.HEADHHFCORRES, "rltnhead");
        loadCodeData(binding.id0003, "nhis");
        loadCodeData(binding.id0004, "submit");
        loadCodeData(binding.id0005, "nhis_no");

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


    private void save(boolean save, boolean close, HdssSociodemoViewModel viewModel) {

        if (save) {
            final HdssSociodemo data = binding.getSociodemo();

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            boolean mar = false;
            boolean val = false;
            if (data.marital_scorres == 1 && !binding.MARITALAGE.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.MARITALAGE.getText().toString().trim());
                if (totalmth < 10) {
                    mar = true;
                    binding.MARITALAGE.setError("Minimum Age Allowed is 10");
                    Toast.makeText(getActivity(), "Minimum Age Allowed is 10", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (!binding.HOUSEOCCTOTFCORRES.getText().toString().trim().isEmpty() && !binding.HOUSEOCCLT5FCORRES.getText().toString().trim().isEmpty()) {
                int totalpeople = Integer.parseInt(binding.HOUSEOCCTOTFCORRES.getText().toString().trim());
                int total5 = Integer.parseInt(binding.HOUSEOCCLT5FCORRES.getText().toString().trim());
                if (totalpeople < total5) {
                    val = true;
                    binding.HOUSEOCCLT5FCORRES.setError("Children aged five cannot be more than total Individuals in household");
                    Toast.makeText(getActivity(), "Children aged five cannot be more than total Individuals in household", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            viewModel.add(data);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioBFragment.newInstance(individual,locations, socialgroup)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
        }

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

    private enum DATE_BUNDLES {
        INT_DAT("INT_DAT");

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