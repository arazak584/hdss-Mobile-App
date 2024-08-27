package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.HierarchyRepository;
import org.openhds.hdsscapture.entity.Hierarchy;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HierarchyViewModel extends AndroidViewModel {

    private final HierarchyRepository hierarchyRepository;


    public HierarchyViewModel(@NonNull Application application) {
        super(application);
        hierarchyRepository = new HierarchyRepository(application);
    }

    public List<Hierarchy> retrieveLevel1() throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel1();
    }

    public List<Hierarchy> retrieveLevel2(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel2(id);
    }

    public List<Hierarchy> retrieveLevel3(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel3(id);
    }

    public List<Hierarchy> retrieveLevel4(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel4(id);
    }

    public List<Hierarchy> retrieveLevel5(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel5(id);
    }

    public List<Hierarchy> retrieveLevel5i(String id,String fw) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel5i(id,fw);
    }

    public List<Hierarchy> retrieveLevel4i(String id,String fw) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel4i(id,fw);
    }

    public List<Hierarchy> retrieveLevel3i(String id,String fw) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel3i(id,fw);
    }

    public List<Hierarchy> retrieveLevel2i(String id,String fw) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel2i(id,fw);
    }

    public List<Hierarchy> retrieveLevel6(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel6(id);
    }

    public List<Hierarchy> clusters(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.clusters(id);
    }

    public List<Hierarchy> retrieveLevel7() throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveLevel7();
    }

    public List<Hierarchy> retrieveVillage() throws ExecutionException, InterruptedException {
        return hierarchyRepository.retrieveVillage();
    }

    public List<Hierarchy> repo(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.repo(id);
    }

    public List<Hierarchy> repos(String id) throws ExecutionException, InterruptedException {
        return hierarchyRepository.repos(id);
    }

    public void add(Hierarchy data){
        hierarchyRepository.create(data);
    }

    public void add(Hierarchy... data){
        hierarchyRepository.create(data);
    }

}
