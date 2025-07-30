package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.Repositories.VaccinationRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.databinding.FragmentVaccinationBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
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
 * Use the {@link VaccinationViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaccinationViewFragment extends Fragment {

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
     * @return A new instance of fragment VaccinationFragment.
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

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((DATE_BUNDLES.BCG.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.BCG.getBundleKey());
                binding.bcg.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.OPV0.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.OPV0.getBundleKey());
                binding.opv0.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.OPV1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.OPV1.getBundleKey());
                binding.opv1.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.OPV2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.OPV2.getBundleKey());
                binding.opv2.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.OPV3.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.OPV3.getBundleKey());
                binding.opv3.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DEPT1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DEPT1.getBundleKey());
                binding.DPTHepBHib1.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DEPT2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DEPT2.getBundleKey());
                binding.DPTHepBHib2.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DEPT3.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DEPT3.getBundleKey());
                binding.DPTHepBHib3.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.PNEUMO1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.PNEUMO1.getBundleKey());
                binding.pneumo1.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.PNEUMO2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.PNEUMO2.getBundleKey());
                binding.pneumo2.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.PNEUMO3.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.PNEUMO3.getBundleKey());
                binding.pneumo3.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ROTA1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ROTA1.getBundleKey());
                binding.rota1.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ROTA2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ROTA2.getBundleKey());
                binding.rota2.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ROTA3.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ROTA3.getBundleKey());
                binding.rota3.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.IPV.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.IPV.getBundleKey());
                binding.ipv.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.VITAMIN6.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.VITAMIN6.getBundleKey());
                binding.vitaminA6.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.VITAMIN12.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.VITAMIN12.getBundleKey());
                binding.vitaminA12.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.VITAMIN18.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.VITAMIN18.getBundleKey());
                binding.vitaminA18.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.RTSS1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RTSS1.getBundleKey());
                binding.rtss6.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.RTSS2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RTSS2.getBundleKey());
                binding.rtss7.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.RTSS4.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RTSS4.getBundleKey());
                binding.rtss9.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.RTSS3.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RTSS3.getBundleKey());
                binding.rtss18.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.MEASLES1.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.MEASLES1.getBundleKey());
                binding.measles1.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.MEASLES2.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.MEASLES2.getBundleKey());
                binding.measles2.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.YF.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.YF.getBundleKey());
                binding.yellowFever.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.MENA.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.MENA.getBundleKey());
                binding.menA.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ITN.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ITN.getBundleKey());
                binding.itn.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ADMIT.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ADMIT.getBundleKey());
                binding.admitDate.setText(result);
            }
        });

        binding.btnBcg.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.BCG.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnOpv0.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.OPV0.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnOpv1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.OPV1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnOpv2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.OPV2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnOpv3.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.OPV3.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnDPTHepBHib1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DEPT1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnDPTHepBHib2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DEPT2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnDPTHepBHib3.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DEPT3.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnPneumo1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.PNEUMO1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnPneumo2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.PNEUMO2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnPneumo3.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.PNEUMO3.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRota1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ROTA1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRota2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ROTA2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRota3.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ROTA3.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnIpv.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.IPV.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnVitaminA6.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.VITAMIN6.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnVitaminA12.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.VITAMIN12.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnVitaminA18.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.VITAMIN18.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRtss6.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RTSS1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRtss7.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RTSS2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRtss9.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RTSS4.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnRtss18.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RTSS3.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnMeasles1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.MEASLES1.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnMeasles2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.MEASLES2.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnYellow.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.YF.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnMenA.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.MENA.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnItn.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ITN.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.btnAdmitDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ADMIT.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

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
        loadCodeData(binding.vacComplete, "submit");
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
}