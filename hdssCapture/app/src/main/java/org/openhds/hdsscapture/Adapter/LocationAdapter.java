package org.openhds.hdsscapture.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.LocationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    ClusterFragment activity;
    LayoutInflater inflater;
    private List<Locations> locationsList;
    private Hierarchy level6Data;

    public LocationAdapter(ClusterFragment activity, Hierarchy level6Data) {
        this.activity = activity;
        this.level6Data = level6Data;
        locationsList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationname, compno, longitude, latitude;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.locationname = view.findViewById(R.id.Location_name);
            this.compno = view.findViewById(R.id.location_compno);
            this.longitude = view.findViewById(R.id.longitude);
            this.latitude = view.findViewById(R.id.latitude);
            this.linearLayout = view.findViewById(R.id.searchedItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.location_view_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);


        return viewHolder;


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Locations locations = locationsList.get(position);

        holder.locationname.setText(locations.getLocationName());
        holder.compno.setText(locations.getCompno());
        holder.longitude.setText(locations.getLongitude());
        holder.latitude.setText(locations.getLatitude());

        Integer st = locations.complete;
        if (st != null) {
            holder.compno.setTextColor(Color.parseColor("#32CD32"));
            holder.locationname.setTextColor(Color.parseColor("#32CD32"));
//            holder.longitude.setTextColor(Color.GREEN);
//            holder.latitude.setTextColor(Color.GREEN);
        } else {
            holder.compno.setTextColor(Color.parseColor("#FF4500"));
            holder.locationname.setTextColor(Color.parseColor("#FF4500"));
//            holder.longitude.setTextColor(Color.parseColor("#FF4500"));
//            holder.latitude.setTextColor(Color.parseColor("#FF4500"));
        }

        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    LocationFragment.newInstance(level6Data, locations)).commit();
        });

    }

    @Override
    public int getItemCount() {

        return locationsList.size();
    }

    @SuppressLint("SuspiciousIndentation")
    public void filter(String charText, LocationViewModel locationViewModel) {
        locationsList.clear();
        if (charText != null && charText.length() > 2) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Locations> list = locationViewModel.findBySearch(level6Data.getUuid(),charText);

                if (list != null) {
                    locationsList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(level6Data != null)
            try {
                List<Locations> list = locationViewModel.findLocationsOfCluster(level6Data.getUuid());

                if (list != null) {
                    locationsList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        notifyDataSetChanged();
    }

}
