package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.entity.HdssSociodemo;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HdssSociodemoRepository {

    private final HdssSociodemoDao dao;

    public HdssSociodemoRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.hdssSociodemoDao();
    }

    public void create(HdssSociodemo s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.create(s));
    }

    public void create(HdssSociodemo... s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.create(s));
    }

    public HdssSociodemo find(String id) throws ExecutionException, InterruptedException {

        Callable<HdssSociodemo> callable = () -> dao.retrieve(id);

        Future<HdssSociodemo> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<HdssSociodemo> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<HdssSociodemo>> callable = () -> dao.retrieveToSync();

        Future<List<HdssSociodemo>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
    
}
