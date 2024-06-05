package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeathViewModel extends AndroidViewModel {

    private final DeathRepository deathRepository;


    public DeathViewModel(@NonNull Application application) {
        super(application);
        deathRepository = new DeathRepository(application);
    }

    public Death find(String id) throws ExecutionException, InterruptedException {
        return deathRepository.find(id);
    }

    public Death retrieve(String id) throws ExecutionException, InterruptedException {
        return deathRepository.retrieve(id);
    }

    public Death finds(String id) throws ExecutionException, InterruptedException {
        return deathRepository.finds(id);
    }

    public List<Death> findToSync() throws ExecutionException, InterruptedException {
        return deathRepository.findToSync();
    }

    public List<Death> end(String id) throws ExecutionException, InterruptedException {
        return deathRepository.end(id);
    }

    public List<Death> error() throws ExecutionException, InterruptedException {
        return deathRepository.error();
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return deathRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return deathRepository.rej(uuid);
    }

    public List<Death> repo() throws ExecutionException, InterruptedException {
        return deathRepository.repo();
    }

    public List<Death> reject(String id) throws ExecutionException, InterruptedException {
        return deathRepository.reject(id);
    }

    public void add(Death data){ deathRepository.create(data);}

    public void add(Death... data){     deathRepository.create(data);  }
}
