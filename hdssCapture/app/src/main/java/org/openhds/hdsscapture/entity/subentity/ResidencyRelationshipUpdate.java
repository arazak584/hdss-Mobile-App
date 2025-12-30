package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;


public class ResidencyRelationshipUpdate {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Integer rltn_head;
    public Integer complete;
}
