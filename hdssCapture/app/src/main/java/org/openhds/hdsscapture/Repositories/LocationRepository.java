package org.openhds.hdsscapture.Repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Consumer;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;

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

//    public int update(LocationAmendment s) {
//        AtomicInteger row = new AtomicInteger();
//        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
//        return row.intValue();
//    }

    public void update(LocationAmendment s, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.update(s);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }


    public Locations find(String id) throws ExecutionException, InterruptedException {

        Callable<Locations> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Locations> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Locations exist(String id,String ids) throws ExecutionException, InterruptedException {

        Callable<Locations> callable = () -> dao.exist(id,ids);

        Future<Locations> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Locations findByUuid(String id) throws ExecutionException, InterruptedException {

        Callable<Locations> callable = () -> dao.findByUuid(id);

        Future<Locations> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveToSync();

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Locations> retrieveAll(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.retrieveAll(id);

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

    public long counts(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.counts(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long hseCount(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.hseCount(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long done(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.done(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long work(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.work(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long works(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.works(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }


    public List<Locations> repo(String id) throws ExecutionException, InterruptedException {

        Callable<List<Locations>> callable = () -> dao.repo(id);

        Future<List<Locations>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
