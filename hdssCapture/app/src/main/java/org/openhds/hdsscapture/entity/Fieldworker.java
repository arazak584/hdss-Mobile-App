package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "fieldworker")
public class Fieldworker implements Parcelable {


    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "status")
    public Integer status;

    public Fieldworker(@NotNull String fw_uuid, String username) {
        this.fw_uuid = fw_uuid;
        this.username = username;
    }

    @NotNull
    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(@NotNull String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Fieldworker() {
    }

    protected Fieldworker(Parcel in) {
        this.fw_uuid = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.fullName = in.readString();
    }

    public static final Creator<Fieldworker> CREATOR = new Creator<Fieldworker>() {
        @Override
        public Fieldworker createFromParcel(Parcel in) {
            return new Fieldworker(in);
        }

        @Override
        public Fieldworker[] newArray(int size) {
            return new Fieldworker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fw_uuid);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.fullName);
    }

    @Override
    public String toString() {
        return username ;
    }
}
