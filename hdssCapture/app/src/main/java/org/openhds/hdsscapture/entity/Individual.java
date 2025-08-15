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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "individual",
indices = {@Index(value = {"uuid","ghanacard","firstName","lastName","compno","hohID","fw_uuid","complete","village"}, unique = false)})
public class Individual extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @Expose
    public String visit_uuid;

    @Expose
    @ColumnInfo(name = "extId")
    public String extId;

    @Expose
    @ColumnInfo(name = "ghanacard")
    public String ghanacard;

    @SerializedName("dob")
    @Expose
    @ColumnInfo(name = "dob")
    public Date dob;

    @Expose
    @ColumnInfo(name = "age")
    public Integer age = AppConstants.NOSELECT;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("firstName")
    @Expose
    @ColumnInfo(name = "firstName")
    public String firstName;

    @SerializedName("lastName")
    @Expose
    @ColumnInfo(name = "lastName")
    public String lastName;

    @SerializedName("gender")
    @Expose
    @ColumnInfo(name = "gender")
    public Integer gender;

    @SerializedName("dobAspect")
    @Expose
    @ColumnInfo(name = "dobAspect")
    public Integer dobAspect;

    @SerializedName("otherName")
    @Expose
    @ColumnInfo(name = "otherName")
    public String otherName;

    @SerializedName("mother_uuid")
    @Expose
    @ColumnInfo(name = "mother_uuid")
    public String mother_uuid;

    @SerializedName("father_uuid")
    @Expose
    @ColumnInfo(name = "father_uuid")
    public String father_uuid;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @Expose
    public Integer complete;

    @SerializedName("other")
    @Expose
    @ColumnInfo(name = "other")
    public Integer other;

    @SerializedName("gh")
    @Expose
    @ColumnInfo(name = "gh")
    public Integer gh;
    @Expose
    public Integer mother;
    @Expose
    public Integer father;

    @Expose
    public Integer f_age = 150;
    @Expose
    public Integer m_age = 150;

    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public Integer endType;

    @Expose
    public String compno;

    @Expose
    public String village;
    @Expose
    public String hohID;
    @Expose
    public Integer rltn_head;

    @Expose
    public Integer reason;

    @Expose
    public String reason_oth;

    @Expose
    public Integer origin;

    @Expose
    public String phone1;

    public Individual() {
    }

    @Ignore
    public Individual(@NotNull String uuid, String extId, Date dob, Integer age, Date insertDate, String firstName, String lastName, Integer gender, Integer dobAspect, String otherName, String mother_uuid, String father_uuid, String fw_uuid) {
        this.uuid = uuid;
        this.extId = extId;
        this.dob = dob;
        this.age = age;
        this.insertDate = insertDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dobAspect = dobAspect;
        this.otherName = otherName;
        this.mother_uuid = mother_uuid;
        this.father_uuid = father_uuid;
        this.fw_uuid = fw_uuid;
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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
    public String getDob() {
        if (dob == null) return null;
        return f.format(dob);
    }

    public void setDob(String dob) {
        try {
            this.dob = f.parse(dob);
            calculateAge();
        } catch (ParseException e) {
            System.out.println("Dob error " + e.getMessage());
        }
    }

    private void calculateAge() {
        if (dob == null) {
            // Handle the case where date of birth is not set
            age = -1; // or throw an exception, set a default age, etc.
            return;
        }
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(this.dob);
        Calendar nowCalendar = Calendar.getInstance();
        age = nowCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        // Adjust age if the birthday hasn't occurred yet this year
        if (nowCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
    }

    @Bindable
    public int getAge() {
        if (dob == null) {
            return 0;
        }
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar nowCalendar = Calendar.getInstance();
        int age = nowCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (nowCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
            System.out.println("Date error " + e.getMessage());
        }
    }


    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Bindable
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getDobAspect() {
        return dobAspect;
    }

    public void setDobAspect(Integer dobAspect) {
        this.dobAspect = dobAspect;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        if (otherName != null && otherName == "") {
            this.otherName = null;
        } else {
            this.otherName = otherName;
        }
    }


    public String getMother_uuid() {
        return mother_uuid;
    }

    public void setMother_uuid(String mother_uuid) {
        this.mother_uuid = mother_uuid;
    }

    public String getFather_uuid() {
        return father_uuid;
    }

    public void setFather_uuid(String father_uuid) {
        this.father_uuid = father_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @Bindable
    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    @Bindable
    public String getGhanacard() {
        return ghanacard;
    }

    public void setGhanacard(String ghanacard) {
        if (ghanacard != null && ghanacard == "") {
            this.ghanacard = null;
        } else {
            this.ghanacard = ghanacard;
        }
    }

    public Integer getOther() {
        return other;
    }

    public void setOther(Integer other) {
        this.other = other;
    }

    public Integer getGh() {
        return gh;
    }

    public void setGh(Integer gh) {
        this.gh = gh;
    }

    public String getF_age() {
        return f_age == null ? "" : String.valueOf(f_age);
    }

    public void setF_age(String f_age) {
        try {
            this.f_age = (f_age == null) ? null : Integer.valueOf(f_age);
        } catch (NumberFormatException e) {
        }
    }

    public String getM_age() {
        return m_age == null ? "" : String.valueOf(m_age);
    }

    public void setM_age(String m_age) {
        try {
            this.m_age = (m_age == null) ? null : Integer.valueOf(m_age);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public Integer getEndType() {
        return endType;
    }

    public void setEndType(Integer endType) {
        this.endType = endType;
    }
    @Bindable
    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
    }
    @Bindable
    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }
    @Bindable
    public String getHohID() {
        return hohID;
    }

    public void setHohID(String hohID) {
        this.hohID = hohID;
    }

    public Integer getRltn_head() {
        return rltn_head;
    }

    public void setRltn_head(Integer rltn_head) {
        this.rltn_head = rltn_head;
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
    @Bindable
    public void setReason_oth(String reason_oth) {
        this.reason_oth = reason_oth;
    }
    @Bindable
    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        if (phone1 != null && phone1.length() == 9) {
            this.phone1 = "0" + phone1;
        } else {
            this.phone1 = (phone1 != null && !phone1.trim().isEmpty()) ? phone1 : null;
        }
    }

    protected Individual(Parcel in) {
        this.uuid = in.readString();
        this.extId = in.readString();
        this.dob = (java.util.Date) in.readSerializable();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.otherName = in.readString();
        this.gender = in.readInt();
        this.dobAspect = in.readInt();
        this.mother_uuid = in.readString();
        this.father_uuid = in.readString();
        this.fw_uuid = in.readString();
        this.complete = in.readInt();
        this.ghanacard = in.readString();
        this.gh = in.readInt();
        this.other = in.readInt();
        this.mother = in.readInt();
        this.father = in.readInt();
        this.endType = in.readInt();
        this.compno = in.readString();
        this.village = in.readString();
        this.hohID = in.readString();
        this.phone1 = in.readString();

    }

    public static final Creator<Individual> CREATOR = new Creator<Individual>() {
        @Override
        public Individual createFromParcel(Parcel in) {
            return new Individual(in);
        }

        @Override
        public Individual[] newArray(int size) {
            return new Individual[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.extId);
        dest.writeSerializable(this.dob);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.otherName);
        dest.writeInt(this.gender);
        dest.writeInt(this.dobAspect);
        dest.writeString(this.mother_uuid);
        dest.writeString(this.father_uuid);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.complete);
        dest.writeString(this.ghanacard);
        dest.writeInt(this.gh);
        dest.writeInt(this.other);
        dest.writeInt(this.mother);
        dest.writeInt(this.father);
        dest.writeInt(this.endType);
        dest.writeString(this.compno);
        dest.writeString(this.village);
        dest.writeString(this.hohID);
        dest.writeString(this.phone1);

    }

    //SPINNERS ENTITY DOB ASPECT
    public void setDobAspects(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            dobAspect = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            dobAspect = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY Gender
    public void setGenders(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            gender = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            gender = kv.codeValue;
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
    public void setOther(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            other = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            other = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    //SPINNERS ENTITY
    public void setGh(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            gh = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            gh = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    //SPINNERS ENTITY
    public void setMother(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            mother = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            mother = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    //SPINNERS ENTITY
    public void setFather(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            father = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            father = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    public void setRltn_head(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            rltn_head = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            rltn_head = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

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
        }
        patternSkipper(view);


    }

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

    private void patternSkipper(View view) {

        if (view != null) {

            if (reason == null || reason != 77)
                setReason_oth(null);
        }

    }
}

