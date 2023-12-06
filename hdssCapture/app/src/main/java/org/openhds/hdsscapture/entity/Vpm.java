package org.openhds.hdsscapture.entity;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
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

@Entity(tableName = "vpm",
indices = {@Index(value = {"individual_uuid", "compno", "fw_uuid","complete"}, unique = false)})
public class Vpm extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public Date deathDate;

    @Expose
    public Date dob;

    @Expose
    public Date insertDate;

    @Expose
    public String firstName;

    @Expose
    public String lastName;

    @Expose
    public Integer gender;

    @Expose
    public String compno;

    @Expose
    public String visit_uuid;

    @Expose
    public Integer deathCause;

    @Expose
    public Integer deathPlace;

    @Expose
    public String deathPlace_oth;

    @Expose
    public String respondent;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer complete;
    @Expose
    public String extId;

    @Expose
    public String househead;

    @Expose
    public String compname;

    @Expose
    public String villname;

    @Expose
    public String villcode;


    public Vpm(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getDeathDate() {
        if (deathDate == null) return "";
        return f.format(deathDate);
    }

    public void setDeathDate(String deathDate) {
        try {
            this.deathDate = f.parse(deathDate);
        } catch (ParseException e) {
            System.out.println("DOD Date Error " + e.getMessage());
        }
    }

    public String getDob() {
        if (dob == null) return "";
        return f.format(dob);
    }

    public void setDob(String dob) {

        try {
            this.dob = f.parse(dob);
        } catch (ParseException e) {
            System.out.println("DOB Date Error " + e.getMessage());
        }
    }

    public String getInsertDate() {
        if (insertDate == null) return null;
        return f.format(insertDate);
    }
    public void setInsertDate(String insertDate) {
        if(insertDate == null ) this.insertDate=null;
        else
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
        return gender == null ? "" : String.valueOf(gender);
    }

    public void setGender(String gender) {

        try {
            this.gender = (gender == null) ? null : Integer.valueOf(gender);
        } catch (NumberFormatException e) {
        }
    }

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public Integer getDeathCause() {
        return deathCause;
    }

    public void setDeathCause(Integer causeofdeath) {
        this.deathCause = deathCause;
    }

    public Integer getDeathPlace() {
        return deathPlace;
    }

    public void setDeathPlace(Integer deathPlace) {
        this.deathPlace = deathPlace;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getHousehead() {
        return househead;
    }

    public void setHousehead(String househead) {
        this.househead = househead;
    }

    public String getCompname() {
        return compname;
    }

    public void setCompname(String compname) {
        this.compname = compname;
    }

    public String getVillname() {
        return villname;
    }

    public void setVillname(String villname) {
        this.villname = villname;
    }

    public String getVillcode() {
        return villcode;
    }

    public void setVillcode(String villcode) {
        this.villcode = villcode;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public String getDeathPlace_oth() {
        return deathPlace_oth;
    }

    public void setDeathPlace_oth(String deathPlace_oth) {
        this.deathPlace_oth = deathPlace_oth;
    }


    protected Vpm(Parcel in) {
        this.uuid = in.readString();
        this.individual_uuid = in.readString();
        this.extId = in.readString();
        this.deathDate = (Date) in.readSerializable();
        this.dob = (Date) in.readSerializable();
        this.insertDate = (Date) in.readSerializable();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.gender = in.readInt();
        this.compno = in.readString();
        this.compname = in.readString();
        this.visit_uuid = in.readString();
        this.deathCause = in.readInt();
        this.deathPlace = in.readInt();
        this.deathPlace_oth = in.readString();
        this.respondent = in.readString();
        this.househead = in.readString();
        this.fw_uuid = in.readString();

    }

    public static final Creator<Vpm> CREATOR = new Creator<Vpm>() {
        @Override
        public Vpm createFromParcel(Parcel in) {
            return new Vpm(in);
        }

        @Override
        public Vpm[] newArray(int size) {
            return new Vpm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.individual_uuid);
        dest.writeString(this.extId);
        dest.writeSerializable(this.deathDate);
        dest.writeSerializable(this.dob);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeInt(this.gender);
        dest.writeString(this.compno);
        dest.writeString(this.compname);
        dest.writeString(this.visit_uuid);
        dest.writeInt(this.deathCause);
        dest.writeInt(this.deathPlace);
        dest.writeString(this.deathPlace_oth);
        dest.writeString(this.respondent);
        dest.writeString(this.househead);
        dest.writeString(this.fw_uuid);

    }

    //SPINNERS ENTITY FOR DEATH PLACE
    public void setDeathPlace(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            deathPlace = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            deathPlace = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    //SPINNERS ENTITY FOR DEATH CAUSE
    public void setDeathCause(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            deathCause = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            deathCause = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    private void patternSkipper(View view) {

        if (view != null) {

            if(deathPlace == null || deathPlace!=77){
                setDeathPlace_oth(null);
            }

            notifyPropertyChanged(BR._all);
        }
    }

}
