package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.ServerQueries;

import java.util.List;

@Dao
public interface QueriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(ServerQueries... data);

    @Query("SELECT * FROM serverqueries")
    List<ServerQueries> retrieve();

    @Query("DELETE FROM serverqueries")
    void deleteAll();

    @Query("SELECT * FROM serverqueries WHERE fw=:username")
    List<ServerQueries> findByFw(String username);

}
