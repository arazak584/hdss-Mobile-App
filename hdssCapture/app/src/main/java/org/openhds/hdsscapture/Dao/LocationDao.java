package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Locations;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Locations> locations);

    @Delete
    void delete(Locations locations);

    @Query("SELECT * FROM Locations WHERE compno=:id")
    Locations retrieve(String id);

    @Query("SELECT * FROM Locations ")
    List<Locations> retrieve();

    @Query("SELECT * FROM Locations WHERE complete=1")
    List<Locations> retrieveToSync();

    @Query("SELECT * FROM Locations WHERE locationLevel_uuid=:id order by compno")
    List<Locations> retrieveByClusterId(String id);

    @Query("SELECT * FROM Locations WHERE locationLevel_uuid=:id AND (compno LIKE:ids OR locationName LIKE:ids OR compno LIKE:ids) order by compno")
    List<Locations> retrieveBySearch(String id, String ids);

    @Query("SELECT * FROM Locations WHERE compno LIKE:id OR locationName LIKE:id OR compno LIKE:id order by compno")
    List<Locations> retrieveBySearchs(String id);

    @Query("SELECT * FROM Locations WHERE compno LIKE:id OR locationName LIKE:id OR compno LIKE:id order by compno")
    List<Locations> filter(String id);


    @Query("SELECT a.* FROM Locations as a " + "INNER JOIN locationhierarchy as b ON a.locationLevel_uuid = b.uuid " +
            " LEFT JOIN listing as d on a.uuid=d.location_uuid where b.name=:id and d.location_uuid is null order by a.compno")
    List<Locations> retrieveByVillage(String id);


    @Query("SELECT COUNT(*) FROM locations a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

}
