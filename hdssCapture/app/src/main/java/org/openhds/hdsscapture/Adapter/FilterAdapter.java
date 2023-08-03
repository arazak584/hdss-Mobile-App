package org.openhds.hdsscapture.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Dialog.FilterDialogFragment;
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

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    FilterDialogFragment activity;
    LayoutInflater inflater;
    private List<Locations> locationsList;
    private Hierarchy level6Data;

    public FilterAdapter(FilterDialogFragment activity, Hierarchy level6Data) {
        this.activity = activity;
        this.level6Data = level6Data;
        locationsList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationname, compno, extid;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.locationname = view.findViewById(R.id.filter_name);
            this.compno = view.findViewById(R.id.filter_compno);
            this.extid = view.findViewById(R.id.filter_extid);
            this.linearLayout = view.findViewById(R.id.filterItem);
        }
    }
    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.filter_view_items, parent, false);
        FilterAdapter.ViewHolder viewHolder = new FilterAdapter.ViewHolder(listItem);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.ViewHolder holder, int position) {
        final Locations locations = locationsList.get(position);

        holder.locationname.setText(locations.getLocationName());
        holder.compno.setText(locations.getCompno());
        holder.extid.setText(locations.getCompextId());
//
//        holder.linearLayout.setOnClickListener(v -> {
//            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    LocationFragment.newInstance(level6Data, locations)).commit();
//        });

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
                List<Locations> list = locationViewModel.filter(charText);

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
