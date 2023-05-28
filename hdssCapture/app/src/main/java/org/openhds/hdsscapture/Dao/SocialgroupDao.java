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
    void update(Socialgroup socialgroup);

    @Query("DELETE FROM socialgroup")
    void deleteAll();

    @Query("SELECT * FROM socialgroup WHERE socialgroup_uuid=:id")
    Socialgroup retrieve(String id);

    @Query("SELECT * FROM socialgroup WHERE socialgroup_uuid=:id")
    Socialgroup findhse(String id);

    @Query("SELECT * FROM socialgroup WHERE complete=1")
    List<Socialgroup> retrieveToSync();

    @Query("SELECT * FROM socialgroup WHERE insertDate BETWEEN :startDate AND :endDate")
    List<Socialgroup> retrieve(Date startDate, Date endDate);

    @Update(entity = Socialgroup.class)
    int update (SocialgroupAmendment socialgroupAmendment);

    @Query("SELECT * FROM socialgroup")
    List<Socialgroup> getAll();

    @Query("SELECT a.*,compextId FROM socialgroup as a " + "INNER JOIN residency as b ON a.socialgroup_uuid = b.socialgroup_uuid" +
            " INNER JOIN Locations as c on b.location_uuid=c.location_uuid " +
            " WHERE b.endType=1 and c.compextId=:id GROUP BY a.houseExtId ")
    List<Socialgroup> retrieveBySocialgroup(String id);


    @Query("SELECT COUNT(*) FROM socialgroup a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

}
