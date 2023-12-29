package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Round;

import java.util.List;

@Dao
public interface RoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Round round);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Round... round);

    @Query("DELETE FROM round")
    void deleteAll();

    @Query("SELECT * FROM round WHERE endDate>insertDate")
    List<Round> retrieve();

}
