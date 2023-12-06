package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.VpmDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Vpm;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VpmRepository {

    private final VpmDao dao;

    public VpmRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.vpmDao();
    }

    public void create(Vpm... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(Vpm data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Vpm> retrieveToSync() throws ExecutionException, InterruptedException {

        Callable<List<Vpm>> callable = () -> dao.retrieveToSync();

        Future<List<Vpm>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Vpm find(String id) throws ExecutionException, InterruptedException {

        Callable<Vpm> callable = () -> dao.find(id);

        Future<Vpm> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
