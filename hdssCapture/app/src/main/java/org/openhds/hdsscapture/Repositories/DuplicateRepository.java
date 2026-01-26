package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Morbidity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DuplicateRepository {

    private final DuplicateDao dao;

    public DuplicateRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.duplicateDao();
    }

    public void create(Duplicate... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Duplicate data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void delete(Duplicate data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.delete(data);
        });
    }

    public void delete(Duplicate... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.delete(data);
        });
    }


    public Duplicate find(String id) throws ExecutionException, InterruptedException {

        Callable<Duplicate> callable = () -> dao.find(id);

        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Duplicate getId(String id) throws ExecutionException, InterruptedException {

        Callable<Duplicate> callable = () -> dao.getId(id);

        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Duplicate finds(String id) throws ExecutionException, InterruptedException {

        Callable<Duplicate> callable = () -> dao.finds(id);

        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Duplicate findByAnyUuid(String id) throws ExecutionException, InterruptedException {
        Callable<Duplicate> callable = () -> dao.findByAnyUuid(id);
        Future<Duplicate> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Duplicate> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Duplicate>> callable = () -> dao.retrieveSync();

        Future<List<Duplicate>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Duplicate> repo() throws ExecutionException, InterruptedException {

        Callable<List<Duplicate>> callable = () -> dao.repo();

        Future<List<Duplicate>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Duplicate> getByUuids(List<String> uuids) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<List<Duplicate>> callable = () -> dao.getByUuids(uuids);
            Future<List<Duplicate>> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdown();
        }
    }

    public List<Duplicate> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Duplicate>> callable = () -> dao.reject(id);

        Future<List<Duplicate>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }


}
