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

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.RelationshipDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.databinding.FragmentRelationshipBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelationshipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationshipFragment extends Fragment {

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

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

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
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported

            if (bundle.containsKey((RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.relStartDate.setText(result);
            }

            if (bundle.containsKey((RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey()))) {
                final String result = bundle.getString(RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey());
                binding.relEndDate.setText(result);
            }

        });

        binding.buttonRelStartDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.relStartDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.relStartDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.STARTDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        binding.buttonRelEndDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.relEndDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.relEndDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(RelationshipFragment.DATE_BUNDLES.ENDDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);
        RelationshipViewModel viewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        try {
            Relationship data = viewModel.find(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setRelationship(data);
                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }
            } else {
                data = new Relationship();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");


                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;

                data.individualA_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.dob = HouseMembersFragment.selectedIndividual.dob;

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

        Handler.colorLayouts(requireContext(), binding.RELATIONSHIPLAYOUT);
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
            boolean hasErrors = new Handler().hasInvalidInput(binding.RELATIONSHIPLAYOUT, validateOnComplete, false);

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
                    Date stdate = f.parse(binding.relStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.womanDob.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.relStartDate.setError("Start Date Cannot Be Less than Date of Birth");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Date of Birth", Toast.LENGTH_LONG).show();
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

            if (finalData.sttime !=null && finalData.edtime==null){
                finalData.edtime = endtime;
            }
            finalData.complete=1;
            viewModel.add(finalData);
            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual data = iview.visited(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.individualA_uuid;
                    visited.complete = 2;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
}