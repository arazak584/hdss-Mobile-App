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

    @Query("SELECT COUNT(*) FROM visit WHERE insertDate BETWEEN :startDate AND :endDate")
    long countVisits(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM visit WHERE insertDate BETWEEN :startDate AND :endDate group by location_uuid ")
    long countLocs(Date startDate, Date endDate);
}
