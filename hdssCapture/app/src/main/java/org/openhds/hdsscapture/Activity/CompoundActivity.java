package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.openhds.hdsscapture.Adapter.CompoundAdapter;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subentity.AvailableCompnoDTO;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompoundActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private final String TAG = "CompoundActivity";

    private EditText prefixInput;
    private Button retrieveButton;
    private RecyclerView recyclerView;
    private CompoundAdapter adapter;

    private String authorizationHeader;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compound);

        // Initialize views
        prefixInput = findViewById(R.id.prefix_input);
        retrieveButton = findViewById(R.id.retrieve_button);
        recyclerView = findViewById(R.id.compounds_recycler);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CompoundAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Load preferences
        loadPreferences();

        // Initialize API DAO
        dao = AppJson.getInstance(this).getJsonApi();

        // Setup button click listener
        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prefix = prefixInput.getText().toString().trim();

                if (prefix.isEmpty()) {
                    Toast.makeText(CompoundActivity.this,
                            "Please enter a prefix", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isInternetAvailable()) {
                    Toast.makeText(CompoundActivity.this,
                            "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                fetchAvailableCompounds(prefix);
            }
        });
    }

    private void loadPreferences() {
        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);
    }

    private void fetchAvailableCompounds(String prefix) {
        showProgressDialog("Fetching available compounds...");

        Call<List<AvailableCompnoDTO>> call = dao.getAvailableCompounds(authorizationHeader, prefix);

        call.enqueue(new Callback<List<AvailableCompnoDTO>>() {
            @Override
            public void onResponse(Call<List<AvailableCompnoDTO>> call,
                                   Response<List<AvailableCompnoDTO>> response) {
                dismissProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    List<AvailableCompnoDTO> compoundList = response.body();

                    if (!compoundList.isEmpty()) {
                        // Convert DTO list to display strings
                        List<String> compoundStrings = new ArrayList<>();
                        for (AvailableCompnoDTO dto : compoundList) {
                            compoundStrings.add(dto.getAvailableCompno() + " (" + dto.getSource() + ")");
                        }

                        adapter.updateData(compoundStrings);
                        Toast.makeText(CompoundActivity.this,
                                "Found " + compoundStrings.size() + " compounds",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompoundActivity.this,
                                "No compounds found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    Toast.makeText(CompoundActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AvailableCompnoDTO>> call, Throwable t) {
                dismissProgressDialog();
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(CompoundActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgressDialog(String message) {
        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setCancelable(false);
        }
        progress.setMessage(message);
        progress.show();
    }

    private void dismissProgressDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}