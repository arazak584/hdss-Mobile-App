package org.openhds.hdsscapture.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.DemographicAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface DemographicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Demographic... demographic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Demographic demographic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Demographic> demographics);

    @Query("DELETE FROM demographic")
    void deleteAll();

    @Update
    void update(Demographic demographic);

    @Update(entity = Demographic.class)
    int update(DemographicAmendment demographicAmendment);

    @Delete
    void Delete(Demographic user);


    @Query("SELECT * FROM demographic")
    List<Demographic> retrieve();

    @Query("SELECT * FROM demographic as a inner join individual as b on a.individual_uuid=b.uuid WHERE a.complete=1")
    List<Demographic> retrieveToSync();

    @Query("SELECT * FROM demographic where individual_uuid=:id")
    Demographic find(String id);

    @Query("SELECT COUNT(*) FROM demographic a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT * FROM demographic as a inner join individual as b on a.individual_uuid=b.uuid WHERE a.complete=1 ")
    List<Demographic> error();
}
