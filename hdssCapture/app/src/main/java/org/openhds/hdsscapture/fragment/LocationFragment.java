package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentLocationBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "LOCATION.TAG";

    private Hierarchy cluster_id;
    private Location location;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private String name;
    private FragmentLocationBinding binding;
    private CaseItem caseItem;

    public LocationFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cluster_id  Parameter 1.
     * @param location    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param residency Parameter 4.
     * @param individual Parameter 5.
     * @return A new instance of fragment LocationFragment.
     */

    public static LocationFragment newInstance(Hierarchy cluster_id, Location location, Socialgroup socialgroup, Residency residency, Individual individual) {

        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUSTER_IDS, cluster_id);
        args.putParcelable(LOC_LOCATION_IDS, location);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            cluster_id = getArguments().getParcelable(ARG_CLUSTER_IDS);
            location = getArguments().getParcelable(LOC_LOCATION_IDS);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        binding.setLocation(location);


        final Intent intent = getActivity().getIntent();
        final Hierarchy level6Data = intent.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);


        if(location.locationLevel_uuid==null){
            binding.getLocation().locationLevel_uuid = level6Data.getUuid();
        }

        if(location.villcode==null){
            binding.getLocation().villcode = level6Data.getVillcode();
        }

        if(location.fw_uuid==null){
            binding.getLocation().fw_uuid = fieldworkerData.getFw_uuid();
        }


        // Generate a UUID
        if(location.location_uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.toString().replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getLocation().location_uuid = uuidString;
            }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.locationstatus, codeBookViewModel, "status");
        loadCodeData(binding.complete, codeBookViewModel, "complete");
        loadCodeData(binding.locationtype, codeBookViewModel, "locationType");



        binding.buttonSubmit.setOnClickListener(v -> {
            final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            final Location location = binding.getLocation();
            location.setCompextId(this.location.getCompextId());

            boolean isExists = false;
            binding.locationextid.setError(null);
            binding.locationName.setError(null);

            binding.locationcompno.setError(null);

            if (binding.locationInsertDate.getText().toString().isEmpty()) {
                binding.locationInsertDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime()));
            }

            if(location.compno==null){
                isExists = true;
                binding.locationcompno.setError("Structure Number is Required");

            }

            if(location.compextId==null){
                isExists = true;
                binding.locationextid.setError("A Valid Compound ID is Required");
            }

            if(location.locationName==null){
                isExists = true;
                binding.locationName.setError("Compound Name is Required");
            }


            try {
                Location location1 = null;
                if(location.getCompno()!=null && !location.getCompno().equalsIgnoreCase(this.location.getCompno())) {
                    location1 = locationViewModel.find(location.getCompno());
                    if (location1 != null) {
                        isExists = true;
                        binding.locationcompno.setError("Already Exists");
                    }
                }
                if(location.getCompextId()!=null && !location.getCompextId().equalsIgnoreCase(this.location.getCompextId())) {
                    location1 = locationViewModel.find(location.getCompextId());
                    if (location1 != null) {
                        isExists = true;
                        binding.locationextid.setError("Already Exists");
                    }
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(isExists){//if there is an error, do not continue
                return;
            }

            locationViewModel.add(location);
            Toast.makeText(v.getContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                 HouseVisitFragment.newInstance(individual, residency, location, socialgroup)).commit();



        });

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View v = binding.getRoot();
        return v;



    }



    private <T> void callable(Spinner spinner, T[] array) {

        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void loadCodeData(Spinner spinner, CodeBookViewModel viewModel, final String codeFeature) {
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
        INSERTDATE ("INSERTDATE");

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