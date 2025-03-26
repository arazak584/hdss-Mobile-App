package org.openhds.hdsscapture.Sync;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BackgroundSyncManager {

    private static final String SYNC_WORK_TAG = "syncWork";
    private static final long MIN_SYNC_INTERVAL = 15; // WorkManager requires at least 15 minutes

    public static void setupBackgroundSync(Context context, long interval, TimeUnit timeUnit) {
        long safeInterval = Math.max(interval, MIN_SYNC_INTERVAL); // Ensure valid interval

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)  // Can sync even when not charging
                .setRequiresDeviceIdle(false) // Allow sync during normal device usage
                .build();

        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                DownloadManager.class,
                safeInterval,
                timeUnit)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .addTag(SYNC_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP, // Keeps the existing worker
                syncWorkRequest
        );
    }

    public static void cancelBackgroundSync(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(SYNC_WORK_TAG);
        workManager.pruneWork(); // Removes completed jobs from history
    }
}
