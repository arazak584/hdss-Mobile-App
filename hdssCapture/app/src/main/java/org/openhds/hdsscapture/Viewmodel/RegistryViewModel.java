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

    private final RegistryRepository registryRepository;

       public RegistryViewModel(@NonNull Application application) {
        super(application);
           registryRepository = new RegistryRepository(application);
    }

    public Registry find(String id) throws ExecutionException, InterruptedException {
        return registryRepository.find(id);
    }

    public Registry finds(String id) throws ExecutionException, InterruptedException {
        return registryRepository.finds(id);
    }

    public List<Registry> findToSync() throws ExecutionException, InterruptedException {
        return registryRepository.findToSync();
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        return registryRepository.count(id);
    }

    public void add(Registry data){
        registryRepository.create(data);
    }

    public void add(Registry... data){
        registryRepository.create(data);
    }

    public LiveData<Long> sync() {
        return registryRepository.sync();
    }

}
