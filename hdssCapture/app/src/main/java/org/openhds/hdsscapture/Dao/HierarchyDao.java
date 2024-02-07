package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;

import java.util.Date;
import java.util.List;

@Dao
public interface HierarchyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Hierarchy... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Hierarchy data);

    @Query("DELETE FROM locationhierarchy")
    void deleteAll();

    @Update
    void update(Hierarchy data);

    @Delete
    void delete(Hierarchy data);

    @Query("SELECT * FROM locationhierarchy where level_uuid='hierarchyLevelId1'")
    List<Hierarchy> retrieveLevel1();

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b " +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId2' and a.parent_uuid=:id order by a.name")
    List<Hierarchy> retrieveLevel2(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId3' and a.parent_uuid=:id order by a.name")
    List<Hierarchy> retrieveLevel3(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId4' and a.parent_uuid=:id order by a.name")
    List<Hierarchy> retrieveLevel4(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId5' and a.parent_uuid=:id order by a.name")
    List<Hierarchy> retrieveLevel5(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId6' and a.parent_uuid=:id order by a.name")
    List<Hierarchy> retrieveLevel6(String id);

//    @Query("SELECT b.extId,b.name,b.uuid FROM Locations as a INNER JOIN locationhierarchy as b ON a.locationLevel_uuid = b.uuid " +
//            " INNER JOIN locationhierarchy as c on b.parent_uuid=c.uuid " +
//            " where b.parent_uuid=:id GROUP BY b.extId order by c.name")
//    List<Hierarchy> clusters(String id);

    @Query("SELECT b.extId,b.name,b.uuid FROM locationhierarchy as a INNER JOIN locationhierarchy as b ON a.uuid = b.parent_uuid " +
            " LEFT JOIN locations as c on b.uuid=c.locationLevel_uuid " +
            " where b.parent_uuid=:id GROUP BY b.extId order by b.name")
    List<Hierarchy> clusters(String id);

    @Query("SELECT * FROM locationhierarchy where level_uuid='hierarchyLevelId6' order by name")
    List<Hierarchy> retrieveLevel7();

    @Query("SELECT * FROM locationhierarchy where level_uuid='hierarchyLevelId5' order by name")
    List<Hierarchy> retrieveVillage();

    @Query("SELECT a.uuid,d.name,c.name as area,b.name as town,a.name as parent_uuid FROM locationhierarchy as a INNER JOIN locationhierarchy as b on a.parent_uuid=b.uuid " +
            "INNER JOIN locationhierarchy as c on b.parent_uuid=c.uuid " +
            " INNER JOIN locationhierarchy as d on c.parent_uuid=d.uuid WHERE a.fw_name=:id AND a.level_uuid='hierarchyLevelId6' order by a.name")
    List<Hierarchy> repo(String id);

}
