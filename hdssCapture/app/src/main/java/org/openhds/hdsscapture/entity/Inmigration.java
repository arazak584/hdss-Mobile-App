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
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "inmigration",
indices = {@Index(value = {"individual_uuid","residency_uuid","fw_uuid","complete"}, unique = false)})
public class Inmigration extends BaseObservable implements Parcelable {

    @SerializedName("residency_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "residency_uuid")
    @PrimaryKey
    public String residency_uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Integer migType;

    @Expose
    public Integer reason;

    @Expose
    public String reason_oth;

    @Expose
    public Integer origin;

    @Expose
    public Integer farm;

    @Expose
    public Date recordedDate;

    @Expose
    public String uuid;

    @Expose
    public String visit_uuid;

    @Expose
    public String fw_uuid;

    @ColumnInfo(name = "complete")
    public Integer complete;
    @Expose
    public String sttime;

    @Expose
    public String edtime;
    @Expose
    public Integer livestock;
    @Expose
    public String farm_other;
    @Expose
    public Integer food_yn;
    @Expose
    public Integer cash_yn;
    @Expose
    public Integer cash_crops;

    @Expose
    public Integer livestock_yn;
    @Expose
    public Integer acres;
    @Expose
    public Integer food_crops;
    @Expose
    public String location_uuid;

    @Expose
    public String comment;

    @Expose
    public String why_ext;

    @Expose
    public String why_int;

    @Expose
    public Integer how_lng;

    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;

    public Inmigration(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

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

    public String getInsertDate() {
        if (insertDate == null) return "";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    public Integer getMigType() {
        return migType;
    }

    public void setMigType(Integer migType) {
        this.migType = migType;
    }

    public String getRecordedDate() {
        if (recordedDate == null) return "";
        return f.format(recordedDate);
    }

    public void setRecordedDate(String recordedDate) {
        try {
            this.recordedDate = f.parse(recordedDate);
        } catch (ParseException e) {
            System.out.println("Recorded Date Error " + e.getMessage());
        }
    }

    @NotNull
    public String getResidency_uuid() {
        return residency_uuid;
    }

    public void setResidency_uuid(@NotNull String residency_uuid) {
        this.residency_uuid = residency_uuid;
    }

    @Bindable
    public String getHow_lng() {
        return how_lng == null ? "" : String.valueOf(how_lng);
    }

    public void setHow_lng(String how_lng) {

        try {
            this.how_lng = (how_lng == null) ? null : Integer.valueOf(how_lng);
        } catch (NumberFormatException e) {
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

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getReason_oth() {
        return reason_oth;
    }

    public void setReason_oth(String reason_oth) {
        this.reason_oth = reason_oth;
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

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public Integer getLivestock() {
        return livestock;
    }

    public void setLivestock(Integer livestock) {
        this.livestock = livestock;
    }

    public String getFarm_other() {
        return farm_other;
    }

    public void setFarm_other(String farm_other) {
        this.farm_other = farm_other;
    }

    public Integer getFood_yn() {
        return food_yn;
    }

    public void setFood_yn(Integer food_yn) {
        this.food_yn = food_yn;
    }

    public Integer getCash_yn() {
        return cash_yn;
    }

    public void setCash_yn(Integer cash_yn) {
        this.cash_yn = cash_yn;
    }

    public Integer getCash_crops() {
        return cash_crops;
    }

    public void setCash_crops(Integer cash_crops) {
        this.cash_crops = cash_crops;
    }

    public Integer getLivestock_yn() {
        return livestock_yn;
    }

    public void setLivestock_yn(Integer livestock_yn) {
        this.livestock_yn = livestock_yn;
    }

    public String getAcres() {
        return acres == null ? "" : String.valueOf(acres);
    }

    public void setAcres(String acres) {
        if (acres == null) this.acres = null;
        else
            try {
                this.acres = Integer.valueOf(acres);
            } catch (NumberFormatException e) {
            }
    }

    public Integer getFood_crops() {
        return food_crops;
    }

    public void setFood_crops(Integer food_crops) {
        this.food_crops = food_crops;
    }
    public String getLocation_uuid() {
        return location_uuid;
    }
    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    public String getWhy_ext() {
        return why_ext;
    }

    public void setWhy_ext(String why_ext) {
        this.why_ext = why_ext;
    }

    public String getWhy_int() {
        return why_int;
    }

    public void setWhy_int(String why_int) {
        this.why_int = why_int;
    }

    protected Inmigration(Parcel in) {
        this.uuid = in.readString();
        this.individual_uuid = in.readString();
        this.insertDate = (Date) in.readSerializable();
        this.migType = in.readInt();
        this.reason = in.readInt();
        this.recordedDate = (Date) in.readSerializable();
        this.residency_uuid = in.readString();
        this.visit_uuid = in.readString();
        this.origin = in.readInt();
        this.fw_uuid = in.readString();
    }

    public static final Creator<Inmigration> CREATOR = new Creator<Inmigration>() {
        @Override
        public Inmigration createFromParcel(Parcel in) {
            return new Inmigration(in);
        }

        @Override
        public Inmigration[] newArray(int size) {
            return new Inmigration[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.individual_uuid);
        dest.writeSerializable(this.insertDate);
        dest.writeInt(this.migType);
        dest.writeInt(this.reason);
        dest.writeSerializable(this.recordedDate);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.visit_uuid);
        dest.writeInt(this.origin);
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

    //SPINNERS ENTITY
    public void setReason(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            reason = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            reason = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);


    }

    //SPINNERS ENTITY
    public void setMigType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            migType = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            migType = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    //SPINNERS ENTITY
    public void setOrigin(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            origin = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            origin = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setFarm(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            farm = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            farm = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setLivestock(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            livestock = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            livestock = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
    }

    public void setLivestock_yn(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            livestock_yn = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            livestock_yn = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setFood_yn(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            food_yn = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            food_yn = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setFood_crops(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            food_crops = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            food_crops = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
    }

    public void setCash_yn(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            cash_yn = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            cash_yn = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);
    }

    public void setCash_crops(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            cash_crops = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            cash_crops = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
    }

    private void patternSkipper(View view) {

        if (view != null) {

            if(reason == null || reason!=77)
                setReason_oth(null);

            if(reason == null || reason != 9) {
                setFarm(null);
                setLivestock(null);
                setFarm_other(null);
                setFood_yn(null);
                setCash_yn(null);
                setCash_crops(null);
                setLivestock_yn(null);
                setAcres(null);
                setFood_crops(null);
            }

            if(farm == null || farm!=77)
                setFarm_other(null);

            if(food_yn == null || food_yn!=1)
                setFood_crops(null);

            if(cash_yn == null || cash_yn!=1)
                setCash_crops(null);

            if(livestock_yn == null || livestock_yn!=1)
                setLivestock(null);

            if (migType != 1 || (origin != 1 && origin != 2)) {
                setWhy_ext(null);
            }

            if (migType != 2 || (origin == 1 || origin == 2)) {
                setWhy_int(null);
            }


            notifyPropertyChanged(BR._all);
        }
    }
}
