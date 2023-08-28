package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    @Query("SELECT * FROM outcome ")
    List<Outcome> getAll();

    @Query("SELECT * FROM outcome")
    List<Outcome> retrieve();

    @Query("SELECT * FROM outcome as a inner join pregnancyoutcome as b on a.preg_uuid=b.uuid WHERE a.complete=1")
    List<Outcome> retrieveToSync();

    @Query("SELECT * FROM outcome where uuid=:id")
    Outcome find(String id);
}
