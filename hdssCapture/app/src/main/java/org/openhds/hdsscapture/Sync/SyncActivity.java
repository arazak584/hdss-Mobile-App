package org.openhds.hdsscapture.Sync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncActivity extends AppCompatActivity {

    private SendLocationAdapter sendLocationAdapter;
    private SendHouseholdAdapter sendHouseholdAdapter;
    private LocationViewModel locationViewModel;
    private SocialgroupViewModel socialgroupViewModel;
    private ApiDao dao;
    private String authorizationHeader;
    private ProgressDialog progress;
    private Button buttonSendLocations;
    private Button buttonSendSocialgroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        // Initialize ViewModel
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);

        progress = new ProgressDialog(SyncActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        // Retrieve authorizationHeader from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        // Find RecyclerView and Button
        RecyclerView recyclerViewLocations = findViewById(R.id.recyclerViewLocations);
        buttonSendLocations = findViewById(R.id.buttonSendSelectedLocations);
        RecyclerView recyclerViewSocialgroup = findViewById(R.id.recyclerViewSocialgroup);
        buttonSendSocialgroup = findViewById(R.id.buttonSendSelectedSocialgroup);

        // Set up RecyclerView with LayoutManager
        recyclerViewLocations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialgroup.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve and set up adapters
        try {
            List<Locations> locationsList = locationViewModel.findToSync();
            sendLocationAdapter = new SendLocationAdapter(locationsList);
            recyclerViewLocations.setAdapter(sendLocationAdapter);
            updateButtonVisibility(locationsList, buttonSendLocations);

            List<Socialgroup> socialgroupList = socialgroupViewModel.findToSync();
            sendHouseholdAdapter = new SendHouseholdAdapter(socialgroupList);
            recyclerViewSocialgroup.setAdapter(sendHouseholdAdapter);
            updateButtonVisibility(socialgroupList, buttonSendSocialgroup);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
        }

        // Handle button clicks
        buttonSendLocations.setOnClickListener(v -> sendSelectedLocationsToApi(sendLocationAdapter.getSelectedLocations()));
        buttonSendSocialgroup.setOnClickListener(v -> sendSelectedSocialgroupToApi(sendHouseholdAdapter.getselectedSocialgroup()));
    }

    private void updateButtonVisibility(List<?> dataList, Button button) {
        if (dataList != null && !dataList.isEmpty()) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void sendSelectedLocationsToApi(List<Locations> selectedLocations) {
        if (selectedLocations.isEmpty()) return;

        progress.setMessage("Sending Locations...");
        progress.show();

        DataWrapper<Locations> data = new DataWrapper<>(selectedLocations);
        Call<DataWrapper<Locations>> call = dao.sendLocationdata(authorizationHeader, data);
        call.enqueue(new Callback<DataWrapper<Locations>>() {
            @Override
            public void onResponse(Call<DataWrapper<Locations>> call, Response<DataWrapper<Locations>> response) {
                progress.dismiss();
                if (response.isSuccessful()) {
                    // Use the original selectedLocations to reset complete and update locally
                    handleSuccess(selectedLocations, locationViewModel);
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<DataWrapper<Locations>> call, Throwable t) {
                progress.dismiss();
                handleFailure();
            }
        });
    }


    private void sendSelectedSocialgroupToApi(List<Socialgroup> selectedSocialgroup) {
        if (selectedSocialgroup.isEmpty()) return;

        progress.setMessage("Sending Socialgroups...");
        progress.show();

        DataWrapper<Socialgroup> data = new DataWrapper<>(selectedSocialgroup);
        Call<DataWrapper<Socialgroup>> call = dao.sendSocialgroupdata(authorizationHeader, data);
        call.enqueue(new Callback<DataWrapper<Socialgroup>>() {
            @Override
            public void onResponse(Call<DataWrapper<Socialgroup>> call, Response<DataWrapper<Socialgroup>> response) {
                progress.dismiss();
                if (response.isSuccessful()) {
                    // Use the original selectedSocialgroup to reset complete and update locally
                    handleSuccess(selectedSocialgroup, socialgroupViewModel);
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<DataWrapper<Socialgroup>> call, Throwable t) {
                progress.dismiss();
                handleFailure();
            }
        });
    }


    private <T> void handleSuccess(List<T> data, ViewModel viewModel) {
        if (viewModel instanceof LocationViewModel) {
            // Cast the data to List<Locations> and update
            List<Locations> locations = (List<Locations>) data;
            for (Locations location : locations) {
                location.setComplete(0);
                Log.e("PUSH.tag", "Has value " + location.getCompno());
            }

            // Call the add method in ViewModel to insert or update records
            ((LocationViewModel) viewModel).add(locations.toArray(new Locations[0]));
        } else if (viewModel instanceof SocialgroupViewModel) {
            // Add similar handling for Socialgroup if needed
            List<Socialgroup> socialgroups = (List<Socialgroup>) data;
            for (Socialgroup socialgroup : socialgroups) {
                socialgroup.setComplete(0);
                Log.e("PUSH.tag", "Has value " + socialgroup.getExtId());
            }
            ((SocialgroupViewModel) viewModel).add(socialgroups.toArray(new Socialgroup[0]));
        }

        // Refresh data and update UI immediately
        refreshData();
        Toast.makeText(this, "Sent " + data.size() + " records", Toast.LENGTH_SHORT).show();
    }

    private void refreshData() {
        try {
            // Re-fetch the data from ViewModels
            List<Locations> locationsList = locationViewModel.findToSync();
            List<Socialgroup> socialgroupList = socialgroupViewModel.findToSync();

            // Update the adapters with the new data
            sendLocationAdapter.updateData(locationsList);
            sendHouseholdAdapter.updateData(socialgroupList);

            // Update button visibility based on new data
            updateButtonVisibility(locationsList, buttonSendLocations);
            updateButtonVisibility(socialgroupList, buttonSendSocialgroup);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to refresh data", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleError(int errorCode) {
        Toast.makeText(this, "Failed to send data: Error " + errorCode, Toast.LENGTH_LONG).show();
    }

    private void handleFailure() {
        Toast.makeText(this, "Failed to send data", Toast.LENGTH_LONG).show();
    }


}