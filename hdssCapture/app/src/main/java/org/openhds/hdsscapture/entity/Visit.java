package org.openhds.hdsscapture.entity;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Entity(tableName = "visit",
        indices = {@Index(value = {"location_uuid","uuid"}, unique = false)})
public class Visit extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String extId;

    @Expose
    public String uuid;

    @Expose
    public String location_uuid;

    @Expose
    public Integer roundNumber;

    @Expose
    public Date insertDate;

    @Expose
    public Date visitDate;

    @Expose
    public Integer realVisit;

    @Expose
    public String fw_uuid;

    @Expose
    public String respondent;

    @Expose
    public String houseExtId;

    @Expose
    public String socialgroup_uuid;

    @Expose
    public Integer complete;

    public Visit(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    @Bindable
    public String getUuid() {
        return uuid;
    }

    @Bindable
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Bindable
    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    @Bindable
    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @Bindable
    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return "SELECT DATE OF VISIT";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getVisitDate() {
        if (visitDate == null) return "SELECT DATE OF VISIT";
        return f.format(visitDate);
    }

    public void setVisitDate(String visitDate) {
        try {
            this.visitDate = f.parse(visitDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getHouseExtId() {
        return houseExtId;
    }

    public void setHouseExtId(String houseExtId) {
        this.houseExtId = houseExtId;
    }

    @Bindable
    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    protected Visit(Parcel in) {

        this.extId = in.readString();
        this.uuid = in.readString();
        this.location_uuid = in.readString();
        this.roundNumber = in.readInt();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.visitDate = (java.util.Date) in.readSerializable();
        this.realVisit = in.readInt();
        this.fw_uuid = in.readString();
        this.respondent = in.readString();
        this.houseExtId = in.readString();
        this.socialgroup_uuid = in.readString();
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeString(this.uuid);
        dest.writeString(this.location_uuid);
        dest.writeInt(this.roundNumber);
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.visitDate);
        dest.writeInt(this.realVisit);
        dest.writeString(this.fw_uuid);
        dest.writeString(this.respondent);
        dest.writeString(this.houseExtId);
        dest.writeString(this.socialgroup_uuid);

    }

    @Override
    public String toString() {
        return houseExtId;
    }

    //SPINNERS ENTITY COMPLETE FORM FOR SYNC
    public void setComplete(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            complete = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            complete = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }



    //SPINNERS ENTITY
    public void setRealVisit(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            realVisit = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            realVisit = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

}
