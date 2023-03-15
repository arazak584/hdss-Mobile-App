package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openhds.hdsscapture.Adapter.LocationViewAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

/**

 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    private static final String ARG_CLUSTER_ID = "ARG_CLUSTER_ID";
    private Cluster clusterData;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;


    public BlankFragment() {
        // Required empty public constructor

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param clusterData Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(Cluster clusterData) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUSTER_ID, clusterData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterData = getArguments().getParcelable(ARG_CLUSTER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.listview);
        final LocationViewAdapter adapter = new LocationViewAdapter(this, clusterData);
        final LocationViewModel locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of cluster locations
        adapter.filter("", locationViewModel);

        // Locate the EditText in listview_main.xml
        final SearchView editSearch = view.findViewById(R.id.search);
        // below line is to call set on query text listener method.
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                adapter.filter(newText, locationViewModel);
                return false;
            }
        });
        

        final FloatingActionButton add_location = view.findViewById(R.id.button_add_location);
        add_location.setOnClickListener(v -> {

            final Location location = new Location();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_main,
                    LocationFragment.newInstance(clusterData, location, socialgroup, residency,individual)).commit();
        });


        return view;
    }



}