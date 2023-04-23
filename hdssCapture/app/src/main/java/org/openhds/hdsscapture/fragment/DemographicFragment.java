package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.databinding.FragmentDemographicBinding;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemographicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemographicFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "DEMOGRAPHIC.TAG";

    // TODO: Rename and change types of parameters
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDemographicBinding binding;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Demographic demographic;

    public DemographicFragment() {
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
     * @return A new instance of fragment DemographicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemographicFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        DemographicFragment fragment = new DemographicFragment();
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
        binding = FragmentDemographicBinding.inflate(inflater, container, false);
        binding.setDemographic(demographic);

        //final TextView compno = binding.getRoot().findViewById(R.id.textView2_extid);
        //final TextView compname = binding.getRoot().findViewById(R.id.textView2_firstname);
        //final TextView cluster = binding.getRoot().findViewById(R.id.textView2_lastname);

        //compno.setText(locations.getCompno());
        //compname.setText(locations.getLocationName());
        //cluster.setText(locations.villcode);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        DemographicViewModel viewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        try {
            Demographic data = viewModel.find(individual.individual_uuid);
            if (data != null) {
                Toast.makeText(requireActivity(), "data pulled ", Toast.LENGTH_LONG).show();
                binding.setDemographic(data);
            } else {
                data = new Demographic();

                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.complete = 1;

                binding.setDemographic(data);
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
        loadCodeData(binding.demoComplete, "complete");
        loadCodeData(binding.demoPhone, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        Handler.colorLayouts(requireContext(), binding.DEMOGRAPHICLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, DemographicViewModel viewModel) {

        if (save) {
            Demographic finalData = binding.getDemographic();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.DEMOGRAPHICLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
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
}