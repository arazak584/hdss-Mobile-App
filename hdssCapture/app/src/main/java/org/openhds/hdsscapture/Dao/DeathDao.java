package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Death;

import java.util.Date;
import java.util.List;

@Dao
public interface DeathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Death death);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Death... death);

    @Query("DELETE FROM death")
    void deleteAll();

    @Query("SELECT * FROM death")
    List<Death> getAll();

    @Query("SELECT * FROM death")
    List<Death> retrieve();

    @Query("SELECT * FROM death where individual_uuid=:id")
    Death find(String id);

    @Query("SELECT * FROM death WHERE complete=1")
    List<Death> retrieveToSync();

    @Query("SELECT * FROM death WHERE vpmcomplete=1")
    List<Death> retrieveVpmSync();

    @Query("SELECT COUNT(*) FROM death a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
