package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdateEndDate;

import java.util.Date;
import java.util.List;

@Dao
public interface ResidencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Residency... residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Residency residency);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Residency> residency);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Residency... residency);

    @Update
    int update(Residency s);

    @Update(entity = Residency.class)
    int update(ResidencyAmendment s);

    @Update(entity = Residency.class)
    int updates(ResidencyUpdate s);

    @Update(entity = Residency.class)
    int updatez(ResidencyUpdateEndDate s);

    @Query("DELETE FROM residency")
    void deleteAll();

    @Query("SELECT * FROM residency where individual_uuid=:id")
    List<Residency> find(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id and location_uuid=:locid and endType=1")
    Residency findRes(String id, String locid);

    @Query("SELECT * FROM residency where individual_uuid=:id and endType=1")
    Residency getUuid(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id and location_uuid=:locid and endType=3")
    Residency findDth(String id, String locid);

    @Query("SELECT * FROM residency where individual_uuid=:id and location_uuid!=:locid and endType=1")
    Residency findEnd(String id, String locid);

    @Query("SELECT * FROM residency WHERE individual_uuid = :id ORDER BY startDate DESC LIMIT 1 OFFSET 1")
    Residency findLastButOne(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id AND endDate is not null ORDER BY startDate DESC LIMIT 1")
    Residency finds(String id);

    @Query("SELECT * FROM residency WHERE uuid=:id ")
    Residency updateres(String id);

    @Query("SELECT * FROM residency WHERE complete=1")
    List<Residency> retrieveToSync();

//    @Query("SELECT * FROM residency WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 AND complete!=2")
//    List<Residency> retrieveToSync();

    @Query("SELECT * FROM residency WHERE uuid=:id AND location_uuid!=loc")
    Residency fetch(String id);

    @Query("SELECT * FROM residency WHERE individual_uuid=:id AND location_uuid!=:locid AND endType=1")
    Residency fetchs(String id, String locid);

    @Query("SELECT a.* FROM residency as a INNER JOIN individual as b on a.individual_uuid=b.uuid " +
            "WHERE socialgroup_uuid=:id AND firstName='FAKE' AND a.endType=1")
    Residency unk(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id ORDER BY startDate ASC LIMIT 1")
    Residency amend(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id ORDER BY startDate DESC LIMIT 1")
    Residency views(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id AND location_uuid=:locid and endType=1 ORDER BY startDate ASC")
    Residency dth(String id, String locid);

    @Query("SELECT * FROM residency where individual_uuid=:id and endType=3 ORDER BY startDate ASC")
    Residency restore(String id);

    @Query("SELECT * FROM residency WHERE individual_uuid=:id AND location_uuid=:locid AND endType=2")
    Residency resomg(String id, String locid);

    @Query("SELECT COUNT(*) FROM residency a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " INNER JOIN individual as c on a.individual_uuid=c.uuid " +
            " WHERE c.firstName!='FAKE' AND a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

//    @Query("SELECT a.uuid,a.age as extId, a.loc as firstName,a.location_uuid as lastName,a.fw_uuid as compextId FROM residency as a LEFT JOIN individual as b on a.individual_uuid=b.uuid" +
//            " LEFT JOIN socialgroup as c on a.socialgroup_uuid=c.uuid WHERE c.uuid is null ")
//    List<Residency> error();

//    @Query("SELECT b.uuid,a.firstName as socialgroup_uuid,a.extId as location_uuid,b.individual_uuid as compextId,b.socialgroup_uuid FROM individual as a INNER JOIN residency as b on a.uuid=b.individual_uuid" +
//            " INNER JOIN socialgroup as c on b.socialgroup_uuid=c.uuid WHERE groupName='UNK' ")
//    List<Residency> error();

    @Query("SELECT b.uuid,a.location_uuid as extId FROM residency as a INNER JOIN socialgroup as b on a.socialgroup_uuid=b.uuid WHERE b.insertDate>'2023-08-15'" +
            " GROUP BY a.socialgroup_uuid")
    List<Residency> error();

    @Query("SELECT a.uuid, 'Membership' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM residency as a inner join individual as b ON a.individual_uuid=b.uuid WHERE a.complete = 1 and a.endType=1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM residency where uuid=:id")
    LiveData<Residency> getView(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id")
    LiveData<Residency> getViews(String id);

    @Query("SELECT COUNT(*) FROM residency WHERE complete= 1")
    LiveData<Long> sync();

}
