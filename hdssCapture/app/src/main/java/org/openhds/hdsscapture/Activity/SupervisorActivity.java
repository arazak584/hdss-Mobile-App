package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private ProgressBar progressBar;
    private AppJson appJson;

    @Override
    protected void onResume() {
        super.onResume();

        // Update the AppJson instance and API DAO in onResume
        appJson = AppJson.getInstance(this);
        dao = appJson.getJsonApi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        appJson = AppJson.getInstance(this);
        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

        final EditText username = findViewById(R.id.text_username);
        final EditText password = findViewById(R.id.text_password);

        progress = new ProgressDialog(SupervisorActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressBar = findViewById(R.id.login_progress);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();


        final Button button_SyncFw = findViewById(R.id.button_SyncFieldworkerData);
        button_SyncFw.setOnClickListener(v -> {
            final TextView textView_SyncFw = findViewById(R.id.textView_SyncFieldworkerData);
            textView_SyncFw.setText("");

            // Get the username and password from the EditText fields
            String enteredUsername = username.getText().toString().trim();
            String enteredPassword = password.getText().toString().trim();
            if (TextUtils.isEmpty(enteredUsername) || TextUtils.isEmpty(enteredPassword)) {
                Toast.makeText(SupervisorActivity.this, "Username or Password Empty", Toast.LENGTH_LONG).show();
                return;
            }

            // Construct Basic Authentication header
            String credentials = enteredUsername + ":" + enteredPassword;
            String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            String authorizationHeader = "Basic " + base64Credentials;

            // Save authorizationHeader in SharedPreferences
            SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("authorizationHeader", authorizationHeader);
            editor.apply();

            // Show the ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            final FieldworkerViewModel viewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

            // Modify the service method to accept the credentials in the header
            Call<DataWrapper<Fieldworker>> c_callable = dao.getFw(authorizationHeader);
            c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                @Override
                public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                    if (response != null && response.isSuccessful()) {
                        DataWrapper<Fieldworker> dataWrapper = response.body();
                        if (dataWrapper != null) {
                            List<Fieldworker> fieldworkers = dataWrapper.getData();
                            if (fieldworkers != null && !fieldworkers.isEmpty()) {
                                // Process the data
                                Fieldworker[] fieldworkerArray = fieldworkers.toArray(new Fieldworker[0]);
                                viewModel.add(fieldworkerArray);
                                progressBar.setVisibility(View.GONE);
                                textView_SyncFw.setText("Successful Download of " + fieldworkers.size() + " Data Collectors");
                                textView_SyncFw.setTextColor(ContextCompat.getColor(textView_SyncFw.getContext(), R.color.LimeGreen));
                                //textView_SyncFw.setTextColor(Color.GREEN);
                            } else {
                                // Handle empty or null fieldworkers list
                                handleEmptyFieldworkers();
                            }
                        } else {
                            // Handle null dataWrapper
                            handleNullDataWrapper();
                        }
                    } else {
                        // Handle unsuccessful response
                        progressBar.setVisibility(View.GONE);
                        textView_SyncFw.setText("Unsuccessful response: " + response.code());
                        textView_SyncFw.setTextColor(Color.RED);
                    }
                }

                @Override
                public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {
                    // Handle failure
                    progressBar.setVisibility(View.GONE);
                    textView_SyncFw.setText("Fieldworker Sync Error!");
                    textView_SyncFw.setTextColor(Color.RED);
                }

                private void handleEmptyFieldworkers() {
                    progressBar.setVisibility(View.GONE);
                    textView_SyncFw.setText("Fieldworkers data is empty or null");
                    textView_SyncFw.setTextColor(Color.RED);
                }

                private void handleNullDataWrapper() {
                    progressBar.setVisibility(View.GONE);
                    textView_SyncFw.setText("DataWrapper is null");
                    textView_SyncFw.setTextColor(Color.RED);
                }
            });

        });
    }


}