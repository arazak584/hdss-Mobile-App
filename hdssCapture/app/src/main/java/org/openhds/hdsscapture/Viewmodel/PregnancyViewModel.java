package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.PregnancyRepository;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyViewModel extends AndroidViewModel {

    private final PregnancyRepository pregnancyRepository;


    public PregnancyViewModel(@NonNull Application application) {
        super(application);
        pregnancyRepository = new PregnancyRepository(application);
    }

    public List<Pregnancy> findToSync() throws ExecutionException, InterruptedException {
        return pregnancyRepository.findToSync();
    }


    public List<Pregnancy> retrievePregnancy(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.retrievePregnancy(id);
    }

    public Pregnancy find(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.find(id);
    }

    public Pregnancy out(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.out(id);
    }

    public Pregnancy out2(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.out2(id);
    }
    public Pregnancy out3(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.out3(id);
    }
    public Pregnancy lastpregs(String id,Date recordedDate) throws ExecutionException, InterruptedException {
        return pregnancyRepository.lastpregs(id,recordedDate);
    }

    public Pregnancy lastpreg(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.lastpreg(id);
    }

    public Pregnancy finds(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.finds(id);
    }
    public Pregnancy find3(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.find3(id);
    }

    public Pregnancy ins(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.ins(id);
    }

    public List<Pregnancy> retrievePreg(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.retrievePreg(id);
    }
    public Pregnancy findss(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.findss(id);
    }

    public Pregnancy finds3(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.finds3(id);
    }

    public Pregnancy outcome2(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.outcome2(id);
    }

    public Pregnancy outcome3(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.outcome3(id);
    }

    public Pregnancy findpreg(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.findpreg(id);
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return pregnancyRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return pregnancyRepository.rej(uuid);
    }

    public List<Pregnancy> reject(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.reject(id);
    }

    public void add(Pregnancy data){ pregnancyRepository.create(data);}

    public void add(Pregnancy... data){
        pregnancyRepository.create(data);
    }
}
