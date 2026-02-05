package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Vpm;

import java.util.List;

@Dao
public interface DuplicateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Duplicate duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Duplicate... duplicate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Duplicate> duplicate);

    @Delete
    void delete(Duplicate duplicate);

    @Delete
    void delete(Duplicate... duplicate);


    @Query("DELETE FROM duplicate")
    void deleteAll();

    @Query("SELECT * FROM duplicate WHERE complete IN (1,2)")
    List<Duplicate> retrieveSync();

//    @Query("SELECT * FROM duplicate WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 ORDER BY insertDate ASC")
//    List<Duplicate> retrieveSync();

    @Query("SELECT * FROM duplicate where uuid=:id limit 1")
    Duplicate find(String id);

    @Query("SELECT * FROM duplicate where individual_uuid=:id limit 1")
    Duplicate getId(String id);

    @Query("SELECT * FROM duplicate where individual_uuid=:id AND complete_n!=2 limit 1")
    Duplicate finds(String id);

    @Query("SELECT * FROM duplicate WHERE " +
            "(individual_uuid = :id OR dup_uuid = :id OR dup1_uuid = :id OR dup2_uuid = :id) LIMIT 1")
    Duplicate findByAnyUuid(String id);

    @Query("SELECT * FROM duplicate where individual_uuid=:id AND complete!=1 ")
    Duplicate ins(String id);

    @Query("SELECT a.*,b.compno as dup_uuid,b.hohID as dup1_uuid FROM duplicate a inner join individual as b on a.individual_uuid=b.uuid ")
    List<Duplicate> repo();

    @Query("SELECT COUNT(*) FROM duplicate WHERE complete= 1")
    LiveData<Long> sync();

    @Query("SELECT * FROM duplicate WHERE uuid IN (:uuids) AND complete!=1")
    List<Duplicate> getByUuids(List<String> uuids);

    @Query("SELECT * FROM duplicate WHERE fw_uuid=:id AND status=2 order by insertDate DESC")
    List<Duplicate> reject(String id);

    @Query("SELECT COUNT(*) FROM duplicate WHERE status=2 AND fw_uuid = :uuid ")
    long rej(String uuid);

    @Query("SELECT uuid, 'Duplicate' AS formType, insertDate, fname || ' ' || lname as fullName FROM duplicate WHERE complete IN (1,2) ORDER BY insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM duplicate where uuid=:id")
    LiveData<Duplicate> getView(String id);

}
