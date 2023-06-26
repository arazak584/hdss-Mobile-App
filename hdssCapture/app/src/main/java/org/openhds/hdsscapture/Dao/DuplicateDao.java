package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Duplicate;

import java.util.List;

@Dao
public interface DuplicateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Duplicate duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Duplicate... duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Duplicate> duplicate);


    @Query("DELETE FROM duplicate")
    void deleteAll();

    @Query("SELECT * FROM duplicate")
    List<Duplicate> getAll();

    @Query("SELECT * FROM duplicate")
    List<Duplicate> retrieve();

    @Query("SELECT * FROM duplicate WHERE complete=1")
    List<Duplicate> retrieveSync();

    @Query("SELECT * FROM duplicate where individual_uuid=:id")
    Duplicate find(String id);

}
