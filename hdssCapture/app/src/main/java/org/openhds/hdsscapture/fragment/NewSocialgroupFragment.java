package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Calculators;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentNewSocialgroupBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewSocialgroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewSocialgroupFragment extends DialogFragment {

    //private Button showDialogButton;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    private Locations locations;
    private Residency residency;
    private Individual individual;
    private FragmentNewSocialgroupBinding binding;
    private Socialgroup socialgroup;
    private Hierarchy level6Data;
    private Locations selectedLocation;

    public NewSocialgroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @param individual Parameter 4.
     * @param residency Parameter 2.
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment IndividualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewSocialgroupFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        NewSocialgroupFragment fragment = new NewSocialgroupFragment();
        Bundle args = new Bundle();

        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(SOCIAL_ID, socialgroup);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewSocialgroupBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);
        binding.setSocialgroup(socialgroup);
        binding.setResidency(residency);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        // Generate a UUID
        if(individual.uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getIndividual().uuid = uuidString;
            binding.getSocialgroup().individual_uuid = uuidString;
            binding.getResidency().individual_uuid = uuidString;
        }

        if(residency.uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String resuuidString = uuid.replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getResidency().uuid = resuuidString;
        }

        if(socialgroup.uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String suuidString = uuid.replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getSocialgroup().uuid = suuidString;
            binding.getResidency().socialgroup_uuid = suuidString;
        }

        if(individual.fw_uuid==null){
           binding.getIndividual().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(residency.fw_uuid==null){
            binding.getResidency().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(socialgroup.fw_uuid==null){
            binding.getSocialgroup().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(residency.location_uuid==null){
            binding.getResidency().location_uuid = selectedLocation.getUuid();
            binding.getIndividual().compno = selectedLocation.getCompno();
        }

        if(socialgroup.complete==null){
            binding.getSocialgroup().complete = 2;
        }

        if(residency.complete==null){
            binding.getResidency().complete = 2;
        }

        if(individual.insertDate==null){
            binding.getIndividual().insertDate = new Date();
        }

        if(socialgroup.insertDate==null){
            binding.getSocialgroup().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        if(residency.insertDate==null){
            binding.getResidency().insertDate = new Date();
        }

        // Generate ID if extId is null
        if (binding.getIndividual().extId == null) {
            final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
            int sequenceNumber = 1;
            String id = selectedLocation.compextId + String.format("%04d", sequenceNumber); // generate ID with sequence number padded with zeros
            while (true) {
                try {
                    if (individualViewModels.findAll(id) == null) break;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } // check if ID already exists in ViewModel
                sequenceNumber++; // increment sequence number if ID exists
                id = selectedLocation.compextId + String.format("%04d", sequenceNumber); // generate new ID with updated sequence number
            }
            binding.getIndividual().extId = id; // set the generated ID to the extId property of the Individual object
        }

        // Generate ID if extId is null
        if (binding.getSocialgroup().extId == null) {
            final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            String id = UniqueIDGen.generateHouseholdId(socialgroupViewModel, selectedLocation.compextId);
            binding.getIndividual().hohID = id;
            binding.getSocialgroup().extId = id;

        }


        if(individual.firstName==null){
            binding.getIndividual().firstName = "FAKE";
        }

        if(individual.lastName==null){
            binding.getIndividual().lastName = "FAKE";
        }

        if (individual.dob == null) {
            binding.getIndividual().dob = new Date(2003-12-15);
        }

        if(individual.gender==null){
            binding.getIndividual().gender = 1;
        }

        if(socialgroup.groupType==null){
            binding.getSocialgroup().groupType = 1;
        }

        if(residency.startDate==null){
            binding.getResidency().startDate = new Date();
        }

        if(residency.startType==null){
            binding.getResidency().startType = 1;
        }

        if(residency.endType==null){
            binding.getResidency().endType = 1;
            binding.getIndividual().endType = 1;
        }

        if(residency.rltn_head==null){
            binding.getResidency().rltn_head = 1;
        }

        if(individual.complete==null){
            binding.getIndividual().complete = 2;
        }

        if(socialgroup.groupName==null){
            binding.getSocialgroup().groupName = "UNK";
        }


        if (binding.getIndividual().dob!=null) {
            final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
            binding.individualAge.setText("" + estimatedAge + " years old");
            binding.dob.setError(null);
        }

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (bundle.containsKey((NewSocialgroupFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(NewSocialgroupFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.dob.setText(result);
            }

            if (bundle.containsKey((NewSocialgroupFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(NewSocialgroupFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.editTextStartDate.setText(result);
            }

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
            DialogFragment newFragment = new DatePickerFragment(NewSocialgroupFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonResidencyStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(NewSocialgroupFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonSaveClose.setOnClickListener(v -> {
            final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

            final Individual individual = binding.getIndividual();
            final Socialgroup socialgroup = binding.getSocialgroup();
            final Residency residency = binding.getResidency();
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
        loadCodeData(binding.individualComplete,  "yn");
        loadCodeData(binding.gender, "gender");
        loadCodeData(binding.selectGroupType, "groupType");
        loadCodeData(binding.starttype, "startType");
        loadCodeData(binding.endtype, "endType");
        loadCodeData(binding.rltnHead, "rltnhead");

        binding.buttonSaveClose.setOnClickListener(v -> {

            String comp = binding.sociagroupExtid.getText().toString();
            boolean val = false;

            if (binding.getSocialgroup().extId != null && comp.length() != 11) {
                binding.sociagroupExtid.setError("Household ID must be 11 characters in length");
                Toast.makeText(getActivity(), "Household ID must be 11 characters in length", Toast.LENGTH_LONG).show();
                val = true;
                return;
            }

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });


        binding.setEventname(AppConstants.EVENT_BASE);
        HandlerSelect.colorLayouts(requireContext(), binding.BASELINELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close) {

        if (save) {
            Individual finalData = binding.getIndividual();
            Residency res = binding.getResidency();
            Socialgroup soc = binding.getSocialgroup();
            //finalData.modified = AppConstants.YES;


            IndividualViewModel viewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            viewModel.add(finalData);

            ResidencyViewModel viewModels = new ViewModelProvider(this).get(ResidencyViewModel.class);
            viewModels.add(res);

            SocialgroupViewModel viewModelss = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            viewModelss.add(soc);
        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    ClusterFragment.newInstance(level6Data,locations, socialgroup)).commit();
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
        STARTDATE ("STARTDATE"),
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