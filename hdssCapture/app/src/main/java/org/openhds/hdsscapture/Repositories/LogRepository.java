package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.LogDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.LogBook;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogRepository {

    private final LogDao dao;

    public LogRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.logDao();
    }

    public void create(LogBook... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public void create(LogBook data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<LogBook> retrieveToSync() throws ExecutionException, InterruptedException {

        Callable<List<LogBook>> callable = () -> dao.retrieveToSync();

        Future<List<LogBook>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
