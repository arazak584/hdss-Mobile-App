package org.openhds.hdsscapture.fragment;

import android.content.Intent;
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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Utilities.UniqueUUIDGenerator;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutcomeBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutcomeFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OUTCOME.TAG";
    public static final String OUTCOME_NUMBER = "OUTCOME_NUMBER";
    public static final String OUTCOME = "OUTCOME";
    public static final String PREGNANCY = "PREGNANCY";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentOutcomeBinding binding;
    private Locations selectedLocation;
    private Individual selectedIndividual;
    private Pregnancy pregnancy;
    private int currentOutcomeNumber = 1;
    private int totalBirths = 0;
    private IndividualViewModel individualViewModel;
    private ResidencyViewModel residencyViewModel;
    private OutcomeViewModel viewModel;
    private Pregnancyoutcome pregnancyoutcome;
    private Hierarchy level6Data;

    public OutcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 2.
     * @param individual Parameter 3.
     * @param pregnancyoutcome parameter 4.
     * @param pregnancy parameter 5.
     * @param outcomeNumber parameter 6.
     * @return A new instance of fragment BirthAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutcomeFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup,Pregnancyoutcome pregnancyoutcome,
                                              Pregnancy pregnancy, int outcomeNumber) {
        OutcomeFragment fragment = new OutcomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(OUTCOME, pregnancyoutcome);
        args.putParcelable(PREGNANCY, pregnancy);
        args.putInt(OUTCOME_NUMBER, outcomeNumber);
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
            pregnancyoutcome = getArguments().getParcelable(OUTCOME);
            pregnancy = getArguments().getParcelable(PREGNANCY);
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
        //binding.setResidency(re);

        final Intent j = getActivity().getIntent();
        level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        viewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);

        // Set outcome number dropdown
        setupOutcomeNumberDropdown();

        try {
            Outcome data = viewModel.getByOutcomeIdAndNumber(pregnancyoutcome.uuid, currentOutcomeNumber);
            if (data != null) {
                binding.setOutcome(data);

                // Check if baby individual already exists
                Individual baby = null;
                if (data.childuuid != null) {
                    baby = individualViewModel.find(data.childuuid);
                    Log.d("Outcome", "Child exists for outcome " + currentOutcomeNumber);
                }

                if (baby != null) {
                    binding.setIndividual(baby);
                    baby.dob = pregnancyoutcome.outcomeDate;
                    baby.hohID = socialgroup.extId;
                    baby.compno = selectedLocation.compno;
                    baby.village = level6Data.getName();
                    Log.d("Outcome", "Loaded existing child");
                } else {
                    Log.d("Outcome", "Creating new child for outcome " + currentOutcomeNumber);
                    baby = createNewBaby();
                    binding.setIndividual(baby);
                    binding.getIndividual().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                }

                //Check for Residency
                Residency babys = null;
                if (data.childuuid != null) {
                    babys = residencyViewModel.getUuid(data.childuuid);
                    Log.d("Outcome", "Child exists for outcome " + currentOutcomeNumber);
                }

                if (babys != null) {
                    binding.setResidency(babys);
                    babys.startDate = pregnancyoutcome.outcomeDate;
                    babys.location_uuid = selectedLocation.uuid;
                    babys.socialgroup_uuid = socialgroup.uuid;
                    Log.d("Outcome", "Loaded existing Residency child");
                } else {
                    Log.d("Outcome", "Creating new residency for outcome " + currentOutcomeNumber);
                    babys = createNewBabyResidency();
                    binding.setResidency(babys);
                    binding.getResidency().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                }



            } else {

                // Create new outcome record
                data = new Outcome();
                Log.d("Outcome", "Creating new outcome " + currentOutcomeNumber);

                String uuid = UUID.randomUUID().toString();
                //data.uuid = uuid.replaceAll("-", "");
                data.uuid = UniqueUUIDGenerator.generate(getContext());
                data.preg_uuid = pregnancyoutcome.uuid;
                data.complete = 1;
                data.outcomeNumber = currentOutcomeNumber;
                data.mother_uuid = selectedIndividual.uuid;
                binding.setOutcome(data);

                // Create new baby
                Individual baby = createNewBaby();
                binding.setIndividual(baby);
                binding.getIndividual().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                // Create new residency for baby
                Residency babys = createNewBabyResidency();
                binding.setResidency(babys);
                binding.getResidency().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.out1Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.gender, codeBookViewModel, "gender");
        loadCodeData(binding.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.chdSize, codeBookViewModel, "size");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel,individualViewModel,residencyViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel,individualViewModel,residencyViewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    /**
     * Create a new baby Individual record
     */
    private Individual createNewBaby() {
        Individual baby = new Individual();
        //baby.uuid = UUID.randomUUID().toString().replace("-", "");
        baby.uuid = UniqueUUIDGenerator.generate(getContext());
        baby.fw_uuid = pregnancyoutcome.getFw_uuid();
        baby.complete = 1;
        baby.dob = pregnancyoutcome.outcomeDate;
        baby.mother_uuid = pregnancyoutcome.mother_uuid;
        baby.father_uuid = pregnancyoutcome.father_uuid;
        baby.deleted = 0;
        baby.dobAspect = 1;
        baby.endType = 1;
        baby.hohID = socialgroup.extId;
        baby.compno = selectedLocation.compno;
        baby.village = level6Data.getName();
        baby.extId = UniqueIDGen.generateUniqueId(individualViewModel, selectedLocation.compextId);
        Log.d("Outcome", "Child dob: " + pregnancyoutcome.outcomeDate);
        return baby;
    }

    /**
     * Create a new baby residency record
     */
    private Residency createNewBabyResidency() {
        Residency baby = new Residency();
        //baby.uuid = UUID.randomUUID().toString().replace("-", "");
        baby.uuid = UniqueUUIDGenerator.generate(getContext());
        baby.fw_uuid = pregnancyoutcome.getFw_uuid();
        baby.complete = 1;
        baby.startDate = pregnancyoutcome.outcomeDate;
        baby.location_uuid = selectedLocation.uuid;
        baby.socialgroup_uuid = socialgroup.uuid;
        Log.d("Outcome", "Child dob: " + pregnancyoutcome.outcomeDate);
        return baby;
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

    private void save(boolean save, boolean close, OutcomeViewModel viewModel, IndividualViewModel individualViewModel, ResidencyViewModel residencyViewModel) {

        if (save) {
            Outcome data = binding.getOutcome();
            Individual individual = binding.getIndividual();
            Residency residency = binding.getResidency();

            final Intent j = getActivity().getIntent();
            final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

            final boolean validateOnComplete = true;
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

                    ConfigViewModel cviewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
                    List<Configsettings> configsettings = null;
                    try {
                        configsettings = cviewModel.findAll();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    int mis = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).mis : 0;
                    int stb = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).stb : 0;

                    if (binding.getPregnancyoutcome().gestationWks != null) {

                        int tweeks = binding.getPregnancyoutcome().gestationWks;

                        // Miscarriage or Abortion allowed only if weeks <= mis
                        if ((data.type == 3 || data.type == 4) && tweeks > mis) {
                            Toast.makeText(getActivity(), "Miscarriage or Abortion is not allowed beyond " + mis + " weeks", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Live Birth or Still Birth allowed only if weeks >= stb
                        if ((data.type == 1 || data.type == 2) && tweeks < stb) {
                            Toast.makeText(getActivity(), "Live Birth or Still Birth is not allowed before " + stb + " weeks", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    // FIXED LOGIC: Handle individual and residency based on outcome type
                    if (binding.getOutcome().type == 1) {
                        // Type 1 = Live Birth: Save individual and residency
                        residency.individual_uuid = individual.uuid;
                        data.childuuid = individual.uuid;
                        residency.endType = 1;
                        residency.startType = 2;
                        individual.endType = 1;

                        // Validate names before saving (only for live births)
                        String firstName = binding.individual1FirstName.getText().toString();
                        String lastName = binding.individual1LastName.getText().toString();

                        if (!firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
                            boolean hasError = false;

                            // Validate First Name
                            if (firstName.trim().length() != firstName.length()) {
                                binding.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                                hasError = true;
                            } else if (!firstName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                                binding.individual1FirstName.setError("Only letters, hyphens, and single spaces are allowed in the Name");
                                Toast.makeText(getContext(), "Only letters, hyphens, and single spaces are allowed in the Name", Toast.LENGTH_LONG).show();
                                hasError = true;
                            } else {
                                binding.individual1FirstName.setError(null);
                            }

                            // Validate Last Name
                            if (lastName.trim().length() != lastName.length()) {
                                binding.individual1LastName.setError("Spaces are not allowed before or after the Name");
                                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                                hasError = true;
                            } else if (!lastName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                                binding.individual1LastName.setError("Only letters, hyphens, and single spaces are allowed in the Name");
                                Toast.makeText(getContext(), "Only letters, hyphens, and single spaces are allowed in the Name", Toast.LENGTH_LONG).show();
                                hasError = true;
                            } else {
                                binding.individual1LastName.setError(null);
                            }

                            if (hasError) {
                                return;
                            }
                        }

                        // Save individual and residency for live birth
                        individualViewModel.add(individual);
                        residencyViewModel.add(residency);
                        Log.d("OutcomeSave", "Live birth: Saved individual " + individual.uuid + " and residency");

                    } else {
                        // Outcome is NOT live birth (stillbirth, miscarriage, abortion, etc.)
                        // Check if individual record exists from previous live birth entry
                        if (individual.uuid != null && !individual.uuid.isEmpty()) {
                            try {
                                Individual existingIndividual = individualViewModel.find(individual.uuid);
                                if (existingIndividual != null) {
                                    // Individual exists - mark as deleted
                                    existingIndividual.deleted = 1;
                                    existingIndividual.deletedDate = new Date();
                                    existingIndividual.complete = 1;
                                    individualViewModel.add(existingIndividual);
                                    Log.d("OutcomeSave", "Outcome changed from livebirth: Marking individual " +
                                            existingIndividual.uuid + " as deleted");
                                    Toast.makeText(requireContext(),
                                            "Individual record marked as deleted due to outcome change",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                                Log.e("OutcomeSave", "Error checking for existing individual", e);
                            }

                            // Also check and mark residency as deleted if it exists
                            try {
                                // Assuming you have a method to find residency by individual UUID
                                // This part may need adjustment based on your ResidencyViewModel implementation
                                // List<Residency> existingResidencies = residencyViewModel.findByIndividualUuid(individual.uuid);
                                // if (existingResidencies != null && !existingResidencies.isEmpty()) {
                                //     for (Residency existingResidency : existingResidencies) {
                                //         existingResidency.deleted = 1;
                                //         existingResidency.deletedDate = new Date();
                                //         residencyViewModel.add(existingResidency);
                                //     }
                                //     Log.d("OutcomeSave", "Marked residency records as deleted");
                                // }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("OutcomeSave", "Error checking for existing residency", e);
                            }
                        }

                        // Clear individual ID from outcome since it's not a live birth
                        data.childuuid = null;
                        Log.d("OutcomeSave", "Not a livebirth: Individual ID cleared from outcome");
                    }

                    // Handle VPM (Verbal Post Mortem) records for stillbirths
                    VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);

                    if (binding.getOutcome().type == 2) {
                        // Type 2 = Stillbirth: Create or update VPM record
                        String vpmUuid = "ST-" + selectedIndividual.getExtId();

                        try {
                            // Check if VPM record already exists
                            Vpm existingVpm = vpmViewModel.finds(vpmUuid);

                            if (existingVpm != null) {
                                // VPM exists, just update it
                                existingVpm.complete = 1;
                                existingVpm.insertDate = new Date();
                                existingVpm.deathDate = pregnancyoutcome.outcomeDate;
                                existingVpm.dob = pregnancyoutcome.outcomeDate;
                                vpmViewModel.add(existingVpm);
                                Log.d("OutcomeSave", "Updated existing VPM record: " + vpmUuid);
                            } else {
                                // Create new VPM record for stillbirth
                                Vpm vpm = new Vpm();
                                vpm.fw_uuid = individual.fw_uuid;
                                vpm.uuid = vpmUuid;
                                vpm.insertDate = new Date();
                                vpm.firstName = "Still";
                                vpm.lastName = "Birth";
                                vpm.gender = 3;
                                vpm.compno = selectedLocation.getCompno();
                                vpm.extId = vpmUuid;
                                vpm.compname = selectedLocation.getLocationName();
                                vpm.individual_uuid = selectedIndividual.getUuid();
                                vpm.villname = level6Data.getName();
                                vpm.villcode = level6Data.getExtId();
                                vpm.respondent = selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName();
                                vpm.househead = selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName();
                                vpm.deathCause = 77;
                                vpm.deathPlace = 1;
                                vpm.complete = 1;
                                vpm.deathDate = pregnancyoutcome.outcomeDate;
                                vpm.dob = pregnancyoutcome.outcomeDate;

                                vpmViewModel.add(vpm);
                                Log.d("OutcomeSave", "Created new VPM record for stillbirth: " + vpmUuid + " " + pregnancyoutcome.outcomeDate);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            Log.e("OutcomeSave", "Error handling VPM record", e);
                        }

                    } else {
                        // Not a stillbirth: Check if VPM record exists from previous entry
                        String vpmUuid = "ST-" + selectedIndividual.getExtId();

                        try {
                            Vpm existingVpm = vpmViewModel.finds(vpmUuid);

                            if (existingVpm != null) {
                                // VPM exists from previous stillbirth entry
                                // Mark as incomplete since outcome type changed
                                existingVpm.complete = 2;
                                existingVpm.insertDate = new Date();
                                vpmViewModel.add(existingVpm);
                                Log.d("OutcomeSave", "Outcome changed from stillbirth: Marked VPM as incomplete (complete=2): " + vpmUuid);
                                Toast.makeText(requireContext(),
                                        "VPM record marked as incomplete due to outcome change",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            Log.e("OutcomeSave", "Error checking for existing VPM record", e);
                        }
                    }
                }
            }

            data.mother_uuid = selectedIndividual.getUuid();
            data.complete = 1;

            viewModel.add(data);
        }

        if (save) {
            // Check if there are more outcomes to enter
            if (currentOutcomeNumber < totalBirths) {
                // Navigate to next outcome
                int nextOutcomeNumber = currentOutcomeNumber + 1;
                Toast.makeText(requireContext(), "Please enter outcome " + nextOutcomeNumber + " of " + totalBirths, Toast.LENGTH_LONG).show();

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                        OutcomeFragment.newInstance(individual, locations, socialgroup, pregnancyoutcome, pregnancy, nextOutcomeNumber)).commit();
            } else {
                // All outcomes entered, go back to household members
                Toast.makeText(requireContext(), "All " + totalBirths + " outcomes saved successfully", Toast.LENGTH_LONG).show();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                        HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
            }
        } else {
            // Cancel - go back to pregnancy outcome
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyOutcomeFragment.newInstance(individual, locations, socialgroup, pregnancy)).commit();
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