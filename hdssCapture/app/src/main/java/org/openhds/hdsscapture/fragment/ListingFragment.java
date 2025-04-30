package org.openhds.hdsscapture.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.ClusterDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentListingBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";

    private Locations locations;
    private Hierarchy level6Data;
    private Socialgroup socialgroup;
    private FragmentListingBinding binding;
    public static  Locations selectedLocation;
    private LocationManager locationManager;
    private Location currentLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ProgressBar progressBar;
    private TextView statusText;
    private String fw;
    private String fwname;
    private EditText latitudeEditText, longitudeEditText, accuracyEditText, altitudeEditText;
    private AppCompatEditText comp;
    List<Configsettings> configsettings;

    public ListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @return A new instance of fragment ListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingFragment newInstance(Locations locations) {
        ListingFragment fragment = new ListingFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListingBinding.inflate(inflater, container, false);

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_cluster);
        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                ClusterDialogFragment.newInstance(locations)
                        .show(getChildFragmentManager(), "ClusterDialogFragment");
            }
        });

        // Initialize the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Get a reference to the progress bar view
        progressBar = binding.getRoot().findViewById(R.id.progress_bar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        statusText = binding.getRoot().findViewById(R.id.statusText);
        latitudeEditText = binding.getRoot().findViewById(R.id.latitude);
        longitudeEditText = binding.getRoot().findViewById(R.id.longitude);
        accuracyEditText = binding.getRoot().findViewById(R.id.accuracy);
        altitudeEditText = binding.getRoot().findViewById(R.id.altitude);
        comp = binding.getRoot().findViewById(R.id.locationcompno);

        binding.getRoot().findViewById(R.id.button_gps).setOnClickListener(v -> getLocation());

//        binding.buttonGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Check for location permissions
//                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted, request it
//                    ActivityCompat.requestPermissions(requireActivity(),
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                            REQUEST_LOCATION_PERMISSION);
//                } else {
//                    // Permission is granted, show the progress bar and start requesting location updates
//                    progressBar.setVisibility(View.VISIBLE);
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(@NonNull Location location) {
//                            // Update the currentLocation variable with the new location
//                            currentLocation = location;
//                            double latitude = currentLocation.getLatitude();
//                            double longitude = currentLocation.getLongitude();
//
//                            // Define the number of decimal places you want to display (e.g., 6)
//                            int decimalPlaces = 6;
//
//                            // Format the latitude and longitude to the specified number of decimal places
//                            String formattedLatitude = String.format("%.6f", latitude);
//                            String formattedLongitude = String.format("%.6f", longitude);
//
//                            // Display the longitude, latitude, and accuracy values in the EditText views
//                            EditText longitudeEditText = requireView().findViewById(R.id.longitude);
////                            longitudeEditText.setText(String.valueOf(currentLocation.getLongitude()));
//                            longitudeEditText.setText(formattedLongitude);
//
//                            EditText latitudeEditText = requireView().findViewById(R.id.latitude);
//                            latitudeEditText.setText(formattedLatitude);
////                            latitudeEditText.setText(String.valueOf(currentLocation.getLatitude()));
//
//                            EditText accuracyEditText = requireView().findViewById(R.id.accuracy);
//                            accuracyEditText.setText(String.valueOf(currentLocation.getAccuracy()));
//
//                            // Check if the accuracy is less than or equal to 10 meters, dismiss the progress bar, and stop requesting location updates
//                            if (currentLocation.getAccuracy() <= 10) {
//                                progressBar.setVisibility(View.GONE);
//                                locationManager.removeUpdates(this);
//                            }
//                        }
//
//                        @Override
//                        public void onProviderDisabled(String provider) {
//                            // Handle the case when the location provider is disabled
//                            // For example, you can display a message or prompt the user to enable the location provider
//                        }
//
//                        // Other methods of LocationListener
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {
//                        }
//
//                        @Override
//                        public void onProviderEnabled(String provider) {
//                        }
//                    });
//                }
//            }
//        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        // Retrieve fw_uuid from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);
        fwname = sharedPreferences.getString(LoginActivity.FW_NAME, null);

        final Intent intent = getActivity().getIntent();
        final Hierarchy level5Data = intent.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        ConfigViewModel cviewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        configsettings = null;
        try {
            configsettings = cviewModel.findAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        ListingViewModel viewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        try {
            Listing data = viewModel.find(ClusterFragment.selectedLocation.compno);
            if (data != null) {
                binding.setListing(data);
                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);

                String regex = "[A-Z]{2}\\d{4}";
                String input = ClusterFragment.selectedLocation.getCompno();
                if (!input.matches(regex)){
                    binding.buttonChangeCluster.setEnabled(false);
                }else{
                    binding.buttonChangeCluster.setEnabled(true);
                }

                if (data.edit_compno != null && data.edit_compno ==1){
                    binding.locationcompno.setEnabled(true);
                }else{
                    binding.locationcompno.setEnabled(false);
                }


            } else {
                data = new Listing();

                data.fw_uuid = fw;
                //data.compextId = ClusterFragment.selectedLocation.getCompextId();
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.status = ClusterFragment.selectedLocation.getStatus();
                data.village = level6Data.getName();
                data.fw_name = fwname;
                data.locationName = ClusterFragment.selectedLocation.getLocationName();
                data.location_uuid = ClusterFragment.selectedLocation.getUuid();
                data.vill_extId = level6Data.getExtId();
                data.cluster_id = ClusterFragment.selectedLocation.getLocationLevel_uuid();
                //data.longitude = ClusterFragment.selectedLocation.getLongitude();
                //data.latitude = ClusterFragment.selectedLocation.getLatitude();
                //data.accuracy = ClusterFragment.selectedLocation.getAccuracy();
                data.compextId = ClusterFragment.selectedLocation.getCompextId();

                String regex = "[A-Z]{2}\\d{4}";
                String input = ClusterFragment.selectedLocation.getCompno();
                if (!input.matches(regex)){
                    binding.buttonChangeCluster.setEnabled(false);
                }else{
                    binding.buttonChangeCluster.setEnabled(true);
                }

                if (data.edit_compno != null && data.edit_compno ==1){
                    binding.locationcompno.setEnabled(true);
                }else{
                    binding.locationcompno.setEnabled(false);
                }

                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);


                binding.setListing(data);
                binding.getListing().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.locationstatus, "status");
        loadCodeData(binding.complete, "submit");
        loadCodeData(binding.correct, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {


            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ListingViewModel viewModel) {

        if (save) {
            final Listing finalData = binding.getListing();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            boolean val = false;
            boolean fmt = false;
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

           // String villExtId = level6Data.getExtId();

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

            finalData.complete=1;
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

            LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            try {

                Locations data = locationViewModel.find(ClusterFragment.selectedLocation.compno);
                if (data !=null) {

                    LocationAmendment locationz = new LocationAmendment();
                    locationz.uuid = finalData.location_uuid;
                    locationz.compno = finalData.compno;
                    locationz.compextId = finalData.compextId;

                    if (!binding.repllocationName.getText().toString().trim().isEmpty()) {
                        locationz.locationName = binding.getListing().repl_locationName;
                    } else {
                        locationz.locationName = finalData.locationName;
                    }
                    locationz.status = binding.getListing().status;
                    locationz.locationLevel_uuid = binding.getListing().cluster_id;
                    locationz.extId = binding.getListing().vill_extId;

                    Log.d("Listing", "Compound Status: "+ finalData.status);

                    if (finalData.status!=null && finalData.status == 3){
                        locationz.longitude = data.longitude;
                        locationz.latitude = data.latitude;
                        locationz.accuracy = data.accuracy;
                        locationz.altitude = data.altitude;

                    }else{
                        locationz.longitude = binding.getListing().longitude;
                        locationz.latitude = binding.getListing().latitude;
                        locationz.accuracy = binding.getListing().accuracy;
                        locationz.altitude = binding.getListing().altitude;
                    }

//                    // Assuming vill_extId is a String
//                    String villExtId = binding.getListing().vill_extId;
//                    String compExtId = finalData.compextId;
//                    Log.d("Listing", "Generated Villcode: " + villExtId);
//
//                    // Determine the desired format for compExtId based on the length of villExtId
//                    if (villExtId.length() == 3) {
//                        // If vill_extId has 3 characters, pick the last 4 numbers from finalData.compno
//                        int startIndex = Math.max(finalData.compno.length() - 4, 0);
//                        compExtId = villExtId + "00" + finalData.compno.substring(startIndex);
//                        Log.d("Listing", "Generated Compno: " + compExtId);
//                    } else if (villExtId.length() == 4) {
//                        // If vill_extId has 4 characters, pick the last 3 numbers from finalData.compno
//                        int startIndex = Math.max(finalData.compno.length() - 3, 0);
//                        compExtId = villExtId + "00" + finalData.compno.substring(startIndex);
//                        Log.d("Listing", "Generated Compno: " + compExtId);
//
//                    } else {
//                        // Handle other cases or validation as needed
//                        compExtId = finalData.compextId; // Keep the original value
//                    }
//
//                    locationz.compextId = compExtId;
                    locationz.complete = 1;

                    locationViewModel.update(locationz);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            viewModel.add(finalData);

        }
        if (close) {


            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    ClusterFragment.newInstance(level6Data,locations, socialgroup)).commit();
        }
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
        altitudeEditText.setText(String.format("%.4f", location.getAltitude()));
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
}