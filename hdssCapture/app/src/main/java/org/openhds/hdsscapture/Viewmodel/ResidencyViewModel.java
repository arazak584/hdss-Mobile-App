package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.ResidencyRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdateEndDate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResidencyViewModel extends AndroidViewModel {

    private final ResidencyRepository residencyRepository;


    public ResidencyViewModel(@NonNull Application application) {
        super(application);
        residencyRepository = new ResidencyRepository(application);
    }

    public List<Residency> find(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.find(id);
    }

    public Residency findRes(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.findRes(id, locid);
    }

    public Residency findDth(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.findDth(id, locid);
    }

    public Residency resomg(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.resomg(id, locid);
    }

    public Residency findEnd(String id,String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.findEnd(id, locid);
    }

    public Residency finds(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.finds(id);
    }

    public Residency findLastButOne(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.findLastButOne(id);
    }

    public Residency updateres(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.updateres(id);
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

    public Residency unk(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.unk(id);
    }

    public Residency amend(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.amend(id);
    }

    public Residency views(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.views(id);
    }

    public Residency dth(String id, String locid) throws ExecutionException, InterruptedException {
        return residencyRepository.dth(id,locid);
    }

    public Residency restore(String id) throws ExecutionException, InterruptedException {
        return residencyRepository.restore(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return residencyRepository.count(startDate, endDate, username);
    }

    public List<Residency> error() throws ExecutionException, InterruptedException {
        return residencyRepository.error();
    }

    public LiveData<Residency> getView(String id) {
        return residencyRepository.view(id);
    }

    public void add(Residency data){ residencyRepository.create(data);}

    public void add(Residency... data){
        residencyRepository.create(data);
    }

//    public int update(ResidencyAmendment s){
//        return residencyRepository.update(s);
//    }

    public void update(ResidencyAmendment s, Consumer<Integer> callback) {
        residencyRepository.update(s, callback);
    }

    public void updates(ResidencyUpdate s, Consumer<Integer> callback) {
        residencyRepository.updates(s, callback);
    }

    public void updatez(ResidencyUpdateEndDate s, Consumer<Integer> callback) {
        residencyRepository.updatez(s, callback);
    }

//    public int update(ResidencyUpdate s){
//        return residencyRepository.update(s);
//    }
}
