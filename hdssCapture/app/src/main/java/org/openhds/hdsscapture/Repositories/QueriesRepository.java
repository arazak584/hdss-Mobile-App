package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.QueriesDao;
import org.openhds.hdsscapture.entity.ServerQueries;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QueriesRepository {

    private final QueriesDao dao;

    public QueriesRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.queriesDao();
    }

    public void create(ServerQueries... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteAll();
        });
    }

    public List<ServerQueries> findAll() throws ExecutionException, InterruptedException {

        Callable<List<ServerQueries>> callable = () -> dao.retrieve();

        Future<List<ServerQueries>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<ServerQueries> findByFw(String fw) throws ExecutionException, InterruptedException {

        Callable<List<ServerQueries>> callable = () -> dao.findByFw(fw);

        Future<List<ServerQueries>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
