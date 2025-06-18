package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

import java.util.Date;
import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Locations... locations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Locations locations);

    @Query("DELETE FROM locations")
    void deleteAll();

    @Update
    void update(Locations s);

    @Update(entity = Locations.class)
    int update(LocationAmendment s);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Locations> locations);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Locations... locations);

    @Delete
    void delete(Locations locations);

    @Query("SELECT * FROM Locations WHERE compno=:id")
    Locations retrieve(String id);

    @Query("SELECT * FROM Locations WHERE complete=1")
    List<Locations> retrieveToSync();

//    @Query("SELECT * FROM Locations WHERE insertDate > 1748121600000")
//    List<Locations> retrieveToSync();

    @Query("SELECT * FROM Locations a INNER JOIN locationhierarchy b ON a.locationLevel_uuid=b.uuid WHERE fw_name=:id")
    List<Locations> retrieveAll(String id);

    @Query("SELECT * FROM Locations WHERE locationLevel_uuid=:id order by compno")
    List<Locations> retrieveByClusterId(String id);

    @Query("SELECT * FROM Locations WHERE locationLevel_uuid=:id AND (compno LIKE:ids OR locationName LIKE:ids OR compno LIKE:ids) order by compno")
    List<Locations> retrieveBySearch(String id, String ids);

    @Query("SELECT * FROM Locations WHERE compno LIKE:id OR locationName LIKE:id OR compno LIKE:id order by compno")
    List<Locations> retrieveBySearchs(String id);

    @Query("SELECT * FROM Locations WHERE compno LIKE:id OR locationName LIKE:id OR compextId LIKE:id order by compno")
    List<Locations> filter(String id);


    @Query("SELECT a.* FROM Locations as a " + "INNER JOIN locationhierarchy as b ON a.locationLevel_uuid = b.uuid " +
            " LEFT JOIN locationhierarchy c on b.parent_uuid=c.uuid " +
            " LEFT JOIN listing as d on a.uuid=d.location_uuid where b.name LIKE:id and d.location_uuid is null order by a.compextId")
    List<Locations> retrieveByVillage(String id);


    @Query("SELECT COUNT(*) FROM locations a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(*) FROM Locations as a LEFT JOIN listing as b on a.compno=b.compno " +
            "WHERE locationLevel_uuid=:id AND b.compno IS NULL")
    long counts(String id);

    @Query("SELECT COUNT(DISTINCT hohID) FROM individual as a LEFT JOIN visit as b on a.hohID=b.houseExtId " +
            "WHERE village=:id AND b.houseExtId IS NULL AND endType=1")
    long hseCount(String id);

    @Query("SELECT COUNT(*) FROM Locations WHERE locationLevel_uuid=:id")
    long done(String id);

    @Query("SELECT COUNT(a.compno) FROM Locations as a INNER JOIN locationhierarchy as b on a.locationLevel_uuid=b.uuid " +
            "WHERE fw_name=:id")
    long work(String id);

    @Query("SELECT COUNT(a.compno) FROM listing as a INNER JOIN locationhierarchy as b on a.cluster_id=b.uuid " +
            "WHERE b.fw_name=:id")
    long works(String id);

    @Query("SELECT * FROM locations WHERE insertDate > (SELECT startDate FROM round ORDER BY roundNumber DESC LIMIT 1) AND fw_uuid=:id order by insertDate DESC")
    List<Locations> repo(String id);

}
