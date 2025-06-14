package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentAmendmentBinding;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AmendmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AmendmentFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "AMEND.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentAmendmentBinding binding;
    private EventForm eventForm;
    private ProgressDialog progressDialog;
    private IndividualViewModel individualViewModel;

    public AmendmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment AmendmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AmendmentFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        AmendmentFragment fragment = new AmendmentFragment();
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
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAmendmentBinding.inflate(inflater, container, false);
        //binding.setIndividual(individual);


        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.replDob.setText(result);
            }

        });

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

        binding.buttonReplDob.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.replDob.getText())) {
                // If replDob is not empty, parse the date and use it as the initial date
                String currentDate = binding.replDob.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {

                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        AmendmentViewModel viewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
        ResidencyViewModel resViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Amendment data = viewModel.find(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setAmendment(data);
                binding.amendFirstName.setEnabled(false);
                binding.amendLastName.setEnabled(false);
                binding.amendOtherName.setEnabled(false);
                binding.amendGender.setEnabled(false);
                binding.amendGhanacard.setEnabled(false);

            } else {
                data = new Amendment();

                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.orig_firstName = HouseMembersFragment.selectedIndividual.getFirstName();
                data.orig_lastName = HouseMembersFragment.selectedIndividual.getLastName();
                data.orig_otherName = HouseMembersFragment.selectedIndividual.getOtherName();
                data.orig_ghanacard = HouseMembersFragment.selectedIndividual.getGhanacard();
                data.orig_dob = HouseMembersFragment.selectedIndividual.dob;
                data.orig_gender = HouseMembersFragment.selectedIndividual.getGender();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();

                Individual datae = individualViewModel.mother(HouseMembersFragment.selectedIndividual.uuid);
                if (datae!=null){
                    data.mother_uuid=datae.uuid;
                }

                Individual dataf = individualViewModel.father(HouseMembersFragment.selectedIndividual.uuid);
                if (dataf!=null){
                    data.father_uuid=dataf.uuid;
                }

                binding.amendFirstName.setEnabled(false);
                binding.amendLastName.setEnabled(false);
                binding.amendOtherName.setEnabled(false);
                binding.amendGender.setEnabled(false);
                binding.amendGhanacard.setEnabled(false);

                binding.setAmendment(data);
                binding.getAmendment().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Residency datas = resViewModel.amend(HouseMembersFragment.selectedIndividual.uuid);
            if (datas != null) {
                binding.setRes(datas);

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            Individual datae = individualViewModel.mother(HouseMembersFragment.selectedIndividual.uuid);
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
            Individual data = individualViewModel.father(HouseMembersFragment.selectedIndividual.uuid);
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
            Individual datae = individualViewModel.find(HouseMembersFragment.selectedIndividual.uuid);
            if (datae != null) {
                binding.setIndividual(datae);

                EditText age = binding.getRoot().findViewById(R.id.individ_age);
                age.setText(String.valueOf(datae.getAge()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Codebook
        loadCodeData(binding.amendComplete, "submit");
        loadCodeData(binding.amendGender, "gender");
        loadCodeData(binding.replGender, "gender");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.AMENDLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, AmendmentViewModel viewModel) {

        if (save) {
            Amendment finalData = binding.getAmendment();

            try {
                if (!binding.replDob.getText().toString().trim().isEmpty() && !binding.startDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.startDate.getText().toString().trim());
                    Date dob = f.parse(binding.replDob.getText().toString().trim());
                    if (dob.after(stdate)) {
                        binding.replDob.setError("Date of Birth Cannot Be Greater than Start Date");
                        Toast.makeText(getActivity(), "Date of Birth Cannot Be Greater than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.replDob.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.AMENDLAYOUTS, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
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

            if (!binding.replGhanacard.getText().toString().trim().isEmpty()) {
                String input = binding.replGhanacard.getText().toString().trim();
                String regex = "[A-Z]{3}-\\d{9}-\\d";

                if (!input.matches(regex)) {
                    gh = true;
                    Toast.makeText(getActivity(), "Ghana Card Number or format is incorrect", Toast.LENGTH_LONG).show();
                    binding.replGhanacard.setError("Format Should Be GHA-XXXXXXXXX-X");
                    return;
                }
            }

            // Individual Name Validation
            boolean hasError = false;
            String firstName = binding.replFirstName.getText().toString();
            String lastName = binding.replLastName.getText().toString();
            // Validate First Name
            if (!firstName.isEmpty()) {
                if (firstName.startsWith(" ") || firstName.endsWith(" ")) {
                    binding.replFirstName.setError("Spaces are not allowed before or after the Name");
                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                    hasError = true;
                } else if (!firstName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                    binding.replFirstName.setError("Numbers are not allowed in the Name");
                    Toast.makeText(getContext(), "Numbers are not allowed in the Name", Toast.LENGTH_LONG).show();
                    hasError = true;
                } else {
                    binding.replFirstName.setError(null);
                }
            }

            // Validate Last Name
            if (!lastName.isEmpty()) {
                if (lastName.trim().length() != lastName.length()) {
                    binding.replLastName.setError("Spaces are not allowed before or after the Last Name");
                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Last Name", Toast.LENGTH_LONG).show();
                    hasError = true;
                } else if (!lastName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                    binding.replLastName.setError("Only letters, hyphens, and single spaces are allowed in the Name");
                    Toast.makeText(getContext(), "Only letters, hyphens, and single spaces are allowed in the Name", Toast.LENGTH_LONG).show();
                    hasError = true;
                } else {
                    binding.replLastName.setError(null);
                }
            }

            // Stop execution if any errors exist
            if (hasError) {
                return;
            }

//            boolean val = false;
//            if (!binding.replFirstName.getText().toString().trim().isEmpty()) {
//                if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
//                    binding.replFirstName.setError("Spaces are not allowed before or after the Name");
//                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
//                    val = true;
//                    return;
//                } else {
//                    binding.replFirstName.setError(null); // Clear the error if the input is valid
//                }
//            }
//
//            boolean vals = false;
//            if (!binding.replLastName.getText().toString().trim().isEmpty()) {
//                if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
//                    binding.replLastName.setError("Spaces are not allowed before or after the Name");
//                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
//                    vals = true;
//                    return;
//                } else {
//                    binding.replLastName.setError(null); // Clear the error if the input is valid
//                }
//            }



            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // First block - amendment update
                    Individual amendData = individualViewModel.find(HouseMembersFragment.selectedIndividual.uuid);
                    if (amendData != null) {
                        IndividualAmendment amend = new IndividualAmendment();
                        amend.uuid = binding.getAmendment().individual_uuid;
                        amend.father_uuid = binding.getAmendment().father_uuid;
                        amend.mother_uuid = binding.getAmendment().mother_uuid;
                        amend.complete = 1;

                        // First name
                        amend.firstName = (binding.getAmendment().yn_firstName == 1)
                                ? binding.getAmendment().repl_firstName
                                : finalData.orig_firstName;

                        // Last name
                        amend.lastName = (binding.getAmendment().yn_lastName == 1)
                                ? binding.getAmendment().repl_lastName
                                : finalData.orig_lastName;

                        // Other name
                        amend.otherName = (binding.getAmendment().yn_otherName == 1)
                                ? binding.getAmendment().repl_otherName
                                : finalData.orig_otherName;

                        // Ghana card
                        amend.ghanacard = (binding.getAmendment().yn_ghanacard == 1)
                                ? binding.getAmendment().repl_ghanacard
                                : finalData.orig_ghanacard;

                        // Gender
                        amend.gender = (binding.replGender.getSelectedItemPosition() != 0)
                                ? binding.getAmendment().repl_gender
                                : finalData.orig_gender;

                        // Date of birth
                        amend.dob = (!binding.replDob.getText().toString().trim().isEmpty())
                                ? binding.getAmendment().repl_dob
                                : finalData.orig_dob;

                        individualViewModel.update(amend, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("AmendmentFragment", "Amend Update successful!");
                                    } else {
                                        Log.d("AmendmentFragment", "Amend Update Failed!");
                                    }
                                })
                        );
                    }
                } catch (Exception e) {
                    Log.e("AmendmentFragment", "Error in amendment update", e);
                    e.printStackTrace();
                }

//                // Add a small delay between operations to ensure the first one completes
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }

//                try {
//                    // Second block - visited update (with different variable name)
//                    Individual visitedData = individualViewModel.visited(HouseMembersFragment.selectedIndividual.uuid);
//                    if (visitedData != null) {
//                        IndividualVisited visited = new IndividualVisited();
//                        visited.uuid = binding.getAmendment().individual_uuid;
//                        visited.complete = 1;
//
//                        individualViewModel.visited(visited, result ->
//                                new Handler(Looper.getMainLooper()).post(() -> {
//                                    if (result > 0) {
//                                        Log.d("AmendmentFragment", "Visit Update successful!");
//                                    } else {
//                                        Log.d("AmendmentFragment", "Visit Update Failed!");
//                                    }
//                                })
//                        );
//                    }
//                } catch (Exception e) {
//                    Log.e("AmendmentFragment", "Error in visited update", e);
//                    e.printStackTrace();
//                }
            });

            executor.shutdown();

            finalData.complete=1;
            viewModel.add(finalData);

        }
        if (close) {
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