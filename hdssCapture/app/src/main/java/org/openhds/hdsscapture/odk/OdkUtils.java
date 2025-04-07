package org.openhds.hdsscapture.odk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.openhds.hdsscapture.entity.Individual;

public class OdkUtils {
    public static final String ODK_PACKAGE = "org.odk.collect.android";
    public static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String ODK_REFERRER = "android-app://" + ODK_PACKAGE;
    private static final String[] PROJECTION = {"_id", "jrFormId"};
    private static final String TAG = "OdkUtils";

    public static Intent createOdkFormIntent(String formUri, Individual individual) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(ODK_PACKAGE);
        intent.setData(Uri.parse(formUri));

        // Add individual data directly to the intent extras
        intent.putExtra("firstname", individual.getFirstName());
        intent.putExtra("lastname", individual.getLastName());
        intent.putExtra("permid", individual.getExtId());
        intent.putExtra("gender", individual.getGender());
        intent.putExtra("dob", individual.getDob());
        intent.putExtra("village", individual.getVillage());
        intent.putExtra("hhid", individual.getHohID());
        intent.putExtra("compno", individual.getCompno());

        // Add individual extras for debugging and additional data
        addIndividualExtras(intent, individual, true);

        return intent;
    }

    public static void addIndividualExtras(@NonNull Intent intent, @NonNull Individual individual, boolean includeDebugInfo) {
        intent.putExtra("firstname", individual.getFirstName());
        intent.putExtra("lastname", individual.getLastName());
        intent.putExtra("permid", individual.getExtId());
        intent.putExtra("gender", individual.getGender());
        intent.putExtra("dob", individual.getDob());
        intent.putExtra("village", individual.getVillage());
        intent.putExtra("hhid", individual.getHohID());
        intent.putExtra("compno", individual.getCompno());

    }

    public static void returnToOdk(@NonNull Activity activity, @NonNull Individual individual) {
        Uri referrer = activity.getReferrer();
        if (referrer != null && ODK_REFERRER.equals(referrer.toString())) {
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