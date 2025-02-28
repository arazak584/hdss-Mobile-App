package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

public class PregnancyobsAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Integer outcome;
    public Date outcome_date;
}
