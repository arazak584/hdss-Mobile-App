package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.RelationshipDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.databinding.FragmentRelationshipBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelationshipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationshipFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "RELATIONSHIP.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentRelationshipBinding binding;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Relationship relationship;

    public RelationshipFragment() {
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
     * @return A new instance of fragment RelationshipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RelationshipFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        RelationshipFragment fragment = new RelationshipFragment();
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
        binding = FragmentRelationshipBinding.inflate(inflater, container, false);
        binding.setRelationship(relationship);

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_partner);

        // Set a click listener on the button for partner
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                RelationshipDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "RelationshipDialogFragment");
            }
        });

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported

            if (bundle.containsKey((RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.relStartDate.setText(result);
            }

            if (bundle.containsKey((RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey()))) {
                final String result = bundle.getString(RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey());
                binding.relEndDate.setText(result);
            }

        });

        binding.buttonRelStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonRelEndDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        RelationshipViewModel viewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        try {
            Relationship data = viewModel.find(individual.individual_uuid);
            if (data != null) {
                binding.setRelationship(data);
            } else {
                data = new Relationship();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");


                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.rel_uuid = uuidString;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                //data.residency_uuid = residency.residency_uuid;
                //data.visit_uuid = visit.visit_uuid;

                binding.setRelationship(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //SPINNERS
        loadCodeData(binding.relComplete,  "complete");
        loadCodeData(binding.relStarttype,  "relationshipType");
        loadCodeData(binding.relEndtype,  "relendType");
        loadCodeData(binding.mar,  "complete");
        loadCodeData(binding.polygamous,  "complete");
        loadCodeData(binding.lcow,  "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        Handler.colorLayouts(requireContext(), binding.RELATIONSHIPLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, RelationshipViewModel viewModel) {

        if (save) {
            Relationship finalData = binding.getRelationship();

            boolean val = false;
            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.RELATIONSHIPLAYOUT, validateOnComplete, false);

            if (!binding.tnbch.getText().toString().trim().isEmpty() && !binding.nchdm.getText().toString().trim().isEmpty()) {
                int totalBiolChildren = Integer.parseInt(binding.tnbch.getText().toString().trim());
                int childrenFromMarriage = Integer.parseInt(binding.nchdm.getText().toString().trim());
                if (totalBiolChildren < childrenFromMarriage) {
                    val = true;
                    binding.nchdm.setError("Number of Biological Children Cannot be Less Children from this Marriage");
                    return;
                }
            }


            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
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

    private enum DATE_BUNDLES {
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