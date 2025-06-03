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
import org.openhds.hdsscapture.Dao.OdkDao;
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
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
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
    private OdkDao odkDao;
    AppDatabase appDatabase;
    private Button downloadAllButton;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private AppCompatButton complete;

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
                                                    final OdkViewModel odkViewModel = new ViewModelProvider(PullActivity.this).get(OdkViewModel.class);
                                                    Call<DataWrapper<OdkForm>> c_callable = dao.getOdk(authorizationHeader);
                                                    c_callable.enqueue(new Callback<DataWrapper<OdkForm>>() {
                                                    @Override
                                                    public void onResponse(Call<DataWrapper<OdkForm>> call, Response<DataWrapper<OdkForm>> response) {
                                                    OdkForm[] odk = response.body().getData().toArray(new OdkForm[0]);
                                                    odkViewModel.add(odk);

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
                // Check if the location.zip file exists
                if (locationZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Location file exists. Extracting and processing...");
                    extractAndProcessFile("location.zip", "location.csv", Locations.class, locationCounts, loc, this::checkAndProcessSocialgroup);
                } else {
                    // File does not exist
                    textView_Sync.setText("Location file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            private void checkAndProcessSocialgroup() {
                // Check if the socialgroup.zip file exists
                if (socialgroupZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Socialgroup file exists. Extracting and processing...");
                    extractAndProcessFile("socialgroup.zip", "socialgroup.csv", Socialgroup.class, socialgroupCounts, soc, () -> {
                        // All datasets processed, perform any final actions here
                        //deleteFiles(getExternalCacheDir());
                        progres.dismiss();
                    });
                } else {
                    // File does not exist
                    textView_Sync.setText("Socialgroup file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
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
                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 10000;
                        int batchCount = 0;

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
            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Locations.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("accuracy").addColumn("compextId").addColumn("compno").addColumn("edtime")
                            .addColumn("fw_uuid").addColumn("insertDate").addColumn("latitude").addColumn("locationLevel_uuid").addColumn("locationName")
                            .addColumn("locationType").addColumn("longitude").addColumn("status").addColumn("sttime").addColumn("vill_extId").addColumn("altitude").build();
                } else if (entityClass.equals(Socialgroup.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("extId").addColumn("fw_uuid").addColumn("groupName")
                            .addColumn("groupType").addColumn("insertDate")
                            .addColumn("individual_uuid").build();
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
                // Check if the individual.zip file exists
                if (individualZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Individual file exists. Extracting and processing...");
                    extractAndProcessFile("individual.zip", "individual.csv", Individual.class, individualCounts, ind, this::checkAndProcessResidency);
                } else {
                    // File does not exist
                    textView_Sync.setText("Individual file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            private void checkAndProcessResidency() {
                // Check if the residency.zip file exists
                if (residencyZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Residency file exists. Extracting and processing...");
                    extractAndProcessFile("residency.zip", "residency.csv", Residency.class, residencyCounts, res, () -> {
                      progres.dismiss();
                    });
                } else {
                    // File does not exist
                    textView_Sync.setText("Residency file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
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
                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 10000;
                        int batchCount = 0;

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
            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Individual.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("dob").addColumn("dobAspect").addColumn("edtime").addColumn("extId")
                            .addColumn("firstName").addColumn("fw_uuid").addColumn("gender").addColumn("ghanacard").addColumn("insertDate")
                            .addColumn("lastName").addColumn("otherName").addColumn("father_uuid").addColumn("mother_uuid")
                            .addColumn("sttime").addColumn("endType").addColumn("compno")
                            .addColumn("village").addColumn("hohID").addColumn("phone1").build();
                } else if (entityClass.equals(Residency.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("edtime").addColumn("endDate").addColumn("endType")
                            .addColumn("fw_uuid").addColumn("insertDate").addColumn("rltn_head")
                            .addColumn("startDate").addColumn("startType")
                            .addColumn("individual_uuid").addColumn("location_uuid").addColumn("socialgroup_uuid")
                            .addColumn("sttime").build();
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


//        //Sync Individual and Residency
//        String IndSyncDatetime = getIndSyncDatetime();
//        if (IndSyncDatetime.isEmpty()) {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
//            IndSyncDatetime = sdf.format(new Date());
//            setLastIndDatetime(IndSyncDatetime);
//        }
//        final TextView syncDateTextView = findViewById(R.id.syncIndividualDate);
//        syncDateTextView.setText(IndSyncDatetime);
//        // Assuming this is inside your Activity class
//
//        final Button button_SyncAll = findViewById(R.id.syncIndividual);
//        button_SyncAll.setOnClickListener(new View.OnClickListener() {
//            final TextView textView_Sync = findViewById(R.id.syncIndividualMessage);
//            final ProgressBar progressBar = findViewById(R.id.IndividualProgressBar);
//            AtomicLong individualCounts = new AtomicLong();
//            AtomicLong residencyCounts = new AtomicLong();
//            String ind = "Individual";
//            String res = "Residency";
//
//            @Override
//            public void onClick(View v) {
//                resetAllCounts();
//                downloadAndProcessIndividual();
//            }
//
//            private void downloadAndProcessIndividual() {
//                // Download and process "individuals" dataset
//                CsvSchema individualsSchema = CsvSchema.builder()
//                        .addColumn("uuid").addColumn("dob").addColumn("dobAspect").addColumn("edtime").addColumn("extId")
//                        .addColumn("firstName").addColumn("fw_uuid").addColumn("gender").addColumn("ghanacard").addColumn("insertDate")
//                        .addColumn("lastName").addColumn("otherName").addColumn("father_uuid").addColumn("mother_uuid")
//                        .addColumn("sttime").addColumn("endType").addColumn("compno")
//                        .addColumn("village").addColumn("hohID").addColumn("phone1").build();
//
//                downloadAndProcessDataset("individual.zip", "individual.csv", () -> dao.downloadIndividual(authorizationHeader), Individual.class, individualsSchema, individualCounts, ind, this::downloadAndProcessResidency);
//            }
//
//            private void downloadAndProcessResidency() {
//                // Download and process "residency" dataset
//                CsvSchema residencySchema = CsvSchema.builder()
//                        .addColumn("uuid").addColumn("edtime").addColumn("endDate").addColumn("endType")
//                        .addColumn("fw_uuid").addColumn("insertDate").addColumn("rltn_head")
//                        .addColumn("startDate").addColumn("startType")
//                        .addColumn("individual_uuid").addColumn("location_uuid").addColumn("socialgroup_uuid")
//                        .addColumn("sttime").build();
//
//                downloadAndProcessDataset("residency.zip", "residency.csv", () -> dao.downloadResidency(authorizationHeader), Residency.class, residencySchema, residencyCounts, res, () -> {
//                    // All datasets processed, perform any final actions here
//                    //deleteFiles(getExternalCacheDir());
//                });
//            }
//
//            private void resetAllCounts() {
//                resetCountOnClick(ind);
//                resetCountOnClick(res);
//            }
//
//            private <T> void downloadAndProcessDataset(String zipFileName, String extractedFileName, Supplier<Call<ResponseBody>> downloadCallSupplier, Class<T> entityClass, CsvSchema schema, AtomicLong countKey, String files, Runnable nextStep) {
//                textView_Sync.setText("Downloading " + files + " Dataset");
//                progres.setTitle("Downloading " + files + " Dataset");
//                progres.show();
//                progres.setProgress(0);
//                progres.setMax(0);
//                progressBar.setProgress(0);
//
//                // File path for the downloaded file
//                File file = new File(getExternalCacheDir(), zipFileName);
//                Call<ResponseBody> call = downloadCallSupplier.get();
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        //progress.show();
//                        if (response.isSuccessful()) {
//                            try {
//                                // Read the response body into a file
//                                InputStream inputStream = response.body().byteStream();
//                                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                                byte[] buffer = new byte[1024];
//                                int read;
//                                while ((read = inputStream.read(buffer)) != -1) {
//                                    fileOutputStream.write(buffer, 0, read);
//                                }
//                                fileOutputStream.close();
//
//                                // Unzip the file
//                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
//                                ZipEntry zipEntry = zipInputStream.getNextEntry();
//                                while (zipEntry != null) {
//                                    String fileName = zipEntry.getName();
//                                    if (fileName.equals(extractedFileName)) {
//                                        File newFile = new File(getExternalCacheDir() + File.separator + fileName);
//                                        FileOutputStream fos = new FileOutputStream(newFile);
//                                        int len;
//                                        while ((len = zipInputStream.read(buffer)) > 0) {
//                                            fos.write(buffer, 0, len);
//                                        }
//                                        fos.close();
//                                        // Check if the extracted CSV file is empty
//                                        if (newFile.length() == 0) {
//                                            // CSV file is empty, skip processing and move to the next step
//                                            nextStep.run();
//                                            return;
//                                        }
//                                        break;
//                                    }
//                                    zipEntry = zipInputStream.getNextEntry();
//                                }
//                                zipInputStream.close();
//
//                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
//
//                                // Use the appropriate DAO based on the entity class
//                                if (entityClass.equals(Individual.class)) {
//                                    individualDao = appDatabase.individualDao();
//                                } else if (entityClass.equals(Residency.class)) {
//                                    residencyDao = appDatabase.residencyDao();
//                                }
//
//                                // Import the unzipped CSV file into the Room database
//                                if (individualDao != null || residencyDao != null) {
//                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
//                                    CsvMapper mapper = new CsvMapper();
//                                    MappingIterator<T> iterator = mapper.readerFor(entityClass).with(schema).readValues(unzippedFile);
//                                    progres.show();
//                                    progressBar.setProgress(0);
//                                    long[] totalRecords = new long[1];
//                                    CsvMapper countMapper = new CsvMapper();
//                                    //CsvSchema countSchema = CsvSchema.emptySchema();  // No header since CSV has no header
//                                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();  // Assuming your CSV has a header
//                                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
//                                    Iterator<Map<String, String>> countIterator = countReader.readValues(unzippedFile);
//
//                                    totalRecords[0] = 0;
//                                    while (countIterator.hasNext()) {
//                                        countIterator.next();
//                                        totalRecords[0]++;
//                                    }
//
//                                    AtomicLong counts = new AtomicLong();
//                                    AppDatabase.databaseWriteExecutor.execute(() -> {
//                                        int batchSize = 10000;
//                                        List<T> entities = new ArrayList<>();
//                                        int batchCount = 0;
//                                        while (iterator.hasNext()) {
//                                            T entity = iterator.next();
//                                            if (entity != null) {
//                                                entities.add(entity);
//                                                batchCount++;
//
//                                                if (batchCount == batchSize) {
//                                                    if (entityClass.equals(Individual.class)) {
//                                                        individualDao.insert((Individual[]) entities.toArray(new Individual[0]));
//                                                        //individualDao.insert((List<Individual>) entities);
//                                                    } else if (entityClass.equals(Residency.class)) {
//                                                        //residencyDao.insert((List<Residency>) entities);
//                                                        residencyDao.insert((Residency[]) entities.toArray(new Residency[0]));
//                                                    }
//                                                    entities.clear();
//                                                    batchCount = 0;
//                                                }
//
//                                                runOnUiThread(() -> {
//                                                    long currentCount = counts.incrementAndGet();
//                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
//                                                    int totalRecordsCount = (int) totalRecords[0];
//                                                    int cnt = (int) currentCount;
//                                                    // Update UI every 500 records or when the task is complete
//                                                    if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
//                                                        runOnUiThread(() -> {
//                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
//                                                            progressBar.setProgress(progress);
//                                                            progres.setMax(totalRecordsCount);
//                                                            progres.setTitle("Downloading " + files + " Dataset");
//                                                            progres.setProgress(cnt); // Set progress based on current count
//
//                                                            // Assuming 'progres' is a ProgressDialog
//                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);
//
//                                                            // Ensure the style is set correctly
//                                                            progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//
//                                                            // If ProgressDialog is indeterminate, set it to false
//                                                            if (progres.isIndeterminate()) {
//                                                                progres.setIndeterminate(false);
//                                                            }
//                                                        });
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        if (batchCount > 0) {
//                                            if (entityClass.equals(Individual.class)) {
//                                                individualDao.insert((Individual[]) entities.toArray(new Individual[0]));
//                                                individualDao.insert((List<Individual>) entities);
//                                            } else if (entityClass.equals(Residency.class)) {
//                                                residencyDao.insert((Residency[]) entities.toArray(new Residency[0]));
//                                            }
//                                        }
//                                        runOnUiThread(() -> {
//                                            long finalCount = counts.get();
//                                            int finalProgress = (int) (((double) finalCount / totalRecords[0]) * 100);
//                                            textView_Sync.setText("Successful Download ");
//                                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
//                                            progressBar.setProgress(finalProgress);
//                                            progres.dismiss();
//                                            //Log.d("Three", "Location Count: " + files + " " + finalCount);
//                                            saveCountsToSharedPreferences(files, finalCount);
//
//                                            // Execute the next step
//                                            nextStep.run();
//
//                                            // Update the synchronization date
//                                            final AtomicReference<String> EventsDatetime = new AtomicReference<>(getIndSyncDatetime());
//
//                                            // Inside the synchronization process after a successful sync
//                                            // Only change the date if there is another sync
//                                            String currentDateWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//                                            if (!currentDateWithTime.equals(EventsDatetime.get())) {
//                                                syncDateTextView.setText(currentDateWithTime);
//                                                EventsDatetime.set(currentDateWithTime);
//                                                setLastIndDatetime(currentDateWithTime);
//                                            }
//                                        });
//                                    });
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        // Show error message
//                        textView_Sync.setText("Download Error! Retry or Contact Administrator");
//                        progressBar.setProgress(0);
//                        progres.dismiss();
//                        textView_Sync.setTextColor(Color.RED);
//                    }
//                });
//            }
//        });

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
                // Check if the Relationship.zip file exists
                if (demographicsZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Demographics file exists. Extracting and processing...");
                    extractAndProcessFile("demographics.zip", "demographics.csv", Demographic.class, demographicsCounts, dem, () -> {
                        // All datasets processed, perform any final actions here
                        //deleteFiles(getExternalCacheDir());
                        progres.dismiss();
                    });
                } else {
                    // File does not exist
                    textView_Sync.setText("Demographics file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
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
                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 10000;
                        int batchCount = 0;

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
            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Demographic.class)) {
                    return CsvSchema.builder()
                            .addColumn("individual_uuid").addColumn("comp_yrs").addColumn("edtime").addColumn("education").addColumn("fw_uuid")
                            .addColumn("insertDate").addColumn("marital").addColumn("occupation").addColumn("occupation_oth")
                            .addColumn("phone1").addColumn("phone2").addColumn("denomination").addColumn("akan_tribe")
                            .addColumn("religion").addColumn("religion_oth").addColumn("sttime").addColumn("tribe").addColumn("tribe_oth")
                            .addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate").addColumn("location_uuid").build();
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
                // Check if the Pregnancy.zip file exists
                if (pregnancyZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Pregnancy file exists. Extracting and processing...");
                    extractAndProcessFile("pregnancy.zip", "pregnancy.csv", Pregnancy.class, pregnancyCounts, preg, this::checkAndProcessRelationship);
                } else {
                    // File does not exist
                    textView_Sync.setText("Pregnancy file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            private void checkAndProcessRelationship() {
                // Check if the Relationship.zip file exists
                if (relationshipZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Relationship file exists. Extracting and processing...");
                    extractAndProcessFile("relationship.zip", "relationship.csv", Relationship.class, relationshipCounts, rel, () -> {
                        // All datasets processed, perform any final actions here
                        //deleteFiles(getExternalCacheDir());
                        progres.dismiss();
                    });
                } else {
                    // File does not exist
                    textView_Sync.setText("Relationship file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
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
                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 10000;
                        int batchCount = 0;

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
            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(Pregnancy.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("ageOfPregFromPregNotes").addColumn("anc_visits").addColumn("anteNatalClinic").addColumn("attend_you")
                            .addColumn("attend_you_other").addColumn("bnet_loc").addColumn("bnet_loc_other").addColumn("bnet_sou").addColumn("bnet_sou_other")
                            .addColumn("edtime").addColumn("estimatedAgeOfPreg").addColumn("expectedDeliveryDate").addColumn("first_preg").addColumn("first_rec")
                            .addColumn("fw_uuid").addColumn("healthfacility").addColumn("how_many").addColumn("insertDate").addColumn("lastClinicVisitDate")
                            .addColumn("medicineforpregnancy").addColumn("outcome").addColumn("outcome_date").addColumn("own_bnet")
                            .addColumn("pregnancyNumber").addColumn("recordedDate").addColumn("slp_bednet").addColumn("trt_bednet").addColumn("ttinjection")
                            .addColumn("why_no").addColumn("why_no_other").addColumn("individual_uuid").addColumn("sttime").addColumn("visit_uuid")
                            .addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate")
                            .addColumn("preg_ready").addColumn("family_plan").addColumn("plan_method").addColumn("plan_method_oth").addColumn("formcompldate")
                            .build();
                } else if (entityClass.equals(Relationship.class)) {
                    return CsvSchema.builder()
                            .addColumn("uuid").addColumn("aIsToB").addColumn("edtime").addColumn("endDate").addColumn("endType")
                            .addColumn("fw_uuid").addColumn("individualA_uuid").addColumn("individualB_uuid").addColumn("insertDate").addColumn("lcow").addColumn("mar")
                            .addColumn("mrank").addColumn("nchdm").addColumn("nwive").addColumn("polygamous")
                            .addColumn("startDate").addColumn("sttime").addColumn("tnbch").addColumn("comment")
                            .addColumn("status").addColumn("supervisor").addColumn("approveDate").addColumn("location_uuid").addColumn("locationB_uuid").addColumn("formcompldate")
                            .build();
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
                // Check if the ses.zip file exists
                if (sesZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("SES file exists. Extracting and processing...");
                    extractAndProcessFile("ses.zip", "ses.csv", HdssSociodemo.class, sesCounts, ses, this::checkAndProcessVac);
                } else {
                    // File does not exist
                    textView_Sync.setText("SES file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            private void checkAndProcessVac() {
                // Check if the vac.zip file exists
                if (vaccinationZipFile.exists()) {
                    // File exists, extract and process it
                    textView_Sync.setText("Vaccination file exists. Extracting and processing...");
                    extractAndProcessFile("vaccination.zip", "vaccination.csv", Vaccination.class, vaccinationCounts, vac, () -> {
                        // All datasets processed, perform any final actions here
                        //deleteFiles(getExternalCacheDir());
                        progres.dismiss();
                    });
                } else {
                    // File does not exist
                    textView_Sync.setText("Vaccination file not downloaded. Please download it first.");
                    textView_Sync.setTextColor(Color.parseColor("#FF0000"));
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
                        long[] totalRecords = new long[1];
                        totalRecords[0] = countTotalRecords(extractedFile);
                        AtomicLong counts = new AtomicLong();
                        List<Object> entities = new ArrayList<>();
                        int batchSize = 10000;
                        int batchCount = 0;

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
            private CsvSchema getCsvSchemaForEntity(Class<?> entityClass) {
                if (entityClass.equals(HdssSociodemo.class)) {
                    return CsvSchema.builder()
                            .addColumn("socialgroup_uuid")
                            .addColumn("aircon_fcorres").addColumn("aircon_num_fcorres").addColumn("animal_othr_fcorres").addColumn("animal_othr_num_fcorres")
                            .addColumn("animal_othr_spfy_fcorres").addColumn("bike_fcorres").addColumn("bike_num_fcorres").addColumn("blender_fcorres")
                            .addColumn("blender_num_fcorres").addColumn("boat_fcorres").addColumn("boat_num_fcorres").addColumn("cabinets_fcorres")
                            .addColumn("cabinets_num_fcorres").addColumn("car_fcorres").addColumn("car_num_fcorres").addColumn("cart_fcorres")
                            .addColumn("cart_num_fcorres").addColumn("cattle_fcorres").addColumn("cattle_num_fcorres").addColumn("cethnic")
                            .addColumn("chew_bnut_oecoccur").addColumn("chew_oecoccur").addColumn("computer_fcorres").addColumn("computer_num_fcorres")
                            .addColumn("cooking_inside_fcorres").addColumn("cooking_loc_fcorres").addColumn("cooking_room_fcorres").addColumn("cooking_vent_fcorres")
                            .addColumn("donkey_fcorres").addColumn("donkey_num_fcorres").addColumn("drink_oecoccur").addColumn("dvd_cd_fcorres")
                            .addColumn("dvd_cd_num_fcorres").addColumn("edtime").addColumn("electricity_fcorres").addColumn("ext_wall_fcorres").addColumn("ext_wall_spfy_fcorres")
                            .addColumn("floor_fcorres").addColumn("floor_spfy_fcorres").addColumn("foam_matt_fcorres").addColumn("foam_matt_num_fcorres")
                            .addColumn("form_comments_txt").addColumn("form_comments_yn").addColumn("formcompldate").addColumn("fridge_fcorres").addColumn("fridge_num_fcorres")
                            .addColumn("fw_uuid").addColumn("goat_fcorres").addColumn("goat_num_fcorres")
                            .addColumn("h2o_dist_fcorres").addColumn("h2o_fcorres").addColumn("h2o_hours_fcorres")
                            .addColumn("h2o_mins_fcorres").addColumn("h2o_prep_fcorres").addColumn("h2o_prep_spfy_fcorres_1")
                            .addColumn("h2o_prep_spfy_fcorres_2").addColumn("h2o_prep_spfy_fcorres_3")
                            .addColumn("h2o_prep_spfy_fcorres_4").addColumn("h2o_prep_spfy_fcorres_5").addColumn("h2o_spfy_fcorres")
                            .addColumn("head_hh_fcorres").addColumn("head_hh_spfy_fcorres").addColumn("horse_fcorres")
                            .addColumn("horse_num_fcorres").addColumn("house_occ_ge5_fcorres").addColumn("house_occ_lt5_fcorres")
                            .addColumn("house_occ_tot_fcorres").addColumn("house_room_child_fcorres")
                            .addColumn("house_rooms_fcorres").addColumn("individual_uuid").addColumn("insertDate")
                            .addColumn("internet_fcorres").addColumn("job_busown_spfy_scorres").addColumn("job_othr_spfy_scorres")
                            .addColumn("job_salary_spfy_scorres").addColumn("job_scorres").addColumn("job_skilled_spfy_scorres")
                            .addColumn("job_smbus_spfy_scorres").addColumn("job_unskilled_spfy_scorres").addColumn("land_fcorres")
                            .addColumn("land_use_fcorres_1").addColumn("land_use_fcorres_2").addColumn("land_use_fcorres_3")
                            .addColumn("land_use_fcorres_4").addColumn("land_use_fcorres_5").addColumn("land_use_fcorres_88")
                            .addColumn("land_use_spfy_fcorres_88").addColumn("landline_fcorres").addColumn("lantern_fcorres")
                            .addColumn("lantern_num_fcorres").addColumn("livestock_fcorres").addColumn("location_uuid")
                            .addColumn("marital_age").addColumn("marital_scorres")
                            .addColumn("mobile_access_fcorres").addColumn("mobile_fcorres").addColumn("mobile_num_fcorres")
                            .addColumn("mosquito_net_fcorres").addColumn("mosquito_net_num_fcorres").addColumn("motorcycle_fcorres")
                            .addColumn("motorcycle_num_fcorres").addColumn("nth_trb_spfy_cethnic")
                            .addColumn("othr_trb_spfy_cethnic").addColumn("own_rent_scorres").addColumn("own_rent_spfy_scorres")
                            .addColumn("pig_fcorres").addColumn("pig_num_fcorres").addColumn("plough_fcorres")
                            .addColumn("plough_num_fcorres").addColumn("poultry_fcorres").addColumn("poultry_num_fcorres")
                            .addColumn("ptr_busown_spfy_scorres").addColumn("ptr_othr_spfy_scorres")
                            .addColumn("ptr_salary_spfy_scorres").addColumn("ptr_scorres").addColumn("ptr_skilled_spfy_scorres")
                            .addColumn("ptr_smbus_spfy_scorres").addColumn("ptr_unskilled_spfy_scorres").addColumn("radio_fcorres")
                            .addColumn("radio_num_fcorres").addColumn("religion_scorres").addColumn("religion_spfy_scorres")
                            .addColumn("roof_fcorres").addColumn("roof_spfy_fcorres").addColumn("sat_dish_fcorres")
                            .addColumn("sat_dish_num_fcorres").addColumn("sd_obsstdat").addColumn("sew_fcorres")
                            .addColumn("sew_num_fcorres").addColumn("sheep_fcorres").addColumn("sheep_num_fcorres")
                            .addColumn("smoke_hhold_in_oecdosfrq").addColumn("smoke_hhold_oecoccur").addColumn("smoke_in_oecdosfrq")
                            .addColumn("smoke_oecoccur").addColumn("sofa_fcorres").addColumn("sofa_num_fcorres")
                            .addColumn("solar_fcorres").addColumn("spring_matt_fcorres").addColumn("spring_matt_num_fcorres")
                            .addColumn("stove_fcorres").addColumn("stove_fuel_fcorres_1").addColumn("stove_fuel_fcorres_10")
                            .addColumn("stove_fuel_fcorres_11").addColumn("stove_fuel_fcorres_12")
                            .addColumn("stove_fuel_fcorres_13").addColumn("stove_fuel_fcorres_14").addColumn("stove_fuel_fcorres_2")
                            .addColumn("stove_fuel_fcorres_3").addColumn("stove_fuel_fcorres_4").addColumn("stove_fuel_fcorres_5")
                            .addColumn("stove_fuel_fcorres_6").addColumn("stove_fuel_fcorres_7").addColumn("stove_fuel_fcorres_8")
                            .addColumn("stove_fuel_fcorres_88").addColumn("stove_fuel_fcorres_9")
                            .addColumn("stove_fuel_spfy_fcorres_88").addColumn("stove_spfy_fcorres").addColumn("straw_matt_fcorres").addColumn("sttime")
                            .addColumn("straw_matt_num_fcorres").addColumn("tables_fcorres").addColumn("tables_num_fcorres")
                            .addColumn("toilet_fcorres").addColumn("toilet_loc_fcorres").addColumn("toilet_loc_spfy_fcorres")
                            .addColumn("toilet_share_fcorres").addColumn("toilet_share_num_fcorres")
                            .addColumn("toilet_spfy_fcorres").addColumn("tractor_fcorres").addColumn("tractor_num_fcorres")
                            .addColumn("tricycles_fcorres").addColumn("tricycles_num_fcorres").addColumn("tv_fcorres")
                            .addColumn("tv_num_fcorres").addColumn("uuid").addColumn("wash_fcorres").addColumn("wash_num_fcorres")
                            .addColumn("watch_fcorres").addColumn("watch_num_fcorres").addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate")
                            .addColumn("pets").addColumn("dogs").addColumn("guinea_pigs").addColumn("cats").addColumn("fish").addColumn("birds")
                            .addColumn("rabbits").addColumn("reptiles").addColumn("pet_other").addColumn("pet_other_spfy").addColumn("pet_vac")
                            .addColumn("id0001").addColumn("id0002").addColumn("id0003").addColumn("id0004").addColumn("id0005")
                            .addColumn("id0006").addColumn("id0006_1").addColumn("id0007").addColumn("id0007_1").addColumn("id0008").addColumn("id0008_1").addColumn("id0009").addColumn("id0009_1").addColumn("id0010")
                            .addColumn("id0010_1").addColumn("id0011").addColumn("id0011_1").addColumn("id0012").addColumn("id0012_1").addColumn("id0014").addColumn("id0014_1").addColumn("id0015").addColumn("id0015_1").addColumn("id0016").addColumn("id0016_1").addColumn("id0017").addColumn("id0017_1").addColumn("id0018").addColumn("id0018_1").addColumn("id0019").addColumn("id0019_1").addColumn("id0013").addColumn("id0013_1").addColumn("id0021")
                            .build();
                } else if (entityClass.equals(Vaccination.class)) {
                    return CsvSchema.builder()
                            .addColumn("individual_uuid").addColumn("admission")
                            .addColumn("admitDate").addColumn("arti").addColumn("artitreat").addColumn("bcg").addColumn("bednet")
                            .addColumn("chlbednet").addColumn("diarrhoea").addColumn("diarrhoeatreat").addColumn("dob")
                            .addColumn("dpt_hepb_hib1").addColumn("dpt_hepb_hib2").addColumn("dpt_hepb_hib3").addColumn("editDate").addColumn("edtime").addColumn("fever")
                            .addColumn("fevertreat").addColumn("fw_uuid").addColumn("hcard").addColumn("hl").addColumn("hod")
                            .addColumn("hom").addColumn("insertDate").addColumn("ipv").addColumn("itn").addColumn("location_uuid")
                            .addColumn("measles_rubella1").addColumn("measles_rubella2").addColumn("menA").addColumn("muac")
                            .addColumn("nhis").addColumn("onet").addColumn("opv0").addColumn("opv1").addColumn("opv2")
                            .addColumn("opv3").addColumn("pneumo1").addColumn("pneumo2").addColumn("pneumo3").addColumn("rea")
                            .addColumn("rea_oth").addColumn("reason").addColumn("reason_oth").addColumn("rota1").addColumn("rota2")
                            .addColumn("rota3").addColumn("rtss18").addColumn("rtss6").addColumn("rtss7").addColumn("rtss9")
                            .addColumn("sbf").addColumn("scar").addColumn("slpbednet").addColumn("socialgroup_uuid")
                            .addColumn("stm").addColumn("sttime").addColumn("sty").addColumn("uuid").addColumn("vitaminA12").addColumn("vitaminA18")
                            .addColumn("vitaminA6").addColumn("weight").addColumn("yellow_fever")
                            .addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate").build();
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
            if (progres.isShowing()) {
                progres.dismiss();
            }
            progres.setProgress(0);
            progres.setMax(0);
            progres.setMessage(message);
            progres.setTitle(title);
            progres.show();
        });
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