package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "subdistrict")
public class Subdistrict implements Parcelable {

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

    @ColumnInfo(name = "districtId")
    private String districtId;

    @ColumnInfo(name = "level_uuid")
    private String level_uuid;

    public Subdistrict() {
    }

    @Ignore
    public Subdistrict(@NotNull String subdistrictId, String subdistrictNm, String districtId) {
        this.extId = subdistrictId;
        this.name = subdistrictNm;
        this.districtId = districtId;
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

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getLevel_uuid() {
        return level_uuid;
    }

    public void setLevel_uuid(String level_uuid) {
        this.level_uuid = level_uuid;
    }

    protected Subdistrict(Parcel in) {
        this.extId = in.readString();
        this.town = in.readString();
        this.name = in.readString();
        this.area = in.readString();
        this.districtId = in.readString();
        this.level_uuid = in.readString();
    }

    public static final Creator<Subdistrict> CREATOR = new Creator<Subdistrict>() {
        @Override
        public Subdistrict createFromParcel(Parcel in) {
            return new Subdistrict(in);
        }

        @Override
        public Subdistrict[] newArray(int size) {  return new Subdistrict[size]; }
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
        dest.writeString(this.districtId);
        dest.writeString(this.level_uuid);
    }

    @Override
    public String toString() {
        return name;
    }
}
