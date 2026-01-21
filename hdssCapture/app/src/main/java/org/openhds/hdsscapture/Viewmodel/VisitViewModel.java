package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.VisitRepository;
import org.openhds.hdsscapture.entity.Visit;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VisitViewModel extends AndroidViewModel {

    private final VisitRepository repo;


    public VisitViewModel(@NonNull Application application) {
        super(application);
        repo = new VisitRepository(application);
    }

    public Visit find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public List<Visit> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public long countVisits(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.countVisits(startDate, endDate, username);
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        return repo.count(id);
    }

//    public long countLocs(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
//        return repo.countLocs(startDate, endDate, username);
//    }


    public void add(Visit data){ repo.create(data);}

    public void add(Visit... data){repo.create(data);}

    public LiveData<Long> sync() {
        return repo.sync();
    }


}
