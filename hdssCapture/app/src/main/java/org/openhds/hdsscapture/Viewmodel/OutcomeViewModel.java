package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Outcome;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutcomeViewModel extends AndroidViewModel {

    private final OutcomeRepository outcomeRepository;


    public OutcomeViewModel(@NonNull Application application) {
        super(application);
        outcomeRepository = new OutcomeRepository(application);
    }

    public List<Outcome> findToSync() throws ExecutionException, InterruptedException {
        return outcomeRepository.findToSync();
    }

    public Outcome find(String id,String locid) throws ExecutionException, InterruptedException {
        return outcomeRepository.find(id,locid);
    }

    public List<Outcome> error(String id) throws ExecutionException, InterruptedException {
        return outcomeRepository.error(id);
    }

    public void add(Outcome data){ outcomeRepository.create(data);}

    public void add(Outcome... data){ outcomeRepository.create(data);  }
}
