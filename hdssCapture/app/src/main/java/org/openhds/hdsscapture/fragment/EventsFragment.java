package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.EventFormAdapter;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentEventsBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
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

    private Location location;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private CaseItem caseItem;
    private FragmentEventsBinding binding;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param location Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @param caseItem Parameter 4.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup,CaseItem caseItem) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOC_LOCATION_IDS, location);
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
            location = getArguments().getParcelable(ARG_LOC_LOCATION_IDS);
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


        final TextView fname = binding.getRoot().findViewById(R.id.textView_fname);
        final TextView lname = binding.getRoot().findViewById(R.id.textView_lname);
        final TextView hhid = binding.getRoot().findViewById(R.id.textView_hhid);
        final TextView permid = binding.getRoot().findViewById(R.id.textView_permid);
        final TextView dob = binding.getRoot().findViewById(R.id.textView_dob);
        final TextView age = binding.getRoot().findViewById(R.id.textView_age);

        fname.setText(individual.getFirstName());
        lname.setText(individual.getLastName());
        permid.setText(individual.getExtId());
        dob.setText(individual.getDob());
        hhid.setText(individual.houseExtId);
        age.setText(String.valueOf(individual.getAge()));

        binding.addMenuFab.setOnClickListener(view -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseVisitFragment.newInstance(individual, residency, location, socialgroup)).commit();
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
                                showResidencyForm(eventForms);
                                showDeathForm(eventForms);
                                showDemographicForm(eventForms);
                                showOutmigrationForm(eventForms);
                                showInmigrationForm(eventForms);

                            }

                            //Adult Female
                            if (individual.gender==2 && yrs>=12) {
                                showResidencyForm(eventForms);
                                showDeathForm(eventForms);
                                showDemographicForm(eventForms);
                                showRelationshipForm(eventForms);
                                showOutmigrationForm(eventForms);
                                showInmigrationForm(eventForms);
                                showPregnancyForm(eventForms);

                            }

                            //Child
                            if (yrs <=11) {

                                showResidencyForm(eventForms);
                                showDeathForm(eventForms);
                                showDemographicForm(eventForms);
                                showOutmigrationForm(eventForms);
                                showInmigrationForm(eventForms);

                            }

                            //Adult
                            if (yrs >= 14) {
                                showVisitForm(eventForms);
                                showSocialgroupForm(eventForms);

                            }

                            }


        }




        final View view = binding.getRoot();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_events);
        EventFormAdapter adapter = new EventFormAdapter(location, socialgroup,residency, individual,caseItem, eventForms, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        return view;

    }

    private void showResidencyForm(List<EventForm> eventForms) {
        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Residency form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Residency();
            }
            if (form.complete ==0 ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS1, AppConstants.EVENT_RESIDENCY, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showVisitForm(List<EventForm> eventForms) {
        VisitViewModel viewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Visit();
            }
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS2, AppConstants.EVENT_VISIT, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showDeathForm(List<EventForm> eventForms) {
        DeathViewModel viewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        try {
            Death form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Death();
            }
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS6, AppConstants.EVENT_DEATH, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showSocialgroupForm(List<EventForm> eventForms) {
        SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        try {
            Socialgroup form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Socialgroup();
            }
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS3, AppConstants.EVENT_HOUSEHOLD, form.complete));
            }
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
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS4, AppConstants.EVENT_DEMO, form.complete));
            }
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
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS7, AppConstants.EVENT_RELATIONSHIP, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showOutmigrationForm(List<EventForm> eventForms) {
        OutmigrationViewModel viewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        try {
            Outmigration form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Outmigration();
            }
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS8, AppConstants.EVENT_OMG, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showInmigrationForm(List<EventForm> eventForms) {
        InmigrationViewModel viewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        try {
            Inmigration form = viewModel.find(individual.individual_uuid);
            if (form == null) {
                form = new Inmigration();
            }
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS9, AppConstants.EVENT_IMG, form.complete));
            }
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
            if (form.complete == null ) {
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS10, AppConstants.EVENT_OBSERVATION, form.complete));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}