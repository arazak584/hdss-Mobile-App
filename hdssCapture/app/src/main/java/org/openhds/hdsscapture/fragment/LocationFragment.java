package org.openhds.hdsscapture.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;

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
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private FragmentLocationBinding binding;
    private CaseItem caseItem;

    private LocationManager locationManager;
    private Location currentLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 1;



    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cluster_id  Parameter 1.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param residency Parameter 4.
     * @param individual Parameter 5.
     * @return A new instance of fragment LocationFragment.
     */

    public static LocationFragment newInstance(Hierarchy cluster_id, Locations locations, Socialgroup socialgroup, Residency residency, Individual individual) {

        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUSTER_IDS, cluster_id);
        args.putParcelable(LOC_LOCATION_IDS, locations);
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
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
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
        binding.setLocations(locations);

        // Initialize the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Create a location request with maximum accuracy of 10
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setSmallestDisplacement(10); // 10 meters




        // Get a reference to the progress bar view
        ProgressBar progressBar = binding.getRoot().findViewById(R.id.progress_bar);

        // Get a reference to the button and set its OnClickListener
        binding.buttonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for location permissions
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                } else {
                    // Permission is granted, show the progress bar and start requesting location updates
                    progressBar.setVisibility(View.VISIBLE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            // Update the currentLocation variable with the new location
                            currentLocation = location;

                            // Display the longitude, latitude, and accuracy values in the TextInputEditText views
                            TextInputEditText longitudeEditText = requireView().findViewById(R.id.longitude);
                            longitudeEditText.setText(String.valueOf(currentLocation.getLongitude()));

                            TextInputEditText latitudeEditText = requireView().findViewById(R.id.latitude);
                            latitudeEditText.setText(String.valueOf(currentLocation.getLatitude()));

                            TextInputEditText accuracyEditText = requireView().findViewById(R.id.accuracy);
                            accuracyEditText.setText(String.valueOf(currentLocation.getAccuracy()));

                            // Check if the accuracy is less than or equal to 10 meters, dismiss the progress bar, and stop requesting location updates
                            if (currentLocation.getAccuracy() <= 10) {
                                progressBar.setVisibility(View.GONE);
                                locationManager.removeUpdates(this);
                            }
                        }
                    });
                }
            }
        });





        final Intent intent = getActivity().getIntent();
        final Hierarchy level6Data = intent.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);


        if(locations.locationLevel_uuid==null){
            binding.getLocations().locationLevel_uuid = level6Data.getUuid();
        }

        if(locations.compno==null){
            binding.getLocations().villcode = level6Data.getVillcode();
        }

        if(locations.fw_uuid==null){
            binding.getLocations().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(locations.insertDate==null){
            binding.getLocations().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        // Generate a UUID
        if(locations.location_uuid == null) {
            String uuid = UUID.randomUUID().toString();
            String uuidString = uuid.toString().replaceAll("-", "");
            // Set the ID of the Fieldworker object
            binding.getLocations().location_uuid = uuidString;
            }

        if (binding.getLocations().complete == null) {
            binding.getLocations().complete = 1;
        }
        binding.getLocations().site=1;

        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.locationstatus, codeBookViewModel, "status");
        loadCodeData(binding.complete, codeBookViewModel, "submit");
        loadCodeData(binding.edit, codeBookViewModel, "complete");
        loadCodeData(binding.locationtype, codeBookViewModel, "locationType");
        loadCodeData(binding.site, codeBookViewModel, "site");

        binding.buttonSubmit.setOnClickListener(v -> {
            final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            final Locations locations = binding.getLocations();
           locations.setCompextId(this.locations.getCompextId());

            boolean isExists = false;
            binding.locationextid.setError(null);
            binding.locationcluster.setError(null);
            binding.locationName.setError(null);
            binding.locationcompno.setError(null);
            binding.longitude.setError(null);
            binding.latitude.setError(null);

            boolean val = false;

            if (!binding.locationcompno.getText().toString().trim().isEmpty()) {
                String comp = binding.locationcompno.getText().toString().trim();
                if (binding.getLocations().site == 1 && comp.length() != 6) {
                    val = true;
                    binding.locationcompno.setError("Must be 6 Character Length");
                    return;
                }else if (binding.getLocations().site == 2 && comp.length() != 6) {
                    val = true;
                    binding.locationcompno.setError("Must be 6 Character Length");
                    return;
                }else if (binding.getLocations().site == 3 && comp.length() != 7) {
                    val = true;
                    binding.locationcompno.setError("Must be 7 Character Length");
                    return;
                }
            }



            try {
                Locations locations1 = null;
                if(locations.getCompno()!=null) {
                    Log.e("COMPNO","LOCATION IS " + isExists);
                    locations1 = locationViewModel.find(locations.getCompno());
                    if (locations1 != null && this.locations.edit==1) {
                        isExists = true;
                        binding.locationcompno.setError("Already Exists");
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

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All Fields Required", Toast.LENGTH_LONG).show();
                return;
            }

            boolean loc = false;
            boolean nhrc = false;
            boolean dhrc = false;

            if (!binding.locationcluster.getText().toString().trim().isEmpty() && !binding.locationcompno.getText().toString().trim().isEmpty() && binding.site.getSelectedItem() != null) {
                String vill = binding.locationcluster.getText().toString().trim();
                String locs = binding.locationcompno.getText().toString().trim();
                String site = binding.site.getSelectedItem().toString();

                if (!vill.startsWith(locs.substring(0, 2))) {
                    if (site.equals("KHDSS")) {
                        loc = true;
                    } else if (site.equals("NHDSS") && !vill.startsWith(locs.substring(0, 3))) {
                        nhrc = true;
                    } else if (site.equals("DHDSS") && !vill.startsWith(locs.substring(0, 4))) {
                        dhrc = true;
                    }

                    Toast.makeText(getActivity(), "Location Creation in Wrong Village", Toast.LENGTH_SHORT).show();
                    binding.locationcompno.setError("Location Creation in Wrong Village " + vill);
                    return;
                }
            }




            locationViewModel.add(locations);
            Toast.makeText(v.getContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                 BlankFragment.newInstance(individual, residency, locations, socialgroup)).commit();


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