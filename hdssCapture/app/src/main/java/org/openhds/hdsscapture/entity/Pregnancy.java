package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
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

@Entity(tableName = "pregnancy",
        indices = {
                @Index(value = {"uuid"}, unique = true),
                @Index(value = {"individual_uuid", "complete"}),
                @Index(value = {"individual_uuid"}),
                @Index(value = {"visit_uuid"}),
                @Index(value = {"fw_uuid"}),
                @Index(value = {"complete"})
        })
public class Pregnancy extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @SerializedName("individual_uuid")
    @Expose
    @ColumnInfo(name = "individual_uuid")
    public String individual_uuid;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @Expose
    public Date formcompldate;

    @SerializedName("outcome")
    @Expose
    @ColumnInfo(name = "outcome")
    public Integer outcome;

    @SerializedName("recordedDate")
    @Expose
    @ColumnInfo(name = "recordedDate")
    public Date recordedDate;

    @SerializedName("expectedDeliveryDate")
    @Expose
    @ColumnInfo(name = "expectedDeliveryDate")
    public Date expectedDeliveryDate;

    @SerializedName("visit_uuid")
    @Expose
    @ColumnInfo(name = "visit_uuid")
    public String visit_uuid;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;
    @Expose
    public Integer anteNatalClinic;//Have you been to an ANC clinic?
    @Expose
    public Integer ageOfPregFromPregNotes;//Age of pregnancy from pregnancy notes (In Weeks; Use the information in the ANC Booklet)
    @Expose
    public Integer estimatedAgeOfPreg;//Number of months pregnant (Use the information in the ANC Booklet)
    @Expose
    public Integer attend_you;//Who attended to you?
    @Expose
    public String attend_you_other;//Other, Specify
    @Expose
    public Integer first_rec;//How many months pregnant were you when you first received antenatal care for this pregnancy
    @Expose
    public Integer anc_visits;//How many Antenatal care visits have you made for this pregnancy
    @Expose
    public Integer why_no;//In no, Why?
    @Expose
    public String why_no_other;//Other, Specify
    @Expose
    public Integer own_bnet;//Do you have a bed net?
    @Expose
    public Integer how_many;//How many?
    @Expose
    public Integer bnet_sou;//Source of bednet
    @Expose
    public String bnet_sou_other;//Other, Specify
    @Expose
    public Integer bnet_loc;//Locations of bednet
    @Expose
    public String bnet_loc_other;//Other, Specify
    @Expose
    public Integer slp_bednet;//Did you sleep under a bed net last night?
    @Expose
    public Integer trt_bednet;//Is the bed net you slept under last night treated?
    @Expose
    public Date lastClinicVisitDate;//Last clinic visit date
    @Expose
    public Integer healthfacility;//Have you attended a Health Facility other than visiting for anc service?
    @Expose
    public Integer medicineforpregnancy;//Have you received any medicine for the pregnancy?
    @Expose
    public Integer ttinjection;//Have you received TT injection?
    @Expose
    public Integer first_preg;//Is this your first Pregnancy?
    @Expose
    public Integer pregnancyNumber;//Total number of pregnancies to date
    @Expose
    public Date outcome_date;//Select Date of Outcome

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    @Expose
    public String firstName;

    @Expose
    public String lastName;

    @Expose
    public Integer extra;

    @Expose
    public Integer id;
    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public String comment;

    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    @Expose
    public Integer preg_ready;//When you got pregnant, did you want to get pregnant at that time?
    @Expose
    public Integer family_plan;//Before this pregnancy,  did you ever use any Family Planning method to delay or prevent getting pregnant?
    @Expose
    public Integer plan_method;//If YES, which of the following methods of family planning did you use to delay or prevent getting pregnant?
    @Expose
    public String plan_method_oth;//Other Method, Specify
    public Date updatedAt;

    @SerializedName("pregnancyOrder")
    @Expose
    @ColumnInfo(name = "pregnancyOrder")
    public int pregnancyOrder=1;

    public Pregnancy(){}



    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Bindable
    public String getUpdatedAt() {
        if (updatedAt == null) return "";
        return g.format(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        try {
            this.updatedAt = f.parse(updatedAt);
        } catch (ParseException e) {
            System.out.println("updatedAt Date Error " + e.getMessage());
        }
    }

    private void calculateEddIfNeeded() {
        // Only calculate if recordedDate exists and anteNatalClinic is 2 or 3 (No ANC)
        if (recordedDate != null && anteNatalClinic != null && (anteNatalClinic == 2 || anteNatalClinic == 3)) {
            // Add 9 months to conception date (LMP) to get expected delivery date
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(recordedDate);
            cal.add(java.util.Calendar.MONTH, 9);
            this.expectedDeliveryDate = cal.getTime();
            //notifyPropertyChanged(BR._all);
        }
    }

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
    public String getFormcompldate() {
        if (formcompldate == null) return null;
        return f.format(formcompldate);
    }

    public void setFormcompldate(String formcompldate) {
        if(formcompldate == null ) this.formcompldate=null;
        else
            try {
                this.formcompldate = f.parse(formcompldate);
            } catch (ParseException e) {
                System.out.println("Visit Date Error " + e.getMessage());
            }
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
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

    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public String getInsertDate() {
        if (insertDate == null) return null;
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    public String getLastClinicVisitDate() {
        if (lastClinicVisitDate == null) return null;
        return f.format(lastClinicVisitDate);
    }

    public void setLastClinicVisitDate(String lastClinicVisitDate) {
        try {
            this.lastClinicVisitDate = f.parse(lastClinicVisitDate);
        } catch (ParseException e) {
            System.out.println("Clinic Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getOutcome_date() {
        if (outcome_date == null) return "";  // Return empty string, not null
        return f.format(outcome_date);
    }

    public void setOutcome_date(String outcome_date) {
        if (outcome_date == null || outcome_date.trim().isEmpty()) {
            this.outcome_date = null;
        } else {
            try {
                this.outcome_date = f.parse(outcome_date.trim());
            } catch (ParseException e) {
                System.out.println("Outcome Date Error: " + e.getMessage());
                this.outcome_date = null;
            }
        }
        notifyPropertyChanged(BR.outcome_date);  // CRITICAL: Notify binding
    }

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return null;
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            if (recordedDate == null || recordedDate.isEmpty()) {
                this.recordedDate = null;
            } else {
                this.recordedDate = f.parse(recordedDate);
            }
            calculateEddIfNeeded();
            notifyPropertyChanged(BR._all);

        } catch (ParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
        }
    }


    public String getExpectedDeliveryDate() {
        if (expectedDeliveryDate == null) return null;
        return f.format(expectedDeliveryDate);
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        try {
            this.expectedDeliveryDate = f.parse(expectedDeliveryDate);
        } catch (ParseException e) {
        }

    }

    public Integer getAnteNatalClinic() {
        return anteNatalClinic;
    }

    public void setAnteNatalClinic(Integer anteNatalClinic) {
        this.anteNatalClinic = anteNatalClinic;

    }

    public Integer getAttend_you() {
        return attend_you;
    }

    public void setAttend_you(Integer attend_you) {
        this.attend_you = attend_you;
    }

    public String getAttend_you_other() {
        return attend_you_other;
    }

    public void setAttend_you_other(String attend_you_other) {
        this.attend_you_other = attend_you_other;
    }

    public Integer getWhy_no() {
        return why_no;
    }

    public void setWhy_no(Integer why_no) {
        this.why_no = why_no;
    }

    public String getWhy_no_other() {
        return why_no_other;
    }

    public void setWhy_no_other(String why_no_other) {
        this.why_no_other = why_no_other;
    }

    public Integer getOwn_bnet() {
        return own_bnet;
    }

    public void setOwn_bnet(Integer own_bnet) {
        this.own_bnet = own_bnet;
    }

    public Integer getBnet_sou() {
        return bnet_sou;
    }

    public void setBnet_sou(Integer bnet_sou) {
        this.bnet_sou = bnet_sou;
    }

    public String getBnet_sou_other() {
        return bnet_sou_other;
    }

    public void setBnet_sou_other(String bnet_sou_other) {
        this.bnet_sou_other = bnet_sou_other;
    }

    public Integer getBnet_loc() {
        return bnet_loc;
    }

    public void setBnet_loc(Integer bnet_loc) {
        this.bnet_loc = bnet_loc;
    }

    public String getBnet_loc_other() {
        return bnet_loc_other;
    }

    public void setBnet_loc_other(String bnet_loc_other) {
        this.bnet_loc_other = bnet_loc_other;
    }

    public Integer getSlp_bednet() {
        return slp_bednet;
    }

    public void setSlp_bednet(Integer slp_bednet) {
        this.slp_bednet = slp_bednet;
    }

    public Integer getTrt_bednet() {
        return trt_bednet;
    }

    public void setTrt_bednet(Integer trt_bednet) {
        this.trt_bednet = trt_bednet;
    }

    public Integer getHealthfacility() {
        return healthfacility;
    }

    public void setHealthfacility(Integer healthfacility) {
        this.healthfacility = healthfacility;
    }

    public Integer getMedicineforpregnancy() {
        return medicineforpregnancy;
    }

    public void setMedicineforpregnancy(Integer medicineforpregnancy) {
        this.medicineforpregnancy = medicineforpregnancy;
    }

    public Integer getTtinjection() {
        return ttinjection;
    }

    public void setTtinjection(Integer ttinjection) {
        this.ttinjection = ttinjection;
    }

    public Integer getFirst_preg() {
        return first_preg;
    }

    public void setFirst_preg(Integer first_preg) {
        this.first_preg = first_preg;
    }

    @Bindable
    public String getFirst_rec() {
        return first_rec == null ? "" : String.valueOf(first_rec);
    }

    public void setFirst_rec(String first_rec) {

        try {
            this.first_rec = (first_rec == null) ? null : Integer.valueOf(first_rec);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public String getAnc_visits() {
        return anc_visits == null ? "" : String.valueOf(anc_visits);
    }

    public void setAnc_visits(String anc_visits) {

        try {
            this.anc_visits = (anc_visits == null) ? null : Integer.valueOf(anc_visits);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public String getEstimatedAgeOfPreg() {
        return estimatedAgeOfPreg == null ? "" : String.valueOf(estimatedAgeOfPreg);
    }

    public void setEstimatedAgeOfPreg(String estimatedAgeOfPreg) {

        try {
            this.estimatedAgeOfPreg = (estimatedAgeOfPreg == null) ? null : Integer.valueOf(estimatedAgeOfPreg);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public String getAgeOfPregFromPregNotes() {
        return ageOfPregFromPregNotes == null ? "" : String.valueOf(ageOfPregFromPregNotes);
    }

    public void setAgeOfPregFromPregNotes(String ageOfPregFromPregNotes) {

        this.estimatedAgeOfPreg = null;
        if (ageOfPregFromPregNotes == null) this.ageOfPregFromPregNotes = null;
        else
            try {
                this.ageOfPregFromPregNotes = Integer.valueOf(ageOfPregFromPregNotes);
                if (this.ageOfPregFromPregNotes != null ) {
                    this.estimatedAgeOfPreg = this.ageOfPregFromPregNotes/4;
                }
            } catch (NumberFormatException e) {
            }
        notifyPropertyChanged(BR.estimatedAgeOfPreg);

    }

    @Bindable
    public String getHow_many() {
        return how_many == null ? "" : String.valueOf(how_many);
    }

    public void setHow_many(String how_many) {

        try {
            this.how_many = (how_many == null) ? null : Integer.valueOf(how_many);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public String getPregnancyNumber() {
        return pregnancyNumber == null ? "" : String.valueOf(pregnancyNumber);
    }

    public void setPregnancyNumber(String pregnancyNumber) {

        try {
            this.pregnancyNumber = (pregnancyNumber == null) ? null : Integer.valueOf(pregnancyNumber);
        } catch (NumberFormatException e) {
        }
    }


    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public Integer getExtra() {
        return extra;
    }

    public void setExtra(Integer extra) {
        this.extra = extra;
    }

    protected Pregnancy(Parcel in) {

        this.insertDate = (java.util.Date) in.readSerializable();
        this.outcome = in.readInt();
        this.recordedDate = (java.util.Date) in.readSerializable();
        this.expectedDeliveryDate = (java.util.Date) in.readSerializable();
    }

    public static final Creator<Pregnancy> CREATOR = new Creator<Pregnancy>() {
        @Override
        public Pregnancy createFromParcel(Parcel in) {
            return new Pregnancy(in);
        }

        @Override
        public Pregnancy[] newArray(int size) {
            return new Pregnancy[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.outcome);
        dest.writeSerializable(this.recordedDate);
        dest.writeSerializable(this.expectedDeliveryDate);

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
    public void setExtra(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            extra = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            extra = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //SPINNERS ENTITY COMPLETE FORM FOR SYNC
    public void setAnteNatalClinic(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            anteNatalClinic = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            anteNatalClinic = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            calculateEddIfNeeded();
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setAttend_you(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            attend_you = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            attend_you = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setWhy_no(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            why_no = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            why_no = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setOwn_bnet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            own_bnet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            own_bnet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }


    //SPINNERS ENTITY
    public void setBnet_sou(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            bnet_sou = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            bnet_sou = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setBnet_loc(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            bnet_loc = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            bnet_loc = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setSlp_bednet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            slp_bednet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            slp_bednet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setTrt_bednet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            trt_bednet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            trt_bednet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setHealthfacility(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            healthfacility = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            healthfacility = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setMedicineforpregnancy(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            medicineforpregnancy = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            medicineforpregnancy = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setTtinjection(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            ttinjection = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            ttinjection = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setFirst_preg(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            first_preg = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            first_preg = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setOutcome(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            outcome = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    //SPINNERS ENTITY

    public void setPreg_ready(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            preg_ready = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            preg_ready = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setFamily_plan(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            family_plan = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            family_plan = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setPlan_method(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            plan_method = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            plan_method = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    private void patternSkipper(View view) {

        if (view != null) {

            if(anteNatalClinic == null || anteNatalClinic!=1){

                setAgeOfPregFromPregNotes(null);
                setEstimatedAgeOfPreg(null);
                lastClinicVisitDate=null;
                setAttend_you(null);
                setTtinjection(null);
                setFirst_rec(null);
                setAnc_visits(null);
            }
            if(anteNatalClinic == null || anteNatalClinic!=2){
                setWhy_no(null);
            }

            if(own_bnet == null || own_bnet!=1){
                setHow_many(null);
                setBnet_sou(null);
                setBnet_loc(null);
                setSlp_bednet(null);
            }

            if(slp_bednet == null || slp_bednet!=1){
                setTrt_bednet(null);
            }

            if(bnet_sou == null || bnet_sou!=77){
                setBnet_sou_other(null);
            }

            if(bnet_loc == null || bnet_loc!=77){
                setBnet_loc_other(null);
            }

            if(attend_you == null || attend_you!=77){
                setAttend_you_other(null);
            }

            if(why_no == null || why_no!=77){
                setWhy_no_other(null);
            }

            if(outcome == null || outcome!=1){
                outcome_date = null;
            }

            if(first_preg != null && first_preg==1){
                this.pregnancyNumber=AppConstants.COMPLETE;
            }

            if(family_plan==null || family_plan!=1){
                plan_method = null;
                plan_method_oth = null;
            }

            notifyPropertyChanged(BR._all);
        }

    }

}
