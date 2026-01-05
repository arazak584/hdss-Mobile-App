package org.openhds.hdsscapture.entity;

import android.graphics.Color;
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

@Entity(tableName = "duplicate",
        indices = {
                @Index(value = {"individual_uuid"}, unique = true),
                @Index(value = {"individual_uuid", "complete"}),
                @Index(value = {"fw_uuid"}),
                @Index(value = {"complete"})
        })
public class Duplicate extends BaseObservable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public String uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date dob;

    @Expose
    public Integer numberofdup;

    @Expose
    public String fname;

    @Expose
    public String lname;

    @Expose
    public String dup_uuid;

    @Expose
    public Date dup_dob;

    @Expose
    public String dup_fname;

    @Expose
    public String dup_lname;

    @Expose
    public Integer complete;
    @Expose
    public Integer complete_n;

    @Expose
    public String fw_uuid;

    @Expose
    public String dup1_uuid;

    @Expose
    public Date dup1_dob;

    @Expose
    public String dup1_fname;

    @Expose
    public String dup1_lname;

    @Expose
    public String dup2_uuid;

    @Expose
    public Date dup2_dob;

    @Expose
    public String dup2_fname;

    @Expose
    public String dup2_lname;

    @Expose
    public String visit_uuid;
    @Expose
    public String dup_compno;
    @Expose
    public String dup1_compno;
    @Expose
    public String dup2_compno;
    @Expose
    public String compno;
    @Expose
    public String comment;
    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    public Date updatedAt;

    public Duplicate(){}

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
            System.out.println("Date Error " + e.getMessage());
        }
    }


    public void setNumberofdup(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            numberofdup = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    @Bindable
    public String getDob() {
        if (dob == null) return "";
        return f.format(dob);
    }

    public void setDob(String dob) {
        try {
            this.dob = f.parse(dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getDup_dob() {
        if (dup_dob == null) return "";
        return f.format(dup_dob);
    }

    public void setDup_dob(String dup_dob) {
        try {
            this.dup_dob = f.parse(dup_dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getDup1_dob() {
        if (dup1_dob == null) return "";
        return f.format(dup1_dob);
    }

    public void setDup1_dob(String dup1_dob) {
        try {
            this.dup1_dob = f.parse(dup1_dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    @Bindable
    public String getDup2_dob() {
        if (dup2_dob == null) return "";
        return f.format(dup2_dob);
    }

    public void setDup2_dob(String dup2_dob) {
        try {
            this.dup2_dob = f.parse(dup2_dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDup_uuid() {
        return dup_uuid;
    }

    public void setDup_uuid(String dup_uuid) {
        this.dup_uuid = dup_uuid;
    }

    public String getDup_fname() {
        return dup_fname;
    }

    public void setDup_fname(String dup_fname) {
        this.dup_fname = dup_fname;
    }

    public String getDup_lname() {
        return dup_lname;
    }

    public void setDup_lname(String dup_lname) {
        this.dup_lname = dup_lname;
    }

    public String getDup1_uuid() {
        return dup1_uuid;
    }

    public void setDup1_uuid(String dup1_uuid) {
        this.dup1_uuid = dup1_uuid;
    }

    public String getDup1_fname() {
        return dup1_fname;
    }

    public void setDup1_fname(String dup1_fname) {
        this.dup1_fname = dup1_fname;
    }

    public String getDup1_lname() {
        return dup1_lname;
    }

    public void setDup1_lname(String dup1_lname) {
        this.dup1_lname = dup1_lname;
    }

    public String getDup2_uuid() {
        return dup2_uuid;
    }

    public void setDup2_uuid(String dup2_uuid) {
        this.dup2_uuid = dup2_uuid;
    }

    public String getDup2_fname() {
        return dup2_fname;
    }

    public void setDup2_fname(String dup2_fname) {
        this.dup2_fname = dup2_fname;
    }

    public String getDup2_lname() {
        return dup2_lname;
    }

    public void setDup2_lname(String dup2_lname) {
        this.dup2_lname = dup2_lname;
    }

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
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

    private void patternSkipper(View view) {

        if (view != null) {


            if(numberofdup == null || numberofdup==1){
                dup1_dob=null;
                setDup1_uuid(null);
                setDup1_fname(null);
                setDup1_lname(null);
                dup2_dob=null;
                setDup2_uuid(null);
                setDup2_fname(null);
                setDup2_lname(null);
            }

            if(numberofdup == null || numberofdup==2){
                dup2_dob=null;
                setDup2_uuid(null);
                setDup2_fname(null);
                setDup2_lname(null);
            }

        notifyPropertyChanged(BR._all);
        }
    }
}
