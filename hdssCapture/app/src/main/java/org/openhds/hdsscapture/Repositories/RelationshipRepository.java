package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.entity.Relationship;
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

    public int update(RelationshipUpdate s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
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

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
