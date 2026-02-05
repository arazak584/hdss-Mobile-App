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
import org.openhds.hdsscapture.Repositories.DuplicateRepository;
import org.openhds.hdsscapture.Repositories.MorbidityRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.databinding.FragmentDupBinding;
import org.openhds.hdsscapture.databinding.FragmentMorbidityBinding;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.KeyboardFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DuplicateViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DuplicateViewFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Duplicate duplicate;
    private FragmentDupBinding binding;
    private DuplicateRepository repository;

    public DuplicateViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MorbidityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DuplicateViewFragment newInstance(String uuid) {
        DuplicateViewFragment fragment = new DuplicateViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new DuplicateRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.duplicate = new Duplicate();  // Initialize placeholder
            this.duplicate.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDupBinding.inflate(inflater, container, false);
        binding.setDup(duplicate);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);


        DuplicateViewModel viewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
        viewModel.getView(duplicate.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setDup(data);

            binding.buttonOriginal.setEnabled(false);
            binding.dupOne.setEnabled(false);
            binding.dupTwo.setEnabled(false);
            binding.dupThree.setEnabled(false);
            binding.buttonDup.setEnabled(false);
            binding.button1Dup.setEnabled(false);
            binding.button2Dup.setEnabled(false);

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

        loadCodeData(binding.relComplete,  "submit");

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

    private void save(boolean save, boolean close, DuplicateViewModel viewModel) {

        if (save) {
            final Duplicate finalData = binding.getDup();

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            finalData.complete_n = finalData.getComplete();

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