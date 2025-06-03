package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;


public class ResidencyUpdateEndDate {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Date endDate;
    public Integer complete;
}
