package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Country;

import java.util.List;

@Dao
public interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Country... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Country data);

    @Update
    void update(Country data);

    @Delete
    void delete(Country data);

    @Query("SELECT * FROM Country")
    List<Country> retrieve();
}
