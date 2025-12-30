package org.openhds.hdsscapture.odk.xml;

/**
 * Represents an ODK XForm definition
 */
public class XFormDef {
    public String formId;
    public String formName;
    public String formVersion;
    public String formFilePath;

    public XFormDef(String formId, String formName, String formVersion, String formFilePath) {
        this.formId = formId;
        this.formName = formName;
        this.formVersion = formVersion;
        this.formFilePath = formFilePath;
    }

    @Override
    public String toString() {
        return "XFormDef{" +
                "formId='" + formId + '\'' +
                ", formName='" + formName + '\'' +
                ", formVersion='" + formVersion + '\'' +
                '}';
    }
}