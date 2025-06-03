package org.openhds.hdsscapture.Repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Consumer;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.subentity.OmgUpdate;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class OutmigrationRepository {

    private final OutmigrationDao dao;

    public OutmigrationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.outmigrationDao();
    }

//    public int update(OmgUpdate s) {
//        AtomicInteger row = new AtomicInteger();
//        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
//        return row.intValue();
//    }

    public void update(OmgUpdate s, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.update(s);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public void create(Outmigration data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Outmigration... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Outmigration> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Outmigration>> callable = () -> dao.retrieveomgToSync();

        Future<List<Outmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration createOmg(String id, String locid) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.createOmg(id, locid);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration find(String id,String locid) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.find(id,locid);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration edit(String id,String locid) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.edit(id,locid);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration finds(String id,String res) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.finds(id,res);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration findLast(String id) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.findLast(id);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Outmigration> end(String id) throws ExecutionException, InterruptedException {

        Callable<List<Outmigration>> callable = () -> dao.end(id);

        Future<List<Outmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Outmigration> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Outmigration>> callable = () -> dao.reject(id);

        Future<List<Outmigration>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Outmigration ins(String id) throws ExecutionException, InterruptedException {

        Callable<Outmigration> callable = () -> dao.ins(id);

        Future<Outmigration> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
}
