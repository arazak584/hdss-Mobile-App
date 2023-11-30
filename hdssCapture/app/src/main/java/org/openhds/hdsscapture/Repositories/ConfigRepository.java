package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ConfigDao;
import org.openhds.hdsscapture.Dao.RoundDao;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Round;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConfigRepository {

    private final ConfigDao dao;

    public ConfigRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.configDao();
    }
    public void create(Configsettings... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Configsettings> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Configsettings>> callable = () -> dao.retrieve();

        Future<List<Configsettings>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
