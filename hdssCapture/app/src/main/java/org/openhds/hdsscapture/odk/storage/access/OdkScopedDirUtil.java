package org.openhds.hdsscapture.odk.storage.access;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import org.openhds.hdsscapture.odk.storage.access.anthonymandra.framework.XDocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for accessing ODK directories using Scoped Storage (Android 11+)
 * Updated to handle newer ODK Collect versions
 */
public class OdkScopedDirUtil {

    private static final String TAG = "OdkScopedDirUtil";

    public static final String EXTERNAL_STORAGE_PROVIDER_AUTHORITY = "com.android.externalstorage.documents";
    public static final String ODK_SHARED_FOLDER_URI = "primary:odk";
    public static final String ODK_SCOPED_FOLDER_URI = "primary:Android/data/org.odk.collect.android/files";

    public static String PRIMARY_ANDROID_DOC_ID = "primary:Android/data/org.odk.collect.android/files";
    // This will be set dynamically based on ODK storage type
    //public static String PRIMARY_ANDROID_DOC_ID = ODK_SCOPED_FOLDER_URI;

    private Context mContext;
    private ContentResolver contentResolver;
    private OdkStorageType odkStorageType;
    private Uri odkDirectoryUri;

    public OdkScopedDirUtil(Context mContext, OdkStorageType odkStorageType) {
        this.mContext = mContext;
        this.contentResolver = mContext.getContentResolver();
        this.odkStorageType = odkStorageType;

        PRIMARY_ANDROID_DOC_ID = odkStorageType==OdkStorageType.ODK_SHARED_FOLDER ? ODK_SHARED_FOLDER_URI : ODK_SCOPED_FOLDER_URI;

        this.odkDirectoryUri = DocumentsContract.buildTreeDocumentUri(EXTERNAL_STORAGE_PROVIDER_AUTHORITY, PRIMARY_ANDROID_DOC_ID);
    }

    public Context getContext() {
        return mContext;
    }

    public OdkStorageType getStorageType() {
        return odkStorageType;
    }

    /**
     * Find a blank form in ODK Collect storage
     */
    public OdkFormObject findBlankForm(String formName) {
        Log.d(TAG, "Searching for form: " + formName);

        Uri odkFilesUri = odkDirectoryUri;
        XDocumentFile odkFilesDocFile = XDocumentFile.fromUri(mContext, odkFilesUri);

        if (odkFilesDocFile == null || !odkFilesDocFile.exists()) {
            Log.e(TAG, "ODK directory not accessible: " + odkFilesUri);
            return null;
        }

        Log.d(TAG, "ODK directory accessible, storage type: " + odkStorageType);

        if (odkStorageType == OdkStorageType.ODK_SHARED_FOLDER ||
                odkStorageType == OdkStorageType.ODK_SCOPED_FOLDER_NO_PROJECTS) {
            Log.d(TAG, "Using flat directory structure (shared or no projects)");
            return findBlankFormInDirectory(odkFilesDocFile, formName);
        }

        if (odkStorageType == OdkStorageType.ODK_SCOPED_FOLDER_PROJECTS) {
            Log.d(TAG, "Using projects directory structure");

            XDocumentFile projectsDocFile = odkFilesDocFile.findFile("projects");

            if (projectsDocFile != null && projectsDocFile.exists()) {
                Log.d(TAG, "Projects directory found");

                // Get all projects and try to find the blank form
                XDocumentFile[] projectFiles = projectsDocFile.listFiles();

                if (projectFiles == null || projectFiles.length == 0) {
                    Log.w(TAG, "No projects found in projects directory");
                    return null;
                }

                Log.d(TAG, "Found " + projectFiles.length + " project(s)");

                for (XDocumentFile projectFile : projectFiles) {
                    if (projectFile.isDirectory()) {
                        Log.d(TAG, "Checking project: " + projectFile.getName());
                        OdkFormObject foundObj = findBlankFormInDirectory(projectFile, formName);
                        if (foundObj != null) {
                            Log.d(TAG, "Form found in project: " + projectFile.getName());
                            return foundObj;
                        }
                    }
                }
            } else {
                Log.w(TAG, "Projects directory not found, trying direct search");
                // Fallback: try to find directly in files directory
                return findBlankFormInDirectory(odkFilesDocFile, formName);
            }
        }

        Log.w(TAG, "Blank form not found: " + formName);
        return null;
    }

    /**
     * Find blank form in a specific directory
     */
    private OdkFormObject findBlankFormInDirectory(XDocumentFile odkRootDocFile, String formName) {
        if (odkRootDocFile == null || !odkRootDocFile.exists()) {
            Log.e(TAG, "Directory does not exist or is null");
            return null;
        }

        Log.d(TAG, "Searching in directory: " + odkRootDocFile.getName());

        XDocumentFile formsDocFile = odkRootDocFile.findFile("forms");
        XDocumentFile instancesDocFile = odkRootDocFile.findFile("instances");

        if (formsDocFile == null || !formsDocFile.exists()) {
            Log.e(TAG, "Forms directory not found");
            return null;
        }

        if (!formsDocFile.isDirectory()) {
            Log.e(TAG, "Forms is not a directory");
            return null;
        }

        Log.d(TAG, "Forms directory found, searching for: " + formName);

        XDocumentFile[] formFiles = formsDocFile.listFiles();
        if (formFiles == null || formFiles.length == 0) {
            Log.w(TAG, "No forms found in forms directory");
            return null;
        }

        Log.d(TAG, "Found " + formFiles.length + " file(s) in forms directory");

        for (XDocumentFile formFile : formFiles) {
            String fileName = formFile.getName();
            Log.d(TAG, "Checking file: " + fileName);

            if (fileName != null && fileName.equalsIgnoreCase(formName)) {
                Log.d(TAG, "Match found: " + fileName);
                return new OdkFormObject(formFile, instancesDocFile);
            }
        }

        Log.w(TAG, "Form not found in this directory");
        return null;
    }

    /**
     * Represents an ODK form with its file and instances directory
     */
    public class OdkFormObject {
        private XDocumentFile formFile;
        private XDocumentFile instancesDirectoryFile;

        public OdkFormObject(XDocumentFile formFile, XDocumentFile instancesDirectoryFile) {
            this.formFile = formFile;
            this.instancesDirectoryFile = instancesDirectoryFile;

            Log.d(TAG, "Created OdkFormObject - Form: " +
                    (formFile != null ? formFile.getName() : "null") +
                    ", Instances dir: " +
                    (instancesDirectoryFile != null ? instancesDirectoryFile.getName() : "null"));
        }

        public XDocumentFile getFormFile() {
            return formFile;
        }

        public XDocumentFile getInstancesDirectoryFile() {
            return instancesDirectoryFile;
        }

        public Uri getFormUri() {
            return formFile.getUri();
        }

        public Uri getInstancesDirectoryUri() {
            return instancesDirectoryFile != null ? instancesDirectoryFile.getUri() : null;
        }

        public InputStream getFormInputStream() throws FileNotFoundException {
            if (formFile == null) {
                throw new FileNotFoundException("Form file is null");
            }
            return contentResolver.openInputStream(formFile.getUri());
        }

        /**
         * Create a new form instance
         */
        public OdkFormInstance createNewInstance(String directoryName, String fileName) {
            XDocumentFile instancesDirFile = this.getInstancesDirectoryFile();

            if (instancesDirFile == null) {
                Log.e(TAG, "Instances directory is null");
                return null;
            }

            if (!instancesDirFile.exists()) {
                Log.e(TAG, "Instances directory does not exist");
                return null;
            }

            Log.d(TAG, "Creating new instance: " + directoryName + "/" + fileName);

            XDocumentFile newInstanceSubDir = instancesDirFile.createDirectory(directoryName);

            if (newInstanceSubDir == null) {
                Log.e(TAG, "Failed to create instance subdirectory: " + directoryName);
                return null;
            }

            Log.d(TAG, "Instance subdirectory created: " + newInstanceSubDir.getUri());

            XDocumentFile newInstanceFile = newInstanceSubDir.createFile("text/xml", fileName);

            if (newInstanceFile == null) {
                Log.e(TAG, "Failed to create instance file: " + fileName);
                return null;
            }

            Log.d(TAG, "Instance file created: " + newInstanceFile.getUri());

            String relativePath = directoryName + File.separator + fileName;
            return new OdkFormInstance(this, newInstanceFile, relativePath);
        }
    }

    /**
     * Represents an ODK form instance
     */
    public class OdkFormInstance {
        private OdkFormObject formObject;
        private XDocumentFile instanceFile;
        private String instanceRelativePath;

        public OdkFormInstance(OdkFormObject formObject, XDocumentFile instanceFile,
                               String instanceRelativePath) {
            this.formObject = formObject;
            this.instanceFile = instanceFile;
            this.instanceRelativePath = instanceRelativePath;
        }

        public OdkFormObject getFormObject() {
            return formObject;
        }

        public XDocumentFile getInstanceFile() {
            return instanceFile;
        }

        public String getInstanceRelativePath() {
            return instanceRelativePath;
        }

        public Uri getInstanceUri() {
            return instanceFile != null ? instanceFile.getUri() : null;
        }

        public OutputStream getInstanceOutputStream() throws FileNotFoundException {
            if (instanceFile == null) {
                throw new FileNotFoundException("Instance file is null");
            }
            return contentResolver.openOutputStream(instanceFile.getUri());
        }

        public InputStream getInstanceInputStream() throws FileNotFoundException {
            if (instanceFile == null) {
                throw new FileNotFoundException("Instance file is null");
            }
            return contentResolver.openInputStream(instanceFile.getUri());
        }
    }
}