package org.openhds.hdsscapture.odk.task;

import android.net.Uri;

/**
 * Result object for ODK form loading operations
 */
public class OdkFormLoadResult {

    public enum Status {
        SUCCESS,
        ERROR_NO_ODK_INSTALLED,
        ERROR_PROVIDER_NA,
        ERROR_FORM_NOT_FOUND,
        ERROR_ODK_FOLDER_PERMISSION_DENIED,
        ERROR_ODK_CREATE_SAVED_INSTANCE_FILE,
        ERROR_ODK_INSERT_SAVED_INSTANCE
    }

    private Status status;
    private Uri contentUri;
    private String instanceUri;
    private String message;

    public OdkFormLoadResult(Status status) {
        this.status = status;
    }

    public OdkFormLoadResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public OdkFormLoadResult(Status status, Uri contentUri, String instanceUri) {
        this.status = status;
        this.contentUri = contentUri;
        this.instanceUri = instanceUri;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public String getInstanceUri() {
        return instanceUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

    public void setInstanceUri(String instanceUri) {
        this.instanceUri = instanceUri;
    }
}