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

    /**
     * Default constructor for reopening existing forms
     * Used when you only need to set formId and formInstanceUri
     */
    public OdkFormLoadData() {
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = false;
    }

    /**
     * Constructor for loading new form with OdkForm object
     */
    public OdkFormLoadData(OdkForm form, FilledForm preloadedData) {
        this.form = form;
        this.formId = (form != null) ? form.formID : null;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = false;
    }

    /**
     * Constructor for loading new form with just formId string
     */
    public OdkFormLoadData(String formId, FilledForm preloadedData) {
        this.form = null;
        this.formId = formId;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = false;
    }

    /**
     * Constructor with skip unfinalized check option
     */
    public OdkFormLoadData(OdkForm form, FilledForm preloadedData, boolean skipUnfinalizedFormCheck) {
        this.form = form;
        this.formId = (form != null) ? form.formID : null;
        this.preloadedData = preloadedData;
        this.isFormGroupLoad = false;
        this.skipUnfinalizedCheck = skipUnfinalizedFormCheck;
    }

    /**
     * Static factory method for reopening existing forms
     * Provides clearer intent than using default constructor
     */
    public static OdkFormLoadData forExistingForm(String formId, String instanceUri) {
        OdkFormLoadData loadData = new OdkFormLoadData();
        loadData.formId = formId;
        loadData.formInstanceUri = instanceUri;
        loadData.skipUnfinalizedCheck = true; // Usually skip check when reopening
        return loadData;
    }

    /**
     * Static factory method for reopening existing forms with OdkForm object
     */
    public static OdkFormLoadData forExistingForm(OdkForm form, String instanceUri) {
        OdkFormLoadData loadData = new OdkFormLoadData();
        loadData.form = form;
        loadData.formId = (form != null) ? form.formID : null;
        loadData.formInstanceUri = instanceUri;
        loadData.skipUnfinalizedCheck = true;
        return loadData;
    }
}