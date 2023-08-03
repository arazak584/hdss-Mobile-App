package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
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
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
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
 * Use the {@link AmendmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AmendmentFragment extends Fragment {

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
    private CaseItem caseItem;

    public AmendmentFragment() {
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
     * @param eventForm Parameter 7.
     * @return A new instance of fragment AmendmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AmendmentFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, EventForm eventForm) {
        AmendmentFragment fragment = new AmendmentFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
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
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAmendmentBinding.inflate(inflater, container, false);
        //binding.setIndividual(individual);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey());
                binding.replDob.setText(result);
            }

        });

        binding.buttonReplDob.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(AmendmentFragment.DATE_BUNDLES.DOB.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        AmendmentViewModel viewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
        ResidencyViewModel resViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Amendment data = viewModel.find(individual.uuid);
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
                String uuidString = uuid.toString().replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.orig_firstName = individual.firstName;
                data.orig_lastName = individual.lastName;
                data.orig_otherName = individual.otherName;
                data.orig_ghanacard = individual.ghanacard;
                data.orig_dob = individual.dob;
                data.orig_gender = individual.gender;
                data.individual_uuid = individual.getUuid();
                data.complete = 1;

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
            Residency datas = resViewModel.amend(individual.uuid);
            if (datas != null) {
                binding.setRes(datas);

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
        
        binding.setEventname(eventForm.event_name);
        Handler.colorLayouts(requireContext(), binding.AMENDLAYOUT);
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
                        Toast.makeText(getActivity(), "Date of Birth Cannot Be Greater than Start Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.replDob.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            IndividualAmendment amend = new IndividualAmendment();
            amend.uuid = finalData.individual_uuid;

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.AMENDLAYOUT, validateOnComplete, false);

            boolean gh = false;

            if (!binding.replGhanacard.getText().toString().trim().isEmpty()) {
                String input = binding.replGhanacard.getText().toString().trim();
                String regex = "[A-Z]{3}-\\d{9}-\\d";

                if (!input.matches(regex)) {
                    gh = true;
                    Toast.makeText(getActivity(), "Ghana Card Number or format is incorrect", Toast.LENGTH_SHORT).show();
                    binding.replGhanacard.setError("Format Should Be GHA-XXXXXXXXX-X");
                    return;
                }
            }

            boolean val = false;
            if (!binding.replFirstName.getText().toString().trim().isEmpty()) {
                String firstName = binding.replFirstName.getText().toString();
                if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                    binding.replFirstName.setError("Spaces are not allowed before or after the Name");
                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_SHORT).show();
                    val = true;
                    return;
                } else {
                    binding.replFirstName.setError(null); // Clear the error if the input is valid
                }
            }

            boolean vals = false;
            if (!binding.replLastName.getText().toString().trim().isEmpty()) {
                String lastName = binding.replLastName.getText().toString();
                if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                    binding.replLastName.setError("Spaces are not allowed before or after the Name");
                    Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_SHORT).show();
                    vals = true;
                    return;
                } else {
                    binding.replLastName.setError(null); // Clear the error if the input is valid
                }
            }

            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }
            finalData.complete=1;

            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

            if (!binding.replFirstName.getText().toString().trim().isEmpty()) {
                amend.firstName = binding.getAmendment().repl_firstName;
            }else {
                amend.firstName = finalData.orig_firstName;
            }

            if (!binding.replLastName.getText().toString().trim().isEmpty()) {
                amend.lastName = binding.getAmendment().repl_lastName;
            }else {
                amend.lastName = finalData.orig_lastName;
            }

            if (!binding.replOtherName.getText().toString().trim().isEmpty()) {
                amend.otherName = binding.getAmendment().repl_otherName;
            }else {
                amend.otherName = finalData.orig_otherName;
            }

            if (!binding.replGhanacard.getText().toString().trim().isEmpty()) {
                amend.ghanacard = binding.getAmendment().repl_ghanacard;
            }else {
                amend.ghanacard = finalData.orig_ghanacard;
            }

            if (binding.replGender.getSelectedItemPosition() != 0) {
                amend.gender = binding.getAmendment().repl_gender;
            }else {
                amend.gender = finalData.orig_gender;
            }

            if (!binding.replDob.getText().toString().trim().isEmpty()) {
                amend.dob = binding.getAmendment().repl_dob;
            }else {
                amend.dob = finalData.orig_dob;
            }


            amend.complete = 1;
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            individualViewModel.update(amend);

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
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