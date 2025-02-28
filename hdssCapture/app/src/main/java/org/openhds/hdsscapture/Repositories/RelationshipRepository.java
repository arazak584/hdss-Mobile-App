package org.openhds.hdsscapture.Repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Consumer;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RelationshipRepository {

    private final RelationshipDao dao;

    public RelationshipRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.relationshipDao();
    }

    public void create(Relationship... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Relationship data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void update(RelationshipUpdate s, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.update(s);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public List<Relationship> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Relationship>> callable = () -> dao.retrieveToSync();

        Future<List<Relationship>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Relationship find(String id) throws ExecutionException, InterruptedException {

        Callable<Relationship> callable = () -> dao.find(id);

        Future<Relationship> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Relationship finds(String id) throws ExecutionException, InterruptedException {

        Callable<Relationship> callable = () -> dao.finds(id);

        Future<Relationship> future = Executors.newSingleThreadExecutor().submit(callable);

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

    public List<Relationship> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Relationship>> callable = () -> dao.reject(id);

        Future<List<Relationship>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
