package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.SubdistrictRepository;
import org.openhds.hdsscapture.entity.Subdistrict;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubdistrictViewModel extends AndroidViewModel {

    private final SubdistrictRepository subdistrictRepository;


    public SubdistrictViewModel(@NonNull Application application) {
        super(application);
        subdistrictRepository = new SubdistrictRepository(application);
    }

    public Subdistrict find(String id) throws ExecutionException, InterruptedException {
        return subdistrictRepository.find(id);
    }

    public List<Subdistrict> findSubdistrictsOfDistrict(String id) throws ExecutionException, InterruptedException {
        return subdistrictRepository.findByDistrictId(id);
    }

    public List<Subdistrict> findAll() throws ExecutionException, InterruptedException {
        return subdistrictRepository.findAll();
    }

    public void add(Subdistrict data){
        subdistrictRepository.create(data);
    }

    public void add(Subdistrict... data){
        subdistrictRepository.create(data);
    }
}
