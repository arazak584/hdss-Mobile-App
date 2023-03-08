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
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "visit")
public class Visit extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("location")
    @Expose
    @ColumnInfo(name = "location")
    public String location;

    @SerializedName("household")
    @Expose
    @ColumnInfo(name = "household")
    public String household;

    @SerializedName("roundNumber")
    @Expose
    @ColumnInfo(name = "roundNumber")
    public String roundNumber;

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

    @SerializedName("respondent")
    @Expose
    @ColumnInfo(name = "respondent")
    public String respondent;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Visit(){}

    @Ignore
    public Visit(@NotNull String extId, String location, String household, String roundNumber, Date insertDate, Date visitDate, Integer realVisit, String respondent, String fw) {
        this.extId = extId;
        this.location = location;
        this.household = household;
        this.roundNumber = roundNumber;
        this.insertDate = insertDate;
        this.visitDate = visitDate;
        this.realVisit = realVisit;
        this.respondent = respondent;
        this.fw = fw;

    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHousehold() {
        return household;
    }

    public void setHousehold(String household) {
        this.household = household;
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }

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

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

      protected Visit (Parcel in) {
        this.extId = in.readString();
        this.location = in.readString();
        this.household = in.readString();
        this.roundNumber = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.visitDate = (java.util.Date) in.readSerializable();
        this.realVisit = in.readInt();
        this.respondent = in.readString();
        this.fw = in.readString();
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
        dest.writeString(this.extId);
        dest.writeString(this.location);
        dest.writeString(this.household);
        dest.writeString(this.roundNumber);
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.visitDate);
        dest.writeInt(this.realVisit);
        dest.writeString(this.respondent);
        dest.writeString(this.fw);

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
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }
}
