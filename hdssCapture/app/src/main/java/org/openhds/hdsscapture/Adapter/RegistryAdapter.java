package org.openhds.hdsscapture.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistryAdapter extends RecyclerView.Adapter<RegistryAdapter.ViewHolder> {

    private List<Individual> individualList;
    private Context context;
    private RegistryViewModel registryViewModel;
    private IndividualViewModel individualViewModel;
    private Socialgroup socialgroup;
    private Map<String, Boolean> checkedStatusMap = new HashMap<>(); // Track checked status

    // Constructor
    public RegistryAdapter(Context context, List<Individual> individualList, RegistryViewModel registryViewModel, IndividualViewModel individualViewModel, Socialgroup socialgroup) {
        this.context = context;
        this.individualList = new ArrayList<>(individualList);
        this.registryViewModel = registryViewModel;
        this.individualViewModel = individualViewModel;
        this.socialgroup = socialgroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_register, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Individual individual = individualList.get(position);
        holder.nameTextView.setText(individual.getFirstName() + " " + individual.getLastName());
        holder.dobTextView.setText(individual.getDob());

        // Retrieve the corresponding Registry for the Individual
        Registry registry = null;
        try {
            registry = registryViewModel.find(individual.getUuid());
            if (registry != null) {
                boolean isChecked = registry.getStatus() == 1;
                holder.checkBox.setOnCheckedChangeListener(null); // Temporarily remove listener
                holder.checkBox.setChecked(isChecked);
                checkedStatusMap.put(individual.getUuid(), isChecked); // Track initial status
                holder.checkBox.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
                    onCheckBoxChanged(individual, isCheckedNew);
                });
            } else {
                holder.checkBox.setOnCheckedChangeListener(null); // Temporarily remove listener
                holder.checkBox.setChecked(false); // Default to unchecked if no registry found
                checkedStatusMap.put(individual.getUuid(), false); // Track initial status
                holder.checkBox.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
                    onCheckBoxChanged(individual, isCheckedNew);
                });
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            holder.checkBox.setOnCheckedChangeListener(null); // Temporarily remove listener
            holder.checkBox.setChecked(false); // Default to unchecked on error
            Toast.makeText(context, "Error retrieving registry data", Toast.LENGTH_SHORT).show();
            return; // Exit early if there's an error
        }
    }

    private void onCheckBoxChanged(Individual individual, boolean isChecked) {
        checkedStatusMap.put(individual.getUuid(), isChecked);
        Registry updatedRegistry = new Registry();
        updatedRegistry.setIndividual_uuid(individual.getUuid());
        updatedRegistry.setSocialgroup_uuid(socialgroup.getUuid());
        updatedRegistry.setStatus(isChecked ? 1 : 2);

        // Update the registry in the ViewModel
        new Thread(() -> {
            registryViewModel.add(updatedRegistry);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }


    @Override
    public int getItemCount() {
        return individualList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView nameTextView;
        TextView dobTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            nameTextView = itemView.findViewById(R.id.text_individual_name);
            dobTextView = itemView.findViewById(R.id.text_individual_dob);
        }
    }

    // Method to return the list of individuals
    public List<Individual> getIndividualList() {
        return individualList;
    }

    // Method to check if an individual is selected (i.e., marked as present)
    public boolean isChecked(String individualUuid) {
        return checkedStatusMap.getOrDefault(individualUuid, false);
    }

    // Filter method to retrieve and display individuals based on location ID
    public void filter() {
        individualList.clear();
        if (socialgroup != null) {
            List<Individual> list = null;

            try {
                // Attempt to retrieve by the first identifier
                list = individualViewModel.retrieveByLocationId(socialgroup.getExtId());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // If the first list is null or empty, attempt to retrieve by the second identifier
            if (list == null || list.isEmpty()) {
                try {
                    list = individualViewModel.retrieveByLocationId(socialgroup.getUuid());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // If the list is still not null, add all individuals to individualList
            if (list != null) {
                individualList.addAll(list);
            }

            // If the list is empty, notify the user
            if (list == null || list.isEmpty()) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "No Active Individual Found", Toast.LENGTH_SHORT).show()
                );
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            if (individualList != null && !individualList.isEmpty()) {
                notifyDataSetChanged();
            }
        });

        //notifyDataSetChanged();
    }

}
