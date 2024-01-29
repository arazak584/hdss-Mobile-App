package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Vpm;

import java.util.Date;
import java.util.List;

@Dao
public interface VpmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Vpm vpm);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Vpm... vpm);

    @Query("DELETE FROM vpm")
    void deleteAll();

    @Query("SELECT * FROM vpm WHERE complete!=0")
    List<Vpm> retrieveToSync();

    @Query("SELECT * FROM vpm where individual_uuid=:id")
    Vpm find(String id);

}
