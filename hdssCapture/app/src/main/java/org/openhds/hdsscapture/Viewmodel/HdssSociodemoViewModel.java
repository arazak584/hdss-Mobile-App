package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.entity.HdssSociodemo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HdssSociodemoViewModel extends AndroidViewModel {

    private final HdssSociodemoRepository hdssSociodemoRepository;


    public HdssSociodemoViewModel(@NonNull Application application) {
        super(application);
        hdssSociodemoRepository = new HdssSociodemoRepository(application);
    }

    public HdssSociodemo find(String id) throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.find(id);
    }

    public List<HdssSociodemo> findToSync() throws ExecutionException, InterruptedException {
        return hdssSociodemoRepository.findToSync();
    }

    public void add(HdssSociodemo s){
        hdssSociodemoRepository.create(s);
    }

    public void add(HdssSociodemo... s){
        hdssSociodemoRepository.create(s);
    }

}
