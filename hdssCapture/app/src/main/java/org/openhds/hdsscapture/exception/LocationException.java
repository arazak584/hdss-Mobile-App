package org.openhds.hdsscapture.exception;

import org.openhds.hdsscapture.entity.Locations;

public class LocationException extends Exception {

    private Locations failedLocation;

    public LocationException(String message, Locations failedLocation) {
        super(message);
        this.failedLocation = failedLocation;
    }

    public Locations getFailedLocation() {
        return failedLocation;
    }


}
