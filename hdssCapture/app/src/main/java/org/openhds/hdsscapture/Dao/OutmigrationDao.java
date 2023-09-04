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

    @Query("SELECT * FROM residency as a LEFT JOIN outmigration as b on a.uuid=b.residency_uuid" +
            " where b.residency_uuid IS NULL AND a.individual_uuid=:id and location_uuid!=:locid and endType=1")
    Outmigration createOmg(String id, String locid);

    @Query("SELECT * FROM outmigration WHERE complete=1")
    List<Outmigration> retrieveomgToSync();

    @Query("SELECT * FROM outmigration where individual_uuid=:id ORDER BY recordedDate DESC LIMIT 1")
    Outmigration find(String id);

    @Query("SELECT COUNT(*) FROM outmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
