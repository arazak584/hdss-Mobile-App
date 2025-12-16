package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;

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

    @Query("SELECT * FROM visit where socialgroup_uuid=:id")
    Visit find(String id);

    @Query("SELECT * FROM visit WHERE complete=1")
    List<Visit> retrieveToSync();

//    @Query("SELECT * FROM visit WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Visit> retrieveToSync();
    //1746057600000

    @Query("SELECT COUNT(*) FROM visit a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long countVisits(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM visit WHERE socialgroup_uuid = :id")
    long count(String id);

    @Query("SELECT COUNT(*) FROM visit WHERE complete= 1")
    LiveData<Long> sync();




}
