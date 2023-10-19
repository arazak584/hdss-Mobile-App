package org.openhds.hdsscapture.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Relationship> relationships);

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

    @Query("SELECT * FROM relationship where individualA_uuid=:id")
    Relationship find(String id);

    @Query("SELECT * FROM relationship where individualB_uuid=:id")
    Relationship finds(String id);

    @Query("SELECT COUNT(*) FROM relationship a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid " +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);


}
