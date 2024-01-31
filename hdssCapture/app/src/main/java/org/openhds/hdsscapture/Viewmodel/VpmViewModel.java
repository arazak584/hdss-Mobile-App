package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

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

    private final VpmRepository vpmRepository;


    public VpmViewModel(@NonNull Application application) {
        super(application);
        vpmRepository = new VpmRepository(application);
    }


    public List<Vpm> retrieveToSync() throws ExecutionException, InterruptedException {
        return vpmRepository.retrieveToSync();
    }

    public Vpm find(String id) throws ExecutionException, InterruptedException {
        return vpmRepository.find(id);
    }

    public Vpm finds(String id) throws ExecutionException, InterruptedException {
        return vpmRepository.finds(id);
    }

    public int update(VpmUpdate s){
        return vpmRepository.update(s);
    }

    public void add(Vpm data){vpmRepository.create(data);}

    public void add(Vpm... data){vpmRepository.create(data);  }
}
