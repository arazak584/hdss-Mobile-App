package org.openhds.hdsscapture.odk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.entity.Individual;

import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.OdkFormViewHolder> {
    private List<OdkForm> odkForms;
    private IndividualSharedViewModel individualSharedViewModel;
    private Individual selectedIndividual; // Direct reference
    private static final String ODK_PACKAGE = "org.odk.collect.android";
    private static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String[] PROJECTION = {"_id", "jrFormId", "displayName", "formFilePath"};
    private static final String TAG = "OdkFormAdapter";
    private static final int ODK_REQUEST_CODE = 1001;

    public OdkFormAdapter(List<OdkForm> odkForms, IndividualSharedViewModel individualSharedViewModel) {
        this.odkForms = odkForms;
        this.individualSharedViewModel = individualSharedViewModel;
    }

    // Constructor with direct individual reference (alternative approach)
    public OdkFormAdapter(List<OdkForm> odkForms, Individual selectedIndividual) {
        this.odkForms = odkForms;
        this.selectedIndividual = selectedIndividual;
        this.individualSharedViewModel = null;
    }

    // Alternative constructor for backward compatibility
    public OdkFormAdapter(List<OdkForm> odkForms) {
        this.odkForms = odkForms;
        this.individualSharedViewModel = null;
    }

    @NonNull
    @Override
    public OdkFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_odk_form, parent, false);
        return new OdkFormViewHolder(view, individualSharedViewModel, selectedIndividual);
    }

    @Override
    public void onBindViewHolder(@NonNull OdkFormViewHolder holder, int position) {
        OdkForm form = odkForms.get(position);
        holder.bind(form);
    }

    @Override
    public int getItemCount() {
        return odkForms == null ? 0 : odkForms.size();
    }

    public void setOdkForms(List<OdkForm> odkForms) {
        this.odkForms = odkForms;
        notifyDataSetChanged();
    }

    static class OdkFormViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView formIdTextView;
        OdkForm currentForm;
        Context context;
        private IndividualSharedViewModel individualSharedViewModel;
        private Individual directSelectedIndividual;

        public OdkFormViewHolder(@NonNull View itemView, IndividualSharedViewModel sharedViewModel, Individual selectedIndividual) {
            super(itemView);
            formIdTextView = itemView.findViewById(R.id.text_odk_form_id);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            this.individualSharedViewModel = sharedViewModel;
            this.directSelectedIndividual = selectedIndividual;
        }

        public void bind(OdkForm form) {
            currentForm = form;
            formIdTextView.setText(form.getFormName());
        }

        // Updated onClick method for OdkFormViewHolder in OdkFormAdapter
        @Override
        public void onClick(View view) {
            Individual selectedIndividual = null;

            // Try to get individual from direct reference first
            if (directSelectedIndividual != null) {
                selectedIndividual = directSelectedIndividual;
                Log.d(TAG, "Using direct individual reference: " + selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName());
            }
            // Fall back to ViewModel
            else if (individualSharedViewModel != null) {
                selectedIndividual = individualSharedViewModel.getCurrentSelectedIndividual();
                Log.d(TAG, "Retrieved individual from ViewModel: " +
                        (selectedIndividual != null ? selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName() : "null"));
            }

            if (!OdkUtilsPrefilledInstance.isOdkCollectInstalled(context)) {
                Toast.makeText(context, "ODK Collect is not installed", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "Launching ODK form for: " + selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName());

            // USE THE WORKING METHOD - Launch with URL parameters
            launchOdkFormWithUrlParameters(selectedIndividual);
        }

        /**
         * WORKING METHOD 1: Launch ODK form with URL parameters
         * This is the most reliable way to pass data to ODK forms
         */
        private void launchOdkFormWithUrlParameters(Individual selectedIndividual) {
            try {
                // Get ODK form database ID
                int odkFormId = getOdkFormId(context, currentForm.getFormID());
                if (odkFormId == -1) {
                    Toast.makeText(context, "Form not found in ODK Collect", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create intent with ACTION_EDIT
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setPackage(ODK_PACKAGE);

                // Build URI with query parameters - THIS IS THE KEY!
                Uri.Builder uriBuilder = Uri.parse("content://org.odk.collect.android.provider.odk.forms/forms/" + odkFormId).buildUpon();

                // Add parameters with proper encoding
                addEncodedParameter(uriBuilder, "permid", selectedIndividual.getExtId());
                addEncodedParameter(uriBuilder, "firstname", selectedIndividual.getFirstName());
                addEncodedParameter(uriBuilder, "lastname", selectedIndividual.getLastName());
                if (selectedIndividual.getGender() != null) {
                    addEncodedParameter(uriBuilder, "gender", selectedIndividual.getGender().toString());
                }
                if (selectedIndividual.getDob() != null && !selectedIndividual.getDob().isEmpty()) {
                    addEncodedParameter(uriBuilder, "dob", selectedIndividual.getDob());
                }
                if (selectedIndividual.getVillage() != null && !selectedIndividual.getVillage().isEmpty()) {
                    addEncodedParameter(uriBuilder, "village", selectedIndividual.getVillage());
                }
                if (selectedIndividual.getHohID() != null && !selectedIndividual.getHohID().isEmpty()) {
                    addEncodedParameter(uriBuilder, "hhid", selectedIndividual.getHohID());
                }
                if (selectedIndividual.getCompno() != null && !selectedIndividual.getCompno().isEmpty()) {
                    addEncodedParameter(uriBuilder, "compno", selectedIndividual.getCompno());
                }

                // Add all individual data as query parameters
//                uriBuilder.appendQueryParameter("permid", selectedIndividual.getExtId());
//                uriBuilder.appendQueryParameter("firstname", selectedIndividual.getFirstName() != null ? selectedIndividual.getFirstName() : "");
//                uriBuilder.appendQueryParameter("lastname", selectedIndividual.getLastName() != null ? selectedIndividual.getLastName() : "");
//
//                if (selectedIndividual.getGender() != null) {
//                    uriBuilder.appendQueryParameter("gender", selectedIndividual.getGender().toString());
//                }
//
//                if (selectedIndividual.getDob() != null && !selectedIndividual.getDob().isEmpty()) {
//                    uriBuilder.appendQueryParameter("dob", selectedIndividual.getDob());
//                }
//
//                if (selectedIndividual.getVillage() != null && !selectedIndividual.getVillage().isEmpty()) {
//                    uriBuilder.appendQueryParameter("village", selectedIndividual.getVillage());
//                }
//
//                if (selectedIndividual.getHohID() != null && !selectedIndividual.getHohID().isEmpty()) {
//                    uriBuilder.appendQueryParameter("hhid", selectedIndividual.getHohID());
//                }
//
//                if (selectedIndividual.getCompno() != null && !selectedIndividual.getCompno().isEmpty()) {
//                    uriBuilder.appendQueryParameter("compno", selectedIndividual.getCompno());
//                }

                // Add current date
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                uriBuilder.appendQueryParameter("interview_date", currentDate);

                // Set the URI with parameters
                intent.setData(uriBuilder.build());

                Log.d(TAG, "Launching ODK with URI: " + uriBuilder.build().toString());

                // Launch the intent
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, ODK_REQUEST_CODE);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                Toast.makeText(context, "ODK form launched with data for " + selectedIndividual.getFirstName(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e(TAG, "Failed to launch ODK with URL parameters", e);
                // Fallback to basic launch
                launchOdkWithExternalInstance(selectedIndividual);
            }
        }

        private void addEncodedParameter(Uri.Builder builder, String key, String value) {
            if (value != null && !value.isEmpty()) {
                builder.appendQueryParameter(key, URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }

        /**
         * WORKING METHOD 2: Use ODK's external app integration
         * Create an external instance and launch it
         */
        private void launchOdkWithExternalInstance(Individual selectedIndividual) {
            try {
                // Create external instance directory
                File externalDir = new File(context.getExternalFilesDir(null), "odk_external");
                if (!externalDir.exists() && !externalDir.mkdirs()) {
                    Log.e(TAG, "Failed to create external directory");
                    return;
                }

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String instanceDirName = currentForm.getFormID() + "_" + selectedIndividual.getExtId() + "_" + timestamp;
                File instanceDir = new File(externalDir, instanceDirName);

                if (!instanceDir.mkdirs()) {
                    Log.e(TAG, "Failed to create instance directory");
                    return;
                }

                // Create XML instance with data
                File instanceFile = new File(instanceDir, instanceDirName + ".xml");
                String xmlContent = createInstanceXmlWithData(selectedIndividual);

                FileWriter writer = new FileWriter(instanceFile);
                writer.write(xmlContent);
                writer.close();

                // Create intent to open the instance
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setPackage(ODK_PACKAGE);

                // Use FileProvider for Android 7+
                Uri instanceUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider", instanceFile);

                intent.setDataAndType(instanceUri, "text/xml");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                context.startActivity(intent);

                Toast.makeText(context, "ODK instance created with pre-filled data", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e(TAG, "Failed to create external instance", e);
                launchOdkFormBasic(selectedIndividual);
            }
        }

        /**
         * Create XML instance content with individual data
         */
        private String createInstanceXmlWithData(Individual individual) {
            StringBuilder xml = new StringBuilder();

            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<data id=\"").append(currentForm.getFormID()).append("\" version=\"1.0\">\n");

            // Required meta section
            xml.append("  <meta>\n");
            xml.append("    <instanceID/>\n");
            xml.append("  </meta>\n");

            // Individual data - THESE FIELD NAMES MUST MATCH YOUR ODK FORM
            xml.append("  <permid>").append(escapeXml(individual.getExtId())).append("</permid>\n");
            xml.append("  <firstname>").append(escapeXml(individual.getFirstName())).append("</firstname>\n");
            xml.append("  <lastname>").append(escapeXml(individual.getLastName())).append("</lastname>\n");

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

            // Add current date
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            xml.append("  <interview_date>").append(currentDate).append("</interview_date>\n");

            xml.append("</data>");

            return xml.toString();
        }

        /**
         * Escape XML special characters
         */
        private String escapeXml(String text) {
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
         * Fallback method - basic ODK launch
         */
        private void launchOdkFormBasic(Individual selectedIndividual) {
            try {
                int odkFormId = getOdkFormId(context, currentForm.getFormID());

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setPackage(ODK_PACKAGE);

                if (odkFormId != -1) {
                    Uri formUri = Uri.parse("content://org.odk.collect.android.provider.odk.forms/forms/" + odkFormId);
                    intent.setData(formUri);
                } else {
                    // Open form picker
                    intent.setType("vnd.android.cursor.dir/vnd.odk.form");
                }

                context.startActivity(intent);

                Toast.makeText(context,
                        "Form opened - please enter data manually for: " + selectedIndividual.getFirstName(),
                        Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(TAG, "Basic launch also failed", e);
                Toast.makeText(context, "Unable to launch ODK form", Toast.LENGTH_SHORT).show();
            }
        }

        private int getOdkFormId(Context context, String jrFormId) {
            if (jrFormId == null) {
                Log.e(TAG, "jrFormId is null");
                return -1;
            }

            Uri formUri = Uri.parse(ODK_FORMS_PROVIDER);
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(formUri, PROJECTION, null, null, null);
                if (cursor == null) {
                    Log.e(TAG, "Cursor is null - ODK Collect may not be installed or accessible");
                    return -1;
                }

                if (!cursor.moveToFirst()) {
                    Log.e(TAG, "No forms found in cursor");
                    return -1;
                }

                Log.d(TAG, "Total forms found: " + cursor.getCount());

                do {
                    String retrievedFormId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow("displayName"));

                    Log.d(TAG, "Found form - jrFormId: " + retrievedFormId +
                            ", id: " + id + ", displayName: " + displayName);

                    if (jrFormId.equals(retrievedFormId)) {
                        Log.d(TAG, "Matching form found with id: " + id);
                        return id;
                    }
                } while (cursor.moveToNext());

                Log.w(TAG, "No matching form found for jrFormId: " + jrFormId);

            } catch (SecurityException e) {
                Log.e(TAG, "Security exception - no permission to access ODK Collect data", e);
            } catch (Exception e) {
                Log.e(TAG, "Error querying ODK forms", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return -1;
        }

    }
}