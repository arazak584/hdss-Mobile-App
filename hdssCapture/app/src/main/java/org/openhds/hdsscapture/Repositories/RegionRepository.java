package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.RegionDao;
import org.openhds.hdsscapture.entity.Region;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegionRepository {

    private final RegionDao dao;

    public RegionRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.regionDao();
    }

    public void create(Region... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Region data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Region find(String id) throws ExecutionException, InterruptedException {

        Callable<Region> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Region> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Region> findByCountryId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Region>> callable = () -> dao.retrieveByCountryId(id.toUpperCase());

        Future<List<Region>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Region> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Region>> callable = () -> dao.retrieve();

        Future<List<Region>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
