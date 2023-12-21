package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Locations;

import java.util.List;

@Dao
public interface DuplicateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Duplicate duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Duplicate... duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Duplicate> duplicate);


    @Query("DELETE FROM duplicate")
    void deleteAll();

    @Query("SELECT * FROM duplicate WHERE complete=1")
    List<Duplicate> retrieveSync();

    @Query("SELECT * FROM duplicate where individual_uuid=:id limit 1")
    Duplicate find(String id);

    @Query("SELECT a.*,b.compno as dup_uuid,b.hohID as dup1_uuid FROM duplicate a inner join individual as b on a.individual_uuid=b.uuid ")
    List<Duplicate> repo();

}
