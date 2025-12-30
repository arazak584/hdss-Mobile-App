package org.openhds.hdsscapture.odk.model;

/**
 * Enum for different types of repeat groups in ODK forms
 */
public enum RepeatGroupType {
    /**
     * All household members
     */
    ALL_MEMBERS,

    /**
     * Only resident members
     */
    RESIDENT_MEMBERS,

    /**
     * Only deceased members
     */
    DEAD_MEMBERS,

    /**
     * Only out-migrated members
     */
    OUTMIG_MEMBERS,

    /**
     * Custom mapped values (not member-based)
     */
    MAPPED_VALUES
}