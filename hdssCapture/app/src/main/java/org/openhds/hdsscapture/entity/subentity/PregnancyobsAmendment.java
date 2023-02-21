package org.openhds.hdsscapture.entity.subentity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity
public class PregnancyobsAmendment {
    @Expose
    @PrimaryKey
    public String uuid;
    @Expose
    public Integer outcome;
}
