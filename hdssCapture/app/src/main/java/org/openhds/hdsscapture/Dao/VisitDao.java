package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Visit;

import java.util.List;

@Dao
public interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Visit... visit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Visit visit);


    @Query("SELECT * FROM visit")
    List<Visit> getAll();

    @Query("SELECT * FROM visit where houseExtId=:id")
    Visit find(String id);


    @Query("SELECT * FROM visit")
    List<Visit> retrieve();

    @Query("SELECT * FROM visit WHERE complete=1")
    List<Visit> retrieveToSync();
}
