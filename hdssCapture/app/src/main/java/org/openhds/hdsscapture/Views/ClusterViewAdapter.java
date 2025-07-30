package org.openhds.hdsscapture.Views;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClusterViewAdapter extends RecyclerView.Adapter<ClusterViewAdapter.ViewHolder>{

    ClusterViewFragment activity;
    LayoutInflater inflater;
    private final List<Hierarchy> hierarchyList;

    public interface ClusterSelectionListener {
        void onClusterSelected(String clusterid);
    }

    private ClusterViewAdapter.ClusterSelectionListener listener;
    private final String village;

    public void setClusterSelectionListener(ClusterViewAdapter.ClusterSelectionListener listener) {
        this.listener = listener;
    }

    public ClusterViewAdapter(ClusterViewFragment activity, String village) {
        this.activity = activity;
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
    public ClusterViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cluster_itemlist, parent, false);
        ClusterViewAdapter.ViewHolder viewHolder = new ClusterViewAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ClusterViewAdapter.ViewHolder holder, int position) {
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
