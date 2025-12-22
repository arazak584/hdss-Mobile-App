//package org.openhds.hdsscapture.Views;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import org.openhds.hdsscapture.AppConstants;
//import org.openhds.hdsscapture.R;
//import org.openhds.hdsscapture.Repositories.PregnancyoutcomeRepository;
//import org.openhds.hdsscapture.Utilities.HandlerSelect;
//import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
//import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
//import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
//import org.openhds.hdsscapture.databinding.FragmentBirthBinding;
//import org.openhds.hdsscapture.entity.Configsettings;
//import org.openhds.hdsscapture.entity.Pregnancyoutcome;
//import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
//import org.openhds.hdsscapture.Utilities.DatePickerFragment;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.ExecutionException;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BirthViewFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class BirthViewFragment extends Fragment {
//
//    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
//    private final String TAG = "OUTCOME.TAG";
//    private FragmentBirthBinding binding;
//    private PregnancyoutcomeRepository repository;
//    private Pregnancyoutcome pregnancyoutcome;
//
//    public BirthViewFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @return A new instance of fragment BirthFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BirthViewFragment newInstance(String uuid) {
//        BirthViewFragment fragment = new BirthViewFragment();
//        Bundle args = new Bundle();
//        args.putString(INDIVIDUAL_ID, uuid);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        repository = new PregnancyoutcomeRepository(requireActivity().getApplication());
//
//        if (getArguments() != null) {
//            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
//            this.pregnancyoutcome = new Pregnancyoutcome();  // Initialize placeholder
//            this.pregnancyoutcome.uuid = uuid;        // Assign UUID to fetch from DB
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.layout.fragment_birth, container, false);
//        binding = FragmentBirthBinding.inflate(inflater, container, false);
//
//        //CHOOSING THE DATE
//        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
//            // We use a String here, but any type that can be put in a Bundle is supported
//            if (bundle.containsKey((BirthViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
//                final String result = bundle.getString(BirthViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
//                binding.editTextOutcomeDate.setText(result);
//            }
//
//            if (bundle.containsKey((BirthViewFragment.DATE_BUNDLES.CONCEPTION.getBundleKey()))) {
//                final String result = bundle.getString(BirthViewFragment.DATE_BUNDLES.CONCEPTION.getBundleKey());
//                binding.editTextConception.setText(result);
//            }
//
//        });
//
//        binding.buttonOutcomeStartDate.setOnClickListener(v -> {
//            final Calendar c = Calendar.getInstance();
//            DialogFragment newFragment = new DatePickerFragment(BirthViewFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
//            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
//        });
//
//        binding.buttonOutcomeConception.setOnClickListener(v -> {
//            final Calendar c = Calendar.getInstance();
//            DialogFragment newFragment = new DatePickerFragment(BirthViewFragment.DATE_BUNDLES.CONCEPTION.getBundleKey(), c);
//            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
//        });
//
//        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
//
//        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
//        viewModel.getView(pregnancyoutcome.uuid).observe(getViewLifecycleOwner(), data -> {
//            if (data != null) {
//                binding.setPregoutcome(data);
//                binding.buttonOutcomeConception.setEnabled(false);
//                binding.buttonOutcomeStartDate.setEnabled(false);
//                binding.recAnc.setEnabled(false);
//                binding.monthPg.setEnabled(false);
//                binding.whoAnc.setEnabled(false);
//                binding.numAnc.setEnabled(false);
//                binding.preguuid.setEnabled(false);
//                binding.outcomes.setEnabled(false);
//
//                if(data.status!=null && data.status==2){
//                    cmt.setVisibility(View.VISIBLE);
//                }else{
//                    cmt.setVisibility(View.GONE);
//                }
//
//            }
//        });
//
//        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
//        List<Configsettings> configsettings = null;
//        try {
//            configsettings = configViewModel.findAll();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
//            TextView editText = binding.getRoot().findViewById(R.id.earliest);
//            if (dt != null) {
//                String formattedDate = dateFormat.format(dt);
//                editText.setText(formattedDate);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
//        loadCodeData(binding.birthplace, codeBookViewModel, "birthPlace");
//        loadCodeData(binding.notDel, codeBookViewModel, "notdel");
//        loadCodeData(binding.whyNoAnc, codeBookViewModel, "notdel");
//        loadCodeData(binding.recAnc, codeBookViewModel, "yn_anc");
//        loadCodeData(binding.recIpt, codeBookViewModel, "complete");
//        loadCodeData(binding.assDel, codeBookViewModel, "assist");
//        loadCodeData(binding.howDel, codeBookViewModel, "howdel");
//        loadCodeData(binding.whereAnc, codeBookViewModel, "birthPlace");
//        loadCodeData(binding.whoAnc, codeBookViewModel, "assist");
//        loadCodeData(binding.father, codeBookViewModel, "complete");
//        loadCodeData(binding.id1006, codeBookViewModel, "more_chd");
//        loadCodeData(binding.id1007, codeBookViewModel, "preg_chd");
//        loadCodeData(binding.id1008, codeBookViewModel, "complete");
//        loadCodeData(binding.id1009, codeBookViewModel, "fam_plan_method");
//
//        binding.buttonSaveClose.setOnClickListener(v -> {
//
//            save(true, true, viewModel);
//        });
//
//        binding.buttonClose.setOnClickListener(v -> {
//
//            save(false, true, viewModel);
//        });
//
//        HandlerSelect.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
//        View view = binding.getRoot();
//        return view;
//
//    }
//
//    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel) {
//
//        if (save) {
//            Pregnancyoutcome finalData = binding.getPregoutcome();
//
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
//
//            final boolean validateOnComplete = true;//finalData.complete == 1;
//            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);
//
//            if (hasErrors) {
//                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
//                return;
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
//
//            viewModel.add(finalData);
//            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();
//
//        }
//        Integer lb = binding.getPregoutcome().numberofBirths;
//
//        if (save) {
////            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
////                    BirthAViewFragment.newInstance(pregnancyoutcome.uuid)).commit();
//        }else {
//            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    ViewFragment.newInstance()).commit();
//        }
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    private <T> void callable(Spinner spinner, T[] array) {
//
//        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
//                android.R.layout.simple_spinner_item, array
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//    }
//
//    private void loadCodeData(Spinner spinner, CodeBookViewModel viewModel, final String codeFeature) {
//        try {
//            List<KeyValuePair> list = viewModel.findCodesOfFeature(codeFeature);
//            KeyValuePair kv = new KeyValuePair();
//            kv.codeValue = AppConstants.NOSELECT;
//            kv.codeLabel = AppConstants.SPINNER_NOSELECT;
//            if (list != null && !list.isEmpty()) {
//                list.add(0, kv);
//                callable(spinner, list.toArray(new KeyValuePair[0]));
//            } else {
//                list = new ArrayList<KeyValuePair>();
//                list.add(kv);
//
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private enum DATE_BUNDLES {
//        CONCEPTION ("CONCEPTION"),
//        DOB ("DOB"),
//        DOD ("DOD"),
//        RECORDDATE ("RECORDDATE");
//
//        private final String bundleKey;
//
//        DATE_BUNDLES(String bundleKey) {
//            this.bundleKey = bundleKey;
//
//        }
//
//        public String getBundleKey() {
//            return bundleKey;
//        }
//
//        @Override
//        public String toString() {
//            return bundleKey;
//        }
//    }
//}