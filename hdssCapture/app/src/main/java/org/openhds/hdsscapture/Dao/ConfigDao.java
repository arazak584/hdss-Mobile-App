package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Configsettings;

import java.util.List;

@Dao
public interface ConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Configsettings configsettings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Configsettings... configsettings);

    @Query("DELETE FROM config")
    void deleteAll();

    @Query("SELECT * FROM config")
    List<Configsettings> retrieve();

}
