package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.HierarchyRepository;
import org.openhds.hdsscapture.entity.Hierarchy;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HierarchyViewModel extends AndroidViewModel {

    private final HierarchyRepository repo;


    public HierarchyViewModel(@NonNull Application application) {
        super(application);
        repo = new HierarchyRepository(application);
    }

    public List<Hierarchy> retrieveLevel1() throws ExecutionException, InterruptedException {
        return repo.retrieveLevel1();
    }

    public List<Hierarchy> retrieveLevel2(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel2(id);
    }

    public List<Hierarchy> retrieveLevel3(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel3(id);
    }

    public List<Hierarchy> retrieveLevel4(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel4(id);
    }

    public List<Hierarchy> retrieveLevel5(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel5(id);
    }

    public List<Hierarchy> retrieveLevel5i(String id,String fw) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel5i(id,fw);
    }

    public List<Hierarchy> retrieveLevel4i(String id,String fw) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel4i(id,fw);
    }

    public List<Hierarchy> retrieveLevel3i(String id,String fw) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel3i(id,fw);
    }

    public List<Hierarchy> retrieveLevel(String parentId,String level) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel(parentId,level);
    }

    public List<Hierarchy> retrieveLevel2i(String id,String fw) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel2i(id,fw);
    }

    public List<Hierarchy> retrieveLevel6(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveLevel6(id);
    }

    public List<Hierarchy> clusters(String id) throws ExecutionException, InterruptedException {
        return repo.clusters(id);
    }

    public List<Hierarchy> retrieveLevel7() throws ExecutionException, InterruptedException {
        return repo.retrieveLevel7();
    }

    public List<Hierarchy> retrieveVillage() throws ExecutionException, InterruptedException {
        return repo.retrieveVillage();
    }

    public List<Hierarchy> repo(String id) throws ExecutionException, InterruptedException {
        return repo.repo(id);
    }

    public List<Hierarchy> repos(String id) throws ExecutionException, InterruptedException {
        return repo.repos(id);
    }

    public void add(Hierarchy data){
        repo.create(data);
    }

    public void add(Hierarchy... data){
        repo.create(data);
    }

}
