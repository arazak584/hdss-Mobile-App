package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Inmigration;
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

    @Query("SELECT * FROM vaccination WHERE uuid IN (:uuids) AND complete!=1")
    List<Vaccination> getByUuids(List<String> uuids);

    @Query("SELECT * FROM vaccination where individual_uuid=:id")
    Vaccination find(String id);

    @Query("SELECT * FROM vaccination where uuid=:id AND complete!=1 ")
    Vaccination ins(String id);

    @Query("SELECT COUNT(*) FROM vaccination a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.individual_uuid,b.firstName as sttime,b.lastName as edtime,b.extId as socialgroup_uuid,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM vaccination a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 " +
            " AND a.insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1) order by a.insertDate DESC")
    List<Vaccination> reject(String id);

    @Query("SELECT COUNT(*) FROM vaccination WHERE status=2 AND fw_uuid = :uuid AND insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1)")
    long rej(String uuid);

    @Query("SELECT a.uuid, 'Vaccination' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM vaccination as a inner join individual as b ON a.individual_uuid=b.uuid WHERE a.complete = 1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM vaccination where uuid=:id")
    LiveData<Vaccination> getView(String id);

    @Query("SELECT COUNT(*) FROM vaccination WHERE complete= 1")
    LiveData<Long> sync();
}
