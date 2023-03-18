package org.openhds.hdsscapture.Dao;


import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Country;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.District;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Region;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Subdistrict;
import org.openhds.hdsscapture.entity.Village;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiDao {

    @GET("codebook")
    Call<DataWrapper<CodeBook>> getCodeBook();

    @GET("round")
    Call<DataWrapper<Round>> getRound();

    @GET("fieldworker")
    Call<DataWrapper<Fieldworker>> getFw();

    @GET("country")
    Call<DataWrapper<Country>> getAllCountries();

    @GET("region")
    Call<DataWrapper<Region>> getAllRegions();

    @GET("district")
    Call<DataWrapper<District>> getAllDistricts();

    @GET("hierarchy")
    Call<DataWrapper<Hierarchy>> getAllHierarchy();

    @GET("subdistrict")
    Call<DataWrapper<Subdistrict>> getAllSubDistricts();

    @GET("village")
    Call<DataWrapper<Village>> getAllVillages();

    @GET("cluster")
    Call<DataWrapper<Cluster>> getAllClusters();

    @GET("task/location.zip")
    Call<ResponseBody> downloadLocation();

    @GET("task/residency.zip")
    Call<ResponseBody> downloadResidency();

    @GET("task/socialgroup.zip")
    Call<ResponseBody> downloadSocialgroup();

    @GET("task/relationship.zip")
    Call<ResponseBody> downloadRelationship();

    @GET("task/pregnancy.zip")
    Call<ResponseBody> downloadPregnancy();

    @GET("task/individual.zip")
    Call<ResponseBody> downloadZipFile();

    @GET("task/demography.zip")
    Call<ResponseBody> downloadDemography();

    @POST("location")
    Call<DataWrapper<Location>> sendLocationdata(@Body DataWrapper<Location> dataModal);

    @POST("visit")
    Call<DataWrapper<Visit>> sendVisitdata(@Body DataWrapper<Visit> dataModal);

    @POST("individual")
    Call<DataWrapper<Individual>> sendIndividualdata(@Body DataWrapper<Individual> dataModal);

    @POST("residency")
    Call<DataWrapper<Residency>> sendResidencydata(@Body DataWrapper<Residency> dataModal);

    @POST("socialgroup")
    Call<DataWrapper<Socialgroup>> sendSocialgroupdata(@Body DataWrapper<Socialgroup> dataModal);

    @POST("inmigration")
    Call<DataWrapper<Residency>> sendInmigrationdata(@Body DataWrapper<Residency> dataModal);

    @POST("outmigration")
    Call<DataWrapper<Residency>> sendOutmigrationdata(@Body DataWrapper<Residency> dataModal);

    @POST("death")
    Call<DataWrapper<Death>> sendDeathdata(@Body DataWrapper<Death> dataModal);

    @POST("vpm")
    Call<DataWrapper<Death>> sendVpmdata(@Body DataWrapper<Death> dataModal);

    @POST("pregnancy")
    Call<DataWrapper<Pregnancy>> sendPregnancydata(@Body DataWrapper<Pregnancy> dataModal);

    @POST("relationship")
    Call<DataWrapper<Relationship>> sendRelationshipdata(@Body DataWrapper<Relationship> dataModal);

    @POST("outcome")
    Call<DataWrapper<Pregnancyoutcome>> sendPregoutcomedata(@Body DataWrapper<Pregnancyoutcome> dataModal);


}
