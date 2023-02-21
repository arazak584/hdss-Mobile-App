package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.entity.Outmigration;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OutmigrationRepository {

    private final OutmigrationDao dao;

    public OutmigrationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.outmigrationDao();
    }


    public void create(Outmigration data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Outmigration> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Outmigration>> callable = () -> dao.retrieve();

        Future<List<Outmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
