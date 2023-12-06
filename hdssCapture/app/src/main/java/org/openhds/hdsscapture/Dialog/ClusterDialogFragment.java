package org.openhds.hdsscapture.Dialog;

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

import org.openhds.hdsscapture.Adapter.ClusterAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.fragment.ClusterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClusterDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterDialogFragment extends DialogFragment {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";

    private Locations locations;

    public ClusterDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @return A new instance of fragment ClusterDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClusterDialogFragment newInstance(Locations locations) {
        ClusterDialogFragment fragment = new ClusterDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cluster_dialog, container, false);

        final TextView compno = view.findViewById(R.id.textView_compextId);
        if (ClusterFragment.selectedLocation != null) {
            compno.setText(ClusterFragment.selectedLocation.getCompextId());
        } else {
            // Handle the case where locations is null
            compno.setText("Error loading locations data");
        }

        Button closeButton = view.findViewById(R.id.cluster_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Load Socialgroup Data
        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view_cluster);
        final ClusterAdapter adapter = new ClusterAdapter(this, locations);
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