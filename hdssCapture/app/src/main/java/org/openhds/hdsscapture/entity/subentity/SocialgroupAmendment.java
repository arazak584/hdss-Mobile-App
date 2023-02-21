package org.openhds.hdsscapture.entity.subentity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity
public class SocialgroupAmendment {

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
}
