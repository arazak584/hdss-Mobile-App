package org.openhds.hdsscapture.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.DemographicAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface DemographicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Demographic... demographic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Demographic demographic);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Demographic> demographics);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Demographic... demographics);

    @Query("DELETE FROM demographic")
    void deleteAll();

    @Update
    void update(Demographic demographic);

    @Update(entity = Demographic.class)
    int update(DemographicAmendment demographicAmendment);

    @Delete
    void Delete(Demographic user);

    @Query("SELECT * FROM demographic WHERE complete=1")
    List<Demographic> retrieveToSync();

//    @Query("SELECT * FROM demographic WHERE insertDate > 1748121600000")
//    List<Demographic> retrieveToSync();

    @Query("SELECT * FROM demographic where individual_uuid=:id")
    Demographic find(String id);

    @Query("SELECT * FROM demographic where individual_uuid=:id AND complete!=1 ")
    Demographic ins(String id);

    @Query("SELECT COUNT(*) FROM demographic a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT * FROM demographic where individual_uuid='7bfeb156db724f56808cb87551a16cd7'")
    List<Demographic> error();

    @Query("SELECT a.individual_uuid,b.firstName as sttime,b.lastName as edtime,b.extId as occupation_oth,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM demographic a INNER JOIN individual b on a.individual_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2" +
            " AND a.insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1) order by a.insertDate DESC")
    List<Demographic> reject(String id);

    @Query("SELECT COUNT(*) FROM demographic WHERE status=2 AND fw_uuid = :uuid AND insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1)")
    long rej(String uuid);

    @Query("SELECT a.individual_uuid as uuid, 'Demographic' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM demographic as a inner join individual as b ON a.individual_uuid=b.uuid WHERE a.complete = 1")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM demographic where individual_uuid=:id")
    LiveData<Demographic> getView(String id);

    @Query("SELECT COUNT(*) FROM demographic WHERE complete= 1")
    LiveData<Long> sync();
}
