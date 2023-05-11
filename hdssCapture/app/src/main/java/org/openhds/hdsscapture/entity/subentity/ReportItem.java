package org.openhds.hdsscapture.entity.subentity;

public class ReportItem {
    public String event_name;
    public String form_name;
    public int count =0;

    public ReportItem(String event_name, String form_name, int count) {
        this.event_name = event_name;
        this.form_name = form_name;
        this.count = count;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}


