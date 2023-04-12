package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.entity.Death;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeathViewModel extends AndroidViewModel {

    private final DeathRepository deathRepository;


    public DeathViewModel(@NonNull Application application) {
        super(application);
        deathRepository = new DeathRepository(application);
    }


    public List<Death> findAll() throws ExecutionException, InterruptedException {
        return deathRepository.findAll();
    }

    public Death find(String id) throws ExecutionException, InterruptedException {
        return deathRepository.find(id);
    }

    public List<Death> findToSync() throws ExecutionException, InterruptedException {
        return deathRepository.findToSync();
    }

    public List<Death> retrieveVpmSync() throws ExecutionException, InterruptedException {
        return deathRepository.retrieveVpmSync();
    }

    public void add(Death data){ deathRepository.create(data);}

    public void add(Death... data){     deathRepository.create(data);  }
}
