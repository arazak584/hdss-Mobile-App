package org.openhds.hdsscapture.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.Date;

@Dao
public interface ReportDao {

    @Query("SELECT COUNT(*) FROM visit WHERE insertDate BETWEEN :startDate AND :endDate")
    LiveData<Integer> countVisits(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM death WHERE insertDate BETWEEN :startDate AND :endDate")
    LiveData<Integer> countDeaths(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM socialgroup WHERE insertDate BETWEEN :startDate AND :endDate")
    LiveData<Integer> countSocialgroups(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM individual WHERE insertDate BETWEEN :startDate AND :endDate")
    LiveData<Integer> countIndividuals(Date startDate, Date endDate);

}
