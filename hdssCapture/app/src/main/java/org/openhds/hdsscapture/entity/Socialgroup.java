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
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "socialgroup")
public class Socialgroup extends BaseObservable implements Parcelable {

    @SerializedName("extId")
    @Expose
    @NotNull
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("groupName")
    @Expose
    @ColumnInfo(name = "groupName")
    public String groupName;

    @SerializedName("headid")
    @Expose
    @ColumnInfo(name = "headid")
    public String headid;

    @SerializedName("insertDate")
    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @SerializedName("fw")
    @Expose
    @ColumnInfo(name = "fw")
    public String fw;

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
    public Socialgroup(@NotNull String extId, String groupName, String headid, Date insertDate, String fw, Integer groupType) {
        this.extId = extId;
        this.groupName = groupName;
        this.headid = headid;
        this.insertDate = insertDate;
        this.fw = fw;
        this.groupType = groupType;
    }

    @NotNull
    public String getExtId() {
        return extId;
    }

    public void setExtId(@NotNull String extId) {
        this.extId = extId;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHeadid() {
        return headid;
    }

    public void setHeadid(String headid) {
        this.headid = headid;
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

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    protected Socialgroup(Parcel in) {
        this.extId = in.readString();
        this.groupName = in.readString();
        this.headid = in.readString();
        this.insertDate = (java.util.Date) in.readSerializable();
        this.fw = in.readString();
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
        dest.writeString(this.extId);
        dest.writeString(this.groupName);
        dest.writeString(this.headid);
        dest.writeSerializable(this.insertDate);
        dest.writeString(this.fw);
        dest.writeInt(this.groupType);
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
}
