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
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.databinding.FragmentEventsBinding;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
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
    private Configsettings configsettings;
    private FragmentEventsBinding binding;

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
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOC_LOCATION_IDS, locations);
        args.putParcelable(ARG_RESIDENCY_ID, residency);
        args.putParcelable(ARG_SOCIAL_ID, socialgroup);
        args.putParcelable(ARG_INDIVIDUAL_ID, individual);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        binding.addMenuFab.setOnClickListener(view -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
        });

        ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;

        try {
            configsettings = viewModel.findAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        int yrss = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).hoh_age : 0;
        int mage = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).mother_age : 0;
        int fage = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).father_age : 0;
        Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;

        //final SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        int ageyrs = 0;

        if (individual != null) {
            try {
                Date dobdif = f.parse(individual.getDob());
                System.out.println("dateob: " + dobdif);
                long difference = Math.abs(dobdif.getTime() - new Date().getTime());
                ageyrs = (int) (difference / (1000 * 60 * 60 * 24 * 365.25));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("dob: " + individual.dob);
//        System.out.println("age: " + individual.age);
//        System.out.println("ageyrs: " + ageyrs);
//        System.out.println("HOH-YRS: " + yrss);
//        System.out.println("EARLIEST-DATE: " + dt);
        List<EventForm> eventForms = new ArrayList<>();
        if (individual != null) {
            final int yrs = individual.age + (int) ageyrs;

            final String message =
                    (individual.gender == 2 && yrs >= 12 ? "Adult Female" : ((individual.gender == 1 && yrs >= 12 ? "Adult Male" : "")))
                            + (yrs >= 2 && yrs < 12 ? "Child" : (yrs < 2 ? "Infant" : ""))
                            + " --Complete All Events";


            //binding.textViewScreenId.setText(individual.extId);
            binding.textViewLastEventInfo.setText(message);




            if (individual.extId != null && individual.gender != null) {


                if (individual.dob != null) {

                    //Adult Male
                    if (individual.gender == 1 && yrs >= fage) {
                        showDemographicForm(eventForms);
                        showResidencyForm(eventForms);
                        showAmendment(eventForms);
                        showDup(eventForms);

                    }

                    //Adult Female 56 and above
                    if (individual.gender == 2 && yrs > 55) {
                        showDemographicForm(eventForms);
                        showRelationshipForm(eventForms);
                        showResidencyForm(eventForms);
                        showAmendment(eventForms);
                        showDup(eventForms);

                    }

                    //Adult Female
                    if (individual.gender == 2 && yrs >= mage && yrs <= 55) {
                        showDemographicForm(eventForms);
                        showRelationshipForm(eventForms);
                        showPregnancyForm(eventForms);
                        showPregnancyFormExtra(eventForms);
                        showOutcomeForm(eventForms);
                        showOutcome1Form(eventForms);
                        showResidencyForm(eventForms);
                        showAmendment(eventForms);
                        showDup(eventForms);

                    }

                    //Child
                    if (yrs <= 11) {

                        showDemographicForm(eventForms);
                        showResidencyForm(eventForms);
                        showAmendment(eventForms);
                        showDup(eventForms);

                    }

                    //Adult
                    if (yrs >= yrss) {
                        showSocialgroupForm(eventForms);
                        showSocioForm(eventForms);
                    }

                    //Vaccination
                    if (yrs < 5) {
                        showVaccination(eventForms);
                    }

                }


            }


        }

        final View view = binding.getRoot();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_events);
        EventFormAdapter adapter = new EventFormAdapter(locations, socialgroup,residency, individual,eventForms, this);
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
            Socialgroup form = viewModel.findhse(socialgroup.uuid);
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
            Demographic form = viewModel.find(individual.uuid);
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
            HdssSociodemo form = viewModel.findses(socialgroup.uuid);
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
            Relationship form = viewModel.find(individual.uuid);
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
            Residency form = viewModel.findRes(individual.uuid,locations.uuid);
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
            Pregnancyoutcome form = viewModel.find(individual.uuid);
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
            Pregnancyoutcome prev = viewModel.findout(individual.uuid);
            if (prev != null && prev.extra!=null) {
                Pregnancyoutcome extra = viewModel.finds(individual.uuid);
                if (extra == null) {
                    extra = new Pregnancyoutcome();
                }

                eventForms.add(new EventForm(AppConstants.EVENT_HDSS12, AppConstants.EVENT_OUTCOMES, extra.complete));
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
            Pregnancy form = viewModel.find(individual.uuid);
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

    private void showPregnancyFormExtra(List<EventForm> eventForms) {
        PregnancyViewModel viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy previous = viewModel.findss(individual.uuid);
            if (previous != null && previous.extra!=null) {
                Pregnancy extra = viewModel.finds(individual.uuid);
                if (extra == null) {
                    extra = new Pregnancy();
                }
                eventForms.add(new EventForm(AppConstants.EVENT_HDSS13, AppConstants.EVENT_OBSERVATION, extra.complete));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showAmendment(List<EventForm> eventForms) {
        AmendmentViewModel viewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
        try {
            Amendment form = viewModel.find(individual.uuid);
            if (form == null) {
                form = new Amendment();
            }
            eventForms.add(new EventForm(AppConstants.EVENT_HDSS14, AppConstants.EVENT_AMEND, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showVaccination(List<EventForm> eventForms) {
        VaccinationViewModel viewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        try {
            Vaccination form = viewModel.find(individual.uuid);
            if (form == null) {
                form = new Vaccination();
            }
            eventForms.add(new EventForm(AppConstants.EVENT_HDSS15, AppConstants.EVENT_VAC, form.complete));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showDup(List<EventForm> eventForms) {
        DuplicateViewModel viewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
        try {
            Duplicate form = viewModel.find(individual.uuid);
            if (form == null) {
                form = new Duplicate();
            }
            eventForms.add(new EventForm(AppConstants.EVENT_HDSS16, AppConstants.EVENT_DUP, form.complete));

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