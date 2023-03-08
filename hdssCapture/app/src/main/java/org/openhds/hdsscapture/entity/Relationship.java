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

    @Expose
    public String extId;

    @Expose
    public String extIdB;

    @Expose
    public Date startDate;

    @Expose
    public Date endDate;

    @Expose
    public Date insertDate;

    @Expose
    public String startType;

    @Expose
    public String endType;

    @Expose
    public Integer aIsToB;

    @Expose
    public String fw;

    @Expose
    public Integer complete;
    @Expose
    private Integer mar;//Is this the first marriage of the woman?
    @Expose
    private Integer tnbch;//Total Number of biological children
    @Expose
    private Integer nchdm;//Number of biological children from this marriage
    @Expose
    private Integer polygamous;//Are you in a polygamous marriage
    @Expose
    private Integer nwive;//Number of wives of husband(including you)
    @Expose
    private Integer lcow;//Does women live in the same household with co-wife(s)
    @Expose
    private Integer mrank;//Woman's rank (In current marriage)


    public Relationship(){}

    @Ignore
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

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public Integer getMar() {
        return mar;
    }

    public void setMar(Integer mar) {
        this.mar = mar;
    }

    public Integer getTnbch() {
        return tnbch;
    }

    public void setTnbch(Integer tnbch) {
        this.tnbch = tnbch;
    }

    public Integer getNchdm() {
        return nchdm;
    }

    public void setNchdm(Integer nchdm) {
        this.nchdm = nchdm;
    }

    public Integer getPolygamous() {
        return polygamous;
    }

    public void setPolygamous(Integer polygamous) {
        this.polygamous = polygamous;
    }

    public Integer getNwive() {
        return nwive;
    }

    public void setNwive(Integer nwive) {
        this.nwive = nwive;
    }

    public Integer getLcow() {
        return lcow;
    }

    public void setLcow(Integer lcow) {
        this.lcow = lcow;
    }

    public Integer getMrank() {
        return mrank;
    }

    public void setMrank(Integer mrank) {
        this.mrank = mrank;
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
