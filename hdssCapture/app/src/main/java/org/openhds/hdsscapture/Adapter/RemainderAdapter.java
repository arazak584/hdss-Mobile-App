package org.openhds.hdsscapture.Adapter;

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
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.ViewHolder>{

    RemainderActivity activity;
    LayoutInflater inflater;
    private final List<Locations> locationsList;
    private final Hierarchy level6Data;


    public RemainderAdapter(RemainderActivity activity, Hierarchy level6Data) {
        this.activity = activity;
        this.level6Data = level6Data;
        locationsList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationname, compno,extid, longitude, latitude, itemNumber;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.itemNumber = view.findViewById(R.id.item_number);
            this.locationname = view.findViewById(R.id.Location_rname);
            this.compno = view.findViewById(R.id.location_rcompno);
            this.extid = view.findViewById(R.id.location_extids);
//            this.longitude = view.findViewById(R.id.rlongitude);
//            this.latitude = view.findViewById(R.id.rlatitude);
            this.cardView = view.findViewById(R.id.remainderItem);
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
        final Locations locations = locationsList.get(position);

        int itemNumber = position + 1;
        holder.itemNumber.setText(itemNumber + "");

        //holder.extId.setText(locations.getCompextId());
        holder.locationname.setText(locations.getLocationName());
        holder.compno.setText(locations.getCompno());
        holder.extid.setText(locations.getCompextId());
//        holder.longitude.setText(locations.getLongitude());
//        holder.latitude.setText(locations.getLatitude());

    }

    @Override
    public int getItemCount() {

        return locationsList.size();
    }

    public void filter(String charText, LocationViewModel locationViewModel) {
        locationsList.clear(); // Clear the existing data

        if (charText != null && !charText.isEmpty()) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Locations> filteredLocations = locationViewModel.retrieveByVillage(charText);
                locationsList.addAll(filteredLocations); // Add filtered data to locationsList

                if (filteredLocations.isEmpty()) {
                    Toast.makeText(activity, "No Compound(s) Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Error Getting Remainder", Toast.LENGTH_SHORT).show();
            }
        }

        notifyDataSetChanged();
    }

}
