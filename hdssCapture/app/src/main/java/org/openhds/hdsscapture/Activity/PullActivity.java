package org.openhds.hdsscapture.Activity;

import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_DEMO;
import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_IND;
import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_SES;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.Dao.OdkFormDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.Dao.VaccinationDao;
import org.openhds.hdsscapture.Dialog.DemoDownloadSummary;
import org.openhds.hdsscapture.Dialog.ExtraDownloadSummary;
import org.openhds.hdsscapture.Dialog.IndividualDownloadSummary;
import org.openhds.hdsscapture.Dialog.LocationDownloadSummary;
import org.openhds.hdsscapture.Dialog.OtherDownloadSummary;
import org.openhds.hdsscapture.Dialog.ProgressDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyLevelViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkFormViewModel;
import org.openhds.hdsscapture.Viewmodel.QueriesViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.ServerQueries;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.odk.OdkForm;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PullActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progres;
    private IndividualRepository individualRepository;
    private IndividualDao individualDao;
    private LocationDao locationDao;
    private ResidencyDao residencyDao;
    private RelationshipDao relationshipDao;
    private SocialgroupDao socialgroupDao;
    private PregnancyDao pregnancyDao;
    private DemographicDao demographicDao;
    private VaccinationDao vaccinationDao;
    private HdssSociodemoDao hdssSociodemoDao;
    private OdkFormDao odkDao;
    AppDatabase appDatabase;
    private Button downloadAllButton;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private AppCompatButton complete;
    private String fwname;

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog simpleDialog = SimpleDialog.newInstance(message, codeFragment);
        simpleDialog.show(getSupportFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }

    public String getLastSyncDatetime() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        return prefs.getString("lastSyncDatetime", "");
    }

    private void setLastSyncDatetime(String datetime) {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastSyncDatetime", datetime);
        editor.apply();
    }

    public String getIndSyncDatetime() {
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        return pref.getString("IndSyncDatetime", "");
    }

    private void setLastIndDatetime(String datetime) {
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("IndSyncDatetime", datetime);
        editor.apply();
    }

    public String getEventsSyncDatetime() {
        SharedPreferences event = getPreferences(Context.MODE_PRIVATE);
        return event.getString("EventsDatetime", "");
    }

    private void setLastEventsDatetime(String datetime) {
        SharedPreferences event = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = event.edit();
        editor.putString("EventsDatetime", datetime);
        editor.apply();
    }

    public String getSesSyncDatetime() {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        return ses.getString("SesDatetime", "");
    }

    private void setLastSesDatetime(String datetime) {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ses.edit();
        editor.putString("SesDatetime", datetime);
        editor.apply();
    }

    public String getLastDemoDatetime() {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        return ses.getString("DemoDatetime", "");
    }

    private void setLastDemoDatetime(String datetime) {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ses.edit();
        editor.putString("DemoDatetime", datetime);
        editor.apply();
    }

    public String getLastLocDatetime() {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        return ses.getString("LocDatetime", "");
    }

    private void setLastLocDatetime(String datetime) {
        SharedPreferences ses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ses.edit();
        editor.putString("LocDatetime", datetime);
        editor.apply();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Button sync = findViewById(R.id.syncDetails);
        sync.setOnClickListener(v -> {
            ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
            progressDialogFragment.show(getSupportFragmentManager(), "ProgressDialogFragment");
        });

        final ImageButton ind = findViewById(R.id.indsummary);
        ind.setOnClickListener(v -> {
            IndividualDownloadSummary progressDialogFragment = new IndividualDownloadSummary();
            progressDialogFragment.show(getSupportFragmentManager(), "IndividualDownloadSummary");
        });

        final ImageButton extra = findViewById(R.id.extraSummary);
        extra.setOnClickListener(v -> {
            ExtraDownloadSummary progressDialogFragment = new ExtraDownloadSummary();
            progressDialogFragment.show(getSupportFragmentManager(), "ExtraDownloadSummary");
        });

        final ImageButton demo = findViewById(R.id.demoSummary);
        demo.setOnClickListener(v -> {
            DemoDownloadSummary progressDialogFragment = new DemoDownloadSummary();
            progressDialogFragment.show(getSupportFragmentManager(), "DemoDownloadSummary");
        });

        final ImageButton loc = findViewById(R.id.locSummary);
        loc.setOnClickListener(v -> {
            LocationDownloadSummary progressDialogFragment = new LocationDownloadSummary();
            progressDialogFragment.show(getSupportFragmentManager(), "LocationDownloadSummary");
        });

        final ImageButton other = findViewById(R.id.syncD1);
        other.setOnClickListener(v -> {
            OtherDownloadSummary progressDialogFragment = new OtherDownloadSummary();
            progressDialogFragment.show(getSupportFragmentManager(), "OtherDownloadSummary");
        });

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fwname = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);

        appDatabase = AppDatabase.getDatabase(this);
        locationDao = appDatabase.locationDao();
        individualDao = appDatabase.individualDao();
        socialgroupDao = appDatabase.socialgroupDao();
        residencyDao = appDatabase.residencyDao();
        pregnancyDao = appDatabase.pregnancyDao();
        relationshipDao = appDatabase.relationshipDao();
        demographicDao = appDatabase.demographicDao();
        hdssSociodemoDao = appDatabase.hdssSociodemoDao();
        vaccinationDao = appDatabase.vaccinationDao();

        progres = new ProgressDialog(PullActivity.this);
        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progres.setIndeterminate(true);
        progres.setMessage("Initializing...");
        progres.setCancelable(false);


        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        /*final Button button_SyncCodeBook = findViewById(R.id.button_SyncCodeBook);
        button_SyncCodeBook.setOnClickListener(v -> {
            final TextView textView_SyncCodeBook = findViewById(R.id.textView_SyncCodebook);
            textView_SyncCodeBook.setText("");
            progres.show();

            progres.setMessage("Updating Data Dictionary...");
            final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);

            Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook();
            c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                @Override
                public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                    CodeBook[] d = response.body().getData().toArray(new CodeBook[0]);
                    viewModel.add(d);
                    progres.dismiss();
                    textView_SyncCodeBook.setText("Codebook Updated 100%!");
                    textView_SyncCodeBook.setTextColor(Color.rgb(0, 114, 133));
                }

                @Override
                public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {

                }
            });
        });*/

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String authorizationHeader = preferences.getString("authorizationHeader", null);

        //Download Zip Files
        downloadAllButton = findViewById(R.id.downloadAllButton);
        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);
        complete = findViewById(R.id.btn_complete);

        complete.setOnClickListener(v -> {
            deleteFiles(getExternalCacheDir());
        });

        File individualZipFile = new File(getExternalCacheDir(), "individual.zip");
        File residencyZipFile = new File(getExternalCacheDir(), "residency.zip");
        File locationZipFile = new File(getExternalCacheDir(), "location.zip");
        File socialgroupZipFile = new File(getExternalCacheDir(), "socialgroup.zip");
        File pregnancyZipFile = new File(getExternalCacheDir(), "pregnancy.zip");
        File relationshipZipFile = new File(getExternalCacheDir(), "relationship.zip");
        File demographicsZipFile = new File(getExternalCacheDir(), "demographics.zip");
        File sesZipFile = new File(getExternalCacheDir(), "ses.zip");
        File vaccinationZipFile = new File(getExternalCacheDir(), "vaccination.zip");


        downloadAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for internet connection on the main thread
                if (!isInternetAvailable()) {
                    runOnUiThread(() -> {
                        Toast.makeText(PullActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Show the progress bar and update status
                progressBar.setVisibility(View.VISIBLE);
                statusTextView.setText("Status: Downloading...");

                // Ensure the file names and download calls match in size
                String[] fileNames = {"individual.zip", "residency.zip", "location.zip", "socialgroup.zip", "pregnancy.zip", "relationship.zip", "demographics.zip", "ses.zip", "vaccination.zip"};
                Supplier<Call<ResponseBody>>[] downloadCalls = new Supplier[]{
                        () -> dao.downloadIndividual(authorizationHeader),
                        () -> dao.downloadResidency(authorizationHeader),
                        () -> dao.downloadLocation(authorizationHeader),
                        () -> dao.downloadSocialgroup(authorizationHeader),
                        () -> dao.downloadPregnancy(authorizationHeader),
                        () -> dao.downloadRelationship(authorizationHeader),
                        () -> dao.downloadDemography(authorizationHeader),
                        () -> dao.downloadSes(authorizationHeader),
                        () -> dao.downloadVaccination(authorizationHeader)
                };

                if (fileNames.length != downloadCalls.length) {
                    statusTextView.setText("Status: Error - Mismatched file names and download calls");
                    return;
                }

                downloadZipFiles(fileNames, downloadCalls, () -> {
                    // Hide the progress bar and update status when done
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        statusTextView.setText("Status: Download Complete");
                    });
                });
            }

            private void downloadZipFiles(String[] fileNames, Supplier<Call<ResponseBody>>[] downloadCalls, Runnable onComplete) {
                new Thread(() -> {
                    int totalFiles = fileNames.length;
                    int downloadedFiles = 0;
                    boolean isErrorOccurred = false;

                    for (int i = 0; i < totalFiles; i++) {
                        String fileName = fileNames[i];
                        Supplier<Call<ResponseBody>> downloadCall = downloadCalls[i];

                        File file = new File(getExternalCacheDir(), fileName);

                        try {
                            Response<ResponseBody> response = downloadCall.get().execute();
                            if (response.isSuccessful()) {
                                InputStream inputStream = response.body().byteStream();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                downloadedFiles++;

                                // Calculate progress as percentage
                                int progress = (int) (((float) downloadedFiles / totalFiles) * 100);
                                runOnUiThread(() -> statusTextView.setText("Status: Downloading... " + fileName +" " + progress + "%"));

                            } else {
                                isErrorOccurred = true;
                                runOnUiThread(() -> statusTextView.setText("Status: Failed to download " + fileName));
                            }
                        } catch (IOException e) {
                            isErrorOccurred = true;
                            e.printStackTrace();
                            runOnUiThread(() -> statusTextView.setText("Status: Error downloading " + fileName));
                        }

                        if (isErrorOccurred) {
                            break; // Exit loop if an error occurs
                        }
                    }

                    // Check if all files were downloaded successfully
                    if (downloadedFiles == totalFiles && !isErrorOccurred) {
                        runOnUiThread(onComplete);
                    } else {
                        runOnUiThread(() -> statusTextView.setText("Status: Download incomplete"));
                    }

                    // Hide the progress bar in any case
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }).start();
            }
        });





        // Initialize the last sync datetime when the activity is created
        String lastSyncDatetime = getLastSyncDatetime();
        if (lastSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            lastSyncDatetime = sdf.format(new Date());
            setLastSyncDatetime(lastSyncDatetime);
        }

        // Set the initial last sync datetime in the TextView
        final TextView syncEntityDateTextView = findViewById(R.id.syncEntityDate);
        syncEntityDateTextView.setText(lastSyncDatetime);

        //Sync LocationHierarchy
        final Button syncHierarchyData = findViewById(R.id.syncSettings);
        syncHierarchyData.setOnClickListener(v -> {

            final TextView textView_SyncHierarchyData = findViewById(R.id.syncCodebookMessage);
            final ProgressBar progressBar = findViewById(R.id.codebookProgressBar);
            textView_SyncHierarchyData.setText("");
            progres.setTitle("Downloading ");
            progres.setProgress(0);
            progres.setMax(100);
            progres.show();
            progressBar.setProgress(0);

            //when region is synched, sync Hierarchy
            textView_SyncHierarchyData.setText("Updating Hierarchy...");
            progres.setTitle("Updating Hierarchy...");
            final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(PullActivity.this).get(HierarchyViewModel.class);
            Call<DataWrapper<Hierarchy>> c_callable = dao.getAllHierarchy(authorizationHeader);
            c_callable.enqueue(new Callback<DataWrapper<Hierarchy>>() {
                @Override
                public void onResponse(Call<DataWrapper<Hierarchy>> call, Response<DataWrapper<Hierarchy>> response) {
                    Hierarchy[] hierarchies = response.body().getData().toArray(new Hierarchy[0]);
                    hierarchyViewModel.add(hierarchies);


                    //Sync Round
                    textView_SyncHierarchyData.setText("Updating Round...");
                    progres.setTitle("Updating Round...");
                    final RoundViewModel round = new ViewModelProvider(PullActivity.this).get(RoundViewModel.class);
                    Call<DataWrapper<Round>> c_callable = dao.getRound(authorizationHeader);
                    c_callable.enqueue(new Callback<DataWrapper<Round>>() {
                        @Override
                        public void onResponse(Call<DataWrapper<Round>> call, Response<DataWrapper<Round>> response) {
                            Round[] i = response.body().getData().toArray(new Round[0]);
                            for (Round rounds : i) {
                                rounds.insertDate = new Date();
                                Log.d("DateInsertion", "Round insert date: " + rounds.insertDate);
                            }
                            round.add(i);

                            //Sync Round
                            textView_SyncHierarchyData.setText("Updating Codebook...");
                            progres.setTitle("Updating Codebook...");
                            final CodeBookViewModel codeBook = new ViewModelProvider(PullActivity.this).get(CodeBookViewModel.class);
                            Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook(authorizationHeader);
                            c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                                @Override
                                public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                                    CodeBook[] co = response.body().getData().toArray(new CodeBook[0]);
                                    codeBook.add(co);

                                    //Sync Fieldworker
                                    textView_SyncHierarchyData.setText("Updating Fieldworker...");
                                    progres.setTitle("Updating Fieldworker...");
                                    final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(PullActivity.this).get(FieldworkerViewModel.class);
                                    Call<DataWrapper<Fieldworker>> c_callable = dao.getFw(authorizationHeader);
                                    c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                                        @Override
                                        public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                                            Fieldworker[] fw = response.body().getData().toArray(new Fieldworker[0]);
                                            fieldworkerViewModel.add(fw);

                                            //Sync Community
                                            textView_SyncHierarchyData.setText("Updating Community Report...");
                                            progres.setTitle("Updating Community Report...");
                                            final CommunityViewModel communityViewModel = new ViewModelProvider(PullActivity.this).get(CommunityViewModel.class);
                                            Call<DataWrapper<CommunityReport>> c_callable = dao.getCommunity(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<CommunityReport>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<CommunityReport>> call, Response<DataWrapper<CommunityReport>> response) {
                                                    CommunityReport[] cm = response.body().getData().toArray(new CommunityReport[0]);
                                                    communityViewModel.add(cm);

                                                    //Sync HierarchyLevel
                                                    final HierarchyLevelViewModel hieViewModel = new ViewModelProvider(PullActivity.this).get(HierarchyLevelViewModel.class);
                                                    Call<DataWrapper<HierarchyLevel>> c_callable = dao.getHierarchyLevel(authorizationHeader);
                                                    c_callable.enqueue(new Callback<DataWrapper<HierarchyLevel>>() {
                                                        @Override
                                                    public void onResponse(Call<DataWrapper<HierarchyLevel>> call, Response<DataWrapper<HierarchyLevel>> response) {
                                                    HierarchyLevel[] hie = response.body().getData().toArray(new HierarchyLevel[0]);
                                                    hieViewModel.add(hie);

                                            //Sync ODK EXTRA
                                            final OdkFormViewModel odkViewModel = new ViewModelProvider(PullActivity.this).get(OdkFormViewModel.class);
                                            Call<DataWrapper<OdkForm>> c_callable = dao.getOdk(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<OdkForm>>() {
                                            @Override
                                            public void onResponse(Call<DataWrapper<OdkForm>> call, Response<DataWrapper<OdkForm>> response) {
                                            OdkForm[] odk = response.body().getData().toArray(new OdkForm[0]);
                                            odkViewModel.add(odk);

                                                //Sync Server Queries
                                                final QueriesViewModel queriesViewModel = new ViewModelProvider(PullActivity.this).get(QueriesViewModel.class);
                                                Call<DataWrapper<ServerQueries>> c_callable = dao.getQueries(authorizationHeader, fwname);
                                                c_callable.enqueue(new Callback<DataWrapper<ServerQueries>>() {
                                                    @Override
                                                    public void onResponse(Call<DataWrapper<ServerQueries>> call, Response<DataWrapper<ServerQueries>> response) {
                                                        ServerQueries[] query = response.body().getData().toArray(new ServerQueries[0]);
                                                        queriesViewModel.add(query);

                                            //Sync Settings
                                            textView_SyncHierarchyData.setText("Updating Settings...");
                                            progres.setTitle("Updating Settings...");
                                            final ConfigViewModel configViewModel = new ViewModelProvider(PullActivity.this).get(ConfigViewModel.class);
                                            Call<DataWrapper<Configsettings>> c_callable = dao.getConfig(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<Configsettings>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<Configsettings>> call, Response<DataWrapper<Configsettings>> response) {
                                                    Configsettings[] cng = response.body().getData().toArray(new Configsettings[0]);
                                                    configViewModel.add(cng);
                                                    progres.dismiss();
                                                    progressBar.setProgress(100);

                                                    final AtomicReference<String> lastSyncDatetime = new AtomicReference<>(getLastSyncDatetime());

                                                    // Inside the synchronization process after a successful sync
                                                    // Only change the date if there is another sync
                                                    String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                                                    if (!currentDateWithTime.equals(lastSyncDatetime.get())) {
                                                        syncEntityDateTextView.setText(currentDateWithTime);
                                                        lastSyncDatetime.set(currentDateWithTime);
                                                        setLastSyncDatetime(currentDateWithTime);
                                                    }
                                                    textView_SyncHierarchyData.setText("Codebook and Locationhierarchy updated Successfully");
                                                    textView_SyncHierarchyData.setTextColor(Color.parseColor("#32CD32"));

                                                }


                                                @Override
                                                public void onFailure(Call<DataWrapper<Configsettings>> call, Throwable t) {
                                                    progres.dismiss();
                                                    progressBar.setProgress(0);
                                                    textView_SyncHierarchyData.setText("Settings Sync Error!");
                                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                                }
                                            });
                                        }

                                                    @Override
                                                    public void onFailure(Call<DataWrapper<ServerQueries>> call, Throwable t) {
                                                        progres.dismiss();
                                                        progressBar.setProgress(0);
                                                        textView_SyncHierarchyData.setText("Queries Sync Error!");
                                                        textView_SyncHierarchyData.setTextColor(Color.RED);
                                                    }
                                                });
                                            }
                                                        @Override
                                                        public void onFailure(Call<DataWrapper<OdkForm>> call, Throwable t) {
                                                            progres.dismiss();
                                                            progressBar.setProgress(0);
                                                            textView_SyncHierarchyData.setText("ODK Sync Error!");
                                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                                        }
                                                    });
                                                   }

                                                        @Override
                                                        public void onFailure(Call<DataWrapper<HierarchyLevel>> call, Throwable t) {
                                                            progres.dismiss();
                                                            progressBar.setProgress(0);
                                                            textView_SyncHierarchyData.setText("Level Sync Error!");
                                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<DataWrapper<CommunityReport>> call, Throwable t) {
                                                    progres.dismiss();
                                                    progressBar.setProgress(0);
                                                    textView_SyncHierarchyData.setText("Community Report Sync Error!");
                                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {
                                            progres.dismiss();
                                            progressBar.setProgress(0);
                                            textView_SyncHierarchyData.setText("Fieldworker Sync Error!");
                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {
                                    progres.dismiss();
                                    progressBar.setProgress(0);
                                    textView_SyncHierarchyData.setText("Codebook Sync Error!");
                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<DataWrapper<Round>> call, Throwable t) {
                            progres.dismiss();
                            progressBar.setProgress(0);
                            textView_SyncHierarchyData.setText("Round Sync Error!");
                            textView_SyncHierarchyData.setTextColor(Color.RED);
                        }
                    });
                }


                @Override
                public void onFailure(Call<DataWrapper<Hierarchy>> call, Throwable t) {
                    progres.dismiss();
                    progressBar.setProgress(0);
                    textView_SyncHierarchyData.setText("Hierarchy Sync Error!");
                    textView_SyncHierarchyData.setTextColor(Color.RED);
                }
            });


        });


        //Sync Location and Socialgroup
        String LocSyncDatetime = getLastLocDatetime();
        if (LocSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            LocSyncDatetime = sdf.format(new Date());
            setLastLocDatetime(LocSyncDatetime);
        }
        final TextView syncLocDateTextView = findViewById(R.id.syncLocationDate);
        syncLocDateTextView.setText(LocSyncDatetime);

        final Button button_loc = findViewById(R.id.syncLocation);
        button_loc.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncLocationMessage);
            final ProgressBar progressBar = findViewById(R.id.LocationProgressBar);
            AtomicLong locationCounts = new AtomicLong();
            AtomicLong socialgroupCounts = new AtomicLong();
            String loc = "Location";
            String soc = "Socialgroup";

            @Override
            public void onClick(View v) {
                resetAndShowProgress("Starting Sync...", "Syncing data"); // Show immediately
                new Thread(() -> {
                    // Perform heavy work in background
                    resetAllCounts();
                    checkAndProcessLocation();
                }).start();
            }

            private void checkAndProcessLocation() {
                if (locationZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Location file exists. Extracting and processing..."));

                    // Do file extraction and processing in background if it's long-running
                    extractAndProcessFile("location.zip", "location.csv", Locations.class, locationCounts, loc, this::checkAndProcessSocialgroup);
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Location file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }

            private void checkAndProcessSocialgroup() {
                if (socialgroupZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Socialgroup file exists. Extracting and processing..."));

                    extractAndProcessFile("socialgroup.zip", "socialgroup.csv", Socialgroup.class, socialgroupCounts, soc, () -> {
                        // All datasets processed, perform any final actions here
                        runOnUiThread(() -> {
                            // If progres might be null or already dismissed, add checks
                            if (progres != null && progres.isShowing()) {
                                progres.dismiss();
                            }
                        });
                    });
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Socialgroup file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }


            private void extractAndProcessFile(String zipFileName, String extractedFileName, Class<?> entityClass, AtomicLong countKey, String files, Runnable nextStep) {
                // Extract the zip file and process it
                File zipFile = new File(getExternalCacheDir(), zipFileName);
                File extractedFile = new File(getExternalCacheDir(), extractedFileName);
                try {
                    // Unzip the file
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        if (fileName.equals(extractedFileName)) {
                            FileOutputStream fos = new FileOutputStream(extractedFile);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            break;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();

                    // If the extracted CSV file is empty, skip processing and move to the next step
                    if (extractedFile.length() == 0) {
                        nextStep.run();
                        return;
                    }

                    // Process the CSV file and insert data into the database
                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                    CsvMapper mapper = new CsvMapper();
                    CsvSchema schema = getCsvSchemaForEntity(entityClass);
                    MappingIterator<?> iterator = mapper.readerFor(entityClass).with(schema).readValues(extractedFile);

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // *** RESET TABLE BEFORE INSERTION ***
                        resetTableForEntity(entityClass);
                        // Update UI to show table is being reset
                        runOnUiThread(() -> {
                            textView_Sync.setText("Clearing existing " + files + " data...");
                        });

                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 20000;
                        int batchCount = 0;

                        // Update UI to show processing is starting
                        runOnUiThread(() -> {
                            textView_Sync.setText("Processing " + files + " data...");
                        });

                        while (iterator.hasNext()) {
                            Object entity = iterator.next();
                            if (entity != null) {
                                entities.add(entity);
                                batchCount++;

                                if (batchCount == batchSize) {
                                    insertEntitiesIntoDatabase(entities, entityClass);
                                    entities.clear();
                                    batchCount = 0;
                                }

                                updateProgress(counts, totalRecords, entityClass, files);
                            }
                        }

                        // Insert remaining entities
                        if (batchCount > 0) {
                            insertEntitiesIntoDatabase(entities, entityClass);
                        }

                        // Save counts to shared preferences after processing
                        saveCountsToSharedPreferences(files, counts.get());

                        runOnUiThread(() -> {
                            textView_Sync.setText("Successful Download ");
                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                            nextStep.run();

                            // Update the synchronization date
                            final AtomicReference<String> LocDatetime = new AtomicReference<>(getLastLocDatetime());

                            // Inside the synchronization process after a successful sync
                            // Only change the date if there is another sync
                            String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                            if (!currentDateWithTime.equals(LocDatetime.get())) {
                                syncLocDateTextView.setText(currentDateWithTime);
                                LocDatetime.set(currentDateWithTime);
                                setLastLocDatetime(currentDateWithTime);
                            }
                        });

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textView_Sync.setText("Error processing file. Please try again.");
                    });
                }
            }

            // *** RESET TABLE METHOD ***
            private void resetTableForEntity(Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Locations.class)) {
                    // Clear all data from Locations table
                    locationDao.deleteAll(); // You'll need to add this method to your DAO
                } else if (entityClass.equals(Socialgroup.class)) {
                    // Clear all data from Socialgroup table
                    socialgroupDao.deleteAll(); // You'll need to add this method to your DAO
                }
            }

            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Locations.class)) {
                    return CsvSchema.emptySchema().withHeader();
                } else if (entityClass.equals(Socialgroup.class)) {
                    return CsvSchema.emptySchema().withHeader();
                }
                return CsvSchema.emptySchema();
            }

            private long countTotalRecords(File csvFile) {
                // Count total records in the CSV
                try {
                    CsvMapper countMapper = new CsvMapper();
                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();
                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                    Iterator<Map<String, String>> countIterator = countReader.readValues(csvFile);
                    long totalRecords = 0;
                    while (countIterator.hasNext()) {
                        countIterator.next();
                        totalRecords++;
                    }
                    return totalRecords;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            private void insertEntitiesIntoDatabase(List<Object> entities, Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Locations.class)) {
                    locationDao.insert((Locations[]) entities.toArray(new Locations[0]));
                } else if (entityClass.equals(Socialgroup.class)) {
                    socialgroupDao.insert((Socialgroup[]) entities.toArray(new Socialgroup[0]));
                }
            }

            private void updateProgress(AtomicLong counts, long[] totalRecords, Class<?> entityClass, String files) {
                long currentCount = counts.incrementAndGet();
                int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                int totalRecordsCount = (int) totalRecords[0];
                int cnt = (int) currentCount;
                if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Saving " + progress + "% of " + files);
                        progres.setTitle("Saving " + files + " Dataset");
                        progressBar.setProgress(progress);
                        progres.setMax(totalRecordsCount);
                        progres.setProgress(cnt);
                        progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
                        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        // Ensure ProgressDialog is visible and updated
                        if (progres.isIndeterminate()) {
                            progres.setIndeterminate(false);
                        }
                    });
                }
            }

            private void resetAllCounts() {
                resetCountOnClick(loc);
                resetCountOnClick(soc);
            }
        });



        //Sync Individual and Residency
        String IndSyncDatetime = getIndSyncDatetime();
        if (IndSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            IndSyncDatetime = sdf.format(new Date());
            setLastIndDatetime(IndSyncDatetime);
        }
        final TextView syncDateTextView = findViewById(R.id.syncIndividualDate);
        syncDateTextView.setText(IndSyncDatetime);

        final Button button_SyncAll = findViewById(R.id.syncIndividual);
        button_SyncAll.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncIndividualMessage);
            final ProgressBar progressBar = findViewById(R.id.IndividualProgressBar);
            AtomicLong individualCounts = new AtomicLong();
            AtomicLong residencyCounts = new AtomicLong();
            String ind = "Individual";
            String res = "Residency";

            @Override
            public void onClick(View v) {
                resetAndShowProgress("Starting Sync...", "Syncing data"); // Show immediately
                new Thread(() -> {
                    // Perform heavy work in background
                    resetAllCounts();
                    checkAndProcessIndividual();
                }).start();
            }

            private void checkAndProcessIndividual() {
                if (individualZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Individual file exists. Extracting and processing..."));

                    extractAndProcessFile("individual.zip", "individual.csv", Individual.class, individualCounts, ind, this::checkAndProcessResidency);
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Individual file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }

            private void checkAndProcessResidency() {
                if (residencyZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Residency file exists. Extracting and processing..."));

                    extractAndProcessFile("residency.zip", "residency.csv", Residency.class, residencyCounts, res, () -> {
                        runOnUiThread(() -> {
                            if (progres != null && progres.isShowing()) {
                                progres.dismiss();
                            }
                        });
                    });
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Residency file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }


            private void extractAndProcessFile(String zipFileName, String extractedFileName, Class<?> entityClass, AtomicLong countKey, String files, Runnable nextStep) {
                // Extract the zip file and process it
                File zipFile = new File(getExternalCacheDir(), zipFileName);
                File extractedFile = new File(getExternalCacheDir(), extractedFileName);
                try {
                    // Unzip the file
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        if (fileName.equals(extractedFileName)) {
                            FileOutputStream fos = new FileOutputStream(extractedFile);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            break;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();

                    // If the extracted CSV file is empty, skip processing and move to the next step
                    if (extractedFile.length() == 0) {
                        nextStep.run();
                        return;
                    }

                    // Process the CSV file and insert data into the database
                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                    CsvMapper mapper = new CsvMapper();
                    CsvSchema schema = getCsvSchemaForEntity(entityClass);
                    MappingIterator<?> iterator = mapper.readerFor(entityClass).with(schema).readValues(extractedFile);

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // *** RESET TABLE BEFORE INSERTION ***
                        resetTableForEntity(entityClass);
                        // Update UI to show table is being reset
                        runOnUiThread(() -> {
                            textView_Sync.setText("Clearing existing " + files + " data...");
                        });

                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 20000;
                        int batchCount = 0;

                        // Update UI to show processing is starting
                        runOnUiThread(() -> {
                            textView_Sync.setText("Processing " + files + " data...");
                        });

                        while (iterator.hasNext()) {
                            Object entity = iterator.next();
                            if (entity != null) {
                                entities.add(entity);
                                batchCount++;

                                if (batchCount == batchSize) {
                                    insertEntitiesIntoDatabase(entities, entityClass);
                                    entities.clear();
                                    batchCount = 0;
                                }

                                updateProgress(counts, totalRecords, entityClass, files);
                            }
                        }

                        // Insert remaining entities
                        if (batchCount > 0) {
                            insertEntitiesIntoDatabase(entities, entityClass);
                        }

                        // Save counts to shared preferences after processing
                        saveCountsToSharedPreferences(files, counts.get());

                        runOnUiThread(() -> {
                            textView_Sync.setText("Successful Download ");
                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                            nextStep.run();

                            // Update the synchronization date
                            final AtomicReference<String> EventsDatetime = new AtomicReference<>(getIndSyncDatetime());

                                            // Inside the synchronization process after a successful sync
                                            // Only change the date if there is another sync
                            String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                             if (!currentDateWithTime.equals(EventsDatetime.get())) {
                              syncDateTextView.setText(currentDateWithTime);
                              EventsDatetime.set(currentDateWithTime);
                              setLastIndDatetime(currentDateWithTime);
                              }
                        });

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textView_Sync.setText("Error processing file. Please try again.");
                    });
                }
            }

            // *** RESET TABLE METHOD ***
            private void resetTableForEntity(Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Individual.class)) {
                    // Clear all data from Individual table
                    individualDao.deleteAll(); // You'll need to add this method to your DAO
                } else if (entityClass.equals(Residency.class)) {
                    // Clear all data from residency table
                    residencyDao.deleteAll(); // You'll need to add this method to your DAO
                }
            }

            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Individual.class)) {
                    return CsvSchema.emptySchema().withHeader();
                } else if (entityClass.equals(Residency.class)) {
                    return CsvSchema.emptySchema().withHeader();
                }
                return CsvSchema.emptySchema();
            }

            private long countTotalRecords(File csvFile) {
                // Count total records in the CSV
                try {
                    CsvMapper countMapper = new CsvMapper();
                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();
                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                    Iterator<Map<String, String>> countIterator = countReader.readValues(csvFile);
                    long totalRecords = 0;
                    while (countIterator.hasNext()) {
                        countIterator.next();
                        totalRecords++;
                    }
                    return totalRecords;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            private void insertEntitiesIntoDatabase(List<Object> entities, Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Individual.class)) {
                    individualDao.insert((Individual[]) entities.toArray(new Individual[0]));
                } else if (entityClass.equals(Residency.class)) {
                    residencyDao.insert((Residency[]) entities.toArray(new Residency[0]));
                }
            }

            private void updateProgress(AtomicLong counts, long[] totalRecords, Class<?> entityClass, String files) {
                long currentCount = counts.incrementAndGet();
                int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                int totalRecordsCount = (int) totalRecords[0];
                int cnt = (int) currentCount;
                if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Saving " + progress + "% of " + files);
                        progres.setTitle("Saving " + files + " Dataset");
                        progres.setMax(totalRecordsCount);
                        progressBar.setProgress(progress);
                        progres.setProgress(cnt);
                        progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
                        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        // Ensure ProgressDialog is visible and updated
                        if (progres.isIndeterminate()) {
                            progres.setIndeterminate(false);
                        }
                    });
                }
            }

            private void resetAllCounts() {
                resetCountOnClick(ind);
                resetCountOnClick(res);
            }
        });


        //Download Demographic
        String DemoDatetime = getLastDemoDatetime();
        if (DemoDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            DemoDatetime = sdf.format(new Date());
            setLastDemoDatetime(DemoDatetime);
        }
        final TextView syncdDateTextView = findViewById(R.id.syncDemoDate);
        syncdDateTextView.setText(DemoDatetime);
        // Assuming this is inside your Activity class

        final Button button_demo = findViewById(R.id.syncDemo);
        button_demo.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncDemoMessage);
            final ProgressBar progressBar = findViewById(R.id.DemoProgressBar);
            AtomicLong demographicsCounts = new AtomicLong();
            String dem = "Demographic";

            @Override
            public void onClick(View v) {
                resetAndShowProgress("Starting Sync...", "Syncing data"); // Show immediately
                new Thread(() -> {
                    // Perform heavy work in background
                    resetAllCounts();
                    checkAndProcessDemographic();
                }).start();
            }

            private void checkAndProcessDemographic() {
                if (demographicsZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Demographics file exists. Extracting and processing..."));

                    extractAndProcessFile("demographics.zip", "demographics.csv", Demographic.class, demographicsCounts, dem, () -> {
                        runOnUiThread(() -> {
                            if (progres != null && progres.isShowing()) {
                                progres.dismiss();
                            }
                        });
                    });
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Demographics file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }


            private void extractAndProcessFile(String zipFileName, String extractedFileName, Class<?> entityClass, AtomicLong countKey, String files, Runnable nextStep) {
                // Extract the zip file and process it
                File zipFile = new File(getExternalCacheDir(), zipFileName);
                File extractedFile = new File(getExternalCacheDir(), extractedFileName);
                try {
                    // Unzip the file
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        if (fileName.equals(extractedFileName)) {
                            FileOutputStream fos = new FileOutputStream(extractedFile);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            break;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();

                    // If the extracted CSV file is empty, skip processing and move to the next step
                    if (extractedFile.length() == 0) {
                        nextStep.run();
                        return;
                    }

                    // Process the CSV file and insert data into the database
                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                    CsvMapper mapper = new CsvMapper();
                    CsvSchema schema = getCsvSchemaForEntity(entityClass);
                    MappingIterator<?> iterator = mapper.readerFor(entityClass).with(schema).readValues(extractedFile);

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // *** RESET TABLE BEFORE INSERTION ***
                        resetTableForEntity(entityClass);
                        // Update UI to show table is being reset
                        runOnUiThread(() -> {
                            textView_Sync.setText("Clearing existing " + files + " data...");
                        });

                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 20000;
                        int batchCount = 0;

                        // Update UI to show processing is starting
                        runOnUiThread(() -> {
                            textView_Sync.setText("Processing " + files + " data...");
                        });

                        while (iterator.hasNext()) {
                            Object entity = iterator.next();
                            if (entity != null) {
                                entities.add(entity);
                                batchCount++;

                                if (batchCount == batchSize) {
                                    insertEntitiesIntoDatabase(entities, entityClass);
                                    entities.clear();
                                    batchCount = 0;
                                }

                                updateProgress(counts, totalRecords, entityClass, files);
                            }
                        }

                        // Insert remaining entities
                        if (batchCount > 0) {
                            insertEntitiesIntoDatabase(entities, entityClass);
                        }

                        // Save counts to shared preferences after processing
                        saveCountsToSharedPreferences(files, counts.get());

                        runOnUiThread(() -> {
                            textView_Sync.setText("Successful Download ");
                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                            nextStep.run();

                            // Update the synchronization date
                            final AtomicReference<String> DemoDatetime = new AtomicReference<>(getLastDemoDatetime());

                            // Inside the synchronization process after a successful sync
                            // Only change the date if there is another sync
                            String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                            if (!currentDateWithTime.equals(DemoDatetime.get())) {
                                syncdDateTextView.setText(currentDateWithTime);
                                DemoDatetime.set(currentDateWithTime);
                                setLastDemoDatetime(currentDateWithTime);
                            }
                        });

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textView_Sync.setText("Error processing file. Please try again.");
                    });
                }
            }

            // *** RESET TABLE METHOD ***
            private void resetTableForEntity(Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Demographic.class)) {
                    // Clear all data from Demographic table
                    demographicDao.deleteAll(); // You'll need to add this method to your DAO
                }
            }

            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Demographic.class)) {
                    return CsvSchema.emptySchema().withHeader();
                }
                return CsvSchema.emptySchema();
            }

            private long countTotalRecords(File csvFile) {
                // Count total records in the CSV
                try {
                    CsvMapper countMapper = new CsvMapper();
                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();
                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                    Iterator<Map<String, String>> countIterator = countReader.readValues(csvFile);
                    long totalRecords = 0;
                    while (countIterator.hasNext()) {
                        countIterator.next();
                        totalRecords++;
                    }
                    return totalRecords;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            private void insertEntitiesIntoDatabase(List<Object> entities, Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Demographic.class)) {
                    demographicDao.insert((Demographic[]) entities.toArray(new Demographic[0]));
                }
            }

            private void updateProgress(AtomicLong counts, long[] totalRecords, Class<?> entityClass, String files) {
                long currentCount = counts.incrementAndGet();
                int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                int totalRecordsCount = (int) totalRecords[0];
                int cnt = (int) currentCount;
                if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Saving " + progress + "% of " + files);
                        progres.setTitle("Saving " + files + " Dataset");
                        progressBar.setProgress(progress);
                        progres.setMax(totalRecordsCount);
                        progres.setProgress(cnt);
                        progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
                        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        // Ensure ProgressDialog is visible and updated
                        if (progres.isIndeterminate()) {
                            progres.setIndeterminate(false);
                        }
                    });
                }
            }

            private void resetAllCounts() {
                resetCountOnClick(dem);
            }
        });


        //Sync Pregnancy and Relationship
        String EventsDatetime = getEventsSyncDatetime();
        if (EventsDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            EventsDatetime = sdf.format(new Date());
            setLastEventsDatetime(EventsDatetime);
        }
        final TextView synceDateTextView = findViewById(R.id.syncEventsDate);
        synceDateTextView.setText(EventsDatetime);
        // Assuming this is inside your Activity class

        final Button button_Events = findViewById(R.id.syncEvents);
        button_Events.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncEventsMessage);
            final ProgressBar progressBar = findViewById(R.id.EventsProgressBar);
            AtomicLong pregnancyCounts = new AtomicLong();
            AtomicLong relationshipCounts = new AtomicLong();
            String preg = "Pregnancy";
            String rel = "Relationship";

            @Override
            public void onClick(View v) {
                resetAndShowProgress("Starting Sync...", "Syncing data"); // Show immediately
                new Thread(() -> {
                    // Perform heavy work in background
                    resetAllCounts();
                    checkAndProcessPregnancy();
                }).start();
            }

            private void checkAndProcessPregnancy() {
                if (pregnancyZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Pregnancy file exists. Extracting and processing..."));

                    extractAndProcessFile("pregnancy.zip", "pregnancy.csv", Pregnancy.class, pregnancyCounts, preg, this::checkAndProcessRelationship);
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Pregnancy file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }

            private void checkAndProcessRelationship() {
                if (relationshipZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Relationship file exists. Extracting and processing..."));

                    extractAndProcessFile("relationship.zip", "relationship.csv", Relationship.class, relationshipCounts, rel, () -> {
                        runOnUiThread(() -> {
                            if (progres != null && progres.isShowing()) {
                                progres.dismiss();
                            }
                        });
                    });
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Relationship file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }


            private void extractAndProcessFile(String zipFileName, String extractedFileName, Class<?> entityClass, AtomicLong countKey, String files, Runnable nextStep) {
                // Extract the zip file and process it
                File zipFile = new File(getExternalCacheDir(), zipFileName);
                File extractedFile = new File(getExternalCacheDir(), extractedFileName);
                try {
                    // Unzip the file
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        if (fileName.equals(extractedFileName)) {
                            FileOutputStream fos = new FileOutputStream(extractedFile);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            break;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();

                    // If the extracted CSV file is empty, skip processing and move to the next step
                    if (extractedFile.length() == 0) {
                        nextStep.run();
                        return;
                    }

                    // Process the CSV file and insert data into the database
                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                    CsvMapper mapper = new CsvMapper();
                    CsvSchema schema = getCsvSchemaForEntity(entityClass);
                    MappingIterator<?> iterator = mapper.readerFor(entityClass).with(schema).readValues(extractedFile);

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // *** RESET TABLE BEFORE INSERTION ***
                        resetTableForEntity(entityClass);
                        // Update UI to show table is being reset
                        runOnUiThread(() -> {
                            textView_Sync.setText("Clearing existing " + files + " data...");
                        });

                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 20000;
                        int batchCount = 0;

                        // Update UI to show processing is starting
                        runOnUiThread(() -> {
                            textView_Sync.setText("Processing " + files + " data...");
                        });

                        while (iterator.hasNext()) {
                            Object entity = iterator.next();
                            if (entity != null) {
                                entities.add(entity);
                                batchCount++;

                                if (batchCount == batchSize) {
                                    insertEntitiesIntoDatabase(entities, entityClass);
                                    entities.clear();
                                    batchCount = 0;
                                }

                                updateProgress(counts, totalRecords, entityClass, files);
                            }
                        }

                        // Insert remaining entities
                        if (batchCount > 0) {
                            insertEntitiesIntoDatabase(entities, entityClass);
                        }

                        // Save counts to shared preferences after processing
                        saveCountsToSharedPreferences(files, counts.get());

                        runOnUiThread(() -> {
                            textView_Sync.setText("Successful Download ");
                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                            nextStep.run();

                            // Update the synchronization date
                            final AtomicReference<String> EventsDatetime = new AtomicReference<>(getEventsSyncDatetime());

                            // Inside the synchronization process after a successful sync
                            // Only change the date if there is another sync
                            String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                            if (!currentDateWithTime.equals(EventsDatetime.get())) {
                                synceDateTextView.setText(currentDateWithTime);
                                EventsDatetime.set(currentDateWithTime);
                                setLastEventsDatetime(currentDateWithTime);
                            }
                        });

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textView_Sync.setText("Error processing file. Please try again.");
                    });
                }
            }

            // *** RESET TABLE METHOD ***
            private void resetTableForEntity(Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Pregnancy.class)) {
                    // Clear all data from Pregnancy table
                    pregnancyDao.deleteAll(); // You'll need to add this method to your DAO
                } else if (entityClass.equals(Relationship.class)) {
                    // Clear all data from Relationship table
                    relationshipDao.deleteAll(); // You'll need to add this method to your DAO
                }
            }

            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Pregnancy.class)) {
                    return CsvSchema.emptySchema().withHeader();
                } else if (entityClass.equals(Relationship.class)) {
                    return CsvSchema.emptySchema().withHeader();
                }
                return CsvSchema.emptySchema();
            }

            private long countTotalRecords(File csvFile) {
                // Count total records in the CSV
                try {
                    CsvMapper countMapper = new CsvMapper();
                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();
                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                    Iterator<Map<String, String>> countIterator = countReader.readValues(csvFile);
                    long totalRecords = 0;
                    while (countIterator.hasNext()) {
                        countIterator.next();
                        totalRecords++;
                    }
                    return totalRecords;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            private void insertEntitiesIntoDatabase(List<Object> entities, Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(Pregnancy.class)) {
                    pregnancyDao.insert((Pregnancy[]) entities.toArray(new Pregnancy[0]));
                } else if (entityClass.equals(Relationship.class)) {
                    relationshipDao.insert((Relationship[]) entities.toArray(new Relationship[0]));
                }
            }

            private void updateProgress(AtomicLong counts, long[] totalRecords, Class<?> entityClass, String files) {
                long currentCount = counts.incrementAndGet();
                int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                int totalRecordsCount = (int) totalRecords[0];
                int cnt = (int) currentCount;
                if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Saving " + progress + "% of " + files);
                        progres.setTitle("Saving " + files + " Dataset");
                        progressBar.setProgress(progress);
                        progres.setMax(totalRecordsCount);
                        progres.setProgress(cnt);
                        progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
                        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        // Ensure ProgressDialog is visible and updated
                        if (progres.isIndeterminate()) {
                            progres.setIndeterminate(false);
                        }
                    });
                }
            }

            private void resetAllCounts() {
                resetCountOnClick(preg);
                resetCountOnClick(rel);
            }
        });

        //Vaccination & SES
        String SesDatetime = getSesSyncDatetime();
        if (SesDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            SesDatetime = sdf.format(new Date());
            setLastSesDatetime(SesDatetime);
        }
        final TextView syncsDate = findViewById(R.id.syncsesDate);
        syncsDate.setText(SesDatetime);
        // Assuming this is inside your Activity class

        final Button button_Oth = findViewById(R.id.syncSes);
        button_Oth.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncsesMessage);
            final ProgressBar progressBar = findViewById(R.id.sesProgressBar);
            AtomicLong sesCounts = new AtomicLong();
            AtomicLong vaccinationCounts = new AtomicLong();

            String ses = "SES";
            String vac = "Vaccination";

            @Override
            public void onClick(View v) {
                resetAndShowProgress("Starting Sync...", "Syncing data"); // Show immediately
                new Thread(() -> {
                    // Perform heavy work in background
                    resetAllCounts();
                    checkAndProcessSes();
                }).start();
            }

            private void checkAndProcessSes() {
                if (sesZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("SES file exists. Extracting and processing..."));

                    extractAndProcessFile("ses.zip", "ses.csv", HdssSociodemo.class, sesCounts, ses, this::checkAndProcessVac);
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("SES file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }

            private void checkAndProcessVac() {
                if (vaccinationZipFile.exists()) {
                    runOnUiThread(() -> textView_Sync.setText("Vaccination file exists. Extracting and processing..."));

                    extractAndProcessFile("vaccination.zip", "vaccination.csv", Vaccination.class, vaccinationCounts, vac, () -> {
                        runOnUiThread(() -> {
                            if (progres != null && progres.isShowing()) {
                                progres.dismiss();
                            }
                            // Optionally: deleteFiles(getExternalCacheDir());
                        });
                    });
                } else {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Vaccination file not downloaded. Download Zip Data");
                        textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                        // Dismiss progress dialog if file is not found
                        if (progres != null && progres.isShowing()) {
                            progres.dismiss();
                        }
                    });
                }
            }


            private void extractAndProcessFile(String zipFileName, String extractedFileName, Class<?> entityClass, AtomicLong countKey, String files, Runnable nextStep) {
                // Extract the zip file and process it
                File zipFile = new File(getExternalCacheDir(), zipFileName);
                File extractedFile = new File(getExternalCacheDir(), extractedFileName);
                try {
                    // Unzip the file
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        if (fileName.equals(extractedFileName)) {
                            FileOutputStream fos = new FileOutputStream(extractedFile);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            break;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();

                    // If the extracted CSV file is empty, skip processing and move to the next step
                    if (extractedFile.length() == 0) {
                        nextStep.run();
                        return;
                    }

                    // Process the CSV file and insert data into the database
                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                    CsvMapper mapper = new CsvMapper();
                    CsvSchema schema = getCsvSchemaForEntity(entityClass);
                    MappingIterator<?> iterator = mapper.readerFor(entityClass).with(schema).readValues(extractedFile);

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // *** RESET TABLE BEFORE INSERTION ***
                        resetTableForEntity(entityClass);
                        // Update UI to show table is being reset
                        runOnUiThread(() -> {
                            textView_Sync.setText("Clearing existing " + files + " data...");
                        });

                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 20000;
                        int batchCount = 0;

                        // Update UI to show processing is starting
                        runOnUiThread(() -> {
                            textView_Sync.setText("Processing " + files + " data...");
                        });

                        while (iterator.hasNext()) {
                            Object entity = iterator.next();
                            if (entity != null) {
                                entities.add(entity);
                                batchCount++;

                                if (batchCount == batchSize) {
                                    insertEntitiesIntoDatabase(entities, entityClass);
                                    entities.clear();
                                    batchCount = 0;
                                }

                                updateProgress(counts, totalRecords, entityClass, files);
                            }
                        }

                        // Insert remaining entities
                        if (batchCount > 0) {
                            insertEntitiesIntoDatabase(entities, entityClass);
                        }

                        // Save counts to shared preferences after processing
                        saveCountsToSharedPreferences(files, counts.get());

                        runOnUiThread(() -> {
                            textView_Sync.setText("Successful Download ");
                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                            nextStep.run();

                            // Update the synchronization date
                            final AtomicReference<String> SesDatetime = new AtomicReference<>(getSesSyncDatetime());

                            // Inside the synchronization process after a successful sync
                            // Only change the date if there is another sync
                            String currentDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                            if (!currentDateTime.equals(SesDatetime.get())) {
                                syncsDate.setText(currentDateTime);
                                SesDatetime.set(currentDateTime);
                                setLastSesDatetime(currentDateTime);
                            }
                        });

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textView_Sync.setText("Error processing file. Please try again.");
                    });
                }
            }

            // *** RESET TABLE METHOD ***
            private void resetTableForEntity(Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(HdssSociodemo.class)) {
                    // Clear all data from SES table
                    hdssSociodemoDao.deleteAll(); // You'll need to add this method to your DAO
                } else if (entityClass.equals(Vaccination.class)) {
                    // Clear all data from Vaccination table
                    vaccinationDao.deleteAll(); // You'll need to add this method to your DAO
                }
            }

            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(HdssSociodemo.class)) {
                    return CsvSchema.emptySchema().withHeader();
                } else if (entityClass.equals(Vaccination.class)) {
                    return CsvSchema.emptySchema().withHeader();
                }
                return CsvSchema.emptySchema();
            }

            private long countTotalRecords(File csvFile) {
                // Count total records in the CSV
                try {
                    CsvMapper countMapper = new CsvMapper();
                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();
                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                    Iterator<Map<String, String>> countIterator = countReader.readValues(csvFile);
                    long totalRecords = 0;
                    while (countIterator.hasNext()) {
                        countIterator.next();
                        totalRecords++;
                    }
                    return totalRecords;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            private void insertEntitiesIntoDatabase(List<Object> entities, Class<?> entityClass) {
                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                if (entityClass.equals(HdssSociodemo.class)) {
                    hdssSociodemoDao.insert((HdssSociodemo[]) entities.toArray(new HdssSociodemo[0]));
                } else if (entityClass.equals(Vaccination.class)) {
                    vaccinationDao.insert((Vaccination[]) entities.toArray(new Vaccination[0]));
                }
            }

            private void updateProgress(AtomicLong counts, long[] totalRecords, Class<?> entityClass, String files) {
                long currentCount = counts.incrementAndGet();
                int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                int totalRecordsCount = (int) totalRecords[0];
                int cnt = (int) currentCount;
                if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                    runOnUiThread(() -> {
                        textView_Sync.setText("Saving " + progress + "% of " + files);
                        progres.setTitle("Saving " + files + " Dataset");
                        progressBar.setProgress(progress);
                        progres.setMax(totalRecordsCount);
                        progres.setProgress(cnt);
                        progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
                        progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        // Ensure ProgressDialog is visible and updated
                        if (progres.isIndeterminate()) {
                            progres.setIndeterminate(false);
                        }
                    });
                }
            }

            private void resetAllCounts() {
                resetCountOnClick(ses);
                resetCountOnClick(vac);
            }
        });



    }

    public long getCountFromSharedPreferences(String entityName) {
//                        progress.dismiss();
//                        textView_SyncVac.setText("Error while unzipping or inserting data.");
//                        textView_SyncVac.setTextColor(Color.RED);
        SharedPreferences sharedPreferences = getSharedPreferences("entity_counts", MODE_PRIVATE);
        String countKey = getEntityKey(entityName);
        return sharedPreferences.getLong(countKey, 0);
    }

    public void saveCountsToSharedPreferences(String entityName, long count) {
        SharedPreferences sharedPreferences = getSharedPreferences("entity_counts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String countKey = getEntityKey(entityName);
        editor.putLong(countKey, count);
        editor.apply();
    }

    private String getEntityKey(String entityName) {
        return entityName.toLowerCase() + "_count";
    }

    // Add this method to your class
    public void resetCountOnClick(String entityName) {
        // Set the count to 0
        saveCountsToSharedPreferences(entityName, 0);
    }


    public void AppInfo(View view) {
        showDialogInfo(null, DOWNLOAD_SES);
    }

    public void AppIndInfo(View view) {
        showDialogInfo(null, DOWNLOAD_IND);
    }

    public void AppOthInfo(View view) {
        showDialogInfo(null, DOWNLOAD_DEMO);
    }

    //Delete All Files
    private void deleteFiles(File directory) {
        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            Log.d("FileDeletion", "Deleted file: " + fileName);
                            Toast.makeText(PullActivity.this, "Congratulations on Successful Download ", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("FileDeletion", "Failed to delete file: " + fileName);
                        }
                    }
                }
            } else {
                Log.e("FileDeletion", "No files found in directory: " + directory.getAbsolutePath());
            }
        } else {
            Log.e("FileDeletion", "Invalid directory: " + directory.getAbsolutePath());
        }
    }

    private void resetAndShowProgress(String title, String message) {
        runOnUiThread(() -> {
            try {
                // Avoid showing if activity is finishing or destroyed
                if (isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed())) {
                    return;
                }

                if (progres != null && progres.isShowing()) {
                    progres.dismiss();
                }

                if (progres == null) {
                    progres = new ProgressDialog(this);
                    progres.setCancelable(false);
                    progres.setIndeterminate(true);
                }

                progres.setTitle(title);
                progres.setMessage(message);
                progres.setProgress(0);
                progres.setMax(0);

                // Again check activity state before showing
                if (!isFinishing() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !isDestroyed())) {
                    progres.show();
                }
            } catch (Exception e) {
                e.printStackTrace(); // For logging; replace with Log.e if needed
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (progres != null && progres.isShowing()) {
            progres.dismiss();
            progres = null;
        }
        super.onDestroy();
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            PullActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


}