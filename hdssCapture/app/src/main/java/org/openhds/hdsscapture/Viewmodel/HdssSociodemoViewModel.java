package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HdssSociodemoViewModel extends AndroidViewModel {

    private final HdssSociodemoRepository repo;


    public HdssSociodemoViewModel(@NonNull Application application) {
        super(application);
        repo = new HdssSociodemoRepository(application);
    }

    public List<HdssSociodemo> find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public HdssSociodemo findses(String id) throws ExecutionException, InterruptedException {
        return repo.findses(id);
    }
    public HdssSociodemo ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public List<HdssSociodemo> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<HdssSociodemo> reject(String id) throws ExecutionException, InterruptedException {
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

    public long cnt(String id) throws ExecutionException, InterruptedException {
        return repo.cnt(id);
    }

    public List<HdssSociodemo> error(String id) throws ExecutionException, InterruptedException {
        return repo.error(id);
    }

    public List<HdssSociodemo> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("HdssSociodemoViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch HdssSociodemo records", e);
        }
    }

    public LiveData<HdssSociodemo> getView(String id) {
        return repo.view(id);
    }

    public void add(HdssSociodemo s){
        repo.create(s);
    }

    public void add(HdssSociodemo... s){
        repo.create(s);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
