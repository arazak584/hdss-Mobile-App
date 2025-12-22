package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface
PregnancyoutcomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Pregnancyoutcome pregnancyoutcome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancyoutcome... pregnancyoutcome);

    @Update(entity = Pregnancyoutcome.class)
    int update(OutcomeUpdate s);

    @Query("DELETE FROM pregnancyoutcome")
    void deleteAll();

    @Query("SELECT * FROM pregnancyoutcome WHERE complete=1")
    List<Pregnancyoutcome> retrieveToSync();

//    @Query("SELECT * FROM pregnancyoutcome WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Pregnancyoutcome> retrieveToSync();

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id ORDER BY outcomeDate ASC LIMIT 1")
    Pregnancyoutcome find(String id);

    @Query("SELECT * FROM pregnancyoutcome where pregnancy_uuid=:id LIMIT 1")
    Pregnancyoutcome preg(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND (id IS NULL OR (id != 2 AND id != 3)) ORDER BY outcomeDate ASC LIMIT 1")
    Pregnancyoutcome find1(String id);

//    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id and location=:locid ORDER BY outcomeDate ASC LIMIT 1")
//    Pregnancyoutcome findloc(String id, String locid);

    @Query("SELECT a.* FROM pregnancyoutcome a INNER JOIN individual b ON a.mother_uuid=b.uuid " +
            "where a.mother_uuid=:id and b.compno=:locid AND (id IS NULL OR (id != 2 AND id != 3)) ORDER BY outcomeDate ASC LIMIT 1")
    Pregnancyoutcome findloc(String id, String locid);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND (id IS NULL OR (id != 2 AND id != 3)) ORDER BY outcomeDate ASC LIMIT 1")
    Pregnancyoutcome findMother(String id);

    @Query("SELECT a.* FROM pregnancyoutcome a INNER JOIN individual b ON a.mother_uuid=b.uuid " +
            "where a.uuid=:id and b.hohID=:locid")
    Pregnancyoutcome findedit(String id, String locid);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND id=2")
    Pregnancyoutcome finds(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND id=3")
    Pregnancyoutcome finds3(String id);

    @Query("SELECT a.* FROM pregnancyoutcome a INNER JOIN individual b ON a.mother_uuid=b.uuid " +
            " where a.mother_uuid=:id and b.compno=:locid AND id=2")
    Pregnancyoutcome findsloc(String id, String locid);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND outcomeDate < :recordedDate ORDER BY outcomeDate DESC LIMIT 1")
    Pregnancyoutcome lastpregs(String id, Date recordedDate);

    @Query("SELECT a.* FROM pregnancyoutcome a INNER JOIN individual b ON a.mother_uuid=b.uuid " +
            " where a.mother_uuid=:id and b.compno=:locid AND id=3")
    Pregnancyoutcome find3(String id, String locid);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id and extra=1 AND id IS NULL")
    Pregnancyoutcome findout(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id and extra=1 AND id=2")
    Pregnancyoutcome findout3(String id);

    @Query("SELECT a.uuid,b.firstName as visit_uuid,pregnancy_uuid,b.lastName as father_uuid,b.extId as fw_uuid,outcomeDate,a.mother_uuid " +
            " FROM pregnancyoutcome as a INNER JOIN individual as b ON a.mother_uuid = b.uuid " +
            " WHERE b.hohID = :id ")
    List<Pregnancyoutcome> retrieveOutcome(String id);

    @Query("SELECT * FROM pregnancyoutcome where uuid=:id AND complete!=1")
    Pregnancyoutcome ins(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id ORDER BY outcomeDate DESC LIMIT 1")
    Pregnancyoutcome findpreg(String id);

    @Query("SELECT COUNT(*) FROM pregnancyoutcome a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.uuid,b.firstName as sttime,b.lastName as edtime,b.extId as father_uuid,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM pregnancyoutcome a INNER JOIN individual b on a.mother_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Pregnancyoutcome> reject(String id);

    @Query("SELECT COUNT(*) FROM pregnancyoutcome WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT a.uuid,b.extId as pregnancy_uuid,b.compno as location,b.firstName as mother_uuid,b.lastName as father_uuid FROM pregnancyoutcome a " +
            " INNER JOIN individual as b on a.mother_uuid=b.uuid WHERE a.complete IS NULL AND a.fw_uuid = :id ")
    List<Pregnancyoutcome> error(String id);

    @Query("SELECT COUNT(*) FROM pregnancyoutcome a " +
            " INNER JOIN individual as b on a.mother_uuid=b.uuid WHERE a.complete IS NULL AND a.fw_uuid = :id ")
    long cnt(String id);

    @Query("SELECT a.uuid, 'Pregnancy Outcome' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM pregnancyoutcome as a inner join individual as b ON a.mother_uuid=b.uuid WHERE a.complete = 1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM pregnancyoutcome where uuid=:id")
    LiveData<Pregnancyoutcome> getView(String id);

    @Query("SELECT COUNT(*) FROM pregnancyoutcome WHERE complete= 1")
    LiveData<Long> sync();

    @Query("SELECT * FROM pregnancyoutcome WHERE pregnancy_uuid=:id")
    Pregnancyoutcome getId(String id);
}
