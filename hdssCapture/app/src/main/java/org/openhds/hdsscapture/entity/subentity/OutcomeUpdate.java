package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;

public class OutcomeUpdate {

    @PrimaryKey
    @NonNull
    public String uuid;
    public Date outcomeDate;
    public Date conceptionDate;
    public Integer rec_anc;
    public Integer month_pg;
    public Integer who_anc;
    public Integer num_anc;
    public String pregnancy_uuid;
    public Integer complete;
}
