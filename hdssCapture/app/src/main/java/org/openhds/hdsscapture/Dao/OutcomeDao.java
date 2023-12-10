package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Outcome;

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

//    @Query("SELECT a.uuid,c.extId as extId,e.compextId as childuuid,c.firstName,c.lastName FROM outcome as a left join pregnancyoutcome as b on a.preg_uuid=b.uuid " +
//            " INNER JOIN individual as c on a.mother_uuid=c.uuid INNER JOIN residency as d on c.uuid=d.individual_uuid " +
//            " INNER JOIN locations as e on d.location_uuid=e.uuid " +
//            "WHERE b.uuid is NULL and c.endType=1 ")
//    List<Outcome> error();

    @Query("SELECT a.uuid,c.extId as extId,c.compno as childuuid,c.firstName,c.lastName FROM outcome as a left join pregnancyoutcome as b on a.preg_uuid=b.uuid " +
            " INNER JOIN individual as c on a.mother_uuid=c.uuid " +
            "WHERE b.uuid is NULL and endType=1 ")
    List<Outcome> error();

    @Query("SELECT * FROM outcome where uuid=:id")
    Outcome find(String id);
}
