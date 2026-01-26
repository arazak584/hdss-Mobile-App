package org.openhds.hdsscapture.Views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.VaccinationRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.databinding.FragmentVaccinationBinding;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.fragment.KeyboardFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VaccinationViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaccinationViewFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "VACCINATION.TAG";
    private FragmentVaccinationBinding binding;
    private Vaccination vaccination;
    private VaccinationRepository repository;

    public VaccinationViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VaccinationViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VaccinationViewFragment newInstance(String uuid) {
        VaccinationViewFragment fragment = new VaccinationViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new VaccinationRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.vaccination = new Vaccination();  // Initialize placeholder
            this.vaccination.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVaccinationBinding.inflate(inflater, container, false);
        binding.setVaccination(vaccination);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        //CHOOSING THE DATE
        setupDatePickers();

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        VaccinationViewModel viewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        viewModel.getView(vaccination.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setVaccination(data);

            if(data.status!=null && data.status==2){
                cmt.setVisibility(View.VISIBLE);
                rsv.setVisibility(View.VISIBLE);
                rsvd.setVisibility(View.VISIBLE);
            }else{
                cmt.setVisibility(View.GONE);
                rsv.setVisibility(View.GONE);
                rsvd.setVisibility(View.GONE);
            } Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();


        });

        //Codebook
        loadCodeData(binding.sbf, "complete");
        loadCodeData(binding.nhis, "nhis");
        loadCodeData(binding.hl, "HL");
        loadCodeData(binding.rea, "rea");
        loadCodeData(binding.onet, "onet");
        loadCodeData(binding.hcard, "HC");
        loadCodeData(binding.reason, "reavac");
        loadCodeData(binding.admission, "complete");
        loadCodeData(binding.bednet, "complete");
        loadCodeData(binding.scar, "scar");
        loadCodeData(binding.fever, "complete");
        loadCodeData(binding.fevertreat, "complete");
        loadCodeData(binding.diarrhoea, "complete");
        loadCodeData(binding.diarrhoeatreat, "complete");
        loadCodeData(binding.arti, "complete");
        loadCodeData(binding.artitreat, "complete");
        loadCodeData(binding.slpbednet, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        binding.setEventname(AppConstants.EVENT_VAC);
        HandlerSelect.colorLayouts(requireContext(), binding.VACLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, VaccinationViewModel viewModel) {

        if (save) {
            Vaccination finalData = binding.getVaccination();

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.opv0.getText().toString().trim().isEmpty() && !binding.opv1.getText().toString().trim().isEmpty() ||
                        !binding.opv2.getText().toString().trim().isEmpty() || !binding.opv3.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date opv0 = f.parse(binding.opv0.getText().toString().trim());
                    Date opv1 = f.parse(binding.opv1.getText().toString().trim());
                    Date opv2 = f.parse(binding.opv2.getText().toString().trim());
                    Date opv3 = f.parse(binding.opv3.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (opv0.after(currentDate)) {
                        binding.opv0.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (opv0.before(dob)) {
                        binding.opv0.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (opv1.before(opv0)) {
                        binding.opv1.setError("OPV1 Date Cannot Be Less than OPV0 Date");
                        Toast.makeText(getActivity(), "OPV1 Date Cannot Be Less than OPV0 Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (opv2.before(opv1)) {
                        binding.opv2.setError("OPV2 Date Cannot Be Less than OPV1 Date");
                        Toast.makeText(getActivity(), "OPV2 Date Cannot Be Less than OPV1 Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (opv3.before(opv3)) {
                        binding.opv3.setError("OPV3 Date Cannot Be Less than OPV2 Date");
                        Toast.makeText(getActivity(), "OPV3 Date Cannot Be Less than OPV2 Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.opv1.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.DPTHepBHib1.getText().toString().trim().isEmpty() && !binding.DPTHepBHib2.getText().toString().trim().isEmpty() ||
                        !binding.DPTHepBHib3.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date DPTHepBHib1 = f.parse(binding.DPTHepBHib1.getText().toString().trim());
                    Date DPTHepBHib2 = f.parse(binding.DPTHepBHib2.getText().toString().trim());
                    Date DPTHepBHib3 = f.parse(binding.DPTHepBHib3.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (DPTHepBHib1.after(currentDate)) {
                        binding.DPTHepBHib1.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (DPTHepBHib1.before(dob)) {
                        binding.DPTHepBHib1.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (DPTHepBHib2.before(DPTHepBHib1)) {
                        binding.DPTHepBHib2.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (DPTHepBHib3.before(DPTHepBHib2)) {
                        binding.DPTHepBHib3.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.DPTHepBHib1.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.pneumo1.getText().toString().trim().isEmpty() && !binding.pneumo2.getText().toString().trim().isEmpty() ||
                        !binding.pneumo3.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date pneumo1 = f.parse(binding.pneumo1.getText().toString().trim());
                    Date pneumo2 = f.parse(binding.pneumo2.getText().toString().trim());
                    Date pneumo3 = f.parse(binding.pneumo3.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (pneumo1.after(currentDate)) {
                        binding.pneumo1.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (pneumo1.before(dob)) {
                        binding.pneumo1.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (pneumo2.before(pneumo1)) {
                        binding.pneumo2.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (pneumo3.before(pneumo2)) {
                        binding.pneumo3.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.pneumo1.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.rota1.getText().toString().trim().isEmpty() && !binding.rota2.getText().toString().trim().isEmpty() ||
                        !binding.rota3.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date rota1 = f.parse(binding.rota1.getText().toString().trim());
                    Date rota2 = f.parse(binding.rota2.getText().toString().trim());
                    Date rota3 = f.parse(binding.rota3.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (rota1.after(currentDate)) {
                        binding.rota1.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (rota1.before(dob)) {
                        binding.rota1.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (rota2.before(rota1)) {
                        binding.rota2.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (rota3.before(rota2)) {
                        binding.rota3.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.rota1.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.ipv.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date ipv = f.parse(binding.ipv.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (ipv.after(currentDate)) {
                        binding.ipv.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (ipv.before(dob)) {
                        binding.ipv.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.ipv.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.admitDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date admit = f.parse(binding.admitDate.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (admit.after(currentDate)) {
                        binding.ipv.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (admit.before(dob)) {
                        binding.ipv.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.ipv.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.vitaminA6.getText().toString().trim().isEmpty() && !binding.vitaminA12.getText().toString().trim().isEmpty() ||
                        !binding.vitaminA18.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date vita1 = f.parse(binding.vitaminA6.getText().toString().trim());
                    Date vita2 = f.parse(binding.vitaminA12.getText().toString().trim());
                    Date vita3 = f.parse(binding.vitaminA18.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (vita1.after(currentDate)) {
                        binding.vitaminA6.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (vita2.before(vita1)) {
                        binding.vitaminA12.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (vita1.before(dob)) {
                        binding.vitaminA6.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (vita3.before(vita2)) {
                        binding.vitaminA18.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.vitaminA6.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.rtss6.getText().toString().trim().isEmpty() && !binding.rtss6.getText().toString().trim().isEmpty() ||
                        !binding.rtss9.getText().toString().trim().isEmpty() || !binding.rtss18.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date rtss1 = f.parse(binding.rtss6.getText().toString().trim());
                    Date rtss2 = f.parse(binding.rtss7.getText().toString().trim());
                    Date rtss3 = f.parse(binding.rtss9.getText().toString().trim());
                    Date rtss4 = f.parse(binding.rtss18.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (rtss1.after(currentDate)) {
                        binding.rtss6.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (rtss1.before(dob)) {
                        binding.rtss6.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (rtss2.before(rtss1)) {
                        binding.rtss6.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (rtss3.before(rtss2)) {
                        binding.rtss9.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (rtss4.before(rtss3)) {
                        binding.rtss18.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.rtss6.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.measles1.getText().toString().trim().isEmpty() && !binding.measles2.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date measles1 = f.parse(binding.measles1.getText().toString().trim());
                    Date measles2 = f.parse(binding.measles2.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (measles1.after(currentDate)) {
                        binding.measles1.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (measles1.before(dob)) {
                        binding.measles1.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (measles2.before(measles1)) {
                        binding.measles2.setError("Date Cannot Be Less than Previous Date");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than Previous Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.measles1.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.yellowFever.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date yf = f.parse(binding.yellowFever.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (yf.after(currentDate)) {
                        binding.yellowFever.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (yf.before(dob)) {
                        binding.yellowFever.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.yellowFever.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.menA.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date men = f.parse(binding.menA.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (men.after(currentDate)) {
                        binding.menA.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (men.before(dob)) {
                        binding.menA.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.menA.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.itn.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date itn = f.parse(binding.itn.getText().toString().trim());
                    Date dob = f.parse(binding.dob.getText().toString().trim());
                    if (itn.after(currentDate)) {
                        binding.itn.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (itn.before(dob)) {
                        binding.itn.setError("Date Cannot Be Less than DOB");
                        Toast.makeText(getActivity(), "Date Cannot Be Less than DOB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.itn.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.VACLAYOUT, validateOnComplete, false);


            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                //return;
            }

            finalData.complete=1;

            viewModel.add(finalData);

            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
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
        BCG ("BCG"),
        OPV0 ("OPV0"),
        OPV1("OPV1"),
        OPV2("OPV2"),
        OPV3("OPV3"),
        DEPT1 ("DEPT1"),
        DEPT2 ("DEPT2"),
        DEPT3 ("DEPT3"),
        PNEUMO1 ("PNEUMO1"),
        PNEUMO2 ("PNEUMO2"),
        PNEUMO3 ("PNEUMO3"),
        ROTA1 ("ROTA1"),
        ROTA2 ("ROTA2"),
        ROTA3 ("ROTA3"),
        IPV ("IPV"),
        VITAMIN6 ("VITAMIN6"),
        VITAMIN12 ("VITAMIN12"),
        VITAMIN18 ("VITAMIN18"),
        RTSS1 ("RTSS1"),
        RTSS2 ("RTSS2"),
        RTSS3 ("RTSS3"),
        RTSS4 ("RTSS4"),
        MEASLES1 ("MEASLES1"),
        MEASLES2 ("MEASLES2"),
        YF ("YF"),
        MENA ("MENA"),
        ADMIT ("ADMIT"),
        ITN ("ITN");

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
            // Handle all date results using the helper method
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.BCG, binding.bcg);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.OPV0, binding.opv0);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.OPV1, binding.opv1);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.OPV2, binding.opv2);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.OPV3, binding.opv3);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.DEPT1, binding.DPTHepBHib1);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.DEPT2, binding.DPTHepBHib2);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.DEPT3, binding.DPTHepBHib3);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.PNEUMO1, binding.pneumo1);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.PNEUMO2, binding.pneumo2);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.PNEUMO3, binding.pneumo3);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.ROTA1, binding.rota1);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.ROTA2, binding.rota2);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.ROTA3, binding.rota3);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.IPV, binding.ipv);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.VITAMIN6, binding.vitaminA6);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.VITAMIN12, binding.vitaminA12);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.VITAMIN18, binding.vitaminA18);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.RTSS1, binding.rtss6);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.RTSS2, binding.rtss7);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.RTSS4, binding.rtss9);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.RTSS3, binding.rtss18);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.MEASLES1, binding.measles1);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.MEASLES2, binding.measles2);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.YF, binding.yellowFever);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.MENA, binding.menA);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.ITN, binding.itn);
            handleDateResult(bundle, VaccinationViewFragment.DATE_BUNDLES.ADMIT, binding.admitDate);
        });

        // Set up click listeners for all date pickers using helper method
        binding.btnBcg.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.BCG, binding.bcg));

        binding.btnOpv0.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.OPV0, binding.opv0));

        binding.btnOpv1.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.OPV1, binding.opv1));

        binding.btnOpv2.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.OPV2, binding.opv2));

        binding.btnOpv3.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.OPV3, binding.opv3));

        binding.btnDPTHepBHib1.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.DEPT1, binding.DPTHepBHib1));

        binding.btnDPTHepBHib2.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.DEPT2, binding.DPTHepBHib2));

        binding.btnDPTHepBHib3.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.DEPT3, binding.DPTHepBHib3));

        binding.btnPneumo1.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.PNEUMO1, binding.pneumo1));

        binding.btnPneumo2.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.PNEUMO2, binding.pneumo2));

        binding.btnPneumo3.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.PNEUMO3, binding.pneumo3));

        binding.btnRota1.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.ROTA1, binding.rota1));

        binding.btnRota2.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.ROTA2, binding.rota2));

        binding.btnRota3.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.ROTA3, binding.rota3));

        binding.btnIpv.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.IPV, binding.ipv));

        binding.btnVitaminA6.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.VITAMIN6, binding.vitaminA6));

        binding.btnVitaminA12.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.VITAMIN12, binding.vitaminA12));

        binding.btnVitaminA18.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.VITAMIN18, binding.vitaminA18));

        binding.btnRtss6.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.RTSS1, binding.rtss6));

        binding.btnRtss7.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.RTSS2, binding.rtss7));

        binding.btnRtss9.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.RTSS4, binding.rtss9));

        binding.btnRtss18.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.RTSS3, binding.rtss18));

        binding.btnMeasles1.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.MEASLES1, binding.measles1));

        binding.btnMeasles2.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.MEASLES2, binding.measles2));

        binding.btnYellow.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.YF, binding.yellowFever));

        binding.btnMenA.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.MENA, binding.menA));

        binding.btnItn.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.ITN, binding.itn));

        binding.btnAdmitDate.setEndIconOnClickListener(v ->
                showDatePicker(VaccinationViewFragment.DATE_BUNDLES.ADMIT, binding.admitDate));
    }

    private void handleDateResult(Bundle bundle, VaccinationViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(VaccinationViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
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