package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

public class RelationshipUpdate {

    @PrimaryKey
    @NonNull
    public String individualA_uuid;
    public Date endDate;
    public Integer endType;
    public Integer complete;
}
