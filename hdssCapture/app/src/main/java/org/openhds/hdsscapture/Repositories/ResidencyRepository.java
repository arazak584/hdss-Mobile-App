package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.entity.Residency;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResidencyRepository {

    private final ResidencyDao dao;

    public ResidencyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.residencyDao();
    }

    public void create(Residency... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Residency data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Residency> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.retrieve();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.retrieveToSync();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Residency> findomgToSync() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.retrieveomgToSync();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Residency> findimgToSync() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.retrieveimgToSync();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
