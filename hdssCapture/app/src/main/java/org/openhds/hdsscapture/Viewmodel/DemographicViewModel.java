package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DemographicRepository;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DemographicViewModel extends AndroidViewModel {

    private final DemographicRepository demographicRepository;


    public DemographicViewModel(@NonNull Application application) {
        super(application);
        demographicRepository = new DemographicRepository(application);
    }

    public List<Demographic> findToSync() throws ExecutionException, InterruptedException {
        return demographicRepository.findToSync();
    }

    public Demographic find(String id) throws ExecutionException, InterruptedException {
        return demographicRepository.find(id);
    }
    public Demographic ins(String id) throws ExecutionException, InterruptedException {
        return demographicRepository.ins(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return demographicRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return demographicRepository.rej(uuid);
    }

    public List<Demographic> reject(String id) throws ExecutionException, InterruptedException {
        return demographicRepository.reject(id);
    }

    public LiveData<Demographic> getView(String id) {
        return demographicRepository.view(id);
    }

    public List<Demographic> error() throws ExecutionException, InterruptedException {
        return demographicRepository.error();
    }
    public void add(Demographic data){ demographicRepository.create(data);}

    public void add(Demographic... data){
        demographicRepository.create(data);
    }

    public LiveData<Long> sync() {
        return demographicRepository.sync();
    }
}
