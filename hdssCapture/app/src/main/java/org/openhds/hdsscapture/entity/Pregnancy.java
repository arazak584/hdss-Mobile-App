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

@Entity(tableName = "pregnancy")
public class Pregnancy extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @SerializedName("extId")
    @Expose
    @ColumnInfo(name = "extId")
    public String extId;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("outcome")
    @Expose
    @ColumnInfo(name = "outcome")
    public Integer outcome;

    @SerializedName("recordedDate")
    @Expose
    @ColumnInfo(name = "recordedDate")
    public Date recordedDate;

    @SerializedName("expectedDeliveryDate")
    @Expose
    @ColumnInfo(name = "expectedDeliveryDate")
    public Date expectedDeliveryDate;

    @SerializedName("visitid")
    @Expose
    @ColumnInfo(name = "visitid")
    public String visitid;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @Expose
    public Integer complete;

    public Pregnancy(){}

    @Ignore
    public Pregnancy(@NotNull String uuid,String extId, Date insertDate, Integer outcome, Date recordedDate, Date expectedDeliveryDate, String visitid, String fw) {
        this.uuid = uuid;
        this.extId = extId;
        this.insertDate = insertDate;
        this.outcome = outcome;
        this.recordedDate = recordedDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.visitid = visitid;
        this.fw = fw;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extid) {
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

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return "SELECT DATE OF CONCEPTION";
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            this.recordedDate = f.parse(recordedDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
    }

    public String getExpectedDeliveryDate() {
        if (expectedDeliveryDate == null) return "SELECT DATE";
        return f.format(expectedDeliveryDate);
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        try {
            this.expectedDeliveryDate = f.parse(expectedDeliveryDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
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

    protected Pregnancy(Parcel in) {
        this.uuid = in.readString();
        this.extId = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.outcome = in.readInt();
        this.recordedDate = (java.util.Date) in.readSerializable();
        this.expectedDeliveryDate = (java.util.Date) in.readSerializable();
        this.visitid = in.readString();
        this.fw = in.readString();
    }

    public static final Creator<Pregnancy> CREATOR = new Creator<Pregnancy>() {
        @Override
        public Pregnancy createFromParcel(Parcel in) {
            return new Pregnancy(in);
        }

        @Override
        public Pregnancy[] newArray(int size) {
            return new Pregnancy[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.extId);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.outcome);
        dest.writeSerializable(this.recordedDate);
        dest.writeSerializable(this.expectedDeliveryDate);
        dest.writeString(this.visitid);
        dest.writeString(this.fw);

    }
}
