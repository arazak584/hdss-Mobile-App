package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DemographicRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DemographicViewModel extends AndroidViewModel {

    private final DemographicRepository repo;


    public DemographicViewModel(@NonNull Application application) {
        super(application);
        repo = new DemographicRepository(application);
    }

    public List<Demographic> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public Demographic find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }
    public Demographic ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public List<Demographic> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public LiveData<Demographic> getView(String id) {
        return repo.view(id);
    }

    public List<Demographic> error() throws ExecutionException, InterruptedException {
        return repo.error();
    }

    public List<Demographic> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("DemographicViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Demographic records", e);
        }
    }
    
    public void add(Demographic data){ repo.create(data);}

    public void add(Demographic... data){
        repo.create(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
