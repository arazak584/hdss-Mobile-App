package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.AmendmentRepository;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Demographic;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AmendmentViewModel extends AndroidViewModel {

    private final AmendmentRepository repo;

       public AmendmentViewModel(@NonNull Application application) {
        super(application);
           repo = new AmendmentRepository(application);
    }

    public Amendment find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public List<Amendment> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public LiveData<Amendment> getView(String id) {
        return repo.view(id);
    }

    public void add(Amendment data){
        repo.create(data);
    }

    public void add(Amendment... data){
        repo.create(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
