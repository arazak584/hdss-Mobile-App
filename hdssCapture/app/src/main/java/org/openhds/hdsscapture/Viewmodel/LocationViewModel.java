package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.LocationRepository;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

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

    public List<Locations> findBySearch(String id,String ids) throws ExecutionException, InterruptedException {
        return locationRepository.findBySearch(id,"%" + ids + "%");
    }

    public List<Locations> retrieveBySearchs(String id) throws ExecutionException, InterruptedException {
        return locationRepository.retrieveBySearchs("%" + id + "%");
    }

    public List<Locations> retrieveByVillage(String id) throws ExecutionException, InterruptedException {
        return locationRepository.retrieveByVillage("%" + id + "%");
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {
        return locationRepository.findToSync();
    }

    public List<Locations> filter(String id) throws ExecutionException, InterruptedException {
        return locationRepository.filter("%" + id + "%");
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return locationRepository.count(startDate, endDate, username);
    }

    public List<Locations> repo() throws ExecutionException, InterruptedException {
        return locationRepository.repo();
    }

    public void add(Locations data){
        locationRepository.create(data);
    }

    public void add(Locations... data){
        locationRepository.create(data);
    }

    public int update(LocationAmendment s){
        return locationRepository.update(s);
    }
}
