package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialgroupRepository {


    private final SocialgroupDao dao;

    public SocialgroupRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.socialgroupDao();
    }

    public void create(Socialgroup... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Socialgroup data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public int update(SocialgroupAmendment s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
    }

    public int visited(HvisitAmendment s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.visited(s)));
        return row.intValue();
    }


    public Socialgroup retrieve(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Socialgroup find(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.find(id);

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Socialgroup visit(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.visit(id);

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Socialgroup minor(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.minor(id);

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Socialgroup createhse(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.createhse(id.toUpperCase());

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Socialgroup findhse(String id) throws ExecutionException, InterruptedException {

        Callable<Socialgroup> callable = () -> dao.findhse(id);

        Future<Socialgroup> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> retrieveBySocialgroup(String id) throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieveBySocialgroup(id);

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> changehousehold(String id) throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.changehousehold(id);

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> findAll(Date startDate, Date endDate) throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieve(startDate, endDate);

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.retrieveToSync();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> error() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.error();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> errors() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.errors();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Socialgroup> repo() throws ExecutionException, InterruptedException {

        Callable<List<Socialgroup>> callable = () -> dao.repo();

        Future<List<Socialgroup>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
