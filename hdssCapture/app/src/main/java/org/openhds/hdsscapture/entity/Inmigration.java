package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
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

@Entity(tableName = "inmigration")
public class Inmigration extends BaseObservable implements Parcelable {

    @SerializedName("img_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "img_uuid")
    @PrimaryKey
    public String img_uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Integer migType;

    @Expose
    public Integer reason;

    @Expose
    public String reason_oth;

    @Expose
    public Integer origin;

    @Expose
    public Date recordedDate;

    @Expose
    public String residency_uuid;

    @Expose
    public String visit_uuid;

    @Expose
    public String fw_uuid;

    @ColumnInfo(name = "complete")
    public Integer complete;

    public Inmigration(){}


    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


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

    public Integer getMigType() {
        return migType;
    }

    public void setMigType(Integer migType) {
        this.migType = migType;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return "";
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            this.recordedDate = f.parse(recordedDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
    }

    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    @NotNull
    public String getImg_uuid() {
        return img_uuid;
    }

    public void setImg_uuid(@NotNull String img_uuid) {
        this.img_uuid = img_uuid;
    }

    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getReason_oth() {
        return reason_oth;
    }

    public void setReason_oth(String reason_oth) {
        this.reason_oth = reason_oth;
    }

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }


    protected Inmigration(Parcel in) {
        this.img_uuid = in.readString();
        this.individual_uuid = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.migType = in.readInt();
        this.reason = in.readInt();
        this.recordedDate = (Date) in.readSerializable();
        this.residency_uuid = in.readString();
        this.visit_uuid = in.readString();
        this.origin = in.readInt();
        this.fw_uuid = in.readString();
    }

    public static final Creator<Inmigration> CREATOR = new Creator<Inmigration>() {
        @Override
        public Inmigration createFromParcel(Parcel in) {
            return new Inmigration(in);
        }

        @Override
        public Inmigration[] newArray(int size) {
            return new Inmigration[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.img_uuid);
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.migType);
        dest.writeInt(this.reason);
        dest.writeSerializable(this.recordedDate);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.visit_uuid);
        dest.writeInt(this.origin);
        dest.writeString(this.fw_uuid);
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
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY
    public void setReason(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            reason = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            reason = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    //SPINNERS ENTITY
    public void setMigType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            migType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            migType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY
    public void setOrigin(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            origin = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            origin = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }
}
