package org.openhds.hdsscapture.Viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.openhds.hdsscapture.entity.Locations;

public class ClusterSharedViewModel extends ViewModel {
    private MutableLiveData<Locations> selectedLocation = new MutableLiveData<>();

    public LiveData<Locations> getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Locations location) {
        selectedLocation.setValue(location);
    }

    public Locations getCurrentSelectedLocation() {
        return selectedLocation.getValue();
    }

    public boolean hasSelectedLocation() {
        return selectedLocation.getValue() != null;
    }
}