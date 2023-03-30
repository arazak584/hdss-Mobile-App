package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.entity.Locations;

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

    public void create(Locations... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Locations data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Locations find(String id) throws ExecutionException, InterruptedException {

        Callable<Locations> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Locations> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveToSync();

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findByClusterId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveByClusterId(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> retrieveByVillage(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveByVillage(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieve();

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findBySearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveBySearch(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
