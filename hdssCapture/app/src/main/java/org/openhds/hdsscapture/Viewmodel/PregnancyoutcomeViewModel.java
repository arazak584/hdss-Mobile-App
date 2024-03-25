package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.PregnancyoutcomeRepository;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyoutcomeViewModel extends AndroidViewModel {

    private final PregnancyoutcomeRepository pregnancyoutcomeRepository;


    public PregnancyoutcomeViewModel(@NonNull Application application) {
        super(application);
        pregnancyoutcomeRepository = new PregnancyoutcomeRepository(application);
    }

    public List<Pregnancyoutcome> findToSync() throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findToSync();
    }

    public Pregnancyoutcome find(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.find(id);
    }

    public Pregnancyoutcome findloc(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findloc(id,locid);
    }

    public Pregnancyoutcome finds(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.finds(id);
    }

    public Pregnancyoutcome findsloc(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findsloc(id,locid);
    }

    public Pregnancyoutcome findout(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findout(id);
    }

    public Pregnancyoutcome findpreg(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findpreg(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.count(startDate, endDate, username);
    }

    public void add(Pregnancyoutcome data){ pregnancyoutcomeRepository.create(data);}

    public void add(Pregnancyoutcome... data){ pregnancyoutcomeRepository.create(data);  }
}
