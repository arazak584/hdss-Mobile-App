package org.openhds.hdsscapture.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;

import java.util.Date;
import java.util.List;

@Dao
public interface IndividualDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Individual... individual);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Individual individual);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Individual individual);

    @Update
    int update(Individual s);

    @Update(entity = Individual.class)
    int update(IndividualAmendment s);

    @Update
    int dthupdate(Individual e);
    @Update(entity = Individual.class)
    int dthupdate(IndividualEnd e);

    @Update(entity = Individual.class)
    int visited(IndividualVisited e);

    @Update(entity = Individual.class)
    int updateres(IndividualResidency e);

    @Query("DELETE FROM individual")
    void deleteAll();

    @Delete
    void Delete(Individual user);

    @Query("SELECT * FROM individual where extId=:id ")
    Individual retrieve(String id);

    @Query("SELECT * FROM individual WHERE complete=1 order by dob")
    List<Individual> retrieveToSync();

    @Query("SELECT * from individual WHERE endType=1 and firstName!='FAKE' and hohID=:id order by dob")
    List<Individual> retrieveByLocationId(String id);

    @Query("SELECT * from individual WHERE endType=2 and firstName!='FAKE' and compno=:id order by dob")
    List<Individual> retrieveReturn(String id);


//    @Query("SELECT a.*,f.extId as houseExtId, compno as compextId, firstName || ' ' || lastName as fullName, b.endType " +
//            "FROM individual AS a " +
//            "INNER JOIN ( " +
//            "   SELECT individual_uuid, MAX(startDate) AS maxStartDate " +
//            "   FROM residency " +
//            "   GROUP BY individual_uuid " +
//            ") AS latest_residency ON a.uuid = latest_residency.individual_uuid " +
//            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid AND b.startDate = latest_residency.maxStartDate " +
//            "INNER JOIN socialgroup as f ON b.socialgroup_uuid=f.uuid " +
//            "INNER JOIN Locations AS c ON b.location_uuid = c.uuid " +
//            "INNER JOIN locationhierarchy as d on c.locationLevel_uuid=d.uuid " +
//            "LEFT JOIN locationhierarchy as e on d.parent_uuid=e.uuid " +
//            "WHERE b.endType != 3 AND firstName != 'FAKE' AND d.name LIKE :id AND (fullName LIKE :searchText OR c.compno LIKE :searchText OR ghanacard LIKE :searchText) " +
//            "ORDER BY f.extId,dob")
//    List<Individual> retrieveBySearch(String id, String searchText);

//    @Query("SELECT * ,firstName || ' ' || lastName as fullName, endType FROM individual " +
//            "WHERE endType != 3 AND firstName != 'FAKE' AND village LIKE :id AND " +
//            "(fullName LIKE :searchText OR compno LIKE :searchText OR ghanacard LIKE :searchText) ORDER BY hohID,dob")
//    List<Individual> retrieveBySearch(String id, String searchText);

    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names, endType FROM individual " +
            "WHERE endType != 3 AND firstName != 'FAKE' " +
            "AND ((:id IS NULL) OR (village LIKE :id)) " +
            "AND (fullName LIKE :searchText OR Names LIKE :searchText OR compno LIKE :searchText OR ghanacard LIKE :searchText) ORDER BY hohID, dob")
    List<Individual> retrieveBySearch(String id, String searchText);


    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names FROM individual " +
            " WHERE endType!=3 AND firstName!='FAKE' " +
            " AND (fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id) ORDER BY dob")
    List<Individual> retrieveBy(String id);

//    @Query("SELECT * FROM individual WHERE uuid IN (SELECT a.uuid FROM individual AS a " +
//            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
//            "INNER JOIN Locations AS c ON b.location_uuid = c.uuid " +
//            "WHERE b.endType = 1 AND gender = 2 AND c.compextId = :id " +
//            "AND strftime('%Y', 'now') - strftime('%Y', datetime(a.dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(a.dob / 1000, 'unixepoch'))) >=(SELECT mother_age from config) " +
//            "UNION " +
//            "SELECT uuid FROM individual WHERE extId = 'UNK') order by dob DESC")
//    List<Individual> retrieveByMother(String id);

    @Query("SELECT * FROM individual WHERE endType = 1 AND gender = 2 AND compno = :id " +
            "AND strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT mother_age from config) " +
            "UNION " +
            "SELECT * FROM individual WHERE extId = 'UNK' order by dob DESC")
    List<Individual> retrieveByMother(String id);

//    @Query("SELECT a.* FROM individual AS a " +
//            "INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
//            " INNER JOIN Locations as c on b.location_uuid=c.uuid " +
//            "WHERE gender = 2 AND b.endType = 1 AND " +
//            "strftime('%Y', 'now') - strftime('%Y', datetime(a.dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(a.dob / 1000, 'unixepoch'))) >=(SELECT mother_age from config) AND " +
//            "(firstName LIKE :id OR lastName LIKE :id OR c.compno LIKE :id)")
//    List<Individual> retrieveByMotherSearch(String id);

    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names FROM individual WHERE gender = 2 AND endType = 1 AND " +
            "strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT mother_age from config) " +
            "AND (fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id)")
    List<Individual> retrieveByMotherSearch(String id);

    @Query("SELECT * FROM individual WHERE endType = 1 AND gender = 1 AND compno = :id AND firstName!='FAKE' " +
            "AND strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT father_age from config) " +
            "UNION " +
            "SELECT * FROM individual WHERE extId = 'UNK' order by dob DESC")
    List<Individual> retrieveByFather(String id);

    @Query("SELECT * FROM individual  WHERE endType!=3 and compno=:id and firstName!='FAKE' and uuid!=:ids order by dob ")
    List<Individual> retrieveDup(String id,String ids);

    @Query("SELECT * FROM individual WHERE endType=1 and gender=1 and compno=:id and firstName!='FAKE' and " +
            " strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT rel_age from config) order by dob")
    List<Individual> retrievePartner(String id);

    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names FROM individual WHERE gender = 1 AND endType = 1 AND firstName!='FAKE' AND " +
            "strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT father_age from config) AND " +
            "(fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id)")
    List<Individual> retrieveByFatherSearch(String id);

    @Query("SELECT * FROM individual where uuid=:id")
    Individual find(String id);

    @Query("SELECT * FROM individual WHERE endType=1 and compno=:id and firstName!='FAKE' and " +
            " strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT hoh_age from config) order by dob")
    List<Individual> retrieveHOH(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup as d on b.socialgroup_uuid=d.uuid " +
            " WHERE b.endType=1 and d.extId=:id and firstName!='FAKE' and " +
            " date('now', '-5 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveChild(String id);

    @Query("SELECT COUNT(*) FROM individual a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND a.firstName!='FAKE'")
    long countIndividuals(Date startDate, Date endDate, String username);

    @Query("SELECT * FROM individual WHERE hohID=:id AND firstName='FAKE' AND endType=1")
    Individual unk(String id);

    @Query("SELECT b.uuid,b.firstName,b.lastName,a.insertDate,a.socialgroup_uuid,a.extId  FROM death as a INNER JOIN individual as b on a.individual_uuid=b.uuid WHERE socialgroup_uuid=:id")
    List<Individual> retrieveDth(String id);

//    @Query("SELECT a.*,d.compextId,b.extId as houseExtId FROM individual as a " + "INNER JOIN socialgroup as b ON a.uuid = b.individual_uuid " +
//            " INNER JOIN residency c on b.uuid=c.socialgroup_uuid INNER JOIN locations d " +
//            " ON c.location_uuid=d.uuid " +
//            " WHERE firstName!='FAKE' and groupName!='UNK' and c.endType=1 and " +
//            " strftime('%Y', 'now') - strftime('%Y', datetime(a.dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(a.dob / 1000, 'unixepoch'))) < (SELECT hoh_age from config) GROUP BY b.extId order by dob")
//    List<Individual> error();

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN socialgroup as b ON a.uuid = b.individual_uuid " +
            " WHERE firstName!='FAKE' and endType=1 and " +
            " strftime('%Y', 'now') - strftime('%Y', datetime(a.dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(a.dob / 1000, 'unixepoch'))) < (SELECT hoh_age from config) GROUP BY b.extId order by dob")
    List<Individual> error();

    @Query("SELECT a.*,d.compextId,c.extId as houseExtId,c.groupName as lastName FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup c on b.socialgroup_uuid=c.uuid INNER JOIN locations d " +
            " ON b.location_uuid=d.uuid " +
            " WHERE firstName!='FAKE' and groupName='UNK' and b.endType=1 " +
            " GROUP BY c.extId")
    List<Individual> err();

//    @Query("SELECT * FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
//            " INNER JOIN locations c on b.location_uuid=c.uuid " +
//            " WHERE firstName!='FAKE' and " +
//            " date('now', '-14 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
//    List<Individual> errors();

//    @Query("SELECT a.*,c.compextId,d.extId as houseExtId,groupName as lastName FROM individual AS a INNER JOIN residency AS b ON a.uuid = b.individual_uuid " +
//            " INNER JOIN locations c on b.location_uuid=c.uuid " +
//            " INNER JOIN socialgroup d on b.socialgroup_uuid = d.uuid " +
//            "WHERE a.firstName != 'FAKE' AND b.endType = 1 " +
//            "AND b.socialgroup_uuid IN ( " +
//            "    SELECT b2.socialgroup_uuid " +
//            "    FROM individual AS a2 " +
//            "    INNER JOIN residency AS b2 ON a2.uuid = b2.individual_uuid " +
//            "    WHERE b2.endType = 1 " +
//            "    GROUP BY b2.socialgroup_uuid " +
//            "    HAVING MAX(STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(a2.dob/1000, 'unixepoch'))) < 14" +
//            ") GROUP BY socialgroup_uuid " +
//            "ORDER BY c.compextId")
//    List<Individual> errors();

    @Query("SELECT a.* FROM individual AS a WHERE a.firstName != 'FAKE' AND endType = 1 " +
            "AND hohID IN ( " +
            "    SELECT hohID FROM individual WHERE endType = 1 GROUP BY hohID " +
            "    HAVING MAX(STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(dob/1000, 'unixepoch'))) < (SELECT hoh_age from config)" +
            ") GROUP BY hohID " +
            "ORDER BY compno")
    List<Individual> errors();


    @Query("SELECT b.* FROM individual as a LEFT JOIN individual as b on a.mother_uuid=b.uuid" +
            " where a.uuid=:id")
    Individual mother(String id);

    @Query("SELECT b.* FROM individual as a LEFT JOIN individual as b on a.father_uuid=b.uuid" +
            " where a.uuid=:id")
    Individual father(String id);

    @Query("SELECT * FROM individual WHERE insertDate > (SELECT startDate FROM round LIMIT 1) order by insertDate DESC")
    List<Individual> repo();

    @Query("SELECT * FROM individual Where uuid=:id and complete IS NULL ")
    Individual visited(String id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Individual> individuals);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Individual... individuals);

}
