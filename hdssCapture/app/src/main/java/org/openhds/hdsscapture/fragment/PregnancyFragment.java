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
     * @return A new instance of fragment PregnancyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup,EventForm eventForm) {
        PregnancyFragment fragment = new PregnancyFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
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
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPregnancyBinding.inflate(inflater, container, false);
        binding.setPregnancy(pregnancy);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(individual.firstName + " " + individual.lastName);

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
        PregnancyViewModel pviewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy data = viewModel.find(individual.uuid);
            if (data != null) {
                binding.setPregnancy(data);
            } else {
                data = new Pregnancy();

                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.individual_uuid = individual.getUuid();
                data.visit_uuid = socialgroup.getVisit_uuid();

                binding.setPregnancy(data);
                binding.getPregnancy().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Pregnancy datas = pviewModel.lastpreg(individual.uuid);
            if (datas != null) {
                binding.setPreg(datas);
            }else{
                binding.lastPreg.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //LOAD SPINNERS
        loadCodeData(binding.anteNatalClinic, "yn_anc");
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

        binding.setEventname(eventForm.event_name);
        Handler.colorLayouts(requireContext(), binding.PREGNANCYLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyViewModel viewModel) {

        if (save) {
            Pregnancy finalData = binding.getPregnancy();

            try {
                if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    String edateStr = "2018-01-01"; // Assuming edate is a String in the format "yyyy-MM-dd"
                    Date edate = f.parse(edateStr);
                    Date stdate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    if (stdate.before(edate)) {
                        binding.editTextRecordedDate.setError("Conception Date Cannot Be Less than Earliest migration date 2018");
                        Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest migration date 2018", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextRecordedDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty() && !binding.editTextLastClinicVisitDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    Date edate = f.parse(binding.editTextLastClinicVisitDate.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.editTextRecordedDate.setError("Date of Conception Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Conception Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.before(stdate) || edate.equals(stdate)) {
                        binding.editTextLastClinicVisitDate.setError("Last Visit Date Cannot Be Less than or Equal to Conception Date");
                        Toast.makeText(getActivity(), "Last Visit Date Cannot Be Less than or Equal to Conception Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextLastClinicVisitDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            boolean total = false;
            if (finalData.anteNatalClinic == 1 && !binding.ageOfPregFromPregNotes.getText().toString().trim().isEmpty()) {
                int totalweeks = Integer.parseInt(binding.ageOfPregFromPregNotes.getText().toString().trim());
                if (totalweeks < 4 || totalweeks > 52) {
                    total = true;
                    binding.ageOfPregFromPregNotes.setError("Maximum Number of Weeks Allowed is 4 - 52");
                    Toast.makeText(getActivity(), "Maximum Number of Weeks Allowed is 4 - 52 ", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean totalm = false;
            if (finalData.anteNatalClinic == 1 && !binding.estimatedAgeOfPreg.getText().toString().trim().isEmpty()) {
                int totalmnth = Integer.parseInt(binding.estimatedAgeOfPreg.getText().toString().trim());
                if (totalmnth < 1 || totalmnth > 12) {
                    totalm = true;
                    binding.estimatedAgeOfPreg.setError("Maximum Number of Months Allowed is 12");
                    Toast.makeText(getActivity(), "Maximum Number of Months Allowed is 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean check = false;
            if (finalData.anteNatalClinic == 1
                    && !binding.estimatedAgeOfPreg.getText().toString().trim().isEmpty()
                    && !binding.ageOfPregFromPregNotes.getText().toString().trim().isEmpty()) {

                int totalMonths = Integer.parseInt(binding.estimatedAgeOfPreg.getText().toString().trim());
                int totalWeeks = Integer.parseInt(binding.ageOfPregFromPregNotes.getText().toString().trim());

                int totalWeeksConvertedToMonths = totalWeeks / 4;

                if (totalMonths < totalWeeksConvertedToMonths) {
                    check = true;
                    binding.ageOfPregFromPregNotes.setError("Check Number of Months and weeks Pregnant");
                    Toast.makeText(getActivity(), "Check Number of Months and weeks Pregnant", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean totalmths = false;
            if (finalData.anteNatalClinic == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
                int totalmnth = Integer.parseInt(binding.firstRec.getText().toString().trim());
                if (totalmnth < 1 || totalmnth > 12) {
                    totalmths = true;
                    binding.firstRec.setError("Maximum Number of Months Allowed is 12");
                    Toast.makeText(getActivity(), "Maximum Number of Months Allowed is 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean bed = false;
            if (finalData.own_bnet == 1 && !binding.howMany.getText().toString().trim().isEmpty()) {
                int totalmnth = Integer.parseInt(binding.howMany.getText().toString().trim());
                if (totalmnth < 1 || totalmnth > 10) {
                    bed = true;
                    binding.howMany.setError("Maximum Number of Bednets is 10");
                    Toast.makeText(getActivity(), "Maximum Number of Bednets is 10", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean pregs = false;
            if (finalData.first_preg == 2 && !binding.pregnancyNumber.getText().toString().trim().isEmpty()) {
                int totalbirth = Integer.parseInt(binding.pregnancyNumber.getText().toString().trim());
                if (totalbirth < 2 || totalbirth > 15) {
                    pregs = true;
                    binding.pregnancyNumber.setError("Cannot be less than 2");
                    Toast.makeText(getActivity(), "Total Pregnancies Cannot be less than 2", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextLastClinicVisitDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    Date edate = f.parse(binding.editTextLastClinicVisitDate.getText().toString().trim());
                    if (stdate.before(edate)) {
                        binding.editTextLastClinicVisitDate.setError("Date of Outcome Cannot Be Before Last Clinic Visit Date");
                        Toast.makeText(getActivity(), "Date of Outcome Cannot Be Before Last Clinic Visit Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextLastClinicVisitDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Date of Outcome Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.before(stdate) || edate.equals(stdate)) {
                        binding.expectedDelivery.setError("Delivery Date Cannot Be Less than Conception Date");
                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextOutcomeDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.lastPreg.getText().toString().trim().isEmpty() && !binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.lastPreg.getText().toString().trim());
                    Date edate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    String formattedDate = f.format(stdate);
                    if (edate.before(stdate)) {
                        binding.editTextRecordedDate.setError("Pregnancy with a later Date exist " + formattedDate);
                        Toast.makeText(getActivity(), "Pregnancy with a later Date exist " + formattedDate, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextRecordedDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    Date recordedDate = f.parse(binding.editTextRecordedDate.getText().toString().trim());

                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(recordedDate);

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(outcomeDate);

                    int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                    int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                    int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                    // Adjust the difference based on the day component
                    if (dayDiff < 0) {
                        monthDiff--;
                    }

                    // Calculate the total difference in months
                    int totalDiffMonths = yearDiff * 12 + monthDiff;

                    if (totalDiffMonths < 1 || totalDiffMonths > 12) {
                        binding.editTextRecordedDate.setError("The difference between outcome and conception Date should be between 1 and 12 months");
                        Toast.makeText(getActivity(), "The difference between outcome and conception Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Clear error if validation passes
                    binding.editTextRecordedDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }




            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.PREGNANCYLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }
            finalData.complete=1;
            viewModel.add(finalData);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
        }
        if (save && binding.getPregnancy().outcome==1) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyoutcomeFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup)).commit();
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