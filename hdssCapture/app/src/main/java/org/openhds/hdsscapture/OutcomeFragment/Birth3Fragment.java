package org.openhds.hdsscapture.OutcomeFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.FatherOutcomeDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentBirthBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
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
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.DatePickerFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

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
 * Use the {@link Birth3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Birth3Fragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OUTCOME.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentBirthBinding binding;
    private ProgressDialog progressDialog;

    public Birth3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment BirthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Birth3Fragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        Birth3Fragment fragment = new Birth3Fragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_birth, container, false);
        binding = FragmentBirthBinding.inflate(inflater, container, false);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);
        final TextView title = binding.getRoot().findViewById(R.id.preg);
        title.setText("Pregnancy Outcome 3");

        Button showDialogButtons = binding.getRoot().findViewById(R.id.button_outcome_father);

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

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((Birth3Fragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(Birth3Fragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.editTextOutcomeDate.setText(result);
            }

            if (bundle.containsKey((Birth3Fragment.DATE_BUNDLES.CONCEPTION.getBundleKey()))) {
                final String result = bundle.getString(Birth3Fragment.DATE_BUNDLES.CONCEPTION.getBundleKey());
                binding.editTextConception.setText(result);
            }

        });

        binding.buttonOutcomeStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(Birth3Fragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonOutcomeConception.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(Birth3Fragment.DATE_BUNDLES.CONCEPTION.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);

        PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        try {
            Pregnancyoutcome data = viewModel.find3(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.compno);
            if (data != null) {
                binding.setPregoutcome(data);
                binding.buttonOutcomeConception.setEnabled(false);
                binding.buttonOutcomeStartDate.setEnabled(false);
                binding.recAnc.setEnabled(false);
                binding.monthPg.setEnabled(false);
                binding.whoAnc.setEnabled(false);
                binding.numAnc.setEnabled(false);
                binding.preguuid.setEnabled(false);
                data.location = ClusterFragment.selectedLocation.uuid;

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                }

                Pregnancy dts = pregnancyViewModel.out3(HouseMembersFragment.selectedIndividual.uuid);
                if (dts != null){
                    data.outcomeDate = dts.outcome_date;
                    data.conceptionDate = dts.recordedDate;
                    data.rec_anc = dts.anteNatalClinic;
                    data.month_pg = dts.first_rec;
                    data.who_anc = dts.attend_you;
                    data.num_anc = dts.anc_visits;
                    data.pregnancy_uuid = dts.uuid;
                }

                // Fetch the last record before the current one
                Pregnancyoutcome previousOutcome = viewModel.lastpregs(HouseMembersFragment.selectedIndividual.uuid, data.outcomeDate);
                if (previousOutcome != null) {
                    binding.setPreg(previousOutcome);
                } else {
                    binding.lastPreg.setVisibility(View.GONE);
                }
            } else {
                data = new Pregnancyoutcome();

                Pregnancy dts = pregnancyViewModel.out3(HouseMembersFragment.selectedIndividual.uuid);
                if (dts != null){
                    data.outcomeDate = dts.outcome_date;
                    data.conceptionDate = dts.recordedDate;
                    data.rec_anc = dts.anteNatalClinic;
                    data.month_pg = dts.first_rec;
                    data.who_anc = dts.attend_you;
                    data.num_anc = dts.anc_visits;
                    data.pregnancy_uuid = dts.uuid;

                    binding.recAnc.setEnabled(false);
                    binding.monthPg.setEnabled(false);
                    binding.whoAnc.setEnabled(false);
                    binding.numAnc.setEnabled(false);
                    binding.preguuid.setEnabled(false);
                }
                if(data.pregnancy_uuid ==null){
                    Toast.makeText(getContext(), "Kindly Pick the Pregnancy Before you pick the Outcome", Toast.LENGTH_LONG).show();
                }

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dta = visitViewModel.find(socialgroup.uuid);
                if (dta != null){
                    data.visit_uuid = dta.uuid;
                }

                // Fetch the last record before the current one
                Pregnancyoutcome previousOutcome = viewModel.lastpregs(HouseMembersFragment.selectedIndividual.uuid, data.outcomeDate);
                if (previousOutcome != null) {
                    binding.setPreg(previousOutcome);
                } else {
                    binding.lastPreg.setVisibility(View.GONE);
                }

                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                //data.complete = 1;
                data.location = ClusterFragment.selectedLocation.uuid;
                data.extra = 2;
                data.id = 3;

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

//        try {
//            Pregnancyoutcome datas = viewModel.findpreg(HouseMembersFragment.selectedIndividual.uuid);
//            if (datas != null) {
//                binding.setPreg(datas);
//
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }

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
        loadCodeData(binding.father, codeBookViewModel, "complete");
        loadCodeData(binding.id1006, codeBookViewModel, "more_chd");
        loadCodeData(binding.id1007, codeBookViewModel, "preg_chd");
        loadCodeData(binding.id1008, codeBookViewModel, "complete");
        loadCodeData(binding.id1009, codeBookViewModel, "fam_plan_method");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        Handler.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel) {

        if (save) {
            Pregnancyoutcome finalData = binding.getPregoutcome();

            final Intent j = getActivity().getIntent();
            final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.editTextConception.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.editTextConception.setError("Conception Date Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextConception.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextConception.getText().toString().trim());
                    Date edate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    if (edate.after(currentDate)) {
                        binding.editTextOutcomeDate.setError("Date of Delivery Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Delivery Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.before(stdate)) {
                        binding.editTextConception.setError("Delivery Date Cannot Be Less than Conception Date");
                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextConception.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            boolean month = false;
            if (finalData.rec_anc == 1 && !binding.monthPg.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.monthPg.getText().toString().trim());
                if (totalmth < 1 || totalmth > 12) {
                    month = true;
                    binding.monthPg.setError("Months Pregnant Before ANC Cannot be More than 12");
                    Toast.makeText(getActivity(), "Months Pregnant Before ANC Cannot be More than 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean anc = false;
            if (finalData.rec_anc == 1 && !binding.numAnc.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.numAnc.getText().toString().trim());
                if (totalmth < 1 || totalmth > 20) {
                    anc = true;
                    binding.numAnc.setError("Maximum Number of ANC Visit is 20");
                    Toast.makeText(getActivity(), "Maximum Number of ANC Visit is 20", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean ipt = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
                if (totalmth < 1 || totalmth > 12) {
                    ipt = true;
                    binding.firstRec.setError("Months Pregnant for IPT Cannot be More than 12");
                    Toast.makeText(getActivity(), "Months Pregnant for IPT Cannot be More than 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean iptt = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.manyIpt.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.manyIpt.getText().toString().trim());
                if (totalmth < 1 || totalmth > 10) {
                    iptt = true;
                    binding.manyIpt.setError("Number of IPT taken Cannot be More than 10");
                    Toast.makeText(getActivity(), "Number of IPT taken Cannot be More than 10", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean iptm = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
                if (totalmth < 3) {
                    iptm = true;
                    binding.firstRec.setError("IPT is given at 13 weeks (3 Months)");
                    Toast.makeText(getActivity(), "IPT is given at 13 weeks (3 Months)", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean nurse = false;
            if (finalData.ass_del != 1 && finalData.how_del == 2) {
                nurse = true;
                Toast.makeText(getActivity(), "Only Doctors Perform Caesarian Section", Toast.LENGTH_LONG).show();
                return;
            }


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }




            if (binding.getPregoutcome().numberofBirths != null) {

                try {
                    if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                        Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

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
                            binding.editTextConception.setError("The difference between outcome and conception Date should be between 1 and 12 months");
                            Toast.makeText(getActivity(), "The difference between outcome and conception Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Clear error if validation passes
                        binding.editTextConception.setError(null);
                    }
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }

            finalData.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
            finalData.visit_uuid = binding.getPregoutcome().visit_uuid;
            finalData.uuid = binding.getPregoutcome().uuid;
            //finalData.complete=1;
            viewModel.add(finalData);
            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual data = iview.visited(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = binding.getPregoutcome().mother_uuid;
                    visited.complete = 2;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }
        Integer lb = binding.getPregoutcome().numberofBirths;

        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    Birth3AFragment.newInstance(individual, locations, socialgroup)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
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
}