package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;

import java.util.Date;
import java.util.List;

@Dao
public interface HdssSociodemoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HdssSociodemo... s);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HdssSociodemo s);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<HdssSociodemo> hdssSociodemos);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HdssSociodemo... hdssSociodemos);

    @Query("DELETE FROM sociodemo")
    void deleteAll();

    @Update
    void update(HdssSociodemo s);

    @Delete
    void delete(HdssSociodemo s);

    @Query("SELECT * FROM sociodemo WHERE socialgroup_uuid=:id")
    List<HdssSociodemo> retrieve(String id);

    @Query("SELECT * FROM sociodemo WHERE socialgroup_uuid=:id")
   HdssSociodemo findses(String id);

    @Query("SELECT * FROM sociodemo where uuid=:id AND complete!=1 ")
    HdssSociodemo ins(String id);

    @Query("SELECT * FROM sociodemo WHERE complete=1")
    List<HdssSociodemo> retrieveToSync();

//    @Query("SELECT * FROM sociodemo WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<HdssSociodemo> retrieveToSync();

    @Query("SELECT a.socialgroup_uuid,b.extId as sttime,b.groupName as edtime,c.compno as visit_uuid" +
            ",a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM sociodemo a INNER JOIN socialgroup b on a.socialgroup_uuid=b.uuid " +
            "INNER JOIN locations c on a.location_uuid=c.uuid " +
            "WHERE a.fw_uuid=:id AND a.status=2 order by a.insertDate DESC")
    List<HdssSociodemo> reject(String id);

    @Query("SELECT COUNT(*) FROM sociodemo a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM sociodemo a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE formcompldate BETWEEN :startDate AND :endDate AND b.username = :username AND insertDate <  (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1) AND" +
            " formcompldate >= (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1)")
    long counts(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM sociodemo WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT a.*,groupName as visit_uuid,b.extId as form_comments_txt,c.compno as id0021 FROM sociodemo as a INNER JOIN socialgroup as b ON a.socialgroup_uuid=b.uuid " +
            " INNER JOIN locations as c on a.location_uuid=c.uuid " +
            " where a.form_comments_yn IS NULL GROUP BY a.socialgroup_uuid")
    List<HdssSociodemo> error();

    @Query("SELECT COUNT(*) FROM sociodemo as a INNER JOIN socialgroup as b ON a.socialgroup_uuid=b.uuid " +
            " INNER JOIN locations as c on a.location_uuid=c.uuid " +
            " where a.form_comments_yn IS NULL GROUP BY a.socialgroup_uuid")
    long cnt();

    @Query("SELECT a.uuid, 'SES' AS formType, a.insertDate, extId || ' - ' || groupName as fullName FROM sociodemo as a inner join socialgroup as b ON a.socialgroup_uuid=b.uuid WHERE a.complete = 1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();
    @Query("SELECT * FROM sociodemo where uuid=:id")
    LiveData<HdssSociodemo> getView(String id);
}
