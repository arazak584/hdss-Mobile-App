package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.entity.Individual;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IndividualRepository {

    private final IndividualDao dao;

    public IndividualRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.individualDao();
    }

    public void create(Individual... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Individual data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<Individual> findAll() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieve();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveToSync();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByLocationId(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByMother(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByMother(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByMotherSearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByMotherSearch(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveBySearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveBySearch(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }



    public List<Individual> retrieveByFatherSearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByFatherSearch(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByFather(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByFather(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
