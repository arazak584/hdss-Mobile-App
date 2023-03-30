package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.subentity.PregnancyobsAmendment;

import java.util.List;

@Dao
public interface PregnancyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Pregnancy pregnancy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancy... pregnancy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (List<Pregnancy> pregnancies);

    @Update
    void update(Pregnancy pregnancy);

    @Update(entity = Pregnancy.class)
    int update (PregnancyobsAmendment pregnancyobsAmendment);

    @Query("SELECT * FROM Pregnancy")
    List<Pregnancy> getAll();

    @Query("SELECT * FROM Pregnancy")
    List<Pregnancy> retrieve();

    @Query("SELECT * FROM pregnancy WHERE complete=1")
    List<Pregnancy> retrieveToSync();

    @Query("SELECT * FROM pregnancy where individual_uuid=:id")
    Pregnancy find(String id);

    @Query("SELECT a.* FROM pregnancy as a " + "INNER JOIN residency as b ON a.individual_uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.location_uuid " +
            " WHERE endType=1 and outcome=2 and c.compextId=:id ")
    List<Pregnancy> retrievePregnancy(String id);
}
