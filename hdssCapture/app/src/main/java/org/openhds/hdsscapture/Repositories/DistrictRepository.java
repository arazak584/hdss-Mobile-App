package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DistrictDao;
import org.openhds.hdsscapture.entity.District;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DistrictRepository {

    private final DistrictDao dao;

    public DistrictRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.districtDao();
    }

    public void create(District... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(District data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public District find(String id) throws ExecutionException, InterruptedException {

        Callable<District> callable = () -> dao.retrieve(id.toUpperCase());

        Future<District> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<District> findByRegionId(String id) throws ExecutionException, InterruptedException {

        Callable<List<District>> callable = () -> dao.retrieveByRegionId(id);

        Future<List<District>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<District> findAll() throws ExecutionException, InterruptedException {

        Callable<List<District>> callable = () -> dao.retrieve();

        Future<List<District>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
