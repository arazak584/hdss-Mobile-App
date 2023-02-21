package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.ClusterRepository;
import org.openhds.hdsscapture.entity.Cluster;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClusterViewModel extends AndroidViewModel {

    private final ClusterRepository clusterRepository;


    public ClusterViewModel(@NonNull Application application) {
        super(application);
        clusterRepository = new ClusterRepository(application);
    }

    public Cluster find(String id) throws ExecutionException, InterruptedException {
        return clusterRepository.find(id);
    }

    public List<Cluster> findClustersOfVillage(String id) throws ExecutionException, InterruptedException {
        return clusterRepository.findByVillageId(id);
    }

    public List<Cluster> findAll() throws ExecutionException, InterruptedException {
        return clusterRepository.findAll();
    }

    public void add(Cluster data){
        clusterRepository.create(data);
    }

    public void add(Cluster... data){
        clusterRepository.create(data);
    }
}
