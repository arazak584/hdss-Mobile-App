package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SocialgroupRepository {


    private final SocialgroupDao dao;

    public SocialgroupRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.socialgroupDao();
    }

    public void create(Socialgroup... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Socialgroup data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Socialgroup find(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> findhse(String id) throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.findhse(id);

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> retrieveBySocialgroup(String id) throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieveBySocialgroup(id);

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieve();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieveToSync();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
