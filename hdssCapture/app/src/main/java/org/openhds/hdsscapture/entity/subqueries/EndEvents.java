package org.openhds.hdsscapture.entity.subqueries;

import java.util.Date;

public class EndEvents {

    public String eventName;
    public String eventId;
    public String eventError;
    public String eventDate;

    public EndEvents() {
        // Default constructor with no arguments
    }

    // Assuming a constructor with 4 parameters
    public EndEvents(String eventName, String eventDate, String eventId, String eventError) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventId = eventId;
        this.eventError = eventError;
    }
}
