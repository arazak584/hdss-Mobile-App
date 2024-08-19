package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HdssSociodemoViewModel extends AndroidViewModel {

    private final HdssSociodemoRepository hdssSociodemoRepository;


    public HdssSociodemoViewModel(@NonNull Application application) {
        super(application);
        hdssSociodemoRepository = new HdssSociodemoRepository(application);
    }

    public List<HdssSociodemo> find(String id) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.find(id);
    }

    public HdssSociodemo findses(String id) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.findses(id);
    }

    public List<HdssSociodemo> findToSync() throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.findToSync();
    }

    public List<HdssSociodemo> reject(String id) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.reject(id);
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.count(startDate, endDate, username);
    }

    public long counts(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.counts(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.rej(uuid);
    }

    public List<HdssSociodemo> error() throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.error();
    }

    public void add(HdssSociodemo s){
        hdssSociodemoRepository.create(s);
    }

    public void add(HdssSociodemo... s){
        hdssSociodemoRepository.create(s);
    }

}
