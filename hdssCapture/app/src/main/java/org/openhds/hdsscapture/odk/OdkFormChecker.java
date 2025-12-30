package org.openhds.hdsscapture.odk;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Utility class to check ODK Collect form availability
 */
public class OdkFormChecker {

    private static final String TAG = "OdkFormChecker";

    /**
     * Check if a specific form exists in ODK Collect
     */
    public static boolean isFormAvailable(Context context, String formId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    FormsProviderAPI.FormsColumns.CONTENT_URI,
                    new String[]{FormsProviderAPI.FormsColumns._ID},
                    FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?",
                    new String[]{formId},
                    null
            );

            boolean available = cursor != null && cursor.getCount() > 0;
            Log.d(TAG, "Form " + formId + " availability: " + available);
            return available;

        } catch (Exception e) {
            Log.e(TAG, "Error checking form availability for " + formId, e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Get the display name of a form from ODK Collect
     */
    public static String getFormDisplayName(Context context, String formId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    FormsProviderAPI.FormsColumns.CONTENT_URI,
                    new String[]{
                            FormsProviderAPI.FormsColumns.DISPLAY_NAME,
                            FormsProviderAPI.FormsColumns.JR_VERSION
                    },
                    FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?",
                    new String[]{formId},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(
                        cursor.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME));
                String version = cursor.getString(
                        cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_VERSION));

                if (version != null && !version.isEmpty()) {
                    return displayName + " (v" + version + ")";
                }
                return displayName;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting form display name for " + formId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * Check if ODK Collect is installed
     */
    public static boolean isOdkCollectInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("org.odk.collect.android", 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Count total forms in ODK Collect
     */
    public static int getFormCount(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    FormsProviderAPI.FormsColumns.CONTENT_URI,
                    new String[]{FormsProviderAPI.FormsColumns._ID},
                    null,
                    null,
                    null
            );

            return cursor != null ? cursor.getCount() : 0;

        } catch (Exception e) {
            Log.e(TAG, "Error getting form count", e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}