package org.openhds.hdsscapture.Views;

public class CompletedForm {

    public String uuid;
    public String formType;
    public String displayText;

    public CompletedForm(String uuid, String formType, String displayText) {
        this.uuid = uuid;
        this.formType = formType;
        this.displayText = displayText;
    }
}
