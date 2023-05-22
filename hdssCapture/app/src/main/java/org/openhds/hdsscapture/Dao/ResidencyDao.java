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

    @Query("SELECT * FROM residency where individual_uuid=:id")
    List<Residency> find(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id and endType=1")
    Residency findRes(String id);

    @Query("SELECT * FROM residency where individual_uuid=:id AND endDate is not null ORDER BY startDate DESC LIMIT 1")
    Residency finds(String id);

    @Query("SELECT * FROM residency WHERE complete=1")
    List<Residency> retrieveToSync();

    @Query("SELECT * FROM residency WHERE residency_uuid=:id AND location_uuid!=loc")
    Residency fetch(String id);

}
