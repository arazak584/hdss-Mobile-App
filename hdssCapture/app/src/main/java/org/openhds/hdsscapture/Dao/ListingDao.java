package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;

@Dao
public interface ListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Listing... listings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Listing listing);

    @Query("DELETE FROM listing")
    void deleteAll();

    @Update
    void update(Listing listing);

    @Query("SELECT * FROM listing WHERE compno=:id")
    Listing retrieve(String id);

    @Query("SELECT * FROM listing WHERE complete=1")
    List<Listing> retrieveToSync();


    @Query("SELECT COUNT(*) FROM listing a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE a.insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.location_uuid,c.compno,c.compextId,c.locationName FROM visit as a INNER JOIN locations c on a.location_uuid=c.uuid" +
            " LEFT JOIN listing as b ON a.location_uuid=b.location_uuid" +
            " where b.location_uuid IS NULL GROUP BY a.location_uuid")
    List<Listing> error();

    @Query("SELECT a.*,c.compextId FROM visit as a INNER JOIN locations c on a.location_uuid=c.uuid" +
            " LEFT JOIN listing as b ON a.location_uuid=b.location_uuid" +
            " where b.location_uuid IS NULL GROUP BY a.location_uuid")
    LiveData<Listing> errors();

}
