package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentMembershipBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;
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
    private ProgressDialog progressDialog;


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
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Households...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);

                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);

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
        ResidencyViewModel resViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        ResidencyViewModel esViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        ResidencyViewModel eviewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        DeathViewModel deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        try {
            Residency dataRes = viewModel.findRes(individual.uuid, locations.uuid);

            if (dataRes != null) {
                binding.setResidency(dataRes);
                dataRes.loc = locations.getUuid();
                dataRes.dobs = individual.dob;
                if(dataRes.img ==null){
                    dataRes.img = 2;
                }
                binding.starttype.setEnabled(false);
                binding.editTextStartDate.setEnabled(false);
                binding.residencyImg.setEnabled(false);
                binding.buttonResidencyStartDate.setEnabled(false);
            } else {
                dataRes = new Residency();
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                dataRes.fw_uuid = fieldworkerData.getFw_uuid();
                dataRes.uuid = uuidString;
                dataRes.individual_uuid = individual.getUuid();
                dataRes.location_uuid = locations.getUuid();
                dataRes.socialgroup_uuid = socialgroup.uuid;
                dataRes.loc = locations.getUuid();
                dataRes.complete = 1;
                dataRes.dobs = individual.dob;
                dataRes.age = individual.getAge();
                dataRes.startType = 1;
                binding.starttype.setEnabled(false);

                if (dataRes!=null){
                    dataRes.img=1;
                }else{dataRes.img=2;}

                binding.setResidency(dataRes);
                binding.getResidency().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.residencyImg.setEnabled(false);


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Residency datas = resViewModel.finds(individual.uuid);
            if (datas != null) {
                binding.setRes(datas);
                binding.starttype.setEnabled(false);
                if (binding.getResidency().startDate == null) {
                    Calendar calendar = Calendar.getInstance(Locale.US);
                    calendar.setTime(datas.endDate);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    binding.getResidency().setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                    binding.getResidency().startType= 1;
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Residency datae = esViewModel.findEnd(individual.uuid, locations.uuid);
            if (datae != null) {
                binding.setOmgg(datae);
                if (binding.getOmgg().old_residency == null) {
                    binding.getOmgg().loc = datae.getLocation_uuid();
                    binding.getOmgg().old_residency = datae.getUuid();
                    binding.getOmgg().startDate = datae.startDate;
                    binding.getResidency().old_residency =datae.getUuid();
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Death datadth = deathViewModel.find(individual.uuid);
            if (datadth != null) {
                binding.setDeath(datadth);
            } else {
                datadth = new Death();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                datadth.fw_uuid = fieldworkerData.getFw_uuid();
                datadth.uuid = uuidString;
                datadth.dob = individual.dob;
                datadth.firstName = individual.getFirstName();
                datadth.lastName = individual.getLastName();
                datadth.gender = individual.getGender();
                datadth.compno = locations.getCompno();
                datadth.extId = individual.getExtId();
                datadth.compname = locations.getLocationName();
                datadth.individual_uuid = individual.getUuid();
                datadth.villname = level5Data.getName();
                datadth.villcode = level5Data.getExtId();
                datadth.visit_uuid = socialgroup.getVisit_uuid();
                datadth.deathDate = binding.getResidency().endDate;
                datadth.vpmcomplete=1;
                datadth.complete = 1;
                datadth.househead = socialgroup.getGroupName();


                binding.setDeath(datadth);
                binding.getDeath().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Residency datas = eviewModel.amend(individual.uuid);
            if (datas != null) {
                binding.setMig(datas);

            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Inmigration dataimg = inmigrationViewModel.find(individual.uuid);
            if (dataimg != null && binding.getResidency().rltn_head !=null) {
                binding.setInmigration(dataimg);
                binding.img.migtype.setEnabled(false);
            } else {
                dataimg = new Inmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                dataimg.uuid=uuidString;
                dataimg.fw_uuid = fieldworkerData.getFw_uuid();
                dataimg.residency_uuid = binding.getResidency().uuid;
                dataimg.individual_uuid = individual.getUuid();
                dataimg.visit_uuid = socialgroup.getVisit_uuid();
                dataimg.recordedDate = binding.getResidency().startDate;
                dataimg.complete = 1;

                if (binding.getMig() == null || binding.getMig().individual_uuid == null) {
                    if (dataimg.migType == null) {
                        dataimg.migType = 1;
                    }
                } else {
                    if (dataimg.migType == null) {
                        dataimg.migType = 2;
                    }
                }

                binding.setInmigration(dataimg);


                binding.img.migtype.setEnabled(false);

                binding.getInmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Outmigration data = outmigrationViewModel.find(individual.uuid);
            if (data != null) {
                binding.setOutmigration(data);
            } else {
                data = new Outmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                data.uuid=uuidString;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.residency_uuid = binding.getResidency().uuid;
                data.individual_uuid = individual.getUuid();
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.recordedDate = binding.getResidency().endDate;
                data.complete = 1;


                binding.setOutmigration(data);
                binding.getOutmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        loadCodeData(binding.membershipComplete, "submit");
        loadCodeData(binding.starttype, "startType");
        loadCodeData(binding.endtype,  "endType");
        loadCodeData(binding.rltnHead,  "rltnhead");
        loadCodeData(binding.residencyImg,  "complete");
        loadCodeData(binding.img.reason,  "reason");
        loadCodeData(binding.omg.reasonOut,  "reasonForOutMigration");
        loadCodeData(binding.img.origin,  "comingfrom");
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
//            if (!binding.locationExtid.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim())) {
//                isOmg = true;
//                binding.currentLoc.setError("Move Individual Out of His/Her Previous Compound");
//            }
//
//            if(isOmg){//if there is an error, do not continue
//                MoveInErrorFragment dialog = MoveInErrorFragment.newInstance(individual,residency,locations,socialgroup);
//                dialog.show(requireActivity().getSupportFragmentManager(), "dialog");
//                return;
//            }


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
                if (!binding.omgg.oldStartDate.getText().toString().trim().isEmpty() && !binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.omgg.oldStartDate.getText().toString().trim());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(edate);
                    calendar.add(Calendar.DAY_OF_MONTH, 3);
                    Date minStartDate = calendar.getTime();

                    if (stdate.before(minStartDate)) {
                        binding.editTextStartDate.setError("Start Date must be at least three days after the previous start date " + f.format(minStartDate));
                        Toast.makeText(getActivity(), "Start Date must be at least three days after the previous start date " + f.format(minStartDate), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Clear error if validation passes
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
                    String formattedDate = f.format(edate);
                    if (edate.after(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Or Equal to " + formattedDate);
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Or Equal to " + formattedDate, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (edate.equals(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Or Equal to " + formattedDate);
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Or Equal to " + formattedDate, Toast.LENGTH_SHORT).show();
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


            try {
                if (!binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Start Date Cannot Be a Future Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
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

            if(finalData.endType==2 || finalData.endType==3){
                finalData.complete=1;
            }

//            SocialgroupAmendment socialgroupA = new SocialgroupAmendment();
//
//            if (socialgroup.groupName!= null && individual.getFirstName()!= null && "UNK".equals(socialgroup.groupName)) {
//                socialgroupA.individual_uuid = individual.getUuid();
//                socialgroupA.groupName = individual.getFirstName() +' '+ individual.getLastName();
//                socialgroupA.uuid = socialgroup.uuid;
//            }
//
//            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
//            socialgroupViewModel.update(socialgroupA);
//
//            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
            finalData.complete=1;
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();


            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            try {
                Socialgroup data = socialgroupViewModel.find(socialgroup.uuid);
                if (data !=null) {
                    SocialgroupAmendment socialgroupAmendment = new SocialgroupAmendment();
                    socialgroupAmendment.individual_uuid = individual.uuid;
                    socialgroupAmendment.groupName = individual.getFirstName() + ' ' + individual.getLastName();
                    socialgroupAmendment.uuid = socialgroup.uuid;

                    socialgroupViewModel.update(socialgroupAmendment);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            RelationshipViewModel relModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            try {
                Relationship data = relModel.find(individual.uuid);
                if (data != null && !binding.dth.dthDeathDate.getText().toString().trim().isEmpty()) {

                    RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                    relationshipUpdate.endType = 4;
                    relationshipUpdate.endDate = binding.getDeath().deathDate;
                    relationshipUpdate.individualA_uuid = individual.uuid;
                    relationshipUpdate.complete = 1;

                    relModel.update(relationshipUpdate);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            OutmigrationViewModel omgModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
            try {
                Outmigration data = omgModel.createOmg(individual.uuid, locations.uuid);
            if (data != null && !binding.omgg.oldLoc.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim())) {

                Outmigration omg = new Outmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                // Subtract one day from the recordedDate
                Date recordedDate = binding.getInmigration().recordedDate;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(recordedDate);
                calendar.add(Calendar.DAY_OF_MONTH, -1);

                omg.recordedDate = calendar.getTime();
                omg.uuid = uuidString;
                omg.individual_uuid = finalData.individual_uuid;
                omg.insertDate = finalData.insertDate;
                omg.destination = binding.getInmigration().origin;
                omg.reason = binding.getInmigration().reason;
                omg.residency_uuid = binding.getOmgg().old_residency;
                omg.fw_uuid = finalData.fw_uuid;
                omg.complete = 1;
                omg.visit_uuid = binding.getInmigration().visit_uuid;

                omgModel.add(omg);
            }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            try {
                Residency data = resModel.fetchs(individual.uuid);
                if (data != null && !binding.omgg.oldLoc.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim())) {

                    ResidencyAmendment residencyAmendment = new ResidencyAmendment();

                    Date recordedDate = binding.getInmigration().recordedDate;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(recordedDate);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);

                    residencyAmendment.endType = 2;
                    residencyAmendment.endDate = calendar.getTime();
                    residencyAmendment.uuid = binding.getOmgg().old_residency;
                    residencyAmendment.complete = 1;

                    resModel.update(residencyAmendment);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        if (save && binding.getResidency().endType==1) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
        }else if (save && binding.getResidency().endType==2 || binding.getResidency().endType==3){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        }
        else {
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