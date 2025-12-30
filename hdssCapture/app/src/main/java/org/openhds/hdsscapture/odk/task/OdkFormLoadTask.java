package org.openhds.hdsscapture.odk.task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.openhds.hdsscapture.odk.FormUtilities;
import org.openhds.hdsscapture.odk.FormsProviderAPI;
import org.openhds.hdsscapture.odk.InstanceProviderAPI;
import org.openhds.hdsscapture.odk.model.FilledForm;
import org.openhds.hdsscapture.odk.model.OdkFormLoadData;
import org.openhds.hdsscapture.odk.storage.access.OdkScopedDirUtil;
import org.openhds.hdsscapture.odk.storage.access.OdkStorageType;
import org.openhds.hdsscapture.odk.xml.OdkColumnsPreloader;
import org.openhds.hdsscapture.odk.xml.XFormDef;
import org.openhds.hdsscapture.odk.xml.XMLFinder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Task to load ODK forms with preloaded data
 * Based on HDS Explorer implementation
 */
public class OdkFormLoadTask {

    private static final String TAG = "OdkFormLoadTask";

    private FormUtilities formUtilities;
    private OdkFormLoadData loadData;
    private Uri existingContentUri;
    private OdkFormLoadListener listener;
    private ContentResolver resolver;
    private FilledForm filledForm;
    private Uri odkContentUri;

    public interface OdkFormLoadListener {
        void onOdkFormLoadSuccess(OdkFormLoadResult result);
        void onOdkFormLoadFailure(OdkFormLoadResult result);
    }

    public OdkFormLoadTask(FormUtilities formUtilities, OdkFormLoadData loadData,
                           OdkFormLoadListener listener) {
        this.formUtilities = formUtilities;
        this.loadData = loadData;
        this.listener = listener;
        this.resolver = formUtilities.getContext().getContentResolver();
        this.filledForm = loadData.preloadedData;
    }

    public OdkFormLoadTask(FormUtilities formUtilities, OdkFormLoadData loadData,
                           Uri existingContentUri, OdkFormLoadListener listener) {
        this.formUtilities = formUtilities;
        this.loadData = loadData;
        this.existingContentUri = existingContentUri;
        this.listener = listener;
        this.resolver = formUtilities.getContext().getContentResolver();
        this.filledForm = loadData.preloadedData;
    }

    public void execute() {
        new Thread(() -> {
            OdkFormLoadResult result = performLoad();

            if (result.getStatus() == OdkFormLoadResult.Status.SUCCESS) {
                if (listener != null) {
                    listener.onOdkFormLoadSuccess(result);
                }
            } else {
                if (listener != null) {
                    listener.onOdkFormLoadFailure(result);
                }
            }
        }).start();
    }

    private OdkFormLoadResult performLoad() {
        // Check if reopening existing form
        if (existingContentUri != null && loadData.formInstanceUri != null) {
            return reopenExistingForm();
        }

        // Load new form
        return loadNewForm();
    }

    private OdkFormLoadResult reopenExistingForm() {
        try {
            Cursor cursor = resolver.query(existingContentUri,
                    new String[]{InstanceProviderAPI.InstanceColumns.STATUS},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return new OdkFormLoadResult(OdkFormLoadResult.Status.SUCCESS,
                        existingContentUri, loadData.formInstanceUri);
            } else {
                if (cursor != null) cursor.close();
                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reopening form", e);
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_PROVIDER_NA);
        }
    }

//    private OdkFormLoadResult loadNewForm() {
//        // Step 1: Query ODK's Forms Provider to get form metadata
//        String jrFormId = null;
//        String jrFormName = null;
//        String formFilePath = null;
//        String formAbsoluteFilePath = null;
//        String formVersion = null;
//
//        Cursor cursor = null;
//        try {
//            cursor = getCursorForFormsProvider(filledForm.getFormName());
//        } catch (Exception e) {
//            Log.e(TAG, "Error querying forms provider", e);
//
//            // Retry once (HDS Explorer pattern)
//            if (e.getMessage() != null && e.getMessage().contains("on a null object reference")) {
//                try {
//                    cursor = getCursorForFormsProvider(filledForm.getFormName());
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_PROVIDER_NA);
//                }
//            } else {
//                e.printStackTrace();
//                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_PROVIDER_NA);
//            }
//        }
//
//        // Parse cursor results
//        if (cursor != null && cursor.moveToNext()) {
//            int formIdIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_FORM_ID);
//            int formNameIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME);
//            int formPathIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH);
//            int formVersionIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_VERSION);
//
//            jrFormId = cursor.getString(formIdIndex);
//            jrFormName = cursor.getString(formNameIndex);
//            formFilePath = cursor.getString(formPathIndex);
//            formVersion = cursor.getString(formVersionIndex);
//            cursor.close();
//        } else {
//            if (cursor != null) cursor.close();
//            Log.e(TAG, "Form not found in ODK provider");
//            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
//        }
//
//        if (jrFormId == null || formFilePath == null) {
//            Log.e(TAG, "Form ID or path is null");
//            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
//        }
//
//        Log.d(TAG, "Found form: id=" + jrFormId + ", version=" + formVersion + ", path=" + formFilePath);
//
//        // Step 2: Handle different storage types based on Android version
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return loadFormAndroid11Plus(jrFormId, jrFormName, formFilePath, formVersion);
//        } else {
//            return loadFormAndroid10Minus(jrFormId, jrFormName, formFilePath, formVersion);
//        }
//    }


    private OdkFormLoadResult loadNewForm() {
        // Step 1: Query ODK's Forms Provider to get form metadata
        String jrFormId = null;
        String jrFormName = null;
        String formFilePath = null;
        String formAbsoluteFilePath = null;
        String formVersion = null;

        Cursor cursor = null;
        try {
            cursor = getCursorForFormsProvider(filledForm.getFormName());
        } catch (Exception e) {
            Log.e(TAG, "Error querying forms provider", e);

            // Retry once (HDS Explorer pattern)
            if (e.getMessage() != null && e.getMessage().contains("on a null object reference")) {
                try {
                    cursor = getCursorForFormsProvider(filledForm.getFormName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_PROVIDER_NA);
                }
            } else {
                e.printStackTrace();
                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_PROVIDER_NA);
            }
        }

        // Parse cursor results - SELECT THE LATEST VERSION
        if (cursor != null) {
            FormVersion latestForm = findLatestFormVersion(cursor);
            cursor.close();

            if (latestForm != null) {
                jrFormId = latestForm.formId;
                jrFormName = latestForm.formName;
                formFilePath = latestForm.formPath;
                formVersion = latestForm.version;

                Log.d(TAG, "Selected form version: " + formVersion + " (from " + latestForm.totalVersions + " versions)");
            } else {
                Log.e(TAG, "No valid form found");
                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
            }
        } else {
            Log.e(TAG, "Form not found in ODK provider");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
        }

        if (jrFormId == null || formFilePath == null) {
            Log.e(TAG, "Form ID or path is null");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
        }

        Log.d(TAG, "Found form: id=" + jrFormId + ", version=" + formVersion + ", path=" + formFilePath);

        // Step 2: Handle different storage types based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return loadFormAndroid11Plus(jrFormId, jrFormName, formFilePath, formVersion);
        } else {
            return loadFormAndroid10Minus(jrFormId, jrFormName, formFilePath, formVersion);
        }
    }

    /**
     * Find the latest version of a form from the cursor
     * Handles different version formats: numeric, semantic versioning, date-based, etc.
     */
    private FormVersion findLatestFormVersion(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }

        int formIdIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_FORM_ID);
        int formNameIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME);
        int formPathIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH);
        int formVersionIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_VERSION);
        int dateIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns.DATE);

        FormVersion latestForm = null;
        int totalVersions = 0;

        do {
            totalVersions++;

            String formId = cursor.getString(formIdIndex);
            String formName = cursor.getString(formNameIndex);
            String formPath = cursor.getString(formPathIndex);
            String version = cursor.getString(formVersionIndex);
            long date = cursor.getLong(dateIndex);

            Log.d(TAG, "Found form version: id=" + formId + ", version=" + version + ", date=" + date);

            if (latestForm == null) {
                // First form found
                latestForm = new FormVersion(formId, formName, formPath, version, date);
            } else {
                // Compare versions
                if (isNewerVersion(version, date, latestForm.version, latestForm.date)) {
                    latestForm = new FormVersion(formId, formName, formPath, version, date);
                    Log.d(TAG, "  → This is newer, selecting it");
                } else {
                    Log.d(TAG, "  → Older version, skipping");
                }
            }
        } while (cursor.moveToNext());

        if (latestForm != null) {
            latestForm.totalVersions = totalVersions;
        }

        return latestForm;
    }

    /**
     * Compare two form versions to determine which is newer
     * Handles multiple version formats
     */
    private boolean isNewerVersion(String newVersion, long newDate,
                                   String currentVersion, long currentDate) {
        // If both have versions, compare versions
        if (newVersion != null && !newVersion.trim().isEmpty() &&
                currentVersion != null && !currentVersion.trim().isEmpty()) {

            int comparison = compareVersions(newVersion, currentVersion);
            if (comparison != 0) {
                return comparison > 0;
            }
        }

        // If versions are equal or not available, use date
        return newDate > currentDate;
    }

    /**
     * Compare version strings intelligently
     * Handles: numeric (1, 2, 3), semantic (1.0.0, 1.2.3), dates (2024-01-01), mixed
     */
    private int compareVersions(String version1, String version2) {
        try {
            // Try numeric comparison first
            try {
                double v1 = Double.parseDouble(version1);
                double v2 = Double.parseDouble(version2);
                return Double.compare(v1, v2);
            } catch (NumberFormatException e) {
                // Not simple numeric, try other methods
            }

            // Try semantic versioning (1.2.3)
            if (version1.matches("\\d+(\\.\\d+)*") && version2.matches("\\d+(\\.\\d+)*")) {
                return compareSemanticVersions(version1, version2);
            }

            // Try date-based (2024-01-01, 20240101, etc.)
            if (isDateFormat(version1) && isDateFormat(version2)) {
                return version1.compareTo(version2);
            }

            // Fallback to string comparison
            return version1.compareTo(version2);

        } catch (Exception e) {
            Log.w(TAG, "Error comparing versions: " + version1 + " vs " + version2, e);
            return 0;
        }
    }

    /**
     * Compare semantic versions (1.2.3 vs 1.3.0)
     */
    private int compareSemanticVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < maxLength; i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }

        return 0;
    }

    /**
     * Check if string looks like a date
     */
    private boolean isDateFormat(String version) {
        return version.matches("\\d{4}[-\\.]?\\d{2}[-\\.]?\\d{2}.*");
    }


    /**
     * Helper class to store form version information
     */
    private static class FormVersion {
        String formId;
        String formName;
        String formPath;
        String version;
        long date;
        int totalVersions;

        FormVersion(String formId, String formName, String formPath, String version, long date) {
            this.formId = formId;
            this.formName = formName;
            this.formPath = formPath;
            this.version = version;
            this.date = date;
        }
    }

    /**
     * Load form on Android 11+ using Scoped Storage
     */
    private OdkFormLoadResult loadFormAndroid11Plus(String jrFormId, String jrFormName,
                                                    String formFilePath, String formVersion) {
        OdkScopedDirUtil odkUtil = formUtilities.getOdkScopedDirUtil();

        if (odkUtil == null) {
            Log.e(TAG, "OdkScopedDirUtil is null");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_FOLDER_PERMISSION_DENIED);
        }

        String formAbsoluteFilePath = formFilePath;

        // For shared folder, extract just the filename
        if (formUtilities.getOdkStorageType() == OdkStorageType.ODK_SHARED_FOLDER) {
            formFilePath = new File(formFilePath).getName();
        }

        // Find the blank form
        OdkScopedDirUtil.OdkFormObject formObject = odkUtil.findBlankForm(formFilePath);

        if (formObject == null) {
            Log.e(TAG, "Blank form not found: " + formFilePath);
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
        }

        // Generate preloaded XML
        OdkColumnsPreloader preloader = new OdkColumnsPreloader(formUtilities, filledForm);
        String xml = preloader.generatePreloadedXml(jrFormId, formVersion, formObject);

        if (xml == null || xml.isEmpty()) {
            Log.e(TAG, "Failed to generate preloaded XML");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_CREATE_SAVED_INSTANCE_FILE);
        }

        // Create new instance file
        OdkScopedDirUtil.OdkFormInstance formInstance =
                createNewOdkFormInstanceFile(xml, jrFormId, formObject);

        if (formInstance == null) {
            Log.e(TAG, "Failed to create form instance file");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_CREATE_SAVED_INSTANCE_FILE);
        }

        // Register instance with ODK
        boolean inserted = insertNewOdkFormInstance(formInstance, formAbsoluteFilePath,
                jrFormName, jrFormId, formVersion);

        if (!inserted) {
            Log.e(TAG, "Failed to insert form instance into ODK provider");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_INSERT_SAVED_INSTANCE);
        }

        String instanceUri = formInstance.getInstanceRelativePath();
        return new OdkFormLoadResult(OdkFormLoadResult.Status.SUCCESS, odkContentUri, instanceUri);
    }

    /**
     * Load form on Android 10 and below using File API
     */
    private OdkFormLoadResult loadFormAndroid10Minus(String jrFormId, String jrFormName,
                                                     String formFilePath, String formVersion) {
        // For scoped folder (not shared), find the correct path
        if (formUtilities.getOdkStorageType() != OdkStorageType.ODK_SHARED_FOLDER) {
            SearchFormResult searchResult = findOdkFormOnScopedDir(formFilePath);

            if (searchResult.status == SearchStatus.NOT_FOUND) {
                Log.e(TAG, "Form not found in scoped directory");
                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_FORM_NOT_FOUND);
            }

            if (searchResult.status == SearchStatus.PERMISSION_DENIED) {
                Log.e(TAG, "Permission denied to access scoped directory");
                return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_FOLDER_PERMISSION_DENIED);
            }

            formFilePath = searchResult.formFile.getAbsolutePath();
        }

        File formFile = new File(formFilePath);
        if (!formFile.exists() || !formFile.canRead()) {
            Log.e(TAG, "Cannot read form file: " + formFilePath);
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_FOLDER_PERMISSION_DENIED);
        }

        // Generate preloaded XML
        OdkColumnsPreloader preloader = new OdkColumnsPreloader(formUtilities, filledForm);
        String xml = preloader.generatePreloadedXml(jrFormId, formVersion, formFilePath);

        if (xml == null || xml.isEmpty()) {
            Log.e(TAG, "Failed to generate preloaded XML");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_CREATE_SAVED_INSTANCE_FILE);
        }

        // Create new instance file
        File instanceFile = createNewOdkFormInstanceFile(xml, jrFormId, formFile);

        if (instanceFile == null) {
            Log.e(TAG, "Failed to create form instance file");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_CREATE_SAVED_INSTANCE_FILE);
        }

        // Register instance with ODK
        boolean inserted = insertNewOdkFormInstance(instanceFile, jrFormName, jrFormId, formVersion);

        if (!inserted) {
            Log.e(TAG, "Failed to insert form instance into ODK provider");
            return new OdkFormLoadResult(OdkFormLoadResult.Status.ERROR_ODK_INSERT_SAVED_INSTANCE);
        }

        String instanceUri = instanceFile.getParentFile().getName() + File.separator + instanceFile.getName();
        return new OdkFormLoadResult(OdkFormLoadResult.Status.SUCCESS, odkContentUri, instanceUri);
    }

    /**
     * Query forms provider - returns ALL versions of matching forms
     */
    private Cursor getCursorForFormsProvider(String name) {
        return resolver.query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                new String[]{
                        FormsProviderAPI.FormsColumns.JR_FORM_ID,
                        FormsProviderAPI.FormsColumns.DISPLAY_NAME,
                        FormsProviderAPI.FormsColumns.FORM_FILE_PATH,
                        FormsProviderAPI.FormsColumns.JR_VERSION,
                        FormsProviderAPI.FormsColumns.DATE
                },
                FormsProviderAPI.FormsColumns.JR_FORM_ID + " like ?",
                new String[]{name + "%"},
                FormsProviderAPI.FormsColumns.DATE + " DESC");  // Order by date descending
    }

//    private Cursor getCursorForFormsProvider(String name) {
//        return resolver.query(FormsProviderAPI.FormsColumns.CONTENT_URI,
//                new String[]{
//                        FormsProviderAPI.FormsColumns.JR_FORM_ID,
//                        FormsProviderAPI.FormsColumns.DISPLAY_NAME,
//                        FormsProviderAPI.FormsColumns.FORM_FILE_PATH,
//                        FormsProviderAPI.FormsColumns.JR_VERSION
//                },
//                FormsProviderAPI.FormsColumns.JR_FORM_ID + " like ?",
//                new String[]{name + "%"},
//                null);
//    }

    private SearchFormResult findOdkFormOnScopedDir(String formFileName) {
        File formsDir = getOdkScopedStorageFormsDir();

        if (formsDir != null && formsDir.exists() && formsDir.isDirectory() && formsDir.canRead()) {
            for (File file : formsDir.listFiles()) {
                if (file.isFile() && file.getName().equalsIgnoreCase(formFileName)) {
                    return new SearchFormResult(file, SearchStatus.FOUND);
                }
            }
            return new SearchFormResult(null, SearchStatus.NOT_FOUND);
        } else {
            return new SearchFormResult(null, SearchStatus.PERMISSION_DENIED);
        }
    }

    private File getOdkScopedStorageFormsDir() {
        String odkBasePath = getOdkScopedStoragePath();
        File odkDir = new File(odkBasePath);

        if (!odkDir.exists()) {
            return null;
        }

        if (formUtilities.getOdkStorageType() == OdkStorageType.ODK_SCOPED_FOLDER_PROJECTS) {
            File projectsDir = new File(odkBasePath, "projects");

            if (projectsDir.exists()) {
                for (File projectSubDir : projectsDir.listFiles()) {
                    if (projectSubDir.isDirectory()) {
                        File formsDir = new File(projectSubDir, "forms");
                        if (formsDir.exists() && formsDir.isDirectory()) {
                            return formsDir;
                        }
                    }
                }
            }
        } else if (formUtilities.getOdkStorageType() == OdkStorageType.ODK_SCOPED_FOLDER_NO_PROJECTS) {
            File formsDir = new File(odkBasePath, "forms");
            if (formsDir.exists() && formsDir.isDirectory()) {
                return formsDir;
            }
        }

        return null;
    }

    private String getOdkScopedStoragePath() {
        return "/sdcard/Android/data/org.odk.collect.android/files/";
    }

    /**
     * Create instance file using File API (Android 10 and below)
     */
    private File createNewOdkFormInstanceFile(String xml, String jrFormId, File blankFormFile) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        df.setTimeZone(TimeZone.getDefault());
        String date = df.format(new Date());

        String dirName = jrFormId + "_" + date;
        String fileName = jrFormId + "_" + date + ".xml";

        // Get instances directory
        File formsDir = blankFormFile.getParentFile();
        File odkDir = formsDir.getParentFile();
        String destinationPath = odkDir.getAbsolutePath() + File.separator + "instances" +
                File.separator + dirName + File.separator;

        File baseDir = new File(destinationPath);
        File targetFile = new File(destinationPath, fileName);

        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                Log.e(TAG, "Failed to create directories: " + baseDir.getAbsolutePath());
                return null;
            }
        }

        try {
            FileWriter writer = new FileWriter(targetFile);
            writer.write(xml);
            writer.close();
            Log.d(TAG, "Created instance file: " + targetFile.getAbsolutePath());
            return targetFile;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write instance file", e);
            return null;
        }
    }

    /**
     * Create instance file using SAF (Android 11+)
     */
    private OdkScopedDirUtil.OdkFormInstance createNewOdkFormInstanceFile(String xml, String jrFormId,
                                                                          OdkScopedDirUtil.OdkFormObject formObject) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        df.setTimeZone(TimeZone.getDefault());
        String date = df.format(new Date());

        String dirName = jrFormId + "_" + date;
        String fileName = jrFormId + "_" + date + ".xml";

        OdkScopedDirUtil.OdkFormInstance formInstance = formObject.createNewInstance(dirName, fileName);

        if (formInstance == null) {
            Log.e(TAG, "Failed to create form instance");
            return null;
        }

        try {
            OutputStream outputStream = formInstance.getInstanceOutputStream();
            PrintStream output = new PrintStream(outputStream);
            output.print(xml);
            output.close();
            Log.d(TAG, "Created instance file via SAF");
            return formInstance;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write instance file via SAF", e);
            return null;
        }
    }

    /**
     * Insert instance using File API
     */
    private boolean insertNewOdkFormInstance(File targetFile, String displayName,
                                             String formId, String formVersion) {
        ContentValues values = new ContentValues();
        values.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, targetFile.getAbsolutePath());
        values.put(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, displayName);
        values.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, formId);

        if (formVersion != null) {
            values.put(InstanceProviderAPI.InstanceColumns.JR_VERSION, formVersion);
        }

        odkContentUri = resolver.insert(InstanceProviderAPI.InstanceColumns.CONTENT_URI, values);

        Log.d(TAG, "Inserted instance, URI: " + odkContentUri);
        return odkContentUri != null;
    }

    /**
     * Insert instance using SAF
     */
    private boolean insertNewOdkFormInstance(OdkScopedDirUtil.OdkFormInstance targetFile,
                                             String blankFormFilePath, String displayName,
                                             String formId, String formVersion) {
        String instanceFilePath = targetFile.getInstanceRelativePath();

        // For shared folder, use absolute path
        if (formUtilities.getOdkStorageType() == OdkStorageType.ODK_SHARED_FOLDER) {
            File formsDir = new File(blankFormFilePath).getParentFile();
            File odkDir = formsDir.getParentFile();
            instanceFilePath = odkDir.getAbsolutePath() + File.separator + "instances" +
                    File.separator + targetFile.getInstanceRelativePath();
        }

        ContentValues values = new ContentValues();
        values.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, instanceFilePath);
        values.put(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, displayName);
        values.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, formId);

        if (formVersion != null) {
            values.put(InstanceProviderAPI.InstanceColumns.JR_VERSION, formVersion);
        }

        odkContentUri = resolver.insert(InstanceProviderAPI.InstanceColumns.CONTENT_URI, values);

        Log.d(TAG, "Inserted instance (SAF), URI: " + odkContentUri);
        return odkContentUri != null;
    }

    // Helper classes

    class SearchFormResult {
        public File formFile;
        public SearchStatus status;

        public SearchFormResult(File formFile, SearchStatus status) {
            this.formFile = formFile;
            this.status = status;
        }
    }

    enum SearchStatus {
        FOUND, NOT_FOUND, PERMISSION_DENIED
    }
}