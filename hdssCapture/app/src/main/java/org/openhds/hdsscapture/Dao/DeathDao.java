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


    @Query("SELECT * FROM death where individual_uuid=:id")
    Death find(String id);

    @Query("SELECT * FROM death WHERE complete=1")
    List<Death> retrieveToSync();

    @Query("SELECT * FROM death WHERE vpmcomplete=1 AND deathDate IS NOT NULL")
    List<Death> retrieveVpmSync();

    @Query("SELECT a.*,groupName as firstName,b.extId as lastName FROM death as a INNER JOIN socialgroup as b ON a.individual_uuid=b.individual_uuid " +
            " INNER JOIN residency as c on b.uuid=c.socialgroup_uuid " +
            " INNER JOIN individual as d on c.individual_uuid=d.uuid " +
            "where c.endType=1 and a.firstName!='Still' AND d.firstName!='FAKE' GROUP BY a.individual_uuid")
    List<Death> error();

    @Query("SELECT COUNT(*) FROM death a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
