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

@Entity(tableName = "config")
public class Configsettings extends BaseObservable implements Parcelable {

    @PrimaryKey
    @Expose
    @NotNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "hoh_age")
    public Integer hoh_age;

    @ColumnInfo(name = "mother_age")
    public Integer mother_age;

    @ColumnInfo(name = "father_age")
    public Integer father_age;

    @ColumnInfo(name = "rel_age")
    public Integer rel_age;

    @SerializedName("earliestDate")
    @Expose
    @ColumnInfo(name = "earliestDate")
    public Date earliestDate;

    @ColumnInfo(name = "updates")
    public boolean updates;

    @ColumnInfo(name = "enumeration")
    public boolean enumeration;
    @Expose
    public String site;
    @Expose
    public String compno;
    public Integer mis;
    public Integer stb;


    public Configsettings() {
    }


    @Ignore
    public Configsettings(@NotNull String uuid, String remark) {

    }

    //public Round(String roundNumber, String remark) {
    //}


    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public Integer getHoh_age() {
        return hoh_age;
    }

    public void setHoh_age(Integer hoh_age) {
        this.hoh_age = hoh_age;
    }

    public Integer getMother_age() {
        return mother_age;
    }

    public void setMother_age(Integer mother_age) {
        this.mother_age = mother_age;
    }

    public Integer getFather_age() {
        return father_age;
    }

    public void setFather_age(Integer father_age) {
        this.father_age = father_age;
    }

    public Integer getRel_age() {
        return rel_age;
    }

    public void setRel_age(Integer rel_age) {
        this.rel_age = rel_age;
    }

    public boolean isUpdates() {
        return updates;
    }

    public void setUpdates(boolean updates) {
        this.updates = updates;
    }

    public boolean isEnumeration() {
        return enumeration;
    }

    public void setEnumeration(boolean enumeration) {
        this.enumeration = enumeration;
    }

    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
    }

    public Integer getMis() {
        return mis;
    }

    public void setMis(Integer mis) {
        this.mis = mis;
    }

    public Integer getStb() {
        return stb;
    }

    public void setStb(Integer stb) {
        this.stb = stb;
    }

    public String getEarliestDate() {
        if (earliestDate == null) return "Select Start Date";
        return f.format(earliestDate);
    }

    public void setEarliestDate(String startDate) {
        if(startDate == null ) this.earliestDate=null;
        else
            try {
                this.earliestDate = f.parse(startDate);
            } catch (ParseException e) {
                System.out.println("Start Date Error " + e.getMessage());
            }
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    protected Configsettings(Parcel in) {
        this.id = in.readString();
        this.hoh_age = in.readInt();
        this.mother_age = in.readInt();
        this.father_age = in.readInt();
        this.rel_age = in.readInt();
        this.earliestDate = (Date) in.readSerializable();
        this.site = in.readString();
    }

    public static final Creator<Configsettings> CREATOR = new Creator<Configsettings>() {
        @Override
        public Configsettings createFromParcel(Parcel in) {
            return new Configsettings(in);
        }

        @Override
        public Configsettings[] newArray(int size) {
            return new Configsettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.hoh_age);
        dest.writeInt(this.mother_age);
        dest.writeInt(this.father_age);
        dest.writeInt(this.rel_age);
        dest.writeSerializable(this.earliestDate);
        dest.writeString(this.site);
    }

    @Override
    public String toString() {
        return  id;
    }
}
