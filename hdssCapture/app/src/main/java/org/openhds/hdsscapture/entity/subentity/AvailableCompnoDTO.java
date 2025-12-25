package org.openhds.hdsscapture.entity.subentity;

public class AvailableCompnoDTO {
    private String availableCompno;  // The compound number (e.g., "XA001")
    private String prefix;            // The prefix part
    private Long num;                 // Numeric part
    private String source;            // "gap" or "next"

    // Constructors
    public AvailableCompnoDTO() {}

    // Getters and Setters
    public String getAvailableCompno() {
        return availableCompno;
    }

    public void setAvailableCompno(String availableCompno) {
        this.availableCompno = availableCompno;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return availableCompno + " (" + source + ")";
    }
}