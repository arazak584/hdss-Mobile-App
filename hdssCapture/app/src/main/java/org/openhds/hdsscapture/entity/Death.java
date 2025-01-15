package org.openhds.hdsscapture.entity;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "death",
indices = {@Index(value = {"individual_uuid","residency_uuid","fw_uuid","complete"}, unique = false)})
public class Death extends BaseObservable implements Parcelable {

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
    public String sttime;

    @Expose
    public String edtime;

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
    public String deathCause_oth;

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
    public Integer edit;

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

    @Expose
    public Integer AgeAtDeath=0;

    @Expose
    public String residency_uuid;

    @Expose
    public String socialgroup_uuid;
    @Expose
    public String comment;

    @Expose
    public Integer status;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    @Expose
    public Integer estimated_dod;

    public Death(){}


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

    public void setEstimated_dod(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            estimated_dod = Integer.parseInt(TAG);
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

    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    public Integer getEdit() {
        return edit;
    }

    public void setEdit(Integer edit) {
        this.edit = edit;
    }

    public String getDeathCause_oth() {
        return deathCause_oth;
    }

    public void setDeathCause_oth(String deathCause_oth) {
        this.deathCause_oth = deathCause_oth;
    }

    public Integer getAgeAtDeath() {

        if (dob == null && deathDate==null) {
            return 0;
        }
        Calendar dobCalendar = Calendar.getInstance();
        Calendar dodCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        dodCalendar.setTime(deathDate);
        Calendar nowCalendar = Calendar.getInstance();
        int AgeAtDeath = dodCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (dodCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            AgeAtDeath--;
        }
        return AgeAtDeath;
    }

    public void setAgeAtDeath(Integer ageAtDeath) {
        AgeAtDeath = ageAtDeath;
    }

    protected Death(Parcel in) {
        this.uuid = in.readString();
        this.individual_uuid = in.readString();
        this.extId = in.readString();
        this.deathDate = (java.util.Date) in.readSerializable();
        this.dob = (java.util.Date) in.readSerializable();
        this.insertDate = (java.util.Date) in.readSerializable();
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
        this.AgeAtDeath = in.readInt();

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
        dest.writeInt(this.AgeAtDeath);

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

    public void setEdit(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            edit = Integer.parseInt(TAG);
        }
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

            if(deathPlace == null || deathPlace!=AppConstants.OTHER_SPECIFY){
                setDeathPlace_oth(null);
            }

            notifyPropertyChanged(BR._all);
        }
    }
}
