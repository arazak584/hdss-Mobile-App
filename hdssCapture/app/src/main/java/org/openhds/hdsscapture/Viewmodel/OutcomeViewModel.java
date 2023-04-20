package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.entity.Outcome;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutcomeViewModel extends AndroidViewModel {

    private final OutcomeRepository outcomeRepository;


    public OutcomeViewModel(@NonNull Application application) {
        super(application);
        outcomeRepository = new OutcomeRepository(application);
    }


    public List<Outcome> findAll() throws ExecutionException, InterruptedException {
        return outcomeRepository.findAll();
    }

    public List<Outcome> findToSync() throws ExecutionException, InterruptedException {
        return outcomeRepository.findToSync();
    }

    public Outcome find(String id) throws ExecutionException, InterruptedException {
        return outcomeRepository.find(id);
    }

    public void add(Outcome data){ outcomeRepository.create(data);}

    public void add(Outcome... data){ outcomeRepository.create(data);  }
}
