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

    @Query("SELECT * FROM individual WHERE complete=1 order by dob")
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
    List<Individual> retrieveBy(String id);

    @Query("SELECT a.*, compno, c.compextId, firstName || ' ' || lastName as fullName, b.endType " +
            "FROM individual AS a " +
            "INNER JOIN ( " +
            "   SELECT individual_uuid, MAX(startDate) AS maxStartDate " +
            "   FROM residency " +
            "   GROUP BY individual_uuid " +
            ") AS latest_residency ON a.uuid = latest_residency.individual_uuid " +
            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid AND b.startDate = latest_residency.maxStartDate " +
            "INNER JOIN Locations AS c ON b.location_uuid = c.uuid " +
            "INNER JOIN locationhierarchy as d on c.locationLevel_uuid=d.uuid " +
            "LEFT JOIN locationhierarchy as e on d.parent_uuid=e.uuid " +
            "WHERE b.endType != 3 AND firstName != 'FAKE' AND d.name LIKE :id AND (fullName LIKE :searchText OR c.compno LIKE :searchText OR ghanacard LIKE :searchText) " +
            "ORDER BY dob")
    List<Individual> retrieveBySearch(String id, String searchText);


    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and gender=2 and c.compextId=:id and " +
            " date('now', '-12 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByMother(String id);

    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            "WHERE gender = 2 AND b.endType = 1 AND " +
            "date('now', '-12 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
            "(firstName LIKE :id OR lastName LIKE :id OR c.compno LIKE :id)")
    List<Individual> retrieveByMotherSearch(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType=1 and gender=1 and c.compextId=:id and firstName!='FAKE' and " +
            " date('now', '-12 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveByFather(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            " WHERE b.endType!=3 and c.compextId=:id and firstName!='FAKE' order by dob ")
    List<Individual> retrieveDup(String id);


    @Query("SELECT a.* FROM individual AS a " +
            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
            "WHERE gender = 1 AND b.endType = 1 and firstName!='FAKE' AND " +
            "date('now', '-12 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) AND " +
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
            " date('now', '-14 years') >= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveHOH(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup as d on b.socialgroup_uuid=d.uuid " +
            " WHERE b.endType=1 and d.extId=:id and firstName!='FAKE' and " +
            " date('now', '-5 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveChild(String id);

    @Query("SELECT COUNT(*) FROM individual a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND a.firstName!='FAKE'")
    long countIndividuals(Date startDate, Date endDate, String username);

//    @Query("SELECT * FROM sociodemo as a INNER JOIN socialgroup as b ON a.socialgroup_uuid=b.uuid " +
//            " INNER JOIN residency as c on b.uuid=c.socialgroup_uuid " +
//            " INNER JOIN individual as d on c.individual_uuid=d.uuid " +
//            "where c.endType=1 and a.complete=2 AND d.firstName!='FAKE' AND compextId is not null GROUP BY a.socialgroup_uuid order by dob")
//    List<Individual> error();

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN socialgroup as b ON a.uuid = b.individual_uuid " +
            " WHERE firstName!='FAKE' and groupName!='UNK' and " +
            " date('now', '-14 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> error();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Individual> individuals);

}
