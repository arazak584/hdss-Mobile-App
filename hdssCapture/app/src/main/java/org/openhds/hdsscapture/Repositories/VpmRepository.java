package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.VpmDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.VpmUpdate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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

    public int update(VpmUpdate s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
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

    public Vpm finds(String id) throws ExecutionException, InterruptedException {

        Callable<Vpm> callable = () -> dao.finds(id);

        Future<Vpm> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }


}
