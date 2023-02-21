package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "inmigration")
public class Inmigration extends BaseObservable implements Parcelable {

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

    @SerializedName("migType")
    @Expose
    @ColumnInfo(name = "migType")
    public Integer migType;

    @SerializedName("reason")
    @Expose
    @ColumnInfo(name = "reason")
    public String reason;

    @SerializedName("origin")
    @Expose
    @ColumnInfo(name = "origin")
    public Integer origin;

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

    public Inmigration(){}

    @Ignore
    public Inmigration(@NotNull String extId, Date insertDate, Integer migType, String reason, Date recordedDate, String residency_uuid, String visitid, Integer origin, String fw) {
        this.extId = extId;
        this.insertDate = insertDate;
        this.migType = migType;
        this.reason = reason;
        this.recordedDate = recordedDate;
        this.residency_uuid = residency_uuid;
        this.visitid = visitid;
        this.origin = origin;
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

    public Integer getMigType() {
        return migType;
    }

    public void setMigType(Integer migType) {
        this.migType = migType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return "SELECT DATE OF INMIGRATION";
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

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    protected Inmigration(Parcel in) {
        this.extId = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.migType = in.readInt();
        this.reason = in.readString();
        this.recordedDate = (Date) in.readSerializable();
        this.residency_uuid = in.readString();
        this.visitid = in.readString();
        this.origin = in.readInt();
        this.fw = in.readString();
    }

    public static final Creator<Inmigration> CREATOR = new Creator<Inmigration>() {
        @Override
        public Inmigration createFromParcel(Parcel in) {
            return new Inmigration(in);
        }

        @Override
        public Inmigration[] newArray(int size) {
            return new Inmigration[size];
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
        dest.writeInt(this.migType);
        dest.writeString(this.reason);
        dest.writeSerializable(this.recordedDate);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.visitid);
        dest.writeInt(this.origin);
        dest.writeString(this.fw);
    }
}
