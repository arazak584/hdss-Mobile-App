package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class SocialgroupAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public String groupName;
    public String individual_uuid;
    public Integer complete;
}
