package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

public class IndividualEnd {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Integer endType;
    public Integer complete;
}
