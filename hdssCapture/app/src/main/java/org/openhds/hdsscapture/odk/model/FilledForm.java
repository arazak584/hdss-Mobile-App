package org.openhds.hdsscapture.odk.model;

import org.openhds.hdsscapture.entity.Individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extended FilledForm class with repeat group support
 */
public class FilledForm {

    private String formName;
    private Map<String, String> data;
    private Map<String, RepeatGroupConfig> repeatGroups;

    // Lists for different member types
    private List<Individual> householdMembers;
    private List<Individual> residentMembers;
    private List<Individual> deadMembers;
    private List<Individual> outmigMembers;

    public FilledForm() {
        this.data = new HashMap<>();
        this.repeatGroups = new HashMap<>();
        this.householdMembers = new ArrayList<>();
        this.residentMembers = new ArrayList<>();
        this.deadMembers = new ArrayList<>();
        this.outmigMembers = new ArrayList<>();
    }

    // Basic data methods
    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public Map<String, String> getData() {
        return data;
    }

    public List<String> getVariables() {
        return new ArrayList<>(data.keySet());
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    // Member list setters
    public void setHouseholdMembers(List<Individual> members) {
        this.householdMembers = members != null ? members : new ArrayList<>();
    }

    public List<Individual> getHouseholdMembers() {
        return householdMembers;
    }

    public void setResidentMembers(List<Individual> members) {
        this.residentMembers = members != null ? members : new ArrayList<>();
    }

    public List<Individual> getResidentMembers() {
        return residentMembers;
    }

    public void setDeadMembers(List<Individual> members) {
        this.deadMembers = members != null ? members : new ArrayList<>();
    }

    public List<Individual> getDeadMembers() {
        return deadMembers;
    }

    public void setOutmigMembers(List<Individual> members) {
        this.outmigMembers = members != null ? members : new ArrayList<>();
    }

    public List<Individual> getOutmigMembers() {
        return outmigMembers;
    }

    // Repeat group configuration
    public void addRepeatGroup(String name, RepeatGroupType type, List<Map<String, String>> mapping) {
        RepeatGroupConfig config = new RepeatGroupConfig();
        config.name = name;
        config.type = type;
        config.mapping = mapping;
        repeatGroups.put(name, config);
    }

    public boolean isRepeatGroup(String name) {
        return repeatGroups.containsKey(name);
    }

    public boolean isMemberRepeatGroup(String name) {
        if (!repeatGroups.containsKey(name)) return false;
        RepeatGroupType type = repeatGroups.get(name).type;
        return type == RepeatGroupType.ALL_MEMBERS ||
                type == RepeatGroupType.RESIDENT_MEMBERS ||
                type == RepeatGroupType.DEAD_MEMBERS ||
                type == RepeatGroupType.OUTMIG_MEMBERS;
    }

    public boolean isAllMembersRepeatGroup(String name) {
        if (!repeatGroups.containsKey(name)) return false;
        return repeatGroups.get(name).type == RepeatGroupType.ALL_MEMBERS;
    }

    public boolean isResidentMemberRepeatGroup(String name) {
        if (!repeatGroups.containsKey(name)) return false;
        return repeatGroups.get(name).type == RepeatGroupType.RESIDENT_MEMBERS;
    }

    public boolean isDeadMembersRepeatGroup(String name) {
        if (!repeatGroups.containsKey(name)) return false;
        return repeatGroups.get(name).type == RepeatGroupType.DEAD_MEMBERS;
    }

    public boolean isOutMigMembersRepeatGroup(String name) {
        if (!repeatGroups.containsKey(name)) return false;
        return repeatGroups.get(name).type == RepeatGroupType.OUTMIG_MEMBERS;
    }

    public RepeatGroupType getRepeatGroupType(String name) {
        if (!repeatGroups.containsKey(name)) return null;
        return repeatGroups.get(name).type;
    }

    public int getRepeatGroupCount(String name) {
        if (!repeatGroups.containsKey(name)) return 0;

        RepeatGroupConfig config = repeatGroups.get(name);
        switch (config.type) {
            case ALL_MEMBERS:
                return householdMembers.size();
            case RESIDENT_MEMBERS:
                return residentMembers.size();
            case DEAD_MEMBERS:
                return deadMembers.size();
            case OUTMIG_MEMBERS:
                return outmigMembers.size();
            case MAPPED_VALUES:
                return config.mapping != null ? config.mapping.size() : 0;
            default:
                return 0;
        }
    }

    public List<Map<String, String>> getRepeatGroupMapping(String name) {
        if (!repeatGroups.containsKey(name)) return new ArrayList<>();
        RepeatGroupConfig config = repeatGroups.get(name);
        return config.mapping != null ? config.mapping : new ArrayList<>();
    }

    // Inner class for repeat group configuration
    private static class RepeatGroupConfig {
        String name;
        RepeatGroupType type;
        List<Map<String, String>> mapping;
    }
}