package org.openhds.hdsscapture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.openhds.hdsscapture.Dao.AmendmentDao;
import org.openhds.hdsscapture.Dao.ApiUrlDao;
import org.openhds.hdsscapture.Dao.CodeBookDao;
import org.openhds.hdsscapture.Dao.ConfigDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.Dao.FieldworkerDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.HierarchyDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.ListingDao;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.Dao.OutcomeDao;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.Dao.RoundDao;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.Dao.VaccinationDao;
import org.openhds.hdsscapture.Dao.VisitDao;
import org.openhds.hdsscapture.Utilities.Converter;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.ApiUrl;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(  entities = {
        Relationship.class, Locations.class, Residency.class, Pregnancyoutcome.class, Individual.class, Round.class, Demographic.class,
        Visit.class, Outmigration.class, Death.class, Socialgroup.class, Pregnancy.class, CodeBook.class, Hierarchy.class,
        Fieldworker.class, Inmigration.class, HdssSociodemo.class, Outcome.class, Listing.class, Amendment.class, Vaccination.class, Duplicate.class,
        ApiUrl.class, Configsettings.class
        }, version = 2 , exportSchema = true)

@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract IndividualDao individualDao();
    public abstract CodeBookDao codeBookDao();
    public abstract LocationDao locationDao();
    public abstract FieldworkerDao fieldworkerDao();
    public abstract SocialgroupDao socialgroupDao();
    public abstract DeathDao deathDao();
    public abstract ResidencyDao residencyDao();
    public abstract OutmigrationDao outmigrationDao();
    public abstract PregnancyDao pregnancyDao();
    public abstract PregnancyoutcomeDao pregnancyoutcomeDao();
    public abstract InmigrationDao inmigrationDao();
    public abstract RelationshipDao relationshipDao();
    public abstract VisitDao visitDao();
    public abstract RoundDao roundDao();
    public abstract DemographicDao demographicDao();
    public abstract HierarchyDao hierarchyDao();
    public abstract HdssSociodemoDao hdssSociodemoDao();
    public abstract OutcomeDao outcomeDao();
    public abstract ListingDao listingDao();
    public abstract AmendmentDao amendmentDao();
    public abstract VaccinationDao vaccinationDao();
    public abstract DuplicateDao duplicateDao();
    public abstract ApiUrlDao apiUrlDao();
    public abstract ConfigDao configDao();


    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "hdss")
                                .addCallback(sRoomDatabaseCallback)
                                .fallbackToDestructiveMigration()
                                .build();
            }
        }
        //Return Database
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public void resetDatabase(final Context context, final ResetCallback callback) {
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Delete all data from the tables
                individualDao().deleteAll();
                codeBookDao().deleteAll();
                locationDao().deleteAll();
                deathDao().deleteAll();
                demographicDao().deleteAll();
                hdssSociodemoDao().deleteAll();
                hierarchyDao().deleteAll();
                inmigrationDao().deleteAll();
                outcomeDao().deleteAll();
                outmigrationDao().deleteAll();
                pregnancyDao().deleteAll();
                pregnancyoutcomeDao().deleteAll();
                relationshipDao().deleteAll();
                residencyDao().deleteAll();
                roundDao().deleteAll();
                socialgroupDao().deleteAll();
                visitDao().deleteAll();
                listingDao().deleteAll();
                amendmentDao().deleteAll();
                vaccinationDao().deleteAll();
                duplicateDao().deleteAll();
                fieldworkerDao().deleteAll();
                configDao().deleteAll();

                // Perform any other necessary cleanup or initialization

                // Invoke the callback when all entities are reset
                callback.onResetComplete();
            }
        });
    }

    // Callback interface to notify when all entities are reset
    public interface ResetCallback {
        void onResetComplete();
    }


}

