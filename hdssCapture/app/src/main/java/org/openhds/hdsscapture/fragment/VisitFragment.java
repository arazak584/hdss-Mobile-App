package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentVisitBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitFragment extends Fragment {

    private Visit visit;
    private FragmentVisitBinding binding;
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private CaseItem caseItem;
    private EventForm eventForm;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String VISIT_ID = "VISIT_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "VISIT.TAG";




    public VisitFragment() {
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
     * @param visit Parameter 5.
     * @param caseItem Parameter 6.
     * @param eventForm Parameter 7.
     * @return A new instance of fragment VisitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, Visit visit, CaseItem caseItem, EventForm eventForm) {
        VisitFragment fragment = new VisitFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(VISIT_ID, visit);
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
            visit = getArguments().getParcelable(VISIT_ID);
            caseItem = getArguments().getParcelable(CASE_ID);
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVisitBinding.inflate(inflater, container, false);
        binding.setVisit(visit);

        //final TextView compno = binding.getRoot().findViewById(R.id.textView4_compextId);
        //final TextView compname = binding.getRoot().findViewById(R.id.textView4_compname);
        //final TextView cluster = binding.getRoot().findViewById(R.id.textView4_clusterId);

        //compno.setText(locations.getCompextId());
        //compname.setText(locations.getLocationName());
        //cluster.setText(locations.villcode);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        /*
        if(visit.fw_uuid==null){
            binding.getVisit().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(visit.roundNumber==null){
            binding.getVisit().roundNumber = roundData.getRoundNumber();
        }

        if(visit.location_uuid==null){
            binding.getVisit().location_uuid = locations.getLocation_uuid();
        }

        // Generate a UUID
        if(visit.visit_uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.toString().replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getVisit().visit_uuid = uuidString;
        }*/

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_household);

        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                HouseholdDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "HouseholdDialogFragment");
            }
        });

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((VisitFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(VisitFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.visitInsertDate.setText(result);

            }

        });

        binding.buttonVisitInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(VisitFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonSaveClose.setOnClickListener(v -> {
            final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);

            final Visit visit = binding.getVisit();
            visit.setVisitextId(this.visit.getVisitextId());

            boolean isExists = false;
            binding.visitFw.setError(null);
            binding.visitRespondent.setError(null);
            binding.visitId.setError(null);
            binding.visitInsertDate.setError(null);

            if (binding.visitInsertDate.getText().toString().isEmpty()) {
                binding.visitInsertDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime()));
            }

            if(visit.visitextId==null){
                isExists = true;
                binding.visitId.setError("Visit Id is Required");
            }

            if(visit.insertDate==null){
                isExists = true;
                binding.visitInsertDate.setError("Date of visit is Required");

            }

            if(visit.fw_uuid==null){
                isExists = true;
                binding.visitFw.setError("Fieldworker userName is Required");
            }

            if(visit.location_uuid==null){
                isExists = true;
                binding.visitRespondent.setError("Respondent is Required");
            }


        });

        loadCodeData(binding.visitcomplete, "complete");
        loadCodeData(binding.realVisit, "yn");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        Handler.colorLayouts(requireContext(), binding.VISITLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Visit finalData = binding.getVisit();


            if (finalData.complete != null) {

            }

            VisitViewModel viewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup, caseItem)).commit();
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
        INSERTDATE ("INSERTDATE");
        //DOB ("DOB");

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