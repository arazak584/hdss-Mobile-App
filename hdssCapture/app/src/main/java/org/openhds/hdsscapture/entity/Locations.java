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
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "locations",
        indices = {
                @Index(value = {"uuid"}, unique = true),
                @Index(value = {"compno"}, unique = true),
                @Index(value = {"compextId"}, unique = true),
                @Index(value = {"compno", "complete"}),
                @Index(value = {"houseExtId"}),
                @Index(value = {"locationLevel_uuid"}),
                @Index(value = {"extId"}),
                @Index(value = {"vill_extId"}),
                @Index(value = {"fw_uuid"}),
                @Index(value = {"complete"})
        })
public class Locations extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @SerializedName("compextId")
    @Expose
    @ColumnInfo(name = "compextId")
    public String compextId;

    @SerializedName("compno")
    @Expose
    @ColumnInfo(name = "compno")
    public String compno;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @SerializedName("locationLevel_uuid")
    @Expose
    @ColumnInfo(name = "locationLevel_uuid")
    public String locationLevel_uuid;

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

    @SerializedName("altitude")
    @Expose
    @ColumnInfo(name = "altitude")
    public String altitude;


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
    public Integer locationType=1;

    @SerializedName("complete")
    @Expose
    public Integer complete;

    @Expose
    public String extId;

    @Expose
    public String houseExtId;

    @Expose
    public String vill_extId;

    @SerializedName("edit")
    @Expose
    @ColumnInfo(name = "edit")
    public Integer edit;

    @SerializedName("site")
    @Expose
    @ColumnInfo(name = "site")
    public Integer site;
    @Expose
    public String sttime;

    @Expose
    public String edtime;
    @Expose
    public boolean selected;
    public Date updatedAt;

    public Locations(){}

    @Ignore
    public Locations(@NotNull String compextId, String compno, String locationName) {
        this.compextId = compextId;
        this.compno = compno;
        this.locationName = locationName;
    }


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Bindable
    public String getUpdatedAt() {
        if (updatedAt == null) return "";
        return g.format(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        try {
            this.updatedAt = f.parse(updatedAt);
        } catch (ParseException e) {
            System.out.println("updatedAt Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return "";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getSttime() {
        return sttime;
    }

    public void setSttime(String sttime) {
        this.sttime = sttime;
    }

    public String getEdtime() {
        return edtime;
    }

    public void setEdtime(String edtime) {
        this.edtime = edtime;
    }

    @Bindable
    public String getCompextId() {
        return compextId;
    }

//    public void setCompextId(String compextId) {
//        this.compextId = compextId;
//
//    }


    public String getLocationLevel_uuid() {
        return locationLevel_uuid;
    }

    public void setLocationLevel_uuid(String locationLevel_uuid) {
        this.locationLevel_uuid = locationLevel_uuid;
    }

    @Bindable
    @NotNull
    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @Bindable
    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
        generateCompextId(); // trigger compextId generation
        notifyPropertyChanged(BR.compno);
    }

//    public void setCompno(String compno) {
//        this.compno = compno;
//
//        // Only update compextId if both compno and extId are non-null
//        if (compno != null && extId != null) {
//            try {
//                if (extId.length() == 3) {
//                    this.compextId = extId + "00" + compno.substring(2, 6);
//                } else if (extId.length() == 4) {
//                    this.compextId = extId + "00" + compno.substring(4, 7);
//                }
//            } catch (StringIndexOutOfBoundsException e) {
//                e.printStackTrace(); // You can replace this with proper logging
//            }
//
//            notifyPropertyChanged(BR.compextId);
//        }
//        // else: leave compextId untouched
//    }

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
        notifyPropertyChanged(BR._all);
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
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

    @Bindable
    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    @Bindable
    public String getVill_extId() {
        return vill_extId;
    }

    public void setVill_extId(String vill_extId) {
        this.vill_extId = vill_extId;
        generateCompextId(); // trigger compextId generation
        notifyPropertyChanged(BR.vill_extId);
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    protected Locations(Parcel in) {
        this.compextId = in.readString();
        this.locationName = in.readString();
        this.locationLevel_uuid = in.readString();
        this.compno = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.fw_uuid = in.readString();
        this.status = in.readInt();
        this.locationType = in.readInt();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.accuracy = in.readString();
        this.extId = in.readString();
        this.vill_extId = in.readString();
    }

    public static final Creator<Locations> CREATOR = new Creator<Locations>() {
        @Override
        public Locations createFromParcel(Parcel in) {
            return new Locations(in);
        }

        @Override
        public Locations[] newArray(int size) {
            return new Locations[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.compextId);
        dest.writeString(this.locationName);
        dest.writeString(this.locationLevel_uuid);
        dest.writeString(this.compno);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.status);
        dest.writeInt(this.locationType);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.accuracy);
        dest.writeString(this.extId);
        dest.writeString(this.vill_extId);
    }

    @Override
    public String toString() {
        return compextId;
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

    //SPINNERS ENTITY
    public void setEdit(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            edit = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            edit = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY
    public void setSite(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            site = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            site = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    private void patternSkipper(View view) {

        if (view != null) {



            notifyPropertyChanged(BR._all);
        }
    }

    private void generateCompextId() {
        if (compno != null && extId != null) {
            try {
                if (extId.length() == 3) {
                    this.compextId = extId + "00" + compno.substring(2, 6);
                } else if (extId.length() == 4) {
                    this.compextId = extId + "00" + compno.substring(4, 7);
                }
                notifyPropertyChanged(BR.compextId);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace(); // log error
            }
        }
    }


}
