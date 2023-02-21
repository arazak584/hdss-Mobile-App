package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.FieldworkerDao;
import org.openhds.hdsscapture.entity.Fieldworker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FieldworkerRepository {

    private final FieldworkerDao dao;

    public FieldworkerRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.fieldworkerDao();
    }

    public void create(Fieldworker... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Fieldworker data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Fieldworker find(String id, String password) throws ExecutionException, InterruptedException {

        Callable<Fieldworker> callable = () -> dao.retrieve(id, password);

        Future<Fieldworker> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<Fieldworker> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Fieldworker>> callable = () -> dao.retrieve();

        Future<List<Fieldworker>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public Fieldworker finds(String id) throws ExecutionException, InterruptedException {
        Callable<Fieldworker> callable = () -> dao.retrieves(id);

        Future<Fieldworker> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
