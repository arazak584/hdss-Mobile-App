package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Demographic;

import java.util.Date;
import java.util.List;

@Dao
public interface AmendmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (Amendment amendment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Amendment... amendment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Amendment> amendment);


    @Query("DELETE FROM amendment")
    void deleteAll();

    @Query("SELECT * FROM amendment")
    List<Amendment> getAll();

    @Query("SELECT * FROM amendment")
    List<Amendment> retrieve();

    @Query("SELECT * FROM amendment WHERE complete=1")
    List<Amendment> retrieveSync();

    @Query("SELECT * FROM amendment where individual_uuid=:id")
    Amendment find(String id);

    @Query("SELECT COUNT(*) FROM amendment a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username")
    long count(Date startDate, Date endDate, String username);

    @Query("SELECT a.uuid, 'Amendment' AS formType, a.insertDate, b.firstName || ' ' || b.lastName as fullName FROM amendment as a inner join individual as b ON a.individual_uuid=b.uuid WHERE a.complete = 1 ORDER BY a.insertDate DESC")
    List<CompletedForm> getCompletedForms();

    @Query("SELECT * FROM amendment where uuid=:id")
    LiveData<Amendment> getView(String id);
}
