package org.openhds.hdsscapture.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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

    @Query("SELECT a.individualA_uuid,a.uuid,b.firstName as sttime,b.lastName as edtime,b.extId as individualB_uuid,b.compno as visit_uuid,a.approveDate,a.comment,a.fw_uuid,a.supervisor FROM relationship a INNER JOIN individual b on a.individualA_uuid=b.uuid WHERE a.fw_uuid=:id AND status=2 order by a.insertDate DESC")
    List<Relationship> reject(String id);

    @Query("SELECT COUNT(*) FROM relationship WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);
}
