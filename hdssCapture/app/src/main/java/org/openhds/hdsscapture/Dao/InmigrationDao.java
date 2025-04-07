package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Socialgroup;

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

    @Query("SELECT * FROM inmigration where individual_uuid=:id and location_uuid=:locid ORDER BY recordedDate DESC LIMIT 1")
    Inmigration find(String id, String locid);

    @Query("SELECT COUNT(*) FROM inmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.residency_uuid,b.firstName as sttime,b.lastName as edtime,b.extId as reason_oth,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM inmigration a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Inmigration> reject(String id);

    @Query("SELECT COUNT(*) FROM inmigration WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT * FROM inmigration where uuid=:id")
    Inmigration ins(String id);
}
