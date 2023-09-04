package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface SocialgroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Socialgroup... socialgroup);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Socialgroup socialgroup);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Socialgroup> socialgroup);

    @Update
    int update(Socialgroup s);

    @Update(entity = Socialgroup.class)
    int update(SocialgroupAmendment s);

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

    @Query("SELECT * FROM socialgroup WHERE complete=1 AND groupName!='UNK'")
    List<Socialgroup> retrieveToSync();

    @Query("SELECT * FROM socialgroup WHERE insertDate BETWEEN :startDate AND :endDate")
    List<Socialgroup> retrieve(Date startDate, Date endDate);

    @Query("SELECT a.*,compextId FROM socialgroup as a " + "INNER JOIN residency as b ON a.uuid = b.socialgroup_uuid" +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and c.compextId=:id GROUP BY a.extId ")
    List<Socialgroup> retrieveBySocialgroup(String id);


    @Query("SELECT COUNT(*) FROM socialgroup a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE groupName!='UNK' AND insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

//    @Query("SELECT * FROM socialgroup where groupName='UNK' AND complete=1 ")
//    List<Socialgroup> error();

    @Query("SELECT a.* FROM socialgroup as a INNER JOIN visit as b ON a.visit_uuid=b.uuid" +
            " where respondent='UNK' AND a.groupName!='UNK' ")
    List<Socialgroup> error();

}
