package org.openhds.hdsscapture.Views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;

import java.util.ArrayList;
import java.util.List;

public class CompletedFormsAdapter extends RecyclerView.Adapter<CompletedFormsAdapter.FormViewHolder> {

    private final Fragment activity;
    private final List<CompletedForm> formList;
    private final List<CompletedForm> originalList;

    public CompletedFormsAdapter(List<CompletedForm> formList, Fragment activity) {
        this.formList = new ArrayList<>(formList);
        this.originalList = new ArrayList<>(formList);
        this.activity = activity;
    }

    @Override
    public FormViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.requireContext()).inflate(R.layout.item_completed_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FormViewHolder holder, int position) {
        CompletedForm form = formList.get(position);
        holder.textView.setText(form.getDisplayText());

        holder.itemView.setOnClickListener(v -> {
            Fragment fragment = null;
            Log.d("CompletedFormsAdapter", "Form type: '" + form.formType + "'");

            switch (form.formType.toLowerCase()) {
                case "pregnancy":
                    fragment = PregViewFragment.newInstance(form.uuid);
                    break;
                case "demographic":
                    fragment = DemoViewFragment.newInstance(form.uuid);
                    break;
                case "amendment":
                    fragment = AmendmentViewFragment.newInstance(form.uuid);
                    break;
                case "death":
                    fragment = DeathViewFragment.newInstance(form.uuid);
                    break;
                case "ses":
                    //Log.d("CompletedFormsAdapter", "Opening SES view with UUID: " + form.uuid);
                    fragment = SocioAViewFragment.newInstance(form.uuid);
                    break;
                case "individual":
                    fragment = IndividualViewFragment.newInstance(form.uuid);
                    break;
                case "inmigration":
                    fragment = InmigrationViewFragment.newInstance(form.uuid);
                    break;
                case "outmigration":
                    fragment = OutmigrationViewFragment.newInstance(form.uuid);
                    break;
                case "listing":
                    Log.d("CompletedFormsAdapter", "Opening Listing view with UUID: " + form.uuid);
                    fragment = ListingViewFragment.newInstance(form.uuid);
                    break;
                case "morbidity":
                    fragment = MorbidityViewFragment.newInstance(form.uuid);
                    break;
                case "relationship":
                    fragment = RelationshipViewFragment.newInstance(form.uuid);
                    break;
                case "membership":
                    fragment = ResidencyViewFragment.newInstance(form.uuid);
                    break;
                case "vaccination":
                    fragment = VaccinationViewFragment.newInstance(form.uuid);
                    break;
                case "pregnancy outcome":
                    fragment = PregnancyOutcomeViewFragment.newInstance(form.uuid);
                    break;
                default:
                    Log.d("CompletedFormsAdapter", "Unknown form type: " + form.formType);
                    break;
            }

            if (fragment != null) {
                FragmentTransaction transaction = activity.getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                //transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Log.d("CompletedFormsAdapter", "Fragment is null for form type: " + form.formType);
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
                if (form.getDisplayText().toLowerCase().contains(lower)) {
                    formList.add(form);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<CompletedForm> newForms) {
        this.originalList.clear();
        this.originalList.addAll(newForms);
        this.formList.clear();
        this.formList.addAll(newForms);
        notifyDataSetChanged();
    }

    // Optional: For better performance, you can use DiffUtil instead of notifyDataSetChanged()
    public void updateDataWithDiff(List<CompletedForm> newForms) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return formList.size();
            }

            @Override
            public int getNewListSize() {
                return newForms.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return formList.get(oldItemPosition).uuid.equals(newForms.get(newItemPosition).uuid);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                CompletedForm oldForm = formList.get(oldItemPosition);
                CompletedForm newForm = newForms.get(newItemPosition);
                return oldForm.getDisplayText().equals(newForm.getDisplayText()) &&
                        oldForm.formType.equals(newForm.formType);
            }
        });

        this.originalList.clear();
        this.originalList.addAll(newForms);
        this.formList.clear();
        this.formList.addAll(newForms);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public FormViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.formText);
        }
    }
}
