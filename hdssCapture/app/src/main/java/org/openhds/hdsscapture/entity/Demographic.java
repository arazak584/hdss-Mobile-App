package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "demographic",
        indices = {@Index(value = {"individual_uuid","fw_uuid","complete"}, unique = false)})
public class Demographic extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public String sttime;

    @Expose
    public String edtime;

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
    public String phone1;

    @Expose
    public String phone2;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer complete;

    @Expose
    public Integer phone;

    @Expose
    public String visit_uuid;
    @Expose
    public String comment;

    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    @Expose
    public Integer denomination;
    @Expose
    public Integer akan_tribe;

    public Demographic(){}

    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public void setStatus(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            status = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getApproveDate() {
        if (approveDate == null) return "";
        return g.format(approveDate);
    }

    public void setApproveDate(String approveDate) {
        try {
            this.approveDate = g.parse(approveDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
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
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
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

    @Bindable
    public String getComp_yrs() {
        return comp_yrs == null ? "" : String.valueOf(comp_yrs);
    }

    public void setComp_yrs(String comp_yrs) {

        try {
            this.comp_yrs = (comp_yrs == null) ? null : Integer.valueOf(comp_yrs);
        } catch (NumberFormatException e) {
        }
    }

    public String getOccupation_oth() {
        return occupation_oth;
    }

    public void setOccupation_oth(String occupation_oth) {
        this.occupation_oth = occupation_oth;
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

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public Integer getDenomination() {
        return denomination;
    }

    public void setDenomination(Integer denomination) {
        this.denomination = denomination;
    }

    public Integer getAkan_tribe() {
        return akan_tribe;
    }

    public void setAkan_tribe(Integer akan_tribe) {
        this.akan_tribe = akan_tribe;
    }

    protected Demographic(Parcel in) {
        this.individual_uuid = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.marital = in.readInt();
        this.education = in.readInt();
        this.occupation = in.readInt();
        this.religion = in.readInt();
        this.tribe = in.readInt();
        this.phone1 = in.readString();
        this.phone2 = in.readString();
        this.fw_uuid = in.readString();
        this.denomination = in.readInt();
        this.akan_tribe = in.readInt();
//        this.edtime = (Date) in.readSerializable();
//        this.sttime = (Date) in.readSerializable();
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
        dest.writeString(this.phone1);
        dest.writeString(this.phone2);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.denomination);
        dest.writeInt(this.akan_tribe);
//        dest.writeSerializable(this.edtime);
//        dest.writeSerializable(this.sttime);
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
            notifyPropertyChanged(BR._all);
        }
        if(tribe == null || tribe!=77){
            setTribe_oth(null);
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
            notifyPropertyChanged(BR._all);
        }
        if(religion == null || religion!=77){
            setReligion_oth(null);
        }

    }

    //SPINNERS ENTITY Education
    public void setEducations(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            education = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            education = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
        if(education != null && education==1){
            this.comp_yrs=AppConstants.NOSELECT;
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
            notifyPropertyChanged(BR._all);
        }
        if(occupation == null || occupation!=77){
            setOccupation_oth(null);
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

    //SPINNERS ENTITY
    public void setPhone(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            phone = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            phone = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
    }

    public void setAkan_tribe(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            akan_tribe = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            akan_tribe = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
    }

    public void setDenomination(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            denomination = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            denomination = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
    }

    private void patternSkipper(View view) {

        if (view != null) {

            if(tribe == null || tribe!=1)
                setAkan_tribe(null);

            if (religion == null || religion !=1)
                setDenomination(null);

            notifyPropertyChanged(BR._all);
        }
    }
}
