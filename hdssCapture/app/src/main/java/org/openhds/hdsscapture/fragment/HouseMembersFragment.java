package org.openhds.hdsscapture.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Dialog.MinorDialogFragment;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.OutcomeFragment.Birth3Fragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthExtraFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.*;
import org.openhds.hdsscapture.entity.*;
import org.openhds.hdsscapture.databinding.FragmentHouseMembersBinding;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HouseMembersFragment extends Fragment implements IndividualViewAdapter.IndividualClickListener {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    // Core data
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private Locations currentLocation;
    private Hierarchy level6Data;

    // UI Components - initialized lazily
    private View view;
    private AppCompatButton finish;
    private IndividualViewAdapter adapter;
    private RecyclerView recyclerView;

    // ViewModels - cached
    private IndividualViewModel individualViewModel;
    private IndividualSharedViewModel individualSharedViewModel;
    private VisitViewModel visitViewModel;
    private RegistryViewModel registryViewModel;
    private DeathViewModel deathViewModel;
    private SocialgroupViewModel viewModel;

    // Background execution
    private ExecutorService backgroundExecutor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Cache for config settings
    private int hohAge = 0;
    private int motherAge = 0;
    private int fatherAge = 0;

    public HouseMembersFragment() {
        // Required empty public constructor
    }

    public static HouseMembersFragment newInstance(Locations locations, Socialgroup socialgroup, Individual individual) {
        HouseMembersFragment fragment = new HouseMembersFragment();
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

        // Extract arguments
        if (getArguments() != null) {
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }

        // Initialize shared resources once
        individualSharedViewModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        backgroundExecutor = Executors.newSingleThreadExecutor();

        // Load config settings once in background
        loadConfigSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_house_members, container, false);

        // Initialize ViewModels
        initializeViewModels();

        // Get current location
        ClusterSharedViewModel clusterSharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        currentLocation = clusterSharedViewModel.getCurrentSelectedLocation();

        // Setup UI
        setupRecyclerView();
        setupButtons();
        setupHouseholdInfo();

        // Observe data after view is created
        view.post(this::observeData);

        // Background checks
        performBackgroundChecks();

        return view;
    }

    private void initializeViewModels() {
        // Initialize only once and cache
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
    }

    private void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerView_household);
        adapter = new IndividualViewAdapter(this, locations, socialgroup, this, individualSharedViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));
    }

    private void setupButtons() {
        finish = view.findViewById(R.id.button_cpvisit);
        updateButtonState();

        // Search button
        view.findViewById(R.id.search).setOnClickListener(v ->
                SearchFragment.newInstance(locations, socialgroup)
                        .show(getChildFragmentManager(), "SearchFragment")
        );

        // Visit button
        view.findViewById(R.id.button_visit).setOnClickListener(v ->
                VisitFragment.newInstance(individual, locations, socialgroup)
                        .show(getChildFragmentManager(), "VisitFragment")
        );

        // Event menu
        view.findViewById(R.id.menu).setOnClickListener(this::showEventMenu);

        // Setup all navigation buttons
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        // Pregnancy buttons
        view.findViewById(R.id.pregnancy).setOnClickListener(v ->
                navigateToFragment(PregnancyFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.pregnancy2).setOnClickListener(v ->
                navigateToFragment(PregnancyExtraFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.pregnancy3).setOnClickListener(v ->
                navigateToFragment(Pregnancy3Fragment.newInstance(individual, locations, socialgroup))
        );

        // Outcome buttons
        view.findViewById(R.id.outcome).setOnClickListener(v ->
                navigateToFragment(BirthFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.outcome2).setOnClickListener(v ->
                navigateToFragment(BirthExtraFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.outcome3).setOnClickListener(v ->
                navigateToFragment(Birth3Fragment.newInstance(individual, locations, socialgroup))
        );

        // Other navigation buttons
        view.findViewById(R.id.dup).setOnClickListener(v ->
                navigateToFragment(DupFragment.newInstance(individual, null, locations, socialgroup))
        );
        view.findViewById(R.id.demographic).setOnClickListener(v ->
                navigateToFragment(DemographicFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.registry).setOnClickListener(v ->
                navigateToFragment(RegisterFragment.newInstance(locations, socialgroup, individual))
        );
        view.findViewById(R.id.death).setOnClickListener(v ->
                navigateToFragment(DeathFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.omg).setOnClickListener(v ->
                navigateToFragment(OutmigrationFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.amend).setOnClickListener(v ->
                navigateToFragment(AmendmentFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.rel).setOnClickListener(v ->
                navigateToFragment(RelationshipFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.hoh).setOnClickListener(v ->
                navigateToFragment(SocialgroupFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.img).setOnClickListener(v ->
                navigateToFragment(InmigrationFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.ses).setOnClickListener(v ->
                navigateToFragment(SocioAFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.morbidity).setOnClickListener(v ->
                navigateToFragment(MorbidityFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.vac).setOnClickListener(v ->
                navigateToFragment(VaccinationFragment.newInstance(individual, locations, socialgroup))
        );
        view.findViewById(R.id.relhoh).setOnClickListener(v ->
                navigateToFragment(ResidencyFragment.newInstance(individual, locations, socialgroup))
        );
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_cluster, fragment)
                .commit();
    }

    private void setupHouseholdInfo() {
        TextView name = view.findViewById(R.id.textView_hh);
        TextView min = view.findViewById(R.id.minor);

        backgroundExecutor.execute(() -> {
            try {
                Socialgroup data = viewModel.findhse(socialgroup.uuid);
                Socialgroup minorData = viewModel.minor(socialgroup.uuid);
                Visit visitData = visitViewModel.find(socialgroup.uuid);

                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    if (data != null) {
                        name.setText(data.getGroupName() + " - " + data.getExtId());
                    }

                    if (minorData != null) {
                        min.setText("Household Head is Minor");
                        min.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Red));
                    }

                    AppCompatButton searchBtn = view.findViewById(R.id.search);
                    if (visitData != null) {
                        searchBtn.setEnabled(true);
                    } else {
                        searchBtn.setEnabled(false);
                        searchBtn.setText("Unvisited");
                    }

                    AppCompatButton hohBtn = view.findViewById(R.id.hoh);
                    if (data != null) {
                        hohBtn.setEnabled(true);
                        hohBtn.setVisibility(View.VISIBLE);
                        hohBtn.setText("Change Household Head");
                    }
                });
            } catch (Exception e) {
                Log.e("HouseMembersFragment", "Error loading household info", e);
            }
        });
    }

    private void loadConfigSettings() {
        backgroundExecutor.execute(() -> {
            try {
                ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
                List<Configsettings> settings = configViewModel.findAll();
                if (settings != null && !settings.isEmpty()) {
                    hohAge = settings.get(0).hoh_age;
                    motherAge = settings.get(0).mother_age;
                    fatherAge = settings.get(0).father_age;
                }
            } catch (Exception e) {
                Log.e("HouseMembersFragment", "Error loading config", e);
            }
        });
    }

    private void performBackgroundChecks() {
        backgroundExecutor.execute(() -> {
            try {
                PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
                List<Pregnancy> pregnancyList = pregnancyViewModel.retrievePregnancy(socialgroup.getExtId());
                List<Individual> minorsList = individualViewModel.minors(currentLocation.compno, socialgroup.getExtId());

                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    if (pregnancyList != null && !pregnancyList.isEmpty()) {
                        showPregnancyDialog();
                    }
                    if (minorsList != null && !minorsList.isEmpty()) {
                        showMinorsDialog();
                    }
                });
            } catch (Exception e) {
                Log.e("HouseMembersFragment", "Error in background checks", e);
            }
        });
    }

    private void showEventMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.getMenuInflater().inflate(R.menu.edit_event, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.dth) {
                DthAdapterFragment.newInstance(locations, socialgroup, individual)
                        .show(getChildFragmentManager(), "DthAdapterFragment");
            } else if (itemId == R.id.omg) {
                OmgAdapterFragment.newInstance(locations, socialgroup, individual)
                        .show(getChildFragmentManager(), "OmgAdapterFragment");
            }
            return true;
        });
        popupMenu.show();
    }

    @Override
    public void onIndividualClick(Individual selectedIndividual) {
        if (selectedIndividual == null) {
            Toast.makeText(getContext(), "No individual selected", Toast.LENGTH_SHORT).show();
            return;
        }

        individualSharedViewModel.setSelectedIndividual(selectedIndividual);

        TextView person = view.findViewById(R.id.textView_person);
        person.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);

        // Update UI in background
        updateIndividualButtons(selectedIndividual);
    }

    private void updateIndividualButtons(Individual selectedIndividual) {
        backgroundExecutor.execute(() -> {
            try {
                // Fetch all data in background
                Visit visitData = visitViewModel.find(socialgroup.uuid);
                ButtonStateData stateData = fetchButtonStates(selectedIndividual);

                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    applyButtonStates(selectedIndividual, visitData, stateData);
                });
            } catch (Exception e) {
                Log.e("HouseMembersFragment", "Error updating buttons", e);
            }
        });
    }

    private ButtonStateData fetchButtonStates(Individual individual) throws Exception {
        ButtonStateData data = new ButtonStateData();

        // Fetch all completion states
        DuplicateViewModel dupVM = new ViewModelProvider(this).get(DuplicateViewModel.class);
        data.duplicate = dupVM.find(individual.uuid);

        AmendmentViewModel amendVM = new ViewModelProvider(this).get(AmendmentViewModel.class);
        data.amendment = amendVM.find(individual.uuid);

        DemographicViewModel demoVM = new ViewModelProvider(this).get(DemographicViewModel.class);
        data.demographic = demoVM.find(individual.uuid);

        PregnancyViewModel pregVM = new ViewModelProvider(this).get(PregnancyViewModel.class);
        data.pregnancy1 = pregVM.find(individual.uuid);
        data.pregnancy2 = pregVM.finds(individual.uuid);
        data.pregnancy3 = pregVM.find3(individual.uuid);
        data.extraPreg2 = pregVM.findss(individual.uuid);
        data.extraPreg3 = pregVM.finds3(individual.uuid);
        data.outcomeCheck2 = pregVM.outcome2(individual.uuid);
        data.outcomeCheck3 = pregVM.outcome3(individual.uuid);

        PregnancyoutcomeViewModel outcomeVM = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        data.outcome1 = outcomeVM.find1(individual.uuid);
        data.outcome2 = outcomeVM.finds(individual.uuid);
        data.outcome3 = outcomeVM.finds3(individual.uuid);

        RelationshipViewModel relVM = new ViewModelProvider(this).get(RelationshipViewModel.class);
        data.relationship = relVM.find(individual.uuid);

        HdssSociodemoViewModel sesVM = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        data.sociodemo = sesVM.findses(socialgroup.uuid);

        RegistryViewModel regVM = new ViewModelProvider(this).get(RegistryViewModel.class);
        data.registry = regVM.finds(socialgroup.uuid);

        MorbidityViewModel morbVM = new ViewModelProvider(this).get(MorbidityViewModel.class);
        data.morbidity = morbVM.finds(individual.uuid);

        SocialgroupViewModel sgVM = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        data.socialgroup = sgVM.findhse(socialgroup.uuid);

        ResidencyViewModel resVM = new ViewModelProvider(this).get(ResidencyViewModel.class);
        data.residency = resVM.amend(individual.uuid);

        VaccinationViewModel vacVM = new ViewModelProvider(this).get(VaccinationViewModel.class);
        data.vaccination = vacVM.find(individual.uuid);

        InmigrationViewModel imgVM = new ViewModelProvider(this).get(InmigrationViewModel.class);
        data.inmigration = imgVM.find(individual.uuid, currentLocation.uuid);

        return data;
    }

    private void applyButtonStates(Individual individual, Visit visitData, ButtonStateData stateData) {
        // Get all buttons
        AppCompatButton[] buttons = getButtons();
        View[] dividers = getDividers();

        boolean isVisited = visitData != null;

        // Set enabled state for all buttons
        for (AppCompatButton btn : buttons) {
            setButtonEnabled(btn, isVisited);
        }

        // Apply completion colors
        changeButtonColor(buttons[0], stateData.duplicate); // dup
        changeButtonColor(buttons[1], stateData.amendment); // amend
        changeButtonColor(buttons[2], stateData.demographic); // dem
        changeButtonColor(buttons[3], stateData.pregnancy1); // preg
        changeButtonColor(buttons[4], stateData.pregnancy2); // preg2
        changeButtonColor(buttons[5], stateData.pregnancy3); // preg3
        changeButtonColor(buttons[6], stateData.outcome1); // outcome
        changeButtonColor(buttons[7], stateData.outcome2); // outcome2
        changeButtonColor(buttons[8], stateData.outcome3); // outcome3
        changeButtonColor(buttons[9], stateData.relationship); // rel
        changeButtonColor(buttons[10], stateData.sociodemo); // ses
        changeButtonColor(buttons[11], stateData.registry); // reg
        changeButtonColor(buttons[12], stateData.morbidity); // mor
        changeButtonColor(buttons[13], stateData.socialgroup); // choh
        changeButtonColor(buttons[14], stateData.residency); // relhoh
        changeButtonColor(buttons[15], stateData.vaccination); // vac
        changeButtonColor(buttons[16], stateData.inmigration); // img

        // Set visibility based on individual attributes
        applyVisibilityRules(individual, buttons, dividers, stateData);
    }

    private AppCompatButton[] getButtons() {
        return new AppCompatButton[] {
                view.findViewById(R.id.dup),
                view.findViewById(R.id.amend),
                view.findViewById(R.id.demographic),
                view.findViewById(R.id.pregnancy),
                view.findViewById(R.id.pregnancy2),
                view.findViewById(R.id.pregnancy3),
                view.findViewById(R.id.outcome),
                view.findViewById(R.id.outcome2),
                view.findViewById(R.id.outcome3),
                view.findViewById(R.id.rel),
                view.findViewById(R.id.ses),
                view.findViewById(R.id.registry),
                view.findViewById(R.id.morbidity),
                view.findViewById(R.id.hoh),
                view.findViewById(R.id.relhoh),
                view.findViewById(R.id.vac),
                view.findViewById(R.id.img)
        };
    }

    private View[] getDividers() {
        return new View[] {
                view.findViewById(R.id.id1), view.findViewById(R.id.id2),
                view.findViewById(R.id.id3), view.findViewById(R.id.id4),
                view.findViewById(R.id.id5), view.findViewById(R.id.id6),
                view.findViewById(R.id.id7), view.findViewById(R.id.id8),
                view.findViewById(R.id.id9), view.findViewById(R.id.id10),
                view.findViewById(R.id.id11), view.findViewById(R.id.id12),
                view.findViewById(R.id.id13), view.findViewById(R.id.id14),
                view.findViewById(R.id.id15), view.findViewById(R.id.id16),
                view.findViewById(R.id.id17), view.findViewById(R.id.id18)
        };
    }

    private void applyVisibilityRules(Individual individual, AppCompatButton[] buttons,
                                      View[] dividers, ButtonStateData stateData) {
        // First, hide all conditional buttons and dividers by default
        hideAll(buttons[3], buttons[4], buttons[5], buttons[6], buttons[7], buttons[8]); // All pregnancy & outcome buttons
        hideAll(buttons[9], buttons[10], buttons[13], buttons[15], buttons[16]); // rel, ses, choh, vac, img
        hideAll(dividers[4], dividers[5], dividers[6], dividers[7], dividers[8], dividers[9]); // Pregnancy dividers
        hideAll(dividers[11], dividers[12], dividers[13], dividers[15], dividers[17]); // Other conditional dividers

        // Show basic buttons if gender is set
        if (individual.gender != null) {
            setVisible(buttons[0], buttons[1], buttons[2], buttons[11], buttons[12], buttons[14]); // dup, amend, dem, reg, mor, relhoh
            setVisible(dividers[0], dividers[1], dividers[2], dividers[3], dividers[10], dividers[14], dividers[16]);
        }

        // Death & outmigration buttons (always visible if gender is set)
        AppCompatButton dth = view.findViewById(R.id.death);
        AppCompatButton omg = view.findViewById(R.id.omg);
        if (individual.gender != null && dth != null && omg != null) {
            dth.setVisibility(View.VISIBLE);
            omg.setVisibility(View.VISIBLE);
        }

        // Inmigration - show if data exists
        if (stateData.inmigration != null) {
            buttons[16].setVisibility(View.VISIBLE);
            dividers[17].setVisibility(View.VISIBLE);
        }

        // Pregnancy & outcomes - ONLY for females aged motherAge to 55
        if (individual.gender != null && individual.gender == 2 &&
                individual.getAge() >= motherAge && individual.getAge() <= 55) {

            setVisible(buttons[3], buttons[6]); // preg, outcome
            setVisible(dividers[4], dividers[7]);

            // Show extra pregnancy 2 if data exists
            if (stateData.extraPreg2 != null) {
                setVisible(buttons[4], dividers[5]); // preg2
            }

            // Show extra pregnancy 3 if data exists
            if (stateData.extraPreg3 != null) {
                setVisible(buttons[5], dividers[6]); // preg3
            }

            // Show extra outcome 2 if pregnancy 2 exists
            if (stateData.outcomeCheck2 != null) {
                setVisible(buttons[7], dividers[8]); // outcome2
            }

            // Show extra outcome 3 if pregnancy 3 exists
            if (stateData.outcomeCheck3 != null) {
                setVisible(buttons[8], dividers[9]); // outcome3
            }
        }

        // Relationship - ONLY for females >= motherAge
        if (individual.gender != null && individual.gender == 2 &&
                individual.getAge() >= motherAge) {
            setVisible(buttons[9], dividers[11]); // rel
        }

        // HOH & SES - age >= hohAge
        if (individual.getAge() >= hohAge) {
            setVisible(buttons[10], buttons[13]); // ses, choh
            setVisible(dividers[12], dividers[13]);
        }

        // Vaccination - ONLY for children under 5
        if (individual.getAge() < 5) {
            setVisible(buttons[15], dividers[15]); // vac
        }
    }

    private void hideAll(View... views) {
        for (View v : views) {
            if (v != null) v.setVisibility(View.GONE);
        }
    }

    private void setVisible(View... views) {
        for (View v : views) {
            if (v != null) v.setVisibility(View.VISIBLE);
        }
    }

    private void setGone(View... views) {
        for (View v : views) {
            if (v != null) v.setVisibility(View.GONE);
        }
    }

    private void setButtonEnabled(AppCompatButton button, boolean isEnabled) {
        button.setEnabled(isEnabled);
        button.setVisibility(View.VISIBLE);
        button.setText(isEnabled ? button.getText() : "Unvisited");
    }

    private void changeButtonColor(AppCompatButton button, Object entity) {
        if (entity == null) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.home));
            return;
        }

        try {
            Integer complete = (Integer) entity.getClass().getField("complete").get(entity);
            if (complete != null && complete == 1) {
                button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_dg_start));
            } else if (complete != null && complete == 0) {
                button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.MediumBlue));
            }
        } catch (Exception e) {
            Log.e("HouseMembersFragment", "Error setting button color", e);
        }
    }

    private void showPregnancyDialog() {
        PregnancyDialogFragment.newInstance(locations, socialgroup)
                .show(getChildFragmentManager(), "PregnancyDialogFragment");
    }

    private void showMinorsDialog() {
        MinorDialogFragment.newInstance(locations, socialgroup)
                .show(getChildFragmentManager(), "MinorDialogFragment");
    }

    private void updateButtonState() {
        if (!isAdded()) return;

        backgroundExecutor.execute(() -> {
            try {
                long totalInd = individualViewModel.count(socialgroup.extId, currentLocation.compno);
                long totalRegistry = registryViewModel.count(socialgroup.getUuid());
                long totalVisit = visitViewModel.count(socialgroup.uuid);
                long cntDead = deathViewModel.err(socialgroup.extId, currentLocation.compno);
                long errMinors = individualViewModel.err(socialgroup.extId, currentLocation.compno);
                long errsHOH = individualViewModel.errs(socialgroup.extId, currentLocation.compno);

                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    LayoutInflater inflater = getLayoutInflater();
                    View toastView = inflater.inflate(R.layout.custom_toast, null);
                    TextView toastMsg = toastView.findViewById(R.id.toast_message);

                    if (cntDead > 0) {
                        showToast("Change Head of Household [HOH is Dead]", toastView, toastMsg);
                    } else if (totalInd > 0 && totalVisit > 0 && totalRegistry <= 0) {
                        showToast("Complete Household Registry Before Exit", toastView, toastMsg);
                    } else if (errsHOH > 0) {
                        showToast("Household Head is a Minor", toastView, toastMsg);
                    } else if (errMinors > 0) {
                        showToast("Only Minors Left in Household", toastView, toastMsg);
                    } else {
                        enableFinishButton();
                    }
                });
            } catch (Exception e) {
                Log.e("HouseMembersFragment", "Error updating button state", e);
            }
        });
    }

    private void showToast(String message, View customToastView, TextView toastMessage) {
        if (!isAdded()) return;

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home));
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_border_lightgray));
        finish.setOnClickListener(v -> {
            toastMessage.setText(message);
            Toast toast = new Toast(requireContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(customToastView);
            toast.show();
        });
    }

    private void enableFinishButton() {
        if (!isAdded()) return;

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home));
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        finish.setEnabled(true);
        finish.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_cluster, ClusterFragment.newInstance(level6Data, locations, socialgroup))
                        .commit()
        );
    }

    private void observeData() {
        if (socialgroup != null) {
            individualViewModel.retrieveByHouseId(socialgroup.getExtId())
                    .observe(getViewLifecycleOwner(), individuals -> {
                        if (individuals != null && !individuals.isEmpty()) {
                            adapter.setIndividualList(individuals);
                            Individual selected = individualSharedViewModel.getCurrentSelectedIndividual();
                            if (selected != null) {
                                onIndividualClick(selected);
                            }
                        } else {
                            adapter.setIndividualList(Collections.emptyList());
                            Toast.makeText(requireContext(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    try {
                        getActivity().finish();
                    } catch (Exception e) {
                        Log.e("HouseMembersFragment", "Error finishing activity", e);
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        view = null;
        finish = null;
        adapter = null;
        recyclerView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Shutdown executor to prevent memory leaks
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
    }

    // Helper class to hold button state data
    private static class ButtonStateData {
        Duplicate duplicate;
        Amendment amendment;
        Demographic demographic;
        Pregnancy pregnancy1;
        Pregnancy pregnancy2;
        Pregnancy pregnancy3;
        Pregnancy extraPreg2;
        Pregnancy extraPreg3;
        Pregnancy outcomeCheck2;
        Pregnancy outcomeCheck3;
        Pregnancyoutcome outcome1;
        Pregnancyoutcome outcome2;
        Pregnancyoutcome outcome3;
        Relationship relationship;
        HdssSociodemo sociodemo;
        Registry registry;
        Morbidity morbidity;
        Socialgroup socialgroup;
        Residency residency;
        Vaccination vaccination;
        Inmigration inmigration;
    }
}