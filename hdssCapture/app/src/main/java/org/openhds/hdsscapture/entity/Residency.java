package org.openhds.hdsscapture.entity;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
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
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "residency",
indices = {@Index(value = {"residency_uuid","individual_uuid","location_uuid","socialgroup_uuid"}, unique = false)})
public class Residency extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String residency_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date startDate;

    @Expose
    public Date endDate;

    @Expose
    public String individual_uuid;

    @Expose
    public String location_uuid;

    @Expose
    public String socialgroup_uuid;

    @Expose
    public Integer endType;

    @Expose
    public Integer startType;


    @Expose
    public String fw_uuid;

    @Expose
    private Integer rltn_head;

    @Expose
    public Integer complete= AppConstants.NOT_COMPLETE;

    public Residency(){}

    @Ignore
    public Residency(@NotNull String residency_uuid, Date insertDate, Date startDate, Date endDate, String individual_uuid, String location_uuid, String socialgroup_uuid, Integer endType, Integer startType, String fw_uuid) {
        this.residency_uuid = residency_uuid;
        this.insertDate = insertDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.individual_uuid = individual_uuid;
        this.location_uuid = location_uuid;
        this.socialgroup_uuid = socialgroup_uuid;
        this.endType = endType;
        this.startType = startType;
        this.fw_uuid = fw_uuid;

    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Bindable
    @NotNull
    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(@NotNull String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return "";
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

    @Bindable
    public String getStartDate() {
        if (startDate == null) return "Select Start Date";
        return f.format(startDate);
    }

    public void setStartDate(String startDate) {
        if(startDate == null ) this.startDate=null;
        else
            try {
                this.startDate = f.parse(startDate);
            } catch (ParseException e) {
                System.out.println("Start Date Error " + e.getMessage());
            }
    }

    @Bindable
    public String getEndDate() {
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;

    }

    @Bindable
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Bindable
    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    @Bindable
    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }


    @Bindable
    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    @Bindable
    public Integer getEndType() {
        return endType;
    }

    public void setEndType(Integer endType) {
        this.endType = endType;
    }

    @Bindable
    public Integer getStartType() {
        return startType;
    }

    public void setStartType(Integer startType) {
        this.startType = startType;
    }

    public Integer getRltn_head() {
        return rltn_head;
    }

    public void setRltn_head(Integer rltn_head) {
        this.rltn_head = rltn_head;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    protected Residency(Parcel in) {
        this.residency_uuid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.startDate = (java.util.Date) in.readSerializable();
        this.endDate = (java.util.Date) in.readSerializable();
        this.individual_uuid = in.readString();
        this.location_uuid = in.readString();
        this.socialgroup_uuid = in.readString();
        this.endType = in.readInt();
        this.startType = in.readInt();
        this.rltn_head = in.readInt();
        this.fw_uuid = in.readString();

    }

    public static final Creator<Residency> CREATOR = new Creator<Residency>() {
        @Override
        public Residency createFromParcel(Parcel in) {
            return new Residency(in);
        }

        @Override
        public Residency[] newArray(int size) {
            return new Residency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.residency_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
        dest.writeString(this.individual_uuid);
        dest.writeString(this.location_uuid);
        dest.writeString(this.socialgroup_uuid);
        dest.writeInt(this.endType);
        dest.writeInt(this.startType);
        dest.writeInt(this.rltn_head);
        dest.writeString(this.fw_uuid);

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
