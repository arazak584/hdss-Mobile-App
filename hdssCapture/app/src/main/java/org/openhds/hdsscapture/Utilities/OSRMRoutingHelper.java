package org.openhds.hdsscapture.Utilities;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OSRMRoutingHelper {

    private static final String OSRM_BASE_URL = "https://router.project-osrm.org/route/v1/driving/";
    private final OkHttpClient client;
    private final Handler mainHandler;

    public interface RoutingCallback {
        void onRoutingSuccess(List<GeoPoint> routePoints, double distanceKm, double durationMinutes);
        void onRoutingFailure(String error);
    }

    public OSRMRoutingHelper() {
        this.client = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Get route between two points
     * @param start Starting point
     * @param end Destination point
     * @param callback Callback with route information
     */
    public void getRoute(GeoPoint start, GeoPoint end, RoutingCallback callback) {
        // Build OSRM URL: longitude,latitude;longitude,latitude
        String url = OSRM_BASE_URL +
                start.getLongitude() + "," + start.getLatitude() + ";" +
                end.getLongitude() + "," + end.getLatitude() +
                "?overview=full&geometries=geojson";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onRoutingFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onRoutingFailure("Server error: " + response.code()));
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                    if (!json.has("routes") || json.getAsJsonArray("routes").size() == 0) {
                        mainHandler.post(() -> callback.onRoutingFailure("No route found"));
                        return;
                    }

                    JsonObject route = json.getAsJsonArray("routes").get(0).getAsJsonObject();
                    JsonObject geometry = route.getAsJsonObject("geometry");
                    JsonArray coordinates = geometry.getAsJsonArray("coordinates");

                    List<GeoPoint> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.size(); i++) {
                        JsonArray coord = coordinates.get(i).getAsJsonArray();
                        double lon = coord.get(0).getAsDouble();
                        double lat = coord.get(1).getAsDouble();
                        routePoints.add(new GeoPoint(lat, lon));
                    }

                    double distance = route.get("distance").getAsDouble() / 1000.0; // meters to km
                    double duration = route.get("duration").getAsDouble() / 60.0; // seconds to minutes

                    mainHandler.post(() -> callback.onRoutingSuccess(routePoints, distance, duration));

                } catch (Exception e) {
                    mainHandler.post(() -> callback.onRoutingFailure("Parsing error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * Draw route on map
     * @param map MapView to draw on
     * @param routePoints List of points forming the route
     * @return Polyline overlay that was added to the map
     */
    public static Polyline drawRouteOnMap(MapView map, List<GeoPoint> routePoints) {
        Polyline line = new Polyline();
        line.setPoints(routePoints);
        line.setColor(Color.BLUE);
        line.setWidth(8f);
        line.getOutlinePaint().setColor(Color.WHITE);
        line.getOutlinePaint().setStrokeWidth(12f);
        map.getOverlayManager().add(line);
        map.invalidate();
        return line;
    }

    /**
     * Remove route from map
     * @param map MapView to remove from
     * @param polyline Polyline to remove
     */
    public static void removeRouteFromMap(MapView map, Polyline polyline) {
        if (polyline != null) {
            map.getOverlayManager().remove(polyline);
            map.invalidate();
        }
    }
}