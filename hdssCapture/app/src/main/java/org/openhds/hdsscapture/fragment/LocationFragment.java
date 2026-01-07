package org.openhds.hdsscapture.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueUUIDGenerator;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentLocationBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";

    private Hierarchy cluster_id;
    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentLocationBinding binding;
    private LocationManager locationManager;
    private Location currentLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ProgressBar progressBar;
    private TextView statusText;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private EditText latitudeEditText, longitudeEditText, accuracyEditText, altitudeEditText;
    List<Configsettings> configsettings;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cluster_id  Parameter 1.
     * @param locations    Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */

    public static LocationFragment newInstance(Hierarchy cluster_id, Locations locations) {

        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUSTER_IDS, cluster_id);
        args.putParcelable(LOC_LOCATION_IDS, locations);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            cluster_id = getArguments().getParcelable(ARG_CLUSTER_IDS);
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = FragmentLocationBinding.inflate(inflater, container, false);
        if (locations == null) {
            locations = new Locations();
        }
        binding.setLocations(locations);

        // Initialize the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        // Get a reference to the progress bar view
        progressBar = binding.getRoot().findViewById(R.id.progress_bar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        statusText = binding.getRoot().findViewById(R.id.statusText);
        latitudeEditText = binding.getRoot().findViewById(R.id.latitude);
        longitudeEditText = binding.getRoot().findViewById(R.id.longitude);
        accuracyEditText = binding.getRoot().findViewById(R.id.accuracy);
        altitudeEditText = binding.getRoot().findViewById(R.id.altitude);

        binding.getRoot().findViewById(R.id.button_gps).setOnClickListener(v -> getLocation());


        final Intent intent = getActivity().getIntent();
        final Hierarchy level6Data = intent.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);


        if(locations.locationLevel_uuid==null){
            binding.getLocations().locationLevel_uuid = level6Data.getUuid();
        }

        if(locations.extId==null && locations.compno==null){
            binding.getLocations().extId = level6Data.getExtId();
        }

        if(locations.extId==null && locations.compno!=null){
            //binding.getLocations().extId = level6Data.getExtId();
            binding.getLocations().extId = level6Data.getExtId();
        }

        if(locations.vill_extId==null){
            binding.getLocations().vill_extId = level6Data.getParent_uuid();
        }

        if(locations.fw_uuid==null){
            binding.getLocations().fw_uuid = fieldworkerData.getFw_uuid();
        }

        if(locations.compno==null){
            binding.getLocations().edit = 1;
        }else{
            binding.getLocations().edit = 2;
        }

        ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        configsettings = null;
        try {
            configsettings = viewModel.findAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(locations.insertDate==null){
            binding.getLocations().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

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
            locations.sttime = timeString;
        }

        if(locations.compno!=null){
            binding.locationcompno.setEnabled(false);
            binding.locationtype.setEnabled(false);
            binding.locationstatus.setEnabled(false);
            binding.locationName.setEnabled(false);
        }

        binding.edit.setEnabled(false);

        // Generate a UUID
        if(locations.uuid == null) {
//            String uuid = UUID.randomUUID().toString();
//            String uuidString = uuid.toString().replaceAll("-", "");
//            // Set the ID of the Fieldworker object
//            binding.getLocations().uuid = uuidString;

            // Set the ID of the Location object
            binding.getLocations().uuid = UniqueUUIDGenerator.generate(getContext());
            }

        if (binding.getLocations().complete == null) {
            binding.getLocations().complete = 1;
        }

//        Spinner mySpinner = binding.getRoot().findViewById(R.id.site);
//        mySpinner.setSelection(0);

        // Check if binding.site is null or does not have a selected item
        if (binding.getLocations().site == null) {
            binding.getLocations().site = 1;
        }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.locationstatus, codeBookViewModel, "status");
        loadCodeData(binding.complete, codeBookViewModel, "submit");
        loadCodeData(binding.edit, codeBookViewModel, "complete");
        loadCodeData(binding.locationtype, codeBookViewModel, "locationType");
        loadCodeData(binding.site, codeBookViewModel, "site");

        binding.buttonSubmit.setOnClickListener(v -> {
            final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            final Locations locations = binding.getLocations();
           //locations.setCompextId(this.locations.getCompextId());

            boolean isExists = false;
            binding.locationextid.setError(null);
            binding.locationcluster.setError(null);
            binding.locationName.setError(null);
            binding.locationcompno.setError(null);
            binding.longitude.setError(null);
            binding.latitude.setError(null);

            boolean val = false;
            boolean fmt = false;

            if (!binding.locationcompno.getText().toString().isEmpty()) {
                String comp = binding.locationcompno.getText().toString();

                if (comp.contains(" ")) {
                    binding.locationcompno.setError("Spaces are not allowed");
                    Toast.makeText(getActivity(), "Spaces are not allowed in Compound Number", Toast.LENGTH_LONG).show();
                    val = true;
                    return;
                }

                comp = comp.trim();
                String cmp = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).compno : null;
                //cmp = cmp.trim();
                cmp = (cmp != null) ? cmp.trim() : null;
                Log.d("ComActivity", "Compno Format: "+ cmp);
                int cmplength = (cmp != null) ? cmp.trim().length() : 0;

                String villExtId = level6Data.getExtId();

                if (cmplength != comp.length()){
                    binding.locationcompno.setError("Must be " + cmplength + " characters in length");
                    Toast.makeText(getActivity(), "Must be " + cmplength + " characters in length", Toast.LENGTH_LONG).show();
                    val = true;
                    return;
                }

                // Step 1: Separate letters and digits in cmp
                Pattern pattern = Pattern.compile("^([A-Za-z]+)([0-9]+)$");
                Matcher matcher = pattern.matcher(cmp);

                if (matcher.find()) {
                    int letterCount = matcher.group(1).length();
                    int digitCount = matcher.group(2).length();

                    // Build regex pattern like ^[A-Za-z]{4}[0-9]{3}$
                    String formatPattern = "^[A-Za-z]{" + letterCount + "}[0-9]{" + digitCount + "}$";

                    if (!comp.matches(formatPattern)) {
                        binding.locationcompno.setError("Compound Number format must match " + letterCount + " letters and " + digitCount + " digits");
                        Toast.makeText(getActivity(), "Compound Number format is incorrect", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    }
                } else {
                    // cmp is not in expected letter+digit format
                    binding.locationcompno.setError("Reference compound number format is invalid");
                    val = true;
                    return;
                }


//                boolean fmat = false;
//                if (cmp != null && !comp.matches(cmp)){
//                    fmat = true;
//                    Toast.makeText(getActivity(), "Compound Number format is incorrect", Toast.LENGTH_LONG).show();
//                    binding.locationcompno.setError("Compound Number format is incorrect");
//                    return;
//                }

//                if (binding.getLocations().site == 1 && comp.length() != 6) {
//                    binding.locationcompno.setError("Must be 6 characters in length");
//                    Toast.makeText(getActivity(), "Must be 6 characters in length", Toast.LENGTH_LONG).show();
//                    val = true;
//                    return;
//                } else if (binding.getLocations().site == 2 && comp.length() != 6) {
//                    binding.locationcompno.setError("Must be 6 characters in length");
//                    Toast.makeText(getActivity(), "Must be 6 characters in length", Toast.LENGTH_LONG).show();
//                    val = true;
//                    return;
//                } else if (binding.getLocations().site == 3 && comp.length() != 7) {
//                    binding.locationcompno.setError("Must be 7 characters in length");
//                    Toast.makeText(getActivity(), "Must be 7 characters in length", Toast.LENGTH_LONG).show();
//                    val = true;
//                    return;
//                }


//                boolean khd = false;
//                //String regex = "[A-Z]{2}\\d{4}"; // Two uppercase letters followed by four digits
//                String regnn = "[A-Z]{3}\\d{3}"; // Three uppercase letters followed by three digits
//                String regdd = "[A-Z]{4}\\d{3}"; // Four uppercase letters followed by three digits
//
//                String regex = "[A-Z]{2}\\d{4}";
//                if (!binding.locationcompno.getText().toString().trim().isEmpty() && binding.getLocations().site == 1) {
//                    String input = binding.locationcompno.getText().toString().trim();
//                    if (!input.matches(regex)) {
//                        khd = true;
//                        Toast.makeText(getActivity(), "Compound Number format is incorrect", Toast.LENGTH_LONG).show();
//                        binding.locationcompno.setError("Compound Number format is incorrect");
//                        return;
//                    }
//                } else if (!binding.locationcompno.getText().toString().trim().isEmpty() && binding.getLocations().site == 2) {
//                    String input = binding.locationcompno.getText().toString().trim();
//                    if (!input.matches(regnn)) {
//                        khd = true;
//                        Toast.makeText(getActivity(), "Compound Number format is incorrect", Toast.LENGTH_LONG).show();
//                        binding.locationcompno.setError("Compound Number format is incorrect");
//                        return;
//                    }
//                } else if (!binding.locationcompno.getText().toString().trim().isEmpty() && binding.getLocations().site == 3) {
//                    String input = binding.locationcompno.getText().toString().trim();
//                    if (!input.matches(regdd)) {
//                        khd = true;
//                        Toast.makeText(getActivity(), "Compound Number format is incorrect", Toast.LENGTH_LONG).show();
//                        binding.locationcompno.setError("Compound Number format is incorrect");
//                        return;
//                    }
//                }
            }


            try {
                Locations locations1 = null;
                if(locations.getCompno()!=null) {
                    Log.e("COMPNO","LOCATION IS " + isExists);
                    locations1 = locationViewModel.find(locations.getCompno());
                    if (locations1 != null) {
                        isExists = true;
                        binding.locationcompno.setError("Already Exists");
                        Toast.makeText(getActivity(), "Compound Number Already Exists", Toast.LENGTH_LONG).show();
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
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All Fields Required", Toast.LENGTH_LONG).show();
                return;
            }

            boolean loc = false;
            String compno = binding.locationcompno.getText().toString().trim();
            String extid = binding.locationextid.getText().toString().trim();

            if (!compno.isEmpty() && !extid.isEmpty()) {
                // Extract leading letters from compno (e.g. XA from XA0001)
                String compnoLetters = compno.replaceAll("[^A-Za-z]", "");
                int letterCount = compnoLetters.length();

                // Extract first N letters from extid
                String extidLetters = extid.replaceAll("[^A-Za-z]", "");
                String extidPrefix = extidLetters.length() >= letterCount
                        ? extidLetters.substring(0, letterCount)
                        : extidLetters; // in case extid is too short

                if (!compnoLetters.equals(extidPrefix)) {
                    Toast.makeText(getActivity(), "Location Creation in Wrong Village", Toast.LENGTH_LONG).show();
                    binding.locationcompno.setError("Expected to start with: " + extidPrefix);
                    loc = true;
                    return;
                }
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

            if (locations.sttime !=null && locations.edtime==null){
                locations.edtime = endtime;
            }
            locations.complete = 1;
            locationViewModel.add(locations);
            //Toast.makeText(v.getContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                 ClusterFragment.newInstance(level6Data,locations, socialgroup)).commit();


        });

        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View v = binding.getRoot();
        return v;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        if (progressBar == null) {
            Toast.makeText(requireContext(), "Error: progressBar is null", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Getting location...");
        statusText.setVisibility(View.VISIBLE);

        if (isInternetAvailable()) {
            // Use faster network-based location first
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            updateLocationUI(location);
                            if (location.getAccuracy() <= 10.0f) {
                                progressBar.setVisibility(View.GONE);
                                statusText.setText("Target accuracy reached: " + location.getAccuracy() + "m");
                            } else {
                                requestNewLocation();
                            }
                        } else {
                            requestNewLocation();
                        }
                    });
        } else {
            // If no internet, use GPS directly
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null && System.currentTimeMillis() - location.getTime() < 60000) {
                    updateLocationUI(location);
                    if (location.getAccuracy() <= 10.0f) {
                        progressBar.setVisibility(View.GONE);
                        statusText.setText("Target accuracy reached: " + location.getAccuracy() + "m");
                    } else {
                        requestNewLocation();
                    }
                } else {
                    requestNewLocation();
                }
            });
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // Use GPS when needed
                .setInterval(1000) // Increased interval to 1 second
                .setFastestInterval(1000)
                .setNumUpdates(10); // Reduced number of updates

        locationCallback = new LocationCallback() {
            private Location bestLocation;

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = location;
                        updateLocationUI(bestLocation);
                    }
                    if (bestLocation.getAccuracy() <= 10.0f) {
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        progressBar.setVisibility(View.GONE);
                        statusText.setText("Target accuracy reached: " + bestLocation.getAccuracy() + "m");
                        return;
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocationUI(Location location) {
        latitudeEditText.setText(String.format("%.6f", location.getLatitude()));
        longitudeEditText.setText(String.format("%.6f", location.getLongitude()));
        accuracyEditText.setText(String.valueOf(location.getAccuracy()));
        //altitudeEditText.setText(String.valueOf(location.getAltitude()));
        altitudeEditText.setText(String.format("%.4f", location.getAltitude()));

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