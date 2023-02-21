package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.SubdistrictDao;

import org.openhds.hdsscapture.entity.Subdistrict;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubdistrictRepository {

    private final SubdistrictDao dao;

    public SubdistrictRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.subdistrictDao();
    }

    public void create(Subdistrict... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Subdistrict data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Subdistrict find(String id) throws ExecutionException, InterruptedException {

        Callable<Subdistrict> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Subdistrict> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Subdistrict> findByDistrictId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Subdistrict>> callable = () -> dao.retrieveByDistrictId(id);

        Future<List<Subdistrict>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Subdistrict> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Subdistrict>> callable = () -> dao.retrieve();

        Future<List<Subdistrict>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
