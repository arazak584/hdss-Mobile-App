package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.InmigrationRepository;
import org.openhds.hdsscapture.entity.Inmigration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InmigrationViewModel extends AndroidViewModel {

    private final InmigrationRepository inmigrationRepository;


    public InmigrationViewModel(@NonNull Application application) {
        super(application);
        inmigrationRepository = new InmigrationRepository(application);
    }


    public List<Inmigration> findAll() throws ExecutionException, InterruptedException {
        return inmigrationRepository.findAll();
    }

    public List<Inmigration> findToSync() throws ExecutionException, InterruptedException {
        return inmigrationRepository.findToSync();
    }

    public Inmigration find(String id) throws ExecutionException, InterruptedException {
        return inmigrationRepository.find(id);
    }

    public long count(Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return inmigrationRepository.count(startDate, endDate);
    }

    public void add(Inmigration data){ inmigrationRepository.create(data);}

    public void add(Inmigration... data){     inmigrationRepository.create(data);  }
}
