package org.openhds.hdsscapture.entity.subentity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
public class ResidencyAmendment {
    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;
    @SerializedName("endDate")
    @Expose
    @ColumnInfo(name = "endDate")
    public Date endDate;
    @SerializedName("endType")
    @Expose
    @ColumnInfo(name = "endType")
    public String endType;
}
