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
indices = {@Index(value = {"extId","location","socialgroup","compno"}, unique = false)})
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
    public String extId;

    @Expose
    public String location;

    @Expose
    public String compno;

    @Expose
    public String socialgroup;

    @Expose
    public Integer endType;

    @Expose
    public Integer startType;

    @Expose
    public String visitid;

    @Expose
    public String fw;

    @Expose
    public Integer migType;

    @Expose
    public Integer reason;

    @Expose
    public Integer origin;

    @Expose
    public Integer destination;

    @Expose
    public Integer omgreason;

    @Expose
    public Integer complete;

    public Residency(){}

    @Ignore
    public Residency(@NotNull String uuid, Date insertDate, Date startDate, Date endDate, String extId, String location,
                     String compno, String socialgroup, Integer endType, Integer startType, String visitid, String fw,
                     Integer migType, Integer reason, Integer origin, Integer destination, Integer omgreason) {
        this.uuid = uuid;
        this.insertDate = insertDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.extId = extId;
        this.location = location;
        this.compno = compno;
        this.socialgroup = socialgroup;
        this.endType = endType;
        this.startType = startType;
        this.visitid = visitid;
        this.fw = fw;
        this.migType = migType;
        this.reason = reason;
        this.origin = origin;
        this.destination = destination;
        this.omgreason = omgreason;
    }

    @Ignore

    public Residency(@NotNull String uuid, Date insertDate, Date startDate, Date endDate, String extId, String location, String socialgroup, Integer endType, Integer startType, String visitid, String fw, Integer migType, Integer reason, Integer origin, Integer destination, Integer omgreason) {
        this.uuid = uuid;
        this.insertDate = insertDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.extId = extId;
        this.location = location;
        this.socialgroup = socialgroup;
        this.endType = endType;
        this.startType = startType;
        this.visitid = visitid;
        this.fw = fw;
        this.migType = migType;
        this.reason = reason;
        this.origin = origin;
        this.destination = destination;
        this.omgreason = omgreason;
    }


    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

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

    public String getEndDate() {
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;

    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
    }

    @Bindable
    public String getSocialgroup() {
        return socialgroup;
    }

    public void setSocialgroup(String socialgroup) {
        this.socialgroup = socialgroup;
    }

    public Integer getEndType() {
        return endType;
    }

    public void setEndType(Integer endType) {
        this.endType = endType;
    }

    public Integer getStartType() {
        return startType;
    }

    public void setStartType(Integer startType) {
        this.startType = startType;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public Integer getMigType() {
        return migType;
    }

    public void setMigType(Integer migType) {
        this.migType = migType;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public Integer getDestination() {
        return destination;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public Integer getOmgreason() {
        return omgreason;
    }

    public void setOmgreason(Integer omgreason) {
        this.omgreason = omgreason;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    protected Residency(Parcel in) {
        this.uuid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.startDate = (java.util.Date) in.readSerializable();
        this.endDate = (java.util.Date) in.readSerializable();
        this.extId = in.readString();
        this.location = in.readString();
        this.compno = in.readString();
        this.socialgroup = in.readString();
        this.endType = in.readInt();
        this.startType = in.readInt();
        this.visitid = in.readString();
        this.fw = in.readString();
        this.migType = in.readInt();
        this.reason = in.readInt();
        this.origin = in.readInt();
        this.destination = in.readInt();
        this.omgreason = in.readInt();
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
        dest.writeString(this.extId);
        dest.writeString(this.location);
        dest.writeString(this.compno);
        dest.writeString(this.socialgroup);
        dest.writeInt(this.endType);
        dest.writeInt(this.startType);
        dest.writeString(this.visitid);
        dest.writeString(this.fw);
        dest.writeInt(this.migType);
        dest.writeInt(this.reason);
        dest.writeInt(this.origin);
        dest.writeInt(this.destination);
        dest.writeInt(this.omgreason);
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
