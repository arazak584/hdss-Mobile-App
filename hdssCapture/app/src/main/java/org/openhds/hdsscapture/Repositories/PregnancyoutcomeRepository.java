package org.openhds.hdsscapture.Repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PregnancyoutcomeRepository {

    private final PregnancyoutcomeDao dao;

    public PregnancyoutcomeRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.pregnancyoutcomeDao();
    }


    public void create(Pregnancyoutcome data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Pregnancyoutcome... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void update(OutcomeUpdate s, Consumer<Integer> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int result = dao.update(s);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
        });
    }

    public List<Pregnancyoutcome> findToSync() throws ExecutionException, InterruptedException {
        Callable<List<Pregnancyoutcome>> callable = () -> dao.retrieveToSync();
        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Pregnancyoutcome> error(String id) throws ExecutionException, InterruptedException {
        Callable<List<Pregnancyoutcome>> callable = () -> dao.error(id);
        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome find(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.find(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome preg(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.preg(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome find1(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.find1(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public Pregnancyoutcome findloc(String id,String locid) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findloc(id,locid);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome findMother(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findMother(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome findedit(String id,String locid) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findedit(id,locid);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome finds(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.finds(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome finds3(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.finds3(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public Pregnancyoutcome findsloc(String id,String locid) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findsloc(id,locid);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public Pregnancyoutcome lastpregs(String id, Date recordedDate) throws ExecutionException, InterruptedException {

        Callable<Pregnancyoutcome> callable = () -> dao.lastpregs(id,recordedDate);

        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
    public Pregnancyoutcome find3(String id,String locid) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.find3(id,locid);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
    public Pregnancyoutcome findout(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findout(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome findout3(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findout3(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome ins(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.ins(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public Pregnancyoutcome findpreg(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancyoutcome> callable = () -> dao.findpreg(id);
        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long count(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(startDate, endDate, username);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.rej(uuid);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long cnt(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnt(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Pregnancyoutcome> reject(String id) throws ExecutionException, InterruptedException {

        Callable<List<Pregnancyoutcome>> callable = () -> dao.reject(id);

        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancyoutcome> retrieveOutcome(String id) throws ExecutionException, InterruptedException {

        Callable<List<Pregnancyoutcome>> callable = () -> dao.retrieveOutcome(id);

        Future<List<Pregnancyoutcome>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Pregnancyoutcome getId(String id) throws ExecutionException, InterruptedException {

        Callable<Pregnancyoutcome> callable = () -> dao.getId(id);

        Future<Pregnancyoutcome> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Pregnancyoutcome> getByUuids(List<String> uuids) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<List<Pregnancyoutcome>> callable = () -> dao.getByUuids(uuids);
            Future<List<Pregnancyoutcome>> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdown();
        }
    }

    public LiveData<Pregnancyoutcome> view(String id) {
        return dao.getView(id);
    }

    public LiveData<Long> sync() {
        return dao.sync();
    }
}
