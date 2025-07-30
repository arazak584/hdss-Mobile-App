package org.openhds.hdsscapture.Views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.HouseholdDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.Repositories.ResidencyRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentMembershipBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidencyViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidencyViewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Residency residency;
    private ResidencyRepository repository;
    private FragmentMembershipBinding binding;
    private ProgressDialog progressDialog;


    public ResidencyViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResidencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidencyViewFragment newInstance(String uuid) {
        ResidencyViewFragment fragment = new ResidencyViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new ResidencyRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.residency = new Residency();  // Initialize placeholder
            this.residency.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMembershipBinding.inflate(inflater, container, false);

        IndividualViewModel ind = new ViewModelProvider(this).get(IndividualViewModel.class);
        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        viewModel.getView(residency.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setResidency(data);

            binding.starttype.setEnabled(false);
            binding.editTextStartDate.setEnabled(false);
            binding.residencyImg.setEnabled(false);
            binding.buttonResidencyStartDate.setEnabled(false);

        });

        loadCodeData(binding.endtype,  "endType");
        loadCodeData(binding.rltnHead,  "rltnhead");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        binding.setEventname(AppConstants.EVENT_RESIDENCY);
        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ResidencyViewModel viewModel) {

        if (save) {
            Residency finalData = binding.getResidency();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }
            finalData.complete = 1;

            viewModel.add(finalData);

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
        ENDDATE ("ENDDATE");

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