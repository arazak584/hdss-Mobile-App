package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;

import java.util.List;

@Dao
public interface CommunityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(CommunityReport... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(CommunityReport data);

    @Query("DELETE FROM community")
    void deleteAll();

    @Update
    void update(CommunityReport data);

    @Delete
    void delete(CommunityReport data);

    @Query("SELECT * FROM fieldworker WHERE username=:id AND password=:password")
    Fieldworker retrieve(String id, String password);

    @Query("SELECT * FROM community WHERE complete=1")
    List<CommunityReport> retrieveToSync();

    @Query("SELECT uuid,community,insertDate,name,codeLabel as description FROM community a INNER JOIN codebook b ON a.item=b.codeValue WHERE codeFeature='itemlist' AND community=:id  ORDER BY item")
    List<CommunityReport> retrieves(String id);

    @Query("SELECT * FROM community WHERE community='ABC'")
    CommunityReport retrieve();

    @Query("SELECT * FROM community WHERE uuid=:id")
    CommunityReport find(String id);

    @Query("SELECT * FROM community WHERE community=:id ")
    List<CommunityReport> fw(String id);
}
