package org.openhds.hdsscapture.Adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.OdkUtils;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.odk.OdkForm;

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

            int formId = getOdkFormId(context, currentForm.getFormID());
            if (formId == -1) {
                Toast.makeText(context, "Form not found in ODK Collect", Toast.LENGTH_LONG).show();
                return;
            }

            String formUri = ODK_FORMS_PROVIDER + "/" + formId;
            try {
                Intent intent = OdkUtils.createOdkFormIntent(formUri, selectedIndividual);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Error launching ODK form: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error launching ODK form", e);
            }
        }

        private int getOdkFormId(Context context, String jrFormId) {
            if (jrFormId == null) return -1;

            Uri formUri = Uri.parse(ODK_FORMS_PROVIDER);
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(formUri, PROJECTION, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String retrievedFormId = cursor.getString(cursor.getColumnIndexOrThrow("jrFormId"));
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

                        if (jrFormId.equals(retrievedFormId)) {
                            return id;
                        }
                    }
                }
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