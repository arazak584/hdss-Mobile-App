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


@Entity(tableName = "hierarchylevel")
public class HierarchyLevel extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    public String uuid;

    @ColumnInfo(name = "keyIdentifier")
    public Integer keyIdentifier;

    @ColumnInfo(name = "name")
    public String name;

    public HierarchyLevel() {
    }

   @Ignore
    public HierarchyLevel(@NotNull Integer keyIdentifier, String name) {
        this.keyIdentifier = keyIdentifier;
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

    public Integer getKeyIdentifier() {
        return keyIdentifier;
    }

    public void setKeyIdentifier(Integer keyIdentifier) {
        this.keyIdentifier = keyIdentifier;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected HierarchyLevel(Parcel in) {
        this.uuid = in.readString();
        this.keyIdentifier = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<HierarchyLevel> CREATOR = new Creator<HierarchyLevel>() {
        @Override
        public HierarchyLevel createFromParcel(Parcel in) {
            return new HierarchyLevel(in);
        }

        @Override
        public HierarchyLevel[] newArray(int size) {
            return new HierarchyLevel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeInt(this.keyIdentifier);
        dest.writeString(this.name);
    }

    @Override
    public String toString() {
        return  name;
    }
}
