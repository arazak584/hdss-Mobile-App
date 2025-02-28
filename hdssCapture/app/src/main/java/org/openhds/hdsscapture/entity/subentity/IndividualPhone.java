package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class IndividualPhone {

    @PrimaryKey
    @NonNull
    public String uuid;
    public String phone1;
}
