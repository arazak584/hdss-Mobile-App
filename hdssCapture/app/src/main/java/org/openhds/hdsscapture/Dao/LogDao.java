package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.LogBook;

import java.util.Date;
import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (LogBook logBook);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(LogBook... logBook);

    @Query("DELETE FROM LogBook")
    void deleteAll();

    @Query("SELECT * FROM LogBook WHERE complete=1")
    List<LogBook> retrieveToSync();

}
