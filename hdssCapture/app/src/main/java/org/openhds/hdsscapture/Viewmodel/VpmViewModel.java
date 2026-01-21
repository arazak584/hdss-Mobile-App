package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.Repositories.VpmRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.VpmUpdate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VpmViewModel extends AndroidViewModel {

    private final VpmRepository repo;


    public VpmViewModel(@NonNull Application application) {
        super(application);
        repo = new VpmRepository(application);
    }


    public List<Vpm> retrieveToSync() throws ExecutionException, InterruptedException {
        return repo.retrieveToSync();
    }

    public Vpm find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Vpm finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public int update(VpmUpdate s){
        return repo.update(s);
    }

    public void add(Vpm data){repo.create(data);}

    public void add(Vpm... data){repo.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
