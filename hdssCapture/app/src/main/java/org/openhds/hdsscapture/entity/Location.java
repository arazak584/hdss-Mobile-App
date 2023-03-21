package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "location",
indices = {@Index(value = {"extId"}, unique = true)})
public class Location extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;


    @SerializedName("compno")
    @Expose
    @ColumnInfo(name = "compno")
    public String compno;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("clusterId")
    @Expose
    @ColumnInfo(name = "clusterId")
    public String clusterId;

    @SerializedName("longitude")
    @Expose
    @ColumnInfo(name = "longitude")
    public String longitude;

    @SerializedName("latitude")
    @Expose
    @ColumnInfo(name = "latitude")
    public String latitude;

    @SerializedName("accuracy")
    @Expose
    @ColumnInfo(name = "accuracy")
    public String accuracy;


    @SerializedName("locationName")
    @Expose
    @ColumnInfo(name = "locationName")
    public String locationName;

    @SerializedName("status")
    @Expose
    @ColumnInfo(name = "status")
    public Integer status;

    @SerializedName("locationType")
    @Expose
    @ColumnInfo(name = "locationType")
    public Integer locationType;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Location(){}

    @Ignore
    public Location(@NotNull String extId, String compno, String locationName) {
        this.extId = extId;
        this.compno = compno;
        this.locationName = locationName;
    }


    @NotNull
    @Bindable
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    @Bindable
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
//            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    @Bindable
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Bindable
    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
        if(clusterId != null && compno != null && compno.length()==6){
            this.extId = clusterId + "00" + compno.substring(2);
        }else{
            this.extId = "";
        }

    }

    @Bindable
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Bindable
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Bindable
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Bindable
    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    @Bindable
    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    @Bindable
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    protected Location(Parcel in) {
        this.extId = in.readString();
        this.locationName = in.readString();
        this.clusterId = in.readString();
        this.compno = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.fw = in.readString();
        this.status = in.readInt();
        this.locationType = in.readInt();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.accuracy = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeString(this.locationName);
        dest.writeString(this.clusterId);
        dest.writeString(this.compno);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.fw);
        dest.writeInt(this.status);
        dest.writeInt(this.locationType);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.accuracy);

    }

    @Override
    public String toString() {
        return extId;
    }


    //SPINNERS ENTITY COMPOUND STATUS
    public void setStatus(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            status = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            status = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY COMPLETE FORM FOR SYNC
    public void setComplete(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            complete = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            complete = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY FOR LOCATION TYPE
    public void setLocationType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            locationType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            locationType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }


}
