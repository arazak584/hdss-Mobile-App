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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OdkUtilsPrefilledInstance {
    public static final String ODK_PACKAGE = "org.odk.collect.android";
    public static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String TAG = "OdkUtilsPrefilled";
    private static final String FILE_PROVIDER_AUTHORITY = "org.openhds.hdsscapture.fileprovider";

    private static Map<String, Boolean> formValidationCache = new HashMap<>();
    private static Map<String, String> odkFormIdMapping = new HashMap<>();
    private OdkForm odkForm;

    public static List<OdkForm> validateFormsInOdkCollect(Context context, List<OdkForm> appForms) {
        List<OdkForm> validForms = new ArrayList<>();
        if (appForms == null || appForms.isEmpty()) return validForms;

        Map<String, String> odkFormsMap = getAllOdkForms(context);
        Log.d(TAG, "Validating " + appForms.size() + " app forms against " + odkFormsMap.size() + " ODK forms");

        for (OdkForm appForm : appForms) {
            if (isFormValidInOdk(appForm.getFormID(), odkFormsMap)) {
                validForms.add(appForm);
                Log.d(TAG, "✓ Valid: " + appForm.getFormID());
            } else {
                Log.w(TAG, "✗ Invalid: " + appForm.getFormID());
            }
        }
        return validForms;
    }

    private static Map<String, String> getAllOdkForms(Context context) {
        Map<String, String> odkFormsMap = new HashMap<>();
        Uri formUri = Uri.parse(ODK_FORMS_PROVIDER);
        String[] projection = {"_id", "jrFormId", "displayName"};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(formUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String jrFormId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow("displayName"));
                    if (jrFormId != null) {
                        odkFormsMap.put(jrFormId, displayName);
                        odkFormIdMapping.put(jrFormId, cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading ODK forms", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return odkFormsMap;
    }

    private static boolean isFormValidInOdk(String appFormId, Map<String, String> odkFormsMap) {
        if (appFormId == null || appFormId.isEmpty()) return false;

        if (formValidationCache.containsKey(appFormId)) {
            return formValidationCache.get(appFormId);
        }

        boolean isValid = false;

        // Multiple matching strategies
        for (String odkFormId : odkFormsMap.keySet()) {
            if (appFormId.equals(odkFormId) ||
                    appFormId.equalsIgnoreCase(odkFormId) ||
                    normalizeFormId(appFormId).equals(normalizeFormId(odkFormId))) {
                isValid = true;
                break;
            }
        }

        formValidationCache.put(appFormId, isValid);
        return isValid;
    }

    private static String normalizeFormId(String formId) {
        return formId != null ? formId.replaceAll("[_\\s\\-\\.]", "").toLowerCase() : "";
    }

    public static Intent createOdkFormWithPrefilledInstance(Context context, String formId, Individual individual) {
        if (!validateIndividualData(individual) || formId == null) {
            return createBasicOdkFormIntent(context, formId);
        }

        Intent intent = tryMultiplePrefillingApproaches(context, formId, individual);
        return intent != null ? intent : createBasicOdkFormIntent(context, formId);
    }

    private static Intent tryMultiplePrefillingApproaches(Context context, String formId, Individual individual) {
        Intent intent = createOdkFormWithUrlParameters(context, formId, individual);
        if (intent != null) return intent;

        intent = createOdkFormWithExtras(context, formId, individual);
        if (intent != null) return intent;

        return createOdkFormWithInstanceFile(context, formId, individual);
    }

    private static Intent createOdkFormWithUrlParameters(Context context, String formId, Individual individual) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(ODK_PACKAGE);

            String uriString = "content://org.odk.collect.android.provider.odk.forms/forms?formId=" + formId;
            Uri.Builder uriBuilder = Uri.parse(uriString).buildUpon();

            addIndividualParameters(uriBuilder, individual);
            intent.setData(uriBuilder.build());

            if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                Log.d(TAG, "URL parameters intent: " + uriBuilder.build());
                return intent;
            }
        } catch (Exception e) {
            Log.e(TAG, "URL method failed", e);
        }
        return null;
    }

    private static Intent createOdkFormWithExtras(Context context, String formId, Individual individual) {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setPackage(ODK_PACKAGE);

            String dbId = odkFormIdMapping.get(formId);
            if (dbId != null) {
                Uri formUri = Uri.parse(ODK_FORMS_PROVIDER + "/" + dbId);
                intent.setData(formUri);
            }

            addIndividualExtras(intent, individual);
            intent.putExtra("form_id", formId);

            if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                Log.d(TAG, "Extras intent created");
                return intent;
            }
        } catch (Exception e) {
            Log.e(TAG, "Extras method failed", e);
        }
        return null;
    }

    private static Intent createOdkFormWithInstanceFile(Context context, String formId, Individual individual) {
        try {
            String instancePath = createPrefilledInstanceFile(context, formId, individual);
            if (instancePath == null) return null;

            File instanceFile = new File(instancePath);
            Uri instanceUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, instanceFile);

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setDataAndType(instanceUri, "text/xml");
            intent.setPackage(ODK_PACKAGE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            return intent;
        } catch (Exception e) {
            Log.e(TAG, "Instance file method failed", e);
            return null;
        }
    }

    private static void addIndividualParameters(Uri.Builder uriBuilder, Individual individual) {
        uriBuilder.appendQueryParameter("permid", safeString(individual.getExtId()));
        uriBuilder.appendQueryParameter("firstname", safeString(individual.getFirstName()));
        uriBuilder.appendQueryParameter("lastname", safeString(individual.getLastName()));
        if (individual.getGender() != null) {
            uriBuilder.appendQueryParameter("gender", individual.getGender().toString());
        }
        uriBuilder.appendQueryParameter("dob", safeString(individual.getDob()));
        uriBuilder.appendQueryParameter("village", safeString(individual.getVillage()));
        uriBuilder.appendQueryParameter("hhid", safeString(individual.getHohID()));
        uriBuilder.appendQueryParameter("compno", safeString(individual.getCompno()));
        uriBuilder.appendQueryParameter("interview_date",
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
    }

    private static void addIndividualExtras(Intent intent, Individual individual) {
        intent.putExtra("permid", individual.getExtId());
        intent.putExtra("firstname", individual.getFirstName());
        intent.putExtra("lastname", individual.getLastName());
        if (individual.getGender() != null) {
            intent.putExtra("gender", individual.getGender().toString());
        }
        intent.putExtra("dob", individual.getDob());
        intent.putExtra("village", individual.getVillage());
        intent.putExtra("hhid", individual.getHohID());
        intent.putExtra("compno", individual.getCompno());
        intent.putExtra("interview_date",
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
    }

    private static String createPrefilledInstanceFile(Context context, String formId, Individual individual) {
        try {
            File appFilesDir = context.getExternalFilesDir("odk_instances");
            if (appFilesDir == null || (!appFilesDir.exists() && !appFilesDir.mkdirs())) {
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String instanceDirName = formId + "_" + sanitizeFileName(individual.getExtId()) + "_" + timestamp;
            File instanceDir = new File(appFilesDir, instanceDirName);

            if (!instanceDir.mkdirs() && !instanceDir.exists()) return null;

            File instanceFile = new File(instanceDir, instanceDirName + ".xml");
            String xmlContent = createInstanceXmlContent(formId, individual);

            FileWriter writer = new FileWriter(instanceFile);
            writer.write(xmlContent);
            writer.close();

            Log.d(TAG, "Instance file created: " + instanceFile.getAbsolutePath());
            return instanceFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error creating instance file", e);
            return null;
        }
    }

    private static String createInstanceXmlContent(String formId, Individual individual) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<data id=\"").append(escapeXml(formId)).append("\" version=\"1.0\">\n");
        xml.append("  <meta>\n    <instanceID/>\n  </meta>\n");
        xml.append("  <permid>").append(escapeXml(individual.getExtId())).append("</permid>\n");
        xml.append("  <firstname>").append(escapeXml(individual.getFirstName())).append("</firstname>\n");
        xml.append("  <lastname>").append(escapeXml(individual.getLastName())).append("</lastname>\n");
        if (individual.getGender() != null) {
            xml.append("  <gender>").append(escapeXml(individual.getGender().toString())).append("</gender>\n");
        }
        xml.append("  <dob>").append(escapeXml(individual.getDob())).append("</dob>\n");
        xml.append("  <village>").append(escapeXml(individual.getVillage())).append("</village>\n");
        xml.append("  <hhid>").append(escapeXml(individual.getHohID())).append("</hhid>\n");
        xml.append("  <compno>").append(escapeXml(individual.getCompno())).append("</compno>\n");
        xml.append("  <interview_date>")
                .append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))
                .append("</interview_date>\n");
        xml.append("</data>");
        return xml.toString();
    }

    private static Intent createBasicOdkFormIntent(Context context, String formId) {
        // 1. Look up the real row id (_id) in Collect’s forms.db
        String dbId = odkFormIdMapping.get(formId);
        if (dbId == null) {                 // form not on device
            Log.w(TAG, "createBasicOdkFormIntent: formId " + formId + " not found in mapping");
            // fallback – open picker
            Intent pick = new Intent(Intent.ACTION_VIEW);
            pick.setPackage(ODK_PACKAGE);
            pick.setType("vnd.android.cursor.dir/vnd.odk.form");
            return pick;
        }

        // 2. Build the *form-specific* URI that Collect v2023.2+ expects
        Uri formUri = Uri.parse(
                "content://org.odk.collect.android.provider.odk.forms/forms/" + dbId);

        // 3. ACTION_EDIT jumps straight to that blank form
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(formUri);
        intent.setPackage(ODK_PACKAGE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }

    public static boolean validateIndividualData(Individual individual) {
        if (individual == null) return false;
        if (individual.getExtId() == null || individual.getExtId().trim().isEmpty()) return false;
        if (individual.getFirstName() == null || individual.getFirstName().trim().isEmpty()) return false;
        if (individual.getLastName() == null || individual.getLastName().trim().isEmpty()) return false;
        return true;
    }

    public static boolean isOdkCollectInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(ODK_PACKAGE, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasPendingForms(Context context, Individual individual) {
        String[] pending = getPendingFormsForIndividual(context, individual);
        return pending.length > 0;
    }

    public static String[] getPendingFormsForIndividual(Context context, Individual individual) {
        List<String> pendingForms = new ArrayList<>();
        try {
            Uri instancesUri = Uri.parse("content://org.odk.collect.android.provider.odk.instances/instances");
            String[] projection = {"_id", "displayName", "status", "jrFormId"};
            String selection = "status != ? AND displayName LIKE ?";
            String[] selectionArgs = {"complete", "%" + individual.getExtId() + "%"};

            Cursor cursor = context.getContentResolver().query(instancesUri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pendingForms.add(cursor.getString(cursor.getColumnIndexOrThrow("jrFormId")));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking pending forms", e);
        }
        return pendingForms.toArray(new String[0]);
    }

    public static void returnToOdk(Activity activity, Individual individual) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(ODK_PACKAGE);
            intent.setType("vnd.android.cursor.dir/vnd.odk.instance");
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error returning to ODK", e);
        }
    }

    public static void clearFormValidationCache() {
        formValidationCache.clear();
        odkFormIdMapping.clear();
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }

    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String sanitizeFileName(String filename) {
        return filename != null ? filename.replaceAll("[^a-zA-Z0-9._-]", "_") : "unknown";
    }
}