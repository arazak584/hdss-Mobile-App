package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeathViewModel extends AndroidViewModel {

    private final DeathRepository repo;


    public DeathViewModel(@NonNull Application application) {
        super(application);
        repo = new DeathRepository(application);
    }

    public Death find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Death retrieve(String id) throws ExecutionException, InterruptedException {
        return repo.retrieve(id);
    }

    public Death finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public List<Death> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Death> end(String id) throws ExecutionException, InterruptedException {
        return repo.end(id);
    }

    public List<Death> error(String id) throws ExecutionException, InterruptedException {
        return repo.error(id);
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }
    public long cnt() throws ExecutionException, InterruptedException {
        return repo.cnt();
    }

    public long err(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.err(id,ids);
    }

    public List<Death> repo() throws ExecutionException, InterruptedException {
        return repo.repo();
    }

    public List<Death> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }
    public Death ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public List<Death> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("DeathViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Death records", e);
        }
    }

    public LiveData<Death> getView(String id) {
        return repo.view(id);
    }

    public void add(Death data){ repo.create(data);}

    public void add(Death... data){     repo.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
