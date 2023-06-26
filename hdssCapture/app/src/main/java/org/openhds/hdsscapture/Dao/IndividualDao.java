package org.openhds.hdsscapture.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;

import java.util.Date;
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
    int update(Individual s);

    @Update(entity = Individual.class)
    int update(IndividualAmendment s);

    @Query("DELETE FROM individual")
    void deleteAll();

    @Delete
    void Delete(Individual user);

    @Query("SELECT * FROM individual where extId =:id ")
    Individual retrieve(String id);

    @Query("SELECT * FROM individual WHERE complete=1")
    List<Individual> retrieveToSync();

    @Query("SELECT * FROM individual")
    List<Individual> retrieve();

    @Query("SELECT a.*,d.extId as houseExtId,b.endType FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup as d on b.socialgroup_uuid=d.uuid " +
            " WHERE b.endType=1 and firstName!='FAKE' and d.extId=:id order by dob")
    List<Individual> retrieveByLocationId(String id);

    @Query("SELECT a.*,compno,c.compextId,firstName || ' ' || lastName as fullName,b.endType FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid" +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType!=3 AND firstName!='FAKE' AND  " +
            " ( fullName LIKE:id OR c.compno LIKE:id OR ghanacard LIKE :id) ORDER BY dob ")
    List<Individual> retrieveBySearch(String id);


    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and gender=2 and c.compextId=:id and " +
            " date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByMother(String id);

    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            "WHERE gender = 2 AND b.endType = 1 AND " +
            "date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
            "(firstName LIKE :id OR lastName LIKE :id OR c.compno LIKE :id)")
    List<Individual> retrieveByMotherSearch(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and gender=1 and c.compextId=:id and firstName!='FAKE' and " +
            " date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByFather(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType!=3 and c.compextId=:id and firstName!='FAKE' order by dob ")
    List<Individual> retrieveDup(String id);


    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            "WHERE gender = 1 AND b.endType = 1 and firstName!='FAKE' AND " +
            "date('now', '-11 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
            "(firstName LIKE :id OR lastName LIKE :id OR c.compno LIKE :id OR ghanacard LIKE :id)")
    List<Individual> retrieveByFatherSearch(String id);

    @Query("SELECT uuid,extId,dob,age,gender,firstName,lastName FROM individual " +
            " WHERE uuid=:id")
    LiveData<List<CaseItem>> retrieveByIndividual(String id);

    @Query("SELECT * FROM individual where uuid=:id")
    Individual find(String id);

    @Query("SELECT a.*,c.compextId FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and c.compextId=:id and firstName!='FAKE' and " +
            " date('now', '-13 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveHOH(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup as d on b.socialgroup_uuid=d.uuid " +
            " WHERE b.endType=1 and d.extId=:id and firstName!='FAKE' and " +
            " date('now', '-5 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveChild(String id);

    @Query("SELECT COUNT(*) FROM individual a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND a.firstName!='FAKE'")
    long countIndividuals(Date startDate, Date endDate, String username);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Individual> individuals);

}
