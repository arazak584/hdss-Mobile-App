package org.openhds.hdsscapture.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.location.LocationRequest;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentCommunityBinding;
import org.openhds.hdsscapture.databinding.FragmentListingBinding;
import org.openhds.hdsscapture.entity.CommunityReport;
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

        // Create a location request with maximum accuracy of 10
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5); // 5 milliseconds
        locationRequest.setFastestInterval(0); // 0 seconds
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

                            EditText longitudeEditText = requireView().findViewById(R.id.longitude);
                            longitudeEditText.setText(formattedLongitude);

                            EditText latitudeEditText = requireView().findViewById(R.id.latitude);
                            latitudeEditText.setText(formattedLatitude);

                            EditText accuracyEditText = requireView().findViewById(R.id.accuracy);
                            accuracyEditText.setText(String.valueOf(currentLocation.getAccuracy()));

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

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, CommunityViewModel viewModel) {

        if (save) {
            final CommunityReport finalData = binding.getCommunity();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

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