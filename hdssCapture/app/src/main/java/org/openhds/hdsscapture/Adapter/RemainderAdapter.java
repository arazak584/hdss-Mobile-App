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
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.fragment.RemainderFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.ViewHolder>{

    RemainderFragment activity;
    LayoutInflater inflater;
    private List<Location> locationList;
    private Hierarchy level5Data;


    public RemainderAdapter(RemainderFragment activity, Hierarchy level5Data) {
        this.activity = activity;
        this.level5Data = level5Data;
        locationList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView extId, locationname, compno, cluster, longitude, latitude;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.extId = view.findViewById(R.id.locationView_rextid);
            this.locationname = view.findViewById(R.id.Location_rname);
            this.compno = view.findViewById(R.id.location_rcompno);
            this.cluster = view.findViewById(R.id.location_rcluster);
            this.longitude = view.findViewById(R.id.rlongitude);
            this.latitude = view.findViewById(R.id.rlatitude);
            this.linearLayout = view.findViewById(R.id.remainderItem);
        }
    }

    @NonNull
    @Override
    public RemainderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.remainder_view_items, parent, false);
        RemainderAdapter.ViewHolder viewHolder = new RemainderAdapter.ViewHolder(listItem);


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RemainderAdapter.ViewHolder holder, int position) {
        final Location location = locationList.get(position);

        holder.extId.setText(location.getCompextId());
        holder.locationname.setText(location.getLocationName());
        holder.compno.setText(location.getCompno());
        holder.cluster.setText(location.villcode);
        holder.longitude.setText(location.getLongitude());
        holder.latitude.setText(location.getLatitude());

    }

    @Override
    public int getItemCount() {

        return locationList.size();
    }

    public void filter(String charText, LocationViewModel locationViewModel) {
        locationList.clear();

            if(level5Data != null)
                try {
                    List<Location> list = locationViewModel.retrieveByVillage(level5Data.getName());

                    if (list != null) {
                        locationList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        notifyDataSetChanged();
    }
}
