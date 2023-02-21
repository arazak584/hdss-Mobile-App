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

@Entity(tableName = "demographic")
public class Demographic extends BaseObservable implements Parcelable {

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

    @SerializedName("religion")
    @Expose
    @ColumnInfo(name = "religion")
    public Integer religion;

    @SerializedName("tribe")
    @Expose
    @ColumnInfo(name = "tribe")
    public Integer tribe;

    @SerializedName("education")
    @Expose
    @ColumnInfo(name = "education")
    public Integer education;

    @SerializedName("marital")
    @Expose
    @ColumnInfo(name = "marital")
    public Integer marital;

    @SerializedName("occupation")
    @Expose
    @ColumnInfo(name = "occupation")
    public Integer occupation;

    @SerializedName("Phone1")
    @Expose
    @ColumnInfo(name = "Phone1")
    public String Phone1;

    @SerializedName("phone2")
    @Expose
    @ColumnInfo(name = "phone2")
    public String phone2;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Demographic(){}

    @Ignore
    public Demographic (@NotNull String extId,Date insertDate, Integer religion, Integer tribe, Integer education, Integer marital, Integer occupation,String phone1, String phone2, String fw) {
        this.extId = extId;
        this.insertDate = insertDate;
        this.religion = religion;
        this.tribe = tribe;
        this.education = education;
        this.marital = marital;
        this.occupation = occupation;
        Phone1 = phone1;
        this.phone2 = phone2;
        this.fw = fw;
    }

    @NotNull
    public String getExtid() {
        return extId;
    }

    public void setExtid(@NotNull String extId) {
        this.extId = extId;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


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

    public Integer getReligion() {
        return religion;
    }

    public void setReligion(Integer religion) {
        this.religion = religion;
    }

    public Integer getTribe() {
        return tribe;
    }

    public void setTribe(Integer tribe) {
        this.tribe = tribe;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public Integer getMarital() {
        return marital;
    }

    public void setMarital(Integer marital) {
        this.marital = marital;
    }


    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        this.Phone1 = Phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer occupation) {
        this.occupation = occupation;
    }


    protected Demographic(Parcel in) {
        this.extId = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.marital = in.readInt();
        this.education = in.readInt();
        this.occupation = in.readInt();
        this.religion = in.readInt();
        this.tribe = in.readInt();
        this.Phone1 = in.readString();
        this.phone2 = in.readString();
        this.fw = in.readString();
    }

    public static final Creator<Demographic> CREATOR = new Creator<Demographic>() {
        @Override
        public Demographic createFromParcel(Parcel in) {
            return new Demographic(in);
        }

        @Override
        public Demographic[] newArray(int size) {
            return new Demographic[size];
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
        dest.writeInt(this.marital);
        dest.writeInt(this.education);
        dest.writeInt(this.occupation);
        dest.writeInt(this.religion);
        dest.writeInt(this.tribe);
        dest.writeString(this.Phone1);
        dest.writeString(this.phone2);
        dest.writeString(this.fw);
    }

    //SPINNERS ENTITY TRIBE
    public void setTribes(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            tribe = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            tribe = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY Religion
    public void setReligions(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            religion = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            religion = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY Education
    public void setEducations(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            religion = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            religion = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY Occupation
    public void setOccupations(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            occupation = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            occupation = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY Marital
    public void setMaritals(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            marital = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            marital = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
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
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }
}
