package org.openhds.hdsscapture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;

@Entity(tableName = "apiurl")
public class ApiUrl extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @Expose
    public String codeUrl=AppConstants.url;

    public ApiUrl() {
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    protected ApiUrl(Parcel in) {
        this.codeUrl = in.readString();
    }

    public static final Creator<ApiUrl> CREATOR = new Creator<ApiUrl>() {
        @Override
        public ApiUrl createFromParcel(Parcel in) {
            return new ApiUrl(in);
        }

        @Override
        public ApiUrl[] newArray(int size) {
            return new ApiUrl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.codeUrl);
    }

    @Override
    public String toString() {
        return  codeUrl;
    }
}
