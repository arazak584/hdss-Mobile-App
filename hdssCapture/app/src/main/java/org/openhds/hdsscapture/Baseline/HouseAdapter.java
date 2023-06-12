package org.openhds.hdsscapture.Baseline;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder>{


    Baselinehousehold activity;
    LayoutInflater inflater;
    private Locations locations;
    private List<Socialgroup> socialgroupList;
    private Residency residency;
    private Individual individual;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Visit visit;

    public HouseAdapter(Baselinehousehold activity, Residency residency, Locations locations, Individual individual, Visit visit) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.individual = individual;
        this.visit = visit;
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
            this.linearLayout = view.findViewById(R.id.baseHousehold);
        }
    }

    @NonNull
    @Override
    public HouseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.baseline_household_items, parent, false);
        HouseAdapter.ViewHolder viewHolder = new HouseAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HouseAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        holder.name.setText(socialgroup.getGroupName());
        holder.hhid.setText(socialgroup.getExtId());
        holder.hhid.setTextColor(Color.RED);

        Integer visit = socialgroup.complete;

        if (visit != null) {
            holder.hhid.setTextColor(Color.BLUE);
            holder.name.setTextColor(Color.BLUE);
        }else {
            holder.hhid.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
        }


        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BasesocialgroupFragment.newInstance(individual, residency, locations, socialgroup)).commit();
        });
    }

    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

    public void search(SocialgroupViewModel socialgroupViewModel) {
        socialgroupList.clear();
        if(locations != null)
            try {
                List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(locations.getCompextId());

                if (list != null) {
                    socialgroupList.addAll(list);
                }
                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No household Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
        activity.dismissLoadingDialog();
    }
}
