package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.entity.Duplicate;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DuplicateRepository {

    private final DuplicateDao dao;

    public DuplicateRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.duplicateDao();
    }

    public void create(Duplicate... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Duplicate data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Duplicate find(String id) throws ExecutionException, InterruptedException {

        Callable<Duplicate> callable = () -> dao.find(id);

        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Duplicate finds(String id) throws ExecutionException, InterruptedException {

        Callable<Duplicate> callable = () -> dao.finds(id);

        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<Duplicate> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Duplicate>> callable = () -> dao.retrieveSync();

        Future<List<Duplicate>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Duplicate> repo() throws ExecutionException, InterruptedException {

        Callable<List<Duplicate>> callable = () -> dao.repo();

        Future<List<Duplicate>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
