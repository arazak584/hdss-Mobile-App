package org.openhds.hdsscapture.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationRequest;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.ClusterDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentListingBinding;
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

        // Create a location request with maximum accuracy of 10
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(1000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setSmallestDisplacement(50);
        locationRequest.setInterval(5); // 5 milliseconds
        locationRequest.setFastestInterval(0); // 0 seconds
        //locationRequest.setSmallestDisplacement(50); // 10 meters
        locationRequest.setNumUpdates(1);


        // Get a reference to the progress bar view
        ProgressBar progressBar = binding.getRoot().findViewById(R.id.progress_bar);

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
                            double latitude = currentLocation.getLatitude();
                            double longitude = currentLocation.getLongitude();

                            // Define the number of decimal places you want to display (e.g., 6)
                            int decimalPlaces = 6;

                            // Format the latitude and longitude to the specified number of decimal places
                            String formattedLatitude = String.format("%.6f", latitude);
                            String formattedLongitude = String.format("%.6f", longitude);

                            // Display the longitude, latitude, and accuracy values in the EditText views
                            EditText longitudeEditText = requireView().findViewById(R.id.longitude);
//                            longitudeEditText.setText(String.valueOf(currentLocation.getLongitude()));
                            longitudeEditText.setText(formattedLongitude);

                            EditText latitudeEditText = requireView().findViewById(R.id.latitude);
                            latitudeEditText.setText(formattedLatitude);
//                            latitudeEditText.setText(String.valueOf(currentLocation.getLatitude()));

                            EditText accuracyEditText = requireView().findViewById(R.id.accuracy);
                            accuracyEditText.setText(String.valueOf(currentLocation.getAccuracy()));

                            // Check if the accuracy is less than or equal to 10 meters, dismiss the progress bar, and stop requesting location updates
                            if (currentLocation.getAccuracy() <= 10) {
                                progressBar.setVisibility(View.GONE);
                                locationManager.removeUpdates(this);
                            }
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            // Handle the case when the location provider is disabled
                            // For example, you can display a message or prompt the user to enable the location provider
                        }

                        // Other methods of LocationListener
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }
                    });
                }
            }
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent intent = getActivity().getIntent();
        final Hierarchy level5Data = intent.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        ListingViewModel viewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        try {
            Listing data = viewModel.find(ClusterFragment.selectedLocation.compno);
            if (data != null) {
                binding.setListing(data);
                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);
            } else {
                data = new Listing();

                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.compextId = ClusterFragment.selectedLocation.getCompextId();
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.status = ClusterFragment.selectedLocation.getStatus();
                data.village = level6Data.getName();
                data.fw_name = fieldworkerData.getFirstName() + ' ' + fieldworkerData.lastName ;
                data.locationName = ClusterFragment.selectedLocation.getLocationName();
                data.location_uuid = ClusterFragment.selectedLocation.getUuid();
                data.vill_extId = level6Data.getExtId();
                data.cluster_id = ClusterFragment.selectedLocation.getLocationLevel_uuid();
                data.longitude = ClusterFragment.selectedLocation.getLongitude();
                data.latitude = ClusterFragment.selectedLocation.getLatitude();
                data.accuracy = ClusterFragment.selectedLocation.getAccuracy();

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

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ListingViewModel viewModel) {

        if (save) {
            final Listing finalData = binding.getListing();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }
            finalData.complete=1;
            viewModel.add(finalData);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

            LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            try {

                Locations data = locationViewModel.find(ClusterFragment.selectedLocation.compno);
                if (data !=null) {

                    LocationAmendment locationz = new LocationAmendment();
                    locationz.uuid = finalData.location_uuid;

                    if (!binding.repllocationName.getText().toString().trim().isEmpty()) {
                        locationz.locationName = binding.getListing().repl_locationName;
                    } else {
                        locationz.locationName = finalData.locationName;
                    }
                    locationz.status = finalData.status;
                    locationz.locationLevel_uuid = finalData.cluster_id;
                    locationz.extId = finalData.vill_extId;
                    locationz.longitude = finalData.longitude;
                    locationz.latitude = finalData.latitude;
                    locationz.accuracy = finalData.accuracy;

                    // Assuming vill_extId is a String
                    String villExtId = finalData.vill_extId;
                    String compExtId = finalData.compextId;

                    // Determine the desired format for compExtId based on the length of villExtId
                    if (villExtId.length() == 3) {
                        // If vill_extId has 3 characters, pick the last 6 numbers from finalData.compextId
                        int startIndex = Math.max(finalData.compextId.length() - 6, 0);
                        compExtId = villExtId + finalData.compextId.substring(startIndex);
                    } else if (villExtId.length() == 4) {
                        // If vill_extId has 4 characters, pick the last 5 numbers from finalData.compextId
                        int startIndex = Math.max(finalData.compextId.length() - 5, 0);
                        compExtId = villExtId + finalData.compextId.substring(startIndex);
                    } else {
                        // Handle other cases or validation as needed
                        compExtId = finalData.compextId; // Keep the original value
                    }

                    locationz.compextId = compExtId;
                    locationz.complete = 1;

                    locationViewModel.update(locationz);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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