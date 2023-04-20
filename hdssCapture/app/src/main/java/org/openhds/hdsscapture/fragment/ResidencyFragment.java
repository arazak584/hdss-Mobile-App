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
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentResidencyBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
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
    private FragmentResidencyBinding binding;
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
        binding = FragmentResidencyBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

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
            if (bundle.containsKey((ResidencyFragment.DATE_BUNDLES.STARTDATE.getBundleKey()))) {
                final String result = bundle.getString(ResidencyFragment.DATE_BUNDLES.STARTDATE.getBundleKey());
                binding.editTextStartDate.setText(result);
            }

            if (bundle.containsKey((ResidencyFragment.DATE_BUNDLES.ENDDATE.getBundleKey()))) {
                final String result = bundle.getString(ResidencyFragment.DATE_BUNDLES.ENDDATE.getBundleKey());
                binding.editTextEndDate.setText(result);
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

        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        try {
            Residency data = viewModel.findRes(individual.individual_uuid);

            String img = UUID.randomUUID().toString();
            String img_uuid = img.toString().replaceAll("-", "");

            String omg = UUID.randomUUID().toString();
            String omg_uuid = omg.toString().replaceAll("-", "");

            if (data != null && data.endType!=2) {
                binding.setResidency(data);
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.img_uuid = img_uuid;
                data.omg_uuid = omg_uuid;
                data.imgcomplete = 1;
                data.omgcomplete = 1;
                data.loc = locations.getLocation_uuid();
                data.migType = 2;
            } else {
                data = new Residency();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");

                data.omg_uuid = omg_uuid;
                data.img_uuid = img_uuid;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.residency_uuid = uuidString;
                data.insertDate = new Date();
                data.individual_uuid = individual.getIndividual_uuid();
                data.location_uuid = locations.getLocation_uuid();
                data.socialgroup_uuid = socialgroup.socialgroup_uuid;
                data.visit_uuid = socialgroup.getVisit_uuid();
                data.loc = locations.getLocation_uuid();
                data.imgcomplete = 1;
                data.omgcomplete = 1;
                if (data!=null){
                    data.migType=2;
                }
                if (data!=null){
                    data.startType=1;
                }
                if (data!=null){
                    data.img=1;
                }
                if (data != null && data.endDate != null) {
                    try {
                        // Create a SimpleDateFormat object with the desired date format
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        // Parse the endDate into a Date object
                        Date endDate = sdf.parse(String.valueOf(data.endDate));

                        // Create a Calendar object and set it to the endDate
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);

                        // Add one day to the Calendar object
                        cal.add(Calendar.DAY_OF_MONTH, 1);

                        // Get the new date and format it as a String
                        Date startDate = cal.getTime();
                        Date startDateString = sdf.parse(String.valueOf(startDate));

                        // Set the startDate of the data object
                        data.startDate = startDateString;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                binding.setResidency(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        loadCodeData(binding.residencyComplete, "complete");
        loadCodeData(binding.starttype, "startType");
        loadCodeData(binding.endtype,  "endType");
        loadCodeData(binding.rltnHead,  "rltnhead");
        loadCodeData(binding.residencyImg,  "complete");
        loadCodeData(binding.reason,  "reason");
        loadCodeData(binding.origin,  "whereoutside");
        loadCodeData(binding.migtype,  "migType");
        loadCodeData(binding.destination,  "whereoutside");
        loadCodeData(binding.reasonOut,  "reason");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        binding.setEventname(AppConstants.EVENT_RESIDENCY);
        Handler.colorLayouts(requireContext(), binding.RESIDENCYLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ResidencyViewModel viewModel) {

        if (save) {
            Residency finalData = binding.getResidency();

            boolean isOmg = false;

            Log.d("DEBUG", "locationExtid: " + binding.locationExtid);
            Log.d("DEBUG", "currentLoc: " + binding.currentLoc);
            if (!binding.locationExtid.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim())) {
                isOmg = true;
                binding.currentLoc.setError("Move Individual Out of His/Her Previous Compound");
            }
            Log.d("DEBUG", "isOmg: " + isOmg);



            if(isOmg){//if there is an error, do not continue
                MoveInErrorFragment dialog = MoveInErrorFragment.newInstance(individual,residency,locations,socialgroup);
                dialog.show(requireActivity().getSupportFragmentManager(), "dialog");
                return;
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.RESIDENCYLAYOUT, validateOnComplete, false);

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
        }else{
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
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