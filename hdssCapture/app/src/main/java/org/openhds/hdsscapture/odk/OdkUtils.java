package org.openhds.hdsscapture.odk;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.openhds.hdsscapture.entity.Individual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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

    public static Uri createAndSaveOdkInstance(Context context, String formId, Individual individual) {
        String instanceId = formId + "_" + UUID.randomUUID().toString();
        File instanceDir = new File(
                Environment.getExternalStorageDirectory(),
                "Android/data/org.odk.collect.android/files/instances/" + instanceId
        );
        if (!instanceDir.exists()) instanceDir.mkdirs();

        File instanceFile = new File(instanceDir, instanceId + ".xml");

        try (FileOutputStream fos = new FileOutputStream(instanceFile)) {
            String xml =
                    "<data id=\"" + formId + "\">\n" +
                            "  <ind_id>" + individual.getUuid() + "</ind_id>\n" +
                            "  <firstname>" + individual.getFirstName() + "</firstname>\n" +
                            "</data>";

            fos.write(xml.getBytes());
        } catch (IOException e) {
            Log.e("ODKUtils", "Error writing instance XML", e);
            return null;
        }

        // Insert into ODK's content provider so it recognizes the instance
        ContentValues values = new ContentValues();
        values.put("displayName", instanceId);
        values.put("instanceFilePath", instanceFile.getAbsolutePath());
        values.put("jrFormId", formId);
        values.put("status", "incomplete");
        values.put("lastStatusChangeDate", System.currentTimeMillis());

        Uri instanceUri = context.getContentResolver().insert(
                Uri.parse("content://org.odk.collect.android.provider.odk.instances/instances"),
                values
        );

        return instanceUri;
    }



}