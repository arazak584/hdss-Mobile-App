package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.RemainderAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Village;
import org.openhds.hdsscapture.entity.Visit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RemainderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemainderFragment extends Fragment {

    private static final String ARG_VILLAGE_ID = "ARG_VILLAGE_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private final String TAG = "LOCATION.TAG";

    private Village villageData;
    private Location location;
    private Visit visit;


    public RemainderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param villageData Parameter 1.
     * @param location Parameter 2.
     * @return A new instance of fragment RemainderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemainderFragment newInstance(Village villageData, Location location) {
        RemainderFragment fragment = new RemainderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VILLAGE_ID, villageData);
        args.putParcelable(LOC_LOCATION_IDS, location);;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            villageData = getArguments().getParcelable(ARG_VILLAGE_ID);
            this.location = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remainder, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.remainlistview);
        final RemainderAdapter adapter = new RemainderAdapter(this, villageData);
        final LocationViewModel locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of location for villages
        adapter.load("", locationViewModel);

        return view;
    }
}