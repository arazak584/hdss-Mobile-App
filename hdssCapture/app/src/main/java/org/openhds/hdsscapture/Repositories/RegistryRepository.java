package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.AmendmentDao;
import org.openhds.hdsscapture.Dao.RegistryDao;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Registry;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegistryRepository {

    private final RegistryDao dao;

    public RegistryRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.registryDao();
    }

    public void create(Registry... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Registry data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Registry find(String id) throws ExecutionException, InterruptedException {

        Callable<Registry> callable = () -> dao.find(id);

        Future<Registry> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Registry finds(String id) throws ExecutionException, InterruptedException {

        Callable<Registry> callable = () -> dao.finds(id);

        Future<Registry> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Registry> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Registry>> callable = () -> dao.retrieveSync();

        Future<List<Registry>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
