package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Region;

import java.util.List;

@Dao
public interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Region... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Region data);

    @Update
    void update(Region data);

    @Delete
    void delete(Region data);

    @Query("SELECT * FROM region WHERE extId=:id")
    Region retrieve(String id);

    @Query("SELECT * FROM region WHERE parent_uuid=:id")
    List<Region> retrieveByCountryId(String id);


    @Query("SELECT * FROM region")
    List<Region> retrieve();
}
