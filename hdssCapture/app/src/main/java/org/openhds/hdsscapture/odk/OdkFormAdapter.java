package org.openhds.hdsscapture.odk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;

import java.util.List;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.FormViewHolder>{

    private List<Form> forms;

    public OdkFormAdapter(List<Form> forms) {
        this.forms = forms;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        Form form = forms.get(position);
        Log.d("OdkFormAdapter", "onBindViewHolder: Form ID: " + form.formID);
        holder.bind(form);
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class FormViewHolder extends RecyclerView.ViewHolder {

        private Button button;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }

        public void bind(Form form) {
            button.setText("Open Form " + form.formID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOdkForm(form.formID, v);
                }
            });
        }
    }

    private void openOdkForm(String formID, View view) {
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.dir/vnd.odk.form");
        Uri specificFormUri = Uri.withAppendedPath(FormsProviderAPI.FormsColumns.CONTENT_URI, formID);
        intent.setData(specificFormUri);
        // Start the activity with the created intent using the context
        context.startActivity(intent);
    }

}
