package org.openhds.hdsscapture.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.OSRMRoutingHelper;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Locations;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements LocationListener {

    private MapView map;
    private LocationViewModel locationViewModel;
    private final Map<String, Marker> markerMap = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Fieldworker fieldworkerDatas;
    private MyLocationNewOverlay myLocationOverlay;
    private OSRMRoutingHelper routingHelper;
    private Polyline currentRoute;
    private TextView routeInfoText;
    private Button clearRouteButton;
    private GeoPoint userLocation;
    private LocationManager locationManager;

    // Filter UI elements
    private AutoCompleteTextView villageNameInput;
    private AutoCompleteTextView compnoInput;
    private Button applyFilterButton;
    private Button clearFilterButton;
    private ArrayAdapter<String> villageAdapter;
    private ArrayAdapter<String> compnoAdapter;

    // Define the bounding box coordinates
    private static final double MIN_LATITUDE = 4.5;
    private static final double MAX_LATITUDE = 11.0;
    private static final double MIN_LONGITUDE = -3.5;
    private static final double MAX_LONGITUDE = 1.5;
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Load osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        routingHelper = new OSRMRoutingHelper();

        final Intent f = getIntent();
        fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        // Initialize the map
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        // Initialize UI elements
        routeInfoText = findViewById(R.id.routeInfoText);
        clearRouteButton = findViewById(R.id.clearRouteButton);
        clearRouteButton.setVisibility(View.GONE);
        clearRouteButton.setOnClickListener(v -> clearRoute());

        // Initialize filter UI elements
        villageNameInput = findViewById(R.id.villageNameInput);
        compnoInput = findViewById(R.id.compnoInput);
        applyFilterButton = findViewById(R.id.applyFilterButton);
        clearFilterButton = findViewById(R.id.clearFilterButton);

        // Set the initial map zoom level and center position
        IMapController mapController = map.getController();
        mapController.setZoom(9.0);

        // Setup location tracking
        setupLocationTracking();

        // Setup autocomplete
        setupAutocomplete();

        // Setup filter buttons
        applyFilterButton.setOnClickListener(v -> applyFilters());
        clearFilterButton.setOnClickListener(v -> clearFilters());

        // Load locations and display them on the map
        loadLocations(null, null);
    }

    private void setupAutocomplete() {
        // Initialize adapters
        villageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        compnoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());

        villageNameInput.setAdapter(villageAdapter);
        compnoInput.setAdapter(compnoAdapter);

        // Add text watchers for dynamic autocomplete
        villageNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    searchVillageNames(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        compnoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    searchCompno(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load initial autocomplete data
        loadAutocompleteData();
    }

    private void loadAutocompleteData() {
        executor.submit(() -> {
            try {
                List<String> villages = locationViewModel.getAllVillageNames();
                List<String> compnos = locationViewModel.getAllCompno();

                runOnUiThread(() -> {
                    villageAdapter.clear();
                    villageAdapter.addAll(villages);
                    villageAdapter.notifyDataSetChanged();

                    compnoAdapter.clear();
                    compnoAdapter.addAll(compnos);
                    compnoAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error loading autocomplete data", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchVillageNames(String query) {
        executor.submit(() -> {
            try {
                List<String> results = locationViewModel.searchVillageNames(query);
                runOnUiThread(() -> {
                    villageAdapter.clear();
                    villageAdapter.addAll(results);
                    villageAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void searchCompno(String query) {
        executor.submit(() -> {
            try {
                List<String> results = locationViewModel.searchCompno(query);
                runOnUiThread(() -> {
                    compnoAdapter.clear();
                    compnoAdapter.addAll(results);
                    compnoAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void applyFilters() {
        String villageName = villageNameInput.getText().toString().trim();
        String compno = compnoInput.getText().toString().trim();

        if (villageName.isEmpty() && compno.isEmpty()) {
            Toast.makeText(this, "Please enter at least one filter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear existing markers
        clearMapMarkers();

        // Load filtered locations
        loadLocations(villageName.isEmpty() ? null : villageName, compno.isEmpty() ? null : compno);
    }

    private void clearFilters() {
        villageNameInput.setText("");
        compnoInput.setText("");
        clearMapMarkers();
        loadLocations(null, null);
    }

    private void clearMapMarkers() {
        // Remove all markers except location overlay
        map.getOverlays().clear();
        if (myLocationOverlay != null) {
            map.getOverlays().add(myLocationOverlay);
        }
        markerMap.clear();
        map.invalidate();
    }

    private void setupLocationTracking() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        // Add "My Location" overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);

        // Get location manager for updates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        // Get last known location
        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown != null) {
            userLocation = new GeoPoint(lastKnown.getLatitude(), lastKnown.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationTracking();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void loadLocations(String villageName, String compno) {
        executor.submit(() -> {
            try {
                List<Locations> locations;

                // Apply filters based on input
                if (villageName != null && compno != null) {
                    locations = locationViewModel.filterByVillageAndCompno(villageName, compno);
                } else if (villageName != null) {
                    locations = locationViewModel.filterByVillageName(villageName);
                } else if (compno != null) {
                    locations = locationViewModel.filterByCompno(compno);
                } else {
                    locations = locationViewModel.retrieveAll();
                }

                final List<Locations> finalLocations = locations;
                runOnUiThread(() -> {
                    displayLocationsOnMap(finalLocations);
                    if (finalLocations == null || finalLocations.isEmpty()) {
                        Toast.makeText(MapActivity.this, "No locations found matching filters", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapActivity.this, finalLocations.size() + " locations found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void displayLocationsOnMap(List<Locations> locations) {
        if (locations != null) {
            for (Locations location : locations) {
                try {
                    double latitude = Double.parseDouble(location.latitude);
                    double longitude = Double.parseDouble(location.longitude);

                    if (isWithinBoundingBox(latitude, longitude)) {
                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                        Marker marker = createMarker(location, geoPoint);
                        map.getOverlays().add(marker);
                        markerMap.put(location.compno, marker);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (!locations.isEmpty()) {
                centerMapOnFirstLocation(locations.get(0));
            }
        }
    }

    private boolean isWithinBoundingBox(double latitude, double longitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE &&
                longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    private Marker createMarker(Locations location, GeoPoint geoPoint) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setTitle(location.compno);
        marker.setSnippet(location.locationName);
        marker.setIcon(getResources().getDrawable(location.complete == null ? R.drawable.marker_red : R.drawable.marker_green));

        // Add click listener to show route
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            showRouteDialog(location, geoPoint);
            return true;
        });

        return marker;
    }

    private void showRouteDialog(Locations location, GeoPoint destination) {
        if (userLocation == null) {
            Toast.makeText(this, "Current location not available. Please enable GPS.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Get Directions")
                .setMessage("Show route to " + location.locationName + "?")
                .setPositiveButton("Show Route", (dialog, which) -> getAndDisplayRoute(destination))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void getAndDisplayRoute(GeoPoint destination) {
        if (userLocation == null) {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear existing route
        clearRoute();

        // Show loading message
        Toast.makeText(this, "Calculating route...", Toast.LENGTH_SHORT).show();

        routingHelper.getRoute(userLocation, destination, new OSRMRoutingHelper.RoutingCallback() {
            @Override
            public void onRoutingSuccess(List<GeoPoint> routePoints, double distanceKm, double durationMinutes) {
                // Draw route on map
                currentRoute = OSRMRoutingHelper.drawRouteOnMap(map, routePoints);

                // Display route information
                String info = String.format("Distance: %.2f km\nEst. Time: %.0f min", distanceKm, durationMinutes);
                routeInfoText.setText(info);
                routeInfoText.setVisibility(View.VISIBLE);
                clearRouteButton.setVisibility(View.VISIBLE);

                Toast.makeText(MapActivity.this, "Route calculated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRoutingFailure(String error) {
                Toast.makeText(MapActivity.this, "Failed to calculate route: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearRoute() {
        if (currentRoute != null) {
            OSRMRoutingHelper.removeRouteFromMap(map, currentRoute);
            currentRoute = null;
        }
        routeInfoText.setVisibility(View.GONE);
        clearRouteButton.setVisibility(View.GONE);
    }

    private void centerMapOnFirstLocation(Locations location) {
        try {
            double latitude = Double.parseDouble(location.latitude);
            double longitude = Double.parseDouble(location.longitude);
            if (latitude != 0.0 && longitude != 0.0) {
                map.getController().setCenter(new GeoPoint(latitude, longitude));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MapActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        MapActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}