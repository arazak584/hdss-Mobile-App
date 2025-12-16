package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.InmigrationRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InmigrationViewModel extends AndroidViewModel {

    private final InmigrationRepository inmigrationRepository;


    public InmigrationViewModel(@NonNull Application application) {
        super(application);
        inmigrationRepository = new InmigrationRepository(application);
    }

    public List<Inmigration> findToSync() throws ExecutionException, InterruptedException {
        return inmigrationRepository.findToSync();
    }

    public Inmigration find(String id, String locid) throws ExecutionException, InterruptedException {
        return inmigrationRepository.find(id,locid);
    }

    public Inmigration ins(String id) throws ExecutionException, InterruptedException {
        return inmigrationRepository.ins(id);
    }

    public Inmigration dup(String id,String ids) throws ExecutionException, InterruptedException {
        return inmigrationRepository.dup(id,ids);
    }

    public List<Inmigration> reject(String id) throws ExecutionException, InterruptedException {
        return inmigrationRepository.reject(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return inmigrationRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return inmigrationRepository.rej(uuid);
    }

    public LiveData<Inmigration> getView(String id) {
        return inmigrationRepository.view(id);
    }

    public void add(Inmigration data){ inmigrationRepository.create(data);}

    public void add(Inmigration... data){inmigrationRepository.create(data); }

    public LiveData<Long> sync() {
        return inmigrationRepository.sync();
    }
}
