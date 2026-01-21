package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.FatherOutcomeDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueUUIDGenerator;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentPregnancyOutcomeBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.validations.PregnancyoutcomeValidation;

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
 * Use the {@link PregnancyOutcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PregnancyOutcomeFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OUTCOME.TAG";
    public static final String PREGNANCY = "PREGNANCY";
    private String fw;

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentPregnancyOutcomeBinding binding;
    private ProgressDialog progressDialog;
    private Locations selectedLocation;
    private PregnancyoutcomeViewModel viewModel;
    private Individual selectedIndividual;
    private Pregnancy pregnancy;
    private int pregnancyNumber;
    private Pregnancyoutcome pregnancyoutcome;

    public PregnancyOutcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 2.
     * @param individual Parameter 3.
     * @param pregnancy Parameter 4.
     * @return A new instance of fragment BirthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyOutcomeFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup, Pregnancy pregnancy) {
        PregnancyOutcomeFragment fragment = new PregnancyOutcomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(PREGNANCY, pregnancy);
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
            pregnancy = getArguments().getParcelable(PREGNANCY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_birth, container, false);
        binding = FragmentPregnancyOutcomeBinding.inflate(inflater, container, false);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        Button showDialogButtons = binding.getRoot().findViewById(R.id.button_outcome_father);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        //Date Selection
        //setupDatePickers();

        // Set a click listener on the button for mother
        showDialogButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Fathers...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);

                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);

                // Show the dialog fragment
                FatherOutcomeDialogFragment.newInstance(individual, locations,socialgroup)
                        .show(getChildFragmentManager(), "FatherOutcomeDialogFragment");
            }
        });

        // Retrieve fw_uuid from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);

        PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        try {
            Pregnancyoutcome data = viewModel.getId(pregnancy.uuid);
            if (data != null) {
                binding.setPregoutcome(data);
                binding.buttonOutcomeConception.setEnabled(false);
                binding.buttonOutcomeStartDate.setEnabled(false);
                binding.recAnc.setEnabled(false);
                binding.monthPg.setEnabled(false);
                binding.whoAnc.setEnabled(false);
                binding.numAnc.setEnabled(false);
                binding.preguuid.setEnabled(false);
                data.location = selectedLocation.uuid;

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                }
                    data.outcomeDate = pregnancy.outcome_date;
                    data.conceptionDate = pregnancy.recordedDate;
                    data.rec_anc = pregnancy.anteNatalClinic;
                    data.month_pg = pregnancy.first_rec;
                    data.who_anc = pregnancy.attend_you;
                    data.num_anc = pregnancy.anc_visits;
                    data.pregnancy_uuid = pregnancy.uuid;

            } else {
                data = new Pregnancyoutcome();

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dta = visitViewModel.find(socialgroup.uuid);
                if (dta != null){
                    data.visit_uuid = dta.uuid;
                }
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fw;
                //data.uuid = uuidString;
                data.uuid = UniqueUUIDGenerator.generate(getContext());
                data.mother_uuid = selectedIndividual.getUuid();
                //data.complete = 1;
                data.location = selectedLocation.uuid;
                data.outcomeDate = pregnancy.outcome_date;
                data.conceptionDate = pregnancy.recordedDate;
                data.rec_anc = pregnancy.anteNatalClinic;
                data.month_pg = pregnancy.first_rec;
                data.who_anc = pregnancy.attend_you;
                data.num_anc = pregnancy.anc_visits;
                data.pregnancy_uuid = pregnancy.uuid;
                binding.recAnc.setEnabled(false);
                binding.monthPg.setEnabled(false);
                binding.whoAnc.setEnabled(false);
                binding.numAnc.setEnabled(false);
                binding.preguuid.setEnabled(false);

                Date currentDate = new Date(); // Get the current date and time
                // Create a Calendar instance and set it to the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;
                binding.buttonOutcomeConception.setEnabled(false);
                binding.buttonOutcomeStartDate.setEnabled(false);

                binding.setPregoutcome(data);
                binding.getPregoutcome().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = configViewModel.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            TextView editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                String formattedDate = dateFormat.format(dt);
                editText.setText(formattedDate);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.birthplace, codeBookViewModel, "birthPlace");
        loadCodeData(binding.notDel, codeBookViewModel, "notdel");
        loadCodeData(binding.whyNoAnc, codeBookViewModel, "notdel");
        loadCodeData(binding.recAnc, codeBookViewModel, "yn_anc");
        loadCodeData(binding.recIpt, codeBookViewModel, "complete");
        loadCodeData(binding.assDel, codeBookViewModel, "assist");
        loadCodeData(binding.howDel, codeBookViewModel, "howdel");
        loadCodeData(binding.whereAnc, codeBookViewModel, "birthPlace");
        loadCodeData(binding.whoAnc, codeBookViewModel, "assist");
        //loadCodeData(binding.father, codeBookViewModel, "complete");
        loadCodeData(binding.id1006, codeBookViewModel, "more_chd");
        loadCodeData(binding.id1007, codeBookViewModel, "preg_chd");
        loadCodeData(binding.id1008, codeBookViewModel, "complete");
        loadCodeData(binding.id1009, codeBookViewModel, "fam_plan_method");
        loadCodeData(binding.firstNb, codeBookViewModel, "complete");
        loadCodeData(binding.id1001, codeBookViewModel, "complete");
        loadCodeData(binding.id1002, codeBookViewModel, "how_lng");
        loadCodeData(binding.id1003, codeBookViewModel, "complete");
        loadCodeData(binding.id1004, codeBookViewModel, "complete");
        loadCodeData(binding.id1005, codeBookViewModel, "feed_chd");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel) {

        if (save) {
            Pregnancyoutcome finalData = binding.getPregoutcome();

            // Create validator and validate all
            PregnancyoutcomeValidation validator = new PregnancyoutcomeValidation(
                    requireContext(),
                    finalData,
                    getEarliestEventDate()
            );

            if (!validator.validateAll()) {
                return; // Validation failed, errors shown to user
            }

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

//            try {
//                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
//                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
//                    Date edate = f.parse(binding.editTextConception.getText().toString().trim());
//                    if (edate.before(stdate)) {
//                        binding.editTextConception.setError("Conception Date Cannot Be Less than Earliest Event Date");
//                        Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    // clear error if validation passes
//                    binding.editTextConception.setError(null);
//                }
//            } catch (ParseException e) {
//                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//
//            try {
//                if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
//                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                    Date currentDate = new Date();
//                    Date stdate = f.parse(binding.editTextConception.getText().toString().trim());
//                    Date edate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
//                    if (edate.after(currentDate)) {
//                        binding.editTextOutcomeDate.setError("Date of Delivery Cannot Be a Future Date");
//                        Toast.makeText(getActivity(), "Date of Delivery Cannot Be a Future Date", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    if (edate.before(stdate)) {
//                        binding.editTextConception.setError("Delivery Date Cannot Be Less than Conception Date");
//                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    // clear error if validation passes
//                    binding.editTextConception.setError(null);
//                }
//            } catch (ParseException e) {
//                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//
//            boolean month = false;
//            if (finalData.rec_anc == 1 && !binding.monthPg.getText().toString().trim().isEmpty()) {
//                int totalmth = Integer.parseInt(binding.monthPg.getText().toString().trim());
//                if (totalmth < 1 || totalmth > 12) {
//                    month = true;
//                    binding.monthPg.setError("Months Pregnant Before ANC Cannot be More than 12");
//                    Toast.makeText(getActivity(), "Months Pregnant Before ANC Cannot be More than 12", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            boolean anc = false;
//            if (finalData.rec_anc == 1 && !binding.numAnc.getText().toString().trim().isEmpty()) {
//                int totalmth = Integer.parseInt(binding.numAnc.getText().toString().trim());
//                if (totalmth < 1 || totalmth > 20) {
//                    anc = true;
//                    binding.numAnc.setError("Maximum Number of ANC Visit is 20");
//                    Toast.makeText(getActivity(), "Maximum Number of ANC Visit is 20", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            boolean ipt = false;
//            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
//                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
//                if (totalmth < 1 || totalmth > 12) {
//                    ipt = true;
//                    binding.firstRec.setError("Months Pregnant for IPT Cannot be More than 12");
//                    Toast.makeText(getActivity(), "Months Pregnant for IPT Cannot be More than 12", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            boolean iptt = false;
//            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.manyIpt.getText().toString().trim().isEmpty()) {
//                int totalmth = Integer.parseInt(binding.manyIpt.getText().toString().trim());
//                if (totalmth < 1 || totalmth > 10) {
//                    iptt = true;
//                    binding.manyIpt.setError("Number of IPT taken Cannot be More than 10");
//                    Toast.makeText(getActivity(), "Number of IPT taken Cannot be More than 10", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            boolean iptm = false;
//            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
//                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
//                if (totalmth < 3) {
//                    iptm = true;
//                    binding.firstRec.setError("IPT is given at 13 weeks (3 Months)");
//                    Toast.makeText(getActivity(), "IPT is given at 13 weeks (3 Months)", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            boolean nurse = false;
//            if (finalData.ass_del != 1 && finalData.how_del == 2) {
//                nurse = true;
//                Toast.makeText(getActivity(), "Only Doctors Perform Caesarian Section", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            boolean val = false;
//            if (!binding.etOutcomes.getText().toString().trim().isEmpty() && !binding.numberOfLiveBirths.getText().toString().trim().isEmpty()) {
//                int totalOutcomes = Integer.parseInt(binding.etOutcomes.getText().toString().trim());
//                int lvBirths = Integer.parseInt(binding.numberOfLiveBirths.getText().toString().trim());
//                if (totalOutcomes < lvBirths) {
//                    val = true;
//                    binding.numberOfLiveBirths.setError("Number of livebirths for this pregnancy cannot be more than number of outcomes");
//                    Toast.makeText(getActivity(), "Number of livebirths for this pregnancy cannot be more than number of outcomes", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//
//
//
//            if (binding.getPregoutcome().numberofBirths != null) {
//
//                try {
//                    if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
//                        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                        Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
//                        Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());
//
//                        Calendar startCalendar = Calendar.getInstance();
//                        startCalendar.setTime(recordedDate);
//
//                        Calendar endCalendar = Calendar.getInstance();
//                        endCalendar.setTime(outcomeDate);
//
//                        int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
//                        int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
//                        int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);
//
//                        // Adjust the difference based on the day component
//                        if (dayDiff < 0) {
//                            monthDiff--;
//                        }
//
//                        // Calculate the total difference in months
//                        int totalDiffMonths = yearDiff * 12 + monthDiff;
//
//                        if (totalDiffMonths < 1 || totalDiffMonths > 12) {
//                            binding.editTextConception.setError("The difference between outcome and conception Date should be between 1 and 12 months");
//                            Toast.makeText(getActivity(), "The difference between outcome and conception Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        // Clear error if validation passes
//                        binding.editTextConception.setError(null);
//                    }
//                } catch (ParseException e) {
//                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//
//
//            }

            finalData.mother_uuid = selectedIndividual.getUuid();
            finalData.visit_uuid = binding.getPregoutcome().visit_uuid;
            finalData.uuid = binding.getPregoutcome().uuid;


            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {

                Individual visitedData = iview.visited(selectedIndividual.uuid);
                if (visitedData != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.mother_uuid;
                    visited.complete = 1;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finalData.complete =1;
            viewModel.add(finalData);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }

        if (save) {
            pregnancyoutcome = binding.getPregoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    OutcomeFragment.newInstance(individual, locations, socialgroup, pregnancyoutcome, pregnancy, 1)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyFragment.newInstance(individual, locations, socialgroup, pregnancyNumber)).commit();
        }

    }

    // Helper method to get earliest event date
    private Date getEarliestEventDate() {
        try {
            if (!binding.earliest.getText().toString().trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                return sdf.parse(binding.earliest.getText().toString().trim());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
        DOB ("DOB"),
        DOD ("DOD"),
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

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, DATE_BUNDLES.DOB, binding.editTextOutcomeDate);
            handleDateResult(bundle, PregnancyOutcomeFragment.DATE_BUNDLES.RECORDDATE, binding.editTextConception);
        });

        binding.buttonOutcomeStartDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.DOB, binding.editTextOutcomeDate));

        binding.buttonOutcomeConception.setEndIconOnClickListener(v ->
                showDatePicker(PregnancyOutcomeFragment.DATE_BUNDLES.RECORDDATE, binding.editTextConception));

    }

    private void handleDateResult(Bundle bundle, PregnancyOutcomeFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(PregnancyOutcomeFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
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