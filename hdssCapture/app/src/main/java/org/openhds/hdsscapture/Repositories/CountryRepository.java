package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.CountryDao;
import org.openhds.hdsscapture.entity.Country;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CountryRepository {

    private final CountryDao dao;

    public CountryRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.countryDao();
    }

    public void create(Country data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Country... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

/*
    public Country find(String id) throws ExecutionException, InterruptedException {

        Callable<Country> callable = () -> dao.retrieveRegion(id.toUpperCase());

        Future<Country> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }*/

    public List<Country> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Country>> callable = () -> dao.retrieve();

        Future<List<Country>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }




}
