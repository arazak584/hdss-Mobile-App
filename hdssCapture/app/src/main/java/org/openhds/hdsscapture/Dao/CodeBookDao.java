package org.openhds.hdsscapture.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.List;

@Dao
public interface CodeBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(CodeBook... data);

    @Query("SELECT * FROM CodeBook")
    List<CodeBook> retrieve();

    @Query("SELECT codeValue,codeLabel FROM CodeBook WHERE codeFeature=:codeFeature")
    List<KeyValuePair> retrieveCodesOfFeature(String codeFeature);

}
