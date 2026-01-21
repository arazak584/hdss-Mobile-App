package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.MorbidityDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Morbidity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MorbidityRepository {

    private final MorbidityDao dao;

    public MorbidityRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.morbidityDao();
    }

    public void create(Morbidity s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.create(s));
    }

    public void create(Morbidity... s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.create(s));
    }

    public List<Morbidity> find(String id) throws ExecutionException, InterruptedException {

        Callable<List<Morbidity>> callable = () -> dao.retrieve(id);

        Future<List<Morbidity>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Morbidity finds(String id) throws ExecutionException, InterruptedException {

        Callable<Morbidity> callable = () -> dao.find(id);

        Future<Morbidity> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
    public Morbidity ins(String id) throws ExecutionException, InterruptedException {

        Callable<Morbidity> callable = () -> dao.ins(id);

        Future<Morbidity> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<Morbidity> retrieveToSync() throws ExecutionException, InterruptedException {

        Callable<List<Morbidity>> callable = () -> dao.retrieveToSync();

        Future<List<Morbidity>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Morbidity> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Morbidity>> callable = () -> dao.reject(id);

        Future<List<Morbidity>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long counts(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.counts(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Morbidity> getByUuids(List<String> uuids) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<List<Morbidity>> callable = () -> dao.getByUuids(uuids);
            Future<List<Morbidity>> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdown();
        }
    }

    public LiveData<Morbidity> view(String id) {
        return dao.getView(id);
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }
}
