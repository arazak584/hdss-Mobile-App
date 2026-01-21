package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.CommunityRepository;
import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommunityViewModel extends AndroidViewModel {

    private final CommunityRepository repo;


    public CommunityViewModel(@NonNull Application application) {
        super(application);
        repo = new CommunityRepository(application);
    }

    public List<CommunityReport> retrieves(String id) throws ExecutionException, InterruptedException {
        return repo.retrieves(id);
    }

    public CommunityReport retrieve() throws ExecutionException, InterruptedException {
        return repo.retrieve();
    }

    public CommunityReport find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public List<CommunityReport> retrieveToSync() throws ExecutionException, InterruptedException {
        return repo.retrieveToSync();
    }

    public List<CommunityReport> fw(String id) throws ExecutionException, InterruptedException {
        return repo.fw(id);
    }


    public void add(CommunityReport data){ repo.create(data);}

    public void add(CommunityReport... data){repo.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
