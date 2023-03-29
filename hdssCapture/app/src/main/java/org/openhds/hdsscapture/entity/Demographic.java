package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "demographic")
public class Demographic extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String demo_uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Integer religion;

    @Expose
    public String religion_oth;

    @Expose
    public Integer tribe;

    @Expose
    public String tribe_oth;

    @Expose
    public Integer education;

    @Expose
    public Integer comp_yrs;

    @Expose
    public Integer marital;

    @Expose
    public Integer occupation;

    @Expose
    public String occupation_oth;

    @Expose
    public String Phone1;

    @Expose
    public String phone2;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer complete = AppConstants.NOT_COMPLETE;

    public Demographic(){}

    @NotNull
    public String getDemo_uuid() {
        return demo_uuid;
    }

    public void setDemo_uuid(@NotNull String demo_uuid) {
        this.demo_uuid = demo_uuid;
    }

    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


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

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer occupation) {
        this.occupation = occupation;
    }

    public String getReligion_oth() {
        return religion_oth;
    }

    public void setReligion_oth(String religion_oth) {
        this.religion_oth = religion_oth;
    }

    public String getTribe_oth() {
        return tribe_oth;
    }

    public void setTribe_oth(String tribe_oth) {
        this.tribe_oth = tribe_oth;
    }

    public Integer getComp_yrs() {
        return comp_yrs;
    }

    public void setComp_yrs(Integer comp_yrs) {
        this.comp_yrs = comp_yrs;
    }

    public String getOccupation_oth() {
        return occupation_oth;
    }

    public void setOccupation_oth(String occupation_oth) {
        this.occupation_oth = occupation_oth;
    }

    protected Demographic(Parcel in) {
        this.individual_uuid = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.marital = in.readInt();
        this.education = in.readInt();
        this.occupation = in.readInt();
        this.religion = in.readInt();
        this.tribe = in.readInt();
        this.Phone1 = in.readString();
        this.phone2 = in.readString();
        this.fw_uuid = in.readString();
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
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.marital);
        dest.writeInt(this.education);
        dest.writeInt(this.occupation);
        dest.writeInt(this.religion);
        dest.writeInt(this.tribe);
        dest.writeString(this.Phone1);
        dest.writeString(this.phone2);
        dest.writeString(this.fw_uuid);
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
