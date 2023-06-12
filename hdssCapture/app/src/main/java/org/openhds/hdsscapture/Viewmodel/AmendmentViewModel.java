package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.AmendmentRepository;
import org.openhds.hdsscapture.entity.Amendment;

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

    public void add(Amendment data){
        amendmentRepository.create(data);
    }

    public void add(Amendment... data){
        amendmentRepository.create(data);
    }

}
