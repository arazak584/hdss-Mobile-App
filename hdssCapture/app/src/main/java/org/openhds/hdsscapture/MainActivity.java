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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
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
    private ApiDao dao;

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

        //Sync LocationHierarchy and Settings
        final Button syncSettings = findViewById(R.id.button_settings);
        syncSettings.setOnClickListener(v -> {

            final TextView textView_SyncHierarchyData = findViewById(R.id.syncCodebookMessage);
            final ProgressBar progressBar = findViewById(R.id.codebookProgressBar);
            textView_SyncHierarchyData.setText("");
            progressBar.setProgress(0);

            //when region is synched, sync Hierarchy
            textView_SyncHierarchyData.setText("Updating Hierarchy...");
            final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(MainActivity.this).get(HierarchyViewModel.class);
            Call<DataWrapper<Hierarchy>> c_callable = dao.getAllHierarchy(authorizationHeader);
            c_callable.enqueue(new Callback<DataWrapper<Hierarchy>>() {
                @Override
                public void onResponse(Call<DataWrapper<Hierarchy>> call, Response<DataWrapper<Hierarchy>> response) {
                    Hierarchy[] hierarchies = response.body().getData().toArray(new Hierarchy[0]);
                    hierarchyViewModel.add(hierarchies);


                    //Sync Round
                    textView_SyncHierarchyData.setText("Updating Round...");
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
                            final CodeBookViewModel codeBook = new ViewModelProvider(PullActivity.this).get(CodeBookViewModel.class);
                            Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook(authorizationHeader);
                            c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                                @Override
                                public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                                    CodeBook[] co = response.body().getData().toArray(new CodeBook[0]);
                                    codeBook.add(co);

                                    //Sync Fieldworker
                                    textView_SyncHierarchyData.setText("Updating Fieldworker...");
                                    final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(PullActivity.this).get(FieldworkerViewModel.class);
                                    Call<DataWrapper<Fieldworker>> c_callable = dao.getFw(authorizationHeader);
                                    c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                                        @Override
                                        public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                                            Fieldworker[] fw = response.body().getData().toArray(new Fieldworker[0]);
                                            fieldworkerViewModel.add(fw);

                                            //Sync Settings
                                            textView_SyncHierarchyData.setText("Updating Settings...");
                                            final ConfigViewModel configViewModel = new ViewModelProvider(PullActivity.this).get(ConfigViewModel.class);
                                            Call<DataWrapper<Configsettings>> c_callable = dao.getConfig(authorizationHeader);
                                            c_callable.enqueue(new Callback<DataWrapper<Configsettings>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<Configsettings>> call, Response<DataWrapper<Configsettings>> response) {
                                                    Configsettings[] cng = response.body().getData().toArray(new Configsettings[0]);
                                                    configViewModel.add(cng);
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
                                                    progressBar.setProgress(0);
                                                    textView_SyncHierarchyData.setText("Settings Sync Error!");
                                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                                }
                                            });
                                        }



                                        @Override
                                        public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {
                                            progressBar.setProgress(0);
                                            textView_SyncHierarchyData.setText("Fieldworker Sync Error!");
                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {
                                    progressBar.setProgress(0);
                                    textView_SyncHierarchyData.setText("Codebook Sync Error!");
                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<DataWrapper<Round>> call, Throwable t) {
                            progressBar.setProgress(0);
                            textView_SyncHierarchyData.setText("Round Sync Error!");
                            textView_SyncHierarchyData.setTextColor(Color.RED);
                        }
                    });
                }


                @Override
                public void onFailure(Call<DataWrapper<Hierarchy>> call, Throwable t) {
                    progressBar.setProgress(0);
                    textView_SyncHierarchyData.setText("Hierarchy Sync Error!");
                    textView_SyncHierarchyData.setTextColor(Color.RED);
                }
            });


        });


        checkLocationStatusAndShowPrompt();
        wks = findViewById(R.id.textInMiddle);

        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
        fw = fieldworkerDatas.getUsername();
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


}