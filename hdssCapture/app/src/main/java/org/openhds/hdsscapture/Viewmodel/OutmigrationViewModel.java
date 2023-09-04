package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.OutmigrationRepository;
import org.openhds.hdsscapture.entity.Outmigration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutmigrationViewModel extends AndroidViewModel {

    private final OutmigrationRepository outmigrationRepository;


    public OutmigrationViewModel(@NonNull Application application) {
        super(application);
        outmigrationRepository = new OutmigrationRepository(application);
    }


    public void add(Outmigration data){ outmigrationRepository.create(data);}

    public void add(Outmigration... data){outmigrationRepository.create(data);  }

    public Outmigration find(String id) throws ExecutionException, InterruptedException {
        return outmigrationRepository.find(id);
    }

    public List<Outmigration> findToSync() throws ExecutionException, InterruptedException {
        return outmigrationRepository.findToSync();
    }

    public Outmigration createOmg(String id,String locid) throws ExecutionException, InterruptedException {
        return outmigrationRepository.createOmg(id, locid);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return outmigrationRepository.count(startDate, endDate, username);
    }

    //public void add(Death... data){     deathRepository.create(data);  }
}
