package org.openhds.hdsscapture.Utilities;

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

        // Create a JSON object with the data you want to pass
        JSONObject data = new JSONObject();
        try {
            data.put("lastname", individual.getLastName());
            data.put("firstname", individual.getFirstName());
            data.put("permid", individual.getExtId());
            data.put("gender", individual.getGender());
            data.put("dob", individual.getDob());
            data.put("village", individual.getVillage());
            data.put("hhid", individual.getHohID());
            data.put("compno", individual.getCompno());
            // Add more fields as needed
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON data", e);
        }

        // Add the JSON data to the intent
        intent.putExtra("form_data", data.toString());
        Log.d(TAG, "Intent data: " + intent.getStringExtra("form_data"));

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

        if (includeDebugInfo) {
            String debugInfo = String.format("Sending to ODK:\nFirst Name: %s\nLast Name: %s\nID: %s\nGender: %s",
                    individual.getFirstName(),
                    individual.getLastName(),
                    individual.getExtId(),
                    individual.getGender());
            intent.putExtra(Intent.EXTRA_TEXT, debugInfo);
            Log.d(TAG, "Passing extras: " + intent.getExtras());
        }
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