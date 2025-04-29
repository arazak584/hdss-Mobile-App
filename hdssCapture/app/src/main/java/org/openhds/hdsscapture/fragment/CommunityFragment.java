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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.databinding.FragmentCommunityBinding;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends DialogFragment {

    private static final String COM_IDS = "COM_IDS";

    private Hierarchy level5Data;

    private FragmentCommunityBinding binding;
    private LocationManager locationManager;
    private Location currentLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ProgressBar progressBar;
    private TextView statusText;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private EditText latitudeEditText, longitudeEditText, accuracyEditText;

    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance() {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_community, container, false);
        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        final Intent j = getActivity().getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final TextView com = binding.getRoot().findViewById(R.id.txt_community);
        com.setText(level5Data.name);

        // Initialize the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Get a reference to the progress bar view
        progressBar = binding.getRoot().findViewById(R.id.progress_bar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        statusText = binding.getRoot().findViewById(R.id.statusText);
        latitudeEditText = binding.getRoot().findViewById(R.id.latitude);
        longitudeEditText = binding.getRoot().findViewById(R.id.longitude);
        accuracyEditText = binding.getRoot().findViewById(R.id.accuracy);

        //Pick GPS
        binding.getRoot().findViewById(R.id.button_gps).setOnClickListener(v -> getLocation());

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        CommunityViewModel viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        try {
            CommunityReport data = viewModel.retrieve();
            if (data != null) {

            } else {
                data = new CommunityReport();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.uuid = uuid;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.community = level5Data.getName();
                binding.setCommunity(data);
                binding.getCommunity().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.itemlist, "itemlist");

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

    private void save(boolean save, boolean close, CommunityViewModel viewModel) {

        if (save) {
            final CommunityReport finalData = binding.getCommunity();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }
            finalData.complete=1;
            viewModel.add(finalData);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
        if (close) {


            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    CommunityReportFragment.newInstance(level5Data)).commit();
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