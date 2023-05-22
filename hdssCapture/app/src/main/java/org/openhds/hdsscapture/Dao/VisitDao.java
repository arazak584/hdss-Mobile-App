package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Visit;

import java.util.Date;
import java.util.List;

@Dao
public interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Visit... visit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Visit visit);

    @Query("DELETE FROM visit")
    void deleteAll();

    @Update
    void update(Visit visit);


    @Query("SELECT * FROM visit")
    List<Visit> getAll();

    @Query("SELECT * FROM visit where socialgroup_uuid=:id")
    Visit find(String id);


    @Query("SELECT * FROM visit")
    List<Visit> retrieve();

    @Query("SELECT * FROM visit WHERE complete=1")
    List<Visit> retrieveToSync();

    @Query("SELECT COUNT(*) FROM visit a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long countVisits(Date startDate, Date endDate, String username);


    @Query("SELECT COUNT(*) FROM visit a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username GROUP BY a.location_uuid")
    long countLocs(Date startDate, Date endDate, String username);

}
