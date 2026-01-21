package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.InmigrationRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InmigrationViewModel extends AndroidViewModel {

    private final InmigrationRepository repo;


    public InmigrationViewModel(@NonNull Application application) {
        super(application);
        repo = new InmigrationRepository(application);
    }

    public List<Inmigration> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public Inmigration find(String id, String locid) throws ExecutionException, InterruptedException {
        return repo.find(id,locid);
    }

    public Inmigration ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public Inmigration dup(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.dup(id,ids);
    }

    public List<Inmigration> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public List<Inmigration> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("InmigrationViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Inmigration records", e);
        }
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public LiveData<Inmigration> getView(String id) {
        return repo.view(id);
    }

    public void add(Inmigration data){ repo.create(data);}

    public void add(Inmigration... data){repo.create(data); }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
