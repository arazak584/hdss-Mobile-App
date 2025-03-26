package org.openhds.hdsscapture.Utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RejectionNotification {

    private static final String TAG = "NotificationManager";
    private static final String NOTIFICATION_WORK_TAG = "notificationWork";
    private static final String REJECTION_CHANNEL_ID = "hdss_rejection_channel";

    public static class NotificationWorker extends Worker {
        private final DeathViewModel deathViewModel;
        private final InmigrationViewModel inmigrationViewModel;
        private final OutmigrationViewModel outmigrationViewModel;
        private final DemographicViewModel demographicViewModel;
        private final PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
        private final PregnancyViewModel pregnancyViewModel;
        private final RelationshipViewModel relationshipViewModel;
        private final HdssSociodemoViewModel hdssSociodemoViewModel;
        private final VaccinationViewModel vaccinationViewModel;
        private final MorbidityViewModel morbidityViewModel;
        private final String fw;

        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
            super(context, params);
            Application app = (Application) context.getApplicationContext();
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(app);

            // Retrieve fw_uuid from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

            Log.d("Notification", "Fw-worker " + fw);

            inmigrationViewModel = factory.create(InmigrationViewModel.class);
            outmigrationViewModel = factory.create(OutmigrationViewModel.class);
            deathViewModel = factory.create(DeathViewModel.class);
            pregnancyViewModel = factory.create(PregnancyViewModel.class);
            pregnancyoutcomeViewModel = factory.create(PregnancyoutcomeViewModel.class);
            demographicViewModel = factory.create(DemographicViewModel.class);
            relationshipViewModel = factory.create(RelationshipViewModel.class);
            vaccinationViewModel = factory.create(VaccinationViewModel.class);
            hdssSociodemoViewModel = factory.create(HdssSociodemoViewModel.class);
            morbidityViewModel = factory.create(MorbidityViewModel.class);
        }

        @NonNull
        @Override
        public Result doWork() {
            try {
                checkAndNotify();
                return Result.success();
            } catch (Exception e) {
                Log.e(TAG, "Notification worker failed", e);
                return Result.retry();
            }
        }

        private void checkAndNotify() throws ExecutionException, InterruptedException {
            long totalImg = inmigrationViewModel.rej(fw);
            long totalOmg = outmigrationViewModel.rej(fw);
            long totalPre = pregnancyViewModel.rej(fw);
            long totalOut = pregnancyoutcomeViewModel.rej(fw);
            long totalDem = demographicViewModel.rej(fw);
            long totalDth = deathViewModel.rej(fw);
            long totalRel = relationshipViewModel.rej(fw);
            long totalses = hdssSociodemoViewModel.rej(fw);
            long totalvac = vaccinationViewModel.rej(fw);
            long totalmor = morbidityViewModel.rej(fw);

            if (totalImg > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have an Inmigration Query Rejection",
                        createNotificationIntent(),
                        1001
                );
            }
            if (totalOmg > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have an Outmigration Query Rejection",
                        createNotificationIntent(),
                        1002
                );
            }
            if (totalPre > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Pregnancy Query Rejection",
                        createNotificationIntent(),
                        1003
                );
            }
            if (totalOut > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Pregnancy Outcome Query Rejection",
                        createNotificationIntent(),
                        1004
                );
            }
            if (totalDem > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Demographic Query Rejection",
                        createNotificationIntent(),
                        1005
                );
            }
            if (totalDth > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Death Query Rejection",
                        createNotificationIntent(),
                        1006
                );
            }
            if (totalRel > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Relationship Query Rejection",
                        createNotificationIntent(),
                        1007
                );
            }
            if (totalses > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have an SES Query Rejection",
                        createNotificationIntent(),
                        1008
                );
            }
            if (totalvac > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Vaccination Query Rejection",
                        createNotificationIntent(),
                        1009
                );
            }
            if (totalmor > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Rejection Query",
                        "You have a Morbidity Query Rejection",
                        createNotificationIntent(),
                        1010
                );
            }
        }

        private PendingIntent createNotificationIntent() {
            Intent intent = new Intent(getApplicationContext(), RejectionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            return PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }
    }

    /**
     * Initialize notification system
     */
    public static void initialize(Context context) {
        createNotificationChannel(context);
        scheduleNotificationChecks(context);
    }

    /**
     * Schedule periodic notification checks
     */
    private static void scheduleNotificationChecks(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                5, // Check every 5 minutes
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(NOTIFICATION_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NOTIFICATION_WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationWork
        );
    }

    /**
     * Create notification channel for Android O and above
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    REJECTION_CHANNEL_ID,
                    "HDSS Rejection Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for Rejection Queries");
            channel.enableVibration(true);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create and show a notification
     */
    public static void createAndShowNotification(
            Context context,
            String title,
            String message,
            PendingIntent pendingIntent,
            int notificationId
    ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REJECTION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.hds)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500});

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        notificationManager.notify(notificationId, builder.build());
    }
}