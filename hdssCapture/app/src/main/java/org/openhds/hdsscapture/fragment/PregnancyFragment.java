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
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.databinding.FragmentPregnancyBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PregnancyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PregnancyFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String PREGNANCY_ID = "PREGNANCY_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "PREGNANCY.TAG";

    private Pregnancy pregnancy;
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentPregnancyBinding binding;
    private EventForm eventForm;
    private CaseItem caseItem;


    public PregnancyFragment() {
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
     * @return A new instance of fragment PregnancyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        PregnancyFragment fragment = new PregnancyFragment();
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
        binding = FragmentPregnancyBinding.inflate(inflater, container, false);
        binding.setPregnancy(pregnancy);


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
             if (bundle.containsKey((PregnancyFragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(PregnancyFragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.editTextRecordedDate.setText(result);
            }

            if (bundle.containsKey((PregnancyFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey()))) {
                final String result = bundle.getString(PregnancyFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey());
                binding.editTextOutcomeDate.setText(result);
            }

            if (bundle.containsKey((PregnancyFragment.DATE_BUNDLES.CLINICDATE.getBundleKey()))) {
                final String result = bundle.getString(PregnancyFragment.DATE_BUNDLES.CLINICDATE.getBundleKey());
                binding.editTextLastClinicVisitDate.setText(result);
            }

            if (bundle.containsKey((PregnancyFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey()))) {
                final String result = bundle.getString(PregnancyFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey());
                binding.expectedDelivery.setText(result);
            }

        });

        binding.buttonPregStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonPregnancyOutcome.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonLastClinicVisitDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyFragment.DATE_BUNDLES.CLINICDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonPregExpectDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(PregnancyFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        PregnancyViewModel viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy data = viewModel.find(individual.individual_uuid);
            if (data != null && data.extra==1 ) {
                binding.setPregnancy(data);
            } else {
                data = new Pregnancy();

                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.obs_uuid = uuidString;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.complete = 1;
                //data.expectedDeliveryDate = data.recordedDate(9);

                binding.setPregnancy(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //LOAD SPINNERS
        loadCodeData(binding.anteNatalClinic, "complete");
        loadCodeData(binding.individualComplete, "submit");
        loadCodeData(binding.ttinjection, "complete");
        loadCodeData(binding.slpBednet, "complete");
        loadCodeData(binding.firstPreg, "complete");
        loadCodeData(binding.outcometype, "complete");
        loadCodeData(binding.whyNo, "notdel");
        loadCodeData(binding.attendYou, "assist");
        loadCodeData(binding.ownBnet, "complete");
        loadCodeData(binding.bnetSou, "bnetSou");
        loadCodeData(binding.bnetLoc, "bnetLoc");
        loadCodeData(binding.trtBednet, "complete");
        loadCodeData(binding.healthfacility, "complete");
        loadCodeData(binding.medicineforpregnancy, "complete");
        loadCodeData(binding.medicineforpregnancy, "complete");
        loadCodeData(binding.extra, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        //binding.setEventname(eventForm.event_name);
        Handler.colorLayouts(requireContext(), binding.PREGNANCYLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyViewModel viewModel) {

        if (save) {
            Pregnancy finalData = binding.getPregnancy();

            try {
                if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty() && !binding.expectedDelivery.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    Date edate = f.parse(binding.expectedDelivery.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.editTextRecordedDate.setError("Date of Conception Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Conception Cannot Be a Future Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (edate.before(stdate)) {
                        binding.expectedDelivery.setError("Delivery Date Cannot Be Less than Conception Date");
                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.expectedDelivery.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            try {
                if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty() && !binding.editTextOutcomeDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    Date edate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    if (edate.after(currentDate)) {
                        binding.editTextOutcomeDate.setError("Date of Outcome Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Outcome Cannot Be a Future Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (edate.before(stdate)) {
                        binding.expectedDelivery.setError("Delivery Date Cannot Be Less than Conception Date");
                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextOutcomeDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.PREGNANCYLAYOUT, validateOnComplete, false);

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
        OUTCOMEDATE ("OUTCOMEDATE"),
        RECORDDATE ("RECORDDATE"),
        EXPECTDATE ("EXPECTDATE"),
        CLINICDATE ("CLINICDATE");

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