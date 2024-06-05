package org.openhds.hdsscapture;

import static org.openhds.hdsscapture.AppConstants.DATA_CAPTURE;
import static org.openhds.hdsscapture.AppConstants.DATA_DOWNLOAD;
import static org.openhds.hdsscapture.AppConstants.DATA_QUERY;
import static org.openhds.hdsscapture.AppConstants.DATA_REPORT;
import static org.openhds.hdsscapture.AppConstants.DATA_SYNC;
import static org.openhds.hdsscapture.AppConstants.DATA_VIEWS;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.QueryActivity;
import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.fragment.InfoFragment;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button send;
    private int status;
    private ProgressDialog progressDialog;
    private TextView liveDateTimeTextView;
    private TextView wks;
    private String fw;
    private String fws;
    private ApiDao dao;
    private ProgressDialog progress;

    private DeathViewModel deathViewModel;
    private InmigrationViewModel inmigrationViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private DemographicViewModel demographicViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
    private PregnancyViewModel pregnancyViewModel;
    private RelationshipViewModel relationshipViewModel;
    private Button reject;
    private  SharedPreferences preferences;
    private String authorizationHeader;

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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

    private void checkLocationStatusAndShowPrompt() {
        if (isLocationEnabled(this)) {
            // Location is enabled, proceed with your logic
        } else {
            // Location is not enabled, show prompt
            showLocationSettingsPrompt();
        }
    }

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog simpleDialog = SimpleDialog.newInstance(message, codeFragment);
        simpleDialog.show(getSupportFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);

        //Settings Download API USERNAME PASSWORD
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String authorizationHeader = preferences.getString("authorizationHeader", null);

        // Initialize the last sync datetime when the activity is created
        String lastSyncDatetime = getLastSyncDatetime();
        if (lastSyncDatetime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            lastSyncDatetime = sdf.format(new Date());
            setLastSyncDatetime(lastSyncDatetime);
        }

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        progress = new ProgressDialog(MainActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);

        //Sync LocationHierarchy and Settings
        final Button syncSettings = findViewById(R.id.button_settings);
        syncSettings.setOnClickListener(v -> {

            progress.show();
            //when region is synched, sync Hierarchy
            progress.setMessage("Updating Hierarchy...");
            final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(MainActivity.this).get(HierarchyViewModel.class);
            Call<DataWrapper<Hierarchy>> c_callable = dao.getAllHierarchy(authorizationHeader);
            c_callable.enqueue(new Callback<DataWrapper<Hierarchy>>() {
                @Override
                public void onResponse(Call<DataWrapper<Hierarchy>> call, Response<DataWrapper<Hierarchy>> response) {
                    Hierarchy[] hierarchies = response.body().getData().toArray(new Hierarchy[0]);
                    hierarchyViewModel.add(hierarchies);


                    //Sync Round
                    progress.setMessage("Updating Round...");
                    final RoundViewModel round = new ViewModelProvider(MainActivity.this).get(RoundViewModel.class);
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
                            progress.setMessage("Updating Codebook...");
                            final CodeBookViewModel codeBook = new ViewModelProvider(MainActivity.this).get(CodeBookViewModel.class);
                            Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook(authorizationHeader);
                            c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                                @Override
                                public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                                    CodeBook[] co = response.body().getData().toArray(new CodeBook[0]);
                                    codeBook.add(co);

                                    //Sync Fieldworker
                                    progress.setMessage("Updating Fieldworker...");
                                    final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(MainActivity.this).get(FieldworkerViewModel.class);
                                    Call<DataWrapper<Fieldworker>> c_callable = dao.getFw(authorizationHeader);
                                    c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                                        @Override
                                        public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                                            Fieldworker[] fw = response.body().getData().toArray(new Fieldworker[0]);
                                            fieldworkerViewModel.add(fw);

                                            //Sync Community
                                            final CommunityViewModel communityViewModel = new ViewModelProvider(MainActivity.this).get(CommunityViewModel.class);
                                            Call<DataWrapper<CommunityReport>> c_callable = dao.getCommunity(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<CommunityReport>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<CommunityReport>> call, Response<DataWrapper<CommunityReport>> response) {
                                                    CommunityReport[] cm = response.body().getData().toArray(new CommunityReport[0]);
                                                    communityViewModel.add(cm);

                                            //Sync Settings
                                            progress.setMessage("Updating Settings...");
                                            final ConfigViewModel configViewModel = new ViewModelProvider(MainActivity.this).get(ConfigViewModel.class);
                                            Call<DataWrapper<Configsettings>> c_callable = dao.getConfig(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<Configsettings>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<Configsettings>> call, Response<DataWrapper<Configsettings>> response) {
                                                    Configsettings[] cng = response.body().getData().toArray(new Configsettings[0]);
                                                    configViewModel.add(cng);

                                                    progress.dismiss();
                                                    syncSettings.setText("Sync Successful");
                                                    syncSettings.setTextColor(Color.parseColor("#32CD32"));

                                                }


                                                @Override
                                                public void onFailure(Call<DataWrapper<Configsettings>> call, Throwable t) {
                                                    progress.dismiss();
                                                    syncSettings.setText("Settings Sync Error!");
                                                    syncSettings.setTextColor(Color.RED);
                                                }
                                            });
                                        }

                                                @Override
                                                public void onFailure(Call<DataWrapper<CommunityReport>> call, Throwable t) {
                                                    progress.dismiss();
                                                    syncSettings.setText("Community Report Sync Error!");
                                                    syncSettings.setTextColor(Color.RED);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {
                                            progress.dismiss();
                                            syncSettings.setText("Fieldworker Sync Error!");
                                            syncSettings.setTextColor(Color.RED);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {
                                    progress.dismiss();
                                    syncSettings.setText("Codebook Sync Error!");
                                    syncSettings.setTextColor(Color.RED);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<DataWrapper<Round>> call, Throwable t) {
                            progress.dismiss();
                            syncSettings.setText("Round Sync Error!");
                            syncSettings.setTextColor(Color.RED);
                        }
                    });
                }


                @Override
                public void onFailure(Call<DataWrapper<Hierarchy>> call, Throwable t) {
                    progress.dismiss();
                    syncSettings.setText("Hierarchy Sync Error!");
                    syncSettings.setTextColor(Color.RED);
                }
            });


        });


        checkLocationStatusAndShowPrompt();
        wks = findViewById(R.id.textInMiddle);
        countRejected();
        startDownloadProcess();

        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
        fw = fieldworkerDatas.getUsername();
        fws = fieldworkerDatas.getFw_uuid();
        status = (fieldworkerDatas != null) ? fieldworkerDatas.getStatus() : 0;  // Default to 0 or another appropriate value
        calculatePercentage();
        //Toast.makeText(MainActivity.this, "Welcome " + fieldworkerDatas.firstName + " " + fieldworkerDatas.lastName, Toast.LENGTH_LONG).show();
        //Toast.makeText(MainActivity.this, "Welcome " + status, Toast.LENGTH_LONG).show();


        send = findViewById(R.id.btnpush);

        final Button update = findViewById(R.id.btnupdate);
        update.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),HierarchyActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        send.setOnClickListener(v -> {
            if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 1) {
                Intent i = new Intent(getApplicationContext(), PushActivity.class);
                startActivity(i);
            } else {
                // Display a message or take appropriate action when the condition is not met
                Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        });

        final Button pull = findViewById(R.id.btnpull);
        pull.setOnClickListener(v -> {
            if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 2) {
                Intent i = new Intent(getApplicationContext(), PullActivity.class);
                startActivity(i);
            } else {
                // Display a message or take appropriate action when the condition is not met
                Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        });

        final Button control = findViewById(R.id.btnreport);
        control.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ReportActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        final Button query = findViewById(R.id.btnquerry);
        query.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), QueryActivity.class);
            startActivity(i);
        });

        final Button views = findViewById(R.id.btnloc);
        views.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), NewActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

//        reject = findViewById(R.id.btnReject);
//        reject.setText("REJECTIONS");
//        reject.setOnClickListener(v -> {
//            Intent i = new Intent(getApplicationContext(), RejectionsActivity.class);
//            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
//            startActivity(i);
//        });

        final Button info = findViewById(R.id.btninfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 2) {
                    InfoFragment dialogFragment = new InfoFragment();
                    dialogFragment.show(getSupportFragmentManager(), "InfoFragment");
                } else {
                    Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button buttonUrl = findViewById(R.id.button_url);
        buttonUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/arn-techsystem/openAB/releases"); // replace with your URL
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        final Button buttonReset = findViewById(R.id.button_resetDatabase);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 2) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to reset the database?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "Yes"
                                    resetDatabase();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "No"
                                    // Do nothing or perform any desired action
                                }
                            })
                            .show();
                }else {
                    Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void startAppInfo(View view) {
        showDialogInfo(null, DATA_CAPTURE);
    }
    public void startSyncInfo(View view) {
        showDialogInfo(null, DATA_SYNC);
    }
    public void startReportInfo(View view) {
        showDialogInfo(null, DATA_REPORT);
    }
    public void startQueryInfo(View view) {
        showDialogInfo(null, DATA_QUERY);
    }
    public void startViewInfo(View view) {
        showDialogInfo(null, DATA_VIEWS);
    }
    public void startDownloadInfo(View view) {
        showDialogInfo(null, DATA_DOWNLOAD);
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
                            MainActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void resetDatabase() {
        // Get the context of the application
        final Context context = getApplicationContext();
        // Initialize and show the ProgressDialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Resetting database...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Reset the AppDatabase
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.resetDatabase(context, new AppDatabase.ResetCallback() {
            @Override
            public void onResetComplete() {
                // Dismiss the ProgressDialog
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Database reset successful", Toast.LENGTH_SHORT).show();

                        // Start the LoginActivity
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);

                        // Finish the current MainActivity if necessary
                        finish();
                    }
                });
            }
        });
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return isGpsEnabled || isNetworkEnabled;
        }
        return false;
    }

    private void showLocationSettingsPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Disabled");
        builder.setMessage("Please enable location for this app in your device settings to pick GPS");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            // Open app settings
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        // Set dialog not closable
        builder.setCancelable(false);
        builder.show();
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle cancel button click
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculatePercentage();
        countRejected();
        startDownloadProcess();
        if (!isLocationEnabled(this)) {
            showLocationSettingsPrompt();
        }
    }

    private void calculatePercentage() {
        LocationViewModel viewModels = new ViewModelProvider(this).get(LocationViewModel.class);

        try {
            long totalLocations = viewModels.work(fw);
            long editedLocations = viewModels.works(fw);

            if (totalLocations > 0) {
                // Calculate the percentage of edited locations
                double percentage = ((double) editedLocations / totalLocations) * 100;

                // Format the percentage to display two decimal places
                String formattedPercentage = String.format(Locale.getDefault(), "%.2f", percentage);

                ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
                circularProgressBar.setMax((int) totalLocations);
                circularProgressBar.setProgress((int) Math.min(editedLocations, totalLocations));
                wks.setText(formattedPercentage + "% ");

                // Now you have the percentage value, and you can use it as needed
                // For example, you can log it or display it in your app
                Log.d("Percentage", "Percentage of edited locations: " + percentage);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void countRejected() {

        try {
            long totalImg = inmigrationViewModel.rej(fws);
            long totalOmg = outmigrationViewModel.rej(fws);
            long totalPre = pregnancyViewModel.rej(fws);
            long totalOut = pregnancyoutcomeViewModel.rej(fws);
            long totalDem = demographicViewModel.rej(fws);
            long totalDth = deathViewModel.rej(fws);
            long totalRel = relationshipViewModel.rej(fws);

            long totalRejected = totalImg + totalOmg + totalPre + totalOut + totalDem + totalDth + totalRel;

            final Intent f = getIntent();
            final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

            // Update the button text
            reject = findViewById(R.id.btnReject);
            reject.setText("REJECTIONS " + "(" +totalRejected + ")");

            // Set the OnClickListener
            reject.setOnClickListener(v -> {
                Intent i = new Intent(getApplicationContext(), RejectionsActivity.class);
                i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
                startActivity(i);
            });

            //reject.setText("REJECTED " + totalRejected);
            Log.d("MainActivity", "Rejections " + totalRejected);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void startDownloadProcess() {

        if (!isInternetAvailable()) {
            return;
        }

        final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(MainActivity.this).get(InmigrationViewModel.class);
        Call<DataWrapper<Inmigration>> c_callable = dao.getImg(authorizationHeader);
        c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
            @Override
            public void onResponse(Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
                Inmigration[] img = response.body().getData().toArray(new Inmigration[0]);
                inmigrationViewModel.add(img);

                // Next Step: Outmigration
                final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(MainActivity.this).get(OutmigrationViewModel.class);
                Call<DataWrapper<Outmigration>> c_callable = dao.getOmg(authorizationHeader);
                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
                    @Override
                    public void onResponse(Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
                        Outmigration[] i = response.body().getData().toArray(new Outmigration[0]);
                        outmigrationViewModel.add(i);

                        // Next Step: Death
                        final DeathViewModel deathViewModel = new ViewModelProvider(MainActivity.this).get(DeathViewModel.class);
                        Call<DataWrapper<Death>> c_callable = dao.getDth(authorizationHeader);
                        c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                            @Override
                            public void onResponse(Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                                Death[] co = response.body().getData().toArray(new Death[0]);
                                deathViewModel.add(co);

                                // Next Step: Pregnancy
                                final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(MainActivity.this).get(PregnancyViewModel.class);
                                Call<DataWrapper<Pregnancy>> c_callable = dao.getPreg(authorizationHeader);
                                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                                    @Override
                                    public void onResponse(Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
                                        Pregnancy[] fw = response.body().getData().toArray(new Pregnancy[0]);
                                        pregnancyViewModel.add(fw);

                                                // Next Step: Demographic
                                        final DemographicViewModel demographicViewModel = new ViewModelProvider(MainActivity.this).get(DemographicViewModel.class);
                                        Call<DataWrapper<Demographic>> c_callable = dao.getDemo(authorizationHeader);
                                        c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
                                            @Override
                                            public void onResponse(Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
                                                Demographic[] dm = response.body().getData().toArray(new Demographic[0]);
                                                demographicViewModel.add(dm);

                                                // Next Step: Relationship
                                                final RelationshipViewModel relationshipViewModel = new ViewModelProvider(MainActivity.this).get(RelationshipViewModel.class);
                                                Call<DataWrapper<Relationship>> c_callable = dao.getRel(authorizationHeader);
                                                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                                                    @Override
                                                    public void onResponse(Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
                                                        Relationship[] rel = response.body().getData().toArray(new Relationship[0]);
                                                        relationshipViewModel.add(rel);

                                                        // Final Step: Pregnancy Outcome
                                                        final PregnancyoutcomeViewModel pregout = new ViewModelProvider(MainActivity.this).get(PregnancyoutcomeViewModel.class);
                                                        Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.getOut(authorizationHeader);
                                                        c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                                                            @Override
                                                            public void onResponse(Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
                                                                Pregnancyoutcome[] cng = response.body().getData().toArray(new Pregnancyoutcome[0]);
                                                                pregout.add(cng);
                                                            }

                                                            @Override
                                                            public void onFailure(Call<DataWrapper<Pregnancyoutcome>> call, Throwable t) {
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(Call<DataWrapper<Relationship>> call, Throwable t) {
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<DataWrapper<Demographic>> call, Throwable t) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<DataWrapper<Pregnancy>> call, Throwable t) {
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<DataWrapper<Death>> call, Throwable t) {
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DataWrapper<Outmigration>> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<DataWrapper<Inmigration>> call, Throwable t) {
            }
        });
    }


}