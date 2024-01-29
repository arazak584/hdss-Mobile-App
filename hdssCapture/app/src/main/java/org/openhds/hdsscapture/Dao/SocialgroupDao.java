package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface SocialgroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Socialgroup... socialgroup);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Socialgroup socialgroup);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Socialgroup> socialgroup);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Socialgroup... socialgroup);

    @Update
    int update(Socialgroup s);

    @Update(entity = Socialgroup.class)
    int update(SocialgroupAmendment s);

    @Update(entity = Socialgroup.class)
    int visited(HvisitAmendment s);

    @Query("DELETE FROM socialgroup")
    void deleteAll();

    @Query("SELECT * FROM socialgroup WHERE uuid=:id")
    Socialgroup retrieve(String id);

    @Query("SELECT * FROM socialgroup WHERE extId=:id")
    Socialgroup createhse(String id);

    @Query("SELECT * FROM socialgroup WHERE uuid=:id")
    Socialgroup findhse(String id);

    @Query("SELECT * FROM socialgroup where uuid=:id and groupName='UNK' ")
    Socialgroup find(String id);

    @Query("SELECT * FROM socialgroup where uuid=:id and complete IS NULL")
    Socialgroup visit(String id);

    @Query("SELECT * FROM socialgroup as a INNER JOIN individual as b on a.individual_uuid=b.uuid " +
            "where a.uuid=:id and endType=1 and b.firstName!='FAKE' and " +
            "strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) <(SELECT hoh_age from config)")
    Socialgroup minor(String id);

    @Query("SELECT * FROM socialgroup WHERE complete=1 AND groupName!='UNK'")
    List<Socialgroup> retrieveToSync();

    @Query("SELECT * FROM socialgroup WHERE insertDate BETWEEN :startDate AND :endDate")
    List<Socialgroup> retrieve(Date startDate, Date endDate);

    @Query("SELECT a.*,compno FROM socialgroup as a INNER JOIN individual as b ON a.extId = b.hohID" +
            " WHERE b.endType=1 and compno=:id GROUP BY a.extId ")
    List<Socialgroup> retrieveBySocialgroup(String id);

    @Query("SELECT a.*,compno FROM socialgroup as a INNER JOIN individual as b ON a.extId = b.hohID" +
            " WHERE b.endType=1 and compno=:id and groupName!='UNK' GROUP BY a.extId ")
    List<Socialgroup> changehousehold(String id);


    @Query("SELECT COUNT(*) FROM socialgroup a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE groupName!='UNK' AND insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

//    @Query("SELECT a.* FROM socialgroup as a INNER JOIN residency as b on a.uuid=b.socialgroup_uuid" +
//            " INNER JOIN individual as c ON b.individual_uuid=c.uuid " +
//            " where groupName='UNK' AND c.firstName!='FAKE'")
//    List<Socialgroup> errors();

    @Query("SELECT a.*,c.compextId as visit_uuid FROM socialgroup as a INNER JOIN visit as b ON a.visit_uuid=b.uuid" +
            " INNER JOIN locations c on b.location_uuid=c.uuid " +
            " where respondent='UNK' AND a.groupName!='UNK' ")
    List<Socialgroup> error();

    @Query("SELECT * from socialgroup WHERE insertDate>'2023-08-15' ")
    List<Socialgroup> errors();

    @Query("SELECT * FROM socialgroup WHERE insertDate > (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1) AND fw_uuid=:id order by insertDate DESC")
    List<Socialgroup> repo(String id);
}
