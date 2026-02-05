package org.openhds.hdsscapture.Views;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.AmendmentDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.ListingDao;
import org.openhds.hdsscapture.Dao.MorbidityDao;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.Dao.VaccinationDao;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Relationship;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompletedFormsViewModel extends AndroidViewModel {

    private final PregnancyDao pregDao;
    private final DeathDao deathDao;
    private final InmigrationDao migInDao;
    private final OutmigrationDao migOutDao;
    private final DemographicDao demographicDao;
    private final AmendmentDao amendmentDao;
    private final HdssSociodemoDao hdssSociodemoDao;
    private final IndividualDao individualDao;
    private final ListingDao listingDao;
    private final MorbidityDao morbidityDao;
    private final RelationshipDao relationshipDao;
    private final ResidencyDao residencyDao;
    private final VaccinationDao vaccinationDao;
    private final PregnancyoutcomeDao pregnancyoutcomeDao;
    private final DuplicateDao duplicateDao;

    public CompletedFormsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.pregDao = db.pregnancyDao();
        this.deathDao = db.deathDao();
        this.migInDao = db.inmigrationDao();
        this.migOutDao = db.outmigrationDao();
        this.demographicDao = db.demographicDao();
        this.amendmentDao = db.amendmentDao();
        this.hdssSociodemoDao = db.hdssSociodemoDao();
        this.individualDao = db.individualDao();
        this.listingDao = db.listingDao();
        this.morbidityDao = db.morbidityDao();
        this.relationshipDao = db.relationshipDao();
        this.residencyDao = db.residencyDao();
        this.vaccinationDao = db.vaccinationDao();
        this.pregnancyoutcomeDao = db.pregnancyoutcomeDao();
        this.duplicateDao = db.duplicateDao();
    }

    public LiveData<List<CompletedForm>> getAllCompletedForms() {
        MutableLiveData<List<CompletedForm>> result = new MutableLiveData<>();

        // Execute on background thread using the database executor
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<CompletedForm> all = new ArrayList<>();

                // Get completed forms from each DAO
                List<CompletedForm> pregnancies = pregDao.getCompletedForms();
                List<CompletedForm> demographic = demographicDao.getCompletedForms();
                List<CompletedForm> amendment = amendmentDao.getCompletedForms();
                List<CompletedForm> deaths = deathDao.getCompletedForms();
                List<CompletedForm> ses = hdssSociodemoDao.getCompletedForms();
                List<CompletedForm> individual = individualDao.getCompletedForms();
                List<CompletedForm> inmigration = migInDao.getCompletedForms();
                List<CompletedForm> outmigrations = migOutDao.getCompletedForms();
                List<CompletedForm> listing = listingDao.getCompletedForms();
                List<CompletedForm> morbidity = morbidityDao.getCompletedForms();
                List<CompletedForm> relationship = relationshipDao.getCompletedForms();
                List<CompletedForm> membership = residencyDao.getCompletedForms();
                List<CompletedForm> vaccination = vaccinationDao.getCompletedForms();
                List<CompletedForm> birth = pregnancyoutcomeDao.getCompletedForms();
                List<CompletedForm> duplicate = duplicateDao.getCompletedForms();


                // Add all forms to the combined list
                if (pregnancies != null) all.addAll(pregnancies);
                if (demographic != null) all.addAll(demographic);
                if (amendment != null) all.addAll(amendment);
                if (deaths != null) all.addAll(deaths);
                if (ses != null) all.addAll(ses);
                if (individual != null) all.addAll(individual);
                if (inmigration != null) all.addAll(inmigration);
                if (outmigrations != null) all.addAll(outmigrations);
                if (listing != null) all.addAll(listing);
                if (morbidity != null) all.addAll(morbidity);
                if (relationship != null) all.addAll(relationship);
                if (membership != null) all.addAll(membership);
                if (vaccination != null) all.addAll(vaccination);
                if (birth != null) all.addAll(birth);
                if (duplicate != null) all.addAll(duplicate);


                // Sort by insertDate (most recent first)
                // Debug: Log before sorting
                Log.d("CompletedFormsViewModel", "Before sorting - Total forms: " + all.size());
                for (int i = 0; i < Math.min(5, all.size()); i++) {
                    CompletedForm form = all.get(i);
                    Log.d("CompletedFormsViewModel", "Form " + i + ": " + form.formType +
                            " - Date: " + new Date(form.insertDate).toString());
                }

                // Sort by insertDate (most recent first) - comparing long timestamps
                all.sort((form1, form2) -> {
                    // Sort in descending order (most recent first)
                    // Larger timestamp means more recent date
                    return Long.compare(form2.insertDate, form1.insertDate);
                });

                // Debug: Log after sorting
                Log.d("CompletedFormsViewModel", "After sorting:");
                for (int i = 0; i < Math.min(5, all.size()); i++) {
                    CompletedForm form = all.get(i);
                    Log.d("CompletedFormsViewModel", "Form " + i + ": " + form.formType +
                            " - Date: " + new Date(form.insertDate).toString());
                }

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