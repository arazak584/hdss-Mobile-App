package org.openhds.hdsscapture.entity;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "outcome")
public class Outcome extends BaseObservable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    private String uuid;

    @Expose
    private String mother;
    @Expose
    private String childextId;

    @Expose
    private String preg_uuid;

    @Expose
    private Integer type;

    public Outcome(){}

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getChildextId() {
        return childextId;
    }

    public void setChildextId(String childextId) {
        this.childextId = childextId;
    }

    public String getPreg_uuid() {
        return preg_uuid;
    }

    public void setPreg_uuid(String preg_uuid) {
        this.preg_uuid = preg_uuid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
