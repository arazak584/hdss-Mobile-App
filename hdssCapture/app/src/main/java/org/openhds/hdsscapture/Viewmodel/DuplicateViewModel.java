package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DuplicateRepository;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Morbidity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DuplicateViewModel extends AndroidViewModel {

    private final DuplicateRepository repo;

       public DuplicateViewModel(@NonNull Application application) {
        super(application);
           repo = new DuplicateRepository(application);
    }

    public Duplicate find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Duplicate getId(String id) throws ExecutionException, InterruptedException {
        return repo.getId(id);
    }

    public Duplicate finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public Duplicate findByAnyUuid(String id) throws ExecutionException, InterruptedException {
        return repo.findByAnyUuid(id);
    }

    public List<Duplicate> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Duplicate> repo() throws ExecutionException, InterruptedException {
        return repo.repo();
    }

    public List<Duplicate> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("DuplicateViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Duplicate records", e);
        }
    }

    public List<Duplicate> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public void add(Duplicate data){
        repo.create(data);
    }

    public void add(Duplicate... data){
        repo.create(data);
    }

    public void delete(Duplicate data) {
        repo.delete(data);
    }

    public void delete(Duplicate... data) {
        repo.delete(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
