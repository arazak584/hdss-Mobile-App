package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class LocationAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public String locationName;
    public Integer status;
    public Integer complete;
    public String locationLevel_uuid;
    public String extId;
    public String compextId;
    public String longitude;
    public String latitude;
    public String accuracy;
}
