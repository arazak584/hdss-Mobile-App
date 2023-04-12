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
import java.util.Locale;


@Entity(tableName = "visit",
        indices = {@Index(value = {"location_uuid","visit_uuid"}, unique = false)})
public class Visit extends BaseObservable implements Parcelable {

    @SerializedName("visit_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "visit_uuid")
    @PrimaryKey
    public String visit_uuid;

    @ColumnInfo(name = "visitExtId")
    public String visitExtId;

    @SerializedName("location_uuid")
    @Expose
    @ColumnInfo(name = "location_uuid")
    public String location_uuid;

    @SerializedName("roundNumber")
    @Expose
    @ColumnInfo(name = "roundNumber")
    public Integer roundNumber;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("visitDate")
    @Expose
    @ColumnInfo(name = "visitDate")
    public Date visitDate;

    @SerializedName("realVisit")
    @Expose
    @ColumnInfo(name = "realVisit")
    public Integer realVisit;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @SerializedName("respondent")
    @ColumnInfo(name = "respondent")
    @Expose
    public String respondent;

    @SerializedName("houseExtId")
    @Expose
    public String houseExtId;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Visit(){}

    @Ignore
    public Visit(@NotNull String visit_uuid, String visitExtId, String location_uuid, Integer roundNumber, Date insertDate, Date visitDate, Integer realVisit, String fw_uuid) {
        this.visit_uuid = visit_uuid;
        this.visitExtId = visitExtId;
        this.location_uuid = location_uuid;
        this.roundNumber = roundNumber;
        this.insertDate = insertDate;
        this.visitDate = visitDate;
        this.realVisit = realVisit;
        this.fw_uuid = fw_uuid;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Bindable
    @NotNull
    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(@NotNull String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    @Bindable
    public String getVisitExtId() {
        return visitExtId;
    }

    public void setVisitExtId(String visitExtId) {
        this.visitExtId = visitExtId;

    }

    @Bindable
    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    @Bindable
    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @Bindable
    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return "SELECT DATE OF VISIT";
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
    public String getVisitDate() {
        if (visitDate == null) return "SELECT DATE OF VISIT";
        return f.format(visitDate);
    }

    public void setVisitDate(String visitDate) {
        try {
            this.visitDate = f.parse(visitDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    @Bindable
    public String getHouseExtId() {
        return houseExtId;
    }

    public void setHouseExtId(String houseExtId) {
        this.houseExtId = houseExtId;
    }

    protected Visit (Parcel in) {
        this.visitExtId = in.readString();
        this.fw_uuid = in.readString();
        this.visit_uuid = in.readString();
        this.roundNumber = in.readInt();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.visitDate = (java.util.Date) in.readSerializable();
        this.realVisit = in.readInt();
        this.respondent = in.readString();
        this.houseExtId = in.readString();
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.visitExtId);
        dest.writeString(this.fw_uuid);
        dest.writeString(this.visit_uuid);
        dest.writeInt(this.roundNumber);
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.visitDate);
        dest.writeInt(this.realVisit);
        dest.writeString(this.respondent);
        dest.writeString(this.houseExtId);
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
    public void setRealVisit(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            realVisit = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            realVisit = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(255, 0, 255));
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

}
