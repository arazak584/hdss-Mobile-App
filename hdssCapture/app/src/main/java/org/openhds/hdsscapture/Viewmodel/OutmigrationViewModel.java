package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.OutmigrationRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.subentity.OmgUpdate;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutmigrationViewModel extends AndroidViewModel {

    private final OutmigrationRepository repo;


    public OutmigrationViewModel(@NonNull Application application) {
        super(application);
        repo = new OutmigrationRepository(application);
    }


    public void add(Outmigration data){ repo.create(data);}

    public void add(Outmigration... data){repo.create(data);  }

    public void delete(Outmigration data) {
        repo.delete(data);
    }

    public void delete(Outmigration... data) {
        repo.delete(data);
    }

    public Outmigration find(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.find(id,locid);
    }

    public Outmigration edit(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.edit(id,locid);
    }

    public Outmigration finds(String id,String res) throws ExecutionException, InterruptedException {
        return repo.finds(id,res);
    }

    public Outmigration findLast(String id) throws ExecutionException, InterruptedException {
        return repo.findLast(id);
    }

    public List<Outmigration> end(String id) throws ExecutionException, InterruptedException {
        return repo.end(id);
    }

    public List<Outmigration> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }
    public List<Outmigration> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public Outmigration createOmg(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.createOmg(id, locid);
    }

    public Outmigration ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public LiveData<Outmigration> getView(String id) {
        return repo.view(id);
    }


    public List<Outmigration> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("InmigrationViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Inmigration records", e);
        }
    }

//    public int update(OmgUpdate s){
//        return repo.update(s);
//    }

    public void update(OmgUpdate s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }
    //public void add(Death... data){     deathRepository.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
