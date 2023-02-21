package org.openhds.hdsscapture.entity.subentity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openhds.hdsscapture.Utilities.Converter;

@TypeConverters(Converter.class)
@Entity
public class DemographicAmendment {

    @SerializedName("extId")
    @Expose
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;

    @SerializedName("religion")
    @Expose
    @ColumnInfo(name = "religion")
    public Integer religion;

    @SerializedName("tribe")
    @Expose
    @ColumnInfo(name = "tribe")
    public Integer tribe;

    @SerializedName("education")
    @Expose
    @ColumnInfo(name = "education")
    public Integer education;

    @SerializedName("marital")
    @Expose
    @ColumnInfo(name = "marital")
    public Integer marital;

    @SerializedName("occupation")
    @Expose
    @ColumnInfo(name = "occupation")
    public Integer occupation;

    @SerializedName("Phone1")
    @Expose
    @ColumnInfo(name = "Phone1")
    public String Phone1;

    @SerializedName("phone2")
    @Expose
    @ColumnInfo(name = "phone2")
    public String phone2;

}
