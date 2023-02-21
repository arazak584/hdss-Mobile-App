package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.VillageDao;
import org.openhds.hdsscapture.entity.Village;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VillageRepository {

    private final VillageDao dao;

    public VillageRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.villageDao();
    }

    public void create(Village... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Village data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Village find(String id) throws ExecutionException, InterruptedException {

        Callable<Village> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Village> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Village> findByRegionId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Village>> callable = () -> dao.retrieveBySubdistrictId(id);

        Future<List<Village>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Village> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Village>> callable = () -> dao.retrieve();

        Future<List<Village>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
