package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class IndividualRepository {

    private final IndividualDao dao;

    public IndividualRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.individualDao();
    }

    public void create(Individual... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Individual data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public int update(IndividualAmendment s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.update(s)));
        return row.intValue();
    }

    public int dthupdate(IndividualEnd e) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.dthupdate(e)));
        return row.intValue();
    }

    public int visited(IndividualVisited e) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.visited(e)));
        return row.intValue();
    }

    public int updateres(IndividualResidency e) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.updateres(e)));
        return row.intValue();
    }

    public Individual findAll(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<Individual> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveToSync();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByLocationId(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveReturn(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveReturn(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveBy(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveBy(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveChild(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveChild(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByMother(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByMother(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByMotherSearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByMotherSearch(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveBySearch(String id,String searchText) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveBySearch(id, searchText);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByFatherSearch(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByFatherSearch(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveHOH(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveHOH(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByFather(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByFather(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrievePartner(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrievePartner(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveDup(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveDup(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public Individual find(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.find(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual mother(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.mother(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual father(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.father(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual visited(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.visited(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long countIndividuals(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.countIndividuals(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }


    public List<Individual> error() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.error();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> errors() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.errors();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> err() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.err();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> repo() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.repo();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
