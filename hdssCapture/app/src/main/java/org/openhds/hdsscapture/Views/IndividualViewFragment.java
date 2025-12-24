package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.Utilities.Calculators;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualEditBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;
import org.openhds.hdsscapture.fragment.KeyboardFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualViewFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";
    private Individual individual;
    private FragmentIndividualEditBinding binding;
    private IndividualRepository repository;

    public IndividualViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeathFragment.
     */
    public static IndividualViewFragment newInstance(String uuid) {
        IndividualViewFragment fragment = new IndividualViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new IndividualRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.individual = new Individual();  // Initialize placeholder
            this.individual.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIndividualEditBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();

// Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        //CHOOSING THE DATE
        setupDatePickers();

        ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        IndividualViewModel viewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        viewModel.getView(individual.uuid).observe(getViewLifecycleOwner(), data -> {
        binding.setIndividual(data);

            try {
                Residency dataRes = resModel.views(data.uuid);
                if (dataRes != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dt = dataRes.startDate;
                    AppCompatEditText editText = binding.getRoot().findViewById(R.id.editTextStartDate);
                    if (dt != null) {
                        String formattedDate = dateFormat.format(dt);
                        editText.setText(formattedDate);
                    }

                } } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }

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

            try {
                Individual datae = viewModel.mother(data.uuid);
            if (datae != null) {
                binding.setMother(datae);

                AppCompatEditText name = binding.getRoot().findViewById(R.id.mother_name);
                AppCompatEditText dob = binding.getRoot().findViewById(R.id.mother_dob);
                AppCompatEditText age = binding.getRoot().findViewById(R.id.mothers_age);
                name.setText(datae.firstName + " " + datae.lastName);
                dob.setText(datae.getDob());
                age.setText(String.valueOf(datae.getAge()));

            } } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }

            try {
                Individual datae = viewModel.father(data.uuid);
                if (datae != null) {
                    binding.setFather(datae);

                    AppCompatEditText name = binding.getRoot().findViewById(R.id.father_name);
                    AppCompatEditText dob = binding.getRoot().findViewById(R.id.father_dob);
                    AppCompatEditText age = binding.getRoot().findViewById(R.id.fathers_age);
                    name.setText(datae.firstName + " " + datae.lastName);
                    dob.setText(datae.getDob());
                    age.setText(String.valueOf(datae.getAge()));

                } } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }

        });

        loadCodeData(binding.dobAspect, "complete");
        loadCodeData(binding.gender, "gender");
        loadCodeData(binding.other,  "complete");
        loadCodeData(binding.gh,  "complete");
        loadCodeData(binding.mother,  "complete");
        loadCodeData(binding.father,  "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.INDIVIDUALLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, IndividualViewModel viewModel) {

        if (save) {
            Individual finalData = binding.getIndividual();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.INDIVIDUALLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
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

            // Individual Name Validation
            boolean hasError = false;
            String firstName = binding.individualFirstName.getText().toString();
            String lastName = binding.individualLastName.getText().toString();
            // Validate First Name
            if (firstName.trim().length() != firstName.length()) {
                // Leading or trailing spaces not allowed
                binding.individualFirstName.setError("Spaces are not allowed before or after the Name");
                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                hasError = true;
            } else if (!firstName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                // Only letters, hyphens, and single spaces allowed
                binding.individualFirstName.setError("Only letters, hyphens, and single spaces are allowed in the Name");
                Toast.makeText(getContext(), "Only letters, hyphens, and single spaces are allowed in the Name", Toast.LENGTH_LONG).show();
                hasError = true;
            } else {
                // Valid name
                binding.individualFirstName.setError(null);
            }

            // Validate Last Name
            if (lastName.startsWith(" ") || lastName.endsWith(" ")) {
                binding.individualLastName.setError("Spaces are not allowed before or after the Name");
                Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                hasError = true;
            }
            else if (!lastName.matches("^[a-zA-Z]+([ '-][a-zA-Z]+)*$")) {
                binding.individualLastName.setError("Only letters, hyphens, and single spaces are allowed in the Name");
                Toast.makeText(getContext(), "Only letters, hyphens, and single spaces are allowed in the Name", Toast.LENGTH_LONG).show();
                hasError = true;
            }
            else {
                binding.individualLastName.setError(null);
            }

            // Stop execution if any errors exist
            if (hasError) {
                return;
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


            finalData.complete = 1;
            viewModel.add(finalData);

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
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

    private void updateVisibilityBasedOnAge() {
        if (binding.getIndividual() != null && binding.getIndividual().dob != null) {
            final int estimatedAge = Calculators.getAge(binding.getIndividual().dob);
            binding.individAge.setText(String.valueOf(estimatedAge));
            binding.individAge.setTextColor(Color.MAGENTA);

            binding.dob.setError(null);
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

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, IndividualViewFragment.DATE_BUNDLES.DOB, binding.dob);
        });

        binding.buttonIndividualDob.setEndIconOnClickListener(v ->
                showDatePicker(IndividualViewFragment.DATE_BUNDLES.DOB, binding.dob));


    }

    private void handleDateResult(Bundle bundle, IndividualViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);

            // Handle DOB-specific logic
            if (dateType == IndividualViewFragment.DATE_BUNDLES.DOB) {
                if (binding.getIndividual() != null) {
                    binding.getIndividual().setDob(result);
                    updateVisibilityBasedOnAge();
                }
            }
        }
    }

    private void showDatePicker(IndividualViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        Calendar calendar = parseCurrentDate(editText.getText().toString());
        DialogFragment datePickerFragment = new DatePickerFragment(
                dateType.getBundleKey(),
                calendar
        );
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), TAG);
    }

    private Calendar parseCurrentDate(String dateString) {
        Calendar calendar = Calendar.getInstance();

        if (!TextUtils.isEmpty(dateString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date date = sdf.parse(dateString);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }
}