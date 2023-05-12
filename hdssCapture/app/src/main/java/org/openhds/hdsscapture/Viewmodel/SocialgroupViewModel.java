package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.SocialgroupRepository;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SocialgroupViewModel extends AndroidViewModel {

    private final SocialgroupRepository socialgroupRepository;


    public SocialgroupViewModel(@NonNull Application application) {
        super(application);
        socialgroupRepository = new SocialgroupRepository(application);
    }

    public Socialgroup find(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.find(id);
    }

    public List<Socialgroup> retrieveBySocialgroup(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.retrieveBySocialgroup(id);
    }

    public List<Socialgroup> findhse(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.findhse(id);
    }

    public List<Socialgroup> findAll() throws ExecutionException, InterruptedException {
        return socialgroupRepository.findAll();
    }

    public List<Socialgroup> findToSync() throws ExecutionException, InterruptedException {
        return socialgroupRepository.findToSync();
    }

    public long count(Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return socialgroupRepository.count(startDate, endDate);
    }

    public void add(Socialgroup data){
        socialgroupRepository.create(data);
    }

    public void add(Socialgroup... data){
        socialgroupRepository.create(data);
    }
}
