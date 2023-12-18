package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OutcomeDao;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Outcome;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OutcomeRepository {

    private final OutcomeDao dao;

    public OutcomeRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.outcomeDao();
    }


    public void create(Outcome data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Outcome... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Outcome> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Outcome>> callable = () -> dao.retrieveToSync();

        Future<List<Outcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Outcome> error() throws ExecutionException, InterruptedException {

        Callable<List<Outcome>> callable = () -> dao.error();

        Future<List<Outcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public LiveData<Outcome> error1() throws ExecutionException, InterruptedException {

        Callable<LiveData<Outcome>> callable = () -> dao.error1();

        Future<LiveData<Outcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outcome find(String id) throws ExecutionException, InterruptedException {

        Callable<Outcome> callable = () -> dao.find(id);

        Future<Outcome> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
