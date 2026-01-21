package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.VaccinationRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VaccinationViewModel extends AndroidViewModel {

    private final VaccinationRepository repo;

       public VaccinationViewModel(@NonNull Application application) {
        super(application);
           repo = new VaccinationRepository(application);
    }

    public Vaccination find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Vaccination ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public List<Vaccination> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public List<Vaccination> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public List<Vaccination> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("VaccinationViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Vaccination records", e);
        }
    }

    public LiveData<Vaccination> getView(String id) {
        return repo.view(id);
    }

    public void add(Vaccination data){
        repo.create(data);
    }

    public void add(Vaccination... data){
        repo.create(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
