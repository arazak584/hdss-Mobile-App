package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.HouseholdDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {

    HouseholdDialogFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private List<Socialgroup> socialgroupList;
    private Residency residency;
    private Individual individual;

    public HouseholdAdapter(HouseholdDialogFragment activity, Residency residency, Locations locations, Individual individual) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.individual = individual;
        socialgroupList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, hhid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.hhead_name);
            this.hhid = view.findViewById(R.id.hhid_head);
            this.linearLayout = view.findViewById(R.id.searchedHousehold);
        }
    }

    @NonNull
    @Override
    public HouseholdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.household_itemlist, parent, false);
        HouseholdAdapter.ViewHolder viewHolder = new HouseholdAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        holder.name.setText(socialgroup.getGroupName());
        holder.hhid.setText(socialgroup.getHouseExtId());

        holder.linearLayout.setOnClickListener(v -> {
            //activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                   // HouseholdDialogFragment.newInstance(individual, residency, locations, socialgroup )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

    public void filter(String charText, SocialgroupViewModel socialgroupViewModel) {
        socialgroupList.clear();
            if(locations != null)
                try {
                    List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(locations.getCompextId());

                    if (list != null) {
                        socialgroupList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        notifyDataSetChanged();
    }
}
