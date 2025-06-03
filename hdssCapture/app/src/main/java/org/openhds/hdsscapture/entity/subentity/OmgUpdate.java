package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

public class OmgUpdate {

    @PrimaryKey
    @NonNull
    public String residency_uuid;
    public Integer destination;
    public Integer reason;
    public Integer complete;
    public Integer edit;
    public String reason_oth;
    public Integer status;
    public Date recordedDate;

}
