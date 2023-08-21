package org.openhds.hdsscapture.Baseline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import org.openhds.hdsscapture.Dialog.FatherDialogFragment;
import org.openhds.hdsscapture.Dialog.MotherDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Calculators;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentBaselineBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.DatePickerFragment;

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
 * Use the {@link BaselineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaselineFragment extends Fragment {

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
    private FragmentBaselineBinding binding;;
    private ProgressDialog progressDialog;

    public BaselineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param individual Parameter 4.
     * @param residency Parameter 2.
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment BaselineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaselineFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        BaselineFragment fragment = new BaselineFragment();
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
        binding = FragmentBaselineBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

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
                MotherDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "MotherDialogFragment");
            }
        });


        // Set a click listener on the button for mother
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
                FatherDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "FatherDialogFragment");
            }
        });


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported


            if (bundle.containsKey((BaselineFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(BaselineFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.dob.setText(result);

                if (binding.getIndividual().dob != null) {
                    final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
                    binding.individAge.setText(String.valueOf(estimatedAge));
                    binding.individAge.setTextColor(Color.MAGENTA);

                    binding.dob.setError(null);
                }
            }

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
            DialogFragment newFragment = new DatePickerFragment(BaselineFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        try {
            Individual data = individualViewModel.find(individual.uuid);

            if (data != null) {
                binding.setIndividual(data);
                binding.individualExtid.setEnabled(false);

                if (binding.getIndividual().dob != null) {
                    final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
                    binding.individAge.setText(String.valueOf(estimatedAge));
                    binding.dob.setError(null);
                }

            } else {
                data = new Individual();
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;

                binding.individualExtid.setEnabled(false);

                binding.setIndividual(data);
                binding.getIndividual().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                // Generate ID if extId is null
                if (binding.getIndividual().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    int sequenceNumber = 1;
                    String id = locations.compextId + String.format("%04d", sequenceNumber); // generate ID with sequence number padded with zeros
                    while (true) {
                        try {
                            if (!(individualViewModels.findAll(id) != null)) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } // check if ID already exists in ViewModel
                        sequenceNumber++; // increment sequence number if ID exists
                        id = locations.compextId + String.format("%04d", sequenceNumber); // generate new ID with updated sequence number
                    }
                    binding.getIndividual().extId = id; // set the generated ID to the extId property of the Individual object
                }




            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Residency data = viewModel.findRes(individual.uuid,locations.uuid);

            if (data != null) {
                binding.setResidency(data);

            } else {
                data = new Residency();
                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.startType = 3;
                data.endType = 1;
                data.location_uuid = locations.uuid;
                data.socialgroup_uuid = socialgroup.uuid;
                data.complete = 1;
                data.individual_uuid = binding.getIndividual().uuid;

                binding.starttype.setEnabled(false);
                binding.endtype.setEnabled(false);
                binding.editTextStartDate.setEnabled(false);
                binding.buttonResidencyStartDate.setEnabled(false);

                binding.setResidency(data);
                binding.getResidency().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.getResidency().setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));



            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        //LOAD SPINNERS
        loadCodeData(binding.dobAspect, "complete");
        loadCodeData(binding.individualComplete,  "complete");
        loadCodeData(binding.gender, "gender");
        loadCodeData(binding.starttype, "startType");
        loadCodeData(binding.endtype, "endType");
        loadCodeData(binding.rltnHead, "rltnhead");
        loadCodeData(binding.other,  "complete");
        loadCodeData(binding.gh,  "complete");
        loadCodeData(binding.mother,  "complete");
        loadCodeData(binding.father,  "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, individualViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, individualViewModel);
        });

        binding.setEventname(AppConstants.EVENT_BASE);
        Handler.colorLayouts(requireContext(), binding.BASELINELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, ResidencyViewModel viewModel,  IndividualViewModel individualViewModel) {

        if (save) {
            Residency finalData = binding.getResidency();
            Individual Data = binding.getIndividual();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.BASELINELAYOUT, validateOnComplete, false);

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

            boolean agedif = false;
            boolean modif = false;

            if (!binding.fatherAge.getText().toString().trim().isEmpty() && !binding.individAge.getText().toString().trim().isEmpty()) {
                int fAgeValue = Integer.parseInt(binding.fatherAge.getText().toString().trim());
                int individidAgeValue = Integer.parseInt(binding.individAge.getText().toString().trim());
                if (fAgeValue - individidAgeValue < 10) {
                    agedif = true;
                    binding.fatherAge.setError("Father selected is too young to be the father of this Individual");
                    Toast.makeText(getActivity(), "Father selected is too young to be the father of this Individual", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (!binding.motherAge.getText().toString().trim().isEmpty() && !binding.individAge.getText().toString().trim().isEmpty()) {
                int mthgeValue = Integer.parseInt(binding.motherAge.getText().toString().trim());
                int individidAge = Integer.parseInt(binding.individAge.getText().toString().trim());
                if (mthgeValue - individidAge < 10) {
                    modif = true;
                    binding.motherAge.setError("Mother selected is too young to be the mother of this Individual");
                    Toast.makeText(getActivity(), "Mother selected is too young to be the mother of this Individual", Toast.LENGTH_LONG).show();
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

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            finalData.complete=1;
            viewModel.add(finalData);
            individualViewModel.add(Data);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

//            SocialgroupAmendment socialgroupA = new SocialgroupAmendment();
//
//            if (socialgroup.groupName!= null && "UNK".equals(socialgroup.groupName)) {
//                socialgroupA.individual_uuid = binding.getIndividual().getUuid();
//                socialgroupA.groupName = binding.getIndividual().firstName +' '+ binding.getIndividual().lastName;
//                socialgroupA.uuid = socialgroup.uuid;
//            }
//
//            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
//            socialgroupViewModel.update(socialgroupA);
//
//            Toast.makeText(requireActivity(), R.string.updated, Toast.LENGTH_LONG).show();

            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            try {
                Socialgroup data = socialgroupViewModel.find(socialgroup.uuid);
                if (data !=null) {
                    SocialgroupAmendment socialgroupAmendment = new SocialgroupAmendment();
                    socialgroupAmendment.individual_uuid = individual.uuid;
                    socialgroupAmendment.groupName = individual.getFirstName() + ' ' + individual.getLastName();
                    socialgroupAmendment.uuid = socialgroup.uuid;
                    socialgroupAmendment.complete =1;

                    socialgroupViewModel.update(socialgroupAmendment);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    IndividualSummaryFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    IndividualSummaryFragment.newInstance(individual,residency, locations, socialgroup)).commit();
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
        STARTDATE ("STARTDATE"),
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