package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Fieldworker;

import java.util.List;

@Dao
public interface FieldworkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Fieldworker... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Fieldworker data);

    @Query("DELETE FROM fieldworker")
    void deleteAll();

    @Update
    void update(Fieldworker data);

    @Delete
    void delete(Fieldworker data);

    @Query("SELECT * FROM fieldworker WHERE username=:id AND password=:password")
    Fieldworker retrieve(String id, String password);

    @Query("SELECT * FROM fieldworker WHERE username=:id")
    Fieldworker retrieves(String id);
}
