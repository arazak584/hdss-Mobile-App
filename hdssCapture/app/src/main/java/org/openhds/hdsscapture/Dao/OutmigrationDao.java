package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Death;
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
            " where b.residency_uuid IS NULL AND a.individual_uuid=:id and a.location_uuid!=:locid and endType=1")
    Outmigration createOmg(String id, String locid);

    @Query("SELECT * FROM outmigration WHERE complete!=0")
    List<Outmigration> retrieveomgToSync();

    @Query("SELECT * FROM outmigration WHERE individual_uuid=:id AND location_uuid=:locid")
    Outmigration find(String id,String locid);

    @Query("SELECT * FROM outmigration WHERE individual_uuid=:id AND location_uuid=:locid AND edit IS NULL")
    Outmigration edit(String id,String locid);

    @Query("SELECT * FROM outmigration WHERE individual_uuid=:id")
    Outmigration finds(String id);

    @Query("SELECT COUNT(*) FROM outmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE date(insertDate/1000,'unixepoch') BETWEEN date(:startDate/1000,'unixepoch') AND date(:endDate/1000,'unixepoch') AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.uuid,a.insertDate,a.residency_uuid,b.firstName as visit_uuid,b.lastName as location_uuid FROM outmigration as a inner join individual as b on a.individual_uuid=b.uuid WHERE socialgroup_uuid=:id")
    List<Outmigration> end(String id);
}
