package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PregnancyoutcomeRepository {

    private final PregnancyoutcomeDao dao;

    public PregnancyoutcomeRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.pregnancyoutcomeDao();
    }


    public void create(Pregnancyoutcome data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Pregnancyoutcome... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Pregnancyoutcome> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Pregnancyoutcome>> callable = () -> dao.retrieve();

        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancyoutcome> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Pregnancyoutcome>> callable = () -> dao.retrieveToSync();

        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancyoutcome find(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancyoutcome> callable = () -> dao.find(id);

        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
