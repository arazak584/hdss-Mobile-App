package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.DistrictRepository;
import org.openhds.hdsscapture.entity.District;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DistrictViewModel extends AndroidViewModel {

    private final DistrictRepository districtRepository;


    public DistrictViewModel(@NonNull Application application) {
        super(application);
        districtRepository = new DistrictRepository(application);
    }

    public District find(String id) throws ExecutionException, InterruptedException {
        return districtRepository.find(id);
    }

    public List<District> findDistrictsOfRegion(String id) throws ExecutionException, InterruptedException {
        return districtRepository.findByRegionId(id);
    }

    public List<District> findAll() throws ExecutionException, InterruptedException {
        return districtRepository.findAll();
    }

    public void add(District data){
        districtRepository.create(data);
    }

    public void add(District... data){
        districtRepository.create(data);
    }
}
