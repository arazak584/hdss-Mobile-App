package org.openhds.hdsscapture.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.EventFormAdapter;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentEventsBinding;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String ARG_LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String ARG_RESIDENCY_ID = "RESIDENCY_ID";
    private static final String ARG_SOCIAL_ID = "SOCIAL_ID";
    private static final String ARG_CASE_ITEM = "ARG_CASE_ITEM";
    private static final String ARG_EVENT_BLOCK_ID = "ARG_EVENT_BLOCK_ID";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private CaseItem caseItem;
    private FragmentEventsBinding binding;
    private boolean isAllFabsVisible;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @param caseItem Parameter 4.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOC_LOCATION_IDS, locations);
        args.putParcelable(ARG_RESIDENCY_ID, residency);
        args.putParcelable(ARG_SOCIAL_ID, socialgroup);
        args.putParcelable(ARG_INDIVIDUAL_ID, individual);
        args.putParcelable(ARG_CASE_ITEM, caseItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(ARG_LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(ARG_RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(ARG_SOCIAL_ID);
            individual = getArguments().getParcelable(ARG_INDIVIDUAL_ID);
            caseItem = getArguments().getParcelable(ARG_CASE_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        // when isAllFabsVisible becomes
        // true make all the action name
        // texts and FABs GONE.
        binding.addCghohFab.hide();
        binding.addOutcomeFab.hide();
        binding.addSesFab.hide();

        // make the boolean variable false
        // as we have set the sub FABs
        // visibility to GONE
        isAllFabsVisible = false;

        binding.addAdhocFab.setOnClickListener(view -> {

        if (!isAllFabsVisible) {

            // when isAllFabsVisible becomes
            // true make all the action name
            // texts and FABs VISIBLE.
//                binding.addAdverseFab.show();
            binding.addCghohFab.show();
            binding.addOutcomeFab.show();
            binding.addSesFab.show();

            // make the boolean variable true as
            // we have set the sub FABs
            // visibility to GONE
            isAllFabsVisible = true;

        } else {

            // when isAllFabsVisible becomes
            // true make all the action name
            // texts and FABs GONE.
//                binding.addAdverseFab.hide();
            binding.addCghohFab.hide();
            binding.addOutcomeFab.hide();
            binding.addSesFab.hide();


            // make the boolean variable false
            // as we have set the sub FABs
            // visibility to GONE
            isAllFabsVisible = false;
        }
        });

        binding.addSesFab.setOnClickListener(view -> {

            HdssSociodemoViewModel viewModel =
                    new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
            int countForms = 0;
            try {
                List<HdssSociodemo> ses = viewModel.find(socialgroup.socialgroup_uuid);
                countForms = ses.size();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final EventForm eventForm = new EventForm(AppConstants.EVENT_SOCIO, countForms, AppConstants.EVENT_DSOCIO, 0, 0);

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioFragment.newInstance(individual,residency, locations, socialgroup,caseItem, eventForm)).commit();

        });

        binding.addOutcomeFab.setOnClickListener(view -> {

            PregnancyoutcomeViewModel viewModel =
                    new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            int countForms = 0;
            try {
                List<Pregnancyoutcome> preg = viewModel.findpreg(individual.individual_uuid);
                countForms = preg.size();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final EventForm eventForm = new EventForm(AppConstants.EVENT_HDSS12, countForms, AppConstants.EVENT_OUTCOMES, 0, 0);

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    Pregnancyoutcome1Fragment.newInstance(individual,residency, locations, socialgroup,caseItem, eventForm)).commit();

        });

        binding.addCghohFab.setOnClickListener(view -> {

            SocialgroupViewModel viewModel =
                    new ViewModelProvider(this).get(SocialgroupViewModel.class);
            int countForms = 0;
            try {
                List<Socialgroup> socialgroups = viewModel.findhse(socialgroup.houseExtId);
                countForms = socialgroups.size();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final EventForm eventForm = new EventForm(AppConstants.EVENT_HDSS12, countForms, AppConstants.EVENT_OUTCOMES, 0, 0);

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocialgroupFragment.newInstance(individual,residency, locations, socialgroup,caseItem, eventForm)).commit();

        });


        binding.addMenuFab.setOnClickListener(view -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(individual, residency, locations, socialgroup)).commit();
        });

        final SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        //final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-ddd", Locale.US);

        int ageyrs = 0;

        try {
            Date dobdif = f.parse(String.valueOf(individual.dob));
            long difference = Math.abs(dobdif.getTime() - new Date().getTime());
            ageyrs = (int) (difference / (1000 * 60 * 60 * 24 * 365.25));
            System.out.println("dobParse: " + dobdif);
            System.out.println("agediff: " + difference);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("dob: " + individual.dob);
        System.out.println("age: " + individual.age);
        System.out.println("ageyrs: " + ageyrs);


        final int yrs = individual.age + (int) ageyrs;

        final String message =
                (individual.gender==2 && yrs>=12? "Adult Female" : ((individual.gender == 1 && yrs>=12? "Adult Male" : "")))
                        + (yrs >=2 && yrs<12 ? "Child" : (yrs <2 ? "Infant":""))
                        + " --Complete All Events" ;


        //binding.textViewScreenId.setText(individual.extId);
        binding.textViewLastEventInfo.setText(message);

        List<EventForm> eventForms = new ArrayList<>();



        if (individual.extId != null && individual.gender != null) {


            if (individual.dob != null) {

                            //Adult Male
                            if (individual.gender==1 && yrs>=12) {
                                showDemographicForm(eventForms);
                                showResidencyForm(eventForms);

                            }

                            //Adult Female
                            if (individual.gender==2 && yrs>=12) {
                                showDemographicForm(eventForms);
                                showRelationshipForm(eventForms);
                                showPregnancyForm(eventForms);
                                showOutcomeForm(eventForms);
                                showOutcome1Form(eventForms);
                                showResidencyForm(eventForms);

                            }

                            //Child
                            if (yrs <=11) {

                                showDemographicForm(eventForms);
                                showResidencyForm(eventForms);

                            }

                            //Adult
                            if (yrs >= 14) {
                                showSocialgroupForm(eventForms);
                                showSocioForm(eventForms);
                            }

                            }


        }




        final View view = binding.getRoot();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_events);
        EventFormAdapter adapter = new EventFormAdapter(locations, socialgroup,residency, individual,caseItem, eventForms, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        return view;

    }

    private void showSocialgroupForm(List<EventForm> eventForms) {
        SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        try {
            Socialgroup form = viewModel.find(socialgroup.socialgroup_uuid);
            if (form == null) {
                form = new Socialgroup();
            }

                eventForms.add(new EventForm(AppConstants.EVENT_HDSS3, AppConstants.EVENT_HOUSEHOLD, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showDemographicForm(List<EventForm> eventForms) {
        DemographicViewModel viewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        try {
            Demographic form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Demographic();
            }
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS4, AppConstants.EVENT_DEMO, form.complete));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showSocioForm(List<EventForm> eventForms) {
        HdssSociodemoViewModel viewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        try {
            HdssSociodemo form = viewModel.findses(socialgroup.socialgroup_uuid);
            if (form == null) {
                form = new HdssSociodemo();
            }
            eventForms.add(new EventForm(AppConstants.EVENT_SOCIO, AppConstants.EVENT_DSOCIO, form.complete));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showRelationshipForm(List<EventForm> eventForms) {
        RelationshipViewModel viewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        try {
            Relationship form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Relationship();
            }
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS7, AppConstants.EVENT_RELATIONSHIP, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showResidencyForm(List<EventForm> eventForms) {
        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Residency form = viewModel.findRes(individual.individual_uuid);
            if (form == null) {
                form = new Residency();
            }
            eventForms.add(new EventForm(AppConstants.EVENT_HDSS1, AppConstants.EVENT_RESIDENCY, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showOutcomeForm(List<EventForm> eventForms) {
        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        try {
            Pregnancyoutcome form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Pregnancyoutcome();
            }

            eventForms.add(new EventForm(AppConstants.EVENT_HDSS11, AppConstants.EVENT_OUTCOME, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showOutcome1Form(List<EventForm> eventForms) {
        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        try {
            Pregnancyoutcome form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Pregnancyoutcome();
            }

            eventForms.add(new EventForm(AppConstants.EVENT_HDSS12, AppConstants.EVENT_OUTCOMES, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showPregnancyForm(List<EventForm> eventForms) {
        PregnancyViewModel viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Pregnancy();
            }

                eventForms.add(new EventForm(AppConstants.EVENT_HDSS10, AppConstants.EVENT_OBSERVATION, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


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