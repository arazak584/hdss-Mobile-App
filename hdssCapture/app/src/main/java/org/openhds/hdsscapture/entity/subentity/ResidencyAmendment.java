package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;


public class ResidencyAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Date endDate;
    public Integer endType;
    public Integer complete;
    public String hohID;
}
