package org.openhds.hdsscapture.Adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.fragment.BlankFragment;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.HouseholdFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {

    private final ClusterFragment activity;
    private final LayoutInflater inflater;
    //private static Locations selectedLocation;
    private final List<Socialgroup> socialgroupList;
    private final SocialgroupViewModel socialgroupViewModel;
    private final Socialgroup socialgroup;
    private final FragmentActivity activity1;
    private Individual individual;
    private final ClusterSharedViewModel clusterSharedViewModel;
    private Locations currentSelectedLocation;


//    public HouseholdAdapter(Context context, ClusterFragment activity, Locations selectedLocation, Socialgroup socialgroup, SocialgroupViewModel socialgroupViewModel) {
//        this.activity1 = activity.requireActivity();
//        this.activity = activity;
//        this.selectedLocation = selectedLocation;
//        this.socialgroup = socialgroup;
//        this.socialgroupViewModel = socialgroupViewModel;
//        socialgroupList = new ArrayList<>();
//        inflater = LayoutInflater.from(activity.requireContext());
//    }

    public HouseholdAdapter(Context context, ClusterFragment activity, ClusterSharedViewModel viewModel, Socialgroup socialgroup, SocialgroupViewModel socialgroupViewModel) {
        this.activity1 = activity.requireActivity();
        this.activity = activity;
        this.clusterSharedViewModel = viewModel;
        this.socialgroup = socialgroup;
        this.socialgroupViewModel = socialgroupViewModel;
        socialgroupList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, hhid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.hhead_name);
            this.hhid = view.findViewById(R.id.hhid_head);
            this.linearLayout = view.findViewById(R.id.searchHousehold);
        }
    }

    @NonNull
    @Override
    public HouseholdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.household_itemlist, parent, false);
        HouseholdAdapter.ViewHolder viewHolder = new HouseholdAdapter.ViewHolder(listItem);


        return viewHolder;
    }

//    @Override
//    public void onBindViewHolder(@NonNull HouseholdAdapter.ViewHolder holder, int position) {
//        final Socialgroup socialgroup = socialgroupList.get(position);
//        Locations currentSelectedLocation = clusterSharedViewModel.getCurrentSelectedLocation();
//
//        if (ClusterFragment.selectedLocation != null) {
//            holder.name.setText(socialgroup.getGroupName());
//            holder.hhid.setText(socialgroup.getExtId()+ " " + "(" + ClusterFragment.selectedLocation.getCompno() + ")" );
//
//            Integer visit = socialgroup.complete;
//
//            if (visit != null) {
//                holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
//                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
//            } else {
//                holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
//                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
//            }
//        }else {
//            // Handle the case when selectedLocation is null
//            holder.name.setText("No selected location");
//            holder.hhid.setText("");
//        }
//
//        holder.linearLayout.setOnClickListener(v -> {
//            // Use the stored activity reference to access the getSupportFragmentManager()
//            activity1.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container_cluster, HouseMembersFragment.newInstance(selectedLocation, socialgroup,individual))
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        // Get the current selected location from the shared ViewModel
        Locations currentSelectedLocation = clusterSharedViewModel.getCurrentSelectedLocation();

        if (currentSelectedLocation != null) {
            holder.name.setText(socialgroup.getGroupName());
            holder.hhid.setText(socialgroup.getExtId() + " " + "(" + currentSelectedLocation.getCompno() + ")");

            Integer visit = socialgroup.complete;

            if (visit != null) {
                holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            } else {
                holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
            }
        } else {
            // Handle the case when selectedLocation is null
            holder.name.setText("No selected location");
            holder.hhid.setText("");
        }

        holder.linearLayout.setOnClickListener(v -> {
            // Get the current selected location from the shared ViewModel
            Locations currentLocation = clusterSharedViewModel.getCurrentSelectedLocation();

            // Use the stored activity reference to access the getSupportFragmentManager()
            activity1.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, HouseMembersFragment.newInstance(currentLocation, socialgroup, individual))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

    public void setSelectedLocation(Locations selectedLocation) {
        socialgroupList.clear();
        if (selectedLocation != null) {
            activity.showProgressBar(); // Show the ProgressBar
            new Handler().postDelayed(() -> { // Simulate delay for loading
                try {
                    List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(selectedLocation.getCompno());

                    if (list != null) {
                        socialgroupList.clear();
                        socialgroupList.addAll(list);
                    }
                    if (list.isEmpty()){
                        Toast.makeText(activity.getActivity(), "No Active Household In " + selectedLocation.getCompno(), Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    activity.hideProgressBar(); // Hide the ProgressBar
                }
                notifyDataSetChanged();
            }, 100); // You can adjust the delay as needed
        }
    }


}
