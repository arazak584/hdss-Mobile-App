package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class DemographicAmendment {

    @PrimaryKey
    @NonNull
    public String individual_uuid;
    public Integer religion;
    public Integer tribe;
    public Integer education;
    public Integer marital;
    public Integer occupation;
    public String phone1;
    public String phone2;

}
