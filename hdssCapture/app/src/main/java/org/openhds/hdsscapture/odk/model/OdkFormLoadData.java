package org.openhds.hdsscapture.odk.model;

import org.openhds.hdsscapture.odk.OdkForm;

/**
 * Data class to hold all necessary information for loading an ODK form
 */
public class OdkFormLoadData {
    public OdkForm form;
    public String formId;
    public String formInstanceUri;
    public FilledForm preloadedData;
    public boolean isFormGroupLoad;
    public String formGroupId;
    public String formGroupName;
    public String formGroupInstanceUuid;
    public boolean skipUnfinalizedCheck;

    public OdkFormLoadData(OdkForm form, FilledForm preloadedData) {
        this.form = form;
        this.formId = (form != null) ? form.formID : null;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = false;
    }

    public OdkFormLoadData(String formId, FilledForm preloadedData) {
        this.form = null;
        this.formId = formId;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = false;
    }

    public OdkFormLoadData(OdkForm form, FilledForm preloadedData, boolean skipUnfinalizedFormCheck) {
        this.form = form;
        this.formId = (form != null) ? form.formID : null;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = skipUnfinalizedFormCheck;
    }
}