package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.odk.OdkForm;

import java.util.ArrayList;
import java.util.List;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.FormViewHolder> {

    private List<OdkForm> formList;
    private OnFormClickListener listener;

    public interface OnFormClickListener {
        void onFormClick(OdkForm form);
    }

    public OdkFormAdapter(OnFormClickListener listener) {
        this.formList = new ArrayList<>();
        this.listener = listener;
    }

    public void setFormList(List<OdkForm> formList) {
        this.formList = formList != null ? formList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_odk_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        OdkForm form = formList.get(position);
        holder.bind(form, listener);
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    static class FormViewHolder extends RecyclerView.ViewHolder {
        private TextView formNameText;
        private TextView formDescText;
        private AppCompatButton openFormButton;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            formNameText = itemView.findViewById(R.id.form_name);
            formDescText = itemView.findViewById(R.id.form_desc);
            openFormButton = itemView.findViewById(R.id.open_form_button);
        }

        public void bind(OdkForm form, OnFormClickListener listener) {
            formNameText.setText(form.formName != null ? form.formName : "Unnamed Form");

            // Build description with age and gender criteria
            StringBuilder desc = new StringBuilder();
            if (form.formDesc != null && !form.formDesc.isEmpty()) {
                desc.append(form.formDesc);
            }

            // Add criteria info
            List<String> criteria = new ArrayList<>();
            if (form.gender != null && form.gender != 0) {
                criteria.add("Gender: " + (form.gender == 1 ? "Male" : "Female"));
            }
            if (form.minAge != null || form.maxAge != null) {
                String ageRange = "Age: ";
                if (form.minAge != null && form.maxAge != null) {
                    ageRange += form.minAge + "-" + form.maxAge;
                } else if (form.minAge != null) {
                    ageRange += form.minAge + "+";
                } else {
                    ageRange += "≤" + form.maxAge;
                }
                criteria.add(ageRange);
            }

            if (!criteria.isEmpty()) {
                if (desc.length() > 0) desc.append("\n");
                desc.append(String.join(" | ", criteria));
            }

            formDescText.setText(desc.toString());
            formDescText.setVisibility(desc.length() > 0 ? View.VISIBLE : View.GONE);

            openFormButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFormClick(form);
                }
            });
        }
    }
}