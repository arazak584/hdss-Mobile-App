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

@Entity(tableName = "outcome")
public class Pregnancyoutcome extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("outcomeDate")
    @Expose
    @ColumnInfo(name = "outcomeDate")
    public Date outcomeDate;

    @SerializedName("childextid")
    @Expose
    @ColumnInfo(name = "childextid")
    public String childextid;

    @SerializedName("type")
    @Expose
    @ColumnInfo(name = "type")
    public String type;

    @SerializedName("father_extid")
    @Expose
    @ColumnInfo(name = "father_extid")
    public String father_extid;

    @SerializedName("birthplace")
    @Expose
    @ColumnInfo(name = "birthplace")
    public Integer birthplace;

    @SerializedName("childEverborn")
    @Expose
    @ColumnInfo(name = "childEverborn")
    public String childEverborn;

    @SerializedName("visitid")
    @Expose
    @ColumnInfo(name = "visitid")
    public String visitid;

    @SerializedName("NumberofBirths")
    @Expose
    @ColumnInfo(name = "NumberofBirths")
    public String NumberofBirths;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @Expose
    public Integer complete;

    public Pregnancyoutcome(){}

    @Ignore
    public Pregnancyoutcome(@NotNull String extId, Date insertDate, Date outcomeDate, String childextid, String type, String father_extid, Integer birthplace, String childEverborn, String visitid, String numberofBirths, String fw) {
        this.extId = extId;
        this.insertDate = insertDate;
        this.outcomeDate = outcomeDate;
        this.childextid = childextid;
        this.type = type;
        this.father_extid = father_extid;
        this.birthplace = birthplace;
        this.childEverborn = childEverborn;
        this.visitid = visitid;
        NumberofBirths = numberofBirths;
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

    public String getOutcomeDate() {
        if (outcomeDate == null) return "SELECT DATE OF OUTCOME";
        return f.format(outcomeDate);
    }

    public void setOutcomeDate(String outcomeDate) {
        try {
            this.outcomeDate = f.parse(outcomeDate);

        } catch (ParseException e) {
            System.out.println("Outcome Date Error " + e.getMessage());
        }
    }


    public String getChildextid() {
        return childextid;
    }

    public void setChildextid(String childextid) {
        this.childextid = childextid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFather_extid() {
        return father_extid;
    }

    public void setFather_extid(String father_extid) {
        this.father_extid = father_extid;
    }

    public Integer getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(Integer birthplace) {
        this.birthplace = birthplace;
    }

    public String getChildEverborn() {
        return childEverborn;
    }

    public void setChildEverborn(String childEverborn) {
        this.childEverborn = childEverborn;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getNumberofBirths() {
        return NumberofBirths;
    }

    public void setNumberofBirths(String numberofBirths) {
        NumberofBirths = numberofBirths;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    protected Pregnancyoutcome(Parcel in) {
        this.extId = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.outcomeDate = (java.util.Date) in.readSerializable();
        this.childextid = in.readString();
        this.type = in.readString();
        this.father_extid = in.readString();
        this.birthplace = in.readInt();
        this.childEverborn = in.readString();
        this.visitid = in.readString();
        this.NumberofBirths = in.readString();
        this.fw = in.readString();
    }

    public static final Creator<Pregnancyoutcome> CREATOR = new Creator<Pregnancyoutcome>() {
        @Override
        public Pregnancyoutcome createFromParcel(Parcel in) {
            return new Pregnancyoutcome(in);
        }

        @Override
        public Pregnancyoutcome[] newArray(int size) {
            return new Pregnancyoutcome[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.outcomeDate);
        dest.writeString(this.childextid);
        dest.writeString(this.type);
        dest.writeString(this.father_extid);
        dest.writeInt(this.birthplace);
        dest.writeString(this.childEverborn);
        dest.writeString(this.visitid);
        dest.writeString(this.NumberofBirths);
        dest.writeString(this.fw);

    }

    //SPINNERS ENTITY
    public void setBirthplace (AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            birthplace = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            birthplace = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }
}
