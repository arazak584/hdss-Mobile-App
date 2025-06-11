package org.openhds.hdsscapture;

import static org.openhds.hdsscapture.AppConstants.DATA_CAPTURE;
import static org.openhds.hdsscapture.AppConstants.DATA_DOWNLOAD;
import static org.openhds.hdsscapture.AppConstants.DATA_MAP;
import static org.openhds.hdsscapture.AppConstants.DATA_QUERY;
import static org.openhds.hdsscapture.AppConstants.DATA_REJECT;
import static org.openhds.hdsscapture.AppConstants.DATA_REPORT;
import static org.openhds.hdsscapture.AppConstants.DATA_SCHEDULE;
import static org.openhds.hdsscapture.AppConstants.DATA_SYNC;
import static org.openhds.hdsscapture.AppConstants.DATA_VIEWS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.MapActivity;
import org.openhds.hdsscapture.Activity.QueryActivity;
import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.ViewActivity;
import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.Activity.RemainderActivity;
import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyLevelViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.fragment.InfoFragment;
import org.openhds.hdsscapture.odk.OdkForm;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private VaccinationViewModel vaccinationViewModel;
    private MorbidityViewModel morbidityViewModel;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    // Fallback to general settings if the specific one fails
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            } else {
                //Toast.makeText(this, "All Files Access already granted", Toast.LENGTH_SHORT).show();
            }
        }


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
        vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        morbidityViewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);

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

                                                    //Sync HierarchyLevel
                                                    final HierarchyLevelViewModel hieViewModel = new ViewModelProvider(MainActivity.this).get(HierarchyLevelViewModel.class);
                                                    Call<DataWrapper<HierarchyLevel>> c_callable = dao.getHierarchyLevel(authorizationHeader);
                                                    c_callable.enqueue(new Callback<DataWrapper<HierarchyLevel>>() {
                                                        @Override
                                                        public void onResponse(Call<DataWrapper<HierarchyLevel>> call, Response<DataWrapper<HierarchyLevel>> response) {
                                                        HierarchyLevel[] hie = response.body().getData().toArray(new HierarchyLevel[0]);
                                                        hieViewModel.add(hie);

                                                      //Sync ODK
                                                    final OdkViewModel odkViewModel = new ViewModelProvider(MainActivity.this).get(OdkViewModel.class);
                                                    Call<DataWrapper<OdkForm>> c_callable = dao.getOdk(authorizationHeader);
                                                    c_callable.enqueue(new Callback<DataWrapper<OdkForm>>() {
                                                        @Override
                                                        public void onResponse(Call<DataWrapper<OdkForm>> call, Response<DataWrapper<OdkForm>> response) {
                                                        OdkForm[] odk = response.body().getData().toArray(new OdkForm[0]);
                                                        odkViewModel.add(odk);

                                                        //Sync Settings
                                                        progress.setMessage("Updating Settings...");
                                                        final ConfigViewModel configViewModel = new ViewModelProvider(MainActivity.this).get(ConfigViewModel.class);
                                                        Call<DataWrapper<Configsettings>> c_callable = dao.getConfig(authorizationHeader);
                                                        c_callable.enqueue(new Callback<DataWrapper<Configsettings>>() {
                                                            @Override
                                                            public void onResponse(Call<DataWrapper<Configsettings>> call, Response<DataWrapper<Configsettings>> response) {
                                                                Configsettings[] cng = response.body().getData().toArray(new Configsettings[0]);
                                                                configViewModel.add(cng);


                                                                //Sync Listing
                                                                progress.setMessage("Updating Listing...");
                                                                final ListingViewModel listingViewModel = new ViewModelProvider(MainActivity.this).get(ListingViewModel.class);
                                                                Call<DataWrapper<Listing>> c_callable = dao.getListing(authorizationHeader);
                                                                c_callable.enqueue(new Callback<DataWrapper<Listing>>() {
                                                                    @Override
                                                                    public void onResponse(Call<DataWrapper<Listing>> call, Response<DataWrapper<Listing>> response) {
                                                                        try {
                                                                            if (response.body() != null && response.body().getData() != null) {
                                                                            Listing[] list = response.body().getData().toArray(new Listing[0]);
                                                                                for (Listing newList : list) {
                                                                                    // Fetch the existing pregnancy record by UUID
                                                                                    Listing existingListing = listingViewModel.find(newList.compno);
                                                                                    if (existingListing != null) {
                                                                                        // Preserve location and id fields from the existing record
                                                                                        newList.longitude = existingListing.longitude;
                                                                                        newList.latitude = existingListing.latitude;
                                                                                        newList.accuracy = existingListing.accuracy;
                                                                                        newList.altitude = existingListing.altitude;
                                                                                        newList.vill_extId = existingListing.vill_extId;
                                                                                        newList.cluster_id = existingListing.cluster_id;
                                                                                        newList.locationName = existingListing.locationName;
                                                                                        newList.correct_yn = existingListing.correct_yn;
                                                                                        newList.repl_locationName = existingListing.repl_locationName;
                                                                                        newList.edit_compno = 1;
                                                                                        newList.complete = 0;

                                                                                        Log.d("Insertion", "Listing insert: " + existingListing.compno);
                                                                                    }
                                                                                }
                                                                            listingViewModel.add(list);


                                                                            } else {
                                                                                Log.e("Error", "Response body or data is null");
                                                                            }

                                                                        } catch (ExecutionException e) {
                                                                            Log.e("Error", "ExecutionException occurred while fetching existing pregnancy: " + e.getMessage());
                                                                            e.printStackTrace();
                                                                        } catch (InterruptedException e) {
                                                                            Log.e("Error", "InterruptedException occurred while fetching existing pregnancy: " + e.getMessage());
                                                                            e.printStackTrace();
                                                                            Thread.currentThread().interrupt(); // Restore interrupted status
                                                                        } catch (Exception e) {
                                                                            Log.e("Error", "An unexpected error occurred: " + e.getMessage());
                                                                            e.printStackTrace();
                                                                        }


                                                                progress.dismiss();
                                                                syncSettings.setText("Sync Successful");
                                                                syncSettings.setTextColor(Color.parseColor("#32CD32"));

                                                            }

                                                                    @Override
                                                                    public void onFailure(Call<DataWrapper<Listing>> call, Throwable t) {
                                                                        progress.dismiss();
                                                                        syncSettings.setText("Listing Sync Error!");
                                                                        syncSettings.setTextColor(Color.RED);
                                                                    }
                                                                });
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
                                                        public void onFailure(Call<DataWrapper<OdkForm>> call, Throwable t) {
                                                            progress.dismiss();
                                                            syncSettings.setText("ODK Sync Error!");
                                                            syncSettings.setTextColor(Color.RED);
                                                            Log.d("ODK ERROR", "Error" + t);
                                                        }
                                                    });
                                                  }

                                                        @Override
                                                        public void onFailure(Call<DataWrapper<HierarchyLevel>> call, Throwable t) {
                                                            progress.dismiss();
                                                            syncSettings.setText("Hierarchy Level Sync Error!");
                                                            syncSettings.setTextColor(Color.RED);
                                                            Log.d("ODK ERROR", "Error" + t);
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
        //startDownloadProcess();

        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        // Retrieve login details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);
        fws = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);
        status = sharedPreferences.getInt(LoginActivity.FW_STATUS, 0);

        //status = (fieldworkerDatas != null) ? fieldworkerDatas.getStatus() : 0;  // Default to 0 or another appropriate value
        //calculatePercentage();
        //Toast.makeText(MainActivity.this, "Welcome " + fieldworkerDatas.firstName + " " + fieldworkerDatas.lastName, Toast.LENGTH_LONG).show();
        //Toast.makeText(MainActivity.this, "Welcome " + status, Toast.LENGTH_LONG).show();


        send = findViewById(R.id.btnpush);

        final Button update = findViewById(R.id.btnupdate);
        update.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),HierarchyActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        final Button map = findViewById(R.id.btnMap);
        map.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

//        final Button odk = findViewById(R.id.btnODK);
//        odk.setOnClickListener(v -> {
//            Intent i = new Intent(getApplicationContext(), ODKCsvActivity.class);
//            startActivity(i);
//        });

        send.setOnClickListener(v -> {
            if (status == 1) {
                Intent i = new Intent(getApplicationContext(), PushActivity.class);
                startActivity(i);
            } else {
                // Display a message or take appropriate action when the condition is not met
                Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        });

        final Button pull = findViewById(R.id.btnpull);
        pull.setOnClickListener(v -> {
            if (status == 2) {
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
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        final Button views = findViewById(R.id.btnloc);
        views.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ViewActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        final Button remainder = findViewById(R.id.btnSchedule);
        remainder.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), RemainderActivity.class);
            startActivity(i);
        });


        final Button info = findViewById(R.id.btninfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == 1) {
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
                Uri uri = Uri.parse("https://github.com/arn-techsystem/openAB/releases/download/v2023.0.1/app-debug.apk"); // replace with your URL
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        final Button buttonReset = findViewById(R.id.button_resetDatabase);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 2) {
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
    public void startRejectInfo(View view) {
        showDialogInfo(null, DATA_REJECT);
    }
    public void startScheduleInfo(View view) {
        showDialogInfo(null, DATA_SCHEDULE);
    }
    public void startMapInfo(View view) {
        showDialogInfo(null, DATA_MAP);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Start MainActivity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        // Finish the current activity
                        MainActivity.this.finish();
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
        runInBackground(this::calculatePercentage);  // Run calculatePercentage in background
        runInBackground(this::countRejected);  // Run countRejected in background

        if (!isLocationEnabled(this)) {
            showLocationSettingsPrompt();
        }
    }

    private void runInBackground(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }


    private void calculatePercentage() {
        // Create a new thread to perform the calculation in the background
        new Thread(() -> {
            LocationViewModel viewModels = new ViewModelProvider(this).get(LocationViewModel.class);

            try {
                long totalLocations = viewModels.work(fw);
                long editedLocations = viewModels.works(fw);

                if (totalLocations > 0) {
                    double percentage = ((double) editedLocations / totalLocations) * 100;
                    String formattedPercentage = String.format(Locale.getDefault(), "%.2f", percentage);

                    // Update the UI on the main thread
                    runOnUiThread(() -> {
                        ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
                        circularProgressBar.setMax((int) totalLocations);
                        circularProgressBar.setProgress((int) Math.min(editedLocations, totalLocations));
                        wks.setText(formattedPercentage + "% ");
                        Log.d("Percentage", "Percentage of edited locations: " + percentage);
                    });
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); // Start the thread
    }

    private void countRejected() {
        // Create a new thread to perform the counting in the background
        new Thread(() -> {
            try {
                long totalImg = inmigrationViewModel.rej(fws);
                long totalOmg = outmigrationViewModel.rej(fws);
                long totalPre = pregnancyViewModel.rej(fws);
                long totalOut = pregnancyoutcomeViewModel.rej(fws);
                long totalDem = demographicViewModel.rej(fws);
                long totalDth = deathViewModel.rej(fws);
                long totalRel = relationshipViewModel.rej(fws);
                long totalses = hdssSociodemoViewModel.rej(fws);
                long totalvac = vaccinationViewModel.rej(fws);
                long totalmor = morbidityViewModel.rej(fws);

                long totalRejected = totalImg + totalOmg + totalPre + totalOut + totalDem + totalDth + totalRel + totalses + totalvac + totalmor;

                // Get the Intent and the Fieldworker data
                final Intent f = getIntent();
                final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

                // Update the UI on the main thread
                runOnUiThread(() -> {
                    reject = findViewById(R.id.btnReject);
                    reject.setText("REJECTIONS " + "(" + totalRejected + ")");
                    reject.setOnClickListener(v -> {
                        Intent i = new Intent(getApplicationContext(), RejectionsActivity.class);
                        i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
                        startActivity(i);
                    });
                    Log.d("MainActivity", "Rejections " + totalRejected);
                });

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); // Start the thread
    }



//    private void countRejected() {
//        try {
//            long totalImg = inmigrationViewModel.rej(fws);
//            long totalOmg = outmigrationViewModel.rej(fws);
//            long totalPre = pregnancyViewModel.rej(fws);
//            long totalOut = pregnancyoutcomeViewModel.rej(fws);
//            long totalDem = demographicViewModel.rej(fws);
//            long totalDth = deathViewModel.rej(fws);
//            long totalRel = relationshipViewModel.rej(fws);
//            long totalses = hdssSociodemoViewModel.rej(fws);
//            long totalvac = vaccinationViewModel.rej(fws);
//            long totalmor = morbidityViewModel.rej(fws);
//
//            long totalRejected = totalImg + totalOmg + totalPre + totalOut + totalDem + totalDth + totalRel + totalses + totalvac + totalmor;
//
//            final Intent f = getIntent();
//            final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
//
//            // Update the UI on the main thread
//            runOnUiThread(() -> {
//                reject = findViewById(R.id.btnReject);
//                reject.setText("REJECTIONS " + "(" + totalRejected + ")");
//                reject.setOnClickListener(v -> {
//                    Intent i = new Intent(getApplicationContext(), RejectionsActivity.class);
//                    i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
//                    startActivity(i);
//                });
//                Log.d("MainActivity", "Rejections " + totalRejected);
//            });
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }




}