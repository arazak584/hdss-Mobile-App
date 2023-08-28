package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.ApiUrl;

@Dao
public interface ApiUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (ApiUrl apiUrl);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(ApiUrl... apiUrl);

    @Query("SELECT * FROM apiurl limit 1")
    ApiUrl retrieve();

}
