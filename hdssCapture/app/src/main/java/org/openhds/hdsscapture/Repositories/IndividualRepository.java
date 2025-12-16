package org.openhds.hdsscapture.Repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualPhone;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

import java.util.ArrayList;
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

//    public int update(IndividualAmendment s) {
//        AtomicInteger row = new AtomicInteger();
//        AppDatabase.databaseWriteExecutor.execute(() -> {
//            int result = dao.update(s);
//            row.set(result);
//            Log.d("UpdateResult", "Rows updated: " + result);
//        });
//        return row.intValue();
//    }

//    public int update(IndividualAmendment s) {
//        Future<Integer> future = AppDatabase.databaseWriteExecutor.submit(() -> dao.update(s));
//        try {
//            return future.get(); // Waits for the update operation to complete
//        } catch (Exception e) {
//            e.printStackTrace();
//            return -1; // Handle the error appropriately
//        }
//    }

    public void update(IndividualAmendment s, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.update(s);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public void visited(IndividualVisited e, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.visited(e);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public void dthupdate(IndividualEnd e, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.dthupdate(e);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }


//    public int dthupdate(IndividualEnd e) {
//        AtomicInteger row = new AtomicInteger();
//        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.dthupdate(e)));
//        return row.intValue();
//    }

//    public void contact(IndividualPhone e, Consumer<Integer> callback) {
//        AppDatabase.databaseWriteExecutor.execute(() -> {
//            int result = dao.contact(e);
//            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
//        });
//    }

    public int visited(IndividualVisited s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.visited(s)));
        return row.intValue();
    }

    public int contact(IndividualPhone s) {
        AtomicInteger row = new AtomicInteger();
        AppDatabase.databaseWriteExecutor.execute(() -> row.set(dao.contact(s)));
        return row.intValue();
    }

    public void updateres(IndividualResidency e, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.updateres(e);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public Individual findAll(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.retrieve(id.toUpperCase());

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual mapregistry(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.mapregistry(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> hoh(String comp,String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.hoh(comp,id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public List<Individual> findToSync() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveToSync();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> find() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.find();

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveByLocationId(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public LiveData<List<Individual>> retrieveByHouseId(String id) {
        return dao.retrieveByHouseId(id); // No Future or Callable needed
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

    public List<Individual> morbidity(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.morbidity(id);

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

    public List<Individual> retrieveDup(String id,String ids) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveDup(id, ids);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public Individual find(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.find(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual restore(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.restore(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Individual unk(String id) throws ExecutionException, InterruptedException {

        Callable<Individual> callable = () -> dao.unk(id);

        Future<Individual> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveDth(String id) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveDth(id);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> retrieveOmg(String loc) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.retrieveOmg(loc);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> minors(String id,String ids) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.minors(id,ids);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

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

    public long counts(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.counts(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long count(String id,String ids) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(id,ids);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long err(String id,String ids) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.err(id,ids);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long errs(String id,String ids) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.errs(id,ids);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public long cnt() throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnt();
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public long cnts() throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnts();
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public long cntss() throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cntss();
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

    public List<Individual> nulls() throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.nulls();

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

    public List<Individual> dupRegistration(String uuid,String ghcard,String phone) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.dupRegistration(uuid,ghcard, phone);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Individual> DuplicatesByPhone(String uuid,String phone) throws ExecutionException, InterruptedException {

        Callable<List<Individual>> callable = () -> dao.findDuplicatesByPhone(uuid,phone);

        Future<List<Individual>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public LiveData<Individual> view(String id) {
        return dao.getView(id);
    }

    // New method to update compno for all individuals
    public void updateCompnoForIndividuals(String oldCompno, String newCompno, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.updateCompnoForIndividuals(oldCompno, newCompno);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public List<Individual> findIndividualsBatched(int gender, int minAge, int maxAge, int status) {
        List<Individual> allIndividuals = new ArrayList<>();
        int offset = 0;
        int batchSize = 1000; // Prevent memory overflow

        while (true) {
            List<Individual> batch = dao.getIndividualsBatch(gender, minAge, maxAge, status, batchSize, offset);
            if (batch.isEmpty()) break; // Stop when no more data

            allIndividuals.addAll(batch);
            offset += batchSize;
        }
        return allIndividuals;
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }

}
