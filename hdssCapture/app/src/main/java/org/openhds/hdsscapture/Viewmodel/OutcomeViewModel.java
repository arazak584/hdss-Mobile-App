package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.OutcomeRepository;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutcomeViewModel extends AndroidViewModel {

    private final OutcomeRepository repo;


    public OutcomeViewModel(@NonNull Application application) {
        super(application);
        repo = new OutcomeRepository(application);
    }

    public List<Outcome> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

//    public Outcome find(String id,String locid) throws ExecutionException, InterruptedException {
//        return repo.find(id,locid);
//    }
//
//    public LiveData<Outcome> getView1(String id) {
//        return repo.view1(id);
//    }
//    public LiveData<Outcome> getView2(String id) {
//        return repo.view2(id);
//    }
//    public LiveData<Outcome> getView3(String id) {
//        return repo.view3(id);
//    }
//    public LiveData<Outcome> getView4(String id) {
//        return repo.view4(id);
//    }

    public LiveData<Outcome> getView(String id,Integer outcomeNumber) {
        return repo.view(id,outcomeNumber);
    }

//    public List<Outcome> error(String id) throws ExecutionException, InterruptedException {
//        return repo.error(id);
//    }

//    public long cnt(String id) throws ExecutionException, InterruptedException {
//        return repo.cnt(id);
//    }

    public void add(Outcome data){ repo.create(data);}

    public void add(Outcome... data){ repo.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }

    public Outcome getByOutcomeIdAndNumber(String id,Integer outcomeNumber) throws ExecutionException, InterruptedException {
        return repo.getByOutcomeIdAndNumber(id,outcomeNumber);
    }

    public Outcome getUuid(String id) throws ExecutionException, InterruptedException {
        return repo.getUuid(id);
    }
}
