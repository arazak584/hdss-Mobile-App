package org.openhds.hdsscapture.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.OutcomeFragment.BirthAFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthBFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthCFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthDFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentBirthSBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BirthSViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthSViewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private FragmentBirthSBinding binding;
    private OutcomeRepository repository;
    private Outcome outcome;

    public BirthSViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PregnancyoutcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BirthSViewFragment newInstance(String uuid) {
        BirthSViewFragment fragment = new BirthSViewFragment();
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
        binding = FragmentBirthSBinding.inflate(inflater, container, false);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
        viewModel.getView(outcome.preg_uuid).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setPregoutcome(data);

                if (data.status != null && data.status == 2) {
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                } else {
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }


                outcomeViewModel.getView1(data.uuid).observe(getViewLifecycleOwner(), data1 -> {
                    //Outcome data1 = outcomeViewModel.getView1(data.uuid);
                    if (data1 != null) {
                        binding.setPregoutcome1(data1);

                    }
                });

                outcomeViewModel.getView2(data.uuid).observe(getViewLifecycleOwner(), data2 -> {
                    //Outcome data1 = outcomeViewModel.getView1(data.uuid);
                    if (data2 != null) {
                        binding.setPregoutcome2(data2);

                    }
                });

                outcomeViewModel.getView3(data.uuid).observe(getViewLifecycleOwner(), data3 -> {
                    //Outcome data1 = outcomeViewModel.getView1(data.uuid);
                    if (data3 != null) {
                        binding.setPregoutcome3(data3);

                    }
                });

                outcomeViewModel.getView4(data.uuid).observe(getViewLifecycleOwner(), data4 -> {
                    //Outcome data1 = outcomeViewModel.getView1(data.uuid);
                    if (data4 != null) {
                        binding.setPregoutcome4(data4);

                    }
                });


            }
        });


//        try {
//            final String child_id = "ST-"+ selectedIndividual.extId;
//            Vpm data = vpmViewModel.find(child_id);
//            if (data != null) {
//                binding.setDeath(data);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.firstNb, codeBookViewModel, "complete");
        loadCodeData(binding.id1001, codeBookViewModel, "complete");
        loadCodeData(binding.id1002, codeBookViewModel, "how_lng");
        loadCodeData(binding.id1003, codeBookViewModel, "complete");
        loadCodeData(binding.id1004, codeBookViewModel, "complete");
        loadCodeData(binding.id1005, codeBookViewModel, "feed_chd");
        loadCodeData(binding.individualComplete, codeBookViewModel, "submit");
        loadCodeData(binding.extras, codeBookViewModel, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, vpmViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, vpmViewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel, VpmViewModel vpmViewModel) {

        if (save) {
            Pregnancyoutcome finalData = binding.getPregoutcome();

            final Intent j = getActivity().getIntent();
            final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

            boolean total = false;
            if (finalData.first_nb != null && finalData.first_nb == 2 && !binding.lBirth.getText().toString().trim().isEmpty()) {
                int totalbirth = Integer.parseInt(binding.lBirth.getText().toString().trim());
                if (totalbirth < 1 || totalbirth > 15) {
                    total = true;
                    binding.lBirth.setError("Cannot be less than 1");
                    Toast.makeText(getActivity(), "Previous Live Births Cannot be less than 1 or More than 15", Toast.LENGTH_LONG).show();
                    return;
                }
            }


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            int count = 0; // Initialize a count variable to keep track of the number of occurrences where 'type' is 1

            // Check each Pregoutcome object and increment the count if it's not null and 'type' equals 1
            if (binding.getPregoutcome1() != null && binding.getPregoutcome1().type != null && binding.getPregoutcome1().type == 1) {
                count++;
            }

            if (binding.getPregoutcome2() != null && binding.getPregoutcome2().type != null && binding.getPregoutcome2().type == 1) {
                count++;
            }

            if (binding.getPregoutcome3() != null && binding.getPregoutcome3().type != null && binding.getPregoutcome3().type == 1) {
                count++;
            }

            if (binding.getPregoutcome4() != null && binding.getPregoutcome4().type != null && binding.getPregoutcome4().type == 1) {
                count++;
            }

            // Set finalData.numberOfLiveBirths directly to the count value
            finalData.numberOfLiveBirths = count;

            finalData.complete = 1;
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }

        Integer lb = binding.getPregoutcome().numberofBirths;

        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
        } else {
            Fragment fragment;

            if (lb == 4) {
                fragment = BirthDViewFragment.newInstance(outcome.preg_uuid);
            } else if (lb == 3) {
                fragment = BirthDViewFragment.newInstance(outcome.preg_uuid);
            } else if (lb == 2) {
                fragment = BirthDViewFragment.newInstance(outcome.preg_uuid);
            } else if (lb == 1) {
                fragment = BirthDViewFragment.newInstance(outcome.preg_uuid);
            } else {
                // Handle cases where lb is not 1, 2, 3, or 4 if needed
                fragment = BirthDViewFragment.newInstance(outcome.preg_uuid);
            }

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
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