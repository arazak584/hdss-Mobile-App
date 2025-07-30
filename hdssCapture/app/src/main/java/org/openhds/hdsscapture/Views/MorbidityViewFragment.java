package org.openhds.hdsscapture.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.MorbidityRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.databinding.FragmentMorbidityBinding;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MorbidityViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MorbidityViewFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Morbidity morbidity;
    private FragmentMorbidityBinding binding;
    private MorbidityRepository repository;

    public MorbidityViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MorbidityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MorbidityViewFragment newInstance(String uuid) {
        MorbidityViewFragment fragment = new MorbidityViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new MorbidityRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.morbidity = new Morbidity();  // Initialize placeholder
            this.morbidity.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_morbidity, container, false);
        binding = FragmentMorbidityBinding.inflate(inflater, container, false);
        binding.setMorbidity(morbidity);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);


        MorbidityViewModel viewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);
        viewModel.getView(morbidity.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setMorbidity(data);

            if(data.status!=null && data.status==2){
                cmt.setVisibility(View.VISIBLE);
                rsv.setVisibility(View.VISIBLE);
                rsvd.setVisibility(View.VISIBLE);
            }else{
                cmt.setVisibility(View.GONE);
                rsv.setVisibility(View.GONE);
                rsvd.setVisibility(View.GONE);
            }

        });

        loadCodeData(binding.feverTreat, "submit");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, MorbidityViewModel viewModel) {

        if (save) {
            final Morbidity finalData = binding.getMorbidity();

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            boolean feverdays = false;
            if (!binding.feverDays.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.feverDays.getText().toString().trim());
                if (totals <0 || totals>14) {
                    feverdays = true;
                    binding.feverDays.setError("Should Be Between 0 to 14 days");
                    //Toast.makeText(getActivity(), "Should Be Between 0 to 14 days", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean hyp = false;
            boolean dia = false;
            boolean hea = false;
            boolean str = false;
            boolean sic = false;
            boolean ast = false;
            boolean epi = false;

            if (!binding.hypertensionDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.hypertensionDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    hyp = true;
                    binding.hypertensionDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.diabetesDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.diabetesDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    dia = true;
                    binding.diabetesDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.heartDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.heartDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    hea = true;
                    binding.heartDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.strokeDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.strokeDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    str = true;
                    binding.strokeDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.sickleDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.sickleDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    sic = true;
                    binding.sickleDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.asthmaDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.asthmaDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    ast = true;
                    binding.asthmaDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.epilepsyDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.epilepsyDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    epi = true;
                    binding.epilepsyDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            finalData.complete=1;

            viewModel.add(finalData);
        }

        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
        }
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
}