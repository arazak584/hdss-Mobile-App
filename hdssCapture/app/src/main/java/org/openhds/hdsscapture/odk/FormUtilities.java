package org.openhds.hdsscapture.odk;

import static android.content.Context.STORAGE_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.odk.listener.OdkFormResultListener;
import org.openhds.hdsscapture.odk.model.FilledForm;
import org.openhds.hdsscapture.odk.model.OdkFormLoadData;
import org.openhds.hdsscapture.odk.storage.access.OdkScopedDirUtil;
import org.openhds.hdsscapture.odk.storage.access.OdkStorageType;
import org.openhds.hdsscapture.odk.task.OdkFormLoadResult;
import org.openhds.hdsscapture.odk.task.OdkFormLoadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive utility class for ODK Collect integration
 * Handles different ODK versions, permissions, and storage types
 */
public class FormUtilities {

    private static final String TAG = "FormUtilities";
    public static final int REQUEST_CODE_ODK_FORM = 1001;

    private Context mContext;
    private Fragment fragment;
    private AppCompatActivity activity;
    private OdkFormLoadData formLoadData;
    private Uri contentUri;
    private String instanceUri;
    private boolean formUnFinished;
    private String xmlFilePath;
    private OdkFormResultListener formResultListener;

    private String metaInstanceName;
    private Date lastUpdatedDate;
    private String formId;
    private String deviceId;

    private OdkStorageType odkStorageType;
    private OdkScopedDirUtil odkScopedDirUtil;

    private ActivityResultLauncher<String> requestPermissionRpState;
    private ActivityResultLauncher<String[]> requestPermissionsReadWrite;
    private ActivityResultLauncher<Intent> odkResultLauncher;
    private ActivityResultLauncher<Intent> requestManageAllLauncher;
    private ActivityResultLauncher<Intent> requestAccessAndroidDirLauncher;
    private OdkFormLoadTask currentLoadTask;
    private OnPermissionRequestListener onPermissionRpStateRequestListener;
    private OnPermissionRequestListener onPermissionReadWriteRequestListener;

    public FormUtilities(Fragment fragment, OdkFormResultListener listener) {
        this.fragment = fragment;
        this.mContext = fragment.getContext();
        this.formResultListener = listener;
        initialize();
    }

    public FormUtilities(AppCompatActivity activity, OdkFormResultListener listener) {
        this.activity = activity;
        this.mContext = activity;
        this.formResultListener = listener;
        initialize();
    }

    public Context getContext() {
        return mContext;
    }

    public OdkScopedDirUtil getOdkScopedDirUtil() {
        return this.odkScopedDirUtil;
    }

    private void initialize() {
        this.initResultCallbacks();
        this.retrieveOdkStorageType();
    }

    /**
     * Helper method to run code on UI thread safely
     */
    private void runOnUiThread(Runnable action) {
        if (activity != null) {
            activity.runOnUiThread(action);
        } else if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().runOnUiThread(action);
        }
    }

    private void initResultCallbacks() {

        // Permission result callback
        androidx.activity.result.ActivityResultCallback<Boolean> permissionResultCallback = granted -> {
            if (granted) {
                deviceId = getDeviceId();
                Log.d("deviceid", "" + deviceId);
            }

            if (onPermissionRpStateRequestListener != null) {
                onPermissionRpStateRequestListener.requestFinished(granted);
            }
        };

        // Register launchers based on fragment or activity
        if (this.fragment != null) {
            this.requestPermissionRpState = this.fragment.registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), permissionResultCallback);
            this.requestPermissionsReadWrite = this.fragment.registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), permissionResults -> {
                        boolean granted = !permissionResults.values().contains(false);
                        if (onPermissionReadWriteRequestListener != null) {
                            onPermissionReadWriteRequestListener.requestFinished(granted);
                        }
                    });
            this.odkResultLauncher = this.fragment.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), this::onOdkActivityResult);
            this.requestManageAllLauncher = this.fragment.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> onRequestManageAllActivityResult(result.getResultCode()));
            this.requestAccessAndroidDirLauncher = this.fragment.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), this::onRequestAccessAndroidDirResult);
        } else {
            this.requestPermissionRpState = this.activity.registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), permissionResultCallback);
            this.requestPermissionsReadWrite = this.activity.registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), permissionResults -> {
                        boolean granted = !permissionResults.values().contains(false);
                        if (onPermissionReadWriteRequestListener != null) {
                            onPermissionReadWriteRequestListener.requestFinished(granted);
                        }
                    });
            this.odkResultLauncher = this.activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), this::onOdkActivityResult);
            this.requestManageAllLauncher = this.activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> onRequestManageAllActivityResult(result.getResultCode()));
            this.requestAccessAndroidDirLauncher = this.activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), this::onRequestAccessAndroidDirResult);
        }
    }

    /**
     * Determine ODK Collect version and storage type
     */
    private void retrieveOdkStorageType() {
        final int odk_v_1_26_0 = 3713;
        final int odk_v_2021_2 = 4242;
        String versionName = "NONE";

        try {
            PackageInfo packageInfo = mContext.getPackageManager()
                    .getPackageInfo("org.odk.collect.android", 0);
            versionName = packageInfo.versionName;

            if (packageInfo.versionCode < odk_v_1_26_0) {
                odkStorageType = OdkStorageType.ODK_SHARED_FOLDER;
            } else if (packageInfo.versionCode < odk_v_2021_2) {
                odkStorageType = OdkStorageType.ODK_SCOPED_FOLDER_NO_PROJECTS;
            } else {
                odkStorageType = OdkStorageType.ODK_SCOPED_FOLDER_PROJECTS;
            }
        } catch (PackageManager.NameNotFoundException e) {
            odkStorageType = OdkStorageType.NO_ODK_INSTALLED;
        }

        Log.d(TAG, "ODK Storage Type: " + odkStorageType + ", ODK v" + versionName);
    }

    public OdkStorageType getOdkStorageType() {
        return this.odkStorageType;
    }

    /**
     * Create preloaded data from app entities
     */
    public static FilledForm createPreloadedData(Individual individual, Locations location,
                                                 Socialgroup socialgroup) {
        FilledForm filledForm = new FilledForm();

        if (individual != null) {
            filledForm.put("individualId", individual.uuid);
            filledForm.put("individualExtId", individual.extId);
            filledForm.put("firstName", individual.firstName);
            filledForm.put("middleName", individual.otherName);
            filledForm.put("lastName", individual.lastName);
            filledForm.put("gender", String.valueOf(individual.gender));
            filledForm.put("dob", individual.getDob());
            filledForm.put("age", String.valueOf(individual.getAge()));
        }

        if (location != null) {
            filledForm.put("locationId", location.uuid);
            filledForm.put("locationExtId", location.extId);
            filledForm.put("compno", location.compno);
            //filledForm.put("structno", location.structno);
        }

        if (socialgroup != null) {
            filledForm.put("socialgroupId", socialgroup.uuid);
            filledForm.put("socialgroupExtId", socialgroup.extId);
            filledForm.put("householdName", socialgroup.getGroupName());
        }

        return filledForm;
    }

    /**
     * Load a new ODK form
     */
    public void loadForm(final OdkFormLoadData loadData) {
        this.formId = loadData.formId;
        this.formLoadData = loadData;

        this.currentLoadTask = new OdkFormLoadTask(this, loadData, new OdkFormLoadTask.OdkFormLoadListener() {
            @Override
            public void onOdkFormLoadSuccess(OdkFormLoadResult result) {
                FormUtilities.this.contentUri = result.getContentUri();
                FormUtilities.this.instanceUri = result.getInstanceUri();
                odkResultLauncher.launch(new Intent(Intent.ACTION_EDIT, result.getContentUri()));
            }

            @Override
            public void onOdkFormLoadFailure(OdkFormLoadResult result) {
                createFormLoadResultErrorDialog(result, loadData);
            }
        });

        OnPermissionRequestListener readPhoneStateGrantListener = granted -> {
            if (granted) {
                requestPermissionsForOdkFolders();
            } else {
                showPermissionDeniedDialog(R.string.odk_form_load_permission_request_readphonestate_denied_lbl);
            }
        };

        requestPermissionsForReadingPhoneState(readPhoneStateGrantListener);
    }

    /**
     * Reopen an existing form instance
     */
    public void loadForm(OdkFormLoadData loadData, String contentUriAsString,
                         String instanceXmlUri, final OdkFormResultListener listener) {
        this.formId = loadData.formId;
        this.formLoadData = loadData;
        this.contentUri = Uri.parse(contentUriAsString);
        this.metaInstanceName = "";
        this.lastUpdatedDate = null;
        loadData.formInstanceUri = instanceXmlUri;

        this.currentLoadTask = new OdkFormLoadTask(this, loadData, this.contentUri,
                new OdkFormLoadTask.OdkFormLoadListener() {
                    @Override
                    public void onOdkFormLoadSuccess(OdkFormLoadResult result) {
                        FormUtilities.this.contentUri = result.getContentUri();
                        FormUtilities.this.instanceUri = result.getInstanceUri();
                        odkResultLauncher.launch(new Intent(Intent.ACTION_EDIT, result.getContentUri()));
                    }

                    @Override
                    public void onOdkFormLoadFailure(OdkFormLoadResult result) {
                        if (listener != null) {
                            listener.onFormInstanceNotFound(formLoadData, contentUri);
                        } else {
                            createFormLoadResultErrorDialog(result, loadData);
                        }
                    }
                });

        OnPermissionRequestListener readPhoneStateGrantListener = granted -> {
            if (granted) {
                callExecuteCurrentLoadTask();
            } else {
                showPermissionDeniedDialog(R.string.odk_form_load_permission_request_readphonestate_denied_lbl);
            }
        };

        requestPermissionsForReadingPhoneState(readPhoneStateGrantListener);
    }

    // Permission handling methods

    private void requestPermissionsForReadingPhoneState(OnPermissionRequestListener listener) {
        if (isPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
            requestPermissionsForOdkFolders();
        } else {
            this.onPermissionRpStateRequestListener = listener;
            this.requestPermissionRpState.launch(Manifest.permission.READ_PHONE_STATE);
        }
    }


    // Replace the requestPermissionsForOdkFolders method in your FormUtilities.java

    private void requestPermissionsForOdkFolders() {
        if (odkStorageType == OdkStorageType.NO_ODK_INSTALLED) {
            showErrorDialog(R.string.odk_form_load_error_odk_not_installed_lbl);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            Log.d(TAG, "Android 11+, Storage Type: " + odkStorageType);

            if (odkStorageType == OdkStorageType.ODK_SHARED_FOLDER) {
                // For ODK shared folder (/odk), we need SAF access, NOT MANAGE_EXTERNAL_STORAGE
                Log.d(TAG, "ODK Shared Folder - requesting SAF access");
                OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID = OdkScopedDirUtil.ODK_SHARED_FOLDER_URI;
                requestAccessAndroidDir();
            }
            else if (odkStorageType == OdkStorageType.ODK_SCOPED_FOLDER_NO_PROJECTS ||
                    odkStorageType == OdkStorageType.ODK_SCOPED_FOLDER_PROJECTS) {
                // For newer ODK versions with scoped storage
                Log.d(TAG, "ODK Scoped Folder - requesting SAF access");
                OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID = OdkScopedDirUtil.ODK_SCOPED_FOLDER_URI;
                requestAccessAndroidDir();
            }
        } else {
            // Android 10 and below - use READ/WRITE_EXTERNAL_STORAGE
            Log.d(TAG, "Android 10 or below - requesting READ/WRITE permissions");
            OnPermissionRequestListener readWriteGrantListener = granted -> {
                if (granted) {
                    callExecuteCurrentLoadTask();
                } else {
                    showPermissionDeniedDialog(R.string.odk_form_load_permission_request_readwrite_denied_lbl);
                }
            };

            requestPermissionsForReadAndWriteFiles(readWriteGrantListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestAccessAndroidDir() {
        // Ensure the correct document ID is set based on storage type
        if (odkStorageType == OdkStorageType.ODK_SHARED_FOLDER) {
            OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID = OdkScopedDirUtil.ODK_SHARED_FOLDER_URI;
            Log.d(TAG, "Set to ODK_SHARED_FOLDER_URI: " + OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);
        } else {
            OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID = OdkScopedDirUtil.ODK_SCOPED_FOLDER_URI;
            Log.d(TAG, "Set to ODK_SCOPED_FOLDER_URI: " + OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);
        }

        Log.d(TAG, "=== requestAccessAndroidDir ===");
        Log.d(TAG, "Storage Type: " + odkStorageType);
        Log.d(TAG, "Document ID: " + OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hasSAFPermissionToAndroidDir()) {
            Log.d(TAG, "Permission already granted, initializing OdkScopedDirUtil");
            this.odkScopedDirUtil = new OdkScopedDirUtil(mContext, odkStorageType);
            callExecuteCurrentLoadTask();
        } else {
            Log.d(TAG, "Permission not granted, showing dialog");

            // Show custom dialog with clear instructions
            String folderPath = odkStorageType == OdkStorageType.ODK_SHARED_FOLDER ?
                    "odk" : "Android → data → org.odk.collect.android → files";

            String message = mContext.getString(R.string.odk_form_load_permission_request_android_dir_info_lbl) +
                    "\n\n" + mContext.getString(R.string.select_folder_instructions, folderPath);

            runOnUiThread(() -> {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.odk_form_load_permission_request_manage_all_title_lbl)
                        .setMessage(message)
                        .setPositiveButton(R.string.select_folder_btn, (dialog, which) -> {
                            executeRequestAccessAndroidDir();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(false)
                        .show();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void executeRequestAccessAndroidDir() {
        Log.d(TAG, "=== executeRequestAccessAndroidDir START ===");
        Log.d(TAG, "Storage Type: " + odkStorageType);
        Log.d(TAG, "Document ID: " + OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);

        try {
            // Create the intent to open folder picker
            Intent intent = getPrimaryVolume().createOpenDocumentTreeIntent();

            // Try to set initial location
            try {
                Uri uri = DocumentsContract.buildDocumentUri(
                        OdkScopedDirUtil.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
                        OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);

                Log.d(TAG, "Target URI: " + uri);

                // Use the old constant for compatibility
                intent.putExtra("android.provider.extra.INITIAL_URI", uri);
            } catch (Exception e) {
                Log.w(TAG, "Could not set initial URI", e);
                // Continue anyway - user will just start at root
            }

            Log.d(TAG, "Launching file picker...");
            this.requestAccessAndroidDirLauncher.launch(intent);

        } catch (Exception e) {
            Log.e(TAG, "ERROR in executeRequestAccessAndroidDir", e);
            e.printStackTrace();

            runOnUiThread(() -> {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.error_lbl)
                        .setMessage(mContext.getString(R.string.error_opening_file_manager) +
                                "\n\n" + e.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            });
        }

        Log.d(TAG, "=== executeRequestAccessAndroidDir END ===");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private StorageVolume getPrimaryVolume() {
        StorageManager sm = (StorageManager)  mContext.getSystemService(STORAGE_SERVICE);
        return sm.getPrimaryStorageVolume();
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageExternalStoragePermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            this.requestManageAllLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching MANAGE_EXTERNAL_STORAGE permission", e);
            // Fallback to app-specific settings
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            this.requestManageAllLauncher.launch(intent);
        }
    }

    private void requestPermissionsForReadAndWriteFiles(OnPermissionRequestListener listener) {
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            callExecuteCurrentLoadTask();
        } else {
            this.onPermissionReadWriteRequestListener = listener;
            this.requestPermissionsReadWrite.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    private void executeRequestAccessAndroidDir() {
//        Uri uri = DocumentsContract.buildDocumentUri(
//                OdkScopedDirUtil.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
//                OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);
//        Intent intent = getPrimaryVolume().createOpenDocumentTreeIntent();
//        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
//        this.requestAccessAndroidDirLauncher.launch(intent);
//    }



    private void onRequestManageAllActivityResult(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            callExecuteCurrentLoadTask();
        } else {
            showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
        }
    }

    private void onRequestAccessAndroidDirResult(ActivityResult result) {
        Log.d(TAG, "=== onRequestAccessAndroidDirResult START ===");
        Log.d(TAG, "Result code: " + result.getResultCode());

        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() == null) {
                Log.e(TAG, "Result data is null!");
                showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
                return;
            }

            Uri directoryUri = result.getData().getData();
            Log.d(TAG, "Selected URI: " + directoryUri);

            if (directoryUri == null) {
                Log.e(TAG, "Directory URI is null!");
                showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
                return;
            }

            try {
                // Take persistable permission
                mContext.getContentResolver().takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Log.d(TAG, "Persistable URI permission taken successfully");

                // Log all persisted permissions for debugging
                Log.d(TAG, "=== All Persisted Permissions ===");
                for (UriPermission permission : mContext.getContentResolver().getPersistedUriPermissions()) {
                    Log.d(TAG, "Permission URI: " + permission.getUri() +
                            " (read: " + permission.isReadPermission() +
                            ", write: " + permission.isWritePermission() + ")");
                }

                // Check what we're expecting
                Uri expectedTreeUri = DocumentsContract.buildTreeDocumentUri(
                        OdkScopedDirUtil.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
                        OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);
                Log.d(TAG, "Expected URI: " + expectedTreeUri);
                Log.d(TAG, "Received URI: " + directoryUri);

                // More flexible permission check - check if the selected URI is for the right folder
                boolean hasPermission = false;

                // Extract the document ID from the selected URI
                String selectedDocId = DocumentsContract.getTreeDocumentId(directoryUri);
                Log.d(TAG, "Selected Document ID: " + selectedDocId);
                Log.d(TAG, "Expected Document ID: " + OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);

                // Check if it matches or is a parent/child of the expected path
                if (selectedDocId != null &&
                        (selectedDocId.equals(OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID) ||
                                selectedDocId.startsWith(OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID + "/") ||
                                OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID.startsWith(selectedDocId + "/"))) {
                    hasPermission = true;
                    Log.d(TAG, "Permission check PASSED (document ID match)");
                } else {
                    // Fallback: check using the standard method
                    hasPermission = hasSAFPermissionToAndroidDir();
                    Log.d(TAG, "Permission check via hasSAFPermissionToAndroidDir: " + hasPermission);
                }

                if (hasPermission) {
                    Log.d(TAG, "Permission verified, initializing OdkScopedDirUtil...");
                    this.odkScopedDirUtil = new OdkScopedDirUtil(mContext, odkStorageType);

                    if (this.odkScopedDirUtil != null) {
                        Log.d(TAG, "OdkScopedDirUtil initialized successfully");
                        callExecuteCurrentLoadTask();
                    } else {
                        Log.e(TAG, "OdkScopedDirUtil initialization returned null!");
                        showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
                    }
                } else {
                    Log.e(TAG, "Permission check FAILED!");

                    // Show detailed error to user
                    String expectedPath = odkStorageType == OdkStorageType.ODK_SHARED_FOLDER ?
                            "odk" : "Android/data/org.odk.collect.android/files";

                    runOnUiThread(() -> {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Incorrect Folder Selected")
                                .setMessage("Please select the exact folder:\n\n" + expectedPath +
                                        "\n\nSelected: " + selectedDocId)
                                .setPositiveButton("Try Again", (dialog, which) -> {
                                    executeRequestAccessAndroidDir();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException when taking persistable permission", e);
                showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error in permission handling", e);
                e.printStackTrace();
                showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Log.d(TAG, "User cancelled folder selection");
            showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
        } else {
            Log.e(TAG, "Unexpected result code: " + result.getResultCode());
            showPermissionDeniedDialog(R.string.odk_form_load_permission_storage_denied_lbl);
        }

        Log.d(TAG, "=== onRequestAccessAndroidDirResult END ===");
    }

    private boolean hasSAFPermissionToAndroidDir() {
        Uri treeUri = DocumentsContract.buildTreeDocumentUri(
                OdkScopedDirUtil.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
                OdkScopedDirUtil.PRIMARY_ANDROID_DOC_ID);

        Log.d(TAG, "Checking permission for URI: " + treeUri);

        for (UriPermission uriPermission : mContext.getContentResolver().getPersistedUriPermissions()) {
            Log.d(TAG, "Checking against: " + uriPermission.getUri());

            if (uriPermission.getUri().equals(treeUri) && uriPermission.isReadPermission()) {
                Log.d(TAG, "Permission found and granted!");
                return true;
            }
        }

        Log.d(TAG, "Permission not found");
        return false;
    }


    private boolean isPermissionGranted(final String... permissions) {
        return Arrays.stream(permissions).noneMatch(permission ->
                ContextCompat.checkSelfPermission(this.getContext(), permission) ==
                        PackageManager.PERMISSION_DENIED);
    }

    private void callExecuteCurrentLoadTask() {
        if (this.currentLoadTask != null) {
            this.currentLoadTask.execute();
        }
    }

    // Result handling

    private void onOdkActivityResult(ActivityResult result) {
        this.currentLoadTask = null;
        handleXformResult(result);
    }

    private void handleXformResult(ActivityResult result) {
        new CheckFormStatus(mContext.getContentResolver(), contentUri).execute();
    }

    // Utility methods

    public String getStartTimestamp() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        long gmt = TimeUnit.HOURS.convert(tz.getRawOffset(), TimeUnit.MILLISECONDS);

        sdf.setCalendar(cal);
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, (int) (-1 * gmt));

        return sdf.format(cal.getTime());
    }

    public String getDeviceId() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        String deviceId = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return "android:" + deviceId;
    }

    // Dialog helpers

    private void createFormLoadResultErrorDialog(OdkFormLoadResult result, OdkFormLoadData loadData) {
        @StringRes int messageId = R.string.odk_form_load_error_odk_not_installed_lbl;

        switch (result.getStatus()) {
            case ERROR_NO_ODK_INSTALLED:
                messageId = R.string.odk_form_load_error_odk_not_installed_lbl;
                break;
            case ERROR_PROVIDER_NA:
                messageId = R.string.odk_form_load_error_provider_lbl;
                break;
            case ERROR_FORM_NOT_FOUND:
                messageId = R.string.odk_form_load_error_form_not_found_lbl;
                break;
            case ERROR_ODK_FOLDER_PERMISSION_DENIED:
                messageId = R.string.odk_form_load_error_folder_permissions_lbl;
                break;
            case ERROR_ODK_CREATE_SAVED_INSTANCE_FILE:
                messageId = R.string.odk_form_load_error_saving_prefilled_xml_lbl;
                break;
            case ERROR_ODK_INSERT_SAVED_INSTANCE:
                messageId = R.string.odk_form_load_error_saving_instance_form_lbl;
                break;
        }

        final int finalMessageId = messageId;
        runOnUiThread(() -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.warning_lbl)
                    .setMessage(finalMessageId)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        if (formResultListener != null) {
                            formResultListener.onFormLoadError(loadData, result);
                        }
                    })
                    .show();
        });
    }

    private void showErrorDialog(@StringRes int messageId) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.odk_problem_lbl)
                    .setMessage(messageId)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });
    }

    private void showPermissionDeniedDialog(@StringRes int messageId) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.permissions_sync_storage_title_lbl)
                    .setMessage(messageId)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });
    }

    private void showInfoDialogAndExecute(@StringRes int titleId, @StringRes int messageId,
                                          Runnable action) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(titleId)
                    .setMessage(messageId)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> action.run())
                    .show();
        });
    }

    private void createUnfinishedFormDialog() {
        formUnFinished = true;

        new AlertDialog.Builder(mContext)
                .setTitle(R.string.warning_lbl)
                .setMessage(R.string.odk_unfinished_dialog_msg)
                .setPositiveButton(R.string.odk_unfinished_button_delete, (dialog, which) -> {
                    formUnFinished = false;
                    mContext.getContentResolver().delete(contentUri,
                            InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                            new String[]{InstanceProviderAPI.STATUS_INCOMPLETE});

                    if (formResultListener != null) {
                        formResultListener.onDeleteForm(formLoadData, contentUri, instanceUri);
                    }
                })
                .setNegativeButton(R.string.odk_unfinished_button_change, (dialog, which) -> {
                    formUnFinished = false;
                    odkResultLauncher.launch(new Intent(Intent.ACTION_EDIT, contentUri));
                })
                .setNeutralButton(R.string.odk_unfinished_button_save, (dialog, which) -> {
                    Toast.makeText(mContext, R.string.odk_unfinished_form_saved, Toast.LENGTH_LONG).show();
                    saveUnfinalizedFile();

                    if (formResultListener != null) {
                        formResultListener.onFormUnFinalized(formLoadData, contentUri, formId,
                                instanceUri, metaInstanceName, lastUpdatedDate);
                    }
                })
                .show();
    }

    private void saveUnfinalizedFile() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(contentUri,
                new String[]{
                        InstanceProviderAPI.InstanceColumns.STATUS,
                        InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH,
                        InstanceProviderAPI.InstanceColumns.DISPLAY_NAME,
                        InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE
                },
                InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                new String[]{InstanceProviderAPI.STATUS_INCOMPLETE}, null);

        if (cursor != null && cursor.moveToNext()) {
            xmlFilePath = cursor.getString(cursor.getColumnIndex(
                    InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));

            String sdate = cursor.getString(cursor.getColumnIndex(
                    InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE));
            lastUpdatedDate = new Date(Long.parseLong(sdate));

            metaInstanceName = cursor.getString(cursor.getColumnIndex(
                    InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
        }

        if (cursor != null) cursor.close();
    }

    // AsyncTask for checking form status

    class CheckFormStatus extends AsyncTask<Void, Void, Boolean> {

        private ContentResolver resolver;
        private Uri contentUri;

        public CheckFormStatus(ContentResolver resolver, Uri contentUri) {
            this.resolver = resolver;
            this.contentUri = contentUri;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Cursor cursor = resolver.query(contentUri,
                    new String[]{
                            InstanceProviderAPI.InstanceColumns.STATUS,
                            InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH,
                            InstanceProviderAPI.InstanceColumns.DISPLAY_NAME,
                            InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE
                    },
                    InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                    new String[]{InstanceProviderAPI.STATUS_COMPLETE}, null);

            boolean resultToReturn = false;

            if (cursor != null && cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndex(
                        InstanceProviderAPI.InstanceColumns.STATUS));

                if (InstanceProviderAPI.STATUS_COMPLETE.equals(status)) {
                    String statusChangeDate = cursor.getString(cursor.getColumnIndex(
                            InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE));
                    xmlFilePath = cursor.getString(cursor.getColumnIndex(
                            InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
                    metaInstanceName = cursor.getString(cursor.getColumnIndex(
                            InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
                    lastUpdatedDate = new Date(Long.parseLong(statusChangeDate));
                    resultToReturn = true;
                }
            }

            if (cursor != null) cursor.close();
            return resultToReturn;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (formResultListener != null) {
                    formResultListener.onFormFinalized(formLoadData, contentUri, formId,
                            instanceUri, metaInstanceName, lastUpdatedDate);
                }
            } else {
                if (formLoadData.skipUnfinalizedCheck) {
                    if (formResultListener != null) {
                        saveUnfinalizedFile();
                        formResultListener.onFormUnFinalized(formLoadData, contentUri, formId,
                                instanceUri, metaInstanceName, lastUpdatedDate);
                    }
                } else {
                    createUnfinishedFormDialog();
                }
            }
        }
    }

}

interface OnPermissionRequestListener {
    void requestFinished(boolean granted);
}