package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;


public class ResidencyUpdate {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Date startDate;
    public Integer complete;
}
