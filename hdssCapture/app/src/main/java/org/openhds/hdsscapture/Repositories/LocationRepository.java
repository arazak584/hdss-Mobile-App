package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.entity.Location;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationRepository {

    private final LocationDao dao;

    public LocationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.locationDao();
    }

    public void create(Location... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Location data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Location find(String id) throws ExecutionException, InterruptedException {

        Callable<Location> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Location> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Location> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Location>> callable = () -> dao.retrieveToSync();

        Future<List<Location>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Location> findByClusterId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Location>> callable = () -> dao.retrieveByClusterId(id);

        Future<List<Location>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Location> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Location>> callable = () -> dao.retrieve();

        Future<List<Location>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Location> findBySearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Location>> callable = () -> dao.retrieveBySearch(id);

        Future<List<Location>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
