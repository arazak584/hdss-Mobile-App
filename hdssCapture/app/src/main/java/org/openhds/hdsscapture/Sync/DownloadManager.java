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
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final AtomicInteger pendingDownloads = new AtomicInteger(0);
    private final CountDownLatch downloadLatch = new CountDownLatch(11); // 11 download tasks

    public DownloadManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.dao = AppJson.getInstance(context).getJsonApi();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        if (authorizationHeader == null || fw == null) {
            Log.e(TAG, "Authorization header or field worker UUID not found");
            return Result.failure();
        }

        if (!isInternetAvailable()) {
            Log.e(TAG, "No internet connection available");
            return Result.retry();
        }

        try {
            // Start all downloads
            downloadInmigrationData();
            downloadOutmigrationData();
            downloadDeathData();
            downloadPregnancyData();
            downloadDemographicData();
            downloadRelationshipData();
            downloadVaccinationData();
            downloadHdssSociodemoData();
            downloadMorbidityData();
            downloadPregnancyOutcomeData();
            downloadDuplicateData();

            // Wait for all downloads to complete (max 5 minutes)
            boolean completed = downloadLatch.await(5, TimeUnit.MINUTES);

            if (completed) {
                Log.d(TAG, "All downloads completed successfully");
                return Result.success();
            } else {
                Log.e(TAG, "Download timeout - some tasks did not complete");
                return Result.retry();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Download interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return Result.retry();
        } finally {
            executorService.shutdown();
        }
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
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Inmigration[] data = response.body().getData().toArray(new Inmigration[0]);
                        InmigrationDao dao = AppDatabase.getDatabase(context).inmigrationDao();

                        executorService.execute(() -> {
                            try {
                                // Extract UUIDs
                                List<String> uuids = new ArrayList<>();
                                for (Inmigration item : data) {
                                    uuids.add(item.uuid);
                                }

                                // Batch fetch existing records
                                List<Inmigration> existing = dao.getByUuids(uuids);
                                Map<String, Inmigration> existingMap = new HashMap<>();
                                for (Inmigration item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                // Update records
                                List<Inmigration> toUpdate = new ArrayList<>();
                                for (Inmigration item : data) {
                                    Inmigration exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Inmigration[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Inmigration records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Inmigration: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Inmigration data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Inmigration response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
                Log.e(TAG, "Inmigration download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadOutmigrationData() {
        Call<DataWrapper<Outmigration>> call = dao.getOmg(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Outmigration>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Response<DataWrapper<Outmigration>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Outmigration[] data = response.body().getData().toArray(new Outmigration[0]);
                        OutmigrationDao dao = AppDatabase.getDatabase(context).outmigrationDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Outmigration item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Outmigration> existing = dao.getByUuids(uuids);
                                Map<String, Outmigration> existingMap = new HashMap<>();
                                for (Outmigration item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Outmigration> toUpdate = new ArrayList<>();
                                for (Outmigration item : data) {
                                    Outmigration exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.edit = 1;
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Outmigration[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Outmigration records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Outmigration: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Outmigration data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Outmigration response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
                Log.e(TAG, "Outmigration download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadDeathData() {
        Call<DataWrapper<Death>> call = dao.getDth(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Death>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Death>> call, @NonNull Response<DataWrapper<Death>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Death[] data = response.body().getData().toArray(new Death[0]);
                        DeathDao dao = AppDatabase.getDatabase(context).deathDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Death item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Death> existing = dao.getByUuids(uuids);
                                Map<String, Death> existingMap = new HashMap<>();
                                for (Death item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Death> toUpdate = new ArrayList<>();
                                for (Death item : data) {
                                    Death exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.edit = 1;
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Death[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Death records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Death: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Death data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Death response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                Log.e(TAG, "Death download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadPregnancyData() {
        Call<DataWrapper<Pregnancy>> call = dao.getPreg(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Pregnancy>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Response<DataWrapper<Pregnancy>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Pregnancy[] data = response.body().getData().toArray(new Pregnancy[0]);
                        PregnancyDao dao = AppDatabase.getDatabase(context).pregnancyDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Pregnancy item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Pregnancy> existing = dao.getByUuids(uuids);
                                Map<String, Pregnancy> existingMap = new HashMap<>();
                                for (Pregnancy item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Pregnancy> toUpdate = new ArrayList<>();
                                for (Pregnancy item : data) {
                                    Pregnancy exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Pregnancy[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Pregnancy records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Pregnancy: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Pregnancy data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Pregnancy response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
                Log.e(TAG, "Pregnancy download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadDemographicData() {
        Call<DataWrapper<Demographic>> call = dao.getDemo(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Demographic>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Response<DataWrapper<Demographic>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Demographic[] data = response.body().getData().toArray(new Demographic[0]);
                        DemographicDao dao = AppDatabase.getDatabase(context).demographicDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Demographic item : data) {
                                    uuids.add(item.individual_uuid);
                                }

                                List<Demographic> existing = dao.getByUuids(uuids);
                                Map<String, Demographic> existingMap = new HashMap<>();
                                for (Demographic item : existing) {
                                    existingMap.put(item.individual_uuid, item);
                                }

                                List<Demographic> toUpdate = new ArrayList<>();
                                for (Demographic item : data) {
                                    Demographic exist = existingMap.get(item.individual_uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Demographic[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Demographic records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Demographic: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Demographic data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Demographic response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Throwable t) {
                Log.e(TAG, "Demographic download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadRelationshipData() {
        Call<DataWrapper<Relationship>> call = dao.getRel(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Relationship>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Response<DataWrapper<Relationship>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Relationship[] data = response.body().getData().toArray(new Relationship[0]);
                        RelationshipDao dao = AppDatabase.getDatabase(context).relationshipDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Relationship item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Relationship> existing = dao.getByUuids(uuids);
                                Map<String, Relationship> existingMap = new HashMap<>();
                                for (Relationship item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Relationship> toUpdate = new ArrayList<>();
                                for (Relationship item : data) {
                                    Relationship exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Relationship[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Relationship records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Relationship: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Relationship data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Relationship response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
                Log.e(TAG, "Relationship download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadVaccinationData() {
        Call<DataWrapper<Vaccination>> call = dao.getVac(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Vaccination>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Response<DataWrapper<Vaccination>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Vaccination[] data = response.body().getData().toArray(new Vaccination[0]);
                        VaccinationDao dao = AppDatabase.getDatabase(context).vaccinationDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Vaccination item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Vaccination> existing = dao.getByUuids(uuids);
                                Map<String, Vaccination> existingMap = new HashMap<>();
                                for (Vaccination item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Vaccination> toUpdate = new ArrayList<>();
                                for (Vaccination item : data) {
                                    Vaccination exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Vaccination[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Vaccination records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Vaccination: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Vaccination data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Vaccination response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Throwable t) {
                Log.e(TAG, "Vaccination download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadHdssSociodemoData() {
        Call<DataWrapper<HdssSociodemo>> call = dao.getSes(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Response<DataWrapper<HdssSociodemo>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        HdssSociodemo[] data = response.body().getData().toArray(new HdssSociodemo[0]);
                        HdssSociodemoDao dao = AppDatabase.getDatabase(context).hdssSociodemoDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (HdssSociodemo item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<HdssSociodemo> existing = dao.getByUuids(uuids);
                                Map<String, HdssSociodemo> existingMap = new HashMap<>();
                                for (HdssSociodemo item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<HdssSociodemo> toUpdate = new ArrayList<>();
                                for (HdssSociodemo item : data) {
                                    HdssSociodemo exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new HdssSociodemo[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " HdssSociodemo records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing HdssSociodemo: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download HdssSociodemo data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in HdssSociodemo response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Throwable t) {
                Log.e(TAG, "HdssSociodemo download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadMorbidityData() {
        Call<DataWrapper<Morbidity>> call = dao.getMor(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Morbidity>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Response<DataWrapper<Morbidity>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Morbidity[] data = response.body().getData().toArray(new Morbidity[0]);
                        MorbidityDao dao = AppDatabase.getDatabase(context).morbidityDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Morbidity item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Morbidity> existing = dao.getByUuids(uuids);
                                Map<String, Morbidity> existingMap = new HashMap<>();
                                for (Morbidity item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Morbidity> toUpdate = new ArrayList<>();
                                for (Morbidity item : data) {
                                    Morbidity exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Morbidity[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Morbidity records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Morbidity: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Morbidity data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Morbidity response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Throwable t) {
                Log.e(TAG, "Morbidity download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadPregnancyOutcomeData() {
        Call<DataWrapper<Pregnancyoutcome>> call = dao.getOut(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Response<DataWrapper<Pregnancyoutcome>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Pregnancyoutcome[] data = response.body().getData().toArray(new Pregnancyoutcome[0]);
                        PregnancyoutcomeDao dao = AppDatabase.getDatabase(context).pregnancyoutcomeDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Pregnancyoutcome item : data) {
                                    uuids.add(item.uuid);
                                }

                                List<Pregnancyoutcome> existing = dao.getByUuids(uuids);
                                Map<String, Pregnancyoutcome> existingMap = new HashMap<>();
                                for (Pregnancyoutcome item : existing) {
                                    existingMap.put(item.uuid, item);
                                }

                                List<Pregnancyoutcome> toUpdate = new ArrayList<>();
                                for (Pregnancyoutcome item : data) {
                                    Pregnancyoutcome exist = existingMap.get(item.uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Pregnancyoutcome[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " PregnancyOutcome records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing PregnancyOutcome: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download PregnancyOutcome data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in PregnancyOutcome response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
                Log.e(TAG, "PregnancyOutcome download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

    private void downloadDuplicateData() {
        Call<DataWrapper<Duplicate>> call = dao.getDup(authorizationHeader, fw);
        call.enqueue(new Callback<DataWrapper<Duplicate>>() {
            @Override
            public void onResponse(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Response<DataWrapper<Duplicate>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Duplicate[] data = response.body().getData().toArray(new Duplicate[0]);
                        DuplicateDao dao = AppDatabase.getDatabase(context).duplicateDao();

                        executorService.execute(() -> {
                            try {
                                List<String> uuids = new ArrayList<>();
                                for (Duplicate item : data) {
                                    uuids.add(item.individual_uuid);
                                }

                                List<Duplicate> existing = dao.getByUuids(uuids);
                                Map<String, Duplicate> existingMap = new HashMap<>();
                                for (Duplicate item : existing) {
                                    existingMap.put(item.individual_uuid, item);
                                }

                                List<Duplicate> toUpdate = new ArrayList<>();
                                for (Duplicate item : data) {
                                    Duplicate exist = existingMap.get(item.individual_uuid);
                                    if (exist != null) {
                                        exist.comment = item.comment;
                                        exist.status = item.status;
                                        exist.supervisor = item.supervisor;
                                        exist.approveDate = item.approveDate;
                                        toUpdate.add(exist);
                                    }
                                }

                                if (!toUpdate.isEmpty()) {
                                    dao.create(toUpdate.toArray(new Duplicate[0]));
                                    Log.d(TAG, "Updated " + toUpdate.size() + " Duplicate records");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing Duplicate: " + e.getMessage(), e);
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to download Duplicate data");
                        downloadLatch.countDown();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in Duplicate response: " + e.getMessage(), e);
                    downloadLatch.countDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Throwable t) {
                Log.e(TAG, "Duplicate download failed: " + t.getMessage());
                downloadLatch.countDown();
            }
        });
    }

}

