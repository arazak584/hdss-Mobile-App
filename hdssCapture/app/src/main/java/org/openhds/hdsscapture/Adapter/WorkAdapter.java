package org.openhds.hdsscapture.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.RemainderActivity;
import org.openhds.hdsscapture.Activity.ScheduleActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
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
    private final LocationViewModel locationViewModel;
    private final ListingViewModel listingViewModel;
    public WorkAdapter(ScheduleActivity activity, Hierarchy level6Data, LocationViewModel locationViewModel, ListingViewModel listingViewModel) {
        this.activity = activity;
        this.level6Data = level6Data;
        this.locationViewModel = locationViewModel;
        this.listingViewModel = listingViewModel;
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

        holder.title.setText(hierarchy.dist);
        holder.id2.setText(hierarchy.subdist);
        holder.id3.setText(hierarchy.village);

//        String cps = hierarchy.extId;//Visited Compounds
//        String cp = hierarchy.town;//Total Compounds
//        String hh = hierarchy.area;//Total Households
//
//        try {
//            int cpsInt = Integer.parseInt(cps);
//            int cpInt = Integer.parseInt(cp);
//            int hhInt = Integer.parseInt(hh);
//
//            if (cpInt > 0 && cpInt < cpsInt && hhInt > 0) {
//                holder.id4.setText(hierarchy.cluster + " (" + cp + ", " + hh + ") " + " IN PROGRESS");
//                holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Blue));
//            } else if (cpsInt == cpInt && hhInt <= 0) {
//                holder.id4.setText(hierarchy.cluster + " (" + cp + ", " + hh + ") " + " DONE");
//                holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_blackgreen_end));
//            } else {
//                holder.id4.setText(hierarchy.cluster + " (" + cp + ", " + hh + ") " + " NOT STARTED");
//                holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
//            }
//        } catch (NumberFormatException e) {
//            // Handle the exception if the string cannot be parsed to an integer
//            e.printStackTrace();
//            // Optionally, you can show an error message or handle it accordingly
//        }

        try {
            // Assuming you have access to the necessary ViewModel instances
            long count = locationViewModel.done(hierarchy.uuid);
            long counts = listingViewModel.done(hierarchy.cluster);
            long hse = locationViewModel.hseCount(hierarchy.cluster);
            long rem = count - counts;

            String statusText;
            int color;

            if (counts > 0 && hse > 0) {
                statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "IN PROGRESS";
                color = R.color.Blue;
            } else if (rem == 0 && hse > 0) {
                statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "IN PROGRESS";
                color = R.color.Blue;
            } else if (rem > 0 && hse == 0) {
                statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "IN PROGRESS";
                color = R.color.Blue;
            } else if (rem <= 0 && hse <= 0) {
                statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "DONE";
                color = R.color.color_blackgreen_end;
            } else {
                statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "NOT STARTED";
                color = R.color.Red;
            }

            holder.id4.setText(statusText);
            holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), color));

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            // Handle exceptions appropriately, for example, show an error message
            Toast.makeText(activity, "Error updating counts", Toast.LENGTH_SHORT).show();
        }

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
                List<Hierarchy> filteredHierarchy = hierarchyViewModel.repos(charText);
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
