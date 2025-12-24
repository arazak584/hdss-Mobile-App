package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Vpm;

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

    @Query("SELECT * FROM death where individual_uuid=:id")
    Death finds(String id);

    @Query("SELECT * FROM death where individual_uuid=:id")
    Death retrieve(String id);

//    @Query("SELECT a.uuid,a.individual_uuid,a.insertDate,deathDate,respondent,compname,villname,villcode," +
//            "b.dob,b.firstName,b.lastName,b.gender,b.compno,a.visit_uuid,a.residency_uuid,a.socialgroup_uuid,comment,status " +
//            "deathCause,deathCause_oth,deathPlace,deathPlace_oth,a.fw_uuid,a.edit,supervisor,approveDate FROM death a INNER JOIN individual b on a.individual_uuid=b.uuid where individual_uuid=:id")
//    Death retrieve(String id);

    @Query("SELECT * FROM death WHERE complete!=0")
    List<Death> retrieveToSync();

//    @Query("SELECT * FROM death WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Death> retrieveToSync();

    @Query("SELECT a.*,groupName as firstName,b.extId as lastName,d.compno as compno FROM death as a INNER JOIN socialgroup as b ON a.individual_uuid=b.individual_uuid " +
            " INNER JOIN residency as c on b.uuid=c.socialgroup_uuid " +
            " INNER JOIN individual as d on c.individual_uuid=d.uuid " +
            "where c.endType=1 and a.firstName!='Still' AND d.firstName!='FAKE' AND a.edit=1 AND a.fw_uuid =:id GROUP BY a.individual_uuid")
    List<Death> error(String id);

    @Query("SELECT COUNT(*) FROM death a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND a.edit!=2")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT * FROM death WHERE insertDate > (SELECT startDate FROM round LIMIT 1) AND complete IS NOT NULL order by insertDate DESC")
    List<Death> repo();

    @Query("SELECT * FROM death WHERE socialgroup_uuid=:id")
    List<Death> end(String id);

    @Query("SELECT a.uuid,b.firstName as sttime,b.lastName as edtime,b.extId as deathPlace_oth,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM death a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Death> reject(String id);

    @Query("SELECT COUNT(*) FROM death WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT COUNT(*) FROM death as a INNER JOIN socialgroup as b ON a.individual_uuid=b.individual_uuid " +
            " INNER JOIN residency as c on b.uuid=c.socialgroup_uuid " +
            " INNER JOIN individual as d on c.individual_uuid=d.uuid " +
            "where c.endType=1 and a.firstName!='Still' AND d.firstName!='FAKE' AND a.edit!=2 GROUP BY a.individual_uuid")
    long cnt();

    @Query("SELECT COUNT(DISTINCT d.uuid) FROM death as a INNER JOIN socialgroup as b ON a.individual_uuid=b.individual_uuid " +
            " INNER JOIN residency as c on b.uuid=c.socialgroup_uuid " +
            " INNER JOIN individual as d on c.individual_uuid=d.uuid " +
            "where c.endType=1 and a.firstName!='Still' AND d.firstName!='FAKE' AND a.edit!=2 AND d.hohID = :id AND d.compno = :ids ")
    long err(String id, String ids);

    @Query("SELECT * FROM death where uuid=:id AND complete!=1")
    Death ins(String id);

    @Query("SELECT uuid, 'Death' AS formType, insertDate, firstName || ' ' || lastName as fullName FROM death WHERE complete = 1 ORDER BY insertDate DESC")
    List<CompletedForm> getCompletedForms();
    @Query("SELECT * FROM death where uuid=:id")
    LiveData<Death> getView(String id);

    @Query("SELECT COUNT(*) FROM death WHERE complete= 1")
    LiveData<Long> sync();

}
