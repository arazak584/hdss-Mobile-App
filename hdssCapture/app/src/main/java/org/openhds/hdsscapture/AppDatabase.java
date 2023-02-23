package org.openhds.hdsscapture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.openhds.hdsscapture.Dao.ClusterDao;
import org.openhds.hdsscapture.Dao.CodeBookDao;
import org.openhds.hdsscapture.Dao.CountryDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.DistrictDao;
import org.openhds.hdsscapture.Dao.FieldworkerDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.Dao.RegionDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.Dao.RoundDao;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.Dao.SubdistrictDao;
import org.openhds.hdsscapture.Dao.VillageDao;
import org.openhds.hdsscapture.Dao.VisitDao;
import org.openhds.hdsscapture.Utilities.Converter;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Country;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.District;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Region;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Subdistrict;
import org.openhds.hdsscapture.entity.Village;
import org.openhds.hdsscapture.entity.Visit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(  entities = {
        Relationship.class, Location.class, Residency.class, Pregnancyoutcome.class, Individual.class, Round.class, Demographic.class,
        Visit.class, Outmigration.class, Death.class, Socialgroup.class, Pregnancy.class, CodeBook.class,
        Region.class, Country.class, District.class, Subdistrict.class, Village.class, Cluster.class, Fieldworker.class, Inmigration.class
},         version = 6, exportSchema = true)

@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract IndividualDao individualDao();
    public abstract CodeBookDao codeBookDao();
    public abstract CountryDao countryDao();
    public abstract RegionDao regionDao();
    public abstract DistrictDao districtDao();
    public abstract SubdistrictDao subdistrictDao();
    public abstract VillageDao villageDao();
    public abstract ClusterDao clusterDao();
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

    private static AppDatabase instance;

    private static final int NUMBER_OF_THREADS = 6;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "hdss")
                                .addCallback(sRoomDatabaseCallback)
                                .fallbackToDestructiveMigration()
                                .build();
            }
        }
        //Return Database
        return instance;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}

