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
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "outmigration")
public class Outmigration extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("destination")
    @Expose
    @ColumnInfo(name = "destination")
    public String destination;

    @SerializedName("omgreason")
    @Expose
    @ColumnInfo(name = "omgreason")
    public String omgreason;

    @SerializedName("recordedDate")
    @Expose
    @ColumnInfo(name = "recordedDate")
    public Date recordedDate;

    @SerializedName("residency_uuid")
    @Expose
    @ColumnInfo(name = "residency_uuid")
    public String residency_uuid;

    @SerializedName("visitid")
    @Expose
    @ColumnInfo(name = "visitid")
    public String visitid;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Outmigration(){}

    @Ignore
    public Outmigration(@NotNull String extId, Date insertDate, String destination, String omgreason, Date recordedDate, String residency_uuid, String visitid, String fw) {
        this.extId = extId;
        this.insertDate = insertDate;
        this.destination = destination;
        this.omgreason = omgreason;
        this.recordedDate = recordedDate;
        this.residency_uuid = residency_uuid;
        this.visitid = visitid;
        this.fw = fw;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getReason() {
        return omgreason;
    }

    public void setReason(String reason) {
        this.omgreason = reason;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return "SELECT DATE OF OUTMIGRATION";
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            this.recordedDate = f.parse(recordedDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
    }

    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    protected Outmigration(Parcel in) {
        this.extId = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.destination = in.readString();
        this.omgreason = in.readString();
        this.recordedDate = (java.util.Date) in.readSerializable();
        this.residency_uuid = in.readString();
        this.visitid = in.readString();
        this.fw = in.readString();
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
        dest.writeString(this.extId);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.destination);
        dest.writeString(this.omgreason);
        dest.writeSerializable(this.recordedDate);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.visitid);
        dest.writeString(this.fw);
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
}
