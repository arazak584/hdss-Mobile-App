package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OdkDao;
import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OdkRepository {

    private final OdkDao dao;

    public OdkRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.odkDao();
    }

    public void create(OdkForm... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(OdkForm data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<OdkForm> find() throws ExecutionException, InterruptedException {

        Callable<List<OdkForm>> callable = () -> dao.find();

        Future<List<OdkForm>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
