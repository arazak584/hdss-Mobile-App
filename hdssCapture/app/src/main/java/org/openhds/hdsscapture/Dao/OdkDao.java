package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;

@Dao
public interface OdkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(OdkForm... data);

    @Query("SELECT * FROM Form")
    List<OdkForm> retrieve();

    @Query("DELETE FROM Form")
    void deleteAll();

    @Query("SELECT * FROM Form where enabled = 1")
    List<OdkForm> find();

}
