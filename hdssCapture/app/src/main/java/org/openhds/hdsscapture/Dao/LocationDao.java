package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Location... location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Location location);

    @Update
    void update(Location location);

    @Update(entity = Location.class)
    int update(LocationAmendment locationAmendment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Location> locations);

    @Delete
    void delete(Location location);

    @Query("SELECT * FROM location WHERE compno=:id")
    Location retrieve(String id);

    @Query("SELECT * FROM location ")
    List<Location> retrieve();

    @Query("SELECT * FROM location WHERE complete=1")
    List<Location> retrieveToSync();

    @Query("SELECT * FROM location WHERE clusterId=:id order by extId")
    List<Location> retrieveByClusterId(String id);

    @Query("SELECT * FROM location WHERE compno LIKE:id OR locationName LIKE:id OR location.extid LIKE:id OR clusterId LIKE:id order by compno")
    List<Location> retrieveBySearch(String id);

}
