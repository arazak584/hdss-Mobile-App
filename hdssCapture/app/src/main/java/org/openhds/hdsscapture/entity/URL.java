package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "url")
public class URL extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "url")
    private String url;


    public URL() {
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    protected URL(Parcel in) {
        this.url = in.readString();
    }

    public static final Creator<URL> CREATOR = new Creator<URL>() {
        @Override
        public URL createFromParcel(Parcel in) {
            return new URL(in);
        }

        @Override
        public URL[] newArray(int size) {
            return new URL[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    @Override
    public String toString() {
        return  url;
    }
}
