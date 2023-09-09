package org.openhds.hdsscapture.Dao;

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

    @Query("SELECT a.* FROM locations as a LEFT JOIN listing as b ON a.compno=b.compno" +
            " where b.compno IS NULL AND a.complete IS NOT NULL ")
    List<Listing> error();

}
