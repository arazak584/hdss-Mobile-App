package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.odk.OdkForm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ODKCsvActivity extends AppCompatActivity {
    private IndividualViewModel individualViewModel;
    private OdkViewModel odkViewModel;
    private Button btnExport;
    private static final String TAG = "ODKCsvActivity";
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private static final String ODK_PACKAGE = "org.odk.collect.android";
    private static final String ODK_BASE_PATH = "/storage/Android/data/" + ODK_PACKAGE + "/files/projects";
    private static final int REQUEST_MANAGE_STORAGE = 2296;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odkcsv);

        btnExport = findViewById(R.id.btnSync);
        odkViewModel = new ViewModelProvider(this).get(OdkViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        checkStoragePermission();
        btnExport.setOnClickListener(v -> checkAndRequestPermissions());
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }
        startExport();
    }

    private void startExport() {
        new Thread(() -> {
            List<OdkForm> odkForms;
            try {
                odkForms = odkViewModel.find();
            } catch (ExecutionException | InterruptedException e) {
                String errorMessage = "Error fetching ODK forms: " + e.getMessage();
                runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show());
                return;
            }

            List<String> missingForms = new ArrayList<>();
            File projectsDir = new File(ODK_BASE_PATH);

            if (!projectsDir.exists() || !projectsDir.isDirectory()) {
                Log.e(TAG, "ODK projects directory not found at: " + projectsDir.getAbsolutePath());
                runOnUiThread(() -> Toast.makeText(this,
                        "ODK Collect app data directory not found. Is ODK Collect installed?",
                        Toast.LENGTH_LONG).show());
                return;
            }

            File[] projectDirs = projectsDir.listFiles(File::isDirectory);
            if (projectDirs == null || projectDirs.length == 0) {
                Log.e(TAG, "No project directories found in: " + projectsDir.getAbsolutePath());
                runOnUiThread(() -> Toast.makeText(this,
                        "No ODK projects found", Toast.LENGTH_LONG).show());
                return;
            }

            for (OdkForm form : odkForms) {
                if (form.getCsv() == null || form.getCsv().isEmpty() || form.getFormName() == null) {
                    missingForms.add(form.getFormID());
                    continue;
                }

                boolean csvExported = false;
                for (File projectDir : projectDirs) {
                    String projectName = projectDir.getName();
                    Log.d(TAG, "Processing project: " + projectName);

                    File formDir = new File(projectDir, "forms/" + form.getFormName());
                    if (!formDir.exists()) {
                        continue;
                    }

                    File mediaDir = new File(formDir, form.getFormName() + "-media");
                    if (mediaDir.exists()) {
                        Log.d(TAG, "Found media directory: " + mediaDir.getPath());
                    }

                    String csvFilePath = formDir.getPath() + "/" + form.getCsv() + ".csv";
                    if (generateCsvForOdk(form.getGender(), form.getMinAge(), form.getMaxAge(),
                            form.getStatus(), csvFilePath)) {
                        csvExported = true;
                        Log.i(TAG, "Successfully exported CSV for form " + form.getFormName());
                    }
                }

                if (!csvExported) {
                    missingForms.add(form.getFormID());
                }
            }

            runOnUiThread(() -> {
                if (!missingForms.isEmpty()) {
                    String message = "Missing CSV for forms: " + String.join(", ", missingForms);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "CSV Exported Successfully!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    //Method to push ids to ODK Form
//    package expo.modules.odksync
//
//import expo.modules.kotlin.modules.Module
//import expo.modules.kotlin.modules.ModuleDefinition
//import expo.modules.odksync.OdkSyncView
//import android.content.Intent
//import android.net.Uri
//import android.provider.BaseColumns
//import android.app.Activity
//import expo.modules.kotlin.exception.CodedException
//
//
//    class OdkSyncModule : Module() {
//        override fun definition() = ModuleDefinition {
//            // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
//            Function("selectParcela") { datos: Map<String, Any> ->
//                return@Function returnMultipleData(datos)
//            }
//
//            Function("getInstances") {
//                return@Function getInstances()
//            }
//
//            Function("openOdkForms") {
//                return@Function openOdkForms()
//            }
//        }
//
//        private val context
//        get() = requireNotNull(appContext.reactContext)
//
//        private val currentActivity
//        get() = appContext.activityProvider?.currentActivity ?: throw CodedException("Activity which was provided during module initialization is no longer available")
//
//        private fun openOdkForms() {
//            val myIntent = Intent(Intent.ACTION_VIEW)
//            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            myIntent.setType("vnd.android.cursor.dir/vnd.odk.form")
//            context.startActivity(myIntent)
//        }
//
//        private fun getInstances(): List<Map<String, Any>> {
//
//            val odkUrl = "content://org.odk.collect.android.provider.odk.instances/instances"
//            val uri: Uri = Uri.parse(odkUrl)
//            var cursor = context.contentResolver.query( uri, null, null, null, null)
//
//            val odkFormData = mutableListOf<Map<String, Any>>()
//            if (cursor != null) {
//                try {
//                    while (cursor.moveToNext()) {
//                        val dataMap = mutableMapOf<String, Any>()
//                        var id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
//                        dataMap["displayName"] = cursor.getString(cursor.getColumnIndex("displayName"))
//                        dataMap["jrFormId"] = cursor.getString(cursor.getColumnIndex("jrFormId"))
//                        dataMap["jrVersion"] = cursor.getString(cursor.getColumnIndex("jrVersion"))
//                        dataMap["status"] = cursor.getString(cursor.getColumnIndex("status"))
//                        odkFormData.add(dataMap)
//                    }
//                } finally {
//                    cursor.close()
//                }
//                return odkFormData
//            } else {
//                val dataMap = mutableMapOf<String, Any>()
//                dataMap["warning"] = "Unable to get forms"
//                odkFormData.add(dataMap)
//                return odkFormData
//            }
//
//        }
//
//        private fun returnMultipleData(datos: Map<String, Any>) {
//            if (currentActivity.getReferrer().toString() == "android-app://org.odk.collect.android") {
//                val intent = Intent()
//
//                val nomParcela = datos["nombre_parcela"]?.toString() ?: ""
//                val uuidParcela = datos["uuid_parcela"]?.toString() ?: ""
//                val idCoop = datos["id_cooperativa"]?.toString() ?: ""
//                val nomCoop = datos["nombre_cooperativa"]?.toString() ?: ""
//
//                intent.putExtra("id_parcela_nombre", nomParcela)
//                intent.putExtra("id_parcela_uuid" , uuidParcela)
//                intent.putExtra("id_cooperativa", idCoop)
//                intent.putExtra("id_cooperativa_nombre", nomCoop)
//                currentActivity.setResult(Activity.RESULT_OK, intent)
//                currentActivity.finish()
//            }
//        }
//    }

    private boolean generateCsvForOdk(int gender, int minAge, int maxAge, int status, String csvFilePath) {
        List<Individual> individuals = new ArrayList<>();

        if (gender == 3) {
            individuals.addAll(individualViewModel.getIndividualsForCsv(1, minAge, maxAge, status));
            individuals.addAll(individualViewModel.getIndividualsForCsv(2, minAge, maxAge, status));
        } else {
            individuals = individualViewModel.getIndividualsForCsv(gender, minAge, maxAge, status);
        }

        File csvFile = new File(csvFilePath);
        File parentDir = csvFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + parentDir.getPath());
                return false;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("name,extId,dob,gender,compno,village,hohID\n");

            for (Individual ind : individuals) {
                String fullName = ind.getFirstName() + " " + ind.getLastName();
                writer.write(String.format("%s,%s,%s,%d,%s,%s,%s\n",
                        fullName,
                        ind.getExtId(),
                        ind.getDob(),
                        ind.getGender(),
                        ind.getCompno(),
                        ind.getVillage(),
                        ind.getHohID()
                ));
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing CSV: " + e.getMessage(), e);
            runOnUiThread(() -> Toast.makeText(this,
                    "Error writing CSV: " + e.getMessage(),
                    Toast.LENGTH_LONG).show());
            return false;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    startExport();
//                } else {
//                    Toast.makeText(this, "Storage permission required", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startExport();
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (Scoped Storage Restrictions)
            if (!Environment.isExternalStorageManager()) {
                requestManageExternalStoragePermission();
            } else {
                Toast.makeText(this, "Manage External Storage Permission Granted!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Android 10 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_MANAGE_STORAGE);
            }
        }
    }

    private void requestManageExternalStoragePermission() {
        new AlertDialog.Builder(this)
                .setTitle("Storage Access Required")
                .setMessage("This app needs full access to storage to read ODK Collect project files. Please grant permission.")
                .setPositiveButton("Grant Access", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Storage permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}