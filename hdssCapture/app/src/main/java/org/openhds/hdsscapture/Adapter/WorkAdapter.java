package org.openhds.hdsscapture.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.RemainderActivity;
import org.openhds.hdsscapture.Activity.ScheduleActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.subqueries.Newloc;
import org.openhds.hdsscapture.entity.subqueries.WorkArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder>{

    ScheduleActivity activity;
    LayoutInflater inflater;
    private final List<Hierarchy> hierarchyList;
    private final Hierarchy level6Data;
    private List<Hierarchy> filter;
    public WorkAdapter(ScheduleActivity activity, Hierarchy level6Data) {
        this.activity = activity;
        this.level6Data = level6Data;
        hierarchyList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchNotes(List<Hierarchy> filterName) {
        if (filterName != null) {
            this.filter = filterName;
        } else {
            this.filter = new ArrayList<>(); // Reset to an empty list if null
        }
        notifyDataSetChanged();
    }

    public List<Hierarchy> getHierarchyList() {
        return hierarchyList;
    }

    public void setHierarchyList(List<Hierarchy> hierarchyList)
    {
        this.filter = hierarchyList;
        notifyDataSetChanged();
    }

        public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,id1, id2 , id3,id4;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.txt_id1);
            this.id2 = view.findViewById(R.id.txt_id2);
            this.id3 = view.findViewById(R.id.txt_id3);
            this.id4 = view.findViewById(R.id.txt_id4);
            this.cardView = view.findViewById(R.id.Schedule);

        }
    }


    @NonNull
    @Override
    public WorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.shedule, parent, false);
        WorkAdapter.ViewHolder viewHolder = new WorkAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkAdapter.ViewHolder holder, int position) {
        final Hierarchy hierarchy = hierarchyList.get(position);

//        int itemNumber = position + 1;
//        holder.title.setText(itemNumber + "");

        holder.title.setText(hierarchy.name);
        holder.id2.setText(hierarchy.area);
        holder.id3.setText(hierarchy.town);
        holder.id4.setText(hierarchy.parent_uuid);

    }

    @Override
    public int getItemCount() {
        return hierarchyList.size();
    }

    public void filter(String charText, HierarchyViewModel hierarchyViewModel) {
        hierarchyList.clear(); // Clear the existing data

        if (charText != null && !charText.isEmpty()) {
            charText = charText.toUpperCase(Locale.getDefault());

            try {
                List<Hierarchy> filteredHierarchy = hierarchyViewModel.repo(charText);
                hierarchyList.addAll(filteredHierarchy); // Add filtered data to locationsList

                if (filteredHierarchy.isEmpty()) {
                    Toast.makeText(activity, "No Area Assigned", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Error Getting Remainder", Toast.LENGTH_SHORT).show();
            }
        }

        notifyDataSetChanged();
    }
}
