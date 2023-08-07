package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.ResidencyRepository;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResidencyViewModel extends AndroidViewModel {

    private final ResidencyRepository residencyRepository;


    public ResidencyViewModel(@NonNull Application application) {
        super(application);
        residencyRepository = new ResidencyRepository(application);
    }


    public List<Residency> findAll() throws ExecutionException, InterruptedException {
        return residencyRepository.findAll();
    }

    public List<Residency> find(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.find(id);
    }

    public Residency findRes(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.findRes(id, locid);
    }

    public Residency findEnd(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.findEnd(id, locid);
    }

    public Residency finds(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.finds(id);
    }

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {
        return residencyRepository.findToSync();
    }


    public Residency fetch(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.fetch(id);
    }

    public Residency fetchs(String id, String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.fetchs(id, locid);
    }

    public Residency amend(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.amend(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return residencyRepository.count(startDate, endDate, username);
    }

    public void add(Residency data){ residencyRepository.create(data);}

    public void add(Residency... data){
        residencyRepository.create(data);
    }

    public int update(ResidencyAmendment s){
        return residencyRepository.update(s);
    }
}
