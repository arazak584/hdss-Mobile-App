package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Dialog.ClusterDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.fragment.ClusterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder>{

    ClusterDialogFragment activity;
    LayoutInflater inflater;
    private final List<Hierarchy> hierarchyList;
    private final Locations locations;

    public interface ClusterSelectionListener {
        void onClusterSelected(String clusterid);
    }

    private ClusterAdapter.ClusterSelectionListener listener;
    private final String village;

    public void setClusterSelectionListener(ClusterAdapter.ClusterSelectionListener listener) {
        this.listener = listener;
    }

    public ClusterAdapter(ClusterDialogFragment activity,Locations locations,String village) {
        this.activity = activity;
        this.locations = locations;
        this.village = village;
        hierarchyList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView extId, name;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.extId = view.findViewById(R.id.cluster_codes);
            this.name = view.findViewById(R.id.cluster_name);
            this.linearLayout = view.findViewById(R.id.Clusters);
        }
    }

    @NonNull
    @Override
    public ClusterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cluster_itemlist, parent, false);
        ClusterAdapter.ViewHolder viewHolder = new ClusterAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ClusterAdapter.ViewHolder holder, int position) {
        final Hierarchy hierarchy = hierarchyList.get(position);

        holder.extId.setText(hierarchy.getExtId());
        holder.name.setText(hierarchy.getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field
                EditText clustercode = activity.requireActivity().findViewById(R.id.villcode);
                EditText parent = activity.requireActivity().findViewById(R.id.cluster_code);
                EditText village = activity.requireActivity().findViewById(R.id.village);
                // Set the cluster ID in the text field
                clustercode.setText(hierarchy.getExtId());
                parent.setText(hierarchy.getUuid());
                village.setText(hierarchy.getName());

                // Hide the MotherDialogFragment
                activity.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {

        return hierarchyList.size();
    }

    public void clusters(String charText, HierarchyViewModel hierarchyViewModel) {
        hierarchyList.clear();
        if(village != null)
            try {
                List<Hierarchy> list = hierarchyViewModel.clusters(village);

                if (list != null) {
                    hierarchyList.addAll(list);
                }
                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No Cluster Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }

    }
