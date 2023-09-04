package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.util.Date;
import java.util.List;

@Dao
public interface
PregnancyoutcomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Pregnancyoutcome pregnancyoutcome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancyoutcome... pregnancyoutcome);

    @Query("DELETE FROM pregnancyoutcome")
    void deleteAll();

    @Query("SELECT * FROM pregnancyoutcome WHERE complete=1")
    List<Pregnancyoutcome> retrieveToSync();

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id ORDER BY outcomeDate ASC LIMIT 1")
    Pregnancyoutcome find(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id AND id=2")
    Pregnancyoutcome finds(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id and extra=1")
    Pregnancyoutcome findout(String id);

    @Query("SELECT * FROM pregnancyoutcome where mother_uuid=:id ORDER BY outcomeDate DESC LIMIT 1")
    Pregnancyoutcome findpreg(String id);

    @Query("SELECT COUNT(*) FROM pregnancyoutcome a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);
}
