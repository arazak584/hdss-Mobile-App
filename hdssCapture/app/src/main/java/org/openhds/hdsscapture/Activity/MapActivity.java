package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Locations;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity {

    private MapView map;
    private LocationViewModel locationViewModel;
    private Map<String, Marker> markerMap = new HashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Fieldworker fieldworkerDatas;

    // Define the bounding box coordinates
    private static final double MIN_LATITUDE = 4.5;
    private static final double MAX_LATITUDE = 11.0;
    private static final double MIN_LONGITUDE = -3.5;
    private static final double MAX_LONGITUDE = 1.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Load osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        final Intent f = getIntent();
        fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        // Initialize the map
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        // Set the initial map zoom level and center position
        IMapController mapController = map.getController();
        mapController.setZoom(9.0);

        // Set up the search input field
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchLocation(searchInput.getText().toString().toUpperCase());
                    return true;
                }
                return false;
            }
        });

        // Load locations and display them on the map
        loadLocations();
    }

    private void loadLocations() {
        executor.submit(() -> {
            try {
                List<Locations> locations = locationViewModel.retrieveAll(fieldworkerDatas.getUsername());
                runOnUiThread(() -> {
                    if (locations != null) {
                        for (Locations location : locations) {
                            double latitude = Double.parseDouble(location.latitude);
                            double longitude = Double.parseDouble(location.longitude);

                            // Check if the location is within the bounding box
                            if (latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE &&
                                    longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE) {

                                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                                Marker marker = new Marker(map);
                                marker.setPosition(geoPoint);
                                marker.setTitle(location.compno);
                                marker.setSnippet(location.locationName);

                                // Set the marker color based on the 'complete' attribute
                                if (location.complete == null) {
                                    marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
                                } else {
                                    marker.setIcon(getResources().getDrawable(R.drawable.marker_green));
                                }

                                map.getOverlays().add(marker);
                                markerMap.put(location.compno, marker);
                            }
                        }

                        if (!locations.isEmpty()) {
                            Locations firstLocation = locations.get(0);
                            double latitude = Double.parseDouble(firstLocation.latitude);
                            double longitude = Double.parseDouble(firstLocation.longitude);
                            if (latitude != 0.0 && longitude != 0.0) {
                                map.getController().setCenter(new GeoPoint(latitude, longitude));
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchLocation(String compno) {
        Marker marker = markerMap.get(compno);
        if (marker != null) {
            map.getController().animateTo(marker.getPosition());
            marker.showInfoWindow();
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }
}