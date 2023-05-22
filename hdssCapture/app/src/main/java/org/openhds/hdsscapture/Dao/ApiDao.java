package org.openhds.hdsscapture.Dao;


import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
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

    @GET("/api/codebook")
    Call<DataWrapper<CodeBook>> getCodeBook();

    @GET("/api/round")
    Call<DataWrapper<Round>> getRound();

    @GET("/api/fieldworker")
    Call<DataWrapper<Fieldworker>> getFw();

    @GET("/api/hierarchy")
    Call<DataWrapper<Hierarchy>> getAllHierarchy();

    @GET("/api/zip/location")
    Call<ResponseBody> downloadLocation();

    @GET("/api/zip/residency")
    Call<ResponseBody> downloadResidency();

    @GET("/api/zip/socialgroup")
    Call<ResponseBody> downloadSocialgroup();

    @GET("/api/zip/relationship")
    Call<ResponseBody> downloadRelationship();

    @GET("/api/zip/pregnancy")
    Call<ResponseBody> downloadPregnancy();

    @GET("/api/zip/individual")
    Call<ResponseBody> downloadZipFile();

    @GET("/api/zip/demographics")
    Call<ResponseBody> downloadDemography();

    @GET("/api/zip/ses")
    Call<ResponseBody> downloadSes();

    @POST("/api/location")
    Call<DataWrapper<Locations>> sendLocationdata(@Body DataWrapper<Locations> dataModal);

    @POST("/api/visit")
    Call<DataWrapper<Visit>> sendVisitdata(@Body DataWrapper<Visit> dataModal);

    @POST("/api/individual")
    Call<DataWrapper<Individual>> sendIndividualdata(@Body DataWrapper<Individual> dataModal);

    @POST("/api/residency")
    Call<DataWrapper<Residency>> sendResidencydata(@Body DataWrapper<Residency> dataModal);

    @POST("/api/socialgroup")
    Call<DataWrapper<Socialgroup>> sendSocialgroupdata(@Body DataWrapper<Socialgroup> dataModal);

    @POST("/api/inmigration")
    Call<DataWrapper<Inmigration>> sendInmigrationdata(@Body DataWrapper<Inmigration> dataModal);

    @POST("/api/outmigration")
    Call<DataWrapper<Outmigration>> sendOutmigrationdata(@Body DataWrapper<Outmigration> dataModal);

    @POST("/api/death")
    Call<DataWrapper<Death>> sendDeathdata(@Body DataWrapper<Death> dataModal);

    @POST("/api/vpm")
    Call<DataWrapper<Death>> sendVpmdata(@Body DataWrapper<Death> dataModal);

    @POST("/api/pregnancy")
    Call<DataWrapper<Pregnancy>> sendPregnancydata(@Body DataWrapper<Pregnancy> dataModal);

    @POST("/api/relationship")
    Call<DataWrapper<Relationship>> sendRelationshipdata(@Body DataWrapper<Relationship> dataModal);

    @POST("/api/pregoutcome")
    Call<DataWrapper<Pregnancyoutcome>> sendPregoutcomedata(@Body DataWrapper<Pregnancyoutcome> dataModal);

    @POST("/api/outcome")
    Call<DataWrapper<Outcome>> sendOutcomedata(@Body DataWrapper<Outcome> dataModal);

    @POST("/api/socio")
    Call<DataWrapper<HdssSociodemo>> sendSociodata(@Body DataWrapper<HdssSociodemo> dataModal);

    @POST("/api/demographic")
    Call<DataWrapper<Demographic>> sendDemographicdata(@Body DataWrapper<Demographic> dataModal);


}
