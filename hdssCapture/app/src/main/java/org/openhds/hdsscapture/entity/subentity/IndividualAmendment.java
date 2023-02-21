package org.openhds.hdsscapture.entity.subentity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openhds.hdsscapture.Utilities.Converter;

import java.util.Date;

@TypeConverters(Converter.class)
@Entity
public class IndividualAmendment {

    @SerializedName("extId")
    @Expose
    @ColumnInfo(name = "extId")
    @PrimaryKey
    public String extId;
    @SerializedName("firstName")
    @Expose
    @ColumnInfo(name = "firstName")
    public String firstName;

    @SerializedName("lastName")
    @Expose
    @ColumnInfo(name = "lastName")
    public String lastName;
    @SerializedName("gender")
    @Expose
    @ColumnInfo(name = "gender")
    public String gender;
    @SerializedName("dob")
    @Expose
    @ColumnInfo(name = "dob")
    public Date dob;
    @SerializedName("nickName")
    @Expose
    @ColumnInfo(name = "nickName")
    public String nickName;

}
