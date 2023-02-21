package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.BlankFragment;
import org.openhds.hdsscapture.fragment.LocationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {

    BlankFragment activity;
    LayoutInflater inflater;
    private List<Location> locationList;
    private Cluster clusterData;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;

    public LocationViewAdapter(BlankFragment activity, Cluster clusterData) {
        this.activity = activity;
        this.clusterData = clusterData;
        locationList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView extId, locationname, compno, cluster, longitude, latitude;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.extId = view.findViewById(R.id.locationView_extid);
            this.locationname = view.findViewById(R.id.Location_name);
            this.compno = view.findViewById(R.id.location_compno);
            this.cluster = view.findViewById(R.id.location_cluster);
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
        final Location location = locationList.get(position);

        holder.extId.setText(location.getExtId());
        holder.locationname.setText(location.getLocationName());
        holder.compno.setText(location.getCompno());
        holder.cluster.setText(location.getClusterId());
        holder.longitude.setText(location.getLongitude());
        holder.latitude.setText(location.getLatitude());

        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_main,
                    LocationFragment.newInstance(clusterData, location, socialgroup, residency, individual)).commit();
        });

    }

    @Override
    public int getItemCount() {

        return locationList.size();
    }

    public void filter(String charText, LocationViewModel locationViewModel) {
        locationList.clear();
        if (charText != null && charText.length() > 2) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Location> list = locationViewModel.findBySearch(charText);

                if (list != null) {
                    locationList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(clusterData != null)
            try {
                List<Location> list = locationViewModel.findLocationsOfCluster(clusterData.getExtId());

                if (list != null) {
                    locationList.addAll(list);
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
