package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class HouseholdAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Integer complete;
    public String extId;
}
