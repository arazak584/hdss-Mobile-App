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
import org.openhds.hdsscapture.databinding.FragmentRemainderBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
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

    private Hierarchy level5Data;
    private Locations locations;
    private Visit visit;
    private FragmentRemainderBinding binding;


    public RemainderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param level5Data Parameter 1.
     * @return A new instance of fragment RemainderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemainderFragment newInstance(Hierarchy level5Data) {
        RemainderFragment fragment = new RemainderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VILLAGE_ID, level5Data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level5Data = getArguments().getParcelable(ARG_VILLAGE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remainder, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.remainlist);
        final RemainderAdapter adapter = new RemainderAdapter(this, level5Data);
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
        //final SearchView editSearch = view.findViewById(R.id.research);
        // below line is to call set on query text listener method.
//        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // inside on query text change method we are
//                // calling a method to filter our recycler view.
//                adapter.filter(newText, locationViewModel);
//                return false;
//            }
//        });

        return view;
    }
}