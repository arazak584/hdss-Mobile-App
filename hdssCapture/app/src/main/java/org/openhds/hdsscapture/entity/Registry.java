package org.openhds.hdsscapture.entity;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.Utilities.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "registry",
indices = {@Index(value = {"individual_uuid","fw_uuid","complete","socialgroup_uuid"}, unique = false)})
public class Registry extends BaseObservable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public Date insertDate;
    @Expose
    public String name;
    @Expose
    public String socialgroup_uuid;
    @Expose
    public String location_uuid;
    @Expose
    public Integer status;
    @Expose
    public Integer complete;
    @Expose
    public String fw_uuid;


    public Registry(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Bindable
    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Bindable
    public String getInsertDate() {
        return insertDate == null ? null : f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            if (insertDate == null || insertDate.isEmpty()) {
                this.insertDate = null;
            } else {
                this.insertDate = f.parse(insertDate);
            }
            notifyPropertyChanged(BR.insertDate);
        } catch (ParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    private void patternSkipper(View view) {

        if (view != null) {

            notifyPropertyChanged(BR._all);
        }

    }
}
