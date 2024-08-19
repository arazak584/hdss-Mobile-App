package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PregnancyRepository {

    private final PregnancyDao dao;

    public PregnancyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.pregnancyDao();
    }

    public void create(Pregnancy... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Pregnancy data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Pregnancy> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.retrieveToSync();

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancy> retrievePregnancy(String id) throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.retrievePregnancy(id);

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy find(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.find(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy out(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.out(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy out2(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.out2(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy lastpregs(String id,Date recordedDate) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.lastpregs(id,recordedDate);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy lastpreg(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.lastpreg(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy finds(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.finds(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy ins(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.ins(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancy> retrievePreg(String id) throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.retrievePreg(id);

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy findss(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.findss(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancy findpreg(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancy> callable = () -> dao.findpreg(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

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

    public List<Pregnancy> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Pregnancy>> callable = () -> dao.reject(id);

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
