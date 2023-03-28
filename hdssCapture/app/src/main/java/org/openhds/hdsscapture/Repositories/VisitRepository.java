package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.VisitDao;
import org.openhds.hdsscapture.entity.Visit;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VisitRepository {

    private final VisitDao dao;

    public VisitRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.visitDao();
    }
    public void create(Visit... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(Visit data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Visit> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Visit>> callable = () -> dao.retrieve();

        Future<List<Visit>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Visit find(String id) throws ExecutionException, InterruptedException {

        Callable<Visit> callable = () -> dao.find(id);

        Future<Visit> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Visit> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Visit>> callable = () -> dao.retrieveToSync();

        Future<List<Visit>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
