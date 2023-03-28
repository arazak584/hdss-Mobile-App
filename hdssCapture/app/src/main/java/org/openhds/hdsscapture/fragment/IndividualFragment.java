package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Calculators;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualFragment extends Fragment {

    //private Button showDialogButton;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    private Location location;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentIndividualBinding binding;;
    private CaseItem caseItem;

    public IndividualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @param individual Parameter 4.
     * @param residency Parameter 2.
     * @param location Parameter 1.
     * @param socialgroup Parameter 3.
     * @param caseItem Parameter 5.
     * @return A new instance of fragment IndividualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup, CaseItem caseItem) {
        IndividualFragment fragment = new IndividualFragment();
        Bundle args = new Bundle();

        args.putParcelable(LOC_LOCATION_IDS, location);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIndividualBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        String father = getArguments().getString("uuid");

        EditText extIdField = binding.getRoot().findViewById(R.id.father_uuid);
        //        extIdField.setText(father);

        // Generate a UUID
        if(individual.individual_uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.toString().replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getIndividual().individual_uuid = uuidString;
        }

        if(individual.fw_uuid==null){
           binding.getIndividual().fw_uuid = fieldworkerData.getFw_uuid();
        }

        // Generate a extId
        if(individual.extId == null) {

            int sequenceNumber = 1;
            // Set the ID of the Fieldworker object
            binding.getIndividual().extId = location.compextId+String.format("%03d", sequenceNumber);
            sequenceNumber++;
        }


        // Find the button view
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_individual_mother);
        Button showDialogButton1 = binding.getRoot().findViewById(R.id.button_individual_father);

        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                MotherDialogFragment.newInstance(individual, residency, location,socialgroup)
                        .show(getChildFragmentManager(), "MotherDialogFragment");

            }
        });

        // Set a click listener on the button for mother
        showDialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                FatherDialogFragment.newInstance(individual, residency, location,socialgroup)
                        .show(getChildFragmentManager(), "FatherDialogFragment");
            }
        });

        if (binding.getIndividual().dob!=null) {
            final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
            binding.individualAge.setText("" + estimatedAge + " years old");
            binding.dob.setError(null);
        }

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.individualInsertDate.setText(result);

            }


            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.dob.setText(result);
            }

        });

        binding.buttonIndividualInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });


        binding.buttonIndividualDob.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            final String curbrthdat = binding.dob.getText().toString();
            if(curbrthdat!=null){
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    c.setTime(Objects.requireNonNull(f.parse(curbrthdat)));
                } catch (ParseException e) {
                }
            }
            DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });


        binding.buttonSaveClose.setOnClickListener(v -> {
            final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            final Individual individual = binding.getIndividual();
            individual.setExtId(this.individual.getExtId());

            boolean isExists = false;
            binding.individualExtid.setError(null);
            binding.individualInsertDate.setError(null);
            binding.individualFw.setError(null);
            binding.individualFirstName.setError(null);
            binding.individualLastName.setError(null);
            binding.dob.setError(null);

            if(individual.extId==null){
                isExists = true;
                binding.individualExtid.setError("Individual Id is Required");
            }

            if(individual.insertDate==null){
                isExists = true;
                binding.individualInsertDate.setError("Date of visit is Required");

            }

            if(individual.fw_uuid==null){
                isExists = true;
                binding.individualFw.setError("Fieldworker userName is Required");
            }

            if(individual.firstName==null){
                isExists = true;
                binding.individualFirstName.setError("FirstName is Required");
            }

            if(individual.lastName==null){
                isExists = true;
                binding.individualLastName.setError("LastName is Required");
            }

            if(individual.dob==null){
                isExists = true;
                binding.dob.setError("Date of Birth is Required");
            }


        });

        //LOAD SPINNERS
        loadCodeData(binding.dobAspect, "yn");
        loadCodeData(binding.individualComplete,  "complete");
        loadCodeData(binding.gender, "gender");



        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        Handler.colorLayouts(requireContext(), binding.INDIVIDUALLAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close) {

        if (save) {
            Individual finalData = binding.getIndividual();
            finalData.modified = AppConstants.YES;


            if (finalData.complete != null) {

            }

            IndividualViewModel viewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            viewModel.add(finalData);
        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency,location, socialgroup,caseItem)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseVisitFragment.newInstance(individual,residency,location, socialgroup)).commit();
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
        INSERTDATE ("INSERTDATE"),
        DOB ("DOB");

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