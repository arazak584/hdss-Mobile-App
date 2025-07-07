package org.openhds.hdsscapture.Views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

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

    public List<CompletedForm> getAllCompletedForms() {
        List<CompletedForm> all = new ArrayList<>();
        all.addAll(pregDao.getCompletedForms());
        all.addAll(deathDao.getCompletedForms());
        all.addAll(migInDao.getCompletedForms());
        all.addAll(migOutDao.getCompletedForms());
        return all;
    }
}
