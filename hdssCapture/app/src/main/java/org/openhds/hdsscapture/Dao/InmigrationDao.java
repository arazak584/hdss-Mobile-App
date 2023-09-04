package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Inmigration;

import java.util.Date;
import java.util.List;

@Dao
public interface InmigrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Inmigration inmigration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Inmigration... inmigration);

    @Query("DELETE FROM inmigration")
    void deleteAll();

    @Query("SELECT * FROM inmigration WHERE complete=1")
    List<Inmigration> retrieveimgToSync();

    @Query("SELECT * FROM inmigration where individual_uuid=:id ORDER BY recordedDate DESC LIMIT 1")
    Inmigration find(String id);

    @Query("SELECT COUNT(*) FROM inmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
