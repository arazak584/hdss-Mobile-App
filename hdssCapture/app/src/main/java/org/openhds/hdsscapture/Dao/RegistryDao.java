package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Registry;

import java.util.Date;
import java.util.List;

@Dao
public interface RegistryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Registry registry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Registry... registry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Registry> registry);


    @Query("DELETE FROM registry")
    void deleteAll();

    @Query("SELECT * FROM registry")
    List<Registry> getAll();

    @Query("SELECT * FROM registry")
    List<Registry> retrieve();

    @Query("SELECT * FROM registry WHERE complete=1")
    List<Registry> retrieveSync();

//    @Query("SELECT * FROM registry WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Registry> retrieveSync();

    @Query("SELECT * FROM registry where individual_uuid=:id")
    Registry find(String id);

    @Query("SELECT * FROM registry where socialgroup_uuid=:id LIMIT 1")
    Registry finds(String id);

    @Query("SELECT COUNT(*) FROM registry WHERE socialgroup_uuid = :id")
    long count(String id);

    @Query("SELECT COUNT(*) FROM registry WHERE complete= 1")
    LiveData<Long> sync();

}
