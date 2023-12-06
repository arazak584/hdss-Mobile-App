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
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "residency",
indices = {@Index(value = {"individual_uuid","location_uuid","socialgroup_uuid","fw_uuid","complete"}, unique = false)})
public class Residency extends BaseObservable implements Parcelable {

    @Expose
    @NotNull
    @PrimaryKey
    public String uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date startDate;

    @Expose
    public Date endDate;

    @Expose
    public Date dobs;

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
    public Integer rltn_head;

    @Expose
    public Integer complete;


    @SerializedName("loc")
    @Expose
    public String loc;

    @Expose
    public String old_residency;

    @Expose
    public Integer img;

    @Expose
    public Integer age;

    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public String hohID;

    public Residency(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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

    @Bindable
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

    @Bindable
    public String getStartDate() {
        if (startDate == null) return null;
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

    public String getDobs() {
        if (dobs == null) return null;
        return f.format(dobs);
    }

    public void setDobs(String dobs) {
        try {
            this.dobs = f.parse(dobs);
        } catch (ParseException e) {
            System.out.println("Dob error " + e.getMessage());
        }
    }

    public String getAge() {
        return age == null ? "" : String.valueOf(age);
    }

    public void setAge(String age) {
        try {
            this.age = (age == null) ? null : Integer.valueOf(age);
        } catch (NumberFormatException e) {
        }
    }

    @Bindable
    public String getEndDate() {
        if (endDate == null) return null;
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;
        else
            try {
                this.endDate = f.parse(endDate);
            } catch (ParseException e) {
                System.out.println("End Date Error " + e.getMessage());
            }
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
    @Bindable
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

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getOld_residency() {
        return old_residency;
    }

    public void setOld_residency(String old_residency) {
        this.old_residency = old_residency;
    }

    public String getHohID() {
        return hohID;
    }

    public void setHohID(String hohID) {
        this.hohID = hohID;
    }

    protected Residency(Parcel in) {
        this.uuid = in.readString();
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
        this.img = in.readInt();
        this.loc = in.readString();

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
        dest.writeString(this.uuid);
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
        dest.writeInt(this.img);
        dest.writeString(this.loc);
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
    public void setEndType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            endType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            endType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
        if(endType != null && endType==1){
            this.endDate=null;
        }

    }

    //SPINNERS ENTITY
    public void setStartType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            startType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            startType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    //SPINNERS ENTITY
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

    //SPINNERS ENTITY
    public void setImg(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            img = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            img = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }



}
