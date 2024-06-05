package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InmigrationRepository {

    private final InmigrationDao dao;

    public InmigrationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.inmigrationDao();
    }

    public void create(Inmigration... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Inmigration data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public Inmigration find(String id, String locid) throws ExecutionException, InterruptedException {

        Callable<Inmigration> callable = () -> dao.find(id,locid);

        Future<Inmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Inmigration> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Inmigration>> callable = () -> dao.retrieveimgToSync();

        Future<List<Inmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Inmigration> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Inmigration>> callable = () -> dao.reject(id);

        Future<List<Inmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
