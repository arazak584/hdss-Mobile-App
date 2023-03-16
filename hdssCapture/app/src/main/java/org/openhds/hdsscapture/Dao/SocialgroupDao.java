package org.openhds.hdsscapture.Dao;

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

    @Query("SELECT a.*,location FROM socialgroup as a " + "INNER JOIN residency as b ON a.extId = b.socialgroup" +
            " INNER JOIN location as c on b.location=c.extId " +
            " WHERE b.endType=1 and b.location=:id GROUP BY a.extId ")
    List<Socialgroup> retrieveBySocialgroup(String id);
}
