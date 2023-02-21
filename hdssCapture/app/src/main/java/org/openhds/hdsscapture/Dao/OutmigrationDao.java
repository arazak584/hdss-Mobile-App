package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Outmigration;

import java.util.List;

@Dao
public interface OutmigrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Outmigration outmigration);


    @Query("SELECT * FROM outmigration")
    List<Outmigration> getAll();

    @Query("SELECT * FROM outmigration")
    List<Outmigration> retrieve();
}
