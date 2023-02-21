package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ClusterDao;
import org.openhds.hdsscapture.entity.Cluster;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClusterRepository {

    private final ClusterDao dao;

    public ClusterRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.clusterDao();
    }

    public void create(Cluster... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Cluster data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Cluster find(String id) throws ExecutionException, InterruptedException {

        Callable<Cluster> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Cluster> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Cluster> findByVillageId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Cluster>> callable = () -> dao.retrieveByVillageId(id);

        Future<List<Cluster>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Cluster> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Cluster>> callable = () -> dao.retrieve();

        Future<List<Cluster>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
