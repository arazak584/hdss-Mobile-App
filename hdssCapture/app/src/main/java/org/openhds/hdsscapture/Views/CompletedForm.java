package org.openhds.hdsscapture.Views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CompletedForm {

    public String uuid;
    public String formType;
    public String fullName;
    public long insertDate; // epoch timestamp

    public CompletedForm(String uuid, String formType,String fullName, long insertDate) {
        this.uuid = uuid;
        this.formType = formType;
        this.fullName = fullName;
        this.insertDate = insertDate;
    }

    public String getDisplayText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return formType + ": " + fullName + " (" + sdf.format(new Date(insertDate)) + ")";
    }

}
