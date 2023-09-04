package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.entity.Death;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DeathRepository {

    private final DeathDao dao;

    public DeathRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.deathDao();
    }

    public void create(Death... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(Death data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Death find(String id) throws ExecutionException, InterruptedException {

        Callable<Death> callable = () -> dao.find(id);

        Future<Death> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.retrieveToSync();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> retrieveVpmSync() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.retrieveVpmSync();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> error() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.error();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
}
