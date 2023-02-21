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

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {
        return residencyRepository.findToSync();
    }

    public List<Residency> findimgToSync() throws ExecutionException, InterruptedException {
        return residencyRepository.findimgToSync();
    }

    public List<Residency> findomgToSync() throws ExecutionException, InterruptedException {
        return residencyRepository.findomgToSync();
    }

    public void add(Residency data){ residencyRepository.create(data);}

    public void add(Residency... data){
        residencyRepository.create(data);
    }
}
