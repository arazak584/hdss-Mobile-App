package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;

import java.util.List;

@Dao
public interface HierarchyLevelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HierarchyLevel... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(HierarchyLevel data);

    @Query("DELETE FROM hierarchylevel")
    void deleteAll();

    @Update
    void update(HierarchyLevel data);

    @Delete
    void delete(HierarchyLevel data);

    @Query("SELECT * FROM hierarchylevel")
    List<HierarchyLevel> retrieve();

}
