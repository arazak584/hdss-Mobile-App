package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.entity.Pregnancy;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PregnancyRepository {

    private final PregnancyDao dao;

    public PregnancyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.pregnancyDao();
    }

    public void create(Pregnancy... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Pregnancy data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Pregnancy> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.retrieve();

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancy> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.retrieveToSync();

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
