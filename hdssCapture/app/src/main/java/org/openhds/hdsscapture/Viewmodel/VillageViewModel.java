package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.VillageRepository;
import org.openhds.hdsscapture.entity.Village;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class VillageViewModel extends AndroidViewModel {

    private final VillageRepository villageRepository;


    public VillageViewModel(@NonNull Application application) {
        super(application);
        villageRepository = new VillageRepository(application);
    }

    public Village find(String id) throws ExecutionException, InterruptedException {
        return villageRepository.find(id);
    }

    public List<Village> findVillagesOfSubdistrict(String id) throws ExecutionException, InterruptedException {
        return villageRepository.findByRegionId(id);
    }

    public List<Village> findAll() throws ExecutionException, InterruptedException {
        return villageRepository.findAll();
    }

    public void add(Village data){
        villageRepository.create(data);
    }

    public void add(Village... data){
        villageRepository.create(data);
    }
}
