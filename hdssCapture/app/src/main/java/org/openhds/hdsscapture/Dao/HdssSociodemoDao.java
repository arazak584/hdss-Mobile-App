package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.HdssSociodemo;

import java.util.List;

@Dao
public interface HdssSociodemoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HdssSociodemo... s);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HdssSociodemo s);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<HdssSociodemo> hdssSociodemos);

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

    @Query("SELECT * FROM sociodemo")
    List<HdssSociodemo> retrieveAll();
}
