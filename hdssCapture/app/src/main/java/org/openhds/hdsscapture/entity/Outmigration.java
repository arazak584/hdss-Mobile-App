package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "outmigration",
indices = {@Index(value = {"individual_uuid", "residency_uuid","fw_uuid","complete"}, unique = false)})
public class Outmigration extends BaseObservable implements Parcelable {

    @SerializedName("residency_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "residency_uuid")
    @PrimaryKey
    public String residency_uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date startDate;

    @Expose
    public Integer destination;

    @Expose
    public Integer reason;

    @Expose
    public String reason_oth;

    @Expose
    public Date recordedDate;

    @Expose
    public String uuid;

    @Expose
    public String visit_uuid;

    @Expose
    public String fw_uuid;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;
    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public String location_uuid;

    @Expose
    public String socialgroup_uuid;

    public Outmigration(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getSttime() {
        return sttime;
    }

    public void setSttime(String sttime) {
        this.sttime = sttime;
    }

    public String getEdtime() {
        return edtime;
    }

    public void setEdtime(String edtime) {
        this.edtime = edtime;
    }
    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getInsertDate() {
        if (insertDate == null) return "";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public String getStartDate() {
        if (startDate == null) return "";
        return f.format(startDate);
    }

    public void setStartDate(String startDate) {
        try {
            this.startDate = f.parse(startDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public Integer getDestination() {
        return destination;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getReason_oth() {
        return reason_oth;
    }

    public void setReason_oth(String reason_oth) {
        this.reason_oth = reason_oth;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return null;
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            this.recordedDate = f.parse(recordedDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
    }

    @NotNull
    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(@NotNull String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }


    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    protected Outmigration(Parcel in) {
        this.individual_uuid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.destination = in.readInt();
        this.reason = in.readInt();
        this.recordedDate = (java.util.Date) in.readSerializable();
        this.residency_uuid = in.readString();
        this.visit_uuid = in.readString();
        this.fw_uuid = in.readString();
    }

    public static final Parcelable.Creator<Outmigration> CREATOR = new Parcelable.Creator<Outmigration>() {
        @Override
        public Outmigration createFromParcel(Parcel in) {
            return new Outmigration(in);
        }

        @Override
        public Outmigration[] newArray(int size) {
            return new Outmigration[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.destination);
        dest.writeInt(this.reason);
        dest.writeSerializable(this.recordedDate);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.visit_uuid);
        dest.writeString(this.fw_uuid);
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
    public void setDestination(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            destination = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            destination = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY
    public void setReason(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            reason = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            reason = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
        if(reason == null || reason!=77){
            setReason_oth(null);
        }

    }
}
