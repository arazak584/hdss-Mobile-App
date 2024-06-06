package org.openhds.hdsscapture.Adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.CommunityFragment;
import org.openhds.hdsscapture.fragment.CommunityRegFragment;
import org.openhds.hdsscapture.fragment.CommunityReportFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;
import org.openhds.hdsscapture.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>{

    CommunityReportFragment activity;
    LayoutInflater inflater;
    private final List<CommunityReport> communityList;
    private final Hierarchy level5Data;

    public CommunityAdapter(CommunityReportFragment activity,Hierarchy level5Data) {
        this.activity = activity;
        this.level5Data = level5Data;
        communityList = new ArrayList<>();
        //inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView insertdate, name, desc;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.insertdate = view.findViewById(R.id.text_insertdate);
            this.name = view.findViewById(R.id.text_name);
            this.desc = view.findViewById(R.id.text_desc);
            this.cardView = view.findViewById(R.id.CommunityReport);
        }
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.community_itemlist, parent, false);
        CommunityAdapter.ViewHolder viewHolder = new CommunityAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, int position) {
        final CommunityReport communityReport = communityList.get(position);

        int autoNumber = calculateAutoNumber(communityReport.getDescription(), position);
        holder.insertdate.setText(communityReport.getInsertDate());
        holder.name.setText(communityReport.getName());
        holder.desc.setText(String.valueOf(autoNumber) + ". " + communityReport.description);
        holder.desc.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));

        holder.cardView.setOnClickListener(v -> {
            String uuid = communityReport.getUuid().toString(); // Assuming CommunityReport has a getUuid() method
            CommunityRegFragment fragment = CommunityRegFragment.newInstance(uuid);
            activity.requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, fragment)
                    .addToBackStack(null)
                    .commit();
            //Log.d("Community", "Community UUID " + uuid);
        });



//        holder.cardView.setOnClickListener(v -> {
//            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    CommunityRegFragment.newInstance()).commit();
//        });
    }

    // Method to calculate auto-number based on description
    private int calculateAutoNumber(String description, int currentPosition) {
        int autoNumber = 1; // Initialize auto-number to 1

        // Loop through previous items to find items with the same description
        for (int i = 0; i < currentPosition; i++) {
            CommunityReport report = communityList.get(i);
            // If the description matches, increment auto-number
            if (report.getDescription().equals(description)) {
                autoNumber++;
            }
        }

        return autoNumber;
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public void updateData(List<CommunityReport> newCommunityList) {
        communityList.clear();
        communityList.addAll(newCommunityList);
        notifyDataSetChanged();
    }

    public void com(CommunityViewModel communityViewModel) {
        communityList.clear();
        if(level5Data != null)
            try {
                List<CommunityReport> list = communityViewModel.retrieves(level5Data.getName());

                if (list != null) {
                    communityList.addAll(list);
                }

                if (list.isEmpty()) {
                   Toast.makeText(activity.getActivity(), "No Community Record", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
