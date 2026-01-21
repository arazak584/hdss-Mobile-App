package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.MorbidityRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Morbidity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MorbidityViewModel extends AndroidViewModel {

    private final MorbidityRepository repo;


    public MorbidityViewModel(@NonNull Application application) {
        super(application);
        repo = new MorbidityRepository(application);
    }

    public List<Morbidity> find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Morbidity finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }
    public Morbidity ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public List<Morbidity> retrieveToSync() throws ExecutionException, InterruptedException {
        return repo.retrieveToSync();
    }

    public List<Morbidity> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long counts(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.counts(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public List<Morbidity> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("MorbidityViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Morbidity records", e);
        }
    }

    public LiveData<Morbidity> getView(String id) {
        return repo.view(id);
    }

    public void add(Morbidity s){
        repo.create(s);
    }

    public void add(Morbidity... s){
        repo.create(s);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
