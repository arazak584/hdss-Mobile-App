package org.openhds.hdsscapture.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.openhds.hdsscapture.entity.Individual;

public class OdkUtils {
    public static final String ODK_PACKAGE = "org.odk.collect.android";
    public static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String ODK_REFERRER = "android-app://" + ODK_PACKAGE;
    private static final String[] PROJECTION = {"_id", "jrFormId"};
    private static final String TAG = "OdkUtils";

    @NonNull
    public static Intent createOdkFormIntent(String formUri, Individual individual) {
        if (individual == null) {
            throw new IllegalArgumentException("Individual cannot be null");
        }

        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(formUri))
                .setPackage(ODK_PACKAGE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        addIndividualExtras(intent, individual, true);
        return intent;
    }

    public static void addIndividualExtras(@NonNull Intent intent, @NonNull Individual individual, boolean includeDebugInfo) {
        intent.putExtra("firstName", individual.firstName)
                .putExtra("lastName", individual.lastName)
                .putExtra("permid", individual.extId)
                .putExtra("gender", individual.gender)
                .putExtra("dob", individual.getDob())
                .putExtra("village", individual.village)
                .putExtra("hhid", individual.hohID)
                .putExtra("compno", individual.compno);

        if (includeDebugInfo) {
            String debugInfo = String.format("Sending to ODK:\nFirst Name: %s\nLast Name: %s\nID: %s\nGender: %s",
                    individual.firstName,
                    individual.lastName,
                    individual.extId,
                    individual.gender);
            intent.putExtra(Intent.EXTRA_TEXT, debugInfo);
            Log.d(TAG, "Passing extras: " + intent.getExtras());
        }
    }

    public static void returnToOdk(@NonNull Activity activity, @NonNull Individual individual) {
        if (activity.getReferrer() != null &&
                ODK_REFERRER.equals(activity.getReferrer().toString())) {
            Intent intent = new Intent();
            addIndividualExtras(intent, individual, false);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }

    public static int getOdkFormId(@NonNull Context context, @Nullable String jrFormId) {
        if (jrFormId == null) return -1;

        Uri formUri = Uri.parse(ODK_FORMS_PROVIDER);
        try (Cursor cursor = context.getContentResolver().query(formUri, PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String retrievedFormId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                    if (jrFormId.equals(retrievedFormId)) {
                        return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying ODK forms", e);
        }
        return -1;
    }
}