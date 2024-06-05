package org.openhds.hdsscapture.Activity;

import static org.openhds.hdsscapture.AppConstants.DATA_QUERY;
import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_DEMO;
import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_IND;
import static org.openhds.hdsscapture.AppConstants.DOWNLOAD_SES;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import org.openhds.hdsscapture.Dialog.ExtraDownloadSummary;
import org.openhds.hdsscapture.Dialog.IndividualDownloadSummary;
import org.openhds.hdsscapture.Dialog.OtherDownloadSummary;
import org.openhds.hdsscapture.Dialog.ProgressDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.odk.Form;
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

        //Sync Core Dataset
        String IndSyncDatetime = getIndSyncDatetime();
        if (IndSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            IndSyncDatetime = sdf.format(new Date());
            setLastIndDatetime(IndSyncDatetime);
        }
        final TextView syncDateTextView = findViewById(R.id.syncIndividualDate);
        syncDateTextView.setText(IndSyncDatetime);
        // Assuming this is inside your Activity class

        final Button button_SyncAll = findViewById(R.id.syncIndividual);
        button_SyncAll.setOnClickListener(new View.OnClickListener() {
            final TextView textView_Sync = findViewById(R.id.syncIndividualMessage);
            final ProgressBar progressBar = findViewById(R.id.IndividualProgressBar);
            AtomicLong locationCounts = new AtomicLong();
            AtomicLong individualCounts = new AtomicLong();
            AtomicLong socialgroupCounts = new AtomicLong();
            AtomicLong residencyCounts = new AtomicLong();
            String ind = "Individual";
            String loc = "Location";
            String soc = "Socialgroup";
            String res = "Residency";

            @Override
            public void onClick(View v) {
                resetAllCounts();
                downloadAndProcessLocation();
            }

            private void downloadAndProcessLocation() {
                // Download and process "location" dataset
                CsvSchema locationSchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("accuracy").addColumn("compextId").addColumn("compno").addColumn("edtime")
                        .addColumn("fw_uuid").addColumn("insertDate").addColumn("latitude").addColumn("locationLevel_uuid").addColumn("locationName")
                        .addColumn("locationType").addColumn("longitude").addColumn("status").addColumn("sttime").addColumn("vill_extId").build();

                downloadAndProcessDataset("location.zip", "location.csv", () -> dao.downloadLocation(authorizationHeader), Locations.class, locationSchema, locationCounts, loc, this::downloadAndProcessIndividual);
            }

            private void downloadAndProcessIndividual() {
                // Download and process "individuals" dataset
                CsvSchema individualsSchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("dob").addColumn("dobAspect").addColumn("edtime").addColumn("extId")
                        .addColumn("firstName").addColumn("fw_uuid").addColumn("gender").addColumn("ghanacard").addColumn("insertDate")
                        .addColumn("lastName").addColumn("otherName").addColumn("father_uuid").addColumn("mother_uuid")
                        .addColumn("sttime").addColumn("endType").addColumn("compno")
                        .addColumn("village").addColumn("hohID").addColumn("phone1").build();

                downloadAndProcessDataset("individual.zip", "individual.csv", () -> dao.downloadIndividual(authorizationHeader), Individual.class, individualsSchema, individualCounts, ind, this::downloadAndProcessSocialgroup);
            }

            private void downloadAndProcessSocialgroup() {
                // Download and process "socialgroup" dataset
                CsvSchema socialgroupSchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("extId").addColumn("fw_uuid").addColumn("groupName")
                        .addColumn("groupType").addColumn("insertDate")
                        .addColumn("individual_uuid").build();

                downloadAndProcessDataset("socialgroup.zip", "socialgroup.csv", () -> dao.downloadSocialgroup(authorizationHeader), Socialgroup.class, socialgroupSchema, socialgroupCounts, soc, this::downloadAndProcessResidency);
            }

            private void downloadAndProcessResidency() {
                // Download and process "residency" dataset
                CsvSchema residencySchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("edtime").addColumn("endDate").addColumn("endType")
                        .addColumn("fw_uuid").addColumn("insertDate").addColumn("rltn_head")
                        .addColumn("startDate").addColumn("startType")
                        .addColumn("individual_uuid").addColumn("location_uuid").addColumn("socialgroup_uuid")
                        .addColumn("sttime").build();

                downloadAndProcessDataset("residency.zip", "residency.csv", () -> dao.downloadResidency(authorizationHeader), Residency.class, residencySchema, residencyCounts, res, () -> {
                    // All datasets processed, perform any final actions here
                    deleteFiles(getExternalCacheDir());
//                    resetCountOnClick(ind);
//                    resetCountOnClick(loc);
//                    resetCountOnClick(soc);
//                    resetCountOnClick(res);
                });
            }

            private void resetAllCounts() {
                resetCountOnClick(ind);
                resetCountOnClick(loc);
                resetCountOnClick(soc);
                resetCountOnClick(res);
            }

            // Delete files in a directory
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

            private <T> void downloadAndProcessDataset(String zipFileName, String extractedFileName, Supplier<Call<ResponseBody>> downloadCallSupplier, Class<T> entityClass, CsvSchema schema, AtomicLong countKey, String files, Runnable nextStep) {
                textView_Sync.setText("Downloading " + files + " Dataset");
                progres.setTitle("Downloading " + files + " Dataset");
                progres.show();
                progres.setProgress(0);
                progres.setMax(0);
                progressBar.setProgress(0);

                // File path for the downloaded file
                File file = new File(getExternalCacheDir(), zipFileName);
                Call<ResponseBody> call = downloadCallSupplier.get();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    if (fileName.equals(extractedFileName)) {
                                        File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                        FileOutputStream fos = new FileOutputStream(newFile);
                                        int len;
                                        while ((len = zipInputStream.read(buffer)) > 0) {
                                            fos.write(buffer, 0, len);
                                        }
                                        fos.close();
                                        // Check if the extracted CSV file is empty
                                        if (newFile.length() == 0) {
                                            // CSV file is empty, skip processing and move to the next step
                                            nextStep.run();
                                            return;
                                        }
                                        break;
                                    }
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);

                                // Use the appropriate DAO based on the entity class
                                if (entityClass.equals(Locations.class)) {
                                    locationDao = appDatabase.locationDao();
                                } else if (entityClass.equals(Individual.class)) {
                                    individualDao = appDatabase.individualDao();
                                } else if (entityClass.equals(Socialgroup.class)) {
                                    socialgroupDao = appDatabase.socialgroupDao();
                                }else if (entityClass.equals(Residency.class)) {
                                    residencyDao = appDatabase.residencyDao();
                                }

                                // Import the unzipped CSV file into the Room database
                                if (locationDao != null || individualDao != null || socialgroupDao != null || residencyDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
                                    CsvMapper mapper = new CsvMapper();
                                    MappingIterator<T> iterator = mapper.readerFor(entityClass).with(schema).readValues(unzippedFile);
                                    progres.show();
                                    progressBar.setProgress(0);
                                    long[] totalRecords = new long[1];
                                    CsvMapper countMapper = new CsvMapper();
                                    //CsvSchema countSchema = CsvSchema.emptySchema();  // No header since CSV has no header
                                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();  // Assuming your CSV has a header
                                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                                    Iterator<Map<String, String>> countIterator = countReader.readValues(unzippedFile);

                                    totalRecords[0] = 0;
                                    while (countIterator.hasNext()) {
                                        countIterator.next();
                                        totalRecords[0]++;
                                    }

                                    AtomicLong counts = new AtomicLong();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<T> entities = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            T entity = iterator.next();
                                            if (entity != null) {
                                                entities.add(entity);
                                                batchCount++;

                                                if (batchCount == batchSize) {
                                                    if (entityClass.equals(Locations.class)) {
                                                        //locationDao.insert((List<Locations>) entities);
                                                        locationDao.insert((Locations[]) entities.toArray(new Locations[0]));
                                                    } else if (entityClass.equals(Individual.class)) {
                                                        individualDao.insert((Individual[]) entities.toArray(new Individual[0]));
                                                        //individualDao.insert((List<Individual>) entities);
                                                    }else if (entityClass.equals(Socialgroup.class)) {
                                                        //socialgroupDao.insert((List<Socialgroup>) entities);
                                                        socialgroupDao.insert((Socialgroup[]) entities.toArray(new Socialgroup[0]));
                                                    }else if (entityClass.equals(Residency.class)) {
                                                        //residencyDao.insert((List<Residency>) entities);
                                                        residencyDao.insert((Residency[]) entities.toArray(new Residency[0]));
                                                    }
                                                    entities.clear();
                                                    batchCount = 0;
                                                }

                                                runOnUiThread(() -> {
                                                    long currentCount = counts.incrementAndGet();
                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                                                    int totalRecordsCount = (int) totalRecords[0];
                                                    int cnt = (int) currentCount;
                                                    // Update UI every 500 records or when the task is complete
                                                    if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                                                        runOnUiThread(() -> {
                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
                                                            progressBar.setProgress(progress);
                                                            progres.setMax(totalRecordsCount);
                                                            progres.setTitle("Downloading " + files + " Dataset");
                                                            progres.setProgress(cnt); // Set progress based on current count

                                                            // Assuming 'progres' is a ProgressDialog
                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);

                                                            // Ensure the style is set correctly
                                                            progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                                                            // If ProgressDialog is indeterminate, set it to false
                                                            if (progres.isIndeterminate()) {
                                                                progres.setIndeterminate(false);
                                                            }
                                                        });
                                                    }
                                                });

//                                                runOnUiThread(() -> {
//                                                    long currentCount = counts.incrementAndGet();
//                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
//
//                                                    // Update UI every 500 records (you can adjust this value)
//                                                    if (currentCount % 1000 == 0 || currentCount == totalRecords[0]) {
//                                                        runOnUiThread(() -> {
//                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
//                                                            progressBar.setProgress(progress);
//                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] +" " + files);
//                                                        });
//                                                    }
//                                                });
                                            }
                                        }
                                        if (batchCount > 0) {
                                            if (entityClass.equals(Locations.class)) {
                                                //locationDao.insert((List<Locations>) entities);
                                                locationDao.insert((Locations[]) entities.toArray(new Locations[0]));
                                            } else if (entityClass.equals(Individual.class)) {
                                                individualDao.insert((Individual[]) entities.toArray(new Individual[0]));
                                                individualDao.insert((List<Individual>) entities);
                                            }else if (entityClass.equals(Socialgroup.class)) {
                                                socialgroupDao.insert((Socialgroup[]) entities.toArray(new Socialgroup[0]));
                                                //socialgroupDao.insert((List<Socialgroup>) entities);
                                            }else if (entityClass.equals(Residency.class)) {
                                                residencyDao.insert((Residency[]) entities.toArray(new Residency[0]));
                                                //residencyDao.insert((List<Residency>) entities);
                                            }
                                        }
                                        runOnUiThread(() -> {
                                            long finalCount = counts.get();
                                            int finalProgress = (int) (((double) finalCount / totalRecords[0]) * 100);
                                            textView_Sync.setText("Successful Download ");
                                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                                            progressBar.setProgress(finalProgress);
                                            progres.dismiss();
                                            //Log.d("Three", "Location Count: " + files + " " + finalCount);
                                            saveCountsToSharedPreferences(files, finalCount);

                                            // Execute the next step
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
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        textView_Sync.setText("Download Error! Retry or Contact Administrator");
                        progressBar.setProgress(0);
                        progres.dismiss();
                        textView_Sync.setTextColor(Color.RED);
                    }
                });
            }
        });



        //Sync Events
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
            AtomicLong demographicsCounts = new AtomicLong();

            String preg = "Pregnancy";
            String rel = "Relationship";
            String dem = "Demographic";

            @Override
            public void onClick(View v) {

                resetAll();
                // Download all
                downloadPregnancy();

            }

            private void downloadPregnancy() {
                CsvSchema pregnancySchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("ageOfPregFromPregNotes").addColumn("anc_visits").addColumn("anteNatalClinic").addColumn("attend_you")
                        .addColumn("attend_you_other").addColumn("bnet_loc").addColumn("bnet_loc_other").addColumn("bnet_sou").addColumn("bnet_sou_other")
                        .addColumn("edtime").addColumn("estimatedAgeOfPreg").addColumn("expectedDeliveryDate").addColumn("first_preg").addColumn("first_rec")
                        .addColumn("fw_uuid").addColumn("healthfacility").addColumn("how_many").addColumn("insertDate").addColumn("lastClinicVisitDate")
                        .addColumn("medicineforpregnancy").addColumn("outcome").addColumn("outcome_date").addColumn("own_bnet")
                        .addColumn("pregnancyNumber").addColumn("recordedDate").addColumn("slp_bednet").addColumn("trt_bednet").addColumn("ttinjection")
                        .addColumn("why_no").addColumn("why_no_other").addColumn("individual_uuid").addColumn("sttime").addColumn("visit_uuid")
                        .addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate").build();

                downloadAndProcessDataset("pregnancy.zip", "pregnancy.csv", () -> dao.downloadPregnancy(authorizationHeader), Pregnancy.class, pregnancySchema, pregnancyCounts, preg, this::downloadRelationship);

            }

            private void downloadRelationship() {
                // Download and process "relationship" dataset
                CsvSchema relationshipSchema = CsvSchema.builder()
                        .addColumn("uuid").addColumn("aIsToB").addColumn("edtime").addColumn("endDate").addColumn("endType")
                        .addColumn("fw_uuid").addColumn("individualA_uuid").addColumn("individualB_uuid").addColumn("insertDate").addColumn("lcow").addColumn("mar")
                        .addColumn("mrank").addColumn("nchdm").addColumn("nwive").addColumn("polygamous")
                        .addColumn("startDate").addColumn("sttime").addColumn("tnbch").addColumn("comment")
                        .addColumn("status").addColumn("supervisor").addColumn("approveDate").build();

                downloadAndProcessDataset("relationship.zip", "relationship.csv", () -> dao.downloadRelationship(authorizationHeader), Relationship.class, relationshipSchema, relationshipCounts, rel, this::downloadDemographics);
            }

            private void downloadDemographics() {
                // Download and process "demographics" dataset
                CsvSchema demographicsSchema = CsvSchema.builder()
                        .addColumn("individual_uuid").addColumn("comp_yrs").addColumn("edtime").addColumn("education").addColumn("fw_uuid")
                        .addColumn("insertDate").addColumn("marital").addColumn("occupation").addColumn("occupation_oth")
                        .addColumn("phone1").addColumn("phone2")
                        .addColumn("religion").addColumn("religion_oth").addColumn("sttime").addColumn("tribe").addColumn("tribe_oth")
                        .addColumn("comment").addColumn("status").addColumn("supervisor").addColumn("approveDate").build();

                downloadAndProcessDataset("demographics.zip", "demographics.csv", () -> dao.downloadDemography(authorizationHeader), Demographic.class, demographicsSchema, demographicsCounts, dem, () -> {

                    deleteFiles(getExternalCacheDir());
                });

            }

            private void resetAll(){
                // Reset counts to 0 when the button is clicked
                resetCountOnClick(preg);
                resetCountOnClick(rel);
                resetCountOnClick(dem);
            }

            // Delete files in a directory
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


            private <T> void downloadAndProcessDataset(String zipFileName, String extractedFileName, Supplier<Call<ResponseBody>> downloadCallSupplier, Class<T> entityClass, CsvSchema schema, AtomicLong countKey, String files, Runnable nextStep) {
                textView_Sync.setText("Downloading " + files + " Dataset");
                progres.setTitle("Downloading " + files + " Dataset");
                progres.show();
                progres.setProgress(0);
                progres.setMax(0);
                progressBar.setProgress(0);

                // File path for the downloaded file
                File file = new File(getExternalCacheDir(), zipFileName);
                Call<ResponseBody> call = downloadCallSupplier.get();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progres.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    if (fileName.equals(extractedFileName)) {
                                        File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                        FileOutputStream fos = new FileOutputStream(newFile);
                                        int len;
                                        while ((len = zipInputStream.read(buffer)) > 0) {
                                            fos.write(buffer, 0, len);
                                        }
                                        fos.close();
                                        // Check if the extracted CSV file is empty
                                        if (newFile.length() == 0) {
                                            // CSV file is empty, skip processing and move to the next step
                                            nextStep.run();
                                            return;
                                        }
                                        break;
                                    }
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);

                                // Use the appropriate DAO based on the entity class
                                if (entityClass.equals(Pregnancy.class)) {
                                    pregnancyDao = appDatabase.pregnancyDao();
                                } else if (entityClass.equals(Relationship.class)) {
                                    relationshipDao = appDatabase.relationshipDao();
                                } else if (entityClass.equals(Demographic.class)) {
                                    demographicDao = appDatabase.demographicDao();
                                }

                                // Import the unzipped CSV file into the Room database
                                if (pregnancyDao != null || relationshipDao != null || demographicDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
                                    CsvMapper mapper = new CsvMapper();
                                    MappingIterator<T> iterator = mapper.readerFor(entityClass).with(schema).readValues(unzippedFile);
                                    progressBar.setProgress(0);
                                    progres.show();
                                    long[] totalRecords = new long[1];
                                    CsvMapper countMapper = new CsvMapper();
                                    //CsvSchema countSchema = CsvSchema.emptySchema();  // No header since CSV has no header
                                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();  // Assuming your CSV has a header
                                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                                    Iterator<Map<String, String>> countIterator = countReader.readValues(unzippedFile);

                                    totalRecords[0] = 0;
                                    while (countIterator.hasNext()) {
                                        countIterator.next();
                                        totalRecords[0]++;
                                    }

                                    AtomicLong counts = new AtomicLong();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<T> entities = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            T entity = iterator.next();
                                            if (entity != null) {
                                                entities.add(entity);
                                                batchCount++;

                                                if (batchCount == batchSize) {
                                                    if (entityClass.equals(Pregnancy.class)) {
                                                        //pregnancyDao.insert((List<Pregnancy>) entities);
                                                        pregnancyDao.insert((Pregnancy[]) entities.toArray(new Pregnancy[0]));
                                                    } else if (entityClass.equals(Relationship.class)) {
                                                        relationshipDao.insert((Relationship[]) entities.toArray(new Relationship[0]));
                                                        //relationshipDao.insert((List<Relationship>) entities);
                                                    }else if (entityClass.equals(Demographic.class)) {
                                                        demographicDao.insert((Demographic[]) entities.toArray(new Demographic[0]));
                                                        //demographicDao.insert((List<Demographic>) entities);
                                                    }
                                                    entities.clear();
                                                    batchCount = 0;
                                                }

                                                runOnUiThread(() -> {
                                                    long currentCount = counts.incrementAndGet();
                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                                                    int totalRecordsCount = (int) totalRecords[0];
                                                    int cnt = (int) currentCount;
                                                    // Update UI every 500 records or when the task is complete
                                                    if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                                                        runOnUiThread(() -> {
                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
                                                            progres.setTitle("Downloading " + files + " Dataset");
                                                            progressBar.setProgress(progress);
                                                            progres.setMax(totalRecordsCount);
                                                            progres.setProgress(cnt); // Set progress based on current count


                                                            // Assuming 'progres' is a ProgressDialog
                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);

                                                            // Ensure the style is set correctly
                                                            progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                                                            // If ProgressDialog is indeterminate, set it to false
                                                            if (progres.isIndeterminate()) {
                                                                progres.setIndeterminate(false);
                                                            }
                                                        });
                                                    }
                                                });

//                                                runOnUiThread(() -> {
//                                                    long currentCount = counts.incrementAndGet();
//                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
//                                                    //int progressPercentage = (int) (((double) countKey.get() / totalRecords) * 100);
//
//                                                    // Update UI every 500 records (you can adjust this value)
//                                                    if (currentCount % 1000 == 0 || currentCount == totalRecords[0]) {
//                                                        runOnUiThread(() -> {
//                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
//                                                            progressBar.setProgress(progress);
//                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] +" " + files);
//                                                        });
//                                                    }
//                                                });
                                            }
                                        }
                                        if (batchCount > 0) {
                                            if (entityClass.equals(Pregnancy.class)) {
                                                //pregnancyDao.insert((List<Pregnancy>) entities);
                                                pregnancyDao.insert((Pregnancy[]) entities.toArray(new Pregnancy[0]));
                                            } else if (entityClass.equals(Relationship.class)) {
                                                relationshipDao.insert((Relationship[]) entities.toArray(new Relationship[0]));
                                                //relationshipDao.insert((List<Relationship>) entities);
                                            }else if (entityClass.equals(Demographic.class)) {
                                                //demographicDao.insert((List<Demographic>) entities);
                                                demographicDao.insert((Demographic[]) entities.toArray(new Demographic[0]));
                                            }
                                        }
                                        runOnUiThread(() -> {
                                            long finalCount = counts.get();
                                            int finalProgress = (int) (((double) finalCount / totalRecords[0]) * 100);
                                            textView_Sync.setText("Successful Download");
                                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                                            progressBar.setProgress(finalProgress);
                                            saveCountsToSharedPreferences(files, finalCount);
                                            progres.dismiss();

                                            // Execute the next step
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
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        textView_Sync.setText("Download Error! Retry or Contact Administrator");
                        progressBar.setProgress(0);
                        progres.dismiss();
                        textView_Sync.setTextColor(Color.RED);
                    }
                });

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
                // Download and process "ses" dataset
                    resetAllCounts();
                    downloadSes();
            }

            private void downloadSes() {
                CsvSchema sesSchema = CsvSchema.builder()
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
                        .addColumn("watch_fcorres").addColumn("watch_num_fcorres").build();

                downloadAndProcessDataset("ses.zip", "ses.csv", () -> dao.downloadSes(authorizationHeader), HdssSociodemo.class, sesSchema, sesCounts, ses, this::downloadVac);

            }

            private void downloadVac() {
                // Download and process "vaccination" dataset
                CsvSchema vaccinationSchema = CsvSchema.builder()
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
                        .addColumn("vitaminA6").addColumn("weight").addColumn("yellow_fever").build();

                downloadAndProcessDataset("vaccination.zip", "vaccination.csv", () -> dao.downloadVaccination(authorizationHeader), Vaccination.class, vaccinationSchema, vaccinationCounts, vac, () -> {
                    deleteFiles(getExternalCacheDir());
                });

            }

            private void resetAllCounts() {
                resetCountOnClick(ses);
                resetCountOnClick(vac);
            }

            // Delete files in a directory
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

            private <T> void downloadAndProcessDataset(String zipFileName, String extractedFileName, Supplier<Call<ResponseBody>> downloadCallSupplier, Class<T> entityClass, CsvSchema schema, AtomicLong countKey, String files, Runnable nextStep) {
                textView_Sync.setText("Downloading " + files + " Dataset");
                progressBar.setProgress(0);
                progres.setTitle("Downloading " + files + " Dataset");
                progres.setProgress(0);
                progres.setMax(0);
                progres.show();

                // File path for the downloaded file
                File file = new File(getExternalCacheDir(), zipFileName);
                Call<ResponseBody> call = downloadCallSupplier.get();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    if (fileName.equals(extractedFileName)) {
                                        File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                        FileOutputStream fos = new FileOutputStream(newFile);
                                        int len;
                                        while ((len = zipInputStream.read(buffer)) > 0) {
                                            fos.write(buffer, 0, len);
                                        }
                                        fos.close();

                                        // Check if the extracted CSV file is empty
                                        if (newFile.length() == 0) {
                                            // CSV file is empty, skip processing and move to the next step
                                            nextStep.run();
                                            return;
                                        }

                                        break;
                                    }
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);

                                // Use the appropriate DAO based on the entity class
                                if (entityClass.equals(HdssSociodemo.class)) {
                                    hdssSociodemoDao = appDatabase.hdssSociodemoDao();
                                } else if (entityClass.equals(Vaccination.class)) {
                                    vaccinationDao = appDatabase.vaccinationDao();
                                }

                                // Import the unzipped CSV file into the Room database
                                if (hdssSociodemoDao != null || vaccinationDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
                                    CsvMapper mapper = new CsvMapper();
                                    MappingIterator<T> iterator = mapper.readerFor(entityClass).with(schema).readValues(unzippedFile);
                                    progressBar.setProgress(0);
                                    progres.show();
                                    long[] totalRecords = new long[1];
                                    CsvMapper countMapper = new CsvMapper();
                                    //CsvSchema countSchema = CsvSchema.emptySchema();  // No header since CSV has no header
                                    CsvSchema countSchema = CsvSchema.emptySchema().withHeader();  // Assuming your CSV has a header
                                    ObjectReader countReader = countMapper.readerFor(Map.class).with(countSchema);
                                    Iterator<Map<String, String>> countIterator = countReader.readValues(unzippedFile);

                                    totalRecords[0] = 0;
                                    while (countIterator.hasNext()) {
                                        countIterator.next();
                                        totalRecords[0]++;
                                    }

                                    AtomicLong counts = new AtomicLong();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<T> entities = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            T entity = iterator.next();
                                            if (entity != null) {
                                                entities.add(entity);
                                                batchCount++;

                                                if (batchCount == batchSize) {
                                                    if (entityClass.equals(HdssSociodemo.class)) {
                                                        hdssSociodemoDao.insert((HdssSociodemo[]) entities.toArray(new HdssSociodemo[0]));
                                                        //hdssSociodemoDao.insert((List<HdssSociodemo>) entities);
                                                    } else if (entityClass.equals(Vaccination.class)) {
                                                        vaccinationDao.insert((Vaccination[]) entities.toArray(new Vaccination[0]));
                                                        //vaccinationDao.insert((List<Vaccination>) entities);
                                                    }
                                                    entities.clear();
                                                    batchCount = 0;
                                                }

                                                runOnUiThread(() -> {
                                                    long currentCount = counts.incrementAndGet();
                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
                                                    int totalRecordsCount = (int) totalRecords[0];
                                                    int cnt = (int) currentCount;
                                                    // Update UI every 500 records or when the task is complete
                                                    if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
                                                        runOnUiThread(() -> {
                                                            textView_Sync.setText("Saving " + progress + "% of " + files);
                                                            progres.setTitle("Downloading " + files + " Dataset");
                                                            progressBar.setProgress(progress);
                                                            progres.setMax(totalRecordsCount);
                                                            progres.setProgress(cnt); // Set progress based on current count

                                                            // Assuming 'progres' is a ProgressDialog
                                                            progres.setMessage("Saving " + currentCount + " of " + totalRecords[0] + " " + files);

                                                            // Ensure the style is set correctly
                                                            progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                                                            // If ProgressDialog is indeterminate, set it to false
                                                            if (progres.isIndeterminate()) {
                                                                progres.setIndeterminate(false);
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        }
                                        if (batchCount > 0) {
                                            if (entityClass.equals(HdssSociodemo.class)) {
                                                //hdssSociodemoDao.insert((List<HdssSociodemo>) entities);
                                                hdssSociodemoDao.insert((HdssSociodemo[]) entities.toArray(new HdssSociodemo[0]));
                                            } else if (entityClass.equals(Vaccination.class)) {
                                                //vaccinationDao.insert((List<Vaccination>) entities);
                                                vaccinationDao.insert((Vaccination[]) entities.toArray(new Vaccination[0]));
                                            }
                                        }
                                        runOnUiThread(() -> {
                                            long finalCount = counts.get();
                                            int finalProgress = (int) (((double) finalCount / totalRecords[0]) * 100);
                                            textView_Sync.setText("Successful Download");
                                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
                                            progressBar.setProgress(finalProgress);
                                            progres.setMax(finalProgress);
                                            progres.dismiss();

                                            saveCountsToSharedPreferences(files, finalCount);
                                            // Execute the next step
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
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        textView_Sync.setText("Download Error! Retry or Contact Administrator");
                        progressBar.setProgress(0);
                        progres.dismiss();
                        textView_Sync.setTextColor(Color.RED);
                    }
                });

            }

        });


//        //Vaccination & SES
//        String SesDatetime = getSesSyncDatetime();
//        if (SesDatetime.isEmpty()) {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
//            SesDatetime = sdf.format(new Date());
//            setLastSesDatetime(SesDatetime);
//        }
//        final TextView syncsDate = findViewById(R.id.syncsesDate);
//        syncsDate.setText(SesDatetime);
//        // Assuming this is inside your Activity class
//
//        final Button button_Oth = findViewById(R.id.syncSes);
//        button_Oth.setOnClickListener(new View.OnClickListener() {
//            final TextView textView_Sync = findViewById(R.id.syncsesMessage);
//            final ProgressBar progressBar = findViewById(R.id.sesProgressBar);
//            AtomicLong sesCounts = new AtomicLong();
//            AtomicLong vaccinationCounts = new AtomicLong();
//
//            String ses = "SES";
//            String vac = "Vaccination";
//
//            @Override
//            public void onClick(View v) {
//                // Download and process "ses" dataset
//                CsvSchema sesSchema = CsvSchema.builder()
//                        .addColumn("socialgroup_uuid")
//                        .addColumn("aircon_fcorres").addColumn("aircon_num_fcorres").addColumn("animal_othr_fcorres").addColumn("animal_othr_num_fcorres")
//                        .addColumn("animal_othr_spfy_fcorres").addColumn("bike_fcorres").addColumn("bike_num_fcorres").addColumn("blender_fcorres")
//                        .addColumn("blender_num_fcorres").addColumn("boat_fcorres").addColumn("boat_num_fcorres").addColumn("cabinets_fcorres")
//                        .addColumn("cabinets_num_fcorres").addColumn("car_fcorres").addColumn("car_num_fcorres").addColumn("cart_fcorres")
//                        .addColumn("cart_num_fcorres").addColumn("cattle_fcorres").addColumn("cattle_num_fcorres").addColumn("cethnic")
//                        .addColumn("chew_bnut_oecoccur").addColumn("chew_oecoccur").addColumn("computer_fcorres").addColumn("computer_num_fcorres")
//                        .addColumn("cooking_inside_fcorres").addColumn("cooking_loc_fcorres").addColumn("cooking_room_fcorres").addColumn("cooking_vent_fcorres")
//                        .addColumn("donkey_fcorres").addColumn("donkey_num_fcorres").addColumn("drink_oecoccur").addColumn("dvd_cd_fcorres")
//                        .addColumn("dvd_cd_num_fcorres").addColumn("edtime").addColumn("electricity_fcorres").addColumn("ext_wall_fcorres").addColumn("ext_wall_spfy_fcorres")
//                        .addColumn("floor_fcorres").addColumn("floor_spfy_fcorres").addColumn("foam_matt_fcorres").addColumn("foam_matt_num_fcorres")
//                        .addColumn("form_comments_txt").addColumn("form_comments_yn").addColumn("formcompldate").addColumn("fridge_fcorres").addColumn("fridge_num_fcorres")
//                        .addColumn("fw_uuid").addColumn("goat_fcorres").addColumn("goat_num_fcorres")
//                        .addColumn("h2o_dist_fcorres").addColumn("h2o_fcorres").addColumn("h2o_hours_fcorres")
//                        .addColumn("h2o_mins_fcorres").addColumn("h2o_prep_fcorres").addColumn("h2o_prep_spfy_fcorres_1")
//                        .addColumn("h2o_prep_spfy_fcorres_2").addColumn("h2o_prep_spfy_fcorres_3")
//                        .addColumn("h2o_prep_spfy_fcorres_4").addColumn("h2o_prep_spfy_fcorres_5").addColumn("h2o_spfy_fcorres")
//                        .addColumn("head_hh_fcorres").addColumn("head_hh_spfy_fcorres").addColumn("horse_fcorres")
//                        .addColumn("horse_num_fcorres").addColumn("house_occ_ge5_fcorres").addColumn("house_occ_lt5_fcorres")
//                        .addColumn("house_occ_tot_fcorres").addColumn("house_room_child_fcorres")
//                        .addColumn("house_rooms_fcorres").addColumn("individual_uuid").addColumn("insertDate")
//                        .addColumn("internet_fcorres").addColumn("job_busown_spfy_scorres").addColumn("job_othr_spfy_scorres")
//                        .addColumn("job_salary_spfy_scorres").addColumn("job_scorres").addColumn("job_skilled_spfy_scorres")
//                        .addColumn("job_smbus_spfy_scorres").addColumn("job_unskilled_spfy_scorres").addColumn("land_fcorres")
//                        .addColumn("land_use_fcorres_1").addColumn("land_use_fcorres_2").addColumn("land_use_fcorres_3")
//                        .addColumn("land_use_fcorres_4").addColumn("land_use_fcorres_5").addColumn("land_use_fcorres_88")
//                        .addColumn("land_use_spfy_fcorres_88").addColumn("landline_fcorres").addColumn("lantern_fcorres")
//                        .addColumn("lantern_num_fcorres").addColumn("livestock_fcorres").addColumn("location_uuid")
//                        .addColumn("marital_age").addColumn("marital_scorres")
//                        .addColumn("mobile_access_fcorres").addColumn("mobile_fcorres").addColumn("mobile_num_fcorres")
//                        .addColumn("mosquito_net_fcorres").addColumn("mosquito_net_num_fcorres").addColumn("motorcycle_fcorres")
//                        .addColumn("motorcycle_num_fcorres").addColumn("nth_trb_spfy_cethnic")
//                        .addColumn("othr_trb_spfy_cethnic").addColumn("own_rent_scorres").addColumn("own_rent_spfy_scorres")
//                        .addColumn("pig_fcorres").addColumn("pig_num_fcorres").addColumn("plough_fcorres")
//                        .addColumn("plough_num_fcorres").addColumn("poultry_fcorres").addColumn("poultry_num_fcorres")
//                        .addColumn("ptr_busown_spfy_scorres").addColumn("ptr_othr_spfy_scorres")
//                        .addColumn("ptr_salary_spfy_scorres").addColumn("ptr_scorres").addColumn("ptr_skilled_spfy_scorres")
//                        .addColumn("ptr_smbus_spfy_scorres").addColumn("ptr_unskilled_spfy_scorres").addColumn("radio_fcorres")
//                        .addColumn("radio_num_fcorres").addColumn("religion_scorres").addColumn("religion_spfy_scorres")
//                        .addColumn("roof_fcorres").addColumn("roof_spfy_fcorres").addColumn("sat_dish_fcorres")
//                        .addColumn("sat_dish_num_fcorres").addColumn("sd_obsstdat").addColumn("sew_fcorres")
//                        .addColumn("sew_num_fcorres").addColumn("sheep_fcorres").addColumn("sheep_num_fcorres")
//                        .addColumn("smoke_hhold_in_oecdosfrq").addColumn("smoke_hhold_oecoccur").addColumn("smoke_in_oecdosfrq")
//                        .addColumn("smoke_oecoccur").addColumn("sofa_fcorres").addColumn("sofa_num_fcorres")
//                        .addColumn("solar_fcorres").addColumn("spring_matt_fcorres").addColumn("spring_matt_num_fcorres")
//                        .addColumn("stove_fcorres").addColumn("stove_fuel_fcorres_1").addColumn("stove_fuel_fcorres_10")
//                        .addColumn("stove_fuel_fcorres_11").addColumn("stove_fuel_fcorres_12")
//                        .addColumn("stove_fuel_fcorres_13").addColumn("stove_fuel_fcorres_14").addColumn("stove_fuel_fcorres_2")
//                        .addColumn("stove_fuel_fcorres_3").addColumn("stove_fuel_fcorres_4").addColumn("stove_fuel_fcorres_5")
//                        .addColumn("stove_fuel_fcorres_6").addColumn("stove_fuel_fcorres_7").addColumn("stove_fuel_fcorres_8")
//                        .addColumn("stove_fuel_fcorres_88").addColumn("stove_fuel_fcorres_9")
//                        .addColumn("stove_fuel_spfy_fcorres_88").addColumn("stove_spfy_fcorres").addColumn("straw_matt_fcorres").addColumn("sttime")
//                        .addColumn("straw_matt_num_fcorres").addColumn("tables_fcorres").addColumn("tables_num_fcorres")
//                        .addColumn("toilet_fcorres").addColumn("toilet_loc_fcorres").addColumn("toilet_loc_spfy_fcorres")
//                        .addColumn("toilet_share_fcorres").addColumn("toilet_share_num_fcorres")
//                        .addColumn("toilet_spfy_fcorres").addColumn("tractor_fcorres").addColumn("tractor_num_fcorres")
//                        .addColumn("tricycles_fcorres").addColumn("tricycles_num_fcorres").addColumn("tv_fcorres")
//                        .addColumn("tv_num_fcorres").addColumn("uuid").addColumn("wash_fcorres").addColumn("wash_num_fcorres")
//                        .addColumn("watch_fcorres").addColumn("watch_num_fcorres").build();
//
//                downloadAndProcessDataset("ses.zip", "ses.csv", () -> dao.downloadSes(authorizationHeader), HdssSociodemo.class, sesSchema, sesCounts, ses);
//
//                // Download and process "vaccination" dataset
//                CsvSchema vaccinationSchema = CsvSchema.builder()
//                        .addColumn("individual_uuid").addColumn("admission")
//                        .addColumn("admitDate").addColumn("arti").addColumn("artitreat").addColumn("bcg").addColumn("bednet")
//                        .addColumn("chlbednet").addColumn("diarrhoea").addColumn("diarrhoeatreat").addColumn("dob")
//                        .addColumn("dpt_hepb_hib1").addColumn("dpt_hepb_hib2").addColumn("dpt_hepb_hib3").addColumn("editDate").addColumn("edtime").addColumn("fever")
//                        .addColumn("fevertreat").addColumn("fw_uuid").addColumn("hcard").addColumn("hl").addColumn("hod")
//                        .addColumn("hom").addColumn("insertDate").addColumn("ipv").addColumn("itn").addColumn("location_uuid")
//                        .addColumn("measles_rubella1").addColumn("measles_rubella2").addColumn("menA").addColumn("muac")
//                        .addColumn("nhis").addColumn("onet").addColumn("opv0").addColumn("opv1").addColumn("opv2")
//                        .addColumn("opv3").addColumn("pneumo1").addColumn("pneumo2").addColumn("pneumo3").addColumn("rea")
//                        .addColumn("rea_oth").addColumn("reason").addColumn("reason_oth").addColumn("rota1").addColumn("rota2")
//                        .addColumn("rota3").addColumn("rtss18").addColumn("rtss6").addColumn("rtss7").addColumn("rtss9")
//                        .addColumn("sbf").addColumn("scar").addColumn("slpbednet").addColumn("socialgroup_uuid")
//                        .addColumn("stm").addColumn("sttime").addColumn("sty").addColumn("uuid").addColumn("vitaminA12").addColumn("vitaminA18")
//                        .addColumn("vitaminA6").addColumn("weight").addColumn("yellow_fever").build();
//
//                downloadAndProcessDataset("vaccination.zip", "vaccination.csv", () -> dao.downloadVaccination(authorizationHeader), Vaccination.class, vaccinationSchema, vaccinationCounts, vac);
//
//                // Reset counts to 0 when the button is clicked
//                resetCountOnClick(ses);
//                resetCountOnClick(vac);
//            }
//
//            private <T> void downloadAndProcessDataset(String zipFileName, String extractedFileName, Supplier<Call<ResponseBody>> downloadCallSupplier, Class<T> entityClass, CsvSchema schema, AtomicLong countKey, String files) {
//                textView_Sync.setText("Downloading " + files + " Dataset");
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
//                                        break;
//                                    }
//                                    zipEntry = zipInputStream.getNextEntry();
//                                }
//                                zipInputStream.close();
//
//                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
//
//                                // Use the appropriate DAO based on the entity class
//                                if (entityClass.equals(HdssSociodemo.class)) {
//                                    hdssSociodemoDao = appDatabase.hdssSociodemoDao();
//                                } else if (entityClass.equals(Vaccination.class)) {
//                                    vaccinationDao = appDatabase.vaccinationDao();
//                                }
//
//                                // Import the unzipped CSV file into the Room database
//                                if (hdssSociodemoDao != null || vaccinationDao != null) {
//                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
//                                    CsvMapper mapper = new CsvMapper();
//                                    MappingIterator<T> iterator = mapper.readerFor(entityClass).with(schema).readValues(unzippedFile);
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
//                                                    if (entityClass.equals(HdssSociodemo.class)) {
//                                                        hdssSociodemoDao.insert((HdssSociodemo[]) entities.toArray(new HdssSociodemo[0]));
//                                                        //hdssSociodemoDao.insert((List<HdssSociodemo>) entities);
//                                                    } else if (entityClass.equals(Vaccination.class)) {
//                                                        vaccinationDao.insert((Vaccination[]) entities.toArray(new Vaccination[0]));
//                                                        //vaccinationDao.insert((List<Vaccination>) entities);
//                                                    }
//                                                    entities.clear();
//                                                    batchCount = 0;
//                                                }
//
//                                                runOnUiThread(() -> {
//                                                    long currentCount = counts.incrementAndGet();
//                                                    int progress = (int) (((double) currentCount / totalRecords[0]) * 100);
//
//                                                    // Update UI every 500 records (you can adjust this value)
//                                                    if (currentCount % 500 == 0 || currentCount == totalRecords[0]) {
//                                                        runOnUiThread(() -> {
//                                                            textView_Sync.setText("Saving " + currentCount + " of " + files);
//                                                            progressBar.setProgress(progress);
//                                                        });
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        if (batchCount > 0) {
//                                            if (entityClass.equals(HdssSociodemo.class)) {
//                                                //hdssSociodemoDao.insert((List<HdssSociodemo>) entities);
//                                                hdssSociodemoDao.insert((HdssSociodemo[]) entities.toArray(new HdssSociodemo[0]));
//                                            } else if (entityClass.equals(Vaccination.class)) {
//                                                //vaccinationDao.insert((List<Vaccination>) entities);
//                                                vaccinationDao.insert((Vaccination[]) entities.toArray(new Vaccination[0]));
//                                            }
//                                        }
//                                        runOnUiThread(() -> {
//                                            long finalCount = counts.get();
//                                            int finalProgress = (int) (((double) finalCount / totalRecords[0]) * 100);
//                                            textView_Sync.setText("Successful Download of " + files);
//                                            textView_Sync.setTextColor(Color.parseColor("#32CD32"));
//                                            progressBar.setProgress(finalProgress);
//
//                                            saveCountsToSharedPreferences(files, finalCount);
//
//                                            // Update the synchronization date
//                                            final AtomicReference<String> SesDatetime = new AtomicReference<>(getSesSyncDatetime());
//
//                                            // Inside the synchronization process after a successful sync
//                                            // Only change the date if there is another sync
//                                            String currentDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//                                            if (!currentDateTime.equals(SesDatetime.get())) {
//                                                syncsDate.setText(currentDateTime);
//                                                SesDatetime.set(currentDateTime);
//                                                setLastSesDatetime(currentDateTime);
//                                            }
//
//                                            // Delete files after successful processing
//                                            deleteFiles(getExternalCacheDir());
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
//                        //textView_Syncses.setTextColor(Color.RED);
//                    }
//                });
//
//            }
//            // Delete files in a directory
//            private void deleteFiles(File directory) {
//                if (directory != null && directory.isDirectory()) {
//                    File[] files = directory.listFiles();
//                    for (File file : files) {
//                        String fileName = file.getName();
//                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
//                            file.delete();
//                        }
//                    }
//                }
//            }
//        });




//        //Sync Zipped Vaccination
//        final Button button_DownloadVac = findViewById(R.id.button_SyncVac);
//        final TextView textView_SyncVac = findViewById(R.id.textView_SyncVac);
//        button_DownloadVac.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                progress.show();
//                progress.setMessage("Downloading Vaccination...");
//
//                // Specify the file names
//                String zipFileName = "vaccination.zip";
//                String extractedFileName = "vaccination.csv";
//
//                // File path for the downloaded location file
//                File file = new File(getExternalCacheDir(), zipFileName);
//
//                // Check if the file already exists
//                if (!file.exists()) {
//                    // File doesn't exist, proceed with download, unzip, and insert
//
//                    Call<ResponseBody> call = dao.downloadVaccination();
//                    call.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            //progress.show();
//                            if (response.isSuccessful()) {
//                                try {
//                                    // Read the response body into a file
//                                    InputStream inputStream = response.body().byteStream();
//                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//                                    byte[] buffer = new byte[1024];
//                                    int read;
//                                    while ((read = inputStream.read(buffer)) != -1) {
//                                        fileOutputStream.write(buffer, 0, read);
//                                    }
//                                    fileOutputStream.close();
//
//                                    // Unzip the file
//                                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
//                                    ZipEntry zipEntry = zipInputStream.getNextEntry();
//                                    while (zipEntry != null) {
//                                        String fileName = zipEntry.getName();
//                                        if (fileName.equals(extractedFileName)) {
//                                            File newFile = new File(getExternalCacheDir() + File.separator + fileName);
//                                            FileOutputStream fos = new FileOutputStream(newFile);
//                                            int len;
//                                            while ((len = zipInputStream.read(buffer)) > 0) {
//                                                fos.write(buffer, 0, len);
//                                            }
//                                            fos.close();
//                                            break;
//                                        }
//                                        zipEntry = zipInputStream.getNextEntry();
//                                    }
//                                    zipInputStream.close();
//
//                                    AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
//                                    vaccinationDao = appDatabase.vaccinationDao();
//                                    // Import the unzipped CSV file into the Room database
//                                    if (vaccinationDao != null) {
//                                        File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
//                                        CsvMapper mapper = new CsvMapper();
//                                        CsvSchema schema = CsvSchema.builder().addColumn("individual_uuid").addColumn("admission")
//                                                .addColumn("admitDate").addColumn("arti").addColumn("artitreat").addColumn("bcg").addColumn("bednet")
//                                                .addColumn("chlbednet").addColumn("diarrhoea").addColumn("diarrhoeatreat").addColumn("dob")
//                                                .addColumn("dpt_hepb_hib1").addColumn("dpt_hepb_hib2").addColumn("dpt_hepb_hib3").addColumn("fever")
//                                                .addColumn("fevertreat").addColumn("fw_uuid").addColumn("hcard").addColumn("hl").addColumn("hod")
//                                                .addColumn("hom").addColumn("insertDate").addColumn("ipv").addColumn("itn").addColumn("location_uuid")
//                                                .addColumn("measles_rubella1").addColumn("measles_rubella2").addColumn("menA").addColumn("muac")
//                                                .addColumn("nhis").addColumn("onet").addColumn("opv0").addColumn("opv1").addColumn("opv2")
//                                                .addColumn("opv3").addColumn("pneumo1").addColumn("pneumo2").addColumn("pneumo3").addColumn("rea")
//                                                .addColumn("rea_oth").addColumn("reason").addColumn("reason_oth").addColumn("rota1").addColumn("rota2")
//                                                .addColumn("rota3").addColumn("rtss18").addColumn("rtss6").addColumn("rtss7").addColumn("rtss9")
//                                                .addColumn("sbf").addColumn("scar").addColumn("slpbednet").addColumn("socialgroup_uuid")
//                                                .addColumn("stm").addColumn("sty").addColumn("uuid").addColumn("vitaminA12").addColumn("vitaminA18")
//                                                .addColumn("vitaminA6").addColumn("weight").addColumn("yellow_fever").addColumn("sttime").addColumn("edtime").addColumn("editDate").build();
//                                        MappingIterator<Vaccination> iterator = mapper.readerFor(Vaccination.class).with(schema).readValues(unzippedFile);
//                                        progress.setCancelable(false);
//                                        progress.setCanceledOnTouchOutside(false);
//                                        progress.show();
//                                        AtomicInteger counts = new AtomicInteger();
//                                        AppDatabase.databaseWriteExecutor.execute(() -> {
//                                            int batchSize = 5000;
//                                            List<Vaccination> vaccinations = new ArrayList<>();
//                                            int batchCount = 0;
//                                            while (iterator.hasNext()) {
//                                                Vaccination vaccination = iterator.next();
//                                                if (vaccination != null) {
//                                                    runOnUiThread(new Runnable() {
//                                                        public void run() {
//                                                            progress.setMessage("Saving " + counts.incrementAndGet() + " of the Vaccination");
//                                                        }
//                                                    });
//                                                    vaccinations.add(vaccination);
//                                                    batchCount++;
//                                                    if (batchCount == batchSize) {
//                                                        vaccinationDao.insert(vaccinations);
//                                                        vaccinations.clear();
//                                                        batchCount = 0;
//                                                    }
//                                                }
//                                            }
//                                            if (batchCount > 0) {
//                                                vaccinationDao.insert(vaccinations);
//                                            }
//                                            progress.dismiss();
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    textView_SyncVac.setText("Total Vaccination Saved: " + counts);
//                                                    textView_SyncVac.setTextColor(Color.parseColor("#32CD32"));
//                                                }
//                                            });
//
//                                        });
//                                    }
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            // Show error message
//                            progress.dismiss();
//                            textView_SyncVac.setText("Vaccination Download Error! Retry or Contact Administrator");
//                            textView_SyncVac.setTextColor(Color.RED);
//                        }
//                    });
//                } else {
//                    // File already exists, proceed with unzipping and inserting the data
//                    try {
//                        // Unzip the file
//                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
//                        ZipEntry zipEntry = zipInputStream.getNextEntry();
//                        while (zipEntry != null) {
//                            String fileName = zipEntry.getName();
//                            if (fileName.equals(extractedFileName)) {
//                                File newFile = new File(getExternalCacheDir() + File.separator + fileName);
//                                FileOutputStream fos = new FileOutputStream(newFile);
//                                byte[] buffer = new byte[1024];
//                                int len;
//                                while ((len = zipInputStream.read(buffer)) > 0) {
//                                    fos.write(buffer, 0, len);
//                                }
//                                fos.close();
//                                break;
//                            }
//                            zipEntry = zipInputStream.getNextEntry();
//                        }
//                        zipInputStream.close();
//
//                        // Insert the unzipped data into the Room database
//                        AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
//                        vaccinationDao = appDatabase.vaccinationDao();
//
//                        // Import the unzipped CSV file into the Room database
//                        if (vaccinationDao != null) {
//                            File unzippedFile = new File(getExternalCacheDir() + File.separator + extractedFileName);
//                            CsvMapper mapper = new CsvMapper();
//                            CsvSchema schema = CsvSchema.builder().addColumn("individual_uuid").addColumn("admission")
//                                    .addColumn("bcg").addColumn("dob").addColumn("dpt_hepb_hib1").addColumn("dpt_hepb_hib2")
//                                    .addColumn("dpt_hepb_hib3").addColumn("fw_uuid").addColumn("hcard").addColumn("hl").addColumn("hod")
//                                    .addColumn("hom").addColumn("insertDate").addColumn("ipv").addColumn("itn").addColumn("location_uuid")
//                                    .addColumn("measles_rubella1").addColumn("measles_rubella2").addColumn("menA").addColumn("nhis")
//                                    .addColumn("onet").addColumn("opv0").addColumn("opv1").addColumn("opv2").addColumn("opv3")
//                                    .addColumn("pneumo1").addColumn("pneumo2").addColumn("pneumo3").addColumn("rea").addColumn("rea_oth")
//                                    .addColumn("reason").addColumn("reason_oth")
//                                    .addColumn("rota1").addColumn("rota2").addColumn("rota3").addColumn("rtss18").addColumn("rtss6")
//                                    .addColumn("rtss7").addColumn("rtss9").addColumn("sbf").addColumn("socialgroup_uuid").addColumn("stm")
//                                    .addColumn("sty").addColumn("uuid").addColumn("vitaminA12").addColumn("vitaminA18")
//                                    .addColumn("vitaminA6").addColumn("yellow_fever").addColumn("sttime").addColumn("edtime").addColumn("editDate").build();
//                            MappingIterator<Vaccination> iterator = mapper.readerFor(Vaccination.class).with(schema).readValues(unzippedFile);
//                            progress.setCancelable(false);
//                            progress.setCanceledOnTouchOutside(false);
//                            progress.show();
//                            AtomicInteger counts = new AtomicInteger();
//                            AppDatabase.databaseWriteExecutor.execute(() -> {
//                                int batchSize = 5000;
//                                List<Vaccination> vaccinations = new ArrayList<>();
//                                int batchCount = 0;
//                                while (iterator.hasNext()) {
//                                    Vaccination vaccination = iterator.next();
//                                    if (vaccination != null) {
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                progress.setMessage("Saving " + counts.incrementAndGet() + " of the Vaccination");
//                                            }
//                                        });
//                                        vaccinations.add(vaccination);
//                                        batchCount++;
//                                        if (batchCount == batchSize) {
//                                            vaccinationDao.insert(vaccinations);
//                                            vaccinations.clear();
//                                            batchCount = 0;
//                                        }
//                                    }
//                                }
//                                if (batchCount > 0) {
//                                    vaccinationDao.insert(vaccinations);
//                                }
//                                progress.dismiss();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        textView_SyncVac.setText("Total Vaccination Saved: " + counts);
//                                        textView_SyncVac.setTextColor(Color.parseColor("#32CD32"));
//                                    }
//                                });
//
//                            });
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

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