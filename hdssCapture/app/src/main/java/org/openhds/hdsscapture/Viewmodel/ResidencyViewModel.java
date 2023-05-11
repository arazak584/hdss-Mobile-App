package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.ResidencyRepository;
import org.openhds.hdsscapture.entity.Residency;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResidencyViewModel extends AndroidViewModel {

    private final ResidencyRepository residencyRepository;


    public ResidencyViewModel(@NonNull Application application) {
        super(application);
        residencyRepository = new ResidencyRepository(application);
    }


    public List<Residency> findAll() throws ExecutionException, InterruptedException {
        return residencyRepository.findAll();
    }

    public List<Residency> find(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.find(id);
    }

    public Residency findRes(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.findRes(id);
    }

    public Residency finds(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.finds(id);
    }

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {
        return residencyRepository.findToSync();
    }


    public Residency fetch(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.fetch(id);
    }



    public void add(Residency data){ residencyRepository.create(data);}

    public void add(Residency... data){
        residencyRepository.create(data);
    }
}
