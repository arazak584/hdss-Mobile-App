package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.ListingDao;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListingRepository {

    private final ListingDao dao;

    public ListingRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.listingDao();
    }

    public void create(Listing... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Listing data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Listing find(String id) throws ExecutionException, InterruptedException {

        Callable<Listing> callable = () -> dao.retrieve(id);

        Future<Listing> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Listing> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Listing>> callable = () -> dao.retrieveToSync();

        Future<List<Listing>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Listing> error() throws ExecutionException, InterruptedException {

        Callable<List<Listing>> callable = () -> dao.error();

        Future<List<Listing>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
