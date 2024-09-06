package org.openhds.hdsscapture.Sync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;

public class SendHouseholdAdapter extends RecyclerView.Adapter<SendHouseholdAdapter.ViewHolder> {

    private List<Socialgroup> socialgroupList;
    private List<Socialgroup> selectedSocialgroup = new ArrayList<>();

    public SendHouseholdAdapter(List<Socialgroup> socialgroupList) {
        this.socialgroupList = socialgroupList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_socialgroup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Socialgroup socialgroup = socialgroupList.get(position);
        holder.bind(socialgroup);

        // Check if the item is in the selected list
        holder.checkBox.setChecked(selectedSocialgroup.contains(socialgroup));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedSocialgroup.contains(socialgroup)) {
                    selectedSocialgroup.add(socialgroup);
                }
            } else {
                selectedSocialgroup.remove(socialgroup);
            }
        });
    }


    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

    public List<Socialgroup> getselectedSocialgroup() {
        return selectedSocialgroup;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(Socialgroup socialgroup) {
            checkBox.setText(socialgroup.getExtId() + " " + socialgroup.getGroupName());
        }
    }

    public void updateData(List<Socialgroup> newSocialgroups) {
        this.socialgroupList.clear();
        this.socialgroupList.addAll(newSocialgroups);
        notifyDataSetChanged(); // This will refresh the RecyclerView immediately
    }

}
