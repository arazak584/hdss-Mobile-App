package org.openhds.hdsscapture.entity;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "death")
public class Death extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("dod")
    @Expose
    @ColumnInfo(name = "dod")
    public Date dod;

    @SerializedName("dob")
    @Expose
    @ColumnInfo(name = "dob")
    public Date dob;

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
    public String gender;

    @SerializedName("location")
    @Expose
    @ColumnInfo(name = "location")
    public String location;

    @SerializedName("socialgroup")
    @Expose
    @ColumnInfo(name = "socialgroup")
    public String socialgroup;

    @SerializedName("visitid")
    @Expose
    @ColumnInfo(name = "visitid")
    public String visitid;

    @SerializedName("causeofdeath")
    @Expose
    @ColumnInfo(name = "causeofdeath")
    public String causeofdeath;

    @SerializedName("placeofdeath")
    @Expose
    @ColumnInfo(name = "placeofdeath")
    public String placeofdeath;

    @SerializedName("respondent")
    @Expose
    @ColumnInfo(name = "respondent")
    public String respondent;

    @SerializedName("Phone1")
    @Expose
    @ColumnInfo(name = "Phone1")
    public String Phone1;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete = 1;

    public Death(){}

    @Ignore
    public Death(@NotNull String extId, Date dod, Date dob, Date insertDate, String firstName, String lastName, String gender, String location, String socialgroup, String visitid, String causeofdeath, String placeofdeath, String respondent, String phone1, String fw) {
        this.extId = extId;
        this.dod = dod;
        this.dob = dob;
        this.insertDate = insertDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.location = location;
        this.socialgroup = socialgroup;
        this.visitid = visitid;
        this.causeofdeath = causeofdeath;
        this.placeofdeath = placeofdeath;
        this.respondent = respondent;
        Phone1 = phone1;
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

    public String getDod() {
        if (dod == null) return "SELECT DATE OF DEATH";
        return f.format(dod);
    }

    public void setDod(String dod) {
        try {
            this.dod = f.parse(dod);
        } catch (ParseException e) {
            System.out.println("DOD Date Error " + e.getMessage());
        }
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSocialgroup() {
        return socialgroup;
    }

    public void setSocialgroup(String socialgroup) {
        this.socialgroup = socialgroup;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getCauseofdeath() {
        return causeofdeath;
    }

    public void setCauseofdeath(String causeofdeath) {
        this.causeofdeath = causeofdeath;
    }

    public String getPlaceofdeath() {
        return placeofdeath;
    }

    public void setPlaceofdeath(String placeofdeath) {
        this.placeofdeath = placeofdeath;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    protected Death(Parcel in) {
        this.extId = in.readString();
        this.dod = (java.util.Date) in.readSerializable();
        this.dob = (java.util.Date) in.readSerializable();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.gender = in.readString();
        this.location = in.readString();
        this.socialgroup = in.readString();
        this.visitid = in.readString();
        this.causeofdeath = in.readString();
        this.placeofdeath = in.readString();
        this.respondent = in.readString();
        this.Phone1 = in.readString();
        this.fw = in.readString();

    }

    public static final Creator<Death> CREATOR = new Creator<Death>() {
        @Override
        public Death createFromParcel(Parcel in) {
            return new Death(in);
        }

        @Override
        public Death[] newArray(int size) {
            return new Death[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extId);
        dest.writeSerializable(this.dod);
        dest.writeSerializable(this.dob);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.gender);
        dest.writeString(this.location);
        dest.writeString(this.socialgroup);
        dest.writeString(this.visitid);
        dest.writeString(this.causeofdeath);
        dest.writeString(this.placeofdeath);
        dest.writeString(this.respondent);
        dest.writeString(this.Phone1);
        dest.writeString(this.fw);

    }
}
