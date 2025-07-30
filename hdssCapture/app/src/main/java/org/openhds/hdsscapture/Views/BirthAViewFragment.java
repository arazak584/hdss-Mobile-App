package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.OutcomeFragment.BirthBFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthSFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentBirthABinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BirthAViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthAViewFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentBirthABinding binding;
    private OutcomeRepository repository;
    private Outcome outcome;

    public BirthAViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BirthAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BirthAViewFragment newInstance(String uuid) {
        BirthAViewFragment fragment = new BirthAViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBirthABinding.inflate(inflater, container, false);

        PregnancyoutcomeViewModel viewModels = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel viewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        viewModel.getView(outcome.preg_uuid).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setPregoutcome1(data);

                binding.out1Type.setEnabled(false);
                binding.individual1FirstName.setEnabled(false);
                binding.individual1LastName.setEnabled(false);
                binding.gender.setEnabled(false);
                binding.rltnHead.setEnabled(false);


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

    private void save(boolean save, boolean close, OutcomeViewModel outcomeViewModel) {

        if (save) {
            Outcome data = binding.getPregoutcome1();

            Pregnancyoutcome finalData = binding.getPregoutcome();

            final Intent j = getActivity().getIntent();
            final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            int count = 0; // Initialize a count variable to keep track of the number of occurrences where 'type' is 1

            if (binding.getPregoutcome1().type !=null && binding.getPregoutcome1().type == 1) {
                count++;
            }

            if (binding.getPregoutcome().numberofBirths != null) {

                if (finalData.numberofBirths >= 1) {
                    hasErrors = hasErrors || new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 6.0) {
                            weight = true;
                            binding.weigHcard.setError("Child Weight Cannot be More than 6.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 6.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
                    List<Configsettings> configsettings = null;
                    try {
                        configsettings = viewModel.findAll();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    int mis = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).mis : 0;  // e.g., 19
                    int stb = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).stb : 0;  // e.g., 20

                    if (finalData.outcomeDate != null && finalData.conceptionDate != null) {
                        Date outcomeDate = finalData.outcomeDate;
                        Date conceptionDate = finalData.conceptionDate;

                        long diffInMillis = outcomeDate.getTime() - conceptionDate.getTime();
                        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                        int totalWeeks = (int) (diffInDays / 7);

                        // Miscarriage or Abortion allowed only if weeks <= mis
                        if ((inf.type == 3 || inf.type == 4) && totalWeeks > mis) {
                            Toast.makeText(getActivity(), "Miscarriage or Abortion is not allowed beyond " + mis + " weeks", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Live Birth or Still Birth allowed only if weeks >= stb
                        if ((inf.type == 1 || inf.type == 2) && totalWeeks < stb) {
                            Toast.makeText(getActivity(), "Live Birth or Still Birth is not allowed before " + stb + " weeks", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    outcomeViewModel.add(inf);



                }

                // Individual Name Validation

            }


            data.complete=1;
            outcomeViewModel.add(data);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }
        Integer lb = binding.getPregoutcome().numberofBirths;

        if (save) {
            Fragment fragment;

            if (lb > 1) {
                fragment = BirthBFragment.newInstance(individual, locations, socialgroup);
            } else {
                fragment = BirthSFragment.newInstance(individual, locations, socialgroup);
            }

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, fragment)
                    .commit();

        } else if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    BirthAViewFragment.newInstance(outcome.preg_uuid)).commit();
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