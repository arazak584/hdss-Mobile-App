package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.District;

import java.util.List;

@Dao
public interface DistrictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(District... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(District data);

    @Update
    void update(District data);

    @Delete
    void delete(District data);

    @Query("SELECT * FROM district WHERE extId=:id")
    District retrieve(String id);

    @Query("SELECT * FROM district WHERE parent_uuid=:id")
    List<District> retrieveByRegionId(String id);

    @Query("SELECT * FROM district")
    List<District> retrieve();
}
