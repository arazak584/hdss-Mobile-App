package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.entity.Demographic;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DemographicRepository {

    private final DemographicDao dao;

    public DemographicRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.demographicDao();
    }

    public void create(Demographic... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Demographic data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Demographic> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Demographic>> callable = () -> dao.retrieve();

        Future<List<Demographic>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Demographic> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Demographic>> callable = () -> dao.retrieveToSync();

        Future<List<Demographic>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
