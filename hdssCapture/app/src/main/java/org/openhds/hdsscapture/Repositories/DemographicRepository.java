package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
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

    public Demographic find(String id) throws ExecutionException, InterruptedException {

        Callable<Demographic> callable = () -> dao.find(id);

        Future<Demographic> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Demographic> error() throws ExecutionException, InterruptedException {

        Callable<List<Demographic>> callable = () -> dao.error();

        Future<List<Demographic>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
}
