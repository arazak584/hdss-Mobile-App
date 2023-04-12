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
indices = {@Index(value = {"individual_uuid","lastName","firstName"}, unique = false)})
public class Individual extends BaseObservable implements Parcelable {

    @SerializedName("individual_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "individual_uuid")
    @PrimaryKey
    public String individual_uuid;

    @Expose
    @ColumnInfo(name = "extId")
    public String extId;

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
    public String mother_uuid = AppConstants.Mother;

    @SerializedName("father_uuid")
    @Expose
    @ColumnInfo(name = "father_uuid")
    public String father_uuid = AppConstants.Father;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @Expose
    public Integer complete;

    @Expose
    public Integer other;

    @Expose
    public String compextId;
    @Expose
    public String houseExtId;
    @Expose
    public Integer endType;

    @SerializedName("residency_uuid")
    @Expose
    public String residency_uuid;


    public Individual(){}

    @Ignore
    public Individual(@NotNull String individual_uuid, String extId, Date dob, Integer age, Date insertDate, String firstName, String lastName, Integer gender, Integer dobAspect, String otherName, String mother_uuid, String father_uuid, String fw_uuid) {
        this.individual_uuid = individual_uuid;
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
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Bindable
    public String getDob() {
        if (dob == null) return "SELECT DATE";
        return f.format(dob);
    }

    public void setDob(String dob) {
        try {
            this.dob = f.parse(dob);
        } catch (ParseException e) {
            System.out.println("Dob error " + e.getMessage());
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
        if(insertDate == null) this.insertDate = null;
        else
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            try {
                this.insertDate = new Date(Long.parseLong(insertDate));
            } catch (NumberFormatException ne) {
                System.out.println("InsertDate error1 " + e.getMessage());
                System.out.println("InsertDate error2 " + ne.getMessage());

            }
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
        this.otherName = otherName;
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
    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    protected Individual(Parcel in) {
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
        this.residency_uuid = in.readString();
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
        dest.writeString(this.residency_uuid);
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

}
