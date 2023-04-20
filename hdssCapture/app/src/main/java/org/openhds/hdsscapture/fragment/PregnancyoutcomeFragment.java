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
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutcomeBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

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
 * Use the {@link PregnancyoutcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PregnancyoutcomeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String PREGNANCY_ID = "PREGNANCY_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "OUTCOME.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentOutcomeBinding binding;
    private EventForm eventForm;
    private CaseItem caseItem;

    public PregnancyoutcomeFragment() {
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
     * @param eventForm Parameter 7.
     * @param caseItem Parameter 6.
     * @return A new instance of fragment PregnancyoutcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyoutcomeFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        PregnancyoutcomeFragment fragment = new PregnancyoutcomeFragment();
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
        binding = FragmentOutcomeBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((PregnancyoutcomeFragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(PregnancyoutcomeFragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.editTextOutcomeDate.setText(result);
            }

            if (bundle.containsKey((PregnancyoutcomeFragment.DATE_BUNDLES.CONCEPTION.getBundleKey()))) {
                final String result = bundle.getString(PregnancyoutcomeFragment.DATE_BUNDLES.CONCEPTION.getBundleKey());
                binding.editTextConception.setText(result);
            }

        });

        binding.buttonOutcomeStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyoutcomeFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonOutcomeConception.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyoutcomeFragment.DATE_BUNDLES.CONCEPTION.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        try {
            Pregnancyoutcome data = viewModel.find(individual.individual_uuid);
            if (data != null) {
                binding.setPregoutcome(data);
            } else {
                data = new Pregnancyoutcome();

                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.preg_uuid = uuidString;
                data.insertDate = new Date();
                data.mother_uuid = individual.getIndividual_uuid();
                data.visit_uuid = socialgroup.getVisit_uuid();

                binding.setPregoutcome(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            final String child_id = individual.individual_uuid + AppConstants.CHILD1 + + eventForm.event_name_id;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                binding.setPregoutcome1(data);
            } else {
                data = new Outcome();

                data.mother_uuid = individual.getIndividual_uuid();
                data.child_idx = AppConstants.CHILD1;

                data.vis_number = eventForm.event_name_id;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number;
                data.complete = 1;


                binding.setPregoutcome1(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = individual.individual_uuid + AppConstants.CHILD2 + + eventForm.event_name_id;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                binding.setPregoutcome2(data);
            } else {
                data = new Outcome();

                data.mother_uuid = individual.getIndividual_uuid();
                data.child_idx = AppConstants.CHILD2;

                data.vis_number = eventForm.event_name_id;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number;
                data.complete = 1;

                binding.setPregoutcome2(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = individual.individual_uuid + AppConstants.CHILD3 + + eventForm.event_name_id;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                binding.setPregoutcome3(data);
            } else {
                data = new Outcome();

                data.mother_uuid = individual.getIndividual_uuid();
                data.child_idx = AppConstants.CHILD3;

                data.vis_number = eventForm.event_name_id;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number;
                data.complete = 1;

                binding.setPregoutcome3(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = individual.individual_uuid + AppConstants.CHILD4 + + eventForm.event_name_id;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                binding.setPregoutcome4(data);
            } else {
                data = new Outcome();

                data.mother_uuid = individual.getIndividual_uuid();
                data.child_idx = AppConstants.CHILD4;

                data.vis_number = eventForm.event_name_id;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number;
                data.complete = 1;


                binding.setPregoutcome4(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.childFetus1.out1Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus2.out2Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus3.out3Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus4.out4Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.birthplace, codeBookViewModel, "birthPlace");
        loadCodeData(binding.notDel, codeBookViewModel, "notdel");
        loadCodeData(binding.whyNoAnc, codeBookViewModel, "notdel");
        loadCodeData(binding.firstNb, codeBookViewModel, "complete");
        loadCodeData(binding.recAnc, codeBookViewModel, "complete");
        loadCodeData(binding.recIpt, codeBookViewModel, "complete");
        loadCodeData(binding.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.assDel, codeBookViewModel, "assist");
        loadCodeData(binding.howDel, codeBookViewModel, "howdel");
        loadCodeData(binding.whereAnc, codeBookViewModel, "birthPlace");
        loadCodeData(binding.whoAnc, codeBookViewModel, "assist");
        loadCodeData(binding.chdSize, codeBookViewModel, "size");
        loadCodeData(binding.individualComplete, codeBookViewModel, "complete");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, outcomeViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, outcomeViewModel);
        });

        binding.setEventname(eventForm.event_name);
        Handler.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel, OutcomeViewModel outcomeViewModel) {

        if (save) {
            Pregnancyoutcome finalData = binding.getPregoutcome();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }


            if (finalData.numberofBirths != null) {

                if (finalData.numberofBirths >= 1) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus1.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();
                    inf.complete = 1;
                    outcomeViewModel.add(inf);
                }

                if (finalData.numberofBirths >= 2) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus2.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();
                    inf.complete = 1;
                    outcomeViewModel.add(inf);

                }

                if (finalData.numberofBirths >= 3) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus3.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();
                    inf.complete = 1;
                    outcomeViewModel.add(inf);

                }

                if (finalData.numberofBirths >= 4) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus4.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();
                    inf.complete = 1;
                    outcomeViewModel.add(inf);

                }

            }
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

    private void loadCodeData(Spinner spinner, CodeBookViewModel viewModel, final String codeFeature) {
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
        CONCEPTION ("CONCEPTION"),
        RECORDDATE ("RECORDDATE");

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