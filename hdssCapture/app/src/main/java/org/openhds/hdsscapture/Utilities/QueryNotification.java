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
import org.openhds.hdsscapture.Activity.QueryActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.QueriesViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.entity.ServerQueries;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class QueryNotification {

    private static final String TAG = "NotificationManager";
    private static final String NOTIFICATION_QUERY_TAG = "notificationQueryWork";
    private static final String QUERY_CHANNEL_ID = "hdss_query_channel";

    public static class NotificationWorker extends Worker {
        private final DeathViewModel deathViewModel;
        private final ListingViewModel listingViewModel;
        private final IndividualViewModel individualViewModel;
        private final OutcomeViewModel outcomeViewModel;
        private final PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
        private final HdssSociodemoViewModel hdssSociodemoViewModel;
        private final QueriesViewModel queriesViewModel;

        private final String fw;
        private String fwname;

        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
            super(context, params);
            Application app = (Application) context.getApplicationContext();
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(app);

            // Retrieve fw_uuid from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
            fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);
            fwname = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);

            Log.d("Notification", "Fw-worker "+ fw);

            listingViewModel = factory.create(ListingViewModel.class);
            individualViewModel = factory.create(IndividualViewModel.class);
            deathViewModel = factory.create(DeathViewModel.class);
            pregnancyoutcomeViewModel = factory.create(PregnancyoutcomeViewModel.class);
            outcomeViewModel = factory.create(OutcomeViewModel.class);
            hdssSociodemoViewModel = factory.create(HdssSociodemoViewModel.class);
            queriesViewModel = factory.create(QueriesViewModel.class);
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
            long totalOut = pregnancyoutcomeViewModel.cnt(fw);
            long listing = listingViewModel.cnt();
            long totalses = hdssSociodemoViewModel.cnt(fw);
            long totalDth = deathViewModel.cnt();
            long ind = individualViewModel.cnt();
            long inds = individualViewModel.cnts();
            long indss = individualViewModel.cntss();
            List<ServerQueries> qis = queriesViewModel.findByFw(fwname);
            //long out = outcomeViewModel.cnt(fw);

            if (totalOut > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Incomplete Pregnancy Outcome Form",
                        createNotificationIntent(),
                        1011,
                        QUERY_CHANNEL_ID
                );
            }
            if (listing > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Listing Not Picked",
                        createNotificationIntent(),
                        1012,
                        QUERY_CHANNEL_ID
                );
            }
            if (totalses > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Incomplete SES Form",
                        createNotificationIntent(),
                        1013,
                        QUERY_CHANNEL_ID
                );
            }
            if (totalDth > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Change Head of Household [HOH is Dead]",
                        createNotificationIntent(),
                        1014,
                        QUERY_CHANNEL_ID
                );
            }
            if (ind > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Household Head is a Minor",
                        createNotificationIntent(),
                        1015,
                        QUERY_CHANNEL_ID
                );
            }
            if (inds > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "Query",
                        "Head of Household is Unknown",
                        createNotificationIntent(),
                        1016,
                        QUERY_CHANNEL_ID
                );
            }
//            if (indss > 0) {
//                createAndShowNotification(
//                        getApplicationContext(),
//                        "Query",
//                        "Only Minors Left in Household",
//                        createNotificationIntent(),
//                        1017,
//                        QUERY_CHANNEL_ID
//                );
//            }
            // Create notifications for each ServerQuery with its error message
            if (qis != null && !qis.isEmpty()) {
                int notificationId = 1018; // Starting ID for ServerQueries notifications
                for (ServerQueries query : qis) {
                    String errorMessage = query.getError(); // Default Message
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        createAndShowNotification(
                                getApplicationContext(),
                                "Query",
                                errorMessage,
                                createNotificationIntent(),
                                notificationId++,
                                QUERY_CHANNEL_ID
                        );
                    }
                }
            }

//            if (out > 0) {
//                createAndShowNotification(
//                        getApplicationContext(),
//                        "Query",
//                        "Outcome Error (Pregnancy Outcome incomplete)",
//                        createNotificationIntent(),
//                        1018,
//                        QUERY_CHANNEL_ID
//                );
//            }

        }

        private PendingIntent createNotificationIntent() {
            Intent intent = new Intent(getApplicationContext(), QueryActivity.class);
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
        createQueryNotificationChannel(context);
        scheduleNotificationChecks(context);
    }

    /**
     * Schedule periodic notification checks
     */
    private static void scheduleNotificationChecks(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest notificationQueryWork = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                5, // Check every 5 minutes
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(NOTIFICATION_QUERY_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NOTIFICATION_QUERY_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationQueryWork
        );
    }

    /**
     * Create notification channel for Android O and above
     */
    private static void createQueryNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    QUERY_CHANNEL_ID,
                    "HDSS Query Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for Query Alerts");
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
            int notificationId,
            String channelId
    ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
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