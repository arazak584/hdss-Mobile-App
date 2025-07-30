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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder>{

    ScheduleActivity activity;
    LayoutInflater inflater;
    private final List<Hierarchy> hierarchyList;
    private final Hierarchy level6Data;
    private List<Hierarchy> filter;
    private final LocationViewModel locationViewModel;
    private final ListingViewModel listingViewModel;

    // Cache for computed counts to avoid repeated database queries
    private Map<String, CountData> countCache = new HashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    // Data class to hold count information
    public static class CountData {
        long count;
        long counts;
        long hse;
        long rem;
        String statusText;
        int color;

        CountData(long count, long counts, long hse, long rem, String statusText, int color) {
            this.count = count;
            this.counts = counts;
            this.hse = hse;
            this.rem = rem;
            this.statusText = statusText;
            this.color = color;
        }
    }

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
            this.filter = new ArrayList<>();
        }
        // Clear cache when data changes
        countCache.clear();
        notifyDataSetChanged();
    }

    public List<Hierarchy> getHierarchyList() {
        return hierarchyList;
    }

    public void setHierarchyList(List<Hierarchy> hierarchyList) {
        this.filter = hierarchyList;
        // Clear cache when data changes
        countCache.clear();
        notifyDataSetChanged();
    }

    // Method to preload counts for better performance
    public void preloadCounts() {
        executorService.execute(() -> {
            for (Hierarchy hierarchy : hierarchyList) {
                if (!countCache.containsKey(hierarchy.uuid)) {
                    try {
                        CountData countData = computeCountData(hierarchy);
                        countCache.put(hierarchy.uuid, countData);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Notify adapter on main thread after preloading
            activity.runOnUiThread(() -> notifyDataSetChanged());
        });
    }

    private CountData computeCountData(Hierarchy hierarchy) throws ExecutionException, InterruptedException {
        //Count compounds from location based on subvillage
        long count = locationViewModel.done(hierarchy.uuid);
        //Count of compounds with listings picked for specific subvillage
        long counts = listingViewModel.done(hierarchy.cluster);
        //Count households that has been visited in that subvillage
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
        } else if (count == 0 && hse == 0) {
            statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "INACTIVE";
            color = R.color.Magenta;
        } else if (count > 0 && hse <= 0) {
            statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "INACTIVE COMPOUNDS";
            color = R.color.DarkViolet;
        } else if (rem <= 0 && hse <= 0) {
            statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "DONE";
            color = R.color.color_blackgreen_end;
        } else {
            statusText = hierarchy.cluster + " (" + rem + ", " + hse + ") " + "NOT STARTED";
            color = R.color.Red;
        }

        return new CountData(count, counts, hse, rem, statusText, color);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, id1, id2, id3, id4;
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
        return new WorkAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkAdapter.ViewHolder holder, int position) {
        final Hierarchy hierarchy = hierarchyList.get(position);

        holder.title.setText(hierarchy.dist);
        holder.id2.setText(hierarchy.subdist);
        holder.id3.setText(hierarchy.village);

        // Check if count data is cached
        CountData cachedData = countCache.get(hierarchy.uuid);

        if (cachedData != null) {
            // Use cached data for immediate display
            holder.id4.setText(cachedData.statusText);
            holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), cachedData.color));
        } else {
            // Show loading state
            holder.id4.setText(hierarchy.cluster + " (Loading...)");
            holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Blue));

            // Load data asynchronously
            executorService.execute(() -> {
                try {
                    CountData countData = computeCountData(hierarchy);
                    countCache.put(hierarchy.uuid, countData);

                    // Update UI on main thread
                    activity.runOnUiThread(() -> {
                        // Check if this view is still showing the same item
                        if (holder.getAdapterPosition() == position &&
                                hierarchyList.get(position).uuid.equals(hierarchy.uuid)) {
                            holder.id4.setText(countData.statusText);
                            holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), countData.color));
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        if (holder.getAdapterPosition() == position) {
                            holder.id4.setText(hierarchy.cluster + " (Error)");
                            holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
                        }
                        Toast.makeText(activity, "Error updating counts", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return hierarchyList.size();
    }

    public void filter(String charText, HierarchyViewModel hierarchyViewModel) {
        hierarchyList.clear();
        countCache.clear(); // Clear cache when filtering

        if (charText != null && !charText.isEmpty()) {
            charText = charText.toUpperCase(Locale.getDefault());

            try {
                List<Hierarchy> filteredHierarchy = hierarchyViewModel.repos(charText);
                hierarchyList.addAll(filteredHierarchy);

                if (filteredHierarchy.isEmpty()) {
                    Toast.makeText(activity, "No Area Assigned", Toast.LENGTH_SHORT).show();
                } else {
                    // Preload counts for filtered data
                    preloadCounts();
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Error Getting Remainder", Toast.LENGTH_SHORT).show();
            }
        }

        notifyDataSetChanged();
    }

    // Clean up resources
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}