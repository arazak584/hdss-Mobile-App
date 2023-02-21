package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;

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

    @Query("SELECT * FROM socialgroup WHERE extid=:id")
    Socialgroup retrieve(String id);

    @Query("SELECT * FROM socialgroup WHERE complete=1")
    List<Socialgroup> retrieveToSync();

    @Query("SELECT * FROM socialgroup ")
    List<Socialgroup> retrieve();

    @Update(entity = Socialgroup.class)
    int update (SocialgroupAmendment socialgroupAmendment);

    @Query("SELECT * FROM socialgroup")
    List<Socialgroup> getAll();

    @Query("SELECT COUNT(*) FROM socialgroup")
    int count();

    @Query("SELECT socialgroup.* FROM socialgroup " + "INNER JOIN residency ON socialgroup.extId = residency.socialgroup" +
            " INNER JOIN location on residency.location=location.extId " +
            " WHERE residency.endType=1 and socialgroup.extId=:id GROUP BY socialgroup.extId ")
    LiveData<List<Socialgroup>> retrieveBySocialgroup(String id);
}
