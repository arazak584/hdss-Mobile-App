package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.HierarchyDao;
import org.openhds.hdsscapture.entity.Hierarchy;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HierarchyRepository {

    private final HierarchyDao dao;

    public HierarchyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.hierarchyDao();
    }

    public void create(Hierarchy data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Hierarchy... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Hierarchy> retrieveLevel1() throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel1();

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel2(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel2(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel3(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel3(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel4(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel4(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel5(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel5(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel6(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel6(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> clusters(String id) throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.clusters(id);

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveLevel7() throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveLevel7();

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Hierarchy> retrieveVillage() throws ExecutionException, InterruptedException {

        Callable<List<Hierarchy>> callable = () -> dao.retrieveVillage();

        Future<List<Hierarchy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }



}
