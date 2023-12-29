package org.openhds.hdsscapture.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.EndEventsAdapter;
import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseMembersBinding;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subqueries.EndEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseMembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseMembersFragment extends Fragment implements IndividualViewAdapter.IndividualClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "LOCATION.TAG";


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

    public interface IndividualClickListener {
        void onIndividualClick(Individual selectedIndividual);
    }

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
        query();

        //final TextView hh = view.findViewById(R.id.textView_compextId);
        final TextView name = view.findViewById(R.id.textView_hh);

        if (socialgroup != null) {
            name.setText(socialgroup.getGroupName());
        }else{
            name.setText("Loading...");
        }


        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView_household);
        final IndividualViewAdapter adapter = new IndividualViewAdapter(this, locations, socialgroup,this );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

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
        SocialgroupViewModel smodel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        try {
            Socialgroup data = smodel.minor(socialgroup.uuid);
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


        final AppCompatButton dup = view.findViewById(R.id.dup);
        dup.setOnClickListener(v -> {
            //final Duplicate duplicate = new Duplicate();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DupFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

        final AppCompatButton finish = view.findViewById(R.id.button_cpvisit);
        finish.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    ClusterFragment.newInstance(level6Data, locations, socialgroup)).commit();
        });

        final AppCompatButton demo = view.findViewById(R.id.demographic);
        demo.setOnClickListener(v -> {
            DemographicFragment.newInstance(individual, locations, socialgroup)
                    .show(getChildFragmentManager(), "DemographicFragment");
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

        final AppCompatButton outcome = view.findViewById(R.id.outcome);
        outcome.setOnClickListener(v -> {
            //final Pregnancyoutcome pregnancyoutcome = new Pregnancyoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyoutcomeFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        final AppCompatButton outcome2 = view.findViewById(R.id.outcome2);
        outcome2.setOnClickListener(v -> {
            //final Pregnancyoutcome pregnancyoutcome = new Pregnancyoutcome();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    Pregnancyoutcome1Fragment.newInstance(individual, locations, socialgroup)).commit();
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

        final AppCompatButton ses = view.findViewById(R.id.ses);
        ses.setOnClickListener(v -> {
            //final HdssSociodemo hdssSociodemo = new HdssSociodemo();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioFragment.newInstance(individual, locations, socialgroup)).commit();
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

//        final AppCompatButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
//        add_individual.setOnClickListener(v -> {
//
//            final Individual individual = new Individual();
//
//            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    IndividualFragment.newInstance(individual,residency, locations, socialgroup)).commit();
//        });

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




        return view;

    }

    @Override
    public void onIndividualClick(Individual selectedIndividual) {
        HouseMembersFragment.selectedIndividual = selectedIndividual; // Always update the selectedLocation variable
        // Update the householdAdapter with the selected location
        if (selectedIndividual != null) {

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
            AppCompatButton outcome = view.findViewById(R.id.outcome);
            AppCompatButton outcome2 = view.findViewById(R.id.outcome2);
            AppCompatButton amend = view.findViewById(R.id.amend);
            AppCompatButton rel = view.findViewById(R.id.rel);
            AppCompatButton choh = view.findViewById(R.id.hoh);
            AppCompatButton ses = view.findViewById(R.id.ses);
            AppCompatButton vac = view.findViewById(R.id.vac);
            AppCompatButton relhoh = view.findViewById(R.id.relhoh);
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

            VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            try {
                Visit data = visitViewModel.find(socialgroup.uuid);

                // Define default texts for each button
                String TextDup = "Duplicate";
                String TextPreg = "Pregnancy";
                String TextDth = "Death";
                String TextOmg = "Outmigration";
                String TextDem = "Demographic";
                String Textpreg2 = "Pregnancy (2)";
                String TextOutcome = "Pregnancy Outcome";
                String TextOutcome2 = "Pregnancy Outcome (2)";
                String TextAmend = "Amendment";
                String TextRel = "Relationship";
                String TextChoh = "Change Household Head";
                String TextSes = "Socio-Economic Status";
                String TextVac = "Vaccination";
                String TextRelhoh = "Change Household";

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

            PregnancyoutcomeViewModel d = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            try {
                Pregnancyoutcome data = d.find(selectedIndividual.uuid);
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
            }

            //Extra Pregnancy Outcome
            PregnancyoutcomeViewModel p = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            try {
                Pregnancyoutcome data = p.findout(selectedIndividual.uuid);
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
                id12.setVisibility(View.VISIBLE);
                id11.setVisibility(View.VISIBLE);
            }else{
                ses.setVisibility(View.GONE);
                choh.setVisibility(View.GONE);
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


    public void dismissLoadingDialog() {
        if (progres != null) {
            progres.dismiss();
        }
    }

    public void dismissLoadingDialogs() {
        if (progress != null) {
            progress.dismiss();
        }
    }


    private void query() {
        List<EndEvents> list = new ArrayList<>();

        try {
            int d = 1;
            for (Death death : deathViewModel.end(socialgroup.uuid)) {
                EndEvents endEvent = new EndEvents();
                endEvent.eventName = "Death ";
                endEvent.eventId = "" + death.lastName + " " + death.firstName;
                endEvent.eventDate = "" + death.getInsertDate();
                endEvent.eventError = "";
                list.add(endEvent);
                d++;
            }

            int a = 1;
            for (Outmigration e : outmigrationViewModel.end(socialgroup.uuid)) {
                EndEvents endEvent = new EndEvents();
                endEvent.eventName = "Outmigration ";
                endEvent.eventId = "" + e.visit_uuid + " " + e.location_uuid;
                endEvent.eventDate = "" + e.getInsertDate();
                endEvent.eventError = "";
                list.add(endEvent);
                a++;
            }

            // Assuming filterAll is a field in your class
            filterAll = new ArrayList<>(list);

            // Assuming eventsAdapter is a field in your class
            eventsAdapter = new EndEventsAdapter(requireContext(), this);
            eventsAdapter.setQueries(list);

            RecyclerView recyclerView = view.findViewById(R.id.recyclerView_end);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(eventsAdapter);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
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