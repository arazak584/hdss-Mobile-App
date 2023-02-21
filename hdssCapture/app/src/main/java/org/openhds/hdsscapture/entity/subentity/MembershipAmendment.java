package org.openhds.hdsscapture.entity.subentity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class MembershipAmendment {

    @NotNull
    @PrimaryKey
    public String uuid;
    public String household;
}
