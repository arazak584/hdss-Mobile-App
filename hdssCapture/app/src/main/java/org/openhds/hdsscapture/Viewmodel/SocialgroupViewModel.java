package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.SocialgroupRepository;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SocialgroupViewModel extends AndroidViewModel {

    private final SocialgroupRepository socialgroupRepository;


    public SocialgroupViewModel(@NonNull Application application) {
        super(application);
        socialgroupRepository = new SocialgroupRepository(application);
    }

    public Socialgroup retrieve(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.retrieve(id);
    }

    public Socialgroup find(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.find(id);
    }

    public Socialgroup createhse(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.createhse(id);
    }

    public List<Socialgroup> retrieveBySocialgroup(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.retrieveBySocialgroup(id);
    }

    public Socialgroup findhse(String id) throws ExecutionException, InterruptedException {
        return socialgroupRepository.findhse(id);
    }

    public List<Socialgroup> findAll(Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return socialgroupRepository.findAll(startDate, endDate);
    }

    public List<Socialgroup> findToSync() throws ExecutionException, InterruptedException {
        return socialgroupRepository.findToSync();
    }

    public List<Socialgroup> error() throws ExecutionException, InterruptedException {
        return socialgroupRepository.error();
    }

    public List<Socialgroup> errors() throws ExecutionException, InterruptedException {
        return socialgroupRepository.errors();
    }

    public List<Socialgroup> repo() throws ExecutionException, InterruptedException {
        return socialgroupRepository.repo();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return socialgroupRepository.count(startDate, endDate, username);
    }

    public void add(Socialgroup data){
        socialgroupRepository.create(data);
    }

    public void add(Socialgroup... data){
        socialgroupRepository.create(data);
    }

    public int update(SocialgroupAmendment s){
        return socialgroupRepository.update(s);
    }
}
