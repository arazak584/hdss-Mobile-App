package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.SocialgroupRepository;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.HouseholdAmendment;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SocialgroupViewModel extends AndroidViewModel {

    private final SocialgroupRepository repo;


    public SocialgroupViewModel(@NonNull Application application) {
        super(application);
        repo = new SocialgroupRepository(application);
    }

    public Socialgroup retrieve(String id) throws ExecutionException, InterruptedException {
        return repo.retrieve(id);
    }

    public Socialgroup find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Socialgroup visit(String id) throws ExecutionException, InterruptedException {
        return repo.visit(id);
    }

    public Socialgroup minor(String id) throws ExecutionException, InterruptedException {
        return repo.minor(id);
    }

    public Socialgroup createhse(String id) throws ExecutionException, InterruptedException {
        return repo.createhse(id);
    }

    public List<Socialgroup> retrieveBySocialgroup(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveBySocialgroup(id);
    }

    public List<Socialgroup> changehousehold(String id) throws ExecutionException, InterruptedException {
        return repo.changehousehold(id);
    }

    public Socialgroup findhse(String id) throws ExecutionException, InterruptedException {
        return repo.findhse(id);
    }

    public List<Socialgroup> findAll(Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return repo.findAll(startDate, endDate);
    }

    public List<Socialgroup> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Socialgroup> error() throws ExecutionException, InterruptedException {
        return repo.error();
    }

    public List<Socialgroup> errors() throws ExecutionException, InterruptedException {
        return repo.errors();
    }

    public List<Socialgroup> repo(String id) throws ExecutionException, InterruptedException {
        return repo.repo(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public void add(Socialgroup data){
        repo.create(data);
    }

    public void add(Socialgroup... data){
        repo.create(data);
    }

//    public int update(SocialgroupAmendment s){
//        return repo.update(s);
//    }

    public void update(SocialgroupAmendment s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public void update(HouseholdAmendment s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public void visited(HvisitAmendment s, Consumer<Integer> callback) {
        repo.visited(s, callback);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
