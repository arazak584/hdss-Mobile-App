package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.LocationRepository;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;

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

    public Locations exist(String id,String ids) throws ExecutionException, InterruptedException {
        return locationRepository.exist(id,ids);
    }

    public Locations findByUuid(String id) throws ExecutionException, InterruptedException {
        return locationRepository.findByUuid(id);
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

    public List<Locations> retrieveAll(String id) throws ExecutionException, InterruptedException {
        return locationRepository.retrieveAll(id);
    }

    public List<Locations> filter(String id) throws ExecutionException, InterruptedException {
        return locationRepository.filter("%" + id + "%");
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return locationRepository.count(startDate, endDate, username);
    }

    public long counts(String id) throws ExecutionException, InterruptedException {
        return locationRepository.counts(id);
    }

    public long hseCount(String id) throws ExecutionException, InterruptedException {
        return locationRepository.hseCount(id);
    }

    public long done(String id) throws ExecutionException, InterruptedException {
        return locationRepository.done(id);
    }

    public long work(String id) throws ExecutionException, InterruptedException {
        return locationRepository.work(id);
    }

    public long works(String id) throws ExecutionException, InterruptedException {
        return locationRepository.works(id);
    }
    public List<Locations> repo(String id) throws ExecutionException, InterruptedException {
        return locationRepository.repo(id);
    }

    public void add(Locations data){
        locationRepository.create(data);
    }

    public void add(Locations... data){
        locationRepository.create(data);
    }

//    public int update(LocationAmendment s){
//        return locationRepository.update(s);
//    }

    public void update(LocationAmendment s, Consumer<Integer> callback) {
        locationRepository.update(s, callback);
    }

    public LiveData<Long> sync() {
        return locationRepository.sync();
    }
}
