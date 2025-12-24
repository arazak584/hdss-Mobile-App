package org.openhds.hdsscapture.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutcomeBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.KeyboardFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutcomeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutcomeViewFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    public static final String OUTCOME_NUMBER = "OUTCOME_NUMBER";
    public static final String OUTCOME = "OUTCOME";
    private FragmentOutcomeBinding binding;
    private OutcomeRepository repository;
    private Individual individual;
    private Outcome outcome;
    private int currentOutcomeNumber = 1;
    private int totalBirths = 0;
    private Pregnancyoutcome pregnancyoutcome;
    private IndividualViewModel individualViewModel;
    private ResidencyViewModel residencyViewModel;
    private OutcomeViewModel viewModel;

    public OutcomeViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param outcomeNumber parameter 1.
     * @param pregnancyoutcome parameter 2.
     * @return A new instance of fragment BirthAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutcomeViewFragment newInstance(String uuid,Pregnancyoutcome pregnancyoutcome,int outcomeNumber) {
        OutcomeViewFragment fragment = new OutcomeViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        args.putParcelable(OUTCOME, pregnancyoutcome);
        args.putInt(OUTCOME_NUMBER, outcomeNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new OutcomeRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.outcome = new Outcome();  // Initialize placeholder
            this.outcome.preg_uuid = uuid;        // Assign UUID to fetch from DB
            pregnancyoutcome = getArguments().getParcelable(OUTCOME);
            currentOutcomeNumber = getArguments().getInt(OUTCOME_NUMBER, 1);
        }

        // Get total births from pregnancy outcome
        if (pregnancyoutcome != null && pregnancyoutcome.numberofBirths != null) {
            try {
                totalBirths = pregnancyoutcome.numberofBirths;
            } catch (NumberFormatException e) {
                totalBirths = 1;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOutcomeBinding.inflate(inflater, container, false);
        binding.setPregnancyoutcome(pregnancyoutcome);
        binding.setIndividual(individual);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        PregnancyoutcomeViewModel viewModels = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        viewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);

        // Set outcome number dropdown
        setupOutcomeNumberDropdown();

        viewModel.getView(outcome.preg_uuid, currentOutcomeNumber).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setOutcome(data);

                binding.out1Type.setEnabled(false);
                binding.individual1FirstName.setEnabled(false);
                binding.individual1LastName.setEnabled(false);
                binding.gender.setEnabled(false);
                binding.rltnHead.setEnabled(false);

                // Check if baby individual already exists
                //Individual baby = null;
                if (data.childuuid != null) {
                    individualViewModel.getView(data.childuuid)
                            .observe(getViewLifecycleOwner(), baby -> {
                                if (baby != null) {
                                    binding.setIndividual(baby);
                                }
                            });
                    Log.d("Outcome", "Child exists for outcome " + currentOutcomeNumber);
                }

                //Check for Residency
                if (data.childuuid != null) {
                    residencyViewModel.getViews(data.childuuid)
                            .observe(getViewLifecycleOwner(), residency -> {
                                if (residency != null) {
                                    binding.setResidency(residency);
                                }
                            });
                }


            }
        });


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.out1Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.gender, codeBookViewModel, "gender");
        loadCodeData(binding.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.chdSize, codeBookViewModel, "size");

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

    /**
     * Setup dropdown to show which outcome number is being entered
     */
    private void setupOutcomeNumberDropdown() {
        AutoCompleteTextView dropdown = binding.getRoot().findViewById(R.id.outcomeNumberDropdown);
        if (dropdown != null) {
            String displayText = "Outcome " + currentOutcomeNumber + " of " + totalBirths;
            dropdown.setText(displayText);
            dropdown.setEnabled(false);
        }
    }

    private void save(boolean save, boolean close, OutcomeViewModel outcomeViewModel) {

        if (save) {
            Outcome data = binding.getOutcome();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            data.outcomeNumber = currentOutcomeNumber;

            if (binding.getOutcome().type != null) {

                if (data.type != null) {
                    hasErrors = hasErrors || new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

                    boolean weight = false;
                    if (data.chd_weight != null && data.chd_weight == 1 && !binding.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 6.0) {
                            weight = true;
                            binding.weigHcard.setError("Child Weight Cannot be More than 6.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 6.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }



                }
            }

            data.complete = 1;

            viewModel.add(data);

        }

        if (save) {
            // Check if there are more outcomes to enter
            if (currentOutcomeNumber < totalBirths) {
                // Navigate to next outcome
                int nextOutcomeNumber = currentOutcomeNumber + 1;
                Toast.makeText(requireContext(), "Please enter outcome " + nextOutcomeNumber + " of " + totalBirths, Toast.LENGTH_LONG).show();

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        OutcomeViewFragment.newInstance(outcome.preg_uuid,pregnancyoutcome, nextOutcomeNumber)).commit();
            } else {
                // All outcomes entered, go back to household members
                Toast.makeText(requireContext(), "All " + totalBirths + " outcomes saved successfully", Toast.LENGTH_LONG).show();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        ViewFragment.newInstance()).commit();
            }
        } else {
            // Cancel - go back to pregnancy outcome
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    PregnancyOutcomeViewFragment.newInstance(pregnancyoutcome.uuid)).commit();
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


}