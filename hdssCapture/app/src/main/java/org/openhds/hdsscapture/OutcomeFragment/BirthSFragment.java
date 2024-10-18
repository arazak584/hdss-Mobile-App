package org.openhds.hdsscapture.OutcomeFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
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
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentBirthSBinding;
import org.openhds.hdsscapture.databinding.FragmentOutcomeBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.DatePickerFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.SocioFFragment;

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
 * Use the {@link BirthSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthSFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OUTCOME.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentBirthSBinding binding;

    public BirthSFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment PregnancyoutcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BirthSFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        BirthSFragment fragment = new BirthSFragment();
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
        binding = FragmentBirthSBinding.inflate(inflater, container, false);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);


        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
        try {
            Pregnancyoutcome datas = viewModel.findloc(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.compno);
            if (datas != null) {
                binding.setPregoutcome(datas);

                if(datas.status!=null && datas.status==2){
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

                try {
                    final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD1 + 0 + roundData.roundNumber;
                    Outcome data = outcomeViewModel.find(child_id,ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        binding.setPregoutcome1(data);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                //Child2
                try {
                    final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD2 + 0 + roundData.roundNumber;
                    Outcome data = outcomeViewModel.find(child_id,ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        binding.setPregoutcome2(data);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                //Child3
                try {
                    final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD3 + 0 + roundData.roundNumber;
                    Outcome data = outcomeViewModel.find(child_id,ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        binding.setPregoutcome3(data);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                //Child4
                try {
                    final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD4 + 0 + roundData.roundNumber;
                    Outcome data = outcomeViewModel.find(child_id,ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        binding.setPregoutcome4(data);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = "ST-"+ HouseMembersFragment.selectedIndividual.extId;
            Vpm data = vpmViewModel.find(child_id);
            if (data != null) {
                binding.setDeath(data);
            } else {
                data = new Vpm();

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dts = visitViewModel.find(socialgroup.uuid);
                if (dts != null){
                    data.visit_uuid = dts.uuid;
                }

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = "ST-"+ HouseMembersFragment.selectedIndividual.getExtId();
                data.insertDate = new Date();
                data.firstName = "Still";
                data.lastName = "Birth";
                data.gender = 3;
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.extId = "ST-"+ HouseMembersFragment.selectedIndividual.getExtId();
                data.compname = ClusterFragment.selectedLocation.getLocationName();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.villname = level6Data.getName();
                data.villcode = level6Data.getExtId();
                data.respondent = HouseMembersFragment.selectedIndividual.getFirstName() +" "+ HouseMembersFragment.selectedIndividual.getLastName();
                data.househead = HouseMembersFragment.selectedIndividual.getFirstName() +" "+ HouseMembersFragment.selectedIndividual.getLastName();
                data.deathCause = 77;
                data.complete=1;
                data.deathPlace =1;

                binding.setDeath(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


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

        Handler.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
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
            boolean hasErrors = new Handler().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

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


            //Replicate Same Changes to Pregnancyoutcome1Fragment
            if ((binding.getPregoutcome1() != null && binding.getPregoutcome1().type != null && binding.getPregoutcome1().type == 2) ||
                    (binding.getPregoutcome2() != null && binding.getPregoutcome2().type != null && binding.getPregoutcome2().type == 2) ||
                    (binding.getPregoutcome3() != null && binding.getPregoutcome3().type != null && binding.getPregoutcome3().type == 2) ||
                    (binding.getPregoutcome4() != null && binding.getPregoutcome4().type != null && binding.getPregoutcome4().type == 2)) {

                final Vpm vpm = binding.getDeath();
                vpm.complete = 1;
                vpm.deathDate = binding.getPregoutcome().outcomeDate;
                vpm.dob = binding.getPregoutcome().outcomeDate;
                vpm.deathPlace = 1;
                vpm.extId = "ST-" + HouseMembersFragment.selectedIndividual.getExtId();
                vpmViewModel.add(vpm);

            }


            Date end = new Date(); // Get the current date and time
            // Create a Calendar instance and set it to the current date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            // Extract the hour, minute, and second components
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            // Format the components into a string with leading zeros
            String endtime = String.format("%02d:%02d:%02d", hh, mm, ss);

            if (finalData.sttime != null && finalData.edtime == null) {
                finalData.edtime = endtime;
            }

            finalData.complete = 1;
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }

        Integer lb = binding.getPregoutcome().numberofBirths;

        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, HouseMembersFragment.newInstance(locations, socialgroup, individual))
                    .commit();
        } else {
            Fragment fragment;

            if (lb == 4) {
                fragment = BirthDFragment.newInstance(individual, locations, socialgroup);
            } else if (lb == 3) {
                fragment = BirthCFragment.newInstance(individual, locations, socialgroup);
            } else if (lb == 2) {
                fragment = BirthBFragment.newInstance(individual, locations, socialgroup);
            } else if (lb == 1) {
                fragment = BirthAFragment.newInstance(individual, locations, socialgroup);
            } else {
                // Handle cases where lb is not 1, 2, 3, or 4 if needed
                fragment = BirthDFragment.newInstance(individual, locations, socialgroup);
            }

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, fragment)
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