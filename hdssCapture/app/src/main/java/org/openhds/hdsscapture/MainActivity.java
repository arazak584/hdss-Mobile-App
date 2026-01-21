package org.openhds.hdsscapture;

import static org.openhds.hdsscapture.AppConstants.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;

import org.openhds.hdsscapture.Activity.*;
import org.openhds.hdsscapture.Views.ViewActivity;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.*;
import org.openhds.hdsscapture.entity.*;
import org.openhds.hdsscapture.fragment.InfoFragment;
import org.openhds.hdsscapture.odk.OdkForm;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREFS_LAST_SYNC = "lastSyncDatetime";

    // UI Components
    private TextView reject;
    private TextView wks;
    private ProgressDialog progress;

    // Data
    private int status;
    private String fw, fws, authorizationHeader;
    private ApiDao dao;

    // ViewModels
    private DeathViewModel deathViewModel;
    private InmigrationViewModel inmigrationViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private DemographicViewModel demographicViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
    private PregnancyViewModel pregnancyViewModel;
    private RelationshipViewModel relationshipViewModel;
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private VaccinationViewModel vaccinationViewModel;
    private MorbidityViewModel morbidityViewModel;
    private DuplicateViewModel duplicateViewModel;

    // Single thread executor for background tasks
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize executor service
        executorService = Executors.newSingleThreadExecutor();

        // Request storage permissions for Android R+
        requestStoragePermissions();

        // Load preferences once
        loadPreferences();

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize ViewModels lazily
        initializeViewModels();

        // Initialize last sync datetime
        initializeLastSyncDatetime();

        // Initialize API
        dao = AppJson.getInstance(this).getJsonApi();

        // Initialize progress dialog
        initializeProgressDialog();

        // Setup UI components
        setupUIComponents();

        // Check location status
        checkLocationStatusAndShowPrompt();

        // Load initial data
        countRejected();
    }

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
    }

    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);
        fws = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);
        status = sharedPreferences.getInt(LoginActivity.FW_STATUS, 0);
    }

    private void initializeViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        inmigrationViewModel = provider.get(InmigrationViewModel.class);
        outmigrationViewModel = provider.get(OutmigrationViewModel.class);
        deathViewModel = provider.get(DeathViewModel.class);
        pregnancyViewModel = provider.get(PregnancyViewModel.class);
        pregnancyoutcomeViewModel = provider.get(PregnancyoutcomeViewModel.class);
        demographicViewModel = provider.get(DemographicViewModel.class);
        relationshipViewModel = provider.get(RelationshipViewModel.class);
        vaccinationViewModel = provider.get(VaccinationViewModel.class);
        hdssSociodemoViewModel = provider.get(HdssSociodemoViewModel.class);
        morbidityViewModel = provider.get(MorbidityViewModel.class);
        duplicateViewModel = provider.get(DuplicateViewModel.class);
    }

    private void initializeLastSyncDatetime() {
        String lastSyncDatetime = getLastSyncDatetime();
        if (lastSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            setLastSyncDatetime(sdf.format(new Date()));
        }
    }

    private void initializeProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
    }

    private void setupUIComponents() {
        wks = findViewById(R.id.textInMiddle);
        //send = findViewById(R.id.btnpush);
        reject = findViewById(R.id.btnReject);

        Fieldworker fieldworkerDatas = getIntent().getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        setupButton(R.id.button_settings, v -> syncAllSettings());
        setupButtons(R.id.btnDataCapture, v -> startActivityWithData(HierarchyActivity.class, fieldworkerDatas));
        setupButtons(R.id.btnSiteMap, v -> startActivityWithData(MapActivity.class, fieldworkerDatas));
        setupButtons(R.id.btnSyncData, v -> handlePushAccess());
        setupButtons(R.id.btnDownload, v -> handlePullAccess());
        setupButtons(R.id.btnReports, v -> startActivityWithData(ReportActivity.class, fieldworkerDatas));
        setupButtons(R.id.btnQueries, v -> startActivityWithData(QueryActivity.class, fieldworkerDatas));
        setupButtons(R.id.btnViews, v -> startActivityWithData(ViewActivity.class, fieldworkerDatas));
        setupButtons(R.id.btnWorkSchedule, v -> startActivity(new Intent(this, RemainderActivity.class)));
        setupButton(R.id.btninfo, v -> handleInfoAccess());
        setupButton(R.id.button_url, v -> openUrl("https://github.com/arn-techsystem/openAB/releases/download/v2023.0.1/app-debug.apk"));
        setupButton(R.id.button_resetDatabase, v -> handleDatabaseReset());
        setupButtons(R.id.btnLastCompounds, v -> lastCompoundAccess());

        setupButtons(R.id.btnRejections,v -> startActivityWithData(RejectionsActivity.class, fieldworkerDatas));
    }

    private void setupButton(int buttonId, View.OnClickListener listener) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    private void setupButtons(int buttonId, View.OnClickListener listener) {
        MaterialCardView button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    private void startActivityWithData(Class<?> activityClass, Fieldworker fieldworkerDatas) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
        startActivity(intent);
    }

    private void handlePushAccess() {
        if (status == 1 || status == 2) {
            startActivity(new Intent(this, PushActivity.class));
        } else {
            showAccessDenied();
        }
    }

    private void handlePullAccess() {
        if (status == 1 || status == 2) {
            startActivity(new Intent(this, PullActivity.class));
        } else {
            showAccessDenied();
        }
    }

    private void lastCompoundAccess() {
        if (status == 2) {
            startActivity(new Intent(this, CompoundActivity.class));
        } else {
            showAccessDenied();
        }
    }

    private void handleInfoAccess() {
        if (status == 1) {
            new InfoFragment().show(getSupportFragmentManager(), "InfoFragment");
        } else {
            showAccessDenied();
        }
    }

    private void handleDatabaseReset() {
        if (status == 2) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to reset the database?")
                    .setPositiveButton("Yes", (dialog, which) -> resetDatabase())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            showAccessDenied();
        }
    }

    private void showAccessDenied() {
        Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Sync all settings with optimized nested callbacks
    private void syncAllSettings() {
        if (!isInternetAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();
        progress.setMessage("Updating Hierarchy...");

        ViewModelProvider provider = new ViewModelProvider(this);

        syncHierarchy(provider, () ->
                syncRound(provider, () ->
                        syncCodeBook(provider, () ->
                                syncFieldworker(provider, () ->
                                        syncCommunity(provider, () ->
                                                syncHierarchyLevel(provider, () ->
                                                        syncOdk(provider, () ->
                                                                syncConfig(provider)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private void syncHierarchy(ViewModelProvider provider, Runnable onSuccess) {
        HierarchyViewModel viewModel = provider.get(HierarchyViewModel.class);
        dao.getAllHierarchy(authorizationHeader).enqueue(new SimpleCallback<Hierarchy>("Hierarchy") {
            @Override
            public void onSuccess(DataWrapper<Hierarchy> data) {
                viewModel.add(data.getData().toArray(new Hierarchy[0]));
                onSuccess.run();
            }
        });
    }

    private void syncRound(ViewModelProvider provider, Runnable onSuccess) {
        progress.setMessage("Updating Round...");
        RoundViewModel viewModel = provider.get(RoundViewModel.class);
        dao.getRound(authorizationHeader).enqueue(new SimpleCallback<Round>("Round") {
            @Override
            public void onSuccess(DataWrapper<Round> data) {
                Round[] rounds = data.getData().toArray(new Round[0]);
                Date now = new Date();
                for (Round round : rounds) {
                    round.insertDate = now;
                }
                viewModel.add(rounds);
                onSuccess.run();
            }
        });
    }

    private void syncCodeBook(ViewModelProvider provider, Runnable onSuccess) {
        progress.setMessage("Updating Codebook...");
        CodeBookViewModel viewModel = provider.get(CodeBookViewModel.class);
        dao.getCodeBook(authorizationHeader).enqueue(new SimpleCallback<CodeBook>("Codebook") {
            @Override
            public void onSuccess(DataWrapper<CodeBook> data) {
                viewModel.add(data.getData().toArray(new CodeBook[0]));
                onSuccess.run();
            }
        });
    }

    private void syncFieldworker(ViewModelProvider provider, Runnable onSuccess) {
        progress.setMessage("Updating Fieldworker...");
        FieldworkerViewModel viewModel = provider.get(FieldworkerViewModel.class);
        dao.getFw(authorizationHeader).enqueue(new SimpleCallback<Fieldworker>("Fieldworker") {
            @Override
            public void onSuccess(DataWrapper<Fieldworker> data) {
                viewModel.add(data.getData().toArray(new Fieldworker[0]));
                onSuccess.run();
            }
        });
    }

    private void syncCommunity(ViewModelProvider provider, Runnable onSuccess) {
        CommunityViewModel viewModel = provider.get(CommunityViewModel.class);
        dao.getCommunity(authorizationHeader).enqueue(new SimpleCallback<CommunityReport>("Community Report") {
            @Override
            public void onSuccess(DataWrapper<CommunityReport> data) {
                viewModel.add(data.getData().toArray(new CommunityReport[0]));
                onSuccess.run();
            }
        });
    }

    private void syncHierarchyLevel(ViewModelProvider provider, Runnable onSuccess) {
        HierarchyLevelViewModel viewModel = provider.get(HierarchyLevelViewModel.class);
        dao.getHierarchyLevel(authorizationHeader).enqueue(new SimpleCallback<HierarchyLevel>("Hierarchy Level") {
            @Override
            public void onSuccess(DataWrapper<HierarchyLevel> data) {
                viewModel.add(data.getData().toArray(new HierarchyLevel[0]));
                onSuccess.run();
            }
        });
    }

    private void syncOdk(ViewModelProvider provider, Runnable onSuccess) {
        OdkFormViewModel viewModel = provider.get(OdkFormViewModel.class);
        dao.getOdk(authorizationHeader).enqueue(new SimpleCallback<OdkForm>("ODK") {
            @Override
            public void onSuccess(DataWrapper<OdkForm> data) {
                viewModel.add(data.getData().toArray(new OdkForm[0]));
                onSuccess.run();
            }
        });
    }

    private void syncConfig(ViewModelProvider provider) {
        progress.setMessage("Updating Settings...");
        ConfigViewModel viewModel = provider.get(ConfigViewModel.class);
        dao.getConfig(authorizationHeader).enqueue(new SimpleCallback<Configsettings>("Settings") {
            @Override
            public void onSuccess(DataWrapper<Configsettings> data) {
                viewModel.add(data.getData().toArray(new Configsettings[0]));
                progress.dismiss();
                updateSyncButton(true);
            }
        });
    }

    private void updateSyncButton(boolean success) {
        Button syncButton = findViewById(R.id.button_settings);
        if (success) {
            syncButton.setText("Sync Successful");
            syncButton.setTextColor(Color.parseColor("#32CD32"));
        } else {
            syncButton.setText("Sync Error!");
            syncButton.setTextColor(Color.RED);
        }
    }

    // Abstract callback class to reduce code duplication
    private abstract class SimpleCallback<T> implements Callback<DataWrapper<T>> {
        private final String entityName;

        SimpleCallback(String entityName) {
            this.entityName = entityName;
        }

        abstract void onSuccess(DataWrapper<T> data);

        @Override
        public void onResponse(Call<DataWrapper<T>> call, Response<DataWrapper<T>> response) {
            if (response.isSuccessful() && response.body() != null) {
                onSuccess(response.body());
            } else {
                onFailure(call, new Exception("Response not successful"));
            }
        }

        @Override
        public void onFailure(Call<DataWrapper<T>> call, Throwable t) {
            progress.dismiss();
            updateSyncButton(false);
            Log.e(TAG, entityName + " Sync Error: " + t.getMessage());
        }
    }

    // Info button handlers
    public void startAppInfo(View view) { showDialogInfo(null, DATA_CAPTURE); }
    public void startSyncInfo(View view) { showDialogInfo(null, DATA_SYNC); }
    public void startReportInfo(View view) { showDialogInfo(null, DATA_REPORT); }
    public void startQueryInfo(View view) { showDialogInfo(null, DATA_QUERY); }
    public void startViewInfo(View view) { showDialogInfo(null, DATA_VIEWS); }
    public void startDownloadInfo(View view) { showDialogInfo(null, DATA_DOWNLOAD); }
    public void startRejectInfo(View view) { showDialogInfo(null, DATA_REJECT); }
    public void startScheduleInfo(View view) { showDialogInfo(null, DATA_SCHEDULE); }
    public void startMapInfo(View view) { showDialogInfo(null, DATA_MAP); }

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog.newInstance(message, codeFragment)
                .show(getSupportFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void resetDatabase() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting database...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AppDatabase.getDatabase(this).resetDatabase(this, () -> {
            progressDialog.dismiss();
            runOnUiThread(() -> {
                Toast.makeText(this, "Database reset successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        });
    }

    private void calculatePercentage() {
        executorService.execute(() -> {
            try {
                LocationViewModel viewModel = new ViewModelProvider(this).get(LocationViewModel.class);
                long totalLocations = viewModel.work(fw);
                long editedLocations = viewModel.works(fw);

                if (totalLocations > 0) {
                    double percentage = ((double) editedLocations / totalLocations) * 100;
                    String formattedPercentage = String.format(Locale.getDefault(), "%.2f", percentage);

                    runOnUiThread(() -> {
                        ProgressBar progressBar = findViewById(R.id.circularProgressBar);
                        progressBar.setMax((int) totalLocations);
                        progressBar.setProgress((int) Math.min(editedLocations, totalLocations));
                        wks.setText(formattedPercentage + "% ");
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error calculating percentage", e);
            }
        });
    }

    private void countRejected() {
        executorService.execute(() -> {
            try {
                long totalRejected =
                        inmigrationViewModel.rej(fws) +
                        outmigrationViewModel.rej(fws) +
                        pregnancyViewModel.rej(fws) +
                        pregnancyoutcomeViewModel.rej(fws) +
                        demographicViewModel.rej(fws) +
                        deathViewModel.rej(fws) +
                        relationshipViewModel.rej(fws) +
                        hdssSociodemoViewModel.rej(fws) +
                        vaccinationViewModel.rej(fws) +
                        morbidityViewModel.rej(fws) +
                        duplicateViewModel.rej(fws);


                runOnUiThread(() -> {
                    reject.setText("REJECTIONS (" + totalRejected + ")");
                });
            } catch (Exception e) {
                Log.e(TAG, "Error counting rejections", e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculatePercentage();
        countRejected();

        if (!isLocationEnabled(this)) {
            showLocationSettingsPrompt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    // Utility methods
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String getLastSyncDatetime() {
        return getPreferences(Context.MODE_PRIVATE).getString(PREFS_LAST_SYNC, "");
    }

    private void setLastSyncDatetime(String datetime) {
        getPreferences(Context.MODE_PRIVATE).edit()
                .putString(PREFS_LAST_SYNC, datetime)
                .apply();
    }

    private void checkLocationStatusAndShowPrompt() {
        if (!isLocationEnabled(this)) {
            showLocationSettingsPrompt();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showLocationSettingsPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Location Disabled")
                .setMessage("Please enable location for this app in your device settings to pick GPS")
                .setPositiveButton("Go to Settings", (dialog, which) ->
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }
}