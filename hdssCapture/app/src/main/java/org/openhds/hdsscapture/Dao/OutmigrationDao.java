package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Outmigration;

import java.util.Date;
import java.util.List;

@Dao
public interface OutmigrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Outmigration outmigration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Outmigration... outmigration);

    @Query("DELETE FROM outmigration")
    void deleteAll();

    @Query("SELECT * FROM outmigration")
    List<Outmigration> getAll();

    @Query("SELECT * FROM outmigration")
    List<Outmigration> retrieve();

    @Query("SELECT * FROM outmigration WHERE complete=1")
    List<Outmigration> retrieveomgToSync();

    @Query("SELECT * FROM outmigration where individual_uuid=:id ORDER BY recordedDate DESC LIMIT 1")
    Outmigration find(String id);

    @Query("SELECT COUNT(*) FROM outmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
