package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.List;

@Dao
public interface
OutcomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Outcome outcome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Outcome... outcome);

    @Query("DELETE FROM outcome")
    void deleteAll();

    @Query("SELECT * FROM outcome WHERE complete=1")
    List<Outcome> retrieveToSync();
//    @Query("SELECT a.uuid,c.extId as extId,c.compno as childuuid,c.firstName,c.lastName FROM outcome as a left join pregnancyoutcome as b on a.preg_uuid=b.uuid " +
//            " INNER JOIN individual as c on a.mother_uuid=c.uuid INNER JOIN visit d on a.location=d.location_uuid " +
//            "WHERE b.uuid is NULL and endType=1 AND d.fw_uuid= :id ")
//    List<Outcome> error(String id);
//
//    @Query("SELECT COUNT(*) FROM outcome as a left join pregnancyoutcome as b on a.preg_uuid=b.uuid " +
//            " INNER JOIN individual as c on a.mother_uuid=c.uuid INNER JOIN visit d on a.location=d.location_uuid " +
//            "WHERE b.uuid is NULL and endType=1 AND d.fw_uuid= :id ")
//    long cnt(String id);

    @Query("SELECT * FROM outcome where preg_uuid=:id AND outcomeNumber = :outcomeNumber LIMIT 1")
    LiveData<Outcome> getView(String id, int outcomeNumber);

    @Query("SELECT * FROM outcome where childuuid=:id")
    Outcome getUuid(String id);

    @Query("SELECT COUNT(*) FROM outcome WHERE complete= 1")
    LiveData<Long> sync();

    @Query("SELECT * FROM outcome WHERE preg_uuid = :outcomeId AND outcomeNumber = :outcomeNumber LIMIT 1")
    Outcome getByOutcomeIdAndNumber(String outcomeId, int outcomeNumber);
}
