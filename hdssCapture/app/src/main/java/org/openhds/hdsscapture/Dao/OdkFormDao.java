package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.odk.OdkForm;

import java.util.List;

@Dao
public interface OdkFormDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OdkForm form);

    @Update
    void update(OdkForm form);

    @Delete
    void delete(OdkForm form);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create (OdkForm data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(OdkForm... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(OdkForm form);

    /**
     * Get all enabled forms
     */
    @Query("SELECT * FROM Form WHERE enabled = 1 ORDER BY formName ASC")
    LiveData<List<OdkForm>> getAllEnabledForms();

    /**
     * Get forms matching individual's criteria
     * Forms with gender = 0 or NULL are considered applicable to all genders
     */
    @Query("SELECT * FROM Form WHERE enabled = 1 " +
            "AND (gender IS NULL OR gender = 0 OR gender = :gender) " +
            "AND (minAge IS NULL OR minAge <= :age) " +
            "AND (maxAge IS NULL OR maxAge >= :age) " +
            "ORDER BY formName ASC")
    LiveData<List<OdkForm>> getFormsForIndividual(Integer gender, Integer age);

    /**
     * Get form by ID (synchronous)
     */
    @Query("SELECT * FROM Form WHERE id = :formId LIMIT 1")
    OdkForm getFormByIdSync(String formId);

    /**
     * Get form by formID field
     */
    @Query("SELECT * FROM Form WHERE formID = :formId LIMIT 1")
    LiveData<OdkForm> getFormByFormId(String formId);

    /**
     * Get all forms (synchronous)
     */
    @Query("SELECT * FROM Form ORDER BY formName ASC")
    List<OdkForm> getAllFormsSync();

    /**
     * Delete all forms
     */
    @Query("DELETE FROM Form")
    void deleteAll();

    /**
     * Check if form exists
     */
    @Query("SELECT COUNT(*) FROM Form WHERE formID = :formId")
    int formExists(String formId);

}
