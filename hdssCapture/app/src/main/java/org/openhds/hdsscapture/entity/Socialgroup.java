package org.openhds.hdsscapture.entity;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
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
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "socialgroup",
        indices = {@Index(value = {"socialgroup_uuid","individual_uuid"}, unique = false)})
public class Socialgroup extends BaseObservable implements Parcelable {

    @SerializedName("socialgroup_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "socialgroup_uuid")
    @PrimaryKey
    public String socialgroup_uuid;

    @ColumnInfo(name = "houseExtId")
    public String houseExtId;

    @SerializedName("groupName")
    @Expose
    @ColumnInfo(name = "groupName")
    public String groupName;

    @Expose
    public String individual_uuid;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("fw_uuid")
    @Expose
    @ColumnInfo(name = "fw_uuid")
    public String fw_uuid;

    @SerializedName("groupType")
    @Expose
    @ColumnInfo(name = "groupType")
    public Integer groupType;

    @SerializedName("complete")
    @Expose
    @ColumnInfo(name = "complete")
    public Integer complete;

    public Socialgroup(){}

    @Ignore
    public Socialgroup(@NotNull String houseExtId, String groupName, String individual_uuid, Date insertDate, String fw_uuid, Integer groupType) {
        this.houseExtId = houseExtId;
        this.groupName = groupName;
        this.individual_uuid = individual_uuid;
        this.insertDate = insertDate;
        this.fw_uuid = fw_uuid;
        this.groupType = groupType;
    }

    @NotNull
    public String getHouseExtId() {
        return houseExtId;
    }

    public void setHouseExtId(@NotNull String houseExtId) {
        this.houseExtId = houseExtId;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

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

    @NotNull
    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(@NotNull String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    protected Socialgroup(Parcel in) {
        this.houseExtId = in.readString();
        this.groupName = in.readString();
        this.individual_uuid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.fw_uuid = in.readString();
        this.groupType = in.readInt();
    }

    public static final Creator<Socialgroup> CREATOR = new Creator<Socialgroup>() {
        @Override
        public Socialgroup createFromParcel(Parcel in) {
            return new Socialgroup(in);
        }

        @Override
        public Socialgroup[] newArray(int size) {
            return new Socialgroup[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.houseExtId);
        dest.writeString(this.groupName);
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.groupType);
    }

    @Override
    public String toString() {
        return houseExtId;
    }

    //SPINNERS ENTITY FOR FAMILY TYPE
    public void setGroupTypes(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            groupType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            groupType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(0, 114, 133));
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
