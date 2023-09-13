package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class LocationRepository {

    private final LocationDao dao;

    public LocationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.locationDao();
    }

    public void create(Locations... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Locations data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public int update(LocationAmendment s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
    }


    public Locations find(String id) throws ExecutionException, InterruptedException {

        Callable<Locations> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Locations> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveToSync();

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findByClusterId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveByClusterId(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findBySearch(String id,String ids) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveBySearch(id, ids);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> retrieveBySearchs(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveBySearchs(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> retrieveByVillage(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveByVillage(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> filter(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.filter(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Locations> repo() throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.repo();

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
