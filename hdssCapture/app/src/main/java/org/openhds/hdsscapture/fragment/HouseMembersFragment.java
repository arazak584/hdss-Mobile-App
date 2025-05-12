package org.openhds.hdsscapture.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.openhds.hdsscapture.Adapter.EndEventsAdapter;
import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Dialog.MinorDialogFragment;
import org.openhds.hdsscapture.odk.OdkFormAdapter;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.OutcomeFragment.Birth3Fragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthExtraFragment;
import org.openhds.hdsscapture.OutcomeFragment.BirthFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.odk.OdkUtils;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
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
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subqueries.EndEvents;
import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;
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
    private static final int ODK_REQUEST_CODE = 1;
    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentHouseMembersBinding binding;
    private ProgressDialog progress;
    private ProgressDialog progres;
    private Residency residency;
    private Individual individual;
    private Hierarchy level6Data;
    private ArrayAdapter<Hierarchy> level6Adapter;
    public static  Individual selectedIndividual;
    private View view;
    private EndEventsAdapter eventsAdapter;
    private List<EndEvents> filterAll;
    private DeathViewModel deathViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private SocialgroupViewModel viewModel;
    private RegistryViewModel registryViewModel;
    private VisitViewModel visitViewModel;
    private IndividualViewModel individualViewModel;
    private  Pregnancy pregnancy;
    private Pregnancyoutcome pregnancyoutcome;
    private AppCompatButton finish;
    private OdkViewModel odkViewModel;
    private RecyclerView recyclerViewOdk;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //binding = FragmentHouseMembersBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_house_members, container, false);
        //binding.setIndividual(individual);

        //Ended Events
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        odkViewModel = new ViewModelProvider(this).get(OdkViewModel.class);
        finish = view.findViewById(R.id.button_cpvisit);
        updateButtonState();
        recyclerViewOdk = view.findViewById(R.id.recyclerView_odk);
        grantStoragePermission();
        //query();

        //final TextView hh = view.findViewById(R.id.textView_compextId);
        TextView name = view.findViewById(R.id.textView_hh);
         if (socialgroup != null) {
            name.setText(socialgroup.getGroupName() + "-" + socialgroup.getExtId());
        }else{
            name.setText("Loading...");
        }


        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView_household);
        final IndividualViewAdapter adapter = new IndividualViewAdapter(this, locations, socialgroup,this );
        //final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Individuals in locations
        adapter.filter("", individualViewModel);


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
                    PregnancyFragment.newInstance(individual,locations, socialgroup)).commit();
        });


        final AppCompatButton preg2 = view.findViewById(R.id.pregnancy2);
        preg2.setOnClickListener(v -> {
            //final Pregnancy pregnancy = new Pregnancy();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyExtraFragment.newInstance(individual,locations, socialgroup)).commit();
        });

        final AppCompatButton preg3 = view.findViewById(R.id.pregnancy3);
        preg3.setOnClickListener(v -> {
            //final Pregnancy pregnancy = new Pregnancy();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    Pregnancy3Fragment.newInstance(individual,locations, socialgroup)).commit();
        });


        final AppCompatButton dup = view.findViewById(R.id.dup);
        dup.setOnClickListener(v -> {
            //final Duplicate duplicate = new Duplicate();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DupFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

//        final AppCompatButton finish = view.findViewById(R.id.button_cpvisit);
//        finish.setOnClickListener(view -> {
//            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    ClusterFragment.newInstance(level6Data, locations, socialgroup)).commit();
//        });

        final AppCompatButton demo = view.findViewById(R.id.demographic);
        demo.setOnClickListener(v -> {
            //final Death death = new Death();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DemographicFragment.newInstance(individual,locations, socialgroup)).commit();
        });

        final AppCompatButton reg = view.findViewById(R.id.BTNregistry);
        reg.setOnClickListener(v -> {
            //final Death death = new Death();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    RegisterFragment.newInstance(locations, socialgroup,individual)).commit();
        });
//        demo.setOnClickListener(v -> {
//            DemographicFragment.newInstance(individual, locations, socialgroup)
//                    .show(getChildFragmentManager(), "DemographicFragment");
//        });

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

        final AppCompatButton outcome = view.findViewById(R.id.outcome);
        outcome.setOnClickListener(v -> {
            //final Pregnancyoutcome pregnancyoutcome = new Pregnancyoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BirthFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton outcome2 = view.findViewById(R.id.outcome2);
        outcome2.setOnClickListener(v -> {
            //final Pregnancyoutcome pregnancyoutcome = new Pregnancyoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BirthExtraFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton outcome3 = view.findViewById(R.id.outcome3);
        outcome3.setOnClickListener(v -> {
            //final Pregnancyoutcome pregnancyoutcome = new Pregnancyoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    Birth3Fragment.newInstance(individual, locations, socialgroup)).commit();
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

        final AppCompatButton cghoh = view.findViewById(R.id.hoh);
        cghoh.setOnClickListener(v -> {
           // final Socialgroup socialgroup1 = new Socialgroup();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocialgroupFragment.newInstance(individual, locations, socialgroup)).commit();
        });



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

        try {
            List<Pregnancy> pregnancyList = pregnancyViewModel.retrievePregnancy(socialgroup.getExtId());
            if (pregnancyList != null && !pregnancyList.isEmpty()) {
                showPregnancyDialog();
            }

        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            List<Individual> individualList = individualViewModel.minors(ClusterFragment.selectedLocation.compno,socialgroup.getExtId());
            if (individualList != null && !individualList.isEmpty()) {
                showMinorsDialog();
            }

        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
//                        else if (itemId == R.id.preg){
//                            // open DialogFragment OmgFragment
//                            PregAdapterFragment.newInstance(locations, socialgroup, individual,pregnancy)
//                                    .show(getChildFragmentManager(), "PregAdapterFragment");
//                        }else if (itemId == R.id.outcome){
//                            // open DialogFragment OmgFragment
//                            OutcomeAdapterFragment.newInstance(locations, socialgroup, individual,pregnancyoutcome)
//                                    .show(getChildFragmentManager(), "OutcomeAdapterFragment");
//                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

//        AppCompatButton odkButton = view.findViewById(R.id.odk);
//        odkButton.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setType("vnd.android.cursor.dir/vnd.odk.form");
//            startActivity(intent);
//        });

//        odkButton.setOnClickListener(v -> {
//            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    OdkFragment.newInstance(locations, socialgroup,individual )).commit();
//        });

//        // This callback will intercept the back button press
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                // Disable back button functionality by doing nothing
//                // Or show a Toast message if you want to inform the user
//                Toast.makeText(requireContext(), "Back button is disabled on this screen", Toast.LENGTH_SHORT).show();
//            }
//        };
//        // Add the callback to the activity's OnBackPressedDispatcher
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onIndividualClick(Individual selectedIndividual) {
        HouseMembersFragment.selectedIndividual = selectedIndividual; // Always update the selectedLocation variable
        // Update the householdAdapter with the selected location
        if (selectedIndividual != null) {


                // Check permissions


                // Handle ODK return flow
                OdkUtils.returnToOdk(requireActivity(), selectedIndividual);

                // Set up ODK forms list
                try {
                    List<OdkForm> odkForms = odkViewModel.find();
                    if (odkForms != null && !odkForms.isEmpty()) {
                        recyclerViewOdk.setLayoutManager(new LinearLayoutManager(requireContext()));
                        recyclerViewOdk.setAdapter(new OdkFormAdapter(odkForms));
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.d("HouseMembersFragment", "Error retrieving ODK forms", e);
                    Toast.makeText(requireContext(), "Error retrieving ODK forms", Toast.LENGTH_SHORT).show();
                }


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
            AppCompatButton preg2 = view.findViewById(R.id.pregnancy2);
            AppCompatButton preg3 = view.findViewById(R.id.pregnancy3);
            AppCompatButton outcome = view.findViewById(R.id.outcome);
            AppCompatButton outcome2 = view.findViewById(R.id.outcome2);
            AppCompatButton outcome3 = view.findViewById(R.id.outcome3);
            AppCompatButton amend = view.findViewById(R.id.amend);
            AppCompatButton rel = view.findViewById(R.id.rel);
            AppCompatButton choh = view.findViewById(R.id.hoh);
            AppCompatButton ses = view.findViewById(R.id.ses);
            AppCompatButton vac = view.findViewById(R.id.vac);
            AppCompatButton relhoh = view.findViewById(R.id.relhoh);
            AppCompatButton img = view.findViewById(R.id.img);
            AppCompatButton mor = view.findViewById(R.id.morbidity);
            AppCompatButton reg = view.findViewById(R.id.BTNregistry);
            View id1 = view.findViewById(R.id.id1);
            View id2 = view.findViewById(R.id.id2);
            View id3 = view.findViewById(R.id.id3);
            View id4 = view.findViewById(R.id.id4);
            View id5 = view.findViewById(R.id.id5);
            View id6 = view.findViewById(R.id.id6);
            View id7 = view.findViewById(R.id.id7);
            View id8 = view.findViewById(R.id.id8);
            View id9 = view.findViewById(R.id.id9);
            View id10 = view.findViewById(R.id.id10);
            View id11 = view.findViewById(R.id.id11);
            View id12 = view.findViewById(R.id.id12);
            View id13 = view.findViewById(R.id.id13);
            View id14 = view.findViewById(R.id.id14);
            View id15 = view.findViewById(R.id.id15);
            View id16 = view.findViewById(R.id.id16);
            View id21 = view.findViewById(R.id.id21);
            View id22 = view.findViewById(R.id.id22);

            VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            try {
                Visit data = visitViewModel.find(socialgroup.uuid);

                // Define default texts for each button
                String TextDup = "Duplicate";
                String TextPreg = "Pregnancy (1)";
                String TextDth = "Death";
                String TextOmg = "Outmigration";
                String TextDem = "Demographic";
                String Textpreg2 = "Pregnancy (2)";
                String Textpreg3 = "Pregnancy (3)";
                String TextOutcome = "Pregnancy Outcome (1)";
                String TextOutcome2 = "Pregnancy Outcome (2)";
                String TextOutcome3 = "Pregnancy Outcome (3)";
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
                setButtonEnabled(preg2, !isVisitDataNull);
                setButtonText(preg2, !isVisitDataNull, Textpreg2);
                setButtonEnabled(outcome, !isVisitDataNull);
                setButtonText(outcome, !isVisitDataNull, TextOutcome);
                setButtonEnabled(outcome2, !isVisitDataNull);
                setButtonText(outcome2, !isVisitDataNull, TextOutcome2);
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
                setButtonEnabled(preg3, !isVisitDataNull);
                setButtonText(preg3, !isVisitDataNull, Textpreg3);
                setButtonEnabled(outcome3, !isVisitDataNull);
                setButtonText(outcome3, !isVisitDataNull, TextOutcome3);
                setButtonEnabled(reg, !isVisitDataNull);
                setButtonText(reg, !isVisitDataNull, TextReg);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error retrieving visit data", Toast.LENGTH_SHORT).show();
            }

            DuplicateViewModel dupViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
            try {
                Duplicate data = dupViewModel.find(selectedIndividual.uuid);
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

            try {
                Pregnancy data = c.finds(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(preg2, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(preg2, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            try {
                Pregnancy data = c.find3(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(preg3, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(preg3, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            PregnancyoutcomeViewModel d = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            try {
                Pregnancyoutcome data = d.find1(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(outcome, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(outcome, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            try {
                Pregnancyoutcome data = d.finds(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(outcome2, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(outcome2, false, false);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            try {
                Pregnancyoutcome data = d.finds3(selectedIndividual.uuid);
                if (data != null) {
                    boolean isComplete = data.complete != null && data.complete == 1;
                    boolean isIncomplete = data.complete != null && data.complete == 0;
                    changeDupButtonColor(outcome3, isComplete, isIncomplete);
                } else {
                    changeDupButtonColor(outcome3, false, false);
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


            if(selectedIndividual.gender!=null) {
                dup.setVisibility(View.VISIBLE);
                dem.setVisibility(View.VISIBLE);
                omg.setVisibility(View.VISIBLE);
                dth.setVisibility(View.VISIBLE);
                amend.setVisibility(View.VISIBLE);
                relhoh.setVisibility(View.VISIBLE);
                id1.setVisibility(View.VISIBLE);
                id2.setVisibility(View.VISIBLE);
                id3.setVisibility(View.VISIBLE);
                id4.setVisibility(View.VISIBLE);
                id14.setVisibility(View.VISIBLE);
                id9.setVisibility(View.VISIBLE);
                mor.setVisibility(View.VISIBLE);
                id15.setVisibility(View.VISIBLE);
                reg.setVisibility(View.VISIBLE);
            }

            InmigrationViewModel imgViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
            try {
                Inmigration data = imgViewModel.find(selectedIndividual.uuid,ClusterFragment.selectedLocation.uuid);
                if (data != null) {
                    String TextImg = "Update Inmigration";
                    img.setVisibility(View.VISIBLE);
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

            //Extra Pregnancy Outcome
            PregnancyoutcomeViewModel p = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            try {
                Pregnancy data = c.outcome2(selectedIndividual.uuid);
                if (data != null && selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2) {
                    outcome2.setVisibility(View.VISIBLE);
                    id8.setVisibility(View.VISIBLE);
                } else {
                    outcome2.setVisibility(View.GONE);
                    id8.setVisibility(View.GONE);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

            //Extra Pregnancy Outcome 3
            try {
                Pregnancy data = c.outcome3(selectedIndividual.uuid);
                if (data != null && selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2) {
                    outcome3.setVisibility(View.VISIBLE);
                    id22.setVisibility(View.VISIBLE);
                } else {
                    outcome3.setVisibility(View.GONE);
                    id22.setVisibility(View.GONE);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }

        //Extra Pregnancy
        try {
            Pregnancy data = c.findss(selectedIndividual.uuid);
            if (data != null && selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2) {
                preg2.setVisibility(View.VISIBLE);
                id6.setVisibility(View.VISIBLE);
            } else {
                preg2.setVisibility(View.GONE);
                id6.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
        }

            //Extra Pregnancy 3
            try {
                Pregnancy data = c.finds3(selectedIndividual.uuid);
                if (data != null && selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2) {
                    preg3.setVisibility(View.VISIBLE);
                    id21.setVisibility(View.VISIBLE);
                } else {
                    preg3.setVisibility(View.GONE);
                    id21.setVisibility(View.GONE);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }


            if (selectedIndividual.getAge() >= mage && selectedIndividual.getAge()<= 55 && selectedIndividual.gender==2){
                preg.setVisibility(View.VISIBLE);
                outcome.setVisibility(View.VISIBLE);
                //rel.setVisibility(View.VISIBLE);
                id5.setVisibility(View.VISIBLE);
                id7.setVisibility(View.VISIBLE);
            }else{
                preg.setVisibility(View.GONE);
                outcome.setVisibility(View.GONE);
                //rel.setVisibility(View.GONE);
                id5.setVisibility(View.GONE);
                id7.setVisibility(View.GONE);
            }
            if (selectedIndividual.getAge() >= mage && selectedIndividual.gender==2){
                rel.setVisibility(View.VISIBLE);
                id10.setVisibility(View.VISIBLE);
            }else{
                rel.setVisibility(View.GONE);
                id10.setVisibility(View.GONE);
            }

            if (selectedIndividual.getAge() >= hoh){
                ses.setVisibility(View.VISIBLE);
                choh.setVisibility(View.VISIBLE);
                mor.setVisibility(View.VISIBLE);
                id12.setVisibility(View.VISIBLE);
                id11.setVisibility(View.VISIBLE);
            }else{
                ses.setVisibility(View.GONE);
                choh.setVisibility(View.GONE);
                mor.setVisibility(View.GONE);
                id12.setVisibility(View.GONE);
                id11.setVisibility(View.GONE);
            }


            if (selectedIndividual.getAge() < 5){
                vac.setVisibility(View.VISIBLE);
                id13.setVisibility(View.VISIBLE);
            }else{
                vac.setVisibility(View.GONE);
                id13.setVisibility(View.GONE);
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
                long totalInd = (individualViewModel != null) ? individualViewModel.count(socialgroup != null ? socialgroup.extId : "", ClusterFragment.selectedLocation != null ? ClusterFragment.selectedLocation.compno : "") : 0;
                long totalRegistry = (registryViewModel != null) ? registryViewModel.count(socialgroup != null ? socialgroup.uuid : "") : 0;
                long totalVisit = (visitViewModel != null) ? visitViewModel.count(socialgroup != null ? socialgroup.uuid : "") : 0;
                long cnt = (deathViewModel != null) ? deathViewModel.err(socialgroup != null ? socialgroup.extId : "", ClusterFragment.selectedLocation != null ? ClusterFragment.selectedLocation.compno : "") : 0;
                long err = (individualViewModel != null) ? individualViewModel.err(socialgroup != null ? socialgroup.extId : "", ClusterFragment.selectedLocation != null ? ClusterFragment.selectedLocation.compno : "") : 0;
                long errs = (individualViewModel != null) ? individualViewModel.errs(socialgroup != null ? socialgroup.extId : "", ClusterFragment.selectedLocation != null ? ClusterFragment.selectedLocation.compno : "") : 0;

                handler.post(() -> {
                    if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

                    try {
                        if (cnt > 0) {
                            showToast("Change Head of Household [HOH is Dead]", customToastView, toastMessage);
                        } else if (totalInd > 0 && totalVisit > 0 && totalRegistry <= 0) {
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
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home)); // Original color
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // Original text color
        finish.setEnabled(true);
        finish.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, ClusterFragment.newInstance(level6Data, locations, socialgroup))
                    .commit();
        });
    }



//    private void countRegister() {
//        AppCompatButton finish = view.findViewById(R.id.button_cpvisit);
//
//        try {
//            long totalInd = individualViewModel.count(socialgroup.extId);
//            long totalRegistry = registryViewModel.count(socialgroup.uuid);
//            long totalVisit = visitViewModel.count(socialgroup.uuid);
//
//            if (totalInd > 0 && totalVisit > 0 && totalRegistry <= 0) {
//                // Simulate disabled appearance by changing background color or text color
//                finish.setBackgroundColor(getResources().getColor(R.color.home)); // Replace with your disabled color
//                finish.setTextColor(getResources().getColor(R.color.color_border_lightgray)); // Replace with your disabled text color
//
//                // Set a click listener to show the message without disabling the button
//                finish.setOnClickListener(v -> {
//                    Toast.makeText(requireContext(), "Complete Household Registry Before Exit", Toast.LENGTH_SHORT).show();
//                });
//            } else {
//                // Reset to enabled appearance and functionality
//                finish.setBackgroundColor(getResources().getColor(R.color.home)); // Original color
//                finish.setTextColor(getResources().getColor(R.color.color_border_lightgray)); // Original text color
//                finish.setEnabled(true);
//                finish.setOnClickListener(v -> {
//                    requireActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container_cluster, ClusterFragment.newInstance(level6Data, locations, socialgroup))
//                            .commit();
//                });
//            }
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }



//    private void query() {
//        List<EndEvents> list = new ArrayList<>();
//
//        try {
//            int d = 1;
//            for (Death death : deathViewModel.end(socialgroup.uuid)) {
//                EndEvents endEvent = new EndEvents();
//                endEvent.eventName = "Death ";
//                endEvent.eventId = "" + death.lastName + " " + death.firstName;
//                endEvent.eventDate = "" + death.getInsertDate();
//                endEvent.eventError = "";
//                list.add(endEvent);
//                d++;
//            }
//
//            int a = 1;
//            for (Outmigration e : outmigrationViewModel.end(socialgroup.uuid)) {
//                EndEvents endEvent = new EndEvents();
//                endEvent.eventName = "Outmigration ";
//                endEvent.eventId = "" + e.visit_uuid + " " + e.location_uuid;
//                endEvent.eventDate = "" + e.getInsertDate();
//                endEvent.eventError = "";
//                list.add(endEvent);
//                a++;
//            }
//
//            // Assuming filterAll is a field in your class
//            filterAll = new ArrayList<>(list);
//
//            // Assuming eventsAdapter is a field in your class
//            eventsAdapter = new EndEventsAdapter(requireContext(), this);
//            eventsAdapter.setQueries(list);
//
//            RecyclerView recyclerView = view.findViewById(R.id.recyclerView_end);
//            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//            recyclerView.setAdapter(eventsAdapter);
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private void grantStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                try {
                    if (requireContext().getPackageManager() != null) {
                        String packageName = requireContext().getPackageName();
                        String command = String.format(
                                "pm grant %s android.permission.READ_EXTERNAL_STORAGE",
                                packageName
                        );
                        Runtime.getRuntime().exec(new String[]{"su", "-c", command});

                        // Small delay to allow permission to take effect
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    Log.e("Permission", "Error granting permission: " + e.getMessage());
                }
            }
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