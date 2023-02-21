package org.openhds.hdsscapture.entity.subentity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class LocationAmendment {

    @NotNull
    @PrimaryKey
    public String extId;
    public String locationName;
}
