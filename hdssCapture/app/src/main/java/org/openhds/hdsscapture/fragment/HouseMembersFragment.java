package org.openhds.hdsscapture.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Adapter.OdkFormAdapter;
import org.openhds.hdsscapture.Dialog.MinorDialogFragment;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkFormViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseMembersBinding;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.odk.FormUtilities;
import org.openhds.hdsscapture.odk.OdkForm;
import org.openhds.hdsscapture.odk.listener.OdkFormResultListener;
import org.openhds.hdsscapture.odk.model.FilledForm;
import org.openhds.hdsscapture.odk.model.OdkFormLoadData;
import org.openhds.hdsscapture.odk.model.RepeatGroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseMembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseMembersFragment extends Fragment implements IndividualViewAdapter.IndividualClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "TAG";
    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentHouseMembersBinding binding;
    private Residency residency;
    private Individual individual;
    private Hierarchy level6Data;
    //public static  Individual selectedIndividual;
    private View view;
    private DeathViewModel deathViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private SocialgroupViewModel viewModel;
    private RegistryViewModel registryViewModel;
    private VisitViewModel visitViewModel;
    private IndividualViewModel individualViewModel;
    private Round roundData;
    private AppCompatButton finish;
    private RecyclerView recyclerViewOdk;
    private OdkFormViewModel odkFormViewModel;
    private OdkFormAdapter odkFormAdapter;
    private FormUtilities formUtilities;
    private IndividualViewAdapter adapter;
    private RecyclerView recyclerView;
    private Locations currentLocation;
    private IndividualSharedViewModel individualSharedViewModel;
    private int pregnancyNumber;
    private Fieldworker fieldworkerData;

    private ExecutorService backgroundExecutor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private AlertDialog loadingDialog;

    public HouseMembersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param cluster_id  Parameter 1.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment HouseMembersFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            //this.cluster_id = getArguments().getParcelable(ARG_CLUSTER_IDS);
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
        individualSharedViewModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        odkFormViewModel = new ViewModelProvider(this).get(OdkFormViewModel.class);

        // Initialize FormUtilities with result listener
        formUtilities = new FormUtilities(this, new OdkFormResultListener() {
            @Override
            public void onFormFinalized(OdkFormLoadData loadData, Uri contentUri, String formId,
                                        String instanceUri, String metaInstanceName, Date lastUpdatedDate) {
                Toast.makeText(requireContext(), "Form completed successfully!", Toast.LENGTH_SHORT).show();
                // You can save the form result to your database here
                Log.d("ODK", "Form finalized: " + formId + ", instance: " + metaInstanceName);
            }

            @Override
            public void onFormUnFinalized(OdkFormLoadData loadData, Uri contentUri, String formId,
                                          String instanceUri, String metaInstanceName, Date lastUpdatedDate) {
                Toast.makeText(requireContext(), "Form saved for later", Toast.LENGTH_SHORT).show();
                Log.d("ODK", "Form unfinalized: " + formId);
            }

            @Override
            public void onDeleteForm(OdkFormLoadData loadData, Uri contentUri, String instanceUri) {
                Toast.makeText(requireContext(), "Form deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFormLoadError(OdkFormLoadData loadData, Object result) {
                Log.e("ODK", "Form load error for: " + loadData.formId);
            }

            @Override
            public void onFormInstanceNotFound(OdkFormLoadData loadData, Uri contentUri) {
                Toast.makeText(requireContext(), "Form instance not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize executor once
        backgroundExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //binding = FragmentHouseMembersBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_house_members, container, false);
        //binding.setIndividual(individual);

        final Intent i = getActivity().getIntent();
        roundData = i.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        ClusterSharedViewModel clusterSharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        currentLocation = clusterSharedViewModel.getCurrentSelectedLocation();

        fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        //Ended Events
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        finish = view.findViewById(R.id.button_cpvisit);
        updateButtonState();
        recyclerViewOdk = view.findViewById(R.id.recyclerView_odk);
        //grantStoragePermission();
        //query();
        // Setup ODK Forms RecyclerView
        setupOdkFormsRecyclerView();
        
//        if (socialgroup != null) {
//            // Observe household head count
//            checkHouseholdHead();
//        }

        //final TextView hh = view.findViewById(R.id.textView_compextId);
        TextView name = view.findViewById(R.id.textView_hh);
        try {
            Socialgroup data = viewModel.findhse(socialgroup.uuid);
            if (data != null) {
                name.setText(data.getGroupName() + " - " + data.getExtId());
                checkHouseholdHead();
            }else{
                name.setText("Loading...");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        recyclerView = view.findViewById(R.id.recyclerView_household);
        adapter = new IndividualViewAdapter(this, locations, socialgroup, this, individualSharedViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));

        // Use viewLifecycleOwner to avoid memory leaks
        view.post(this::observeData);

        final AppCompatButton sea = view.findViewById(R.id.search);
        sea.setOnClickListener(v -> {
            SearchFragment.newInstance(locations, socialgroup)
                    .show(getChildFragmentManager(), "SearchFragment");
        });

        TextView min = view.findViewById(R.id.minor);
        try {
            Socialgroup data = viewModel.minor(socialgroup.uuid);
            if (data != null) {
                min.setText("Household Head is Minor");
                min.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Red));
            }else{
                min.setText("");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final AppCompatButton cghoh = view.findViewById(R.id.hoh);
        SocialgroupViewModel model = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        try {
            Socialgroup data = model.find(socialgroup.uuid);
            if (data != null) {
                cghoh.setEnabled(true);
                cghoh.setVisibility(View.VISIBLE);
                cghoh.setText("Change Household Head");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        VisitViewModel vmodel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit data = vmodel.find(socialgroup.uuid);
            if (data != null) {
                sea.setEnabled(true);
            }else{
                sea.setEnabled(false);
                sea.setText("Unvisited");

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final AppCompatButton visit = view.findViewById(R.id.button_visit);
        visit.setOnClickListener(v -> {
            VisitFragment.newInstance(individual, locations, socialgroup)
                    .show(getChildFragmentManager(), "VisitFragment");
        });

        final AppCompatButton preg = view.findViewById(R.id.pregnancy);
        preg.setOnClickListener(v -> {
            //final Pregnancy pregnancy = new Pregnancy();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyFragment.newInstance(individual,locations, socialgroup, pregnancyNumber)).commit();
        });

        final AppCompatButton dup = view.findViewById(R.id.dup);
        dup.setOnClickListener(v -> {
            //final Duplicate duplicate = new Duplicate();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DupFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

        final AppCompatButton demo = view.findViewById(R.id.demographic);
        demo.setOnClickListener(v -> {
            //final Death death = new Death();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DemographicFragment.newInstance(individual,locations, socialgroup)).commit();
        });

        final AppCompatButton reg = view.findViewById(R.id.registry);
        reg.setOnClickListener(v -> {
            //final Death death = new Death();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    RegisterFragment.newInstance(locations, socialgroup,individual)).commit();
        });

        final AppCompatButton dth = view.findViewById(R.id.death);
        dth.setOnClickListener(v -> {
            //final Death death = new Death();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DeathFragment.newInstance(individual,locations, socialgroup)).commit();
        });


        final AppCompatButton omg = view.findViewById(R.id.omg);
        omg.setOnClickListener(v -> {
            // final Outmigration outmigration = new Outmigration();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    OutmigrationFragment.newInstance(individual,locations, socialgroup)).commit();
        });

        final AppCompatButton amend = view.findViewById(R.id.amend);
        amend.setOnClickListener(v -> {
            //final Amendment amendment = new Amendment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    AmendmentFragment.newInstance(individual, locations, socialgroup)).commit();
        });


        final AppCompatButton rel = view.findViewById(R.id.rel);
        rel.setOnClickListener(v -> {
            //final Relationship relationship = new Relationship();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    RelationshipFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        cghoh.setOnClickListener(v -> {
            // final Socialgroup socialgroup1 = new Socialgroup();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocialgroupFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton img = view.findViewById(R.id.img);
        img.setOnClickListener(v -> {
            //final Vaccination vaccination = new Vaccination();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    InmigrationFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton ses = view.findViewById(R.id.ses);
        ses.setOnClickListener(v -> {
            //final HdssSociodemo hdssSociodemo = new HdssSociodemo();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioAFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton mor = view.findViewById(R.id.morbidity);
        mor.setOnClickListener(v -> {
            //final HdssSociodemo hdssSociodemo = new HdssSociodemo();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    MorbidityFragment.newInstance(individual, locations, socialgroup)).commit();
        });


        final AppCompatButton vac = view.findViewById(R.id.vac);
        vac.setOnClickListener(v -> {
            //final Vaccination vaccination = new Vaccination();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    VaccinationFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton relhoh = view.findViewById(R.id.relhoh);
        relhoh.setOnClickListener(v -> {
            //final Residency residency1 = new Residency();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    ResidencyFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                List<Pregnancy> pregnancyList = pregnancyViewModel.retrievePregnancy(socialgroup.getExtId());
                if (pregnancyList != null && !pregnancyList.isEmpty()) {
                    handler.post(this::showPregnancyDialog);
                }

                List<Individual> individualList = individualViewModel.minors(currentLocation.compno,socialgroup.getExtId());
                if (individualList != null && !individualList.isEmpty()) {
                    handler.post(this::showMinorsDialog);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        AppCompatButton event = view.findViewById(R.id.menu);
        //imageView = view.findViewById(R.id.menu);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v); // Use getActivity() to get the Context
                popupMenu.getMenuInflater().inflate(R.menu.edit_event, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.dth) {
                            // open DialogFragment DthFragment
                            DthAdapterFragment.newInstance(locations, socialgroup, individual)
                                    .show(getChildFragmentManager(), "DthAdapterFragment");
                        } else if (itemId == R.id.omg) {
                            // open DialogFragment OmgFragment
                            OmgAdapterFragment.newInstance(locations, socialgroup, individual)
                                    .show(getChildFragmentManager(), "OmgAdapterFragment");
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });


        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the selected individual when leaving this fragment
        //individualSharedViewModel.setSelectedIndividual(null);
        binding = null;
    }

    //method to setup ODK forms
    private void setupOdkFormsRecyclerView() {
        recyclerViewOdk = view.findViewById(R.id.recyclerView_odk);
        recyclerViewOdk.setLayoutManager(new LinearLayoutManager(getContext()));

        odkFormAdapter = new OdkFormAdapter(this::launchOdkForm);
        recyclerViewOdk.setAdapter(odkFormAdapter);
        recyclerViewOdk.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));

        // Initially hide the recycler view
        recyclerViewOdk.setVisibility(View.GONE);
    }


    @Override
    public void onIndividualClick(Individual selectedIndividual) {
        //HouseMembersFragment.selectedIndividual = selectedIndividual; // Always update the selectedLocation variable

        // Update the householdAdapter with the selected individual
        if (selectedIndividual != null) {
            individualSharedViewModel.setSelectedIndividual(selectedIndividual);

            // Load ODK forms applicable to this individual
            loadApplicableOdkForms(selectedIndividual);

            ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
            List<Configsettings> configsettings = null;
            try {
                configsettings = viewModel.findAll();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            int hoh = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).hoh_age : 0;
            int mage = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).mother_age : 0;
            int fage = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).father_age : 0;

            TextView person = view.findViewById(R.id.textView_person);
            person.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);
            //eventFormAdapter.formFactory(HouseMembersFragment.selectedIndividual);
            AppCompatButton dup = view.findViewById(R.id.dup);
            AppCompatButton preg = view.findViewById(R.id.pregnancy);
            AppCompatButton dth = view.findViewById(R.id.death);
            AppCompatButton omg = view.findViewById(R.id.omg);
            AppCompatButton dem = view.findViewById(R.id.demographic);
            AppCompatButton amend = view.findViewById(R.id.amend);
            AppCompatButton rel = view.findViewById(R.id.rel);
            AppCompatButton choh = view.findViewById(R.id.hoh);
            AppCompatButton ses = view.findViewById(R.id.ses);
            AppCompatButton vac = view.findViewById(R.id.vac);
            AppCompatButton relhoh = view.findViewById(R.id.relhoh);
            AppCompatButton img = view.findViewById(R.id.img);
            AppCompatButton mor = view.findViewById(R.id.morbidity);
            AppCompatButton reg = view.findViewById(R.id.registry);
            View id1 = view.findViewById(R.id.id1);
            View id2 = view.findViewById(R.id.id2);
            View id3 = view.findViewById(R.id.id3);
            View id4 = view.findViewById(R.id.id4);
            View id5 = view.findViewById(R.id.id5);
            View id11 = view.findViewById(R.id.id11);
            View id12 = view.findViewById(R.id.id12);
            View id13 = view.findViewById(R.id.id13);
            View id14 = view.findViewById(R.id.id14);
            View id15 = view.findViewById(R.id.id15);
            View id16 = view.findViewById(R.id.id16);
            View id17 = view.findViewById(R.id.id17);
            View id18 = view.findViewById(R.id.id18);

            VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            try {
                Visit data = visitViewModel.find(socialgroup.uuid);

                // Define default texts for each button
                String TextDup = "Duplicate";
                String TextPreg = "Pregnancy";
                String TextDth = "Death";
                String TextOmg = "Outmigration";
                String TextDem = "Demographic";
                String TextAmend = "Amendment";
                String TextRel = "Relationship";
                String TextChoh = "Change Household Head";
                String TextSes = "Socio-Economic Status";
                String TextMor = "Morbidity Assessment";
                String TextVac = "Vaccination";
                String TextRelhoh = "Update Membership";
                String TextImg = "Update Inmigration";
                String TextReg = "Household Registry";


                // Disable all buttons if Visit data is null
                boolean isVisitDataNull = (data == null);
                setButtonEnabled(dup, !isVisitDataNull);
                setButtonText(dup, !isVisitDataNull, TextDup);
                setButtonEnabled(preg, !isVisitDataNull);
                setButtonText(preg, !isVisitDataNull, TextPreg);
                setButtonEnabled(dth, !isVisitDataNull);
                setButtonText(dth, !isVisitDataNull, TextDth);
                setButtonEnabled(omg, !isVisitDataNull);
                setButtonText(omg, !isVisitDataNull, TextOmg);
                setButtonEnabled(dem, !isVisitDataNull);
                setButtonText(dem, !isVisitDataNull, TextDem);
                setButtonEnabled(amend, !isVisitDataNull);
                setButtonText(amend, !isVisitDataNull, TextAmend);
                setButtonEnabled(rel, !isVisitDataNull);
                setButtonText(rel, !isVisitDataNull, TextRel);
                setButtonEnabled(choh, !isVisitDataNull);
                setButtonText(choh, !isVisitDataNull, TextChoh);
                setButtonEnabled(ses, !isVisitDataNull);
                setButtonText(ses, !isVisitDataNull, TextSes);
                setButtonEnabled(vac, !isVisitDataNull);
                setButtonText(vac, !isVisitDataNull, TextVac);
                setButtonEnabled(relhoh, !isVisitDataNull);
                setButtonText(relhoh, !isVisitDataNull, TextRelhoh);
                setButtonEnabled(mor, !isVisitDataNull);
                setButtonText(mor, !isVisitDataNull, TextMor);
                setButtonEnabled(reg, !isVisitDataNull);
                setButtonText(reg, !isVisitDataNull, TextReg);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error retrieving visit data", Toast.LENGTH_SHORT).show();
            }

            DuplicateViewModel dupViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
            try {
                Duplicate data = dupViewModel.getId(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(dup, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(dup, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            AmendmentViewModel a = new ViewModelProvider(this).get(AmendmentViewModel.class);
            try {
                Amendment data = a.find(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(amend, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(amend, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            DemographicViewModel b = new ViewModelProvider(this).get(DemographicViewModel.class);
            try {
                Demographic data = b.find(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(dem, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(dem, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }


            PregnancyViewModel c = new ViewModelProvider(this).get(PregnancyViewModel.class);
            try {
                Pregnancy data = c.find(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(preg, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(preg, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            RelationshipViewModel g = new ViewModelProvider(this).get(RelationshipViewModel.class);
            try {
                Relationship data = g.find(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(rel, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(rel, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            HdssSociodemoViewModel f = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
            try {
                HdssSociodemo data = f.findses(socialgroup.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(ses, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(ses, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            RegistryViewModel r = new ViewModelProvider(this).get(RegistryViewModel.class);
            try {
                Registry data = r.finds(socialgroup.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(reg, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(reg, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            MorbidityViewModel m = new ViewModelProvider(this).get(MorbidityViewModel.class);
            try {
                Morbidity data = m.finds(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(mor, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(mor, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            SocialgroupViewModel h = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            try {
                Socialgroup data = h.findhse(socialgroup.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(choh, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(choh, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            ResidencyViewModel i = new ViewModelProvider(this).get(ResidencyViewModel.class);
            try {
                Residency data = i.amend(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(relhoh, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(relhoh, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            VaccinationViewModel v = new ViewModelProvider(this).get(VaccinationViewModel.class);
            try {
                Vaccination data = v.find(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(vac, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(vac, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }


            if(selectedIndividual.gender!=null && roundData!=null && roundData.roundNumber>0) {
                dup.setVisibility(View.VISIBLE);
                dem.setVisibility(View.VISIBLE);
                omg.setVisibility(View.VISIBLE);
                dth.setVisibility(View.VISIBLE);
                amend.setVisibility(View.VISIBLE);
                id11.setVisibility(View.VISIBLE);
                relhoh.setVisibility(View.VISIBLE);
                id17.setVisibility(View.VISIBLE);
                id1.setVisibility(View.VISIBLE);
                id2.setVisibility(View.VISIBLE);
                id3.setVisibility(View.VISIBLE);
                id4.setVisibility(View.VISIBLE);
                id14.setVisibility(View.VISIBLE);
                mor.setVisibility(View.VISIBLE);
                id15.setVisibility(View.VISIBLE);
                reg.setVisibility(View.VISIBLE);

            }else{

                dup.setVisibility(View.GONE);
                dem.setVisibility(View.VISIBLE);
                id4.setVisibility(View.VISIBLE);
                omg.setVisibility(View.GONE);
                dth.setVisibility(View.GONE);
                amend.setVisibility(View.VISIBLE);
                id11.setVisibility(View.VISIBLE);
                relhoh.setVisibility(View.VISIBLE);
                id17.setVisibility(View.VISIBLE);
                id1.setVisibility(View.GONE);
                id2.setVisibility(View.GONE);
                id3.setVisibility(View.GONE);
                id14.setVisibility(View.GONE);
                mor.setVisibility(View.GONE);
                id15.setVisibility(View.GONE);
                reg.setVisibility(View.GONE);

            }

            InmigrationViewModel imgViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
            try {
                Inmigration data = imgViewModel.find(selectedIndividual.uuid,currentLocation.uuid);
                if (data != null) {
                    String TextImg = "Update Inmigration";
                    img.setVisibility(View.VISIBLE);
                    id18.setVisibility(View.VISIBLE);
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(img, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(img, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }


            if (selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2  && roundData!=null && roundData.roundNumber>0){
                preg.setVisibility(View.VISIBLE);
                //rel.setVisibility(View.VISIBLE);
                id5.setVisibility(View.VISIBLE);
            }else{
                preg.setVisibility(View.GONE);
                //rel.setVisibility(View.GONE);
                id5.setVisibility(View.GONE);
            }
            if (selectedIndividual.getAge() >= mage && selectedIndividual.gender==2  && roundData!=null && roundData.roundNumber>0){
                rel.setVisibility(View.VISIBLE);
                id12.setVisibility(View.VISIBLE);
            }else{
                rel.setVisibility(View.GONE);
                id12.setVisibility(View.GONE);
            }

            if (selectedIndividual.getAge() >= hoh  && roundData!=null && roundData.roundNumber>0){
                ses.setVisibility(View.VISIBLE);
                choh.setVisibility(View.VISIBLE);
                id14.setVisibility(View.VISIBLE);
                id13.setVisibility(View.VISIBLE);
            }else if (selectedIndividual.getAge() >= hoh  && roundData!=null && roundData.roundNumber==0){
                ses.setVisibility(View.GONE);
                choh.setVisibility(View.VISIBLE);
                id14.setVisibility(View.VISIBLE);
                id13.setVisibility(View.GONE);
            }else{
                ses.setVisibility(View.GONE);
                choh.setVisibility(View.GONE);
                id14.setVisibility(View.GONE);
                id13.setVisibility(View.GONE);
            }


            if (selectedIndividual.getAge() < 5  && roundData!=null && roundData.roundNumber>0){
                vac.setVisibility(View.VISIBLE);
                id16.setVisibility(View.VISIBLE);
            }else{
                vac.setVisibility(View.GONE);
                id16.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(getContext(), "No individual selected", Toast.LENGTH_SHORT).show();
        }



    }

    private void setButtonEnabled(AppCompatButton button, boolean isEnabled) {
        button.setEnabled(isEnabled);
        // Set the visibility to visible regardless of the isEnabled state
        button.setVisibility(View.VISIBLE);
    }

    private void setButtonText(AppCompatButton button, boolean isEnabled, String defaultText) {
        if (isEnabled) {
            // Set the default text when the button is enabled
            button.setText(defaultText);
        } else {
            // Set the text for unvisited state
            button.setText("Unvisited");
        }
    }


    private void changeDupButtonColor(AppCompatButton button, boolean isComplete, boolean isIncomplete) {
        if (isComplete) {
            // Change button color when there is data
            //button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btnd_rone_incomp));
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.color_dg_start));
        } else if (isIncomplete) {
            // Change button color when there is no data
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.MediumBlue));
            //button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btnd_rone_comp));
        }
        else {
            // Change button color for other cases
            //button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btnd_rone_btn));
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.home));
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
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);
        TextView toastMessage = customToastView.findViewById(R.id.toast_message);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {

                // Perform database operations in the background
                long totalInd = (individualViewModel != null) ? individualViewModel.count(socialgroup != null ? socialgroup.extId : "", currentLocation.compno != null ? currentLocation.compno : "") : 0;
                long totalRegistry = (registryViewModel != null) ? registryViewModel.count(socialgroup != null ? socialgroup.getUuid() : "") : 0;
                long totalVisit = (visitViewModel != null) ? visitViewModel.count(socialgroup != null ? socialgroup.uuid : "") : 0;
                long cnt = (deathViewModel != null) ? deathViewModel.err(socialgroup != null ? socialgroup.extId : "", currentLocation.compno != null ? currentLocation.compno : "") : 0;
                long err = (individualViewModel != null) ? individualViewModel.err(socialgroup != null ? socialgroup.extId : "", currentLocation.compno != null ? currentLocation.compno : "") : 0;
                long errs = (individualViewModel != null) ? individualViewModel.errs(socialgroup != null ? socialgroup.extId : "", currentLocation.compno != null ? currentLocation.compno : "") : 0;

                handler.post(() -> {
                    if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached
                    Log.d("Count", "Count: " + totalInd +" : " + totalVisit + " : " + totalVisit);

                    try {
                        if (cnt > 0) {
                            showToast("Change Head of Household [HOH is Dead]", customToastView, toastMessage);
                        } else if (totalInd > 0 && totalVisit > 0 && totalRegistry <= 0 && roundData!=null && roundData.roundNumber>0) {
                            showToast("Complete Household Registry Before Exit", customToastView, toastMessage);
                        } else if (errs > 0) {
                            showToast("Household Head is a Minor", customToastView, toastMessage);
                        } else if (err > 0) {
                            showToast("Only Minors Left in Household", customToastView, toastMessage);
                        } else {
                            enableFinishButton();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });
    }

    // Helper method to show a custom toast message
    private void showToast(String message, View customToastView, TextView toastMessage) {
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home));
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_border_lightgray));
        finish.setOnClickListener(v -> {
            toastMessage.setText(message);
            Toast customToast = new Toast(requireContext());
            customToast.setDuration(Toast.LENGTH_LONG);
            customToast.setView(customToastView);
            customToast.show();
        });
    }

    // Helper method to enable the finish button with default functionality
    private void enableFinishButton() {
        if (getActivity() == null || !isAdded()) return;

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home)); // Original color
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // Original text color
        finish.setEnabled(true);
        finish.setOnClickListener(v -> {
            // Clear selected individual before navigating back
            individualSharedViewModel.setSelectedIndividual(null);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, ClusterFragment.newInstance(level6Data, locations, socialgroup))
                    .commit();
        });
    }

    private void observeData() {
        if (socialgroup != null) {
            individualViewModel.retrieveByHouseId(socialgroup.getExtId(), currentLocation.compno)
                    .observe(getViewLifecycleOwner(), individuals -> {
                        if (individuals != null && !individuals.isEmpty()) {
                            adapter.setIndividualList(individuals);

                            //Trigger button visibility logic
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

    private void checkHouseholdHead() {
        if (socialgroup != null && socialgroup.getIndividual_uuid() != null) {

            // Method 1: Check if the individual_uuid from socialgroup is active
            individualViewModel.isActiveHouseholdHead(socialgroup.getIndividual_uuid())
                    .observe(getViewLifecycleOwner(), activeCount -> {
                        if (activeCount != null && activeCount == 0) {
                            // The individual_uuid does not exist or is not active (endType != 1)
                            showNoHeadNotification("not_active");
                        } else if (activeCount > 0 && locations != null) {
                            // Individual exists and is active, now check if they live in this compound
                            checkIfHeadLivesInCompound();
                        }
                    });
        }
    }

    private void checkIfHeadLivesInCompound() {
        if (socialgroup != null && socialgroup.getIndividual_uuid() != null
                && locations != null && locations.getCompno() != null) {

            individualViewModel.isHeadInCompound(
                    socialgroup.getIndividual_uuid(),
                    locations.getCompno()
            ).observe(getViewLifecycleOwner(), count -> {
                if (count != null && count == 0) {
                    // Head exists and is active but doesn't live in this compound
                    showNoHeadNotification("wrong_compound");
                }
            });
        }
    }

    // Enhanced notification method with different messages
    private void showNoHeadNotification(String reason) {
        String message;

        switch (reason) {
            case "not_active":
                message = "The assigned household head is not an active member! " +
                        "They may have migrated, died, or been removed. " +
                        "Kindly assign a new household head.";
                break;
            case "wrong_compound":
                message = "The current household head does not live in this Household/Compound! " +
                        "Kindly assign a household head who resides here.";
                break;
            default:
                message = "No valid household head found. Kindly assign a household head.";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Invalid Household Head")
                .setMessage(message)
                .setPositiveButton("Assign Head", (dialog, which) -> {
                    // Navigate to assign head screen
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_cluster,
                                    SocialgroupFragment.newInstance(individual, locations, socialgroup))
                            .addToBackStack(null)
                            .commit();
                })
                .setNegativeButton("Later", null)
                .setCancelable(false) // Prevent dismissing without action
                .show();
    }

    //ODK FORM ADD ONS
    //Method to load applicable forms based on individual's age and gender
    /**
     * Method to load applicable forms based on individual's age and gender
     */
    private void loadApplicableOdkForms(Individual individual) {
        if (individual == null) {
            recyclerViewOdk.setVisibility(View.GONE);
            return;
        }

        Integer gender = individual.gender;
        Integer age = individual.getAge();

        odkFormViewModel.getFormsForIndividual(gender, age)
                .observe(getViewLifecycleOwner(), forms -> {
                    if (forms != null && !forms.isEmpty()) {
                        List<OdkForm> availableForms = new ArrayList<>();
                        for (OdkForm form : forms) {
                            if (form.enabled != null && form.enabled == 1) {
                                availableForms.add(form);
                            }
                        }

                        if (!availableForms.isEmpty()) {
                            odkFormAdapter.setFormList(availableForms);
                            recyclerViewOdk.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewOdk.setVisibility(View.GONE);
                        }
                    } else {
                        recyclerViewOdk.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Launch ODK form - with existing form detection
     */
    private void launchOdkForm(OdkForm form) {
        Individual selectedIndividual = individualSharedViewModel.getCurrentSelectedIndividual();

        if (selectedIndividual == null) {
            Toast.makeText(requireContext(), "No individual selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if ODK Collect is installed
        if (formUtilities.getOdkStorageType() ==
                org.openhds.hdsscapture.odk.storage.access.OdkStorageType.NO_ODK_INSTALLED) {
            showInstallOdkDialog();
            return;
        }

        // STEP 1: Check for existing incomplete forms for this individual
        checkAndLaunchForm(selectedIndividual, form);
    }

    /**
     * Check for existing forms before creating new one
     */
    private void checkAndLaunchForm(Individual individual, OdkForm form) {
        // Show loading indicator
        showLoadingDialog("Checking for existing forms...");

        backgroundExecutor.execute(() -> {
            try {
                // Check if an existing form exists for this individual
                FormUtilities.ExistingFormInfo existingForm =
                        formUtilities.findExistingForm(individual.uuid, form.formID);

                mainHandler.post(() -> {
                    dismissLoadingDialog();

                    if (existingForm != null) {
                        // Found existing form - ask user what to do
                        showExistingFormDialog(individual, form, existingForm);
                    } else {
                        // No existing form - create new one
                        Log.d(TAG, "No existing form found, creating new one");
                        createNewOdkForm(individual, form);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error checking for existing form", e);
                mainHandler.post(() -> {
                    dismissLoadingDialog();
                    Toast.makeText(requireContext(),
                            "Error checking forms: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Show dialog when existing form is found
     */
    private void showExistingFormDialog(Individual individual, OdkForm form,
                                        FormUtilities.ExistingFormInfo existingForm) {
        String message = String.format(
                "An incomplete form already exists for %s %s.\n\nWhat would you like to do?",
                individual.firstName,
                individual.lastName
        );

        new AlertDialog.Builder(requireContext())
                .setTitle("Existing Form Found")
                .setMessage(message)
                .setPositiveButton("Continue Editing", (dialog, which) -> {
                    Log.d(TAG, "User chose to continue editing existing form");
                    reopenExistingForm(individual, form, existingForm);
                })
                .setNegativeButton("Create New", (dialog, which) -> {
                    Log.d(TAG, "User chose to create new form, deleting old one");
                    deleteAndCreateNew(individual, form, existingForm);
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "User cancelled form launch");
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Reopen existing form for editing
     */
    private void reopenExistingForm(Individual individual, OdkForm form,
                                    FormUtilities.ExistingFormInfo existingForm) {
        try {
            Log.d(TAG, "=== Reopening Existing Form ===");
            Log.d(TAG, "  Individual: " + individual.firstName + " " + individual.lastName);
            Log.d(TAG, "  Form ID: " + form.formID);
            Log.d(TAG, "  Content URI: " + existingForm.contentUri);
            Log.d(TAG, "  Instance Path: " + existingForm.instancePath);

            // Create load data for existing form
            OdkFormLoadData loadData = OdkFormLoadData.forExistingForm(form, existingForm.instancePath);

            //Set this flag to prevent the unfinalized dialog
            loadData.skipUnfinalizedCheck = true;

            Log.d(TAG, "  Skip Unfinalized Check: " + loadData.skipUnfinalizedCheck);

            // Use the loadExistingForm method
            formUtilities.loadExistingForm(
                    loadData,
                    existingForm.contentUri.toString(),
                    existingForm.instancePath,
                    null // Or pass your formResultListener if needed
            );

        } catch (Exception e) {
            Log.e(TAG, "Error reopening form", e);
            Toast.makeText(requireContext(),
                    "Error reopening form: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete existing form and create new one
     */
    private void deleteAndCreateNew(Individual individual, OdkForm form,
                                    FormUtilities.ExistingFormInfo existingForm) {
        try {
            // Delete the old form instance
            int deleted = requireContext().getContentResolver().delete(
                    existingForm.contentUri,
                    null,
                    null
            );

            Log.d(TAG, "Deleted " + deleted + " form(s)");

            // Now create new form
            createNewOdkForm(individual, form);

        } catch (Exception e) {
            Log.e(TAG, "Error deleting old form", e);
            Toast.makeText(requireContext(),
                    "Error deleting old form: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create new ODK form with preloaded data
     */
    private void createNewOdkForm(Individual individual, OdkForm form) {
        try {
            Log.d(TAG, "=== Creating New Form ===");
            Log.d(TAG, "  Individual UUID: " + individual.uuid);
            Log.d(TAG, "  Individual Name: " + individual.firstName + " " + individual.lastName);
            Log.d(TAG, "  Form ID: " + form.formID);

            // Create preloaded data with basic individual/location/household info
            FilledForm preloadedData = FormUtilities.createPreloadedData(
                    individual, locations, socialgroup, fieldworkerData);

            // CRITICAL: Ensure individualId is set
            Log.d(TAG, "  Setting individualId: " + individual.uuid);
            preloadedData.put("individualId", individual.uuid);

            // Set form name
            preloadedData.setFormName(form.formID);

            // Add timestamp and device ID
            preloadedData.put("start", formUtilities.getStartTimestamp());
            preloadedData.put("deviceid", formUtilities.getDeviceId());

            // Load household members in background
            showLoadingDialog("Loading household members...");

            backgroundExecutor.execute(() -> {
                try {
                    // Get all household members
                    List<Individual> allMembers = individualViewModel.getHouseholdMembersSync(
                            socialgroup.getExtId(), currentLocation.compno);

                    // Filter by status if needed
                    List<Individual> residentMembers = filterResidentMembers(allMembers);
                    List<Individual> deadMembers = filterDeadMembers(allMembers);
                    List<Individual> outmigMembers = filterOutmigMembers(allMembers);

                    // Add members to preloaded data
                    preloadedData.setHouseholdMembers(allMembers);
                    preloadedData.setResidentMembers(residentMembers);
                    preloadedData.setDeadMembers(deadMembers);
                    preloadedData.setOutmigMembers(outmigMembers);

                    // Configure repeat groups based on form type
                    configureRepeatGroups(preloadedData, form);

                    // Launch form on main thread
                    mainHandler.post(() -> {
                        dismissLoadingDialog();

                        Log.d(TAG, "Launching new form with " + allMembers.size() + " household members");

                        OdkFormLoadData loadData = new OdkFormLoadData(form, preloadedData);
                        formUtilities.loadForm(loadData);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error loading members", e);
                    mainHandler.post(() -> {
                        dismissLoadingDialog();
                        Toast.makeText(requireContext(),
                                "Error loading household members", Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating ODK form", e);
            Toast.makeText(requireContext(),
                    "Error launching form: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }



    //Working version for new form only
//    private void launchOdkForm(OdkForm form) {
//        Individual selectedIndividual = individualSharedViewModel.getCurrentSelectedIndividual();
//
//        if (selectedIndividual == null) {
//            Toast.makeText(requireContext(), "No individual selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check if ODK Collect is installed
//        if (formUtilities.getOdkStorageType() ==
//                org.openhds.hdsscapture.odk.storage.access.OdkStorageType.NO_ODK_INSTALLED) {
//            showInstallOdkDialog();
//            return;
//        }
//
//        try {
//            // Create preloaded data with basic individual/location/household info
//            FilledForm preloadedData = FormUtilities.createPreloadedData(
//                    selectedIndividual, locations, socialgroup, fieldworkerData);
//
//            // Set form name
//            preloadedData.setFormName(form.formID);
//
//            // Add timestamp and device ID
//            preloadedData.put("start", formUtilities.getStartTimestamp());
//            preloadedData.put("deviceid", formUtilities.getDeviceId());
//
//            // Add household members for repeat groups if needed
//            // Load all household members in background
//            backgroundExecutor.execute(() -> {
//                try {
//                    // Get all household members
//                    List<Individual> allMembers = individualViewModel.getHouseholdMembersSync(
//                            socialgroup.getExtId(), currentLocation.compno);
//
//                    // Filter by status if needed
//                    List<Individual> residentMembers = filterResidentMembers(allMembers);
//                    List<Individual> deadMembers = filterDeadMembers(allMembers);
//                    List<Individual> outmigMembers = filterOutmigMembers(allMembers);
//
//                    // Add members to preloaded data
//                    preloadedData.setHouseholdMembers(allMembers);
//                    preloadedData.setResidentMembers(residentMembers);
//                    preloadedData.setDeadMembers(deadMembers);
//                    preloadedData.setOutmigMembers(outmigMembers);
//
//                    // Configure repeat groups based on form type
//                    configureRepeatGroups(preloadedData, form);
//
//                    // Launch form on main thread
//                    mainHandler.post(() -> {
//                        OdkFormLoadData loadData = new OdkFormLoadData(form, preloadedData);
//                        formUtilities.loadForm(loadData);
//                    });
//
//                } catch (Exception e) {
//                    Log.e("HouseMembersFragment", "Error loading members", e);
//                    mainHandler.post(() ->
//                            Toast.makeText(requireContext(),
//                                    "Error loading household members", Toast.LENGTH_SHORT).show()
//                    );
//                }
//            });
//
//        } catch (Exception e) {
//            Log.e("HouseMembersFragment", "Error launching ODK form", e);
//            Toast.makeText(requireContext(),
//                    "Error launching form: " + e.getMessage(),
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     * Configure repeat groups based on form requirements
     */
    private void configureRepeatGroups(FilledForm preloadedData, OdkForm form) {
        //If form has a "household_roster" repeat group for all members
        if (form.formID.contains("roster") || form.formID.contains("household")) {
            Map<String, String> memberMapping = new HashMap<>();
            memberMapping.put("member_id", "Member.uuid");
            memberMapping.put("member_name", "Member.firstName");
            memberMapping.put("member_gender", "Member.gender");
            memberMapping.put("member_age", "Member.age");

            List<Map<String, String>> mappingList = new ArrayList<>();
            mappingList.add(memberMapping);

            preloadedData.addRepeatGroup("household_roster",
                    RepeatGroupType.ALL_MEMBERS,
                    mappingList);
        }

        // Example: Pregnancy form might need resident females only
        if (form.formID.contains("pregnancy")) {
            Map<String, String> femaleMapping = new HashMap<>();
            femaleMapping.put("woman_id", "Member.uuid");
            femaleMapping.put("woman_name", "Member.firstName");
            femaleMapping.put("woman_age", "Member.age");

            List<Map<String, String>> mappingList = new ArrayList<>();
            mappingList.add(femaleMapping);

            preloadedData.addRepeatGroup("eligible_women",
                    RepeatGroupType.RESIDENT_MEMBERS,
                    mappingList);
        }
    }

    /**
     * Filter members by residency status
     */
    private List<Individual> filterResidentMembers(List<Individual> allMembers) {
        List<Individual> residents = new ArrayList<>();
        for (Individual member : allMembers) {
            // Assuming you have an endType field: 1 = resident, 2 = outmigrated, 3 = dead
            if (member.endType == null || member.endType == 1) {
                residents.add(member);
            }
        }
        return residents;
    }

    private List<Individual> filterDeadMembers(List<Individual> allMembers) {
        List<Individual> dead = new ArrayList<>();
        for (Individual member : allMembers) {
            if (member.endType != null && member.endType == 3) {
                dead.add(member);
            }
        }
        return dead;
    }

    private List<Individual> filterOutmigMembers(List<Individual> allMembers) {
        List<Individual> outmig = new ArrayList<>();
        for (Individual member : allMembers) {
            if (member.endType != null && member.endType == 2) {
                outmig.add(member);
            }
        }
        return outmig;
    }

    private void showInstallOdkDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("ODK Collect Not Installed")
                .setMessage("ODK Collect must be installed to open forms. Would you like to install it?")
                .setPositiveButton("Install", (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=org.odk.collect.android")));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=org.odk.collect.android")));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLoadingDialog(String message) {
        dismissLoadingDialog(); // Dismiss any existing dialog

        loadingDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Please Wait")
                .setMessage(message)
                .setCancelable(false)
                .create();
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    // Override the onBackPressed() method in the hosting activity
    //@Override
    public void onBackPressed() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            getActivity().finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

}