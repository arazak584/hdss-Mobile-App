package org.openhds.hdsscapture.odk;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "Form")
public class Form extends BaseObservable {

    @SerializedName("id")
    @PrimaryKey
    @NotNull
    @Expose
    @ColumnInfo(name = "id")
    public String id;

    @Expose
    public String formID;

    @Expose
    @ColumnInfo(name = "formName")
    public String formName;

    @Expose
    @ColumnInfo(name = "insertDate")
    public Date insertDate;

    @Expose
    @ColumnInfo(name = "formDesc")
    public String formDesc;

    @Expose
    @ColumnInfo(name = "gender")
    public Integer gender;

    @SerializedName("enabled")
    @Expose
    public Integer enabled;

    @Expose
    public Integer modules;

    @Expose
    public Integer minAge;

    @Expose
    public Integer maxAge;


    public Form(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    @Bindable
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

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormDesc() {
        return formDesc;
    }

    public void setFormDesc(String formDesc) {
        this.formDesc = formDesc;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getModules() {
        return modules;
    }

    public void setModules(Integer modules) {
        this.modules = modules;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
}
