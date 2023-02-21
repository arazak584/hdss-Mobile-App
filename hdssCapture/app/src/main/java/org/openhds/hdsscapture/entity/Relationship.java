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

@Entity(tableName = "relationship")
public class Relationship extends BaseObservable implements Parcelable {

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

    @SerializedName("extIdB")
    @Expose
    @ColumnInfo(name = "extIdB")
    public String extIdB;

    @SerializedName("startDate")
    @Expose
    @ColumnInfo(name = "startDate")
    public Date startDate;

    @SerializedName("endDate")
    @Expose
    @ColumnInfo(name = "endDate")
    public Date endDate;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("startType")
    @Expose
    @ColumnInfo(name = "startType")
    public String startType;

    @SerializedName("endType")
    @Expose
    @ColumnInfo(name = "endType")
    public String endType;

    @SerializedName("aIsToB")
    @Expose
    @ColumnInfo(name = "aIsToB")
    public Integer aIsToB;


    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @Expose
    public Integer complete;


    public Relationship(){}

    public Relationship(@NotNull String extId, String extIdB, Date startDate, Date endDate, Date insertDate, String startType, String endType, Integer aIsToB, String fw) {
        this.extId = extId;
        this.extIdB = extIdB;
        this.startDate = startDate;
        this.endDate = endDate;
        this.insertDate = insertDate;
        this.startType = startType;
        this.endType = endType;
        this.aIsToB = aIsToB;
        this.fw = fw;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    public String getExtIdB() {
        return extIdB;
    }

    public void setExtIdB(String extIdB) {
        this.extIdB = extIdB;
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
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;
    }

    public String getInsertDate() {
        if (insertDate == null) return "";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        if(insertDate == null ) this.insertDate=null;
        else
            try {
                this.insertDate = f.parse(insertDate);
            } catch (ParseException e) {
                System.out.println("Visit Date Error " + e.getMessage());
            }
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public Integer getaIsToB() {
        return aIsToB;
    }

    public void setaIsToB(Integer aIsToB) {
        this.aIsToB = aIsToB;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    protected Relationship(Parcel in) {
        this.extId = in.readString();
        this.extIdB = in.readString();
        this.startDate = (Date) in.readSerializable();
        this.endDate = (Date) in.readSerializable();
        this.insertDate = (Date) in.readSerializable();
        this.startType = in.readString();
        this.endType = in.readString();
        this.aIsToB = in.readInt();
        this.fw = in.readString();
    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeString(this.extIdB);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.startType);
        dest.writeString(this.endType);
        dest.writeInt(this.aIsToB);
        dest.writeString(this.fw);
    }
}
