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
import org.openhds.hdsscapture.entity.subentity.ResidencyRelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdateEndDate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResidencyViewModel extends AndroidViewModel {

    private final ResidencyRepository repo;


    public ResidencyViewModel(@NonNull Application application) {
        super(application);
        repo = new ResidencyRepository(application);
    }

    public List<Residency> find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public List<Residency> findResidenciesBySocialgroup(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.findResidenciesBySocialgroup(id, ids);
    }

    public Residency findRes(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findRes(id, locid);
    }

    public Residency getUuid(String id) throws ExecutionException, InterruptedException {
        return repo.getUuid(id);
    }

    public Residency findDth(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findDth(id, locid);
    }

    public Residency resomg(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.resomg(id, locid);
    }

    public Residency findEnd(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findEnd(id, locid);
    }

    public Residency finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public Residency findLastButOne(String id) throws ExecutionException, InterruptedException {
        return repo.findLastButOne(id);
    }

    public Residency updateres(String id) throws ExecutionException, InterruptedException {
        return repo.updateres(id);
    }

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }


    public Residency fetch(String id) throws ExecutionException, InterruptedException {
        return repo.fetch(id);
    }

    public Residency fetchs(String id, String locid) throws ExecutionException, InterruptedException {
        return repo.fetchs(id, locid);
    }

    public Residency unk(String id) throws ExecutionException, InterruptedException {
        return repo.unk(id);
    }

    public Residency amend(String id) throws ExecutionException, InterruptedException {
        return repo.amend(id);
    }

    public Residency views(String id) throws ExecutionException, InterruptedException {
        return repo.views(id);
    }

    public Residency dth(String id, String locid) throws ExecutionException, InterruptedException {
        return repo.dth(id,locid);
    }

    public Residency restore(String id) throws ExecutionException, InterruptedException {
        return repo.restore(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public List<Residency> error() throws ExecutionException, InterruptedException {
        return repo.error();
    }

    public LiveData<Residency> getView(String id) {
        return repo.view(id);
    }

    public LiveData<Residency> getViews(String id) {
        return repo.getViews(id);
    }

    public void add(Residency data){ repo.create(data);}

    public void add(Residency... data){
        repo.create(data);
    }

//    public int update(ResidencyAmendment s){
//        return repo.update(s);
//    }

    public void update(ResidencyAmendment s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public void update(ResidencyRelationshipUpdate s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public void updates(ResidencyUpdate s, Consumer<Integer> callback) {
        repo.updates(s, callback);
    }

    public void updatez(ResidencyUpdateEndDate s, Consumer<Integer> callback) {
        repo.updatez(s, callback);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

//    public int update(ResidencyUpdate s){
//        return repo.update(s);
//    }
}
