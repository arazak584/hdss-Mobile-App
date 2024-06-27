package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.VaccinationRepository;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VaccinationViewModel extends AndroidViewModel {

    private final VaccinationRepository vaccinationRepository;

       public VaccinationViewModel(@NonNull Application application) {
        super(application);
           vaccinationRepository = new VaccinationRepository(application);
    }

    public Vaccination find(String id) throws ExecutionException, InterruptedException {
        return vaccinationRepository.find(id);
    }

    public List<Vaccination> findToSync() throws ExecutionException, InterruptedException {
        return vaccinationRepository.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return vaccinationRepository.count(startDate, endDate, username);
    }

    public List<Vaccination> reject(String id) throws ExecutionException, InterruptedException {
        return vaccinationRepository.reject(id);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return vaccinationRepository.rej(uuid);
    }

    public void add(Vaccination data){
        vaccinationRepository.create(data);
    }

    public void add(Vaccination... data){
        vaccinationRepository.create(data);
    }

}
