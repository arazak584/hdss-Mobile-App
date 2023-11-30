package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.odk.Form;

import java.util.List;

@Dao
public interface OdkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Form... data);

    @Query("SELECT * FROM Form")
    List<Form> retrieve();

    @Query("DELETE FROM Form")
    void deleteAll();

    @Query("SELECT * FROM Form")
    List<Form> find();

}
