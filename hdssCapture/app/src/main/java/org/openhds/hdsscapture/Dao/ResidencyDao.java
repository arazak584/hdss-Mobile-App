package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface ResidencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Residency... residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Residency residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Residency> residency);

    @Update
    int update(Residency s);

    @Update(entity = Residency.class)
    int update(ResidencyAmendment s);

    @Query("DELETE FROM residency")
    void deleteAll();

    @Query("SELECT * FROM residency")
    List<Residency> getAll();

    @Query("SELECT * FROM residency")
    List<Residency> retrieve();

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

    @Query("SELECT * FROM residency as a INNER JOIN outmigration as b on a.uuid=b.residency_uuid WHERE a.individual_uuid=:id AND endType=1")
    Residency fetchs(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id ORDER BY startDate ASC LIMIT 1")
    Residency amend(String id);

    @Query("SELECT COUNT(*) FROM residency a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " INNER JOIN individual as c on a.individual_uuid=c.uuid " +
            " WHERE c.firstName!='FAKE' AND a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

}
