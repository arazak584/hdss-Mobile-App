package org.openhds.hdsscapture.Dao;


import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.odk.Form;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiDao {

    @GET("/api/codebook")
    Call<DataWrapper<CodeBook>> getCodeBook(@Header("Authorization") String authorizationHeader);

    @GET("/api/round")
    Call<DataWrapper<Round>> getRound(@Header("Authorization") String authorizationHeader);

    @GET("/api/community")
    Call<DataWrapper<CommunityReport>> getCommunity(@Header("Authorization") String authorizationHeader);

    @GET("/api/visit")
    Call<DataWrapper<Visit>> getVisit(@Header("Authorization") String authorizationHeader);

    @GET("/api/settings/parameter")
    Call<DataWrapper<Configsettings>> getConfig(@Header("Authorization") String authorizationHeader);

    @GET("/api/odk")
    Call<DataWrapper<Form>> getOdk(@Header("Authorization") String authorizationHeader);

    @GET("/api/fieldworker")
    Call<DataWrapper<Fieldworker>> getFw(@Header("Authorization") String credentials);

    @GET("/api/hierarchy/all")
    Call<DataWrapper<Hierarchy>> getAllHierarchy(@Header("Authorization") String authorizationHeader);

    @GET("/api/hierarchylevel")
    Call<DataWrapper<HierarchyLevel>> getHierarchyLevel(@Header("Authorization") String authorizationHeader);

    //Rejection API
    @GET("/api/inmigration/reject")
    Call<DataWrapper<Inmigration>> getImg(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/outmigration/reject")
    Call<DataWrapper<Outmigration>> getOmg(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/outcome/reject")
    Call<DataWrapper<Pregnancyoutcome>> getOut(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/relationship/reject")
    Call<DataWrapper<Relationship>> getRel(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);

    @GET("/api/death/reject")
    Call<DataWrapper<Death>> getDth(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/pregnancy/reject")
    Call<DataWrapper<Pregnancy>> getPreg(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/demographic/reject")
    Call<DataWrapper<Demographic>> getDemo(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/vac/reject")
    Call<DataWrapper<Vaccination>> getVac(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);
    @GET("/api/ses/reject")
    Call<DataWrapper<HdssSociodemo>> getSes(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);

    @GET("/api/mor/reject")
    Call<DataWrapper<Morbidity>> getMor(@Header("Authorization") String authorizationHeader, @Query("fw") String fw);

    @GET("/api/zip/location")
    Call<ResponseBody> downloadLocation(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/residency")
    Call<ResponseBody> downloadResidency(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/socialgroup")
    Call<ResponseBody> downloadSocialgroup(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/relationship")
    Call<ResponseBody> downloadRelationship(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/pregnancy")
    Call<ResponseBody> downloadPregnancy(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/individual")
    Call<ResponseBody> downloadIndividual(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/demographics")
    Call<ResponseBody> downloadDemography(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/ses")
    Call<ResponseBody> downloadSes(@Header("Authorization") String authorizationHeader);

    @GET("/api/zip/vaccination")
    Call<ResponseBody> downloadVaccination(@Header("Authorization") String authorizationHeader);

    @POST("/api/location")
    Call<DataWrapper<Locations>> sendLocationdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Locations> dataModal);

    @POST("/api/visit")
    Call<DataWrapper<Visit>> sendVisitdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Visit> dataModal);

    @POST("/api/individual")
    Call<DataWrapper<Individual>> sendIndividualdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Individual> dataModal);

    @POST("/api/residency")
    Call<DataWrapper<Residency>> sendResidencydata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Residency> dataModal);

    @POST("/api/socialgroup")
    Call<DataWrapper<Socialgroup>> sendSocialgroupdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Socialgroup> dataModal);

    @POST("/api/inmigration")
    Call<DataWrapper<Inmigration>> sendInmigrationdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Inmigration> dataModal);

    @POST("/api/outmigration")
    Call<DataWrapper<Outmigration>> sendOutmigrationdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Outmigration> dataModal);

    @POST("/api/death")
    Call<DataWrapper<Death>> sendDeathdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Death> dataModal);

    @POST("/api/vpm")
    Call<DataWrapper<Vpm>> sendVpmdata(@Header("Authorization") String authorizationHeader, @Body DataWrapper<Vpm> dataModal);

    @POST("/api/pregnancy")
    Call<DataWrapper<Pregnancy>> sendPregnancydata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Pregnancy> dataModal);

    @POST("/api/relationship")
    Call<DataWrapper<Relationship>> sendRelationshipdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Relationship> dataModal);

    @POST("/api/pregoutcome")
    Call<DataWrapper<Pregnancyoutcome>> sendPregoutcomedata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Pregnancyoutcome> dataModal);

    @POST("/api/outcome")
    Call<DataWrapper<Outcome>> sendOutcomedata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Outcome> dataModal);

    @POST("/api/socio")
    Call<DataWrapper<HdssSociodemo>> sendSociodata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<HdssSociodemo> dataModal);

    @POST("/api/demographic")
    Call<DataWrapper<Demographic>> sendDemographicdata(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Demographic> dataModal);

    @POST("/api/listing")
    Call<DataWrapper<Listing>> sendListing(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Listing> dataModal);

    @POST("/api/amendment")
    Call<DataWrapper<Amendment>> sendAmendment(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Amendment> dataModal);

    @POST("/api/vaccination")
    Call<DataWrapper<Vaccination>> sendVaccination(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Vaccination> dataModal);

    @POST("/api/duplicate")
    Call<DataWrapper<Duplicate>> sendDup(@Header("Authorization") String authorizationHeader,@Body DataWrapper<Duplicate> dataModal);

    @POST("/api/community")
    Call<DataWrapper<CommunityReport>> sendCommunity(@Header("Authorization") String authorizationHeader,@Body DataWrapper<CommunityReport> dataModal);

    @POST("/api/morbidity")
    Call<DataWrapper<Morbidity>> sendMorbidity(@Header("Authorization") String authorizationHeader, @Body DataWrapper<Morbidity> dataModal);

    @POST("/api/registry")
    Call<DataWrapper<Registry>> sendRegistry(@Header("Authorization") String authorizationHeader, @Body DataWrapper<Registry> dataModal);

}
