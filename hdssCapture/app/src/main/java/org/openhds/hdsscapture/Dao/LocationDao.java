package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Locations... locations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Locations locations);

    @Update
    void update(Locations locations);

    @Update(entity = Locations.class)
    int update(LocationAmendment locationAmendment);

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

    @Query("SELECT * FROM Locations WHERE compno LIKE:id OR locationName LIKE:id OR compno LIKE:id order by compno")
    List<Locations> retrieveBySearch(String id);


    @Query("SELECT a.* FROM Locations as a " + "INNER JOIN locationhierarchy as b ON a.locationLevel_uuid = b.uuid " +
            " INNER JOIN locationhierarchy as c on b.parent_uuid=c.uuid " +
            " LEFT JOIN visit as d on a.location_uuid=d.location_uuid where c.name=:id and d.location_uuid is null order by a.compno")
    List<Locations> retrieveByVillage(String id);


}
