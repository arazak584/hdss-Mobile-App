package org.openhds.hdsscapture.entity.subqueries;

import android.os.Parcel;
import android.os.Parcelable;

public class EventForm implements Parcelable {



    public String event_name;
    public String form_name;
    public Integer event_name_id = 0;
    public Integer complete;//complete status

    public EventForm(){}

    public EventForm(String event_name, String form_name, Integer complete) {

        this.event_name = event_name;
        this.form_name = form_name;
        this.complete = complete;
    }


    public EventForm(String event_name, Integer event_name_id, String form_name, Integer complete) {

        this.event_name = event_name;
        this.form_name = form_name;
        this.complete = complete;
        this.event_name_id = event_name_id;
    }


    protected EventForm(Parcel in){

        this.event_name = in.readString();
        this.event_name_id = in.readInt();
        this.form_name = in.readString();
        this.complete = in.readInt();
    }

    public static final Creator<EventForm> CREATOR = new Creator<EventForm>() {
        @Override
        public EventForm createFromParcel(Parcel in) {
            return new EventForm(in);
        }

        @Override
        public EventForm[] newArray(int size) {
            return new EventForm[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_name);
        dest.writeInt(this.event_name_id);
        dest.writeString(this.form_name);
        dest.writeInt(this.complete);
    }

}
