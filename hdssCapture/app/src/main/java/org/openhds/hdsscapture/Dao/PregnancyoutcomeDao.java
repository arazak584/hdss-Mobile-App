package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.List;

@Dao
public interface
PregnancyoutcomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Pregnancyoutcome pregnancyoutcome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancyoutcome... pregnancyoutcome);

    @Query("SELECT * FROM outcome ")
    List<Pregnancyoutcome> getAll();

    @Query("SELECT * FROM outcome")
    List<Pregnancyoutcome> retrieve();

    @Query("SELECT * FROM outcome WHERE complete=1")
    List<Pregnancyoutcome> retrieveToSync();
}
