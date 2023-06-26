package org.openhds.hdsscapture.entity;

import android.graphics.Color;
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

@Entity(tableName = "duplicate")
public class Duplicate extends BaseObservable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date dob;

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
    public String fw_uuid;

    public Duplicate(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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
