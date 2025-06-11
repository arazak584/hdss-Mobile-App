package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;

@Dao
public interface MorbidityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Morbidity... s);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Morbidity s);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Morbidity> morbidities);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Morbidity... morbidities);

    @Query("DELETE FROM morbidity")
    void deleteAll();

    @Update
    void update(Morbidity s);

    @Delete
    void delete(Morbidity s);

    @Query("SELECT * FROM morbidity WHERE socialgroup_uuid=:id")
    List<Morbidity> retrieve(String id);

    @Query("SELECT * FROM morbidity WHERE individual_uuid=:id")
    Morbidity find(String id);

    @Query("SELECT * FROM morbidity where uuid=:id ")
    Morbidity ins(String id);

//    @Query("SELECT * FROM morbidity WHERE complete=1")
//    List<Morbidity> retrieveToSync();

    @Query("SELECT * FROM morbidity WHERE insertDate > 1748121600000")
    List<Morbidity> retrieveToSync();

    @Query("SELECT a.individual_uuid,b.extId as fw_name,ind_name,a.compno" +
            ",a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM morbidity a INNER JOIN individual b on a.individual_uuid=b.uuid " +
            "WHERE a.fw_uuid=:id AND a.status=2 order by a.insertDate DESC")
    List<Morbidity> reject(String id);

    @Query("SELECT COUNT(*) FROM morbidity a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM morbidity a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND insertDate <  (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1) AND" +
            " insertDate >= (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1)")
    long counts(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM morbidity WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);
}
