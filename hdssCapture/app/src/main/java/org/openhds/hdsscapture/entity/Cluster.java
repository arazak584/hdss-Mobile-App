package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "cluster")
public class Cluster extends BaseObservable implements Parcelable {

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

    @ColumnInfo(name = "villageId")
    private String villageId;

    @ColumnInfo(name = "level_uuid")
    private String level_uuid;

    public Cluster() {
    }

    @Ignore
    public Cluster(@NotNull String clusterId, String clusterNm, String villageId) {
        this.extId = clusterId;
        this.name = clusterNm;
        this.villageId = villageId;
    }

    @Bindable
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

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    public String getLevel_uuid() {
        return level_uuid;
    }

    public void setLevel_uuid(String level_uuid) {
        this.level_uuid = level_uuid;
    }

    protected Cluster(Parcel in) {
        this.extId = in.readString();
        this.town = in.readString();
        this.name = in.readString();
        this.area = in.readString();
        this.villageId = in.readString();
        this.level_uuid = in.readString();
    }

    public static final Creator<Cluster> CREATOR = new Creator<Cluster>() {
        @Override
        public Cluster createFromParcel(Parcel in) {
            return new Cluster(in);
        }

        @Override
        public Cluster[] newArray(int size) { return new Cluster[size]; }
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
        dest.writeString(this.villageId);
        dest.writeString(this.level_uuid);
    }

    @Override
    public String toString() {
        return name;
    }
}
