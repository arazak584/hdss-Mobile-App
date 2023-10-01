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
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "relationship")
public class Relationship extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    public String uuid;

    @NotNull
    @PrimaryKey
    @Expose
    public String individualA_uuid;

    @Expose
    public String individualB_uuid;

    @Expose
    public Date dob;

    @Expose
    public Date startDate;

    @Expose
    public Date endDate;

    @Expose
    public Date insertDate;

    @Expose
    public Integer endType  = 1;

    @Expose
    public Integer aIsToB;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer complete;
    @Expose
    public Integer mar;//Is this the first marriage of the woman?
    @Expose
    public Integer tnbch;//Total Number of biological children
    @Expose
    public Integer nchdm;//Number of biological children from this marriage
    @Expose
    public Integer polygamous;//Are you in a polygamous marriage
    @Expose
    public Integer nwive;//Number of wives of husband(including you)
    @Expose
    public Integer lcow;//Does women live in the same household with co-wife(s)
    @Expose
    public Integer mrank;//Woman's rank (In current marriage)
    @Expose
    public Date sttime;

    @Expose
    public Date edtime;


    public Relationship(){}

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
        if (sttime == null) return null;
        return f.format(sttime);
    }

    public void setSttime(String sttime) {
        try {
            this.sttime = f.parse(sttime);
        } catch (ParseException e) {
        }
    }

    public String getEdtime() {
        if (edtime == null) return null;
        return f.format(edtime);
    }

    public void setEdtime(String edtime) {
        try {
            this.edtime = f.parse(edtime);
        } catch (ParseException e) {
        }
    }

    public String getIndividualA_uuid() {
        return individualA_uuid;
    }

    public void setIndividualA_uuid(String individualA_uuid) {
        this.individualA_uuid = individualA_uuid;
    }

    public String getIndividualB_uuid() {
        return individualB_uuid;
    }

    public void setIndividualB_uuid(String individualB_uuid) {
        this.individualB_uuid = individualB_uuid;
    }

    @Bindable
    public String getDob() {
        if (dob == null) return null;
        return f.format(dob);
    }

    public void setDob(String dob) {
        try {
            this.dob = f.parse(dob);
        } catch (ParseException e) {
            System.out.println("Dob error " + e.getMessage());
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
    @Bindable
    public String getEndDate() {
        if (endDate == null) return null;
        return f.format(endDate);
    }

    public void setEndDate(String endDate) {
        if(endDate == null ) this.endDate=null;
    }

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return null;
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
                }
            }
    }

    @Bindable
    public Integer getEndType() {
        return endType;
    }

    public void setEndType(Integer endType) {
        this.endType = endType;
    }
    @Bindable
    public Integer getaIsToB() {
        return aIsToB;
    }

    public void setaIsToB(Integer aIsToB) {
        this.aIsToB = aIsToB;
    }


    public Integer getMar() {
        return mar;
    }

    public void setMar(Integer mar) {
        this.mar = mar;
    }

    public String getTnbch() {
        return tnbch == null ? "" : String.valueOf(tnbch);
    }

    public void setTnbch(String tnbch) {
        if (tnbch == null) this.tnbch = null;
        else
            try {
                this.tnbch = Integer.valueOf(tnbch);
            } catch (NumberFormatException e) {
            }
    }


    public String getNchdm() {
        return nchdm == null ? "" : String.valueOf(nchdm);
    }

    public void setNchdm(String nchdm) {
        if (nchdm == null) this.nchdm = null;
        else
            try {
                this.nchdm = Integer.valueOf(nchdm);
            } catch (NumberFormatException e) {
            }
    }

    public Integer getPolygamous() {
        return polygamous;
    }

    public void setPolygamous(Integer polygamous) {
        this.polygamous = polygamous;
    }

    public String getNwive() {
        return nwive == null ? "" : String.valueOf(nwive);
    }

    public void setNwive(String nwive) {
        try {
            this.nwive = (nwive == null) ? null : Integer.valueOf(nwive);
        } catch (NumberFormatException e) {
        }
    }

    public Integer getLcow() {
        return lcow;
    }

    public void setLcow(Integer lcow) {
        this.lcow = lcow;
    }

    public String getMrank() {
        return mrank == null ? "" : String.valueOf(mrank);
    }

    public void setMrank(String mrank) {
        try {
            this.mrank = (mrank == null) ? null : Integer.valueOf(mrank);
        } catch (NumberFormatException e) {
        }
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    protected Relationship(Parcel in) {
        this.uuid = in.readString();
        this.individualA_uuid = in.readString();
        this.individualB_uuid = in.readString();
        this.startDate = (Date) in.readSerializable();
        this.endDate = (Date) in.readSerializable();
        this.insertDate = (Date) in.readSerializable();
        this.endType = in.readInt();
        this.aIsToB = in.readInt();
        this.fw_uuid = in.readString();
        this.mar = in.readInt();
        this.tnbch = in.readInt();
        this.nchdm = in.readInt();
        this.polygamous = in.readInt();
        this.nwive = in.readInt();
        this.lcow = in.readInt();
        this.mrank = in.readInt();

    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.individualA_uuid);
        dest.writeString(this.individualB_uuid);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.endType);
        dest.writeInt(this.aIsToB);
        dest.writeString(this.fw_uuid);
        dest.writeInt(this.mar);
        dest.writeInt(this.tnbch);
        dest.writeInt(this.nchdm);
        dest.writeInt(this.polygamous);
        dest.writeInt(this.nwive);
        dest.writeInt(this.lcow);
        dest.writeInt(this.mrank);

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
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setAIsToB(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            aIsToB = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            aIsToB = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

        //SPINNERS ENTITY
        public void setMar(AdapterView<?> parent, View view, int position, long id) {

            if (position != parent.getSelectedItemPosition()) {
                parent.setSelection(position);
            }
            if (position == 0) {
                mar = AppConstants.NOSELECT;
            } else {
                final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
                mar = kv.codeValue;
                ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
            }
            patternSkipper(view);

    }

        //SPINNERS ENTITY
        public void setPolygamous(AdapterView<?> parent, View view, int position, long id) {

            if (position != parent.getSelectedItemPosition()) {
                parent.setSelection(position);
            }
            if (position == 0) {
                polygamous = AppConstants.NOSELECT;
            } else {
                final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
                polygamous = kv.codeValue;
                ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
            }
            patternSkipper(view);

        }

        //SPINNERS ENTITY
        public void setLcow(AdapterView<?> parent, View view, int position, long id) {

            if (position != parent.getSelectedItemPosition()) {
                parent.setSelection(position);
            }
            if (position == 0) {
                lcow = AppConstants.NOSELECT;
            } else {
                final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
                lcow = kv.codeValue;
                ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                ((TextView) parent.getChildAt(0)).setTextSize(20);

            }

        }

    private void patternSkipper(View view) {

        if (view != null) {

            if (aIsToB==null || aIsToB != 2) {
                setPolygamous(null);
                setMar(null);
            }

            if (polygamous==null || polygamous != 1) {
                setNwive(null);
                setLcow(null);
                setMrank(null);
            }


            notifyPropertyChanged(BR._all);
        }

    }

}
