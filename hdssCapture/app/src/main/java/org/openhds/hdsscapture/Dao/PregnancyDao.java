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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert (List<Pregnancy> pregnancies);

    @Query("DELETE FROM pregnancy")
    void deleteAll();

    @Update
    void update(Pregnancy pregnancy);

    @Update(entity = Pregnancy.class)
    int update (PregnancyobsAmendment pregnancyobsAmendment);

    @Query("SELECT * FROM pregnancy WHERE complete=1")
    List<Pregnancy> retrieveToSync();

    @Query("SELECT * FROM pregnancy where individual_uuid=:id AND outcome IS NOT NULL ORDER BY recordedDate ASC LIMIT 1")
    Pregnancy find(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id AND outcome=1 ORDER BY recordedDate ASC LIMIT 1")
    Pregnancy out(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id ORDER BY recordedDate ASC LIMIT 1")
    Pregnancy lastpreg(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id AND id=2")
    Pregnancy finds(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id AND outcome=1 AND id=2")
    Pregnancy out2(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id and extra=1")
    Pregnancy findss(String id);

    @Query("SELECT * FROM pregnancy where individual_uuid=:id ORDER BY recordedDate DESC LIMIT 1")
    Pregnancy findpreg(String id);

    @Query("SELECT a.*,d.firstName,d.lastName FROM pregnancy as a " + "INNER JOIN residency as b ON a.individual_uuid = b.individual_uuid " +
            " INNER JOIN individual as d on a.individual_uuid=d.uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and outcome=2 and c.compextId=:id ")
    List<Pregnancy> retrievePregnancy(String id);

    @Query("SELECT COUNT(*) FROM pregnancy a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
