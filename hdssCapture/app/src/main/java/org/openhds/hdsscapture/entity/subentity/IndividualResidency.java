package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class IndividualResidency {

    @PrimaryKey
    @NonNull
    public String uuid;
    public String hohID;
}
