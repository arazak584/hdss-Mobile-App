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

    private final VisitRepository visitRepository;


    public VisitViewModel(@NonNull Application application) {
        super(application);
        visitRepository = new VisitRepository(application);
    }

    public Visit find(String id) throws ExecutionException, InterruptedException {
        return visitRepository.find(id);
    }

    public List<Visit> findToSync() throws ExecutionException, InterruptedException {
        return visitRepository.findToSync();
    }

    public long countVisits(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return visitRepository.countVisits(startDate, endDate, username);
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        return visitRepository.count(id);
    }

//    public long countLocs(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
//        return visitRepository.countLocs(startDate, endDate, username);
//    }


    public void add(Visit data){ visitRepository.create(data);}

    public void add(Visit... data){visitRepository.create(data);}

    public LiveData<Long> sync() {
        return visitRepository.sync();
    }


}
