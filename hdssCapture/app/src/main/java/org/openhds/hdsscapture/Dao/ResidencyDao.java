package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.List;

@Dao
public interface ResidencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Residency... residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Residency residency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Residency> residency);

   @Update
   void update(Residency residency);

    @Update(entity = Residency.class)
   int update (ResidencyAmendment residencyAmendment);

    @Query("SELECT * FROM residency")
    List<Residency> getAll();

    @Query("SELECT * FROM residency")
    List<Residency> retrieve();

    @Query("SELECT * FROM residency WHERE complete=1")
    List<Residency> retrieveToSync();

    @Query("SELECT extId,insertDate,migType,origin,reason,startDate as recordedDate,uuid,visitid FROM residency WHERE complete=1 and startType=1")
    List<Residency> retrieveimgToSync();

    @Query("SELECT extId,insertDate,destination,omgreason as reason,endDate as recordedDate,uuid,visitid FROM residency WHERE complete=1 and startType=2")
    List<Residency> retrieveomgToSync();
}
