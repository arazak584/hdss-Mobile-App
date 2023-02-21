package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.VisitRepository;
import org.openhds.hdsscapture.entity.Visit;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class VisitViewModel extends AndroidViewModel {

    private final VisitRepository visitRepository;


    public VisitViewModel(@NonNull Application application) {
        super(application);
        visitRepository = new VisitRepository(application);
    }


    public List<Visit> findAll() throws ExecutionException, InterruptedException {
        return visitRepository.findAll();
    }

    public List<Visit> findToSync() throws ExecutionException, InterruptedException {
        return visitRepository.findToSync();
    }

    public void add(Visit data){ visitRepository.create(data);}

    public void add(Visit... data){visitRepository.create(data);}

}
