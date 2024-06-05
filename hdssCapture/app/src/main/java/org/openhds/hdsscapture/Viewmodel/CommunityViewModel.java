package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.CommunityRepository;
import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommunityViewModel extends AndroidViewModel {

    private final CommunityRepository communityRepository;


    public CommunityViewModel(@NonNull Application application) {
        super(application);
        communityRepository = new CommunityRepository(application);
    }

    public List<CommunityReport> retrieves(String id) throws ExecutionException, InterruptedException {
        return communityRepository.retrieves(id);
    }

    public CommunityReport retrieve() throws ExecutionException, InterruptedException {
        return communityRepository.retrieve();
    }

    public CommunityReport find(String id) throws ExecutionException, InterruptedException {
        return communityRepository.find(id);
    }

    public List<CommunityReport> retrieveToSync() throws ExecutionException, InterruptedException {
        return communityRepository.retrieveToSync();
    }

    public List<CommunityReport> fw(String id) throws ExecutionException, InterruptedException {
        return communityRepository.fw(id);
    }


    public void add(CommunityReport data){ communityRepository.create(data);}

    public void add(CommunityReport... data){communityRepository.create(data);  }
}
