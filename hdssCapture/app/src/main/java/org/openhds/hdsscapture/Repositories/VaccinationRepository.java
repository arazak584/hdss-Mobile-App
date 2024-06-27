package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.VaccinationDao;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VaccinationRepository {

    private final VaccinationDao dao;

    public VaccinationRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.vaccinationDao();
    }

    public void create(Vaccination... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Vaccination data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public Vaccination find(String id) throws ExecutionException, InterruptedException {

        Callable<Vaccination> callable = () -> dao.find(id);

        Future<Vaccination> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Vaccination> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Vaccination>> callable = () -> dao.retrieveSync();

        Future<List<Vaccination>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Vaccination> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Vaccination>> callable = () -> dao.reject(id);

        Future<List<Vaccination>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
