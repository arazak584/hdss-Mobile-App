package org.openhds.hdsscapture.Dao;


import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
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

    @GET("hierarchy")
    Call<DataWrapper<Hierarchy>> getAllHierarchy();

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
    Call<DataWrapper<Locations>> sendLocationdata(@Body DataWrapper<Locations> dataModal);

    @POST("visit")
    Call<DataWrapper<Visit>> sendVisitdata(@Body DataWrapper<Visit> dataModal);

    @POST("individual")
    Call<DataWrapper<Individual>> sendIndividualdata(@Body DataWrapper<Individual> dataModal);

    @POST("residency")
    Call<DataWrapper<Residency>> sendResidencydata(@Body DataWrapper<Residency> dataModal);

    @POST("socialgroup")
    Call<DataWrapper<Socialgroup>> sendSocialgroupdata(@Body DataWrapper<Socialgroup> dataModal);

    @POST("inmigration")
    Call<DataWrapper<Inmigration>> sendInmigrationdata(@Body DataWrapper<Inmigration> dataModal);

    @POST("outmigration")
    Call<DataWrapper<Outmigration>> sendOutmigrationdata(@Body DataWrapper<Outmigration> dataModal);

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
