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

    private final LocationRepository repo;

       public LocationViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepository(application);
    }

    public Locations find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Locations exist(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.exist(id,ids);
    }

    public Locations findByUuid(String id) throws ExecutionException, InterruptedException {
        return repo.findByUuid(id);
    }

    public List<Locations> findLocationsOfCluster(String id) throws ExecutionException, InterruptedException {
        return repo.findByClusterId(id);
    }

    public List<Locations> findBySearch(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.findBySearch(id,"%" + ids + "%");
    }

    public List<Locations> retrieveBySearchs(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveBySearchs("%" + id + "%");
    }

    public List<Locations> retrieveByVillage(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByVillage("%" + id + "%");
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Locations> retrieveAll(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveAll(id);
    }

    public List<Locations> filter(String id) throws ExecutionException, InterruptedException {
        return repo.filter("%" + id + "%");
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long counts(String id) throws ExecutionException, InterruptedException {
        return repo.counts(id);
    }

    public long hseCount(String id) throws ExecutionException, InterruptedException {
        return repo.hseCount(id);
    }

    public long done(String id) throws ExecutionException, InterruptedException {
        return repo.done(id);
    }

    public long work(String id) throws ExecutionException, InterruptedException {
        return repo.work(id);
    }

    public long works(String id) throws ExecutionException, InterruptedException {
        return repo.works(id);
    }
    public List<Locations> repo(String id) throws ExecutionException, InterruptedException {
        return repo.repo(id);
    }

    public void add(Locations data){
        repo.create(data);
    }

    public void add(Locations... data){
        repo.create(data);
    }

//    public int update(LocationAmendment s){
//        return repo.update(s);
//    }

    public void update(LocationAmendment s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

    // Get all village names for autocomplete
    public List<String> getAllVillageNames() throws ExecutionException, InterruptedException {
        return repo.getAllVillageNames();
    }

    // Get all compno for autocomplete
    public List<String> getAllCompno() throws ExecutionException, InterruptedException {
        return repo.getAllCompno();
    }

    // Filter by village name
    public List<Locations> filterByVillageName(String villageName) throws ExecutionException, InterruptedException {
        return repo.filterByVillageName("%" + villageName + "%");
    }

    // Filter by compno
    public List<Locations> filterByCompno(String compno) throws ExecutionException, InterruptedException {
        return repo.filterByCompno("%" + compno + "%");
    }

    // Filter by both
    public List<Locations> filterByVillageAndCompno(String villageName, String compno) throws ExecutionException, InterruptedException {
        return repo.filterByVillageAndCompno("%" + villageName + "%", "%" + compno + "%");
    }

    // Search village names for autocomplete
    public List<String> searchVillageNames(String query) throws ExecutionException, InterruptedException {
        return repo.searchVillageNames(query.toUpperCase());
    }

    // Search compno for autocomplete
    public List<String> searchCompno(String query) throws ExecutionException, InterruptedException {
        return repo.searchCompno(query.toUpperCase());
    }
}
