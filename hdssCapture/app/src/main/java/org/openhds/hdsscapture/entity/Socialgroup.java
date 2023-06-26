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
        indices = {@Index(value = {"uuid","individual_uuid","extId"}, unique = false)})
public class Socialgroup extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @ColumnInfo(name = "extId")
    public String extId;

    @SerializedName("groupName")
    @Expose
    @ColumnInfo(name = "groupName")
    public String groupName;

    @Expose
    public String individual_uuid;

    @SerializedName("visit_uuid")
    @Expose
    public String visit_uuid;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @NotNull
    @Expose
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


    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
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
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

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

    @Bindable
    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    protected Socialgroup(Parcel in) {
        this.uuid = in.readString();
        this.extId = in.readString();
        this.groupName = in.readString();
        this.individual_uuid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.fw_uuid = in.readString();
        this.groupType = in.readInt();
        this.visit_uuid = in.readString();
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
        dest.writeString(this.uuid);
        dest.writeString(this.extId);
        dest.writeString(this.groupName);
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.groupType);
        dest.writeString(this.visit_uuid);
    }

    @Override
    public String toString() {
        return extId;
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
