package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.subentity.PregnancyobsAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface PregnancyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Pregnancy pregnancy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancy... pregnancy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (List<Pregnancy> pregnancies);

    @Query("DELETE FROM pregnancy")
    void deleteAll();

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

    @Query("SELECT * FROM pregnancy where individual_uuid=:id ORDER BY recordedDate ASC LIMIT 1")
    Pregnancy find(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id AND extra=1")
    Pregnancy finds(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id ORDER BY recordedDate DESC LIMIT 1")
    Pregnancy findpreg(String id);

    @Query("SELECT a.*,d.firstName,d.lastName FROM pregnancy as a " + "INNER JOIN residency as b ON a.individual_uuid = b.individual_uuid " +
            " INNER JOIN individual as d on a.individual_uuid=d.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.location_uuid " +
            " WHERE b.endType=1 and outcome=2 and c.compextId=:id ")
    List<Pregnancy> retrievePregnancy(String id);

    @Query("SELECT COUNT(*) FROM pregnancy a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
