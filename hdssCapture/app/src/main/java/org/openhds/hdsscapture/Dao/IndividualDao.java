package org.openhds.hdsscapture.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.openhds.hdsscapture.Views.CompletedForm;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualPhone;
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
    int contact(IndividualPhone e);

    @Update(entity = Individual.class)
    int updateres(IndividualResidency e);

    @Query("DELETE FROM individual")
    void deleteAll();

    @Delete
    void Delete(Individual user);

    @Query("SELECT * FROM individual where extId=:id ")
    Individual retrieve(String id);

    @Query("SELECT * FROM individual")
    List<Individual> find();

    @Query("SELECT * FROM individual WHERE " +
            "(:gender = 3 OR gender = :gender) AND " +
            "strftime('%Y', 'now') - strftime('%Y', dob) BETWEEN :minAge AND :maxAge AND " +
            "endType = :status " +
            "ORDER BY extId ASC LIMIT :limit OFFSET :offset")
    List<Individual> getIndividualsBatch(int gender, int minAge, int maxAge, int status, int limit, int offset);

    @Query("SELECT a.* FROM individual a INNER JOIN registry b on a.uuid=b.individual_uuid where a.uuid=:id ")
    Individual mapregistry(String id);

    @Query("SELECT * FROM individual WHERE complete=1 order by dob")
    List<Individual> retrieveToSync();

//    @Query("SELECT * FROM individual WHERE insertDate BETWEEN 1748121600000 AND 1749427200000 AND firstName!='FAKE' order by dob")
//    List<Individual> retrieveToSync();

    @Query("SELECT * FROM individual where compno=:comp AND hohID=:id ")
    List<Individual> hoh(String comp,String id);

    @Query("SELECT uuid,extId,firstName,lastName,dob,gender,compno,ghanacard,otherName,phone1,hohID from individual WHERE endType=1 and firstName!='FAKE' and hohID=:id order by dob")
    List<Individual> retrieveByLocationId(String id);

    @Query("SELECT uuid, extId, firstName, lastName, dob, gender, compno, ghanacard, otherName, phone1, hohID,complete FROM individual WHERE endType=1 AND firstName!='FAKE' AND hohID=:id AND (deleted IS NULL OR deleted = 0) ORDER BY dob")
    LiveData<List<Individual>> retrieveByHouseId(String id);

    @Query("SELECT * from individual WHERE endType=2 and firstName!='FAKE' and compno=:id order by dob")
    List<Individual> retrieveReturn(String id);

    @Query("UPDATE individual SET compno = :newCompno WHERE compno = :oldCompno")
    int updateCompnoForIndividuals(String oldCompno, String newCompno);


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

    //Search Individual
    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names, endType FROM individual " +
            "WHERE endType != 3 AND firstName != 'FAKE' " +
            "AND ((:id IS NULL) OR (village LIKE :id)) " +
            "AND (fullName LIKE :searchText OR Names LIKE :searchText OR compno LIKE :searchText OR ghanacard LIKE :searchText OR phone1 LIKE :searchText) ORDER BY dob")
    List<Individual> retrieveBySearch(String id, String searchText);


    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names FROM individual " +
            " WHERE endType!=3 AND firstName!='FAKE' " +
            " AND (fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id OR phone1 LIKE :id) ORDER BY dob")
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
            "AND (fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id OR phone1 LIKE :id)")
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

//    @Query("SELECT a.uuid,a.compno,a.dob,a.firstName,a.lastName,b.uuid as hohID,a.extId FROM individual as a INNER JOIN locations as b ON a.compno=b.compno WHERE endType=1 and gender=1 and a.compno=:id and firstName!='FAKE' and " +
//            " strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT rel_age from config) order by dob")
//    List<Individual> retrievePartner(String id);

    @Query("SELECT *, firstName || ' ' || lastName as fullName, lastName || ' ' ||  firstName as Names FROM individual WHERE gender = 1 AND endType = 1 AND firstName!='FAKE' AND " +
            "strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT father_age from config) AND " +
            "(fullName LIKE :id OR Names LIKE :id OR compno LIKE :id OR ghanacard LIKE :id OR phone1 LIKE :id)")
    List<Individual> retrieveByFatherSearch(String id);

    @Query("SELECT * FROM individual where uuid=:id")
    Individual find(String id);

    @Query("SELECT * FROM individual where uuid=:id AND endType=3")
    Individual restore(String id);

    @Query("SELECT * FROM individual WHERE endType=1 and compno=:id and firstName!='FAKE' and " +
            " strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) >=(SELECT hoh_age from config) order by dob")
    List<Individual> retrieveHOH(String id);

    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
            " INNER JOIN socialgroup as d on b.socialgroup_uuid=d.uuid " +
            " WHERE b.endType=1 and d.extId=:id and firstName!='FAKE' and " +
            " date('now', '-5 years') <= date(strftime('%Y-%m-%d', a.dob/1000, 'unixepoch')) order by dob")
    List<Individual> retrieveChild(String id);

    @Query("SELECT * FROM individual WHERE endType=1 and hohID=:id and firstName!='FAKE'  order by dob")
    List<Individual> morbidity(String id);

    @Query("SELECT COUNT(*) FROM individual a INNER JOIN fieldworker b on a.fw_uuid=b.fw_uuid" +
            " WHERE insertDate BETWEEN :startDate AND :endDate AND b.username = :username AND a.firstName!='FAKE'")
    long countIndividuals(Date startDate, Date endDate, String username);

    @Query("SELECT COUNT(DISTINCT hohID) FROM individual as a LEFT JOIN visit as b on a.hohID=b.houseExtId " +
            "WHERE village=:id AND b.houseExtId IS NULL AND endType=1")
    long counts(String id);

    @Query("SELECT * FROM individual WHERE hohID=:id AND firstName='FAKE' AND endType=1")
    Individual unk(String id);

    @Query("SELECT b.uuid, b.firstName, b.lastName, a.insertDate, a.socialgroup_uuid, b.extId " +
            "FROM death as a " +
            "INNER JOIN individual as b ON a.individual_uuid = b.uuid " +
            "WHERE (a.edit IS NULL OR a.edit = 1) AND endType = 3 AND b.compno = :id GROUP BY b.extId")
    List<Individual> retrieveDth(String id);

    @Query("SELECT b.uuid,b.firstName,b.lastName,a.insertDate,b.hohID socialgroup_uuid,b.extId,location_uuid,residency_uuid" +
            " FROM outmigration as a " +
            "INNER JOIN individual as b on a.individual_uuid=b.uuid " +
            "WHERE (a.edit IS NULL OR a.edit = 1) AND endType =2 AND compno =:loc ORDER BY a.recordedDate DESC LIMIT 1")
    List<Individual> retrieveOmg(String loc);

    //(a.edit IS NULL OR a.edit = 1)
    //a.edit NOT IN (2)
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

//    @Query("SELECT a.* FROM individual as a " + "INNER JOIN residency as b ON a.uuid = b.individual_uuid " +
//            " INNER JOIN socialgroup c on b.socialgroup_uuid=c.uuid INNER JOIN locations d " +
//            " ON b.location_uuid=d.uuid " +
//            " WHERE firstName!='FAKE' and groupName='UNK' and b.endType=1 " +
//            " GROUP BY c.extId")
//    List<Individual> err();

    @Query("SELECT a.* FROM individual as a INNER JOIN socialgroup b on a.hohID=b.extId " +
            " WHERE firstName!='FAKE' and groupName='UNK' and endType=1 AND b.fw_uuid =:id" +
            " GROUP BY b.extId")
    List<Individual> errz(String id);

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

    @Query("SELECT * FROM individual WHERE firstName!='FAKE' AND substr(extId, 1, 4) = 'null' AND fw_uuid= :id ")
    List<Individual> nulls(String id);

    @Query("SELECT a.uuid,b.compno as hohID,a.compno,a.firstName || ' ' || a.lastName as firstName,b.firstName || ' ' || b.lastName as  lastName FROM individual as a INNER JOIN individual b ON a.mother_uuid=b.uuid WHERE " +
            " STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(a.dob/1000, 'unixepoch')) < (SELECT hoh_age from config) AND a.endType=1 AND b.endType=1" +
            " AND b.compno = :id AND b.hohID =:ids AND a.compno != :id")
    List<Individual> minors(String id,String ids);

    @Query("SELECT COUNT(*) FROM individual WHERE hohID = :id AND compno = :ids AND endType=1 AND firstName!='FAKE'")
    long count(String id,String ids);

    @Query("SELECT COUNT(hohID) FROM individual AS a WHERE a.firstName != 'FAKE' AND a.endType = 1 AND a.hohID = :id AND a.compno = :ids " +
            "AND a.hohID IN ( " +
            "    SELECT hohID FROM individual WHERE endType = 1 " +
            "    GROUP BY hohID " +
            "    HAVING MAX(STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(dob / 1000, 'unixepoch'))) < (SELECT hoh_age FROM config) " +
            ")")
    long err(String id, String ids);

//    @Query("SELECT COUNT(DISTINCT a.hohID) FROM individual as a INNER JOIN socialgroup as b ON a.uuid = b.individual_uuid " +
//            "WHERE firstName != 'FAKE' AND endType = 1 AND a.hohID = :id AND a.compno = :ids " +
//            "AND STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(a.dob / 1000, 'unixepoch')) < (SELECT hoh_age FROM config)")
//    Long errs(String id, String ids);

    @Query("SELECT COUNT(DISTINCT a.extId) FROM socialgroup as a INNER JOIN individual as b on a.individual_uuid=b.uuid " +
            "where a.extId=:id AND b.compno = :ids and endType=1 and b.firstName!='FAKE' and " +
            "strftime('%Y', 'now') - strftime('%Y', datetime(dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(dob / 1000, 'unixepoch'))) <(SELECT hoh_age from config)")
    long errs(String id, String ids);

    @Query("SELECT COUNT(*) FROM individual as a INNER JOIN socialgroup as b ON a.uuid = b.individual_uuid " +
            " WHERE firstName!='FAKE' and endType=1 and " +
            " strftime('%Y', 'now') - strftime('%Y', datetime(a.dob / 1000, 'unixepoch')) - (strftime('%m-%d', 'now') < strftime('%m-%d', datetime(a.dob / 1000, 'unixepoch'))) < (SELECT hoh_age from config) GROUP BY b.extId order by dob")
    long cnt();

    @Query("SELECT COUNT(*) FROM individual as a INNER JOIN socialgroup b on a.hohID=b.extId " +
            " WHERE firstName!='FAKE' and groupName='UNK' and endType=1 " +
            " GROUP BY b.extId")
    long cnts();

    @Query("SELECT a.* FROM individual AS a WHERE a.firstName != 'FAKE' AND endType = 1 " +
            "AND hohID IN ( " +
            "    SELECT hohID FROM individual WHERE endType = 1 GROUP BY hohID " +
            "    HAVING MAX(STRFTIME('%Y', 'now') - STRFTIME('%Y', DATE(dob/1000, 'unixepoch'))) < (SELECT hoh_age from config)" +
            ") GROUP BY hohID " +
            "ORDER BY compno")
    long cntss();

//    @Query("SELECT * FROM individual WHERE uuid != :currentUuid AND (ghanacard = :ghanacard OR phone1 = :phone)")
//    List<Individual> dupRegistration(String currentUuid, String ghanacard, String phone);

    @Query("SELECT * FROM individual WHERE uuid != :currentUuid AND ((:ghanacard IS NOT NULL AND ghanacard = :ghanacard) OR " +
        " (:phone IS NOT NULL AND phone1 = :phone) ) ")
    List<Individual> dupRegistration(String currentUuid, String ghanacard, String phone);


    @Query("SELECT * FROM individual WHERE uuid != :currentUuid AND phone1 = :phone AND phone1 IS NOT NULL AND phone1 != ''")
    List<Individual> findDuplicatesByPhone(String currentUuid, String phone);

    @Query("SELECT uuid, 'Individual' AS formType, insertDate, firstName || ' ' || lastName as fullName FROM individual WHERE complete = 1 " +
            " AND insertDate >= (SELECT startDate from round ORDER BY roundNumber DESC limit 1) ORDER BY insertDate DESC")
    List<CompletedForm> getCompletedForms();
    @Query("SELECT * FROM individual where uuid=:id")
    LiveData<Individual> getView(String id);

    @Query("SELECT COUNT(*) FROM individual WHERE complete= 1")
    LiveData<Long> sync();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Individual> individuals);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Individual... individuals);

}
