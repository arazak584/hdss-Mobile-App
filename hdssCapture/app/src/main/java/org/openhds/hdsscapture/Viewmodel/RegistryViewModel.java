package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.AmendmentRepository;
import org.openhds.hdsscapture.Repositories.RegistryRepository;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Registry;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegistryViewModel extends AndroidViewModel {

    private final RegistryRepository repo;

       public RegistryViewModel(@NonNull Application application) {
        super(application);
           repo = new RegistryRepository(application);
    }

    public Registry find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Registry finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public List<Registry> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        return repo.count(id);
    }

    public void add(Registry data){
        repo.create(data);
    }

    public void add(Registry... data){
        repo.create(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
