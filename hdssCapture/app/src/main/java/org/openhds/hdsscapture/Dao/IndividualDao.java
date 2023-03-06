package org.openhds.hdsscapture.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;

import java.util.List;

@Dao
public interface IndividualDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Individual... individual);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Individual individual);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Individual individual);

    @Update
    void update(Individual individual);

    @Update(entity = Individual.class)
    int update(IndividualAmendment individualAmendment);

    @Delete
    void Delete(Individual user);


    //@Query("SELECT individual.* FROM individual " + " INNER JOIN residency ON individual.extid = residency.individual_extid" +
    //" WHERE residency.endtype='NA' ")
    //Individual retrieve(String id);

    @Query("SELECT * FROM individual")
    List<Individual> retrieve();

    @Query("SELECT * FROM individual WHERE complete=1")
    List<Individual> retrieveToSync();

    @Query("SELECT a.*,b.location,b.socialgroup FROM individual as a " + "INNER JOIN residency as b ON a.extId = b.extId " +
            " INNER JOIN location as c on b.location=c.extId " +
            " WHERE endType=1 and b.location=:id order by socialgroup")
    List<Individual> retrieveByLocationId(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.extId = b.extId" +
            " WHERE firstName LIKE:id OR lastName LIKE:id OR b.compno LIKE:id")
    List<Individual> retrieveBySearch(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.extId = b.extId " +
            " INNER JOIN location as c on b.location=c.extId " +
            " WHERE endType=1 and gender=2 and b.location=:id and " +
            " date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByMother(String id);

    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.extId = b.extId " +
            "WHERE gender = 2 AND endType = 1 AND " +
            "date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
            "(firstName LIKE :id OR lastName LIKE :id OR b.compno LIKE :id)")
    List<Individual> retrieveByMotherSearch(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.extId = b.extId " +
            " INNER JOIN location as c on b.location=c.extId " +
            " WHERE endType=1 and gender=1 and b.location=:id and " +
            " date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByFather(String id);


    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.extId = b.extId " +
            "WHERE gender = 1 AND endType = 1 AND " +
            "date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
            "(firstName LIKE :id OR lastName LIKE :id OR b.compno LIKE :id)")
    List<Individual> retrieveByFatherSearch(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Individual> individuals);

}
