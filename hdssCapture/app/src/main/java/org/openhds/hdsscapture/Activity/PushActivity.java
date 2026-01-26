package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.QueriesViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.ServerQueries;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private final String TAG = "Syncing Data Errors: ";

    private Button buttonSyncAll;
    private String authorizationHeader;
    private SharedPreferences preferences;
    private Handler handler;

    // Batch configuration
    private static final int BATCH_SIZE = 50;
    private static final int BATCH_DELAY_MS = 500;

    // Sync tracking
    private int syncOperationsCompleted = 0;
    private int totalSyncOperations = 0;
    private final AtomicBoolean isSyncing = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    // ViewModels
    private ListingViewModel listingViewModel;
    private VisitViewModel visitViewModel;
    private LocationViewModel locationViewModel;
    private IndividualViewModel individualViewModel;
    private SocialgroupViewModel socialgroupViewModel;
    private RelationshipViewModel relationshipViewModel;
    private PregnancyViewModel pregnancyViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
    private OutcomeViewModel outcomeViewModel;
    private DemographicViewModel demographicViewModel;
    private DeathViewModel deathViewModel;
    private VpmViewModel vpmViewModel;
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private ResidencyViewModel residencyViewModel;
    private InmigrationViewModel inmigrationViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private AmendmentViewModel amendmentViewModel;
    private VaccinationViewModel vaccinationViewModel;
    private DuplicateViewModel duplicateViewModel;
    private CommunityViewModel communityViewModel;
    private RegistryViewModel registryViewModel;
    private MorbidityViewModel morbidityViewModel;
    private QueriesViewModel queriesViewModel;
    private TextView tvSyncStatus, tvLastSyncTime, totalRecords;
    private LinearProgressIndicator syncProgressBar;
    private ExecutorService syncExecutor;
    private String username;

    // Count Completed - Flag to track if observers are set up
    private boolean observersInitialized = false;
    private LiveData<Long> locLiveData;
    private LiveData<Long> visitLiveData;
    private LiveData<Long> listingLiveData;
    private LiveData<Long> indLiveData;
    private LiveData<Long> hohLiveData;
    private LiveData<Long> relLiveData;
    private LiveData<Long> pregLiveData;
    private LiveData<Long> pregoutLiveData;
    private LiveData<Long> outcomeLiveData;
    private LiveData<Long> demLiveData;
    private LiveData<Long> dthLiveData;
    private LiveData<Long> vpmLiveData;
    private LiveData<Long> sesLiveData;
    private LiveData<Long> resLiveData;
    private LiveData<Long> imgLiveData;
    private LiveData<Long> omgLiveData;
    private LiveData<Long> amendLiveData;
    private LiveData<Long> vacLiveData;
    private LiveData<Long> dupLiveData;
    private LiveData<Long> comLiveData;
    private LiveData<Long> regLiveData;
    private LiveData<Long> morLiveData;

    private Long loc = 0L, visit = 0L, list = 0L, ind = 0L, hoh = 0L, rel = 0L, preg = 0L, pregout = 0L,
            out = 0L, dem = 0L, dth = 0L, vpm = 0L, ses = 0L, res = 0L, img = 0L, omg = 0L, amend = 0L, vac = 0L,
            dup = 0L, com = 0L, reg = 0L, mor = 0L;

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();
        updateLastSyncTime();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handler = new Handler(Looper.getMainLooper());
        syncExecutor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);

        // Initialize single sync button
        buttonSyncAll = findViewById(R.id.btnSync);
        syncProgressBar = findViewById(R.id.syncProgressBar);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvLastSyncTime = findViewById(R.id.tvLastSyncTime);
        totalRecords = findViewById(R.id.viewRecords);

        // Initialize ViewModels
        initializeViewModels();

        progress = new ProgressDialog(PushActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        // Retrieve authorizationHeader from SharedPreferences
        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        updateLastSyncTime();

        // Initialize observers only once
        countStats();

        // Set up sync button
        buttonSyncAll.setOnClickListener(v -> {
            if (isSyncing.get()) {
                Toast.makeText(this, "Sync already in progress", Toast.LENGTH_LONG).show();
                return;
            }

            if (isInternetAvailable()) {
                startBatchSync();
            } else {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed.set(true);
        isSyncing.set(false);

        // Clean up handlers and remove all callbacks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        // Shut down executor service
        if (syncExecutor != null && !syncExecutor.isShutdown()) {
            syncExecutor.shutdownNow();
        }

        // Dismiss progress dialog to prevent window leak
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

        // Remove all LiveData observers to prevent memory leaks
        removeAllObservers();

        // Clear screen on flag
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Remove all LiveData observers to prevent memory leaks
     */
    private void removeAllObservers() {
        if (locLiveData != null) locLiveData.removeObservers(this);
        if (visitLiveData != null) visitLiveData.removeObservers(this);
        if (listingLiveData != null) listingLiveData.removeObservers(this);
        if (indLiveData != null) indLiveData.removeObservers(this);
        if (hohLiveData != null) hohLiveData.removeObservers(this);
        if (relLiveData != null) relLiveData.removeObservers(this);
        if (pregLiveData != null) pregLiveData.removeObservers(this);
        if (pregoutLiveData != null) pregoutLiveData.removeObservers(this);
        if (outcomeLiveData != null) outcomeLiveData.removeObservers(this);
        if (demLiveData != null) demLiveData.removeObservers(this);
        if (dthLiveData != null) dthLiveData.removeObservers(this);
        if (vpmLiveData != null) vpmLiveData.removeObservers(this);
        if (sesLiveData != null) sesLiveData.removeObservers(this);
        if (resLiveData != null) resLiveData.removeObservers(this);
        if (imgLiveData != null) imgLiveData.removeObservers(this);
        if (omgLiveData != null) omgLiveData.removeObservers(this);
        if (amendLiveData != null) amendLiveData.removeObservers(this);
        if (vacLiveData != null) vacLiveData.removeObservers(this);
        if (dupLiveData != null) dupLiveData.removeObservers(this);
        if (comLiveData != null) comLiveData.removeObservers(this);
        if (regLiveData != null) regLiveData.removeObservers(this);
        if (morLiveData != null) morLiveData.removeObservers(this);
    }

    private void initializeViewModels() {
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
        hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        amendmentViewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
        vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        duplicateViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
        communityViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
        morbidityViewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);
        queriesViewModel = new ViewModelProvider(this).get(QueriesViewModel.class);
    }

    private void startBatchSync() {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            Toast.makeText(this, "Invalid credentials. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        if (isSyncing.get()) {
            Toast.makeText(this, "Sync already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        isSyncing.set(true);

        // Show progress UI properly
        buttonSyncAll.setEnabled(false);
        syncProgressBar.setVisibility(View.VISIBLE);
        syncProgressBar.setIndeterminate(true);  // Use indeterminate progress
        tvSyncStatus.setVisibility(View.VISIBLE);
        tvSyncStatus.setText("Preparing to sync...");

        // Show the ProgressDialog
        if (progress != null && !progress.isShowing()) {
            progress.setMessage("Preparing to sync...");
            progress.setCancelable(false);
            progress.show();
        }

        syncOperationsCompleted = 0;
        totalSyncOperations = 23; // Total number of data types to sync

        // Start the sync chain
        sendLocationData();
    }

    private void sendLocationData() {
        if (isDestroyed.get()) {
            onSyncError("Activity destroyed");
            return;
        }

        new Thread(() -> {
            try {
                List<Locations> itemsToSync = locationViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Locations",
                        (auth, data) -> dao.sendLocationdata(auth, data),
                        sentData -> {
                            Locations[] array = sentData.toArray(new Locations[0]);
                            for (Locations elem : array) {
                                elem.setComplete(0);
                            }
                            locationViewModel.add(array);
                        },
                        this::sendListingData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Locations", e);
                onSyncError("Error retrieving Locations: " + e.getMessage());
            }
        }).start();
    }

    private void sendListingData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Listing> itemsToSync = listingViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Listings",
                        (auth, data) -> dao.sendListing(auth, data),
                        sentData -> {
                            Listing[] array = sentData.toArray(new Listing[0]);
                            for (Listing elem : array) {
                                elem.complete = 0;
                            }
                            listingViewModel.add(array);
                        },
                        this::sendIndividualData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Listings", e);
                onSyncError("Error retrieving Listings: " + e.getMessage());
            }
        }).start();
    }

    private void sendIndividualData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Individual> itemsToSync = individualViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Individuals",
                        (auth, data) -> dao.sendIndividualdata(auth, data),
                        sentData -> {
                            Individual[] array = sentData.toArray(new Individual[0]);
                            for (Individual elem : array) {
                                elem.complete = 0;
                            }
                            individualViewModel.add(array);
                        },
                        this::sendSocialgroupData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Individuals", e);
                onSyncError("Error retrieving Individuals: " + e.getMessage());
            }
        }).start();
    }

    private void sendSocialgroupData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Socialgroup> itemsToSync = socialgroupViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Socialgroups",
                        (auth, data) -> dao.sendSocialgroupdata(auth, data),
                        sentData -> {
                            Socialgroup[] array = sentData.toArray(new Socialgroup[0]);
                            for (Socialgroup elem : array) {
                                elem.complete = 0;
                            }
                            socialgroupViewModel.add(array);
                        },
                        this::sendVisitData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Socialgroups", e);
                onSyncError("Error retrieving Socialgroups: " + e.getMessage());
            }
        }).start();
    }

    private void sendVisitData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Visit> itemsToSync = visitViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Visits",
                        (auth, data) -> dao.sendVisitdata(auth, data),
                        sentData -> {
                            Visit[] array = sentData.toArray(new Visit[0]);
                            for (Visit elem : array) {
                                elem.complete = 0;
                            }
                            visitViewModel.add(array);
                        },
                        this::sendRelationshipData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Visits", e);
                onSyncError("Error retrieving Visits: " + e.getMessage());
            }
        }).start();
    }

    private void sendRelationshipData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Relationship> itemsToSync = relationshipViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Relationships",
                        (auth, data) -> dao.sendRelationshipdata(auth, data),
                        sentData -> {
                            Relationship[] array = sentData.toArray(new Relationship[0]);
                            for (Relationship elem : array) {
                                elem.complete = 0;
                            }
                            relationshipViewModel.add(array);
                        },
                        this::sendPregnancyData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Relationships", e);
                onSyncError("Error retrieving Relationships: " + e.getMessage());
            }
        }).start();
    }

    private void sendPregnancyData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Pregnancy> itemsToSync = pregnancyViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Pregnancies",
                        (auth, data) -> dao.sendPregnancydata(auth, data),
                        sentData -> {
                            Pregnancy[] array = sentData.toArray(new Pregnancy[0]);
                            for (Pregnancy elem : array) {
                                elem.complete = 0;
                            }
                            pregnancyViewModel.add(array);
                        },
                        this::sendPregnancyOutcomeData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Pregnancies", e);
                onSyncError("Error retrieving Pregnancies: " + e.getMessage());
            }
        }).start();
    }

    private void sendPregnancyOutcomeData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Pregnancyoutcome> itemsToSync = pregnancyoutcomeViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Pregnancy Outcomes",
                        (auth, data) -> dao.sendPregoutcomedata(auth, data),
                        sentData -> {
                            Pregnancyoutcome[] array = sentData.toArray(new Pregnancyoutcome[0]);
                            for (Pregnancyoutcome elem : array) {
                                elem.complete = 0;
                            }
                            pregnancyoutcomeViewModel.add(array);
                        },
                        this::sendOutcomeData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Pregnancy Outcomes", e);
                onSyncError("Error retrieving Pregnancy Outcomes: " + e.getMessage());
            }
        }).start();
    }

    private void sendOutcomeData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Outcome> itemsToSync = outcomeViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Outcomes",
                        (auth, data) -> dao.sendOutcomedata(auth, data),
                        sentData -> {
                            Outcome[] array = sentData.toArray(new Outcome[0]);
                            for (Outcome elem : array) {
                                elem.complete = 0;
                            }
                            outcomeViewModel.add(array);
                        },
                        this::sendDemographicData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Outcomes", e);
                onSyncError("Error retrieving Outcomes: " + e.getMessage());
            }
        }).start();
    }

    private void sendDemographicData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Demographic> itemsToSync = demographicViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Demographics",
                        (auth, data) -> dao.sendDemographicdata(auth, data),
                        sentData -> {
                            Demographic[] array = sentData.toArray(new Demographic[0]);
                            for (Demographic elem : array) {
                                elem.complete = 0;
                            }
                            demographicViewModel.add(array);
                        },
                        this::sendDeathData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Demographics", e);
                onSyncError("Error retrieving Demographics: " + e.getMessage());
            }
        }).start();
    }

    private void sendDeathData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Death> itemsToSync = deathViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Deaths",
                        (auth, data) -> dao.sendDeathdata(auth, data),
                        sentData -> {
                            Death[] array = sentData.toArray(new Death[0]);
                            for (Death elem : array) {
                                if (elem.complete == 2) {
                                    // Delete records with complete==2
                                    deathViewModel.delete(elem);
                                } else {
                                    // Reset complete flag to 0 for complete==1
                                    elem.complete = 0;
                                }
                            }
                            // Only update records that weren't deleted (complete==1)
                            List<Death> toUpdate = new java.util.ArrayList<>();
                            for (Death elem : array) {
                                if (elem.complete == 0) {
                                    toUpdate.add(elem);
                                }
                            }
                            if (!toUpdate.isEmpty()) {
                                deathViewModel.add(toUpdate.toArray(new Death[0]));
                            }
                        },
                        this::sendVpmData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Deaths", e);
                onSyncError("Error retrieving Deaths: " + e.getMessage());
            }
        }).start();
    }

//    private void sendDeathData() {
//        if (isDestroyed.get()) return;
//
//        new Thread(() -> {
//            try {
//                List<Death> itemsToSync = deathViewModel.findToSync();
//                sendDataInBatches(
//                        itemsToSync,
//                        "Deaths",
//                        (auth, data) -> dao.sendDeathdata(auth, data),
//                        sentData -> {
//                            Death[] array = sentData.toArray(new Death[0]);
//                            for (Death elem : array) {
//                                elem.complete = 0;
//                            }
//                            deathViewModel.add(array);
//                        },
//                        this::sendVpmData
//                );
//            } catch (ExecutionException | InterruptedException e) {
//                Log.e(TAG, "Error retrieving Deaths", e);
//                onSyncError("Error retrieving Deaths: " + e.getMessage());
//            }
//        }).start();
//    }

    private void sendVpmData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Vpm> itemsToSync = vpmViewModel.retrieveToSync();
                sendDataInBatches(
                        itemsToSync,
                        "VPM",
                        (auth, data) -> dao.sendVpmdata(auth, data),
                        sentData -> {
                            Vpm[] array = sentData.toArray(new Vpm[0]);
                            for (Vpm elem : array) {
                                elem.complete = 0;
                            }
                            vpmViewModel.add(array);
                        },
                        this::sendSociodemoData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving VPM", e);
                onSyncError("Error retrieving VPM: " + e.getMessage());
            }
        }).start();
    }

    private void sendSociodemoData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<HdssSociodemo> itemsToSync = hdssSociodemoViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Sociodemographics",
                        (auth, data) -> dao.sendSociodata(auth, data),
                        sentData -> {
                            HdssSociodemo[] array = sentData.toArray(new HdssSociodemo[0]);
                            for (HdssSociodemo elem : array) {
                                elem.complete = 0;
                            }
                            hdssSociodemoViewModel.add(array);
                        },
                        this::sendResidencyData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Sociodemographics", e);
                onSyncError("Error retrieving Sociodemographics: " + e.getMessage());
            }
        }).start();
    }

    private void sendResidencyData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Residency> itemsToSync = residencyViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Residencies",
                        (auth, data) -> dao.sendResidencydata(auth, data),
                        sentData -> {
                            Residency[] array = sentData.toArray(new Residency[0]);
                            for (Residency elem : array) {
                                elem.complete = 0;
                            }
                            residencyViewModel.add(array);
                        },
                        this::sendInmigrationData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Residencies", e);
                onSyncError("Error retrieving Residencies: " + e.getMessage());
            }
        }).start();
    }

    private void sendInmigrationData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Inmigration> itemsToSync = inmigrationViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Inmigrations",
                        (auth, data) -> dao.sendInmigrationdata(auth, data),
                        sentData -> {
                            Inmigration[] array = sentData.toArray(new Inmigration[0]);
                            for (Inmigration elem : array) {
                                elem.complete = 0;
                            }
                            inmigrationViewModel.add(array);
                        },
                        this::sendOutmigrationData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Inmigrations", e);
                onSyncError("Error retrieving Inmigrations: " + e.getMessage());
            }
        }).start();
    }

    private void sendOutmigrationData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Outmigration> itemsToSync = outmigrationViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Outmigrations",
                        (auth, data) -> dao.sendOutmigrationdata(auth, data),
                        sentData -> {
                            Outmigration[] array = sentData.toArray(new Outmigration[0]);
                            for (Outmigration elem : array) {
                                if (elem.complete == 2) {
                                    // Delete records with complete==2
                                    outmigrationViewModel.delete(elem);
                                } else {
                                    // Reset complete flag to 0 for complete==1
                                    elem.complete = 0;
                                }
                            }
                            // Only update records that weren't deleted (complete==1)
                            List<Outmigration> toUpdate = new java.util.ArrayList<>();
                            for (Outmigration elem : array) {
                                if (elem.complete == 0) {
                                    toUpdate.add(elem);
                                }
                            }
                            if (!toUpdate.isEmpty()) {
                                outmigrationViewModel.add(toUpdate.toArray(new Outmigration[0]));
                            }
                        },
                        this::sendAmendmentData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Outmigrations", e);
                onSyncError("Error retrieving Outmigrations: " + e.getMessage());
            }
        }).start();
    }

//    private void sendOutmigrationData() {
//        if (isDestroyed.get()) return;
//
//        new Thread(() -> {
//            try {
//                List<Outmigration> itemsToSync = outmigrationViewModel.findToSync();
//                sendDataInBatches(
//                        itemsToSync,
//                        "Outmigrations",
//                        (auth, data) -> dao.sendOutmigrationdata(auth, data),
//                        sentData -> {
//                            Outmigration[] array = sentData.toArray(new Outmigration[0]);
//                            for (Outmigration elem : array) {
//                                elem.complete = 0;
//                            }
//                            outmigrationViewModel.add(array);
//                        },
//                        this::sendAmendmentData
//                );
//            } catch (ExecutionException | InterruptedException e) {
//                Log.e(TAG, "Error retrieving Outmigrations", e);
//                onSyncError("Error retrieving Outmigrations: " + e.getMessage());
//            }
//        }).start();
//    }

    private void sendAmendmentData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Amendment> itemsToSync = amendmentViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Amendments",
                        (auth, data) -> dao.sendAmendment(auth, data),
                        sentData -> {
                            Amendment[] array = sentData.toArray(new Amendment[0]);
                            for (Amendment elem : array) {
                                elem.complete = 0;
                            }
                            amendmentViewModel.add(array);
                        },
                        this::sendVaccinationData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Amendments", e);
                onSyncError("Error retrieving Amendments: " + e.getMessage());
            }
        }).start();
    }

    private void sendVaccinationData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Vaccination> itemsToSync = vaccinationViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Vaccinations",
                        (auth, data) -> dao.sendVaccination(auth, data),
                        sentData -> {
                            Vaccination[] array = sentData.toArray(new Vaccination[0]);
                            for (Vaccination elem : array) {
                                elem.complete = 0;
                            }
                            vaccinationViewModel.add(array);
                        },
                        this::sendDuplicateData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Vaccinations", e);
                onSyncError("Error retrieving Vaccinations: " + e.getMessage());
            }
        }).start();
    }

    private void sendDuplicateData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Duplicate> itemsToSync = duplicateViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Duplicates",
                        (auth, data) -> dao.sendDup(auth, data),
                        sentData -> {
                            Duplicate[] array = sentData.toArray(new Duplicate[0]);
                            for (Duplicate elem : array) {
                                if (elem.complete == 2) {
                                    // Delete records with complete==2
                                    duplicateViewModel.delete(elem);
                                    Log.d("DupDelete", "Delete Successful: ");
                                } else {
                                    // Reset complete flag to 0 for complete==1
                                    elem.complete = 0;
                                }
                            }
                            // Only update records that weren't deleted (complete==1)
                            List<Duplicate> toUpdate = new java.util.ArrayList<>();
                            for (Duplicate elem : array) {
                                if (elem.complete == 0) {
                                    toUpdate.add(elem);
                                }
                            }
                            if (!toUpdate.isEmpty()) {
                                duplicateViewModel.add(toUpdate.toArray(new Duplicate[0]));
                            }
                        },
                        this::sendCommunityData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Duplicates", e);
                onSyncError("Error retrieving Duplicates: " + e.getMessage());
            }
        }).start();
    }

//    private void sendDuplicateData() {
//        if (isDestroyed.get()) return;
//
//        new Thread(() -> {
//            try {
//                List<Duplicate> itemsToSync = duplicateViewModel.findToSync();
//                sendDataInBatches(
//                        itemsToSync,
//                        "Duplicates",
//                        (auth, data) -> dao.sendDup(auth, data),
//                        sentData -> {
//                            Duplicate[] array = sentData.toArray(new Duplicate[0]);
//                            for (Duplicate elem : array) {
//                                elem.complete = 0;
//                            }
//                            duplicateViewModel.add(array);
//                        },
//                        this::sendCommunityData
//                );
//            } catch (ExecutionException | InterruptedException e) {
//                Log.e(TAG, "Error retrieving Duplicates", e);
//                onSyncError("Error retrieving Duplicates: " + e.getMessage());
//            }
//        }).start();
//    }

    private void sendCommunityData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<CommunityReport> itemsToSync = communityViewModel.retrieveToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Community Reports",
                        (auth, data) -> dao.sendCommunity(auth, data),
                        sentData -> {
                            CommunityReport[] array = sentData.toArray(new CommunityReport[0]);
                            for (CommunityReport elem : array) {
                                elem.complete = 0;
                            }
                            communityViewModel.add(array);
                        },
                        this::sendRegistryData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Community Reports", e);
                onSyncError("Error retrieving Community Reports: " + e.getMessage());
            }
        }).start();
    }

    private void sendRegistryData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Registry> itemsToSync = registryViewModel.findToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Registry",
                        (auth, data) -> dao.sendRegistry(auth, data),
                        sentData -> {
                            Registry[] array = sentData.toArray(new Registry[0]);
                            for (Registry elem : array) {
                                elem.complete = 0;
                            }
                            registryViewModel.add(array);
                        },
                        this::sendMorbidityData
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Registry", e);
                onSyncError("Error retrieving Registry: " + e.getMessage());
            }
        }).start();
    }

    private void sendMorbidityData() {
        if (isDestroyed.get()) return;

        new Thread(() -> {
            try {
                List<Morbidity> itemsToSync = morbidityViewModel.retrieveToSync();
                sendDataInBatches(
                        itemsToSync,
                        "Morbidity",
                        (auth, data) -> dao.sendMorbidity(auth, data),
                        sentData -> {
                            Morbidity[] array = sentData.toArray(new Morbidity[0]);
                            for (Morbidity elem : array) {
                                elem.complete = 0;
                            }
                            morbidityViewModel.add(array);
                        },
                        () -> downloadUserQueries(username)
                        //this::onOperationComplete
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error retrieving Morbidity", e);
                onSyncError("Error retrieving Morbidity: " + e.getMessage());
            }
        }).start();
    }

    private void downloadUserQueries(String username) {
        if (isDestroyed.get()) {
            onSyncError("Activity destroyed");
            return;
        }

        Log.d(TAG, "userName: " + username);

        // Delete all existing queries first
        AppDatabase.databaseWriteExecutor.execute(() -> {
            queriesViewModel.deleteAll();
            Log.d(TAG, "Deleted all existing queries");

            // Small delay to ensure deletion completes
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }

            // Then download new queries
            handler.post(() -> {
                if (isDestroyed.get()) return;

                Call<DataWrapper<ServerQueries>> itemCall = dao.getQueries(authorizationHeader, username);

                itemCall.enqueue(new Callback<DataWrapper<ServerQueries>>() {
                    @Override
                    public void onResponse(Call<DataWrapper<ServerQueries>> call, Response<DataWrapper<ServerQueries>> response) {
                        if (isDestroyed.get()) return;

                        Log.d(TAG, "=== Queries RESPONSE ===");
                        Log.d(TAG, "Response Code: " + response.code());
                        Log.d(TAG, "Response isSuccessful: " + response.isSuccessful());

                        if (response.isSuccessful() && response.body() != null) {
                            List<ServerQueries> itemList = response.body().getData();
                            Log.d(TAG, "Queries list size: " + (itemList != null ? itemList.size() : "null"));

                            if (itemList != null && !itemList.isEmpty()) {
                                ServerQueries[] items = itemList.toArray(new ServerQueries[0]);
                                queriesViewModel.add(items);
                                Log.d(TAG, "Added " + items.length + " Queries to database");
                            } else {
                                Log.d(TAG, "No Queries returned from server");
                            }

                            // ✅ FIXED: Call onOperationComplete after success
                            handler.post(() -> {
                                if (!isDestroyed.get()) {
                                    onOperationComplete();
                                }
                            });

                        } else {
                            Log.e(TAG, "Queries response not successful");
                            handleErrorResponse(response.code(), "Queries");
                            onSyncError("Queries download failed: Error " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<DataWrapper<ServerQueries>> call, Throwable t) {
                        if (isDestroyed.get()) return;
                        Log.e(TAG, "Queries sync failed", t);
                        onSyncError("Queries download error: " + t.getMessage());
                    }
                });
            });
        });
    }

    /**
     * Generic method to send data in batches
     * Uses List internally but converts to array for ViewModel compatibility
     */
    private <T> void sendDataInBatches(
            List<T> allItems,
            String dataType,
            BiFunction<String, DataWrapper<T>, Call<DataWrapper<T>>> apiCallFunction,
            Consumer<List<T>> updateFunction,
            Runnable onComplete) {

        if (isDestroyed.get()) return;

        if (allItems == null || allItems.isEmpty()) {
            Log.d(TAG, "No " + dataType + " records to sync");
            handler.post(() -> {
                if (!isDestroyed.get()) {
                    onOperationComplete();
                    onComplete.run();
                }
            });
            return;
        }

        int totalBatches = (int) Math.ceil((double) allItems.size() / BATCH_SIZE);
        Log.d(TAG, "Total " + dataType + " to sync: " + allItems.size() + " in " + totalBatches + " batches");

        sendBatch(allItems, dataType, 0, totalBatches, apiCallFunction, updateFunction, onComplete);
    }

//    /**
//     * Recursive method to send individual batches
//     */
//    private <T> void sendBatch(
//            List<T> allItems,
//            String dataType,
//            int currentBatch,
//            int totalBatches,
//            BiFunction<String, DataWrapper<T>, Call<DataWrapper<T>>> apiCallFunction,
//            Consumer<List<T>> updateFunction,
//            Runnable onComplete) {
//
//        if (isDestroyed.get()) return;
//
//        int fromIndex = currentBatch * BATCH_SIZE;
//
//        if (fromIndex >= allItems.size()) {
//            // All batches completed
//            Log.d(TAG, "All " + dataType + " batches sent successfully");
//            handler.post(() -> {
//                if (!isDestroyed.get()) {
//                    onOperationComplete();
//                    onComplete.run();
//                }
//            });
//            return;
//        }
//
//        int toIndex = Math.min(fromIndex + BATCH_SIZE, allItems.size());
//        List<T> batch = allItems.subList(fromIndex, toIndex);
//        int batchNumber = currentBatch + 1;
//
//        handler.post(() -> {
//            if (isDestroyed.get()) return;
//
//            tvSyncStatus.setText("Sending " + dataType + " (" + batchNumber + "/" +
//                    totalBatches + ") - " + batch.size() + " records");
//
//            DataWrapper<T> wrappedData = new DataWrapper<>(batch);
//            Call<DataWrapper<T>> call = apiCallFunction.apply(authorizationHeader, wrappedData);
//
//            call.enqueue(new Callback<DataWrapper<T>>() {
//                @Override
//                public void onResponse(@NonNull Call<DataWrapper<T>> call, @NonNull Response<DataWrapper<T>> response) {
//                    if (isDestroyed.get()) return;
//
//                    if (response.isSuccessful() && response.body() != null
//                            && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                        List<T> sentDataList = wrappedData.getData();
//                        updateFunction.accept(sentDataList);
//
//                        Log.d(TAG, dataType + " batch " + batchNumber + "/" + totalBatches +
//                                " sent successfully (" + sentDataList.size() + " records)");
//
//                        handler.post(() -> {
//                            if (isDestroyed.get()) return;
//
//                            progress.setMessage("Sent " + dataType + " batch " + batchNumber + " of " +
//                                    totalBatches);
//
//                            // Send next batch after delay
//                            handler.postDelayed(() -> {
//                                        if (!isDestroyed.get()) {
//                                            sendBatch(allItems, dataType, currentBatch + 1, totalBatches,
//                                                    apiCallFunction, updateFunction, onComplete);
//                                        }
//                                    },
//                                    BATCH_DELAY_MS);
//                        });
//
//                    } else {
//                        Log.e(TAG, dataType + " batch " + batchNumber + " - Server error: " + response.code());
//                        onSyncError("Failed to send " + dataType + " batch " + batchNumber + ": Error " + response.code());
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<DataWrapper<T>> call, @NonNull Throwable t) {
//                    if (isDestroyed.get()) return;
//                    Log.e(TAG, dataType + " batch " + batchNumber + " send failed", t);
//                    onSyncError("Failed to send " + dataType + " batch " + batchNumber + ": " + t.getMessage());
//                }
//            });
//        });
//    }

    /**
     * Recursive method to send individual batches
     */
    private <T> void sendBatch(
            List<T> allItems,
            String dataType,
            int currentBatch,
            int totalBatches,
            BiFunction<String, DataWrapper<T>, Call<DataWrapper<T>>> apiCallFunction,
            Consumer<List<T>> updateFunction,
            Runnable onComplete) {

        if (isDestroyed.get()) return;

        int fromIndex = currentBatch * BATCH_SIZE;

        if (fromIndex >= allItems.size()) {
            // All batches completed
            Log.d(TAG, "All " + dataType + " batches sent successfully");
            handler.post(() -> {
                if (!isDestroyed.get()) {
                    onOperationComplete();
                    onComplete.run();
                }
            });
            return;
        }

        int toIndex = Math.min(fromIndex + BATCH_SIZE, allItems.size());
        List<T> batch = allItems.subList(fromIndex, toIndex);
        int batchNumber = currentBatch + 1;

        handler.post(() -> {
            if (isDestroyed.get()) return;

            // Update BOTH progress indicators
            String statusMessage = "Sending " + dataType + " (" + batchNumber + "/" +
                    totalBatches + ") - " + batch.size() + " records";

            tvSyncStatus.setText(statusMessage);

            // Update ProgressDialog message
            if (progress != null && progress.isShowing()) {
                progress.setMessage(statusMessage);
            }

            DataWrapper<T> wrappedData = new DataWrapper<>(batch);
            Call<DataWrapper<T>> call = apiCallFunction.apply(authorizationHeader, wrappedData);

            call.enqueue(new Callback<DataWrapper<T>>() {
                @Override
                public void onResponse(@NonNull Call<DataWrapper<T>> call, @NonNull Response<DataWrapper<T>> response) {
                    if (isDestroyed.get()) return;

                    if (response.isSuccessful() && response.body() != null
                            && response.body().getData() != null && !response.body().getData().isEmpty()) {

                        List<T> sentDataList = wrappedData.getData();
                        updateFunction.accept(sentDataList);

                        Log.d(TAG, dataType + " batch " + batchNumber + "/" + totalBatches +
                                " sent successfully (" + sentDataList.size() + " records)");

                        // Send next batch after delay
                        handler.postDelayed(() -> {
                                    if (!isDestroyed.get()) {
                                        sendBatch(allItems, dataType, currentBatch + 1, totalBatches,
                                                apiCallFunction, updateFunction, onComplete);
                                    }
                                },
                                BATCH_DELAY_MS);

                    } else {
                        Log.e(TAG, dataType + " batch " + batchNumber + " - Server error: " + response.code());
                        onSyncError("Failed to send " + dataType + " batch " + batchNumber + ": Error " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DataWrapper<T>> call, @NonNull Throwable t) {
                    if (isDestroyed.get()) return;
                    Log.e(TAG, dataType + " batch " + batchNumber + " send failed", t);
                    onSyncError("Failed to send " + dataType + " batch " + batchNumber + ": " + t.getMessage());
                }
            });
        });
    }

    private void onOperationComplete() {
        if (isDestroyed.get()) return;

        syncOperationsCompleted++;
        Log.d(TAG, "Operation completed: " + syncOperationsCompleted + "/" + totalSyncOperations);

        if (syncOperationsCompleted >= totalSyncOperations) {
            onSyncSuccess();
        }
    }

    private void onSyncSuccess() {
        if (isDestroyed.get()) return;

        runOnUiThread(() -> {
            if (isDestroyed.get()) return;

            isSyncing.set(false);

            // Dismiss ProgressDialog
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            // Update UI to show success
            syncProgressBar.setVisibility(View.GONE);
            tvSyncStatus.setVisibility(View.VISIBLE);
            tvSyncStatus.setText("Sync completed successfully!");
            tvSyncStatus.setTextColor(ContextCompat.getColor(this, R.color.LimeGreen));

            saveLastSyncTime();
            updateLastSyncTime();

            buttonSyncAll.setText("Sync Completed Successfully");
            buttonSyncAll.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));

            Toast.makeText(this, "All data synced successfully!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "=== SYNC SUCCESS ===");

            // Reset button after 3 seconds
            handler.postDelayed(() -> {
                if (!isDestroyed.get()) {
                    buttonSyncAll.setText("Sync All Data");
                    buttonSyncAll.setTextColor(ContextCompat.getColor(PushActivity.this, android.R.color.white));
                    buttonSyncAll.setEnabled(true);
                    tvSyncStatus.setVisibility(View.GONE);
                }
            }, 3000);
        });
    }

    private void onSyncError(String errorMessage) {
        if (isDestroyed.get()) return;

        runOnUiThread(() -> {
            if (isDestroyed.get()) return;

            isSyncing.set(false);

            // Dismiss ProgressDialog
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            // Update UI to show error
            syncProgressBar.setVisibility(View.GONE);
            tvSyncStatus.setVisibility(View.VISIBLE);
            tvSyncStatus.setText("Sync failed!");
            tvSyncStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

            syncOperationsCompleted = 0;
            buttonSyncAll.setEnabled(true);

            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Sync error: " + errorMessage);

            // Reset status text after 3 seconds
            handler.postDelayed(() -> {
                if (!isDestroyed.get()) {
                    tvSyncStatus.setVisibility(View.GONE);
                }
            }, 3000);
        });
    }

    private void handleErrorResponse(int responseCode, String dataType) {
        String errorMessage;
        switch (responseCode) {
            case 401:
                errorMessage = "Authentication failed. Please login again.";
                break;
            case 403:
                errorMessage = "Access forbidden for " + dataType;
                break;
            case 404:
                errorMessage = dataType + " endpoint not found";
                break;
            case 500:
                errorMessage = "Server error while processing " + dataType;
                break;
            default:
                errorMessage = "Error " + responseCode + " while syncing " + dataType;
        }
        Log.e(TAG, errorMessage);
    }

    private void updateLastSyncTime() {
        if (tvLastSyncTime != null && !isDestroyed.get()) {
            SharedPreferences prefs = getSharedPreferences("SyncDatePreferences", Context.MODE_PRIVATE);
            long lastSyncMillis = prefs.getLong("lastSyncTime", 0);

            if (lastSyncMillis == 0) {
                tvLastSyncTime.setText("Last sync: Never");
            } else {
                String formattedTime = formatSyncTime(lastSyncMillis);
                tvLastSyncTime.setText("Last sync: " + formattedTime);
            }
        }
    }

    private String formatSyncTime(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timeMillis));
    }

    private void saveLastSyncTime() {
        SharedPreferences prefs = getSharedPreferences("SyncDatePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastSyncTime", System.currentTimeMillis());
        editor.apply();
    }

    private void countStats() {
        // Only initialize observers once
        if (observersInitialized) {
            return;
        }
        observersInitialized = true;

        // Create LiveData instances
        locLiveData = locationViewModel.sync();
        listingLiveData = listingViewModel.sync();
        visitLiveData = visitViewModel.sync();
        indLiveData = individualViewModel.sync();
        hohLiveData = socialgroupViewModel.sync();
        relLiveData = relationshipViewModel.sync();
        pregLiveData = pregnancyViewModel.sync();
        pregoutLiveData = pregnancyoutcomeViewModel.sync();
        outcomeLiveData = outcomeViewModel.sync();
        demLiveData = demographicViewModel.sync();
        dthLiveData = deathViewModel.sync();
        vpmLiveData = vpmViewModel.sync();
        sesLiveData = hdssSociodemoViewModel.sync();
        resLiveData = residencyViewModel.sync();
        imgLiveData = inmigrationViewModel.sync();
        omgLiveData = outmigrationViewModel.sync();
        amendLiveData = amendmentViewModel.sync();
        vacLiveData = vaccinationViewModel.sync();
        dupLiveData = duplicateViewModel.sync();
        comLiveData = communityViewModel.sync();
        regLiveData = registryViewModel.sync();
        morLiveData = morbidityViewModel.sync();

        // Observe all LiveData instances
        locLiveData.observe(this, total -> {
            loc = total != null ? total : 0L;
            updateCompleted();
        });

        listingLiveData.observe(this, total -> {
            list = total != null ? total : 0L;
            updateCompleted();
        });

        visitLiveData.observe(this, total -> {
            visit = total != null ? total : 0L;
            updateCompleted();
        });

        indLiveData.observe(this, total -> {
            ind = total != null ? total : 0L;
            updateCompleted();
        });

        hohLiveData.observe(this, total -> {
            hoh = total != null ? total : 0L;
            updateCompleted();
        });

        relLiveData.observe(this, total -> {
            rel = total != null ? total : 0L;
            updateCompleted();
        });

        pregLiveData.observe(this, total -> {
            preg = total != null ? total : 0L;
            updateCompleted();
        });

        pregoutLiveData.observe(this, total -> {
            pregout = total != null ? total : 0L;
            updateCompleted();
        });

        outcomeLiveData.observe(this, total -> {
            out = total != null ? total : 0L;
            updateCompleted();
        });

        demLiveData.observe(this, total -> {
            dem = total != null ? total : 0L;
            updateCompleted();
        });

        dthLiveData.observe(this, total -> {
            dth = total != null ? total : 0L;
            updateCompleted();
        });

        vpmLiveData.observe(this, total -> {
            vpm = total != null ? total : 0L;
            updateCompleted();
        });

        sesLiveData.observe(this, total -> {
            ses = total != null ? total : 0L;
            updateCompleted();
        });

        resLiveData.observe(this, total -> {
            res = total != null ? total : 0L;
            updateCompleted();
        });

        imgLiveData.observe(this, total -> {
            img = total != null ? total : 0L;
            updateCompleted();
        });

        omgLiveData.observe(this, total -> {
            omg = total != null ? total : 0L;
            updateCompleted();
        });

        amendLiveData.observe(this, total -> {
            amend = total != null ? total : 0L;
            updateCompleted();
        });

        vacLiveData.observe(this, total -> {
            vac = total != null ? total : 0L;
            updateCompleted();
        });

        dupLiveData.observe(this, total -> {
            dup = total != null ? total : 0L;
            updateCompleted();
        });

        comLiveData.observe(this, total -> {
            com = total != null ? total : 0L;
            updateCompleted();
        });

        regLiveData.observe(this, total -> {
            reg = total != null ? total : 0L;
            updateCompleted();
        });

        morLiveData.observe(this, total -> {
            mor = total != null ? total : 0L;
            updateCompleted();
        });
    }

    private void updateCompleted() {
        if (isDestroyed.get() || totalRecords == null) return;

        String statsText = "Locations: " + loc +
                " | Listing: " + list +
                " | Visit: " + visit +
                " | Individual: " + ind +
                " | Socialgroup: " + hoh +
                " | Relationship: " + rel +
                " | Pregnancy: " + preg +
                " | PregOutcome: " + pregout +
                " | Outcome: " + out +
                " | Demographic: " + dem +
                " | Death: " + dth +
                " | VPM: " + vpm +
                " | Sociodemo: " + ses +
                " | Residency: " + res +
                " | Inmigration: " + img +
                " | Outmigration: " + omg +
                " | Amendment: " + amend +
                " | Vaccination: " + vac +
                " | Duplicate: " + dup +
                " | Community: " + com +
                " | Registry: " + reg +
                " | Morbidity: " + mor;
        totalRecords.setText(statsText);
    }
}