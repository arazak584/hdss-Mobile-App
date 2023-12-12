package org.openhds.hdsscapture.Baseline;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.LocationAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder>{

    private final LocationClickListener locationClickListener;
    BaseFragment activity;
    LayoutInflater inflater;
    private final List<Locations> locationsList;
    private final Hierarchy level6Data;
    private final Fragment fragment;

    public interface LocationClickListener {
        void onLocationClick(Locations selectedLocation);
    }

    public BaseAdapter(Fragment fragment, Hierarchy level6Data, LocationClickListener listener) {
        this.fragment = fragment;
        this.level6Data = level6Data;
        this.locationClickListener = listener;
        locationsList = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationname, compno, gps, latitude;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.locationname = view.findViewById(R.id.Location_name);
            this.compno = view.findViewById(R.id.location_compno);
            this.gps = view.findViewById(R.id.longitude);
            this.linearLayout = view.findViewById(R.id.searchedItem);
        }
    }

    @NonNull
    @Override
    public BaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.location_baseline, parent, false);
        BaseAdapter.ViewHolder viewHolder = new BaseAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.ViewHolder holder, int position) {
        final Locations locations = locationsList.get(position);

        holder.locationname.setText(locations.getLocationName());
        holder.compno.setText(locations.getCompno());
        holder.gps.setText(locations.getLatitude() + "," + locations.getLongitude());

        Integer st = locations.complete;
        if (st != null) {
            holder.compno.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            holder.locationname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        } else {
            holder.compno.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
            holder.locationname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
        }

        holder.gps.setTextIsSelectable(true);

        holder.linearLayout.setOnClickListener(v -> {
            if (locationClickListener != null) {
                locationClickListener.onLocationClick(locations);
                //Log.d("LocationAdapter", "Location clicked: " + locations.getCompno());
            }
        });

    }

    @Override
    public int getItemCount() {

        return locationsList.size();
    }

    public void filter(String charText, LocationViewModel locationViewModel) {
        locationsList.clear();
        if (charText != null && charText.length() > 2) {
            charText = charText.toLowerCase(Locale.getDefault());
            try {
                List<Locations> list = locationViewModel.findBySearch(level6Data.getUuid(), charText);
                if (list != null) {
                    locationsList.addAll(list);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            if (level6Data != null)
                try {
                    List<Locations> list = locationViewModel.findLocationsOfCluster(level6Data.getUuid());
                    if (list != null) {
                        locationsList.addAll(list);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
        }

        notifyDataSetChanged();
    }
}
