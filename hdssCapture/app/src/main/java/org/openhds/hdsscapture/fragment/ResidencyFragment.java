package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentResidencyBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidencyFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "RESIDENCY.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentResidencyBinding binding;;
    private CaseItem caseItem;
    private EventForm eventForm;


    public ResidencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @param caseItem Parameter 6.
     * @param eventForm Parameter 7.
     * @return A new instance of fragment ResidencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidencyFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        ResidencyFragment fragment = new ResidencyFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(CASE_ID, caseItem);
        args.putParcelable(EVENT_ID, eventForm);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            caseItem = getArguments().getParcelable(CASE_ID);
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResidencyBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        /*
        // Generate a UUID
        if(residency.residency_uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.toString().replaceAll("-", "");
            // Set the ID of the uuid
            binding.getResidency().residency_uuid = uuidString;
        }

        if(residency.fw_uuid==null){
            binding.getResidency().fw_uuid = fieldworkerData.getFw_uuid();
        }*/

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((ResidencyFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(ResidencyFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.residencyInsertDate.setText(result);

            }


            if (bundle.containsKey((ResidencyFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(ResidencyFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.editTextStartDate.setText(result);
            }

            if (bundle.containsKey((ResidencyFragment.DATE_BUNDLES.ENDDATE.getBundleKey()))) {
                final String result = bundle.getString(ResidencyFragment.DATE_BUNDLES.ENDDATE.getBundleKey());
                binding.editTextEndDate.setText(result);
            }

        });

        binding.buttonResidencyInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(ResidencyFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonResidencyStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.STARTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonResidencyEndDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ENDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonSaveClose.setOnClickListener(v -> {
            final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

            final Residency residency = binding.getResidency();

        });

        //SPINNERS
        loadCodeData(binding.residencyComplete,  "yn");
        loadCodeData(binding.starttype,  "startType");
        loadCodeData(binding.endtype,  "endType");
        loadCodeData(binding.rltnHead,  "rltnhead");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        binding.setEventname(AppConstants.EVENT_RESIDENCY);
        Handler.colorLayouts(requireContext(), binding.RESIDENCYLAYOUT);
        View view = binding.getRoot();

        return view;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Residency finalData = binding.getResidency();


            if (finalData.complete != null) {

            }

            ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup, caseItem)).commit();
        }
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
        STARTDATE ("STARTDATE"),
        ENDDATE ("ENDDATE");

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