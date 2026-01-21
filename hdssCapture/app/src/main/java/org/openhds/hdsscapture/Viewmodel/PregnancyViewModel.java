package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.PregnancyRepository;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyViewModel extends AndroidViewModel {

    private final PregnancyRepository repo;


    public PregnancyViewModel(@NonNull Application application) {
        super(application);
        repo = new PregnancyRepository(application);
    }

    public List<Pregnancy> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }


    public List<Pregnancy> retrievePregnancy(String id) throws ExecutionException, InterruptedException {
        return repo.retrievePregnancy(id);
    }

    public Pregnancy find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Pregnancy out(String id) throws ExecutionException, InterruptedException {
        return repo.out(id);
    }

    public Pregnancy out2(String id) throws ExecutionException, InterruptedException {
        return repo.out2(id);
    }
    public Pregnancy out3(String id) throws ExecutionException, InterruptedException {
        return repo.out3(id);
    }
    public Pregnancy lastpregs(String id,Date recordedDate) throws ExecutionException, InterruptedException {
        return repo.lastpregs(id,recordedDate);
    }

    public Pregnancy lastpreg(String id) throws ExecutionException, InterruptedException {
        return repo.lastpreg(id);
    }

    public Pregnancy finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }
    public Pregnancy find3(String id) throws ExecutionException, InterruptedException {
        return repo.find3(id);
    }

    public Pregnancy ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public List<Pregnancy> retrievePreg(String id) throws ExecutionException, InterruptedException {
        return repo.retrievePreg(id);
    }

    public List<Pregnancy> findAllByIndividual(String id) throws ExecutionException, InterruptedException {
        return repo.retrieve(id);
    }

    public Pregnancy findss(String id) throws ExecutionException, InterruptedException {
        return repo.findss(id);
    }

    public Pregnancy finds3(String id) throws ExecutionException, InterruptedException {
        return repo.finds3(id);
    }

    public Pregnancy outcome2(String id) throws ExecutionException, InterruptedException {
        return repo.outcome2(id);
    }

    public Pregnancy outcome3(String id) throws ExecutionException, InterruptedException {
        return repo.outcome3(id);
    }

    public Pregnancy findpreg(String id) throws ExecutionException, InterruptedException {
        return repo.findpreg(id);
    }

    public LiveData<Pregnancy> getView(String id) {
        return repo.view(id);
    }


    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public List<Pregnancy> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public List<Pregnancy> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("PregnancyViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Pregnancy records", e);
        }
    }

    public void add(Pregnancy data){ repo.create(data);}

    public void add(Pregnancy... data){
        repo.create(data);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
