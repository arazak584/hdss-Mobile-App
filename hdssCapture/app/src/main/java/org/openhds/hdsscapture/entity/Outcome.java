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

    @SerializedName("out_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "out_uuid")
    @PrimaryKey
    public String out_uuid;

    @Expose
    public String childextId;

    @Expose
    public String preg_uuid;

    @Expose
    public Integer type;

    public Outcome(){}

    @NotNull
    public String getOut_uuid() {
        return out_uuid;
    }

    public void setOut_uuid(@NotNull String out_uuid) {
        this.out_uuid = out_uuid;
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
