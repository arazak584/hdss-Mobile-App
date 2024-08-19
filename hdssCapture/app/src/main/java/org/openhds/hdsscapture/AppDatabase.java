package org.openhds.hdsscapture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.openhds.hdsscapture.Dao.AmendmentDao;
import org.openhds.hdsscapture.Dao.ApiUrlDao;
import org.openhds.hdsscapture.Dao.CodeBookDao;
import org.openhds.hdsscapture.Dao.CommunityDao;
import org.openhds.hdsscapture.Dao.ConfigDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.Dao.FieldworkerDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.HierarchyDao;
import org.openhds.hdsscapture.Dao.HierarchyLevelDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.ListingDao;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.Dao.MorbidityDao;
import org.openhds.hdsscapture.Dao.OdkDao;
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
import org.openhds.hdsscapture.Dao.VpmDao;
import org.openhds.hdsscapture.Utilities.Converter;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.ApiUrl;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
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
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.odk.Form;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(  entities = {
        Relationship.class, Locations.class, Residency.class, Pregnancyoutcome.class, Individual.class, Round.class, Demographic.class,
        Visit.class, Outmigration.class, Death.class, Socialgroup.class, Pregnancy.class, CodeBook.class, Hierarchy.class,
        Fieldworker.class, Inmigration.class, HdssSociodemo.class, Outcome.class, Listing.class, Amendment.class, Vaccination.class, Duplicate.class,
        ApiUrl.class, Configsettings.class, Form.class, Vpm.class, CommunityReport.class, Morbidity.class, HierarchyLevel.class
        }, version = 4 , exportSchema = true)

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
    public abstract OdkDao odkDao();
    public abstract VpmDao vpmDao();
    public abstract CommunityDao communityDao();
    public abstract MorbidityDao morbidityDao();
    public abstract HierarchyLevelDao hierarchyLevelDao();

    //Migrate to another version when a variable is added to and entity.
    // If the version of the database is 2 you upgrade to 3 then set the MIGRATION_2_3 which means version 2 to 3
    //Then write the script to implement the change

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN pets INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN dogs INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN guinea_pigs INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN cats INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN fish INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN birds INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN rabbits INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN reptiles INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN pet_other INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN pet_other_spfy TEXT");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN pet_vac INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0001 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0002 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0003 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0004 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0005 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0006 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0006_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0007 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0007_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0008 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN id0008_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0009 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0009_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0010 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0010_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0011 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0011_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0012 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0012_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0013 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0013_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0014 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0014_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0015 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0015_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0016 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0016_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0017 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0017_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0018 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0018_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0019 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0019_1 INTEGER");
            database.execSQL("ALTER TABLE sociodemo ADD COLUMN  id0021 TEXT");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1001 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1002 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1003 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1004 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1005 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1006 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1007 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1008 INTEGER");
            database.execSQL("ALTER TABLE pregnancyoutcome ADD COLUMN id1009 INTEGER");
            database.execSQL("ALTER TABLE pregnancy ADD COLUMN preg_ready INTEGER");
            database.execSQL("ALTER TABLE pregnancy ADD COLUMN family_plan INTEGER");
            database.execSQL("ALTER TABLE pregnancy ADD COLUMN plan_method INTEGER");
            database.execSQL("ALTER TABLE pregnancy ADD COLUMN plan_method_oth TEXT");

            database.execSQL("CREATE TABLE IF NOT EXISTS hierarchylevel (" +
                    "uuid TEXT NOT NULL PRIMARY KEY, " +
                    "keyIdentifier INTEGER, " +
                    "name TEXT)");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE inmigration ADD COLUMN why_ext TEXT");
            database.execSQL("ALTER TABLE inmigration ADD COLUMN why_int TEXT");
            database.execSQL("ALTER TABLE inmigration ADD COLUMN how_lng INTEGER");

        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS morbidity (" +
                    "individual_uuid TEXT NOT NULL PRIMARY KEY, " +
                    "insertDate INTEGER, " +  // Date stored as INTEGER (timestamp)
                    "complete INTEGER, " +
                    "fw_uuid TEXT, " +
                    "uuid TEXT, " +
                    "location_uuid TEXT, " +
                    "socialgroup_uuid TEXT, " +
                    "ind_name TEXT, " +
                    "fever INTEGER, " +
                    "fever_days INTEGER, " +
                    "fever_treat INTEGER, " +
                    "hypertension INTEGER, " +
                    "hypertension_dur INTEGER, " +
                    "hypertension_trt INTEGER, " +
                    "diabetes INTEGER, " +
                    "diabetes_dur INTEGER, " +
                    "diabetes_trt INTEGER, " +
                    "heart INTEGER, " +
                    "heart_dur INTEGER, " +
                    "heart_trt INTEGER, " +
                    "stroke INTEGER, " +
                    "stroke_dur INTEGER, " +
                    "stroke_trt INTEGER, " +
                    "sickle INTEGER, " +
                    "sickle_dur INTEGER, " +
                    "sickle_trt INTEGER, " +
                    "asthma INTEGER, " +
                    "asthma_dur INTEGER, " +
                    "asthma_trt INTEGER, " +
                    "epilepsy INTEGER, " +
                    "epilepsy_dur INTEGER, " +
                    "epilepsy_trt INTEGER, " +
                    "comment TEXT, " +
                    "status INTEGER DEFAULT 0, " +
                    "supervisor TEXT, " +
                    "approveDate INTEGER, " +  // Date stored as INTEGER (timestamp)
                    "fw_name TEXT, " +
                    "compno TEXT)");

            // Create indices
            database.execSQL("CREATE INDEX index_morbidity_individual_uuid_fw_uuid_complete_socialgroup_uuid ON morbidity(individual_uuid, fw_uuid, complete, socialgroup_uuid)");
        }
    };

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "hdss")
                                .addCallback(sRoomDatabaseCallback)
                                .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4)
                                .fallbackToDestructiveMigrationOnDowngrade()
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
                odkDao().deleteAll();
                vpmDao().deleteAll();
                communityDao().deleteAll();
                morbidityDao().deleteAll();
                hierarchyLevelDao().deleteAll();
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

