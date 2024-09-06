package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.FatherDialogFragment;
import org.openhds.hdsscapture.Dialog.MotherDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Calculators;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualFragment extends Fragment {

    //private Button showDialogButton;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentIndividualBinding binding;
    private ProgressDialog progressDialog;


    public IndividualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @param individual Parameter 4.
     * @param residency Parameter 2.
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment IndividualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        IndividualFragment fragment = new IndividualFragment();
        Bundle args = new Bundle();

        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIndividualBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        // Find the button view
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_individual_mother);
        Button showDialogButton1 = binding.getRoot().findViewById(R.id.button_individual_father);
        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Mothers...");
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
                MotherDialogFragment.newInstance(locations,socialgroup)
                        .show(getChildFragmentManager(), "MotherDialogFragment");
            }
        });
        // Set a click listener on the button for father
        showDialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Fathers...");
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
                FatherDialogFragment.newInstance(locations,socialgroup)
                        .show(getChildFragmentManager(), "FatherDialogFragment");
            }
        });

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.individualInsertDate.setText(result);

            }


            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.dob.setText(result);

                if (binding.getIndividual().dob != null) {
                    final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
                        binding.individAge.setText(String.valueOf(estimatedAge));
                        binding.individAge.setTextColor(Color.MAGENTA);

                    binding.dob.setError(null);
                }
            }

            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.IMGDATE.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.IMGDATE.getBundleKey());
                binding.imgDate.setText(result);
            }

            if (bundle.containsKey((IndividualFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(IndividualFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.editTextStartDate.setText(result);
            }

        });

        binding.buttonIndividualInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonResidencyStartDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.editTextStartDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.editTextStartDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        binding.buttonImgImgDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.IMGDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });


        binding.buttonIndividualDob.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            final String curbrthdat = binding.dob.getText().toString();
            if(curbrthdat!=null){
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    c.setTime(Objects.requireNonNull(f.parse(curbrthdat)));
                } catch (ParseException e) {
                }
            }
            if (!TextUtils.isEmpty(binding.dob.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.dob.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.DOB.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                DialogFragment newFragment = new DatePickerFragment(IndividualFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        Spinner mySpinner = binding.getRoot().findViewById(R.id.migtype);
        mySpinner.setEnabled(false);

        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Individual data = individualViewModel.find(individual.uuid);
            if (data != null) {
                binding.setIndividual(data);
                binding.individualExtid.setEnabled(false);

                String indid = data.getExtId();
                if (indid.length() != 12) {
                    String id = UniqueIDGen.generateUniqueId(individualViewModel, ClusterFragment.selectedLocation.compextId);
                    binding.getIndividual().extId = id;

                }else{
                    data.extId = data.getExtId();
                }
                //data.hohID = socialgroup.getExtId();

                if (binding.getIndividual().dob != null) {
                    final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
                    binding.individAge.setText(String.valueOf(estimatedAge));
                    binding.dob.setError(null);
                }
                data.mother=1;
                data.father=1;
                if (data.ghanacard != null && individual.gh ==null) {
                    data.gh=1;
                }
                if (data.otherName != null && individual.other==null) {
                    data.other=1;
                }
                if (data.ghanacard != null && data.ghanacard !="") {
                    binding.ghanacard.setEnabled(false);
                }
                if (data.otherName != null && data.otherName !="") {
                    binding.individualNickName.setEnabled(false);
                }
                if (data.firstName != null) {
                    binding.individualFirstName.setEnabled(false);
                    binding.individualLastName.setEnabled(false);
                    //binding.buttonIndividualDob.setVisibility(View.GONE);
                    binding.dobAspect.setEnabled(false);
                    binding.gender.setEnabled(false);
                    binding.buttonIndividualDob.setEnabled(false);
                }

            }else{
                    data = new Individual();

                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");
                    // Set the ID of the Fieldworker object
                    data.uuid = uuidString;
                    data.fw_uuid = fieldworkerData.getFw_uuid();
                    data.village = level6Data.getName();
                    data.hohID = socialgroup.getExtId();

                Date currentDate = new Date(); // Get the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;



                binding.individualExtid.setEnabled(false);
                binding.setIndividual(data);
                binding.getIndividual().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                // Generate ID if extId is null
                if (binding.getIndividual().extId == null) {
                    String id = UniqueIDGen.generateUniqueId(individualViewModel, ClusterFragment.selectedLocation.compextId);
                    binding.getIndividual().extId = id;
//                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
//                    int sequenceNumber = 1;
//                    String id = ClusterFragment.selectedLocation.compextId + String.format("%03d", sequenceNumber); // generate ID with sequence number padded with zeros
//                    while (true) {
//                        try {
//                            if (individualViewModels.findAll(id) == null) break;
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } // check if ID already exists in ViewModel
//                        sequenceNumber++; // increment sequence number if ID exists
//                        id = ClusterFragment.selectedLocation.compextId + String.format("%03d", sequenceNumber); // generate new ID with updated sequence number
//                    }
//                    binding.getIndividual().extId = id; // set the generated ID to the extId property of the Individual object
                }
            }


        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IndividualViewModel ind = new ViewModelProvider(this).get(IndividualViewModel.class);
        try {
            Individual datae = ind.mother(individual.uuid);
            if (datae != null) {
                binding.setMother(datae);

                AppCompatEditText name = binding.getRoot().findViewById(R.id.mother_name);
                AppCompatEditText dob = binding.getRoot().findViewById(R.id.mother_dob);
                AppCompatEditText age = binding.getRoot().findViewById(R.id.mothers_age);
                name.setText(datae.firstName + " " + datae.lastName);
                dob.setText(datae.getDob());
                age.setText(String.valueOf(datae.getAge()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Individual data = ind.father(individual.uuid);
            if (data != null) {
                binding.setFather(data);

                AppCompatEditText name = binding.getRoot().findViewById(R.id.father_name);
                AppCompatEditText dob = binding.getRoot().findViewById(R.id.father_dob);
                AppCompatEditText age = binding.getRoot().findViewById(R.id.fathers_age);
                name.setText(data.firstName + " " + data.lastName);
                dob.setText(data.getDob());
                age.setText(String.valueOf(data.getAge()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Residency data = viewModel.findRes(individual.uuid,ClusterFragment.selectedLocation.uuid);

            if (data != null) {
                binding.setResidency(data);
                data.hohID = socialgroup.extId;
                binding.buttonResidencyStartDate.setEnabled(false);

            } else {
                data = new Residency();
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.startType = 1;
                data.endType = 1;
                data.location_uuid = ClusterFragment.selectedLocation.uuid;
                data.socialgroup_uuid = socialgroup.uuid;
                data.complete = 1;
                data.individual_uuid = binding.getIndividual().uuid;
                data.hohID = socialgroup.extId;
                data.img = 1;

                Date currentDate = new Date(); // Get the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;

                binding.setResidency(data);
                binding.getResidency().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Generate Start Date and EndType if previous episode is Outmigration
        try {
            Residency datas = viewModel.finds(individual.uuid);
            if (datas != null) {
                binding.setRes(datas);
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

        //Generate last residency episode of individual, This helps to generate the type of migration either Internal/External
        try {
            Residency datas = viewModel.amend(individual.uuid);
            if (datas != null) {
                binding.setMig(datas);

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Use to Generate Outmigration and end residency for a previous active residency
        try {
            Residency datae = viewModel.findEnd(individual.uuid, ClusterFragment.selectedLocation.uuid);
            if (datae != null) {
                binding.setOmgg(datae);
                binding.getOmgg().loc = datae.getLocation_uuid();
                binding.getOmgg().old_residency = datae.getUuid();
                binding.getOmgg().startDate = datae.startDate;
                binding.getResidency().old_residency = datae.getUuid();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Get Earliest Event Date
        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = configViewModel.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                String formattedDate = dateFormat.format(dt);
                editText.setText(formattedDate);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        try {
            Inmigration dataimg = inmigrationViewModel.find(individual.uuid,ClusterFragment.selectedLocation.uuid);
            if (dataimg != null && binding.getResidency().img!=null) {
                binding.setInmigration(dataimg);

                if(dataimg.status!=null && dataimg.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

            } else {
                dataimg = new Inmigration();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                dataimg.uuid=uuidString;
                dataimg.fw_uuid = fieldworkerData.getFw_uuid();
                dataimg.individual_uuid = binding.getIndividual().uuid;
                dataimg.complete = 1;
                dataimg.location_uuid = ClusterFragment.selectedLocation.uuid;
                Visit dts = visitViewModel.find(socialgroup.uuid);
                if (dts != null){
                    dataimg.visit_uuid = dts.uuid;
                }

                Date currentDate = new Date(); // Get the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                dataimg.sttime = timeString;

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
                binding.getInmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //LOAD SPINNERS
        loadCodeData(binding.dobAspect, "complete");
        loadCodeData(binding.gender, "gender");
        loadCodeData(binding.other,  "complete");
        loadCodeData(binding.gh,  "complete");
        loadCodeData(binding.mother,  "complete");
        loadCodeData(binding.father,  "complete");
        loadCodeData(binding.rltnHead, "rltnhead");
        loadCodeData(binding.reason,  "reason");
        loadCodeData(binding.origin,  "comingfrom");
        loadCodeData(binding.migtype,  "migType");
        loadCodeData(binding.farm,  "farm");
        loadCodeData(binding.livestock,  "livestock");
        loadCodeData(binding.cashCrops,  "cashcrops");
        loadCodeData(binding.foodCrops,  "food");
        loadCodeData(binding.livestockYn, "submit");
        loadCodeData(binding.cashYn, "submit");
        loadCodeData(binding.foodYn, "submit");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, individualViewModel,inmigrationViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, individualViewModel,inmigrationViewModel);
        });

        binding.setEventname(AppConstants.EVENT_MIND00S);
        Handler.colorLayouts(requireContext(), binding.INDIVIDUALLAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, ResidencyViewModel viewModel,  IndividualViewModel individualViewModel,InmigrationViewModel inmigrationViewModel) {

        if (save) {
            Individual finalData = binding.getIndividual();
            Residency Data = binding.getResidency();
            Inmigration img = binding.getInmigration();
            boolean sdate = false;
            boolean isOmg = false;
            boolean dthdate = false;
            boolean imgdate = false;
            boolean omgdate = false;

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.INDIVIDUALLAYOUT, validateOnComplete, false);

            boolean missedout = false;

            if (img.migType!=null && img.migType==2){
                if (img.reason!=null && img.reason==19) {
                    missedout = true;
                    Toast.makeText(getActivity(), "Reason cannot be missed out for Internal Inmigration", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                if (!binding.dob.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.dob.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.dob.setError("Date of Birth Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Birth Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.dob.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            boolean agedif = false;
            boolean modif = false;

            if (!binding.fathers.fathersAge.getText().toString().trim().isEmpty() && !binding.individAge.getText().toString().trim().isEmpty()) {
                int fAgeValue = Integer.parseInt(binding.fathers.fathersAge.getText().toString().trim());
                int individidAgeValue = Integer.parseInt(binding.individAge.getText().toString().trim());
                if (fAgeValue - individidAgeValue < 10) {
                    agedif = true;
                    binding.fathers.fathersAge.setError("Father selected is too young to be the father of this Individual");
                    Toast.makeText(getActivity(), "Father selected is too young to be the father of this Individual", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (!binding.mothers.mothersAge.getText().toString().trim().isEmpty() && !binding.individAge.getText().toString().trim().isEmpty()) {
                int mthgeValue = Integer.parseInt(binding.mothers.mothersAge.getText().toString().trim());
                int individidAge = Integer.parseInt(binding.individAge.getText().toString().trim());
                if (mthgeValue - individidAge < 10) {
                    modif = true;
                    binding.mothers.mothersAge.setError("Mother selected is too young to be the mother of this Individual");
                    Toast.makeText(getActivity(), "Mother selected is too young to be the mother of this Individual", Toast.LENGTH_LONG).show();
                    return;
                }
            }


            boolean gh = false;

            if (!binding.ghanacard.getText().toString().trim().isEmpty()) {
                String input = binding.ghanacard.getText().toString().trim();
                String regex = "[A-Z]{3}-\\d{9}-\\d";

                if (!input.matches(regex)) {
                    gh = true;
                    Toast.makeText(getActivity(), "Ghana Card Number or format is incorrect", Toast.LENGTH_LONG).show();
                    binding.ghanacard.setError("Format Should Be GHA-XXXXXXXXX-X");
                    return;
                }
            }

            boolean val = false;
            String firstName = binding.individualFirstName.getText().toString();
            if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                binding.individualFirstName.setError("Spaces are not allowed before or after the Name");
                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                val = true;
                return;
            } else {
                binding.individualFirstName.setError(null); // Clear the error if the input is valid
            }

            boolean vals = false;
            String lastName = binding.individualLastName.getText().toString();
            if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                binding.individualLastName.setError("Spaces are not allowed before or after the Name");
                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                vals = true;
                return;
            } else {
                binding.individualLastName.setError(null); // Clear the error if the input is valid
            }

            //Date Validations
            try {
                if (!binding.dob.getText().toString().trim().isEmpty() && !binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.dob.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Date of Birth");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Date of Birth", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.earliest.getText().toString().trim());
                    if (edate.after(stdate) && binding.getInmigration().reason!=null) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Earliest Date");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Earliest Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Start Date must be at least three days after the previous start date " + f.format(minStartDate), Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Or Equal to " + formattedDate, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.equals(stdate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be Less than Or Equal to " + formattedDate);
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Or Equal to " + formattedDate, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.editTextStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextStartDate.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.editTextStartDate.setError("Start Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Start Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            Date end = new Date(); // Get the current date and time
            // Create a Calendar instance and set it to the current date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            // Extract the hour, minute, and second components
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            // Format the components into a string with leading zeros
            String endtime = String.format("%02d:%02d:%02d", hh, mm, ss);

            if (finalData.sttime !=null && finalData.edtime==null){
                finalData.edtime = endtime;
            }
            if (Data.sttime !=null && Data.edtime==null){
                Data.edtime = endtime;
            }
            if (img.sttime !=null && img.edtime==null){
                img.edtime = endtime;
            }

            //Update The Househead for the new household
            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            try {
                Socialgroup data = socialgroupViewModel.find(socialgroup.uuid);
                if (data !=null && "UNK".equals(data.groupName)) {

                    SocialgroupAmendment socialgroupAmendment = new SocialgroupAmendment();
                    socialgroupAmendment.individual_uuid = finalData.uuid;
                    socialgroupAmendment.groupName = finalData.getFirstName() + ' ' + finalData.getLastName();
                    socialgroupAmendment.uuid = socialgroup.uuid;
                    socialgroupAmendment.complete =1;
                    socialgroupViewModel.update(socialgroupAmendment);
                    //Toast.makeText(requireContext(), "Successfully Amended Household Head", Toast.LENGTH_LONG).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finalData.compno = ClusterFragment.selectedLocation.compno;
            finalData.endType = Data.endType;
            finalData.hohID = socialgroup.extId;

            if(binding.getResidency().img != null){
                img.residency_uuid = Data.uuid;
                img.recordedDate = Data.startDate;
                img.complete=1;
                inmigrationViewModel.add(img);
            }

            finalData.complete=1;
            Data.complete=1;

            viewModel.add(Data);
            individualViewModel.add(finalData);

            final Intent i = getActivity().getIntent();
            final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

            //Generate Outmigration for previous active episode
            OutmigrationViewModel omgModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
            try {
                Outmigration data = omgModel.createOmg(individual.uuid, ClusterFragment.selectedLocation.uuid);
//                if (data != null && !binding.omgg.oldLoc.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim()))
                if (data != null) {

                    Outmigration omg = new Outmigration();

                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");

                    // Subtract one day from the recordedDate
                    Date recordedDate = binding.getInmigration().recordedDate;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(recordedDate);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);

                    omg.recordedDate = calendar.getTime();
                    omg.uuid = uuidString;
                    omg.individual_uuid = finalData.uuid;
                    omg.insertDate = new Date();
                    omg.destination = binding.getInmigration().origin;
                    omg.reason = binding.getInmigration().reason;
                    omg.reason_oth = binding.getInmigration().reason_oth;
                    omg.residency_uuid = binding.getOmgg().old_residency;
                    omg.fw_uuid = fieldworkerData.fw_uuid;
                    omg.complete = 1;
                    omg.edit = 1;
                    omg.visit_uuid = binding.getInmigration().visit_uuid;
                    omg.socialgroup_uuid = binding.getOmgg().socialgroup_uuid;

                    omgModel.add(omg);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Update Previous Residency if It is Active
            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            try {
                Residency data = resModel.fetchs(individual.uuid, ClusterFragment.selectedLocation.uuid);
                if (data != null) {
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

            //Update Fake Individual's Residency that was used to create the socialgroup
            ResidencyViewModel unks = new ViewModelProvider(this).get(ResidencyViewModel.class);
            try {
                Residency datas = unks.unk(socialgroup.uuid);
                if (datas != null) {
                    ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                    residencyAmendment.endType = 2;
                    residencyAmendment.endDate = new Date();
                    residencyAmendment.uuid = datas.uuid;
                    residencyAmendment.complete = 2;
                    unks.update(residencyAmendment);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Update Fake Individual's Residency that was used to create the socialgroup
            IndividualViewModel unkss = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual datas = unkss.unk(socialgroup.extId);
                if (datas != null) {
                    IndividualEnd endInd = new IndividualEnd();
                    endInd.endType = 2;
                    endInd.uuid = datas.uuid;
                    endInd.complete = 2;
                    individualViewModel.dthupdate(endInd);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            }
        if (close)  {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
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
        IMGDATE("IMGDATE"),
        DOB ("DOB");

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