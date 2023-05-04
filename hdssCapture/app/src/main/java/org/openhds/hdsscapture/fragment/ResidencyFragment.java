package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.HouseholdDialogFragment;
import org.openhds.hdsscapture.Dialog.MoveInErrorFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentMembershipBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

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
 * Use the {@link ResidencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidencyFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "RESIDENCY.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentMembershipBinding binding;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Visit visit;

    public ResidencyFragment() {
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
     * @param caseItem parameter 5.
     * @param eventForm Parameter 7.
     * @return A new instance of fragment ResidencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidencyFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup,CaseItem caseItem, EventForm eventForm) {
        ResidencyFragment fragment = new ResidencyFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(CASE_ID, caseItem);
        args.putParcelable(EVENT_ID, eventForm);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            caseItem = getArguments().getParcelable(CASE_ID);
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMembershipBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        // Find the button view
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_hh);

        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                HouseholdDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "HouseholdDialogFragment");
            }
        });


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.editTextStartDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.ENDDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.ENDDATE.getBundleKey());
                binding.editTextEndDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DEATHDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DEATHDATE.getBundleKey());
                binding.dth.dthDeathDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.IMGDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.IMGDATE.getBundleKey());
                binding.img.imgDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.OMGDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.OMGDATE.getBundleKey());
                binding.omg.omgDate.setText(result);
            }

        });

        binding.buttonResidencyStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.STARTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonResidencyEndDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.ENDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.dth.buttonDeathDod.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DEATHDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.img.buttonImgImgDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.IMGDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.omg.buttonOmgImgDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.OMGDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        ResidencyViewModel res = new ViewModelProvider(this).get(ResidencyViewModel.class);
        DeathViewModel deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        try {
            Residency data = viewModel.findRes(individual.individual_uuid);

            String img = UUID.randomUUID().toString();
            String img_uuid = img.toString().replaceAll("-", "");

            String omg = UUID.randomUUID().toString();
            String omg_uuid = omg.toString().replaceAll("-", "");

            if (data != null) {
                binding.setResidency(data);
                data.loc = locations.getLocation_uuid();
                data.dobs = individual.dob;
                //data.img = 2;
            } else {
                data = new Residency();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.residency_uuid = uuidString;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.location_uuid = locations.getLocation_uuid();
                data.socialgroup_uuid = socialgroup.socialgroup_uuid;
                data.loc = locations.getLocation_uuid();
                data.complete = 1;
                data.dobs = individual.dob;

//                if (res != null) {
//                    data.startDate = binding.getRes().endDate;
//                }
                if (data != null){
                    data.startType=1;
                }
                if (data!=null){
                    data.img=1;
                }
                if (data != null && data.endDate != null) {
                    try {
                        // Parse the endDate into a Date object
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date endDate = sdf.parse(String.valueOf(data.endDate));

                        // Add one day to the endDate
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        Log.d("TAG", "Formatted End Date: " + endDate);

                        // Format the startDate as a string
                        Date startDate = cal.getTime();
                        String startDateString = sdf.format(startDate);
                        Log.d("TAG", "Formatted Start Date: " + startDateString);
                        // Set the startDate of the data object
                        data.startDate = startDate;

                        // Set the text of the TextView to the formatted startDate
                        binding.editTextStartDate.setText(startDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }




                binding.setResidency(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Death data = deathViewModel.find(individual.individual_uuid);
            if (data != null) {
//                data.visit_uuid = socialgroup.getVisit_uuid();
//                data.img_uuid = img_uuid;
//                data.omg_uuid = omg_uuid;
                binding.setDeath(data);
            } else {
                data = new Death();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.death_uuid = uuidString;
                data.insertDate = new Date();
                data.dob = individual.dob;
                data.firstName = individual.getFirstName();
                data.lastName = individual.getLastName();
                data.gender = individual.getGender();
                data.compno = locations.getCompno();
                data.extId = individual.getExtId();
                data.compname = locations.getLocationName();
                data.individual_uuid = individual.getIndividual_uuid();
                data.villname = level5Data.getName();
                data.villcode = level5Data.getVillcode();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.deathDate = binding.getResidency().endDate;
                data.vpmcomplete=1;
                data.complete = 1;


                binding.setDeath(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Inmigration data = inmigrationViewModel.find(individual.individual_uuid);
            if (data != null) {
                binding.setInmigration(data);
            } else {
                data = new Inmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                data.img_uuid=uuidString;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.residency_uuid = binding.getResidency().residency_uuid;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.recordedDate = binding.getResidency().startDate;
                if (data!=null){
                    data.migType=2;
                }
                data.complete = 1;


                binding.setInmigration(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Outmigration data = outmigrationViewModel.find(individual.individual_uuid);
            if (data != null) {
                binding.setOutmigration(data);
            } else {
                data = new Outmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                data.omg_uuid=uuidString;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.residency_uuid = binding.getResidency().residency_uuid;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.recordedDate = binding.getResidency().endDate;
                data.complete = 1;


                binding.setOutmigration(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Residency data = res.findRes(individual.individual_uuid);
            if (data != null) {
                binding.setRes(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        loadCodeData(binding.membershipComplete, "complete");
        loadCodeData(binding.starttype, "startType");
        loadCodeData(binding.endtype,  "endType");
        loadCodeData(binding.rltnHead,  "rltnhead");
        loadCodeData(binding.residencyImg,  "complete");
        loadCodeData(binding.img.reason,  "reason");
        loadCodeData(binding.omg.reasonOut,  "reason");
        loadCodeData(binding.img.origin,  "whereoutside");
        loadCodeData(binding.img.migtype,  "migType");
        loadCodeData(binding.omg.destination,  "whereoutside");
        loadCodeData(binding.dth.dthDeathPlace, "deathPlace");
        loadCodeData(binding.dth.dthDeathCause, "deathCause");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, deathViewModel,inmigrationViewModel,outmigrationViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, deathViewModel,inmigrationViewModel,outmigrationViewModel);
        });

        binding.setEventname(AppConstants.EVENT_RESIDENCY);
        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ResidencyViewModel viewModel, DeathViewModel deathViewModel, InmigrationViewModel inmigrationViewModel,OutmigrationViewModel outmigrationViewModel) {

        if (save) {
            Residency finalData = binding.getResidency();

            boolean isOmg = false;
            boolean dthdate = false;
            boolean imgdate = false;
            boolean omgdate = false;
            boolean sdate = false;


            //Log.d("DEBUG", "locationExtid: " + binding.locationExtid);
            //Log.d("DEBUG", "currentLoc: " + binding.currentLoc);
            if (!binding.locationExtid.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim())) {
                isOmg = true;
                binding.currentLoc.setError("Move Individual Out of His/Her Previous Compound");
            }

            if(isOmg){//if there is an error, do not continue
                MoveInErrorFragment dialog = MoveInErrorFragment.newInstance(individual,residency,locations,socialgroup);
                dialog.show(requireActivity().getSupportFragmentManager(), "dialog");
                return;
            }



            //Date Validations

            try {
                if (!binding.currentdob.getText().toString().trim().isEmpty() && !binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.currentdob.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Date of Birth");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Date of Birth", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            try {
                if (!binding.editTextStartDate.getText().toString().trim().isEmpty() && !binding.res.resEndDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.res.resEndDate.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Previous End Date" + edate);
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Previous End Date" + edate, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (edate.equals(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Previous End Date" + edate);
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Previous End Date" + edate, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            try {
                if (!binding.editTextEndDate.getText().toString().trim().isEmpty() && !binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.editTextEndDate.getText().toString().trim());
                    if (edate.after(currentDate)) {
                        binding.editTextEndDate.setError("End Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "End Date Cannot Be a Future Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (edate.before(stdate)) {
                        binding.editTextEndDate.setError("End Date Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "End Date Cannot Be Less than Start Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextEndDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }





            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }


            if (finalData.individual_uuid != null) {

                if (finalData.endType == 3) {



                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.dth.MAINLAYOUT, validateOnComplete, false);

                    if (!binding.editTextEndDate.getText().toString().trim().equals(binding.dth.dthDeathDate.getText().toString().trim())) {
                        dthdate = true;
                        binding.dth.dthDeathDate.setError("End Date Not Equal to Date of Death");
                        Toast.makeText(getActivity(), "End Date Not Equal to Date of Death", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Death dth = binding.getDeath();
                    dth.complete = 1;
                    deathViewModel.add(dth);
                }

                if (finalData.startType == 1 && binding.getResidency().img==1) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.img.MAINLAYOUT, validateOnComplete, false);

                    if (!binding.editTextStartDate.getText().toString().trim().equals(binding.img.imgDate.getText().toString().trim())) {
                        imgdate = true;
                        binding.img.imgDate.setError("Migration Date Not Equal to Start Date");
                        Toast.makeText(getActivity(), "Migration Date Not Equal to Start Date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Inmigration img = binding.getInmigration();
                    img.complete = 1;
                    inmigrationViewModel.add(img);

                }

                if (finalData.endType == 2) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.omg.MAINLAYOUT, validateOnComplete, false);

                    if (!binding.editTextEndDate.getText().toString().trim().equals(binding.omg.omgDate.getText().toString().trim())) {
                        omgdate = true;
                        binding.omg.omgDate.setError("End Date Not Equal to Date of Outmigration");
                        Toast.makeText(getActivity(), "End Date Not Equal to Date of Outmigration", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Outmigration omg = binding.getOutmigration();
                    omg.complete = 1;
                    outmigrationViewModel.add(omg);

                }


            }
            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
        }
    }


    private <T> void callable(Spinner spinner, T[] array) {

        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void loadCodeData(Spinner spinner, final String codeFeature) {
        final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
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
        INSERTDATE ("INSERTDATE"),
        STARTDATE ("STARTDATE"),
        DEATHDATE("DEATHDATE"),
        IMGDATE("IMGDATE"),
        OMGDATE("OMGDATE"),
        ENDDATE ("ENDDATE");

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