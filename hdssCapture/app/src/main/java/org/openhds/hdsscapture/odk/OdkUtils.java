package org.openhds.hdsscapture.odk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.openhds.hdsscapture.entity.Individual;

public class OdkUtils {
    public static final String ODK_PACKAGE = "org.odk.collect.android";
    public static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String ODK_REFERRER = "android-app://" + ODK_PACKAGE;
    private static final String TAG = "OdkUtils";

    /**
     * Method 1: Launch specific form by URI (most basic approach)
     * This is the standard way to launch ODK forms
     */
    public static Intent createOdkFormIntent(String formUri, Individual individual) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(ODK_PACKAGE);
        intent.setData(Uri.parse(formUri));

        // Add individual data as intent extras for debugging
        // Note: ODK Collect doesn't use these for form pre-filling
        addIndividualExtras(intent, individual);

        return intent;
    }

    /**
     * Method 2: Launch form with content URI and MIME type
     * This is the recommended approach from ODK documentation
     */
    public static Intent createOdkFormIntentWithContentType(String formUri, Individual individual) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.odk.form");
        intent.setPackage(ODK_PACKAGE);
        intent.setData(Uri.parse(formUri));

        // Add individual data as intent extras
        addIndividualExtras(intent, individual);

        return intent;
    }

    /**
     * Method 3: Launch any available form (shows form picker)
     * Use this if you want users to select from available forms
     */
    public static Intent createFormPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.dir/vnd.odk.form");
        intent.setPackage(ODK_PACKAGE);
        return intent;
    }

    /**
     * Method 4: Edit specific form instance
     * Use this to edit already created instances
     */
    public static Intent createEditInstanceIntent(String instanceUri) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setPackage(ODK_PACKAGE);
        intent.setData(Uri.parse(instanceUri));
        return intent;
    }

    /**
     * Method 5: Launch form with external app integration
     * This allows your app to receive data back from ODK
     */
    public static Intent createExternalAppIntent(Context context, String formUri, Individual individual) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(ODK_PACKAGE);
        intent.setData(Uri.parse(formUri));

        // Add individual data as intent extras
        addIndividualExtras(intent, individual);

        return intent;
    }

    /**
     * Add individual data as intent extras
     * Note: ODK Collect doesn't automatically use these for form pre-filling
     * They need to be handled by your form design or external app integration
     */
    public static void addIndividualExtras(@NonNull Intent intent, @NonNull Individual individual) {
        if (individual.getFirstName() != null) {
            intent.putExtra("firstname", individual.getFirstName());
        }
        if (individual.getLastName() != null) {
            intent.putExtra("lastname", individual.getLastName());
        }
        if (individual.getExtId() != null) {
            intent.putExtra("permid", individual.getExtId());
        }
        if (individual.getGender() != null) {
            intent.putExtra("gender", individual.getGender());
        }
        if (individual.getDob() != null) {
            intent.putExtra("dob", individual.getDob());
        }
        if (individual.getVillage() != null) {
            intent.putExtra("village", individual.getVillage());
        }
        if (individual.getHohID() != null) {
            intent.putExtra("hhid", individual.getHohID());
        }
        if (individual.getCompno() != null) {
            intent.putExtra("compno", individual.getCompno());
        }

        // Add UUID for unique identification
        if (individual.getUuid() != null) {
            intent.putExtra("individual_uuid", individual.getUuid());
        }
    }

    /**
     * Handle return from ODK Collect
     */
    public static void returnToOdk(@NonNull Activity activity, @NonNull Individual individual) {
        Uri referrer = activity.getReferrer();
        if (referrer != null && ODK_REFERRER.equals(referrer.toString())) {
            Intent intent = new Intent();
            addIndividualExtras(intent, individual);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }

    /**
     * Check if ODK Collect is installed
     */
    public static boolean isOdkCollectInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(ODK_PACKAGE, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create intent to install ODK Collect from Play Store
     */
    public static Intent createInstallOdkIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + ODK_PACKAGE));
        return intent;
    }
}