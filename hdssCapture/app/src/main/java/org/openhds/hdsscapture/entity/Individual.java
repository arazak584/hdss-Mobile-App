package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
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

@Entity(tableName = "individual",
indices = {@Index(value = {"extId"}, unique = true)})
public class Individual extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

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
    public Integer gender;

    @SerializedName("dobAspect")
    @Expose
    @ColumnInfo(name = "dobAspect")
    public Integer dobAspect;

    @SerializedName("nickName")
    @Expose
    @ColumnInfo(name = "nickName")
    public String nickName;

    @SerializedName("mother")
    @Expose
    @ColumnInfo(name = "mother")
    public String mother;

    @SerializedName("father")
    @Expose
    @ColumnInfo(name = "father")
    public String father;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    @Ignore
    public String location;

    public String socialgroup;

    public Individual(){}

    @Ignore
    public Individual(@NotNull String extId, Date dob, Date insertDate, String firstName, String lastName, Integer gender,  Integer dobAspect, String nickName,  String mother, String father, String fw, Integer complete) {
        this.extId = extId;
        this.dob = dob;
        this.insertDate = insertDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dobAspect = dobAspect;
        this.nickName = nickName;
        this.mother = mother;
        this.father = father;
        this.fw = fw;
        this.complete = complete;
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
            notifyPropertyChanged(BR.dob);
        } catch (ParseException e) {
            System.out.println("Dob error " + e.getMessage());
        }
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

    @Bindable
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
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



    protected Individual(Parcel in) {
        this.extId = in.readString();
        this.dob = (java.util.Date) in.readSerializable();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.nickName = in.readString();
        this.gender = in.readInt();
        this.dobAspect = in.readInt();
        this.mother = in.readString();
        this.father = in.readString();
        this.fw = in.readString();
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
        dest.writeString(this.nickName);
        dest.writeInt(this.gender);
        dest.writeInt(this.dobAspect);
        dest.writeString(this.mother);
        dest.writeString(this.father);
        dest.writeString(this.fw);
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

}
