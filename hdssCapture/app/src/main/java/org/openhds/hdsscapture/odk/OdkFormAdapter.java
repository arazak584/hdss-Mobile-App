package org.openhds.hdsscapture.odk;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.util.List;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.OdkFormViewHolder> {
    private List<OdkForm> odkForms;
    private static final String ODK_PACKAGE = "org.odk.collect.android";
    private static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String[] PROJECTION = {"_id", "jrFormId"};
    private static final String TAG = "OdkFormAdapter";

    public OdkFormAdapter(List<OdkForm> odkForms) {
        this.odkForms = odkForms;
    }

    @NonNull
    @Override
    public OdkFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_odk_form, parent, false);
        return new OdkFormViewHolder(view);
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

        public OdkFormViewHolder(@NonNull View itemView) {
            super(itemView);
            formIdTextView = itemView.findViewById(R.id.text_odk_form_id);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        public void bind(OdkForm form) {
            currentForm = form;
            formIdTextView.setText(form.getFormID());
        }

        @Override
        public void onClick(View view) {
            Individual selectedIndividual = HouseMembersFragment.selectedIndividual;
            if (selectedIndividual == null) {
                Toast.makeText(context, "No individual selected", Toast.LENGTH_SHORT).show();
                return;
            }

            launchOdkForm(selectedIndividual);
        }

        private void launchOdkForm(Individual selectedIndividual) {
            if (selectedIndividual == null) {
                Toast.makeText(context, "No individual selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if ODK Collect is installed
            if (!OdkUtils.isOdkCollectInstalled(context)) {
                Toast.makeText(context, "ODK Collect is not installed", Toast.LENGTH_LONG).show();
                return;
            }

            int formId = getOdkFormId(context, currentForm.getFormID());
            if (formId == -1) {
                Toast.makeText(context, "Form not found in ODK Collect", Toast.LENGTH_LONG).show();
                return;
            }

            String formUri = ODK_FORMS_PROVIDER + "/" + formId;

            try {
                // Use external app integration to pass data
                Intent intent = OdkUtils.createExternalAppIntent(context, formUri, selectedIndividual);

                // Log the intent details for debugging
                Log.d(TAG, "Launching ODK form with URI: " + formUri);
                Log.d(TAG, "Individual data: " + selectedIndividual.getFirstName() + " " + selectedIndividual.getLastName());
                Log.d(TAG, "Launching form: " + currentForm.getFormID() + " for " + selectedIndividual.getFirstName());

                intent.putExtra("firstname", "John");
                context.startActivity(intent);

            } catch (Exception e) {
                Log.e(TAG, "Error launching ODK form", e);
                Toast.makeText(context, "Error launching ODK form: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // Alternative method for testing - launches form picker
        private void launchOdkFormPicker() {
            try {
                Intent intent = OdkUtils.createFormPickerIntent();
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Error launching ODK form picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error launching ODK form picker", e);
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
                    Log.e(TAG, "Cursor is null");
                    return -1;
                }

                if (!cursor.moveToFirst()) {
                    Log.e(TAG, "No forms found in cursor");
                    return -1;
                }

                do {
                    String retrievedFormId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    Log.d(TAG, "Found form with jrFormId: " + retrievedFormId + " and id: " + id);

                    if (jrFormId.equals(retrievedFormId)) {
                        Log.d(TAG, "Matching form found with id: " + id);
                        return id;
                    }
                } while (cursor.moveToNext());
            } catch (Exception e) {
                Log.e(TAG, "Error querying ODK forms", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            Log.e(TAG, "Form not found");
            return -1;
        }
    }
}