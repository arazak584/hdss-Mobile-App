package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.PregnancyRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.databinding.FragmentPregnancyBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.DatePickerFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.PregAdapterFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PregViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PregViewFragment extends Fragment {

    private final String TAG = "PREGNANCY.TAG";
    private static final String PREGNANCY_ID = "PREGNANCY_ID";

    private Pregnancy pregnancy;
    private FragmentPregnancyBinding binding;
    private PregnancyRepository repository;


    public PregViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PregnancyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregViewFragment newInstance(String uuid) {
        PregViewFragment fragment = new PregViewFragment();
        Bundle args = new Bundle();
        args.putString(PREGNANCY_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new PregnancyRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(PREGNANCY_ID); // Correct key
            this.pregnancy = new Pregnancy();  // Initialize placeholder
            this.pregnancy.uuid = uuid;        // Assign UUID to fetch from DB
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
//             if (bundle.containsKey((PregViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
//                final String result = bundle.getString(PregViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
//                binding.editTextRecordedDate.setText(result);
//            }
//
//            if (bundle.containsKey((PregViewFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey()))) {
//                final String result = bundle.getString(PregViewFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey());
//                binding.editTextOutcomeDate.setText(result);
//            }

            if (bundle.containsKey((PregViewFragment.DATE_BUNDLES.CLINICDATE.getBundleKey()))) {
                final String result = bundle.getString(PregViewFragment.DATE_BUNDLES.CLINICDATE.getBundleKey());
                binding.editTextLastClinicVisitDate.setText(result);
            }

            if (bundle.containsKey((PregViewFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey()))) {
                final String result = bundle.getString(PregViewFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey());
                binding.expectedDelivery.setText(result);
            }

        });

        binding.buttonPregStartDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.editTextRecordedDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.editTextRecordedDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        binding.buttonPregnancyOutcome.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.editTextOutcomeDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.editTextOutcomeDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.OUTCOMEDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        binding.buttonLastClinicVisitDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.editTextLastClinicVisitDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.editTextLastClinicVisitDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.CLINICDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.CLINICDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        binding.buttonPregExpectDate.setOnClickListener(v -> {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(PregViewFragment.DATE_BUNDLES.EXPECTDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        PregnancyViewModel viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        viewModel.getView(pregnancy.uuid).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setPregnancy(data);
                binding.outcometype.setEnabled(false);


                // Fetch the last record before the current one
                try {
                    Pregnancy previousPregnancy = viewModel.lastpregs(data.individual_uuid, data.recordedDate);
                    if (previousPregnancy != null) {
                        binding.setPreg(previousPregnancy);
                    } else {
                        binding.lastPreg.setVisibility(View.GONE);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions properly
                    e.printStackTrace();
                    // Optionally, show an error message to the user
                    Toast.makeText(getContext(), "Error fetching previous pregnancy data", Toast.LENGTH_SHORT).show();
                }

                if (data.status != null && data.status == 2) {
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                } else {
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }
            }
        });

        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;

        try {
            configsettings = configViewModel.findAll();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                // Format the Date to a String in "yyyy-MM-dd" format
                String formattedDate = dateFormat.format(dt);
                //System.out.println("EARLIEST-DATE: " + formattedDate);
                editText.setText(formattedDate);
            } else {
                System.out.println("EARLIEST-DATE: null");
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
        loadCodeData(binding.pregReady, "more_chd");
        loadCodeData(binding.familyPlan, "complete");
        loadCodeData(binding.planMethod, "fam_plan_method");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.PREGNANCYLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyViewModel viewModel) {

        if (save) {
            Pregnancy finalData = binding.getPregnancy();

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.editTextRecordedDate.setError("Conception Date Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
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
                        binding.expectedDelivery.setError(String.valueOf(R.string.deliv));
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

            boolean anc = false;
            if (finalData.anteNatalClinic == 1 && !binding.ancVisits.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.ancVisits.getText().toString().trim());
                if (totalmth < 1 || totalmth > 20) {
                    anc = true;
                    binding.ancVisits.setError("Maximum Number of ANC Visit is 20");
                    Toast.makeText(getActivity(), "Maximum Number of ANC Visit is 20", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.PREGNANCYLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }

            PregnancyoutcomeViewModel iview = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                try {
                    // Update Variables in Pregnancy Outcome
                    Pregnancyoutcome data = iview.preg(finalData.uuid);
                    if (data != null) {
                        OutcomeUpdate upd = new OutcomeUpdate();
                        upd.uuid = data.getUuid();
                        upd.complete = 1;
                        upd.outcomeDate = finalData.outcome_date;
                        upd.conceptionDate = finalData.recordedDate;
                        upd.rec_anc = finalData.anteNatalClinic;
                        upd.month_pg = finalData.first_rec;
                        upd.who_anc = finalData.attend_you;
                        upd.num_anc = finalData.anc_visits;
                        upd.pregnancy_uuid = finalData.uuid;

                        iview.update(upd, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("PregnancyOutcome", "Update successful!");
                                    } else {
                                        Log.d("PregnancyOutcome", "Update Failed!");
                                    }
                                })
                        );
                    }
                } catch (Exception e) {
                    Log.e("DemographicFragment", "Error in visited update", e);
                    e.printStackTrace();
                }

            });

            executor.shutdown();


            finalData.complete=1;
            viewModel.add(finalData);

            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
        }
//        if (close) {
//            Intent intent = new Intent(requireContext(), ViewActivity.class);
//            startActivity(intent);
//            requireActivity().finish(); // Optional: finish current activity if you want to remove it from backstack
//        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
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