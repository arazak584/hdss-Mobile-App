package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.RegionRepository;
import org.openhds.hdsscapture.entity.Region;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegionViewModel extends AndroidViewModel {

    private final RegionRepository regionRepository;


    public RegionViewModel(@NonNull Application application) {
        super(application);
        regionRepository = new RegionRepository(application);
    }

    public Region find(String id) throws ExecutionException, InterruptedException {
        return regionRepository.find(id);
    }

    public List<Region> findRegionsOfCountry(String id) throws ExecutionException, InterruptedException {
        return regionRepository.findByCountryId(id);
    }

    public List<Region> findAll() throws ExecutionException, InterruptedException {
        return regionRepository.findAll();
    }

    public void add(Region data){
        regionRepository.create(data);
    }

    public void add(Region... data){
        regionRepository.create(data);
    }
}
