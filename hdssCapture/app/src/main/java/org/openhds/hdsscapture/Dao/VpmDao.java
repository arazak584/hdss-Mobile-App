package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.VpmUpdate;

import java.util.List;

@Dao
public interface VpmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Vpm vpm);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Vpm... vpm);

    @Update(entity = Vpm.class)
    int update(VpmUpdate s);

    @Query("DELETE FROM vpm")
    void deleteAll();

    @Query("SELECT * FROM vpm WHERE complete!=0 AND deathDate IS NOT NULL")
    List<Vpm> retrieveToSync();

    @Query("SELECT * FROM vpm where individual_uuid=:id")
    Vpm find(String id);

    @Query("SELECT * FROM vpm where extId=:id")
    Vpm finds(String id);

}
