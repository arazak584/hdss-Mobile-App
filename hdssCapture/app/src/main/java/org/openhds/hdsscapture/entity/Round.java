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

@Entity(tableName = "round")
public class Round extends BaseObservable implements Parcelable {

    @PrimaryKey
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    public String uuid;

    @ColumnInfo(name = "roundNumber")
    public String roundNumber;

    @ColumnInfo(name = "remark")
    public String remark;

    @SerializedName("startDate")
    @Expose
    @ColumnInfo(name = "startDate")
    public Date startDate;

    @SerializedName("endDate")
    @Expose
    @ColumnInfo(name = "endDate")
    public Date endDate;

    public Round() {
    }


    @Ignore
    public Round(@NotNull String roundNumber, String remark) {
        this.roundNumber = roundNumber;
        this.remark = remark;
    }

    //public Round(String roundNumber, String remark) {
    //}


    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);


    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartDate() {
        if (startDate == null) return "Select Start Date";
        return f.format(startDate);
    }

    public void setStartDate(String startDate) {
        if(startDate == null ) this.startDate=null;
        else
            try {
                this.startDate = f.parse(startDate);
            } catch (ParseException e) {
                System.out.println("Start Date Error " + e.getMessage());
            }
    }

    public String getEndDate() {
        if (endDate == null) return "Select End Date";
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;
        else
            try {
                this.endDate = f.parse(endDate);
            } catch (ParseException e) {
                System.out.println("End Date Error " + e.getMessage());
            }
    }

    protected Round(Parcel in) {
        this.uuid = in.readString();
        this.roundNumber = in.readString();
        this.remark = in.readString();
        this.startDate = (Date) in.readSerializable();
        this.endDate = (Date) in.readSerializable();
    }

    public static final Creator<Round> CREATOR = new Creator<Round>() {
        @Override
        public Round createFromParcel(Parcel in) {
            return new Round(in);
        }

        @Override
        public Round[] newArray(int size) {
            return new Round[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.roundNumber);
        dest.writeString(this.remark);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
    }

    @Override
    public String toString() {
        return  remark;
    }
}
