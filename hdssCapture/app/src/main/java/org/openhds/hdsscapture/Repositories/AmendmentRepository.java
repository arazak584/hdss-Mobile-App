package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.AmendmentDao;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Demographic;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AmendmentRepository {

    private final AmendmentDao dao;

    public AmendmentRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.amendmentDao();
    }

    public void create(Amendment... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Amendment data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Amendment find(String id) throws ExecutionException, InterruptedException {

        Callable<Amendment> callable = () -> dao.find(id);

        Future<Amendment> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Amendment> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Amendment>> callable = () -> dao.retrieveSync();

        Future<List<Amendment>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public LiveData<Amendment> view(String id) {
        return dao.getView(id);
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }

}
