package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.LocationRepository;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocationViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;

       public LocationViewModel(@NonNull Application application) {
        super(application);
        locationRepository = new LocationRepository(application);
    }

    public Locations find(String id) throws ExecutionException, InterruptedException {
        return locationRepository.find(id);
    }

    public List<Locations> findLocationsOfCluster(String id) throws ExecutionException, InterruptedException {
        return locationRepository.findByClusterId(id);
    }

    public List<Locations> retrieveByVillage(String id) throws ExecutionException, InterruptedException {
        return locationRepository.retrieveByVillage(id);
    }

    public List<Locations> findAll() throws ExecutionException, InterruptedException {
        return locationRepository.findAll();
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {
        return locationRepository.findToSync();
    }

    public List<Locations> findBySearch(String id) throws ExecutionException, InterruptedException {
        return locationRepository.findBySearch("%" + id + "%");
    }

    public long count(Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return locationRepository.count(startDate, endDate);
    }

    public void add(Locations data){
        locationRepository.create(data);
    }

    public void add(Locations... data){
        locationRepository.create(data);
    }

}
