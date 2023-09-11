package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface ResidencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Residency... residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Residency residency);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Residency> residency);

    @Update
    int update(Residency s);

    @Update(entity = Residency.class)
    int update(ResidencyAmendment s);

    @Query("DELETE FROM residency")
    void deleteAll();

    @Query("SELECT * FROM residency where individual_uuid=:id")
    List<Residency> find(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id and location_uuid=:locid and endType=1")
    Residency findRes(String id, String locid);

    @Query("SELECT * FROM residency where individual_uuid=:id and location_uuid!=:locid and endType=1")
    Residency findEnd(String id, String locid);

    @Query("SELECT * FROM residency where individual_uuid=:id AND endDate is not null ORDER BY startDate DESC LIMIT 1")
    Residency finds(String id);

    @Query("SELECT * FROM residency WHERE complete=1")
    List<Residency> retrieveToSync();

    @Query("SELECT * FROM residency WHERE uuid=:id AND location_uuid!=loc")
    Residency fetch(String id);

    @Query("SELECT * FROM residency WHERE individual_uuid=:id AND location_uuid!=:locid AND endType=1")
    Residency fetchs(String id, String locid);

    @Query("SELECT a.* FROM residency as a INNER JOIN individual as b on a.individual_uuid=b.uuid " +
            "WHERE socialgroup_uuid=:id AND firstName='FAKE' AND a.endType=1")
    Residency unk(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id ORDER BY startDate ASC LIMIT 1")
    Residency amend(String id);

    @Query("SELECT COUNT(*) FROM residency a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " INNER JOIN individual as c on a.individual_uuid=c.uuid " +
            " WHERE c.firstName!='FAKE' AND a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.*,b.groupName FROM residency as a left join socialgroup as b on a.socialgroup_uuid=b.uuid WHERE b.uuid is null ")
    List<Residency> error();

}
