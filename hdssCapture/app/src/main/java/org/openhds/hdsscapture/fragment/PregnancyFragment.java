package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentPregnancyBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PregnancyFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String ARG_PREGNANCY_NUMBER = "ARG_PREGNANCY_NUMBER";
    private final String TAG = "PREGNANCY.TAG";

    private Pregnancy pregnancy;
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentPregnancyBinding binding;
    private Individual selectedIndividual;
    private AutoCompleteTextView pregnancyNumberDropdown;
    private PregnancyViewModel viewModel;

    private int currentPregnancyNumber = 1;
    private List<Pregnancy> pregnancyRecords = new ArrayList<>();
    private int initialPregnancyNumber = 1;

    public PregnancyFragment() {
        // Required empty public constructor
    }

    public static PregnancyFragment newInstance(Individual individual, Locations locations,
                                                Socialgroup socialgroup, int pregnancyNumber) {
        PregnancyFragment fragment = new PregnancyFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putInt(ARG_PREGNANCY_NUMBER, pregnancyNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            initialPregnancyNumber = getArguments().getInt(ARG_PREGNANCY_NUMBER, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPregnancyBinding.inflate(inflater, container, false);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        String fName = selectedIndividual.firstName + " " + selectedIndividual.lastName;
        ind.setText(fName);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        setupDatePickers();

        viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        setupPregnancyNumberDropdown();
        loadPregnancyRecords();
        loadPregnancyByNumber(initialPregnancyNumber);

        // Add listener for outcome radio group to update dropdown
        binding.outcome.setOnCheckedChangeListener((group, checkedId) -> {
            binding.getRoot().postDelayed(() -> updateDropdownOptions(), 100);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        // Initialize config settings
        initializeConfigSettings();

        // Load spinners
        loadCodeData(binding.anteNatalClinic, "yn_anc");
        loadCodeData(binding.individualComplete, "submit");
        loadCodeData(binding.ttinjection, "complete");
        loadCodeData(binding.slpBednet, "complete");
        loadCodeData(binding.firstPreg, "complete");
        loadCodeData(binding.whyNo, "notdel");
        loadCodeData(binding.attendYou, "assist");
        loadCodeData(binding.ownBnet, "complete");
        loadCodeData(binding.bnetSou, "bnetSou");
        loadCodeData(binding.bnetLoc, "bnetLoc");
        loadCodeData(binding.trtBednet, "complete");
        loadCodeData(binding.healthfacility, "complete");
        loadCodeData(binding.medicineforpregnancy, "complete");
        //loadCodeData(binding.extra, "complete");
        loadCodeData(binding.pregReady, "more_chd");
        loadCodeData(binding.familyPlan, "complete");
        loadCodeData(binding.planMethod, "fam_plan_method");

        binding.buttonSaveClose.setOnClickListener(v -> save(true, true, viewModel, fieldworkerData));
        binding.buttonClose.setOnClickListener(v -> save(false, true, viewModel, fieldworkerData));

        HandlerSelect.colorLayouts(requireContext(), binding.PREGNANCYLAYOUT);
        return binding.getRoot();
    }

    private void setupPregnancyNumberDropdown() {
        pregnancyNumberDropdown = binding.getRoot().findViewById(R.id.pregnancyNumberDropdown);

        List<String> availableNumbers = new ArrayList<>();
        availableNumbers.add("Pregnancy 1");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                availableNumbers
        );
        pregnancyNumberDropdown.setAdapter(adapter);
        pregnancyNumberDropdown.setText("Pregnancy 1", false);

        pregnancyNumberDropdown.setOnItemClickListener((parent, view, position, id) -> {
            int selectedNumber = position + 1;
            if (selectedNumber != currentPregnancyNumber) {
                saveCurrentPregnancyToList();
                loadPregnancyByNumber(selectedNumber);
            }
        });
    }



    private void loadPregnancyRecords() {
        try {
            List<Pregnancy> allPregnancies = viewModel.findAllByIndividual(selectedIndividual.uuid);

            if (allPregnancies != null && !allPregnancies.isEmpty()) {
                pregnancyRecords = new ArrayList<>();

                // Process records
                for (Pregnancy p : allPregnancies) {

                    if (p.pregnancyOrder == 0) {
                        p.pregnancyOrder = 1;
                        viewModel.add(p);
                    }
                    pregnancyRecords.add(p);
                }

                // Sort by pregnancyOrder
                Collections.sort(pregnancyRecords, (p1, p2) ->
                        Integer.compare(p1.pregnancyOrder, p2.pregnancyOrder));

                updateDropdownOptions();
            } else {
                pregnancyRecords = new ArrayList<>();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            pregnancyRecords = new ArrayList<>();
        }
    }

    private void updateDropdownOptions() {
        List<String> availableNumbers = new ArrayList<>();

        // Get the highest pregnancyOrder from records
        int maxOrder = 0;
        for (Pregnancy p : pregnancyRecords) {
            if (p.pregnancyOrder > maxOrder) {
                maxOrder = p.pregnancyOrder;
            }
        }

        // Always show at least Pregnancy 1
        if (maxOrder == 0) {
            maxOrder = 1;
        }

        // Show all pregnancies from 1 to max
        for (int i = 1; i <= maxOrder; i++) {
            availableNumbers.add("Pregnancy " + i);
        }

        // Check if we can add a new pregnancy (max 10)
        if (maxOrder < 10) {
            boolean canAddNew = false;

            if (pregnancyRecords.isEmpty()) {
                canAddNew = true;
            } else {
                // Find the pregnancy with the highest order
                Pregnancy lastPregnancy = null;
                for (Pregnancy p : pregnancyRecords) {
                    if (p.pregnancyOrder == maxOrder) {
                        lastPregnancy = p;
                        break;
                    }
                }

                // Check if the last pregnancy has outcome = yes (1)
                if (lastPregnancy != null && lastPregnancy.outcome != null && lastPregnancy.outcome == 1) {
                    canAddNew = true;
                }
            }

            if (canAddNew) {
                availableNumbers.add("Pregnancy " + (maxOrder + 1));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                availableNumbers
        );
        pregnancyNumberDropdown.setAdapter(adapter);

        // Keep current selection if valid
        if (currentPregnancyNumber >= 1 && currentPregnancyNumber <= availableNumbers.size()) {
            pregnancyNumberDropdown.setText("Pregnancy " + currentPregnancyNumber, false);
        } else if (!availableNumbers.isEmpty()) {
            currentPregnancyNumber = 1;
            pregnancyNumberDropdown.setText("Pregnancy 1", false);
        }
    }

    private void loadPregnancyByNumber(int pregnancyNumber) {
        Pregnancy pregnancy = null;

        // Try to find existing pregnancy with this pregnancyOrder
        for (Pregnancy p : pregnancyRecords) {
            if (p.pregnancyOrder == pregnancyNumber) {
                pregnancy = p;
                break;
            }
        }

        // If not found, create new pregnancy record
        if (pregnancy == null) {
            pregnancy = createNewPregnancy(pregnancyNumber);
        }

        binding.setPregnancy(pregnancy);
        currentPregnancyNumber = pregnancyNumber;

        // CRITICAL FIX FOR outcome_date: Force UI update after binding
        binding.executePendingBindings();

        // Update UI state
        setFieldsEnabled(true);

        // Update dropdown to show current selection
        pregnancyNumberDropdown.setText("Pregnancy " + currentPregnancyNumber, false);

        // ADDITIONAL FIX: Manually set the outcome_date field if it exists
        if (pregnancy.outcome_date != null) {
            binding.editTextOutcomeDate.setText(pregnancy.getOutcome_date());
        }
    }

    private Pregnancy createNewPregnancy(int pregnancyNumber) {
        Pregnancy data = new Pregnancy();

        String uuid = UUID.randomUUID().toString();
        data.uuid = uuid.replaceAll("-", "");
        data.complete = 1;
        data.individual_uuid = selectedIndividual.getUuid();

        // Set pregnancyOrder (int, will be 1 by default from declaration but set explicitly)
        data.pregnancyOrder = pregnancyNumber;

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
        data.sttime = timeString;

        data.setInsertDate(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));
        data.setFormcompldate(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));

        // Set visit UUID
        VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit dts = visitViewModel.find(socialgroup.uuid);
            if (dts != null) {
                data.visit_uuid = dts.uuid;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void saveCurrentPregnancyToList() {
        Pregnancy current = binding.getPregnancy();
        if (current != null && current.uuid != null && !current.uuid.isEmpty()) {
            // Ensure pregnancyOrder is set (shouldn't be 0 with int, but safety check)
            if (current.pregnancyOrder == 0) {
                current.pregnancyOrder = currentPregnancyNumber;
            }

            int currentOrder = current.pregnancyOrder;

            boolean found = false;
            for (int i = 0; i < pregnancyRecords.size(); i++) {
                if (pregnancyRecords.get(i).pregnancyOrder == currentOrder) {
                    pregnancyRecords.set(i, current);
                    found = true;
                    break;
                }
            }

            if (!found) {
                pregnancyRecords.add(current);
                // Re-sort after adding
                Collections.sort(pregnancyRecords, (p1, p2) ->
                        Integer.compare(p1.pregnancyOrder, p2.pregnancyOrder));
            }
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.editTextRecordedDate.setEnabled(enabled);
        binding.editTextLastClinicVisitDate.setEnabled(enabled);
        binding.expectedDelivery.setEnabled(enabled);
        binding.editTextOutcomeDate.setEnabled(enabled);

        // Enable/disable radio buttons and other inputs as needed
        for (int i = 0; i < binding.outcome.getChildCount(); i++) {
            binding.outcome.getChildAt(i).setEnabled(enabled);
        }
    }

    private void initializeConfigSettings() {
        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        try {
            List<Configsettings> configsettings = configViewModel.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                String formattedDate = dateFormat.format(dt);
                editText.setText(formattedDate);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void save(boolean save, boolean close, PregnancyViewModel viewModel, Fieldworker fieldworkerData) {
        if (save) {
            Pregnancy finalData = binding.getPregnancy();

            // Validate dates
            if (!validateAllDates()) {
                return;
            }

            // Validate numeric fields
            if (!validateNumericFields(finalData)) {
                return;
            }

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.PREGNANCYLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }

            // Set end time
            Date end = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            String endtime = String.format("%02d:%02d:%02d", hh, mm, ss);

            if (finalData.sttime != null) {
                finalData.edtime = endtime;
            }

            finalData.complete = 1;
            finalData.fw_uuid = fieldworkerData.getFw_uuid();

            // CRITICAL: Ensure pregnancyOrder is set correctly and never 0
            finalData.pregnancyOrder = currentPregnancyNumber;

            // Update individual visited status
            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual visitedData = iview.visited(selectedIndividual.uuid);
                if (visitedData != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.individual_uuid;
                    visited.complete = 1;
                    iview.visited(visited);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            viewModel.add(finalData);

            saveCurrentPregnancyToList();
            updateDropdownOptions();

            Toast.makeText(requireContext(), "Pregnancy " + currentPregnancyNumber + " saved successfully", Toast.LENGTH_SHORT).show();
        }

        if (save && binding.getPregnancy().outcome == 1) {
            pregnancy = binding.getPregnancy();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyOutcomeFragment.newInstance(individual, locations, socialgroup, pregnancy)).commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
        }
    }

    private boolean validateAllDates() {
        try {
            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // Validate conception date against earliest event date
            if (!binding.earliest.getText().toString().trim().isEmpty() &&
                    !binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                Date stdate = f.parse(binding.earliest.getText().toString().trim());
                Date edate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                if (edate.before(stdate)) {
                    binding.editTextRecordedDate.setError("Conception Date Cannot Be Less than Earliest Event Date");
                    Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                binding.editTextRecordedDate.setError(null);
            }

            // Validate conception date and last clinic visit
            if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty() &&
                    !binding.editTextLastClinicVisitDate.getText().toString().trim().isEmpty()) {
                Date currentDate = new Date();
                Date stdate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                Date edate = f.parse(binding.editTextLastClinicVisitDate.getText().toString().trim());

                if (stdate.after(currentDate)) {
                    binding.editTextRecordedDate.setError("Date of Conception Cannot Be a Future Date");
                    Toast.makeText(getActivity(), "Date of Conception Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                if (edate.before(stdate) || edate.equals(stdate)) {
                    binding.editTextLastClinicVisitDate.setError("Last Visit Date Cannot Be Less than or Equal to Conception Date");
                    Toast.makeText(getActivity(), "Last Visit Date Cannot Be Less than or Equal to Conception Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                binding.editTextLastClinicVisitDate.setError(null);
            }

            // Validate outcome date against last clinic visit
            if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() &&
                    !binding.editTextLastClinicVisitDate.getText().toString().trim().isEmpty()) {
                Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                Date clinicDate = f.parse(binding.editTextLastClinicVisitDate.getText().toString().trim());
                if (outcomeDate.before(clinicDate)) {
                    binding.editTextLastClinicVisitDate.setError("Date of Outcome Cannot Be Before Last Clinic Visit Date");
                    Toast.makeText(getActivity(), "Date of Outcome Cannot Be Before Last Clinic Visit Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                binding.editTextLastClinicVisitDate.setError(null);
            }

            // Validate outcome date against conception date
            if (!binding.editTextRecordedDate.getText().toString().trim().isEmpty() &&
                    !binding.editTextOutcomeDate.getText().toString().trim().isEmpty()) {
                Date currentDate = new Date();
                Date conceptionDate = f.parse(binding.editTextRecordedDate.getText().toString().trim());
                Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());

                if (outcomeDate.after(currentDate)) {
                    binding.editTextOutcomeDate.setError("Date of Outcome Cannot Be a Future Date");
                    Toast.makeText(getActivity(), "Date of Outcome Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                if (outcomeDate.before(conceptionDate) || outcomeDate.equals(conceptionDate)) {
                    binding.editTextOutcomeDate.setError("Delivery Date Cannot Be Less than Conception Date");
                    Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_LONG).show();
                    return false;
                }
                binding.editTextOutcomeDate.setError(null);
            }

            // Validate pregnancy duration (conception to outcome should be 1-12 months)
            if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() &&
                    !binding.editTextRecordedDate.getText().toString().trim().isEmpty()) {
                Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                Date recordedDate = f.parse(binding.editTextRecordedDate.getText().toString().trim());

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(recordedDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(outcomeDate);

                int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                if (dayDiff < 0) {
                    monthDiff--;
                }

                int totalDiffMonths = yearDiff * 12 + monthDiff;

                if (totalDiffMonths < 1 || totalDiffMonths > 12) {
                    binding.editTextRecordedDate.setError("The difference between outcome and conception Date should be between 1 and 12 months");
                    Toast.makeText(getActivity(), "The difference between outcome and conception Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
                    return false;
                }
                binding.editTextRecordedDate.setError(null);
            }

            // Validate chronological order with previous pregnancies
            if (!validatePregnancyChronologicalOrder()) {
                return false;
            }

        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean validatePregnancyChronologicalOrder() {
        try {
            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentRecordedDateStr = binding.editTextRecordedDate.getText().toString().trim();
            String currentOutcomeDateStr = binding.editTextOutcomeDate.getText().toString().trim();

            if (currentRecordedDateStr.isEmpty()) {
                return true;
            }

            Date currentRecordedDate = f.parse(currentRecordedDateStr);
            Date currentOutcomeDate = !currentOutcomeDateStr.isEmpty() ? f.parse(currentOutcomeDateStr) : null;

            // Get the previous pregnancy (pregnancyOrder - 1)
            if (currentPregnancyNumber > 1) {
                Pregnancy previousPregnancy = null;
                for (Pregnancy p : pregnancyRecords) {
                    // FIXED: Direct int comparison instead of Integer
                    int pOrder = p.pregnancyOrder;
                    if (pOrder == (currentPregnancyNumber - 1)) {
                        previousPregnancy = p;
                        break;
                    }
                }

                if (previousPregnancy != null) {
                    // Validate recordedDate against previous pregnancy's recordedDate
                    if (previousPregnancy.recordedDate != null) {
                        Date prevRecordedDate = previousPregnancy.recordedDate;
                        if (currentRecordedDate.before(prevRecordedDate)) {
                            String prevDateStr = f.format(prevRecordedDate);
                            binding.editTextRecordedDate.setError("Pregnancy " + currentPregnancyNumber +
                                    " Conception Date cannot be before Pregnancy " + (currentPregnancyNumber - 1) +
                                    " Conception Date (" + prevDateStr + ")");
                            Toast.makeText(getActivity(),
                                    "Pregnancy " + currentPregnancyNumber + " Conception Date cannot be before Pregnancy " +
                                            (currentPregnancyNumber - 1) + " Conception Date", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    // Validate recordedDate against previous pregnancy's outcome_date (if exists)
                    if (previousPregnancy.outcome_date != null) {
                        Date prevOutcomeDate = previousPregnancy.outcome_date;
                        if (currentRecordedDate.before(prevOutcomeDate)) {
                            String prevOutcomeDateStr = f.format(prevOutcomeDate);
                            binding.editTextRecordedDate.setError("Pregnancy " + currentPregnancyNumber +
                                    " Conception Date cannot be before Pregnancy " + (currentPregnancyNumber - 1) +
                                    " Outcome Date (" + prevOutcomeDateStr + ")");
                            Toast.makeText(getActivity(),
                                    "Pregnancy " + currentPregnancyNumber + " Conception Date cannot be before Pregnancy " +
                                            (currentPregnancyNumber - 1) + " Outcome Date", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    // Validate outcomeDate against previous pregnancy's outcome_date (if both exist)
                    if (currentOutcomeDate != null && previousPregnancy.outcome_date != null) {
                        Date prevOutcomeDate = previousPregnancy.outcome_date;
                        if (currentOutcomeDate.before(prevOutcomeDate)) {
                            String prevOutcomeDateStr = f.format(prevOutcomeDate);
                            binding.editTextOutcomeDate.setError("Pregnancy " + currentPregnancyNumber +
                                    " Outcome Date cannot be before Pregnancy " + (currentPregnancyNumber - 1) +
                                    " Outcome Date (" + prevOutcomeDateStr + ")");
                            Toast.makeText(getActivity(),
                                    "Pregnancy " + currentPregnancyNumber + " Outcome Date cannot be before Pregnancy " +
                                            (currentPregnancyNumber - 1) + " Outcome Date", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }

            // Check against next pregnancy if it exists (in case editing earlier pregnancy)
            if (pregnancyRecords.size() > currentPregnancyNumber) {
                Pregnancy nextPregnancy = null;
                for (Pregnancy p : pregnancyRecords) {
                    // Direct int comparison instead of Integer
                    int pOrder = p.pregnancyOrder;
                    if (pOrder == (currentPregnancyNumber + 1)) {
                        nextPregnancy = p;
                        break;
                    }
                }

                if (nextPregnancy != null && nextPregnancy.recordedDate != null) {
                    Date nextRecordedDate = nextPregnancy.recordedDate;

                    // Current recordedDate cannot be after next pregnancy's recordedDate
                    if (currentRecordedDate.after(nextRecordedDate)) {
                        String nextDateStr = f.format(nextRecordedDate);
                        binding.editTextRecordedDate.setError("Pregnancy " + currentPregnancyNumber +
                                " Conception Date cannot be after Pregnancy " + (currentPregnancyNumber + 1) +
                                " Conception Date (" + nextDateStr + ")");
                        Toast.makeText(getActivity(),
                                "Pregnancy " + currentPregnancyNumber + " Conception Date cannot be after Pregnancy " +
                                        (currentPregnancyNumber + 1) + " Conception Date", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    // Current outcomeDate cannot be after next pregnancy's recordedDate
                    if (currentOutcomeDate != null && currentOutcomeDate.after(nextRecordedDate)) {
                        String nextDateStr = f.format(nextRecordedDate);
                        binding.editTextOutcomeDate.setError("Pregnancy " + currentPregnancyNumber +
                                " Outcome Date cannot be after Pregnancy " + (currentPregnancyNumber + 1) +
                                " Conception Date (" + nextDateStr + ")");
                        Toast.makeText(getActivity(),
                                "Pregnancy " + currentPregnancyNumber + " Outcome Date cannot be after Pregnancy " +
                                        (currentPregnancyNumber + 1) + " Conception Date", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }

            // Clear errors if all validations pass
            binding.editTextRecordedDate.setError(null);
            binding.editTextOutcomeDate.setError(null);

        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Error parsing date in chronological validation", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean validateNumericFields(Pregnancy finalData) {
        // Validate pregnancy weeks
        if (finalData.anteNatalClinic == 1 && !binding.ageOfPregFromPregNotes.getText().toString().trim().isEmpty()) {
            int totalweeks = Integer.parseInt(binding.ageOfPregFromPregNotes.getText().toString().trim());
            if (totalweeks < 4 || totalweeks > 52) {
                binding.ageOfPregFromPregNotes.setError("Maximum Number of Weeks Allowed is 4 - 52");
                Toast.makeText(getActivity(), "Maximum Number of Weeks Allowed is 4 - 52 ", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // Validate pregnancy months
        if (finalData.anteNatalClinic == 1 && !binding.estimatedAgeOfPreg.getText().toString().trim().isEmpty()) {
            int totalmnth = Integer.parseInt(binding.estimatedAgeOfPreg.getText().toString().trim());
            if (totalmnth < 1 || totalmnth > 12) {
                binding.estimatedAgeOfPreg.setError("Maximum Number of Months Allowed is 12");
                Toast.makeText(getActivity(), "Maximum Number of Months Allowed is 12", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (finalData.anteNatalClinic == 1
                && !binding.estimatedAgeOfPreg.getText().toString().trim().isEmpty()
                && !binding.ageOfPregFromPregNotes.getText().toString().trim().isEmpty()) {

            int totalMonths = Integer.parseInt(binding.estimatedAgeOfPreg.getText().toString().trim());
            int totalWeeks = Integer.parseInt(binding.ageOfPregFromPregNotes.getText().toString().trim());

            int totalWeeksConvertedToMonths = totalWeeks / 4;

            if (totalMonths < totalWeeksConvertedToMonths) {
                binding.ageOfPregFromPregNotes.setError("Check Number of Months and weeks Pregnant");
                Toast.makeText(getActivity(), "Check Number of Months and weeks Pregnant", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (finalData.anteNatalClinic == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
            int totalmnth = Integer.parseInt(binding.firstRec.getText().toString().trim());
            if (totalmnth < 1 || totalmnth > 12) {
                binding.firstRec.setError("Maximum Number of Months Allowed is 12");
                Toast.makeText(getActivity(), "Maximum Number of Months Allowed is 12", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (finalData.own_bnet == 1 && !binding.howMany.getText().toString().trim().isEmpty()) {
            int totalmnth = Integer.parseInt(binding.howMany.getText().toString().trim());
            if (totalmnth < 1 || totalmnth > 10) {
                binding.howMany.setError("Maximum Number of Bednets is 10");
                Toast.makeText(getActivity(), "Maximum Number of Bednets is 10", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (finalData.first_preg == 2 && !binding.pregnancyNumber.getText().toString().trim().isEmpty()) {
            int totalbirth = Integer.parseInt(binding.pregnancyNumber.getText().toString().trim());
            if (totalbirth < 2 || totalbirth > 15) {
                binding.pregnancyNumber.setError("Cannot be less than 2");
                Toast.makeText(getActivity(), "Total Pregnancies Cannot be less than 2", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (finalData.anteNatalClinic == 1 && !binding.ancVisits.getText().toString().trim().isEmpty()) {
            int totalmth = Integer.parseInt(binding.ancVisits.getText().toString().trim());
            if (totalmth < 1 || totalmth > 20) {
                binding.ancVisits.setError("Maximum Number of ANC Visit is 20");
                Toast.makeText(getActivity(), "Maximum Number of ANC Visit is 20", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private <T> void callable(Spinner spinner, T[] array) {
        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
                android.R.layout.simple_spinner_item, array);
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
        OUTCOMEDATE("OUTCOMEDATE"),
        RECORDDATE("RECORDDATE"),
        EXPECTDATE("EXPECTDATE"),
        CLINICDATE("CLINICDATE");

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

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, DATE_BUNDLES.OUTCOMEDATE, binding.editTextOutcomeDate);
            handleDateResult(bundle, DATE_BUNDLES.RECORDDATE, binding.editTextRecordedDate);
            handleDateResult(bundle, DATE_BUNDLES.EXPECTDATE, binding.expectedDelivery);
            handleDateResult(bundle, DATE_BUNDLES.CLINICDATE, binding.editTextLastClinicVisitDate);
        });

        binding.buttonPregnancyOutcome.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.OUTCOMEDATE, binding.editTextOutcomeDate));

        binding.buttonPregStartDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.RECORDDATE, binding.editTextRecordedDate));

        binding.buttonPregExpectDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.EXPECTDATE, binding.expectedDelivery));

        binding.buttonLastClinicVisitDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.CLINICDATE, binding.editTextLastClinicVisitDate));
    }

    private void handleDateResult(Bundle bundle, DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(DATE_BUNDLES dateType, TextInputEditText editText) {
        Calendar calendar = parseCurrentDate(editText.getText().toString());
        DialogFragment datePickerFragment = new DatePickerFragment(
                dateType.getBundleKey(),
                calendar
        );
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), TAG);
    }

    private Calendar parseCurrentDate(String dateString) {
        Calendar calendar = Calendar.getInstance();

        if (!TextUtils.isEmpty(dateString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date date = sdf.parse(dateString);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }
}