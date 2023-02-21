package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.RoundDao;
import org.openhds.hdsscapture.entity.Round;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RoundRepository {

    private final RoundDao dao;

    public RoundRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.roundDao();
    }
    public void create(Round... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Round> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Round>> callable = () -> dao.retrieve();

        Future<List<Round>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
