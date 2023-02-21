package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "region")
public class Region implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "extId")
    private String extId;

    @ColumnInfo(name = "town")
    private String town;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "countryId")
    private String countryId;

    @ColumnInfo(name = "level_uuid")
    private String level_uuid;

    public Region() {
    }

    @Ignore
    public Region(@NotNull String extId, String name, String countryId) {
        this.extId = extId;
        this.name = name;
        this.countryId = countryId;
    }



    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getLevel_uuid() {
        return level_uuid;
    }

    public void setLevel_uuid(String level_uuid) {
        this.level_uuid = level_uuid;
    }

    protected Region(Parcel in) {
        this.extId = in.readString();
        this.town = in.readString();
        this.name = in.readString();
        this.area = in.readString();
        this.countryId = in.readString();
        this.level_uuid = in.readString();
    }

    public static final Creator<Region> CREATOR = new Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeString(this.town);
        dest.writeString(this.name);
        dest.writeString(this.area);
        dest.writeString(this.countryId);
        dest.writeString(this.level_uuid);
    }

    @Override
    public String toString() {
        return name;
    }
}
