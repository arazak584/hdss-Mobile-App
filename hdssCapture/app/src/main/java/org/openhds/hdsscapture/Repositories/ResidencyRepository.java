package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ResidencyRepository {

    private final ResidencyDao dao;

    public ResidencyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.residencyDao();
    }

    public void create(Residency... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Residency data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public int update(ResidencyAmendment s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
    }

    public List<Residency> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.retrieveToSync();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Residency> find(String id) throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.find(id);

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency findRes(String id,String locid) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.findRes(id, locid);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency resomg(String id,String locid) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.resomg(id, locid);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency findEnd(String id,String locid) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.findEnd(id, locid);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency finds(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.finds(id);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public Residency fetch(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.fetch(id.toUpperCase());

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency fetchs(String id, String locid) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.fetchs(id, locid);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency unk(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.unk(id);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency amend(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.amend(id);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency dth(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.dth(id);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Residency restore(String id) throws ExecutionException, InterruptedException {

        Callable<Residency> callable = () -> dao.restore(id);

        Future<Residency> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Residency> error() throws ExecutionException, InterruptedException {

        Callable<List<Residency>> callable = () -> dao.error();

        Future<List<Residency>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
