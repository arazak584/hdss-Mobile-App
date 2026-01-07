package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.RelationshipDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueUUIDGenerator;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.databinding.FragmentRelationshipBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
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
 * Use the {@link RelationshipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationshipFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "RELATIONSHIP.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentRelationshipBinding binding;
    private ProgressDialog progressDialog;
    private Locations selectedLocation;
    private Individual selectedIndividual;

    public RelationshipFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment RelationshipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RelationshipFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        RelationshipFragment fragment = new RelationshipFragment();
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
        binding = FragmentRelationshipBinding.inflate(inflater, container, false);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(selectedIndividual.firstName + " " + selectedIndividual.lastName);

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_partner);
        // Set a click listener on the button for partner
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Adult Males...");
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
                RelationshipDialogFragment.newInstance(individual, locations,socialgroup)
                        .show(getChildFragmentManager(), "RelationshipDialogFragment");
            }
        });

        //CHOOSING THE DATE
        setupDatePickers();

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);
        RelationshipViewModel viewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        try {
            Relationship data = viewModel.find(selectedIndividual.uuid);
            if (data != null) {
                binding.setRelationship(data);
                data.location_uuid = selectedLocation.getUuid();
                binding.getRelationship().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

                Date currentDate = new Date(); // Get the current date and time
                // Create a Calendar instance and set it to the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;

            } else {
                data = new Relationship();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");


                data.fw_uuid = fieldworkerData.getFw_uuid();
                //data.uuid = uuidString;
                data.uuid = UniqueUUIDGenerator.generate(getContext());
                data.individualA_uuid = selectedIndividual.getUuid();
                data.dob = selectedIndividual.dob;
                data.location_uuid = selectedLocation.getUuid();

                Date currentDate = new Date(); // Get the current date and time
                // Create a Calendar instance and set it to the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;


                binding.setRelationship(data);
                binding.getRelationship().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.getRelationship().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //SPINNERS
        loadCodeData(binding.relComplete,  "submit");
        loadCodeData(binding.relStarttype,  "relationshipType");
        loadCodeData(binding.relEndtype,  "relendType");
        loadCodeData(binding.mar,  "complete");
        loadCodeData(binding.polygamous,  "complete");
        loadCodeData(binding.lcow,  "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.RELATIONSHIPLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, RelationshipViewModel viewModel) {

        if (save) {
            Relationship finalData = binding.getRelationship();

            boolean dob = false;
            boolean val = false;
            boolean mwive = false;
            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.RELATIONSHIPLAYOUT, validateOnComplete, false);

            if (!binding.tnbch.getText().toString().trim().isEmpty() && !binding.nchdm.getText().toString().trim().isEmpty()) {
                int totalBiolChildren = Integer.parseInt(binding.tnbch.getText().toString().trim());
                int childrenFromMarriage = Integer.parseInt(binding.nchdm.getText().toString().trim());
                if (totalBiolChildren < childrenFromMarriage) {
                    val = true;
                    binding.nchdm.setError("Number of Biological Children Cannot be Less Children from this Marriage");
                    return;
                }
            }

            if (!binding.nwive.getText().toString().trim().isEmpty()) {
                int totalwive = Integer.parseInt(binding.nwive.getText().toString().trim());
                if (totalwive < 2) {
                    mwive = true;
                    binding.nwive.setError("Cannot be less than 2");
                    return;
                }
            }

            try {
                if (!binding.womanDob.getText().toString().trim().isEmpty() && !binding.relStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.relStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.womanDob.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.relStartDate.setError("Start Date Cannot Be Less than Date of Birth");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Date of Birth", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (stdate.after(currentDate)) {
                        binding.relStartDate.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.relStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.relEndDate.getText().toString().trim().isEmpty() && !binding.relStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.relStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.relEndDate.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.relEndDate.setError("End Date Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "End Date Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.relEndDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
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

            if (finalData.sttime !=null){
                finalData.edtime = endtime;
            }
            finalData.complete=1;

            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {

                Individual visitedData = iview.visited(selectedIndividual.uuid);
                if (visitedData != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.individualA_uuid;
                    visited.complete = 1;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            viewModel.add(finalData);


            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
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
        STARTDATE ("STARTDATE"),
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

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, DATE_BUNDLES.STARTDATE, binding.relStartDate);
            handleDateResult(bundle, DATE_BUNDLES.ENDDATE, binding.relEndDate);
        });

        binding.buttonRelStartDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.STARTDATE, binding.relStartDate));

        binding.buttonRelEndDate.setEndIconOnClickListener(v ->
                showDatePicker(DATE_BUNDLES.ENDDATE, binding.relEndDate));

    }

    private void handleDateResult(Bundle bundle, RelationshipFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(RelationshipFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
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