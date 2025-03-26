package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.MainActivity;
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
    private final Map<String, Marker> markerMap = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Fieldworker fieldworkerDatas;

    // Define the bounding box coordinates
    private static final double MIN_LATITUDE = 4.5;
    private static final double MAX_LATITUDE = 11.0;
    private static final double MIN_LONGITUDE = -3.5;
    private static final double MAX_LONGITUDE = 1.5;
    private String fw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Load osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        final Intent f = getIntent();
        fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        // Retrieve fw_uuid from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);

        // Initialize the map
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        // Set the initial map zoom level and center position
        IMapController mapController = map.getController();
        mapController.setZoom(9.0);

        // Set up the search input field
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchLocation(searchInput.getText().toString().toUpperCase());
                return true;
            }
            return false;
        });

        // Load locations and display them on the map
        loadLocations();
    }

    private void loadLocations() {
        executor.submit(() -> {
            try {
                List<Locations> locations = locationViewModel.retrieveAll(fw);
                runOnUiThread(() -> displayLocationsOnMap(locations));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show());
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
        return marker;
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

    private void searchLocation(String compno) {
        Marker marker = markerMap.get(compno);
        if (marker != null) {
            map.getController().animateTo(marker.getPosition());
            marker.showInfoWindow();
        } else {
            Toast.makeText(this, "Location not found (Possibly not located in your working area)", Toast.LENGTH_SHORT).show();
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
                        // Start MainActivity
                        Intent intent = new Intent(MapActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        // Finish the current activity
                        MapActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


}