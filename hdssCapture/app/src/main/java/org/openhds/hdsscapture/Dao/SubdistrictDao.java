package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Subdistrict;

import java.util.List;

@Dao
public interface SubdistrictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Subdistrict... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Subdistrict data);

    @Update
    void update(Subdistrict data);

    @Delete
    void delete(Subdistrict data);

    @Query("SELECT * FROM subdistrict WHERE extId=:id")
    Subdistrict retrieve(String id);

    @Query("SELECT * FROM subdistrict WHERE parent_uuid=:id order by name")
    List<Subdistrict> retrieveByDistrictId(String id);

    @Query("SELECT * FROM subdistrict")
    List<Subdistrict> retrieve();
}
