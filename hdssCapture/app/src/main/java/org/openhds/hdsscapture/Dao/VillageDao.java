package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Village;

import java.util.List;

@Dao
public interface VillageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Village... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Village data);

    @Update
    void update(Village data);

    @Delete
    void delete(Village data);

    @Query("SELECT * FROM village WHERE extId=:id")
    Village retrieve(String id);

    @Query("SELECT * FROM village WHERE parent_uuid=:id order by name")
    List<Village> retrieveBySubdistrictId(String id);

    @Query("SELECT * FROM village")
    List<Village> retrieve();
}
