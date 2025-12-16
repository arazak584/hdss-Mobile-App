package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OutcomeDao;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

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

    public List<Outcome> error(String id) throws ExecutionException, InterruptedException {
        Callable<List<Outcome>> callable = () -> dao.error(id);
        Future<List<Outcome>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long cnt(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnt(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Outcome find(String id,String locid) throws ExecutionException, InterruptedException {
        Callable<Outcome> callable = () -> dao.find(id,locid);
        Future<Outcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public LiveData<Outcome> view1(String id) {
        return dao.getView1(id);
    }

    public LiveData<Outcome> view2(String id) {
        return dao.getView2(id);
    }

    public LiveData<Outcome> view3(String id) {
        return dao.getView3(id);
    }

    public LiveData<Outcome> view4(String id) {
        return dao.getView4(id);
    }

    public LiveData<Outcome> view(String id) {
        return dao.getView(id);
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }
}
