package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.OmgUpdate;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface OutmigrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Outmigration outmigration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Outmigration... outmigration);

    @Query("DELETE FROM outmigration")
    void deleteAll();

    @Update(entity = Outmigration.class)
    int update(OmgUpdate s);

    @Query("SELECT * FROM residency as a LEFT JOIN outmigration as b on a.uuid=b.residency_uuid" +
            " where b.residency_uuid IS NULL AND a.individual_uuid=:id and a.location_uuid!=:locid and endType=1")
    Outmigration createOmg(String id, String locid);

    @Query("SELECT * FROM outmigration WHERE complete!=0")
    List<Outmigration> retrieveomgToSync();

//    @Query("SELECT * FROM outmigration WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Outmigration> retrieveomgToSync();

    @Query("SELECT * FROM outmigration a INNER JOIN residency b on a.residency_uuid=b.uuid WHERE a.individual_uuid=:id AND b.location_uuid=:locid")
    Outmigration find(String id,String locid);

    @Query("SELECT * FROM outmigration WHERE individual_uuid=:id AND location_uuid=:locid AND edit IS NULL")
    Outmigration edit(String id,String locid);

    @Query("SELECT * FROM outmigration WHERE individual_uuid=:id AND visit_uuid=:res")
    Outmigration finds(String id,String res);

    @Query("SELECT * FROM outmigration WHERE residency_uuid=:id")
    Outmigration findLast(String id);

    @Query("SELECT COUNT(*) FROM outmigration a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE date(insertDate/1000,'unixepoch') BETWEEN date(:startDate/1000,'unixepoch') AND date(:endDate/1000,'unixepoch') AND b.username = :username AND edit!=2")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.uuid,a.insertDate,a.residency_uuid,b.firstName as visit_uuid,b.lastName as location_uuid FROM outmigration as a inner join individual as b on a.individual_uuid=b.uuid WHERE socialgroup_uuid=:id")
    List<Outmigration> end(String id);

    @Query("SELECT a.residency_uuid,b.firstName as sttime,b.lastName as edtime,b.extId as reason_oth,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM outmigration a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Outmigration> reject(String id);

    @Query("SELECT COUNT(*) FROM outmigration WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT * FROM outmigration where uuid=:id AND complete!=1 ")
    Outmigration ins(String id);

    @Query("SELECT uuid, 'Outmigration' AS formType, 'Outmigration: ' || ' (' || insertDate || ')' AS displayText FROM outmigration WHERE complete = 1")
    List<CompletedForm> getCompletedForms();
}
