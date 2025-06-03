package org.openhds.hdsscapture;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.work.Configuration;

import org.openhds.hdsscapture.Sync.BackgroundSyncManager;
import org.openhds.hdsscapture.Utilities.QueryNotification;
import org.openhds.hdsscapture.Utilities.RejectionNotification;

import java.util.concurrent.TimeUnit;

public class HDSSCaptureApp extends Application implements Configuration.Provider {

    private static final String TAG = "HDSSCaptureApplication";
    private static final int SYNC_INTERVAL_MINUTES = 10;

    @Override
    public void onCreate() {
        super.onCreate();


        if (!isInitialized) {
            initializeNotifications();
            initializeBackgroundSync();
            isInitialized = true;
        }
    }

    private static boolean isInitialized = false;

    private void initializeBackgroundSync() {
        try {
            BackgroundSyncManager.setupBackgroundSync(
                    this,
                    SYNC_INTERVAL_MINUTES,
                    TimeUnit.MINUTES
            );
            Log.d(TAG, "Background sync initialized successfully with interval: " + SYNC_INTERVAL_MINUTES + " minutes");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize background sync: " + e.getMessage(), e);
        }
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.INFO)
                .build();
    }

    private void initializeNotifications() {
        try {
            RejectionNotification.initialize(this);
            QueryNotification.initialize(this);
            Log.d(TAG, "Notification system initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize notification system: " + e.getMessage(), e);
        }
    }
}
