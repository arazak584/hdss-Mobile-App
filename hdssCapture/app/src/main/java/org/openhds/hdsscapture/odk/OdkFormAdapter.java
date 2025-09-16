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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.OdkFormViewHolder> {
    private List<OdkForm> odkForms;
    private IndividualSharedViewModel individualSharedViewModel;
    private Individual selectedIndividual;
    private static final String ODK_PACKAGE = "org.odk.collect.android";
    private static final String ODK_FORMS_PROVIDER = "content://org.odk.collect.android.provider.odk.forms/forms";
    private static final String[] PROJECTION = {"_id", "jrFormId", "displayName", "formFilePath", "jrVersion"};
    private static final String TAG = "OdkFormAdapter";
    private static final int ODK_REQUEST_CODE = 1001;

    public OdkFormAdapter(List<OdkForm> odkForms, Individual selectedIndividual) {
        this.odkForms = odkForms;
        this.selectedIndividual = selectedIndividual;
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
        private Map<String, Integer> formIdCache = new HashMap<>();

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

        @Override
        public void onClick(View view) {
            Individual selectedIndividual = directSelectedIndividual;

            if (selectedIndividual == null) {
                Toast.makeText(context, "No individual selected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!OdkUtilsPrefilledInstance.isOdkCollectInstalled(context)) {
                Toast.makeText(context, "ODK Collect is not installed", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "Launching ODK form for: " + selectedIndividual.getFirstName());
            launchOdkFormWithMultipleFallbacks(selectedIndividual);
        }

        private void launchOdkFormWithMultipleFallbacks(Individual selectedIndividual) {
            try {
                Intent intent = OdkUtilsPrefilledInstance.createOdkFormWithPrefilledInstance(
                        context, currentForm.getFormID(), selectedIndividual);

                if (intent != null) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, ODK_REQUEST_CODE);
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    Toast.makeText(context, "Opening ODK form...", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch ODK form", e);
                Toast.makeText(context, "Could not open form: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}