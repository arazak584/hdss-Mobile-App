package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.OdkFormRepository;
import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OdkFormViewModel extends AndroidViewModel {

    private final OdkFormRepository repo;

    public OdkFormViewModel(@NonNull Application application) {
        super(application);
        repo = new OdkFormRepository(application);
    }

    public void add(OdkForm... data) {
        repo.create(data);
    }

    public void add(OdkForm data) {
        repo.create(data);
    }

    public void update(OdkForm data) {
        repo.update(data);
    }

    public void delete(OdkForm data) {
        repo.delete(data);
    }

    /**
     * Get all enabled forms
     */
    public LiveData<List<OdkForm>> getAllEnabledForms() {
        return repo.getAllEnabledForms();
    }

    /**
     * Get forms matching individual's gender and age
     */
    public LiveData<List<OdkForm>> getFormsForIndividual(Integer gender, Integer age) {
        return repo.getFormsForIndividual(gender, age);
    }

    /**
     * Get form by ID (synchronous)
     */
    public OdkForm getFormById(String formId) throws ExecutionException, InterruptedException {
        return repo.getFormByIdSync(formId);
    }

    /**
     * Get form by form ID
     */
    public LiveData<OdkForm> getFormByFormId(String formId) {
        return repo.getFormByFormId(formId);
    }

    /**
     * Get all forms (synchronous)
     */
    public List<OdkForm> getAllFormsSync() throws ExecutionException, InterruptedException {
        return repo.getAllFormsSync();
    }
}
