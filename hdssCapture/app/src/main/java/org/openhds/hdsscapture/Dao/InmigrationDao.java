package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Inmigration;

import java.util.List;

@Dao
public interface InmigrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Inmigration inmigration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Inmigration... inmigration);

    @Query("SELECT * FROM inmigration")
    List<Inmigration> getAll();

    @Query("SELECT * FROM inmigration")
    List<Inmigration> retrieve();

    @Query("SELECT * FROM inmigration WHERE complete=1")
    List<Inmigration> retrieveimgToSync();

    @Query("SELECT * FROM inmigration where individual_uuid=:id")
    Inmigration find(String id);
}
