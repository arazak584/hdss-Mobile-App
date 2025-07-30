package org.openhds.hdsscapture.entity;

import android.graphics.Color;
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

@Entity(tableName = "listing",
        indices = {@Index(value = {"compextId","compno","location_uuid","fw_uuid","complete"}, unique = false)})
public class Listing extends BaseObservable {

    @SerializedName("compextId")
    @PrimaryKey
    @NotNull
    @Expose
    @ColumnInfo(name = "compextId")
    public String compextId;

    @Expose
    public String location_uuid;

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

    @SerializedName("status")
    @Expose
    @ColumnInfo(name = "status")
    public Integer status;

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

    @SerializedName("complete")
    @Expose
    public Integer complete;

    @Expose
    public String village;

    @Expose
    public String vill_extId;

    @Expose
    public String cluster_id;

    @Expose
    public String fw_name;

    @SerializedName("locationName")
    @Expose
    @ColumnInfo(name = "locationName")
    public String locationName;

    @SerializedName("correct_yn")
    @Expose
    public Integer correct_yn;

    @Expose
    public String repl_locationName;

    @SerializedName("edit_compno")
    @Expose
    public Integer edit_compno;

    public Listing(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

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

    @Bindable
    @NotNull
    public String getCompextId() {
        return compextId;
    }

//
//
//    private void setCompextId(@NotNull String compextId) {
//        this.compextId = compextId;
//    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getFw_name() {
        return fw_name;
    }

    public void setFw_name(String fw_name) {
        this.fw_name = fw_name;
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
//        if (compno != null && vill_extId != null) {
//            try {
//                if (vill_extId.length() == 3) {
//                    this.compextId = vill_extId + "00" + compno.substring(2, 6);
//                } else if (vill_extId.length() == 4) {
//                    this.compextId = vill_extId + "00" + compno.substring(4, 7);
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


    @Override
    public String toString() {
        return compextId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
        notifyPropertyChanged(BR._all);
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

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public Integer getEdit_compno() {
        return edit_compno;
    }

    public void setEdit_compno(Integer edit_compno) {
        this.edit_compno = edit_compno;
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
            notifyPropertyChanged(BR._all);
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

    //SPINNERS ENTITY COMPLETE FORM FOR SYNC
    public void setCorrect_yn(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            correct_yn = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            correct_yn = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    private void generateCompextId() {
        if (compno != null && vill_extId != null) {
            try {
                if (vill_extId.length() == 3) {
                    this.compextId = vill_extId + "00" + compno.substring(2, 6);
                } else if (vill_extId.length() == 4) {
                    this.compextId = vill_extId + "00" + compno.substring(4, 7);
                }
                notifyPropertyChanged(BR.compextId);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace(); // log error
            }
        }
    }

}
