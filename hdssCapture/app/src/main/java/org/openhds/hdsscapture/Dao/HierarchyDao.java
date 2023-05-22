package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Hierarchy;

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
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId2' and a.parent_uuid=:id")
    List<Hierarchy> retrieveLevel2(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId3' and a.parent_uuid=:id ")
    List<Hierarchy> retrieveLevel3(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId4' and a.parent_uuid=:id ")
    List<Hierarchy> retrieveLevel4(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId5' and a.parent_uuid=:id ")
    List<Hierarchy> retrieveLevel5(String id);

    @Query("select a.* from locationhierarchy as a left join locationhierarchy as b" +
            " on a.parent_uuid=b.uuid where a.level_uuid='hierarchyLevelId6' and a.parent_uuid=:id ")
    List<Hierarchy> retrieveLevel6(String id);

    @Query("SELECT * FROM locationhierarchy where level_uuid='hierarchyLevelId5' order by name")
    List<Hierarchy> retrieveLevel7();

}
