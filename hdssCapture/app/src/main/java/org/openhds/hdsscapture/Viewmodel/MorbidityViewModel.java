package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.MorbidityRepository;
import org.openhds.hdsscapture.entity.Morbidity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MorbidityViewModel extends AndroidViewModel {

    private final MorbidityRepository morbidityRepository;


    public MorbidityViewModel(@NonNull Application application) {
        super(application);
        morbidityRepository = new MorbidityRepository(application);
    }

    public List<Morbidity> find(String id) throws ExecutionException, InterruptedException {
        return morbidityRepository.find(id);
    }

    public Morbidity finds(String id) throws ExecutionException, InterruptedException {
        return morbidityRepository.finds(id);
    }

    public List<Morbidity> retrieveToSync() throws ExecutionException, InterruptedException {
        return morbidityRepository.retrieveToSync();
    }

    public List<Morbidity> reject(String id) throws ExecutionException, InterruptedException {
        return morbidityRepository.reject(id);
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return morbidityRepository.count(startDate, endDate, username);
    }

    public long counts(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return morbidityRepository.counts(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return morbidityRepository.rej(uuid);
    }

    public void add(Morbidity s){
        morbidityRepository.create(s);
    }

    public void add(Morbidity... s){
        morbidityRepository.create(s);
    }

}
