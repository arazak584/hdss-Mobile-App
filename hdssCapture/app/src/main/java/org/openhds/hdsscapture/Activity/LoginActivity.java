package org.openhds.hdsscapture.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Dialog.FilterDialogFragment;
import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.fragment.UrlFragment;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private Fieldworker fieldworkerDatas;
    private AppJson appJson;
    private TextView txtAppName;

    public static final String FIELDWORKER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.FIELDWORKER_DATA";
    public static final String FIELDWORKER_DATAS = "org.openhds.hdsscapture.activity.MainActivity.FIELDWORKER_DATAS";

    public static final String PREFS_NAME = "FieldworkerPrefs";
    public static final String FW_UUID_KEY = "fw_uuid";
    public static final String FW_USERNAME_KEY = "username";
    public static final String FW_STATUS = "status";
    public static final String FW_NAME = "fwname";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the app needs to request the POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Notifications are already enabled
                Log.d("NotificationPermission", "Notifications are already enabled.");
            }
        } else {
            // For Android versions below Tiramisu, no need to request POST_NOTIFICATIONS permission
            Log.d("NotificationPermission", "No need to request POST_NOTIFICATIONS permission for this Android version.");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        appJson = AppJson.getInstance(this);
        dao = appJson.getJsonApi();

        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

        final EditText username = findViewById(R.id.text_username);
        final EditText password = findViewById(R.id.text_password);

        progress = new ProgressDialog(LoginActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        txtAppName = findViewById(R.id.txtAppName);
        txtAppName.setText(getString(R.string.app_name) + " v" + getString(R.string.versions));

        final MaterialButton superv = findViewById(R.id.button_Supervisor);
        superv.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SupervisorActivity.class);
            startActivity(i);
        });

        final MaterialButton start = findViewById(R.id.btnLogin);
        start.setOnClickListener(v -> {
            if (TextUtils.isEmpty(username.getText().toString().trim())) {
                username.setError("Invalid user or PIN");
                Toast.makeText(this, "Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(password.getText().toString().trim())) {
                password.setError("Invalid user or PIN");
                Toast.makeText(this, "Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = username.getText().toString().trim();
            final String mypass = password.getText().toString().trim();

            try {
                fieldworkerDatas = fieldworkerViewModel.find(myuser, mypass);
            } catch (ExecutionException e) {
                Toast.makeText(this, "Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this, "Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if (fieldworkerDatas != null) {
                username.setError(null);
                password.setText(null);

                // Save fw_uuid to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(FW_UUID_KEY, fieldworkerDatas.fw_uuid);
                editor.putString(FW_USERNAME_KEY, fieldworkerDatas.username);
                editor.putInt(FW_STATUS, fieldworkerDatas.status);
                editor.putString(FW_NAME, fieldworkerDatas.firstName + " " + fieldworkerDatas.lastName);
                editor.apply();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra(FIELDWORKER_DATAS, fieldworkerDatas);
                startActivity(i);
            } else {
                username.setError("Invalid user or PIN");
                Toast.makeText(LoginActivity.this, "Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
            }
        });

        final MaterialButton send = findViewById(R.id.apiSettings);
        send.setOnClickListener(v -> {
            UrlFragment dialogFragment = new UrlFragment();
            dialogFragment.show(getSupportFragmentManager(), "UrlFragment");
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Log.d("NotificationPermission", "Notification permission granted.");
            } else {
                // Permission was denied
                Log.d("NotificationPermission", "Notification permission denied.");
                // Optionally, guide the user to manually enable notifications
                // openNotificationSettings();
            }
        }
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_url) {
            UrlFragment dialogFragment = new UrlFragment();
            dialogFragment.show(getSupportFragmentManager(), "UrlFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            LoginActivity.this.finish();
                        } catch (Exception e) {
                            Log.e("LoginActivity", "Error finishing activity", e);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}