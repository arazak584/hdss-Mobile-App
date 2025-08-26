package org.openhds.hdsscapture.odk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import org.openhds.hdsscapture.entity.Individual;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OdkUtilsPrefilledInstance {
    public static final String ODK_PACKAGE = "org.odk.collect.android";
    public static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String TAG = "OdkUtilsPrefilled";

    // FileProvider authority - must match what's in your AndroidManifest.xml
    private static final String FILE_PROVIDER_AUTHORITY = "org.openhds.hdsscapture.fileprovider";

    /**
     * Enhanced method to create ODK form intent with pre-filled instance
     * Fixed to use FileProvider for Android 7.0+ compatibility
     */
    public static Intent createOdkFormWithPrefilledInstance(Context context, String formId, Individual individual) {
        if (individual == null || formId == null) {
            Log.e(TAG, "Individual or formId is null");
            return createBasicOdkFormIntent(context, formId);
        }

        try {
            // Validate required fields
            if (individual.getExtId() == null || individual.getExtId().isEmpty()) {
                Log.e(TAG, "Individual external ID is required but missing");
                return createBasicOdkFormIntent(context, formId);
            }

            // Step 1: Create pre-filled instance file
            String instancePath = createPrefilledInstanceFile(context, formId, individual);

            if (instancePath == null) {
                Log.e(TAG, "Failed to create pre-filled instance file");
                return createBasicOdkFormIntent(context, formId);
            }

            // Step 2: Create intent to open specific instance using FileProvider
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setPackage(ODK_PACKAGE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            // Use FileProvider to create a content URI instead of file URI
            File instanceFile = new File(instancePath);
            Uri instanceUri;

            try {
                // Try using FileProvider first (recommended approach)
                instanceUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, instanceFile);
                intent.setDataAndType(instanceUri, "text/xml");

                // Grant permission to ODK Collect to read the file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Log.d(TAG, "Created FileProvider URI: " + instanceUri);

            } catch (Exception e) {
                Log.w(TAG, "FileProvider failed, trying alternative approaches", e);

                // Fallback 1: Try copying to ODK's directory directly
                String odkInstancePath = copyToOdkInstancesDirectory(context, instanceFile, formId, individual);
                if (odkInstancePath != null) {
                    // Use the ODK instances directory path
                    intent = createOdkInstanceIntent(context, odkInstancePath);
                    Log.d(TAG, "Using ODK instances directory approach");
                } else {
                    // Fallback 2: Use basic form launch
                    Log.w(TAG, "All FileProvider methods failed, using basic form launch");
                    return createBasicOdkFormIntent(context, formId);
                }
            }

            // Add extra metadata
            intent.putExtra("individual_id", individual.getExtId());
            intent.putExtra("form_id", formId);

            Log.d(TAG, "Created ODK intent with pre-filled instance");
            return intent;

        } catch (Exception e) {
            Log.e(TAG, "Error creating pre-filled instance intent", e);
            return createBasicOdkFormIntent(context, formId);
        }
    }

    /**
     * Alternative approach: Copy instance file to ODK's instances directory
     * This bypasses FileProvider issues by putting the file where ODK expects it
     */
    private static String copyToOdkInstancesDirectory(Context context, File sourceFile, String formId, Individual individual) {
        try {
            // Create instance in ODK's standard location
            File odkInstancesDir = new File(Environment.getExternalStorageDirectory(), "odk/instances");

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String instanceDirName = formId + "_" + sanitizeFileName(individual.getExtId()) + "_" + timestamp + "_app";
            File instanceDir = new File(odkInstancesDir, instanceDirName);

            if (!instanceDir.mkdirs() && !instanceDir.exists()) {
                Log.e(TAG, "Failed to create ODK instance directory: " + instanceDir.getAbsolutePath());
                return null;
            }

            // Copy the file to ODK's instances directory
            File targetFile = new File(instanceDir, instanceDirName + ".xml");

            // Read source and write to target
            java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Log.d(TAG, "Copied instance to ODK directory: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Failed to copy to ODK instances directory", e);
            return null;
        }
    }

    /**
     * Create intent for ODK instance that's already in ODK's directory
     */
    private static Intent createOdkInstanceIntent(Context context, String instancePath) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setPackage(ODK_PACKAGE);

        // Try different approaches for ODK instance URIs
        try {
            // Method 1: Use content URI for instances
            String instanceName = new File(instancePath).getParentFile().getName();
            Uri contentUri = Uri.parse("content://org.odk.collect.android.provider.odk.instances/instances/" + instanceName);
            intent.setData(contentUri);
            Log.d(TAG, "Using ODK instances content URI: " + contentUri);

        } catch (Exception e) {
            // Method 2: Try file URI (may still work within ODK's domain)
            File instanceFile = new File(instancePath);
            Uri fileUri = Uri.fromFile(instanceFile);
            intent.setDataAndType(fileUri, "text/xml");
            Log.d(TAG, "Using file URI within ODK domain: " + fileUri);
        }

        return intent;
    }

    /**
     * Enhanced method to create pre-filled instance XML file
     * Same as before but with better error handling
     */
    private static String createPrefilledInstanceFile(Context context, String formId, Individual individual) {
        try {
            // Use app's private external files directory for temporary storage
            File appFilesDir = context.getExternalFilesDir("odk_instances");
            if (appFilesDir == null) {
                // Fallback to regular external storage
                appFilesDir = new File(context.getExternalFilesDir(null), "odk_instances");
            }

            if (!appFilesDir.exists() && !appFilesDir.mkdirs()) {
                Log.e(TAG, "Failed to create app files directory: " + appFilesDir.getAbsolutePath());
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String instanceDirName = formId + "_" + sanitizeFileName(individual.getExtId()) + "_" + timestamp;
            File instanceDir = new File(appFilesDir, instanceDirName);

            if (!instanceDir.mkdirs() && !instanceDir.exists()) {
                Log.e(TAG, "Failed to create instance directory: " + instanceDir.getAbsolutePath());
                return null;
            }

            // Create instance XML file
            File instanceFile = new File(instanceDir, instanceDirName + ".xml");
            String xmlContent = createEnhancedInstanceXmlContent(formId, individual);

            FileWriter writer = new FileWriter(instanceFile);
            writer.write(xmlContent);
            writer.close();

            Log.d(TAG, "Created pre-filled instance: " + instanceFile.getAbsolutePath());
            Log.d(TAG, "Instance XML content:\n" + xmlContent);

            return instanceFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error creating pre-filled instance file", e);
            return null;
        }
    }

    /**
     * Alternative approach: Use ODK's fill blank form intent
     * This approach pre-fills a form by launching it with extra parameters
     */
    public static Intent createOdkFormWithExtras(Context context, String formId, Individual individual) {
        try {
            Intent intent = new Intent("org.odk.collect.android.FORM_FILL");
            intent.setPackage(ODK_PACKAGE);

            // Add individual data as intent extras
            intent.putExtra("permid", individual.getExtId());
            intent.putExtra("firstname", individual.getFirstName());
            intent.putExtra("lastname", individual.getLastName());

            if (individual.getGender() != null) {
                intent.putExtra("gender", individual.getGender().toString());
            }

            if (individual.getDob() != null && !individual.getDob().isEmpty()) {
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

            if (individual.getUuid() != null) {
                intent.putExtra("individual_uuid", individual.getUuid());
            }

            // Add current date
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            intent.putExtra("interview_date", currentDate);

            // Try to specify the form ID
            intent.putExtra("form_id", formId);

            Log.d(TAG, "Created ODK form intent with extras");
            return intent;

        } catch (Exception e) {
            Log.e(TAG, "Error creating ODK form with extras", e);
            return createBasicOdkFormIntent(context, formId);
        }
    }

    /**
     * Method 4: Use ODK's web form approach (if available)
     * Creates a web-based form with pre-filled data
     */
    public static Intent createOdkWebFormIntent(Context context, String formId, Individual individual) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(ODK_PACKAGE);

            // Create a data URI with form parameters
            Uri.Builder uriBuilder = Uri.parse("odk://form").buildUpon();
            uriBuilder.appendQueryParameter("formId", formId);
            uriBuilder.appendQueryParameter("permid", individual.getExtId());
            uriBuilder.appendQueryParameter("firstname", individual.getFirstName());
            uriBuilder.appendQueryParameter("lastname", individual.getLastName());

            if (individual.getGender() != null) {
                uriBuilder.appendQueryParameter("gender", individual.getGender().toString());
            }

            intent.setData(uriBuilder.build());

            Log.d(TAG, "Created ODK web form intent");
            return intent;

        } catch (Exception e) {
            Log.e(TAG, "Error creating ODK web form intent", e);
            return createBasicOdkFormIntent(context, formId);
        }
    }

    /**
     * Enhanced XML content creation - same as before
     */
    private static String createEnhancedInstanceXmlContent(String formId, Individual individual) {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<data id=\"").append(escapeXml(formId)).append("\" version=\"1.0\">\n");

        // Add meta section (required by ODK)
        xml.append("  <meta>\n");
        xml.append("    <instanceID/>\n");
        xml.append("  </meta>\n");

        // Add instruction note field if it exists in your form
        xml.append("  <instruction_note/>\n");

        // Core individual data - matching your form fields exactly
        xml.append("  <permid>").append(escapeXml(individual.getExtId())).append("</permid>\n");

        xml.append("  <firstname>");
        xml.append(escapeXml(individual.getFirstName() != null ? individual.getFirstName() : ""));
        xml.append("</firstname>\n");

        xml.append("  <lastname>");
        xml.append(escapeXml(individual.getLastName() != null ? individual.getLastName() : ""));
        xml.append("</lastname>\n");

        // Optional fields - only include if they have values
        if (individual.getGender() != null) {
            xml.append("  <gender>").append(escapeXml(individual.getGender().toString())).append("</gender>\n");
        }

        if (individual.getDob() != null && !individual.getDob().isEmpty()) {
            xml.append("  <dob>").append(escapeXml(individual.getDob())).append("</dob>\n");
        }

        if (individual.getVillage() != null && !individual.getVillage().isEmpty()) {
            xml.append("  <village>").append(escapeXml(individual.getVillage())).append("</village>\n");
        }

        if (individual.getHohID() != null && !individual.getHohID().isEmpty()) {
            xml.append("  <hhid>").append(escapeXml(individual.getHohID())).append("</hhid>\n");
        }

        if (individual.getCompno() != null && !individual.getCompno().isEmpty()) {
            xml.append("  <compno>").append(escapeXml(individual.getCompno())).append("</compno>\n");
        }

        if (individual.getUuid() != null && !individual.getUuid().isEmpty()) {
            xml.append("  <individual_uuid>").append(escapeXml(individual.getUuid())).append("</individual_uuid>\n");
        }

        // System fields
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        xml.append("  <interview_date>").append(currentDate).append("</interview_date>\n");

        // Add start and end time fields if your form has them
        xml.append("  <start/>\n");
        xml.append("  <end/>\n");
        xml.append("  <deviceid/>\n");

        xml.append("</data>");

        return xml.toString();
    }

    /**
     * Enhanced XML escaping with null safety - same as before
     */
    private static String escapeXml(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Sanitize filename to remove invalid characters - same as before
     */
    private static String sanitizeFileName(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Improved basic ODK form intent - same as before
     */
    public static Intent createBasicOdkFormIntent(Context context, String formId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(ODK_PACKAGE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            String formUri = ODK_FORMS_PROVIDER + "/" + formId;
            intent.setData(Uri.parse(formUri));
            Log.d(TAG, "Created basic ODK form intent with URI: " + formUri);
        } catch (Exception e) {
            intent.setType("vnd.android.cursor.dir/vnd.odk.form");
            Log.d(TAG, "Created generic ODK form intent");
        }

        return intent;
    }

    // ... rest of the utility methods remain the same as before ...

    public static boolean validateIndividualData(Individual individual) {
        if (individual == null) {
            Log.e(TAG, "Individual is null");
            return false;
        }

        if (individual.getExtId() == null || individual.getExtId().trim().isEmpty()) {
            Log.e(TAG, "Permanent ID (extId) is required but missing");
            return false;
        }

        if (individual.getFirstName() == null || individual.getFirstName().trim().isEmpty()) {
            Log.e(TAG, "First name is required but missing");
            return false;
        }

        if (individual.getLastName() == null || individual.getLastName().trim().isEmpty()) {
            Log.e(TAG, "Last name is required but missing");
            return false;
        }

        Log.d(TAG, "Individual data validation passed for: " + individual.getExtId());
        return true;
    }

    public static boolean isOdkCollectInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(ODK_PACKAGE, 0);
            Log.d(TAG, "ODK Collect is installed");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "ODK Collect not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if there are pending forms for an individual
     */
    public static boolean hasPendingForms(Context context, Individual individual) {
        try {
            String[] pendingForms = getPendingFormsForIndividual(context, individual);
            return pendingForms.length > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking for pending forms", e);
            return false;
        }
    }

    /**
     * Get pending forms for a specific individual
     */
    public static String[] getPendingFormsForIndividual(Context context, Individual individual) {
        List<String> pendingForms = new ArrayList<>();

        try {
            // Query ODK's instances provider for incomplete forms
            Uri instancesUri = Uri.parse("content://org.odk.collect.android.provider.odk.instances/instances");
            String[] projection = {"_id", "displayName", "status", "jrFormId"};
            String selection = "status != ? AND displayName LIKE ?";
            String[] selectionArgs = {"complete", "%" + individual.getExtId() + "%"};

            Cursor cursor = context.getContentResolver().query(
                    instancesUri, projection, selection, selectionArgs, null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow("displayName"));
                    String formId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                    pendingForms.add(formId + ": " + displayName);
                }
                cursor.close();
            }

        } catch (SecurityException e) {
            Log.w(TAG, "No permission to access ODK instances", e);
        } catch (Exception e) {
            Log.e(TAG, "Error querying pending forms", e);
        }

        return pendingForms.toArray(new String[0]);
    }

    /**
     * Return to ODK Collect with context about the individual
     */
    public static void returnToOdk(Activity activity, Individual individual) {
        try {
            Intent intent = new Intent();
            intent.setPackage(ODK_PACKAGE);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("vnd.android.cursor.dir/vnd.odk.instance");

            // Add individual context as extras
            intent.putExtra("individual_id", individual.getExtId());
            intent.putExtra("return_context", "pending_forms");

            activity.startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error returning to ODK", e);

            // Fallback: just open ODK Collect
            try {
                Intent fallbackIntent = activity.getPackageManager()
                        .getLaunchIntentForPackage(ODK_PACKAGE);
                if (fallbackIntent != null) {
                    activity.startActivity(fallbackIntent);
                }
            } catch (Exception fallbackError) {
                Log.e(TAG, "Cannot open ODK Collect", fallbackError);
            }
        }
    }
}