package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Vaccination;

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

    @Query("SELECT * FROM sociodemo WHERE complete=1")
    List<HdssSociodemo> retrieveToSync();

    @Query("SELECT COUNT(*) FROM sociodemo a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
