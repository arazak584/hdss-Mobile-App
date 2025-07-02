package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Vaccination;

import java.util.Date;
import java.util.List;

@Dao
public interface VaccinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Vaccination vaccination);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Vaccination... vaccination);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Vaccination> vaccination);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vaccination... vaccination);


    @Query("DELETE FROM vaccination")
    void deleteAll();

    @Query("SELECT * FROM vaccination WHERE complete=1")
    List<Vaccination> retrieveSync();

    @Query("SELECT * FROM vaccination where individual_uuid=:id")
    Vaccination find(String id);

    @Query("SELECT * FROM vaccination where uuid=:id AND complete!=1 ")
    Vaccination ins(String id);

    @Query("SELECT COUNT(*) FROM vaccination a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.individual_uuid,b.firstName as sttime,b.lastName as edtime,b.extId as socialgroup_uuid,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM vaccination a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Vaccination> reject(String id);

    @Query("SELECT COUNT(*) FROM vaccination WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);
}
