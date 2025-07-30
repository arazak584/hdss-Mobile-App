package org.openhds.hdsscapture.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
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
import org.openhds.hdsscapture.Repositories.ListingRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentListingBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.ClusterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingViewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";

    private FragmentListingBinding binding;
    private LocationManager locationManager;
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
    private Listing listing;
    private ListingRepository repository;

    public ListingViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingViewFragment newInstance(String uuid) {
        ListingViewFragment fragment = new ListingViewFragment();
        Bundle args = new Bundle();
        args.putString(LOC_LOCATION_IDS, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new ListingRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(LOC_LOCATION_IDS); // Correct key
            this.listing = new Listing();  // Initialize placeholder
            this.listing.compextId = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListingBinding.inflate(inflater, container, false);

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

        LocationViewModel model = new ViewModelProvider(this).get(LocationViewModel.class);
        ListingViewModel viewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        viewModel.getView(listing.compextId).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setListing(data);

                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);

                String regex = "[A-Z]{2}\\d{4}";
                String input = data.compno;
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

                try {
                    Locations loc = model.find(data.compno);
                    if (loc != null) {
                        String villExtId = loc.vill_extId;
                        Log.d("Listing", "Village from Location: " + villExtId);

                        // Update the button click listener now that we have the location data
                        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_cluster);
                        showDialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Use the vill_extId from the Locations object (loc)
                                ClusterViewFragment.newInstance(villExtId)
                                        .show(getChildFragmentManager(), "ClusterViewFragment");
                            }
                        });
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error fetching location data", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                try {
                    // Update Location
                    Locations data = locationViewModel.find(compno);
                    if (data != null) {
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
                        locationz.locationLevel_uuid = finalData.cluster_id;
                        locationz.extId = finalData.vill_extId;

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
                        locationz.complete = 1;

                        locationViewModel.update(locationz, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("LocationFragment", "List Update successful!");
                                    } else {
                                        Log.d("LocationFragment", "List Update Failed!");
                                    }
                                })
                        );
                    }
                } catch (Exception e) {
                    Log.e("LocationFragment", "Error in update", e);
                    e.printStackTrace();
                }


            });

            executor.shutdown();

            viewModel.deleteByCompno(finalData.compno, () -> {
                viewModel.add(finalData);
            });

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