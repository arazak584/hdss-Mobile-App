package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.OutmigrationRepository;
import org.openhds.hdsscapture.entity.Outmigration;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutmigrationViewModel extends AndroidViewModel {

    private final OutmigrationRepository outmigrationRepository;


    public OutmigrationViewModel(@NonNull Application application) {
        super(application);
        outmigrationRepository = new OutmigrationRepository(application);
    }


    public List<Outmigration> findAll() throws ExecutionException, InterruptedException {
        return outmigrationRepository.findAll();
    }


    public void add(Outmigration data){ outmigrationRepository.create(data);}

    public void add(Outmigration... data){     outmigrationRepository.create(data);  }

    public Outmigration find(String id) throws ExecutionException, InterruptedException {
        return outmigrationRepository.find(id);
    }

    public List<Outmigration> findomgToSync() throws ExecutionException, InterruptedException {
        return outmigrationRepository.findomgToSync();
    }

    //public void add(Death... data){     deathRepository.create(data);  }
}
