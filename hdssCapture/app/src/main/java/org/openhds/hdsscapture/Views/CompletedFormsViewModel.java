package org.openhds.hdsscapture.Views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;

import java.util.ArrayList;
import java.util.List;

public class CompletedFormsViewModel extends AndroidViewModel {

    private final PregnancyDao pregDao;
    private final DeathDao deathDao;
    private final InmigrationDao migInDao;
    private final OutmigrationDao migOutDao;

    public CompletedFormsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.pregDao = db.pregnancyDao();
        this.deathDao = db.deathDao();
        this.migInDao = db.inmigrationDao();
        this.migOutDao = db.outmigrationDao();
    }

    public LiveData<List<CompletedForm>> getAllCompletedForms() {
        MutableLiveData<List<CompletedForm>> result = new MutableLiveData<>();

        // Execute on background thread using the database executor
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<CompletedForm> all = new ArrayList<>();

                // Get completed forms from each DAO
                List<CompletedForm> pregnancies = pregDao.getCompletedForms();
                List<CompletedForm> deaths = deathDao.getCompletedForms();
                List<CompletedForm> inmigrations = migInDao.getCompletedForms();
                List<CompletedForm> outmigrations = migOutDao.getCompletedForms();

                // Add all forms to the combined list
                if (pregnancies != null) all.addAll(pregnancies);
                if (deaths != null) all.addAll(deaths);
                if (inmigrations != null) all.addAll(inmigrations);
                if (outmigrations != null) all.addAll(outmigrations);

                // Post the result back to the main thread
                result.postValue(all);

            } catch (Exception e) {
                // Handle any database errors
                e.printStackTrace();
                result.postValue(new ArrayList<>()); // Return empty list on error
            }
        });

        return result;
    }
}