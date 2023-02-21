package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Cluster;

import java.util.List;

@Dao
public interface ClusterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Cluster... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Cluster data);

    @Update
    void update(Cluster data);

    @Delete
    void delete(Cluster data);

    @Query("SELECT * FROM cluster WHERE extId=:id")
    Cluster retrieve(String id);

    @Query("SELECT * FROM cluster WHERE villageId=:id order by name")
    List<Cluster> retrieveByVillageId(String id);

    @Query("SELECT * FROM cluster")
    List<Cluster> retrieve();
}
