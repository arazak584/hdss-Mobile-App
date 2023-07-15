package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "locationhierarchy",
        indices = {@Index(value = {"uuid","parent_uuid"}, unique = false)})
public class Hierarchy extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "extId")
    private String extId;

    @ColumnInfo(name = "town")
    private String town;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "parent_uuid")
    private String parent_uuid;

    @ColumnInfo(name = "level_uuid")
    private String level_uuid;

    public Hierarchy() {
    }

   @Ignore
    public Hierarchy(@NotNull String extId, String name) {
        this.extId = extId;
        this.name = name;
    }


    @Bindable
    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @Bindable
    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }


    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Bindable
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

    @Bindable
    public String getParent_uuid() {
        return parent_uuid;
    }

    public void setParent_uuid(String parent_uuid) {
        this.parent_uuid = parent_uuid;
    }

    public String getLevel_uuid() {
        return level_uuid;
    }

    public void setLevel_uuid(String level_uuid) {
        this.level_uuid = level_uuid;
    }

    protected Hierarchy(Parcel in) {
        this.uuid = in.readString();
        this.extId = in.readString();
        this.town = in.readString();
        this.name = in.readString();
        this.area = in.readString();
        this.parent_uuid = in.readString();
        this.level_uuid = in.readString();
    }

    public static final Creator<Hierarchy> CREATOR = new Creator<Hierarchy>() {
        @Override
        public Hierarchy createFromParcel(Parcel in) {
            return new Hierarchy(in);
        }

        @Override
        public Hierarchy[] newArray(int size) {
            return new Hierarchy[size];
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
        dest.writeString(this.town);
        dest.writeString(this.name);
        dest.writeString(this.area);
        dest.writeString(this.parent_uuid);
        dest.writeString(this.level_uuid);
    }

    @Override
    public String toString() {
        return  name;
    }
}
