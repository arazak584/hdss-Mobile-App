package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.OdkFormDao;
import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OdkFormRepository {

    private final OdkFormDao dao;

    public OdkFormRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.odkDao();
    }

    public void create(OdkForm... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(OdkForm data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void update(OdkForm data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.update(data);
        });
    }

    public void delete(OdkForm data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.delete(data);
        });
    }

    public LiveData<List<OdkForm>> getAllEnabledForms() {
        return dao.getAllEnabledForms();
    }

    public LiveData<List<OdkForm>> getFormsForIndividual(Integer gender, Integer age) {
        return dao.getFormsForIndividual(gender, age);
    }

    public OdkForm getFormByIdSync(String formId) throws ExecutionException, InterruptedException {
        Callable<OdkForm> callable = () -> dao.getFormByIdSync(formId);
        Future<OdkForm> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }

    public LiveData<OdkForm> getFormByFormId(String formId) {
        return dao.getFormByFormId(formId);
    }

    public List<OdkForm> getAllFormsSync() throws ExecutionException, InterruptedException {
        Callable<List<OdkForm>> callable = () -> dao.getAllFormsSync();
        Future<List<OdkForm>> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }




}
