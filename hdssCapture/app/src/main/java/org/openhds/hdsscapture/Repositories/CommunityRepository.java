package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.CommunityDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommunityRepository {

    private final CommunityDao dao;

    public CommunityRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.communityDao();
    }

    public void create(CommunityReport... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(CommunityReport data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<CommunityReport> retrieves(String id) throws ExecutionException, InterruptedException {

        Callable<List<CommunityReport>> callable = () -> dao.retrieves(id);

        Future<List<CommunityReport>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public CommunityReport retrieve() throws ExecutionException, InterruptedException {

        Callable<CommunityReport> callable = () -> dao.retrieve();

        Future<CommunityReport> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public CommunityReport find(String id) throws ExecutionException, InterruptedException {

        Callable<CommunityReport> callable = () -> dao.find(id);

        Future<CommunityReport> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<CommunityReport> retrieveToSync() throws ExecutionException, InterruptedException {

        Callable<List<CommunityReport>> callable = () -> dao.retrieveToSync();

        Future<List<CommunityReport>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<CommunityReport> fw(String id) throws ExecutionException, InterruptedException {

        Callable<List<CommunityReport>> callable = () -> dao.fw(id);

        Future<List<CommunityReport>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
