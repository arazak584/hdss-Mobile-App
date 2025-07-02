package org.openhds.hdsscapture.Sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Dao.DeathDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.DuplicateDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.InmigrationDao;
import org.openhds.hdsscapture.Dao.MorbidityDao;
import org.openhds.hdsscapture.Dao.OutmigrationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.PregnancyoutcomeDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.VaccinationDao;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadManager extends Worker {

    private static final String TAG = "DownloadManager";
    private final Context context;
    private final ApiDao dao;
    private final ExecutorService executorService;
    private String fw;
    private String authorizationHeader;

    public DownloadManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.dao = AppJson.getInstance(context).getJsonApi();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public Result doWork() {
        // Retrieve fw and authorizationHeader from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        if (authorizationHeader == null || fw == null) {
            Log.e(TAG, "Authorization header or field worker UUID not found in SharedPreferences");
            return Result.failure();
        }
        Log.d("FieldworkerID", "FW ID: " + fw);

        if (!isInternetAvailable()) {
            Log.e(TAG, "No internet connection available");
            return Result.retry();
        }

        executorService.execute(() -> {
            try {
                // Download Inmigration data
                downloadInmigrationData();
                // Download Outmigration data
                downloadOutmigrationData();
                // Download Death data
                downloadDeathData();
                // Download Pregnancy data
                downloadPregnancyData();
                // Download Demographic data
                downloadDemographicData();
                // Download Relationship data
                downloadRelationshipData();
                // Download Vaccination data
                downloadVaccinationData();
                // Download HdssSociodemo data
                downloadHdssSociodemoData();
                // Download Morbidity data
                downloadMorbidityData();
                // Download PregnancyOutcome data
                downloadPregnancyOutcomeData();
                // Download Duplicate data
                downloadDuplicateData();

                Log.d(TAG, "Download process completed successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error during download process: " + e.getMessage());
                e.printStackTrace();
            }
        });

        return Result.success();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void downloadInmigrationData() {
        Call<DataWrapper<Inmigration>> call = dao.getImg(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Inmigration>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Response<DataWrapper<Inmigration>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Inmigration[] inmigrations = response.body().getData().toArray(new Inmigration[0]);
                        InmigrationDao inmigrationDao = AppDatabase.getDatabase(context).inmigrationDao();

                        executorService.execute(() -> {
                            for (Inmigration newInmigration : inmigrations) {
                                Inmigration existingInmigration = inmigrationDao.ins(newInmigration.uuid);

                                //((existingInmigration != null && !(existingInmigration.complete != null && existingInmigration.complete == 1)))
                                if (existingInmigration != null) {
                                    inmigrationDao.create(newInmigration);
                                    Log.d(TAG, "Added or updated Inmigration record with UUID: " + newInmigration.uuid);
                                } else {
                                    Log.d(TAG, "Skipping Inmigration record with UUID: " + newInmigration.uuid);
                                }
                            }
                            Log.d(TAG, "Inmigration data processed successfully");
                        });

                    } else {
                        Log.e(TAG, "Failed to download Inmigration data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Inmigration data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading Inmigration data: " + t.getMessage());
            }
        });
    }



    private void downloadOutmigrationData() {
        Call<DataWrapper<Outmigration>> call = dao.getOmg(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Outmigration>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Response<DataWrapper<Outmigration>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Outmigration[] outmigrations = response.body().getData().toArray(new Outmigration[0]);

                            OutmigrationDao outmigrationDao = AppDatabase.getDatabase(context).outmigrationDao();
                            // Run database operations in a background thread
                        executorService.execute(() -> {
                                for (Outmigration newOutmigration : outmigrations) {
                                    Outmigration existingOutmigration = outmigrationDao.ins(newOutmigration.uuid);

                                    // Ensure existingOutmigration is NOT NULL and complete is NOT 1
                                    //((existingOutmigration != null && !(existingOutmigration.complete != null && existingOutmigration.complete == 1)))
                                    if (existingOutmigration != null ) {
                                        newOutmigration.edit = 1;
                                        outmigrationDao.create(newOutmigration);
                                        Log.d(TAG, "Updated existing Outmigration record with UUID: " + newOutmigration.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping Outmigration record with UUID: " + newOutmigration.uuid);
                                    }
                                }
                                Log.d(TAG, "Outmigration data processed successfully");
                            });

                    } else {
                        Log.e(TAG, "Failed to download Outmigration data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Outmigration data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }

    private void downloadDeathData() {
        Call<DataWrapper<Death>> call = dao.getDth(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Death>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Death>> call, @NonNull Response<DataWrapper<Death>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Death[] deaths = response.body().getData().toArray(new Death[0]);

                            DeathDao deathDao = AppDatabase.getDatabase(context).deathDao();
                            // Run database operations in a background thread
                            executorService.execute(() -> {
                                for (Death newDeath : deaths) {
                                    Death existingDeath = deathDao.ins(newDeath.uuid);

                                    // Ensure existingDeath is NOT NULL and complete is NOT 1
                                    if (existingDeath != null) {
                                        newDeath.complete = 0;
                                        newDeath.edit = 1;
                                        deathDao.create(newDeath);
                                        Log.d(TAG, "Updated existing Death record with UUID: " + newDeath.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping Death record with UUID: " + newDeath.uuid);
                                    }
                                }
                                Log.d(TAG, "Death data processed successfully");
                            });

                    } else {
                        Log.e(TAG, "Failed to download Death data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Death data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }


    private void downloadPregnancyData() {
        Call<DataWrapper<Pregnancy>> call = dao.getPreg(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Pregnancy>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Response<DataWrapper<Pregnancy>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Pregnancy[] newPregs = response.body().getData().toArray(new Pregnancy[0]);
                        PregnancyDao pregnancyDao = AppDatabase.getDatabase(context).pregnancyDao();

                        // Run database operations in a background thread
                        Executors.newSingleThreadExecutor().execute(() -> {
                            for (Pregnancy newPregnancy : newPregs) {
                                Pregnancy existingPregnancy = pregnancyDao.ins(newPregnancy.uuid);

                                if (existingPregnancy != null) {
                                    // Update newPregnancy with fields from existingPregnancy
                                    newPregnancy.extra = existingPregnancy.extra;
                                    newPregnancy.outcome = existingPregnancy.outcome;
                                    newPregnancy.id = existingPregnancy.id;
                                    newPregnancy.complete = 0;
                                    // Save the updated pregnancy data
                                    pregnancyDao.create(newPregnancy);
                                    Log.d(TAG, "Updated and saved Pregnancy record with UUID: " + newPregnancy.uuid);
                                    //Log.d(TAG, "Pregnancy Approved Date: " + newPregnancy.approveDate + " : "  + existingPregnancy.approveDate);
                                } else {
                                    Log.d(TAG, "Skipping Pregnancy record with UUID: " + newPregnancy.uuid);
                                }
                            }
                            Log.d(TAG, "Pregnancy data processed successfully");
                        });

                    } else {
                        Log.e(TAG, "Failed to download Pregnancy data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Pregnancy data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }

    private void downloadDemographicData() {

        Call<DataWrapper<Demographic>> call = dao.getDemo(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Demographic>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Response<DataWrapper<Demographic>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Demographic[] demographics = response.body().getData().toArray(new Demographic[0]);
                        DemographicDao demographicDao = AppDatabase.getDatabase(context).demographicDao();

                        // Run database operations in a background thread
                        Executors.newSingleThreadExecutor().execute(() -> {
                            for (Demographic newDemographic : demographics) {
                                Demographic existingDemographic = demographicDao.ins(newDemographic.individual_uuid);

                                if (existingDemographic != null) {
                                    // Save updated demographic data
                                    demographicDao.create(newDemographic);
                                    Log.d(TAG, "Updated and saved Demographic record with UUID: " + newDemographic.individual_uuid);
                                } else {
                                    Log.d(TAG, "Skipping existing Demographic record with UUID: " + newDemographic.individual_uuid);
                                }
                            }
                            Log.d(TAG, "Demographic data processed successfully");
                        });

                    } else {
                        Log.e(TAG, "Failed to download Demographic data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Demographic data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }


    private void downloadRelationshipData() {

        Call<DataWrapper<Relationship>> call = dao.getRel(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Relationship>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Response<DataWrapper<Relationship>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Relationship[] relationships = response.body().getData().toArray(new Relationship[0]);
                        RelationshipDao relationshipDao = AppDatabase.getDatabase(context).relationshipDao();

                        // Run database operations in a background thread
                        Executors.newSingleThreadExecutor().execute(() -> {
                            for (Relationship newRelationship : relationships) {
                                Relationship existingRelationship = relationshipDao.ins(newRelationship.uuid);

                                if (existingRelationship != null) {
                                    // Save updated relationship data
                                    relationshipDao.create(newRelationship);
                                    Log.d(TAG, "Updated and saved Relationship record with UUID: " + newRelationship.uuid);
                                } else {
                                    Log.d(TAG, "Skipping existing Relationship record with UUID: " + newRelationship.uuid);
                                }
                            }
                            Log.d(TAG, "Relationship data processed successfully");
                        });

                    } else {
                        Log.e(TAG, "Failed to download Relationship data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Relationship data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }


    private void downloadVaccinationData() {
        Call<DataWrapper<Vaccination>> call = dao.getVac(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Vaccination>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Response<DataWrapper<Vaccination>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Vaccination[] vaccinations = response.body().getData().toArray(new Vaccination[0]);

                            VaccinationDao vaccinationDao = AppDatabase.getDatabase(context).vaccinationDao();

                            // Run database operations in a background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                for (Vaccination newVaccination : vaccinations) {
                                    Vaccination existingVaccination = vaccinationDao.ins(newVaccination.uuid);

                                    // Ensure existingVaccination is NOT NULL and complete is NOT 1
                                    if (existingVaccination != null) {
                                        vaccinationDao.create(newVaccination);
                                        Log.d(TAG, "Updated existing Vaccination record with UUID: " + newVaccination.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping Vaccination record with UUID: " + newVaccination.uuid);
                                    }
                                }
                                Log.d(TAG, "Vaccination data processed successfully");
                            });


                    } else {
                        Log.e(TAG, "Failed to download Vaccination data");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Vaccination data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading data: " + t.getMessage());
            }
        });
    }


    private void downloadHdssSociodemoData() {
        Call<DataWrapper<HdssSociodemo>> call = dao.getSes(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Response<DataWrapper<HdssSociodemo>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        HdssSociodemo[] hdssSociodemographics = response.body().getData().toArray(new HdssSociodemo[0]);

                            HdssSociodemoDao hdssSociodemoDao = AppDatabase.getDatabase(context).hdssSociodemoDao();

                            // Run database operations in a background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                for (HdssSociodemo newHdssSociodemo : hdssSociodemographics) {
                                    HdssSociodemo existingHdssSociodemo = hdssSociodemoDao.ins(newHdssSociodemo.uuid);

                                    // Ensure existing record is NOT NULL and complete is NOT 1 before updating
                                    if (existingHdssSociodemo != null ) {
                                        hdssSociodemoDao.create(newHdssSociodemo);
                                        Log.d(TAG, "Added/Updated HdssSociodemo record with UUID: " + newHdssSociodemo.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping existing HdssSociodemo record with UUID: " + newHdssSociodemo.uuid);
                                    }
                                }
                                Log.d(TAG, "HdssSociodemo data processed successfully");
                            });


                    } else {
                        Log.e(TAG, "Failed to download HdssSociodemo data, Response Code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing HdssSociodemo data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading HdssSociodemo data: " + t.getMessage(), t);
            }
        });
    }



    private void downloadMorbidityData() {
        Call<DataWrapper<Morbidity>> call = dao.getMor(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Morbidity>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Response<DataWrapper<Morbidity>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Morbidity[] morbidities = response.body().getData().toArray(new Morbidity[0]);

                            MorbidityDao morbidityDao = AppDatabase.getDatabase(context).morbidityDao();
                            // Run database operations in a background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                for (Morbidity newMorbidity : morbidities) {
                                    Morbidity existingMorbidity = morbidityDao.ins(newMorbidity.uuid);

                                    // Ensure existing record is NOT NULL and complete is NOT 1 before updating
                                    if (existingMorbidity != null) {
                                        morbidityDao.create(newMorbidity);
                                        Log.d(TAG, "Added/Updated Morbidity record with UUID: " + newMorbidity.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping existing Morbidity record with UUID: " + newMorbidity.uuid);
                                    }
                                }
                                Log.d(TAG, "Morbidity data processed successfully");
                            });


                    } else {
                        Log.e(TAG, "Failed to download Morbidity data, Response Code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Morbidity data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading Morbidity data: " + t.getMessage(), t);
            }
        });
    }



    private void downloadPregnancyOutcomeData() {
        Call<DataWrapper<Pregnancyoutcome>> call = dao.getOut(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Response<DataWrapper<Pregnancyoutcome>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Pregnancyoutcome[] newPregnancies = response.body().getData().toArray(new Pregnancyoutcome[0]);
                            PregnancyoutcomeDao pregnancyDao = AppDatabase.getDatabase(context).pregnancyoutcomeDao();

                            // Run database operations in a background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                for (Pregnancyoutcome newPregnancy : newPregnancies) {
                                    Pregnancyoutcome existingPregnancy = pregnancyDao.ins(newPregnancy.uuid);

                                    if (existingPregnancy != null) {
                                            // Preserve certain fields while updating
                                            newPregnancy.location = existingPregnancy.location;
                                            newPregnancy.id = existingPregnancy.id;
                                            newPregnancy.complete = 0;
                                            newPregnancy.extra = existingPregnancy.extra;

                                        // Save updated/new record
                                        pregnancyDao.create(newPregnancy);
                                        Log.d(TAG, "Added/Updated PregnancyOutcome record with UUID: " + newPregnancy.uuid);
                                    } else {
                                        Log.d(TAG, "Skipping existing PregnancyOutcome record with UUID: " + newPregnancy.uuid);
                                    }
                                }
                                Log.d(TAG, "PregnancyOutcome data processed successfully");
                            });


                    } else {
                        Log.e(TAG, "Failed to download PregnancyOutcome data, Response Code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing PregnancyOutcome data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading PregnancyOutcome data: " + t.getMessage(), t);
            }
        });
    }

    private void downloadDuplicateData() {
        Call<DataWrapper<Duplicate>> call = dao.getDup(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Duplicate>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Response<DataWrapper<Duplicate>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Duplicate[] item = response.body().getData().toArray(new Duplicate[0]);

                        DuplicateDao duplicateDao = AppDatabase.getDatabase(context).duplicateDao();
                        // Run database operations in a background thread
                        Executors.newSingleThreadExecutor().execute(() -> {
                            for (Duplicate newDuplicate : item) {
                                Duplicate existingDuplicate = duplicateDao.ins(newDuplicate.individual_uuid);

                                // Ensure existing record is NOT NULL and complete is NOT 1 before updating
                                if (existingDuplicate != null) {
                                    duplicateDao.create(newDuplicate);
                                    Log.d(TAG, "Added/Updated Duplicate record with UUID: " + newDuplicate.individual_uuid);
                                } else {
                                    Log.d(TAG, "Skipping existing Duplicate record with UUID: " + newDuplicate.individual_uuid);
                                }
                            }
                            Log.d(TAG, "Duplicate data processed successfully");
                        });


                    } else {
                        Log.e(TAG, "Failed to download Duplicate data, Response Code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Duplicate data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error downloading Morbidity data: " + t.getMessage(), t);
            }
        });
    }



}