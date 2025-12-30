package org.openhds.hdsscapture.odk.listener;

import android.net.Uri;

import org.openhds.hdsscapture.odk.model.OdkFormLoadData;
import org.openhds.hdsscapture.odk.task.OdkFormLoadResult;

import java.util.Date;

/**
 * Listener for ODK form result callbacks
 */
public interface OdkFormResultListener {

    /**
     * Called when form is finalized/completed
     */
    void onFormFinalized(OdkFormLoadData loadData, Uri contentUri, String formId,
                         String instanceUri, String metaInstanceName, Date lastUpdatedDate);

    /**
     * Called when form is saved but not finalized
     */
    void onFormUnFinalized(OdkFormLoadData loadData, Uri contentUri, String formId,
                           String instanceUri, String metaInstanceName, Date lastUpdatedDate);

    /**
     * Called when user deletes the form
     */
    void onDeleteForm(OdkFormLoadData loadData, Uri contentUri, String instanceUri);

    /**
     * Called when form loading fails
     */
    void onFormLoadError(OdkFormLoadData loadData, Object result);

    /**
     * Called when saved form instance is not found
     */
    void onFormInstanceNotFound(OdkFormLoadData loadData, Uri contentUri);
}

