package org.openhds.hdsscapture.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.entity.Locations;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClusterViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterViewFragment extends DialogFragment {

    private static final String ARG_VILL_EXTID = "vill_extid";

    private String villExtId;

    public ClusterViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param villExtId The village external ID
     * @return A new instance of fragment ClusterViewFragment.
     */
    public static ClusterViewFragment newInstance(String villExtId) {
        ClusterViewFragment fragment = new ClusterViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VILL_EXTID, villExtId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            villExtId = getArguments().getString(ARG_VILL_EXTID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cluster_dialog, container, false);

        Button closeButton = view.findViewById(R.id.cluster_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Load Cluster Data
        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view_cluster);
        final ClusterViewAdapter adapter = new ClusterViewAdapter(this, villExtId);
        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(requireActivity()).get(HierarchyViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of clusters
        adapter.clusters("", hierarchyViewModel);

        return view;
    }
}