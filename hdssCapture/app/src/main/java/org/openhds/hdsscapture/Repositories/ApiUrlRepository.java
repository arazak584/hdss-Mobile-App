package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ApiUrlDao;
import org.openhds.hdsscapture.Dao.CodeBookDao;
import org.openhds.hdsscapture.entity.ApiUrl;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiUrlRepository {

    private final ApiUrlDao dao;

    public ApiUrlRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.apiUrlDao();
    }

    public void create(ApiUrl data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public ApiUrl retrieve() throws ExecutionException, InterruptedException {

        Callable<ApiUrl> callable = () -> dao.retrieve();

        Future<ApiUrl> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
