package org.openhds.hdsscapture.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;

import java.util.Date;
import java.util.List;

@Dao
public interface RelationshipDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Relationship... relationship);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Relationship relationship);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Relationship> relationships);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Relationship... relationships);

    @Query("DELETE FROM relationship")
    void deleteAll();

    @Update
    int update(Relationship s);

    @Update
    int updateb(Relationship z);

    @Update(entity = Relationship.class)
    int update(RelationshipUpdate s);


    @Query("SELECT * FROM relationship WHERE complete=1")
    List<Relationship> retrieveToSync();

//    @Query("SELECT * FROM relationship WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Relationship> retrieveToSync();

    @Query("SELECT * FROM relationship where individualA_uuid=:id")
    Relationship find(String id);

    @Query("SELECT * FROM relationship where uuid=:id AND complete!=1")
    Relationship ins(String id);

    @Query("SELECT * FROM relationship where individualB_uuid=:id")
    Relationship finds(String id);

//    @Query("SELECT COUNT(*) FROM relationship a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid " +
//            " WHERE a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
//    long count(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM relationship a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid " +
            " WHERE (a.insertDate BETWEEN :startDate AND :endDate OR a.formcompldate BETWEEN :startDate AND :endDate) AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.individualA_uuid,a.uuid,b.firstName as sttime,b.lastName as edtime,b.extId as individualB_uuid,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM relationship a INNER JOIN individual b on a.individualA_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 " +
            " AND a.insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1) order by a.insertDate DESC")
    List<Relationship> reject(String id);

    @Query("SELECT COUNT(*) FROM relationship WHERE status=2 AND fw_uuid = :uuid AND insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1)")
    long rej(String uuid);

    @Query("SELECT a.uuid, 'Relationship' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM relationship as a inner join individual as b ON a.individualA_uuid=b.uuid WHERE a.complete = 1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM relationship where uuid=:id")
    LiveData<Relationship> getView(String id);

    @Query("SELECT COUNT(*) FROM relationship WHERE complete= 1")
    LiveData<Long> sync();
}
