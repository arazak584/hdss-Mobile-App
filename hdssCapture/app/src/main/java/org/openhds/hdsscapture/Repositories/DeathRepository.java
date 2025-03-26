package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DeathRepository {

    private final DeathDao dao;

    public DeathRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.deathDao();
    }

    public void create(Death... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(Death data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Death find(String id) throws ExecutionException, InterruptedException {

        Callable<Death> callable = () -> dao.find(id);

        Future<Death> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Death retrieve(String id) throws ExecutionException, InterruptedException {

        Callable<Death> callable = () -> dao.retrieve(id);

        Future<Death> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Death finds(String id) throws ExecutionException, InterruptedException {

        Callable<Death> callable = () -> dao.finds(id);

        Future<Death> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.retrieveToSync();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> end(String id) throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.end(id);

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> error() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.error();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long cnt() throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnt();
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long err(String id,String ids) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.err(id,ids);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Death> repo() throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.repo();

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Death> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Death>> callable = () -> dao.reject(id);

        Future<List<Death>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Death ins(String id) throws ExecutionException, InterruptedException {

        Callable<Death> callable = () -> dao.ins(id);

        Future<Death> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
