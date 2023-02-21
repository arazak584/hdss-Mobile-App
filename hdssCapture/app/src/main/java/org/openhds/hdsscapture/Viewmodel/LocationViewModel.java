package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.LocationRepository;
import org.openhds.hdsscapture.entity.Location;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocationViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;

       public LocationViewModel(@NonNull Application application) {
        super(application);
        locationRepository = new LocationRepository(application);
    }

    public Location find(String id) throws ExecutionException, InterruptedException {
        return locationRepository.find(id);
    }

    public List<Location> findLocationsOfCluster(String id) throws ExecutionException, InterruptedException {
        return locationRepository.findByClusterId(id);
    }

    public List<Location> findAll() throws ExecutionException, InterruptedException {
        return locationRepository.findAll();
    }

    public List<Location> findToSync() throws ExecutionException, InterruptedException {
        return locationRepository.findToSync();
    }

    public List<Location> findBySearch(String id) throws ExecutionException, InterruptedException {
        return locationRepository.findBySearch("%" + id + "%");
    }

    public void add(Location data){
        locationRepository.create(data);
    }

    public void add(Location... data){
        locationRepository.create(data);
    }

}
