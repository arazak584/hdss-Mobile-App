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

    private final AmendmentRepository amendmentRepository;

       public AmendmentViewModel(@NonNull Application application) {
        super(application);
           amendmentRepository = new AmendmentRepository(application);
    }

    public Amendment find(String id) throws ExecutionException, InterruptedException {
        return amendmentRepository.find(id);
    }

    public List<Amendment> findToSync() throws ExecutionException, InterruptedException {
        return amendmentRepository.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return amendmentRepository.count(startDate, endDate, username);
    }

    public LiveData<Amendment> getView(String id) {
        return amendmentRepository.view(id);
    }

    public void add(Amendment data){
        amendmentRepository.create(data);
    }

    public void add(Amendment... data){
        amendmentRepository.create(data);
    }

    public LiveData<Long> sync() {
        return amendmentRepository.sync();
    }

}
