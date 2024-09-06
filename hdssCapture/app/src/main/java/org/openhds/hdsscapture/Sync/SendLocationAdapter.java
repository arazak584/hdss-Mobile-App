package org.openhds.hdsscapture.Sync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Locations;

import java.util.ArrayList;
import java.util.List;

public class SendLocationAdapter extends RecyclerView.Adapter<SendLocationAdapter.ViewHolder> {

    private List<Locations> locationsList;
    private List<Locations> selectedLocations = new ArrayList<>();

    public SendLocationAdapter(List<Locations> locationsList) {
        this.locationsList = locationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Locations location = locationsList.get(position);
        holder.bind(location);

        // Check if the item is in the selected list
        holder.checkBox.setChecked(selectedLocations.contains(location));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedLocations.contains(location)) {
                    selectedLocations.add(location);
                }
            } else {
                selectedLocations.remove(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public List<Locations> getSelectedLocations() {
        return selectedLocations;
    }

    public void updateData(List<Locations> newLocations) {
        this.locationsList.clear();
        this.selectedLocations.clear();  // Clear selections after update
        this.locationsList.addAll(newLocations);
        notifyDataSetChanged(); // This will refresh the RecyclerView immediately
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(Locations location) {
            checkBox.setText(location.getCompno());
        }
    }

}
