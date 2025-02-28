package org.openhds.hdsscapture.entity.subentity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Date;


public class IndividualAmendment {

    @PrimaryKey
    @NonNull
    public String uuid;
    public String firstName;
    public String lastName;
    public Integer gender;
    public Date dob;
    public String otherName;
    public String ghanacard;
    public Integer complete;
    public String father_uuid;
    public String mother_uuid;

}
