package org.openhds.hdsscapture.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.fragment.DeathFragment;
import org.openhds.hdsscapture.fragment.InmigrationFragment;
import org.openhds.hdsscapture.fragment.OutmigrationFragment;
import org.openhds.hdsscapture.fragment.PregnancyFragment;

import java.util.ArrayList;
import java.util.List;

public class CompletedFormsAdapter extends RecyclerView.Adapter<CompletedFormsAdapter.FormViewHolder> {

    private final FragmentActivity activity;
    private final List<CompletedForm> formList;
    private final List<CompletedForm> originalList;

    public CompletedFormsAdapter(List<CompletedForm> formList, FragmentActivity activity) {
        this.formList = new ArrayList<>(formList);
        this.originalList = new ArrayList<>(formList);
        this.activity = activity;
    }

    @Override
    public FormViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_completed_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FormViewHolder holder, int position) {
        CompletedForm form = formList.get(position);
        holder.textView.setText(form.displayText);

        holder.itemView.setOnClickListener(v -> {
            Fragment fragment = null;
            switch (form.formType) {
                case "Pregnancy":
                    fragment = PregViewFragment.newInstance(form.uuid);
                    break;
//                case "Death":
//                    fragment = DeathFragment.newInstance(form.uuid);
//                    break;
//                case "Inmigration":
//                    fragment = InmigrationFragment.newInstance(form.uuid);
//                    break;
//                case "Outmigration":
//                    fragment = OutmigrationFragment.newInstance(form.uuid);
//                    break;
            }

            if (fragment != null) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    public void filter(String query) {
        formList.clear();
        if (query == null || query.trim().isEmpty()) {
            formList.addAll(originalList);
        } else {
            String lower = query.toLowerCase();
            for (CompletedForm form : originalList) {
                if (form.displayText.toLowerCase().contains(lower)) {
                    formList.add(form);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public FormViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.formText);
        }
    }
}
