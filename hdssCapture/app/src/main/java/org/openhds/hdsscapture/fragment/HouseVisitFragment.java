package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseVisitBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseVisitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseVisitFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "LOCATION.TAG";


    private Location location;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private Pregnancy pregnancy;
    private CaseItem caseItem;
    private FragmentHouseVisitBinding binding;

    public HouseVisitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param cluster_id  Parameter 1.
     * @param individual Parameter 5.
     * @param residency Parameter 4.
     * @param location    Parameter 2.
     * @param socialgroup Parameter 3.
     *
     * @return A new instance of fragment HouseVisitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseVisitFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup) {
        HouseVisitFragment fragment = new HouseVisitFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, location);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //this.cluster_id = getArguments().getParcelable(ARG_CLUSTER_IDS);
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.residency = getArguments().getParcelable(RESIDENCY_ID);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
            this.location = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_house_visit, container, false);
        binding = FragmentHouseVisitBinding.inflate(inflater, container, false);

        final TextView compno = binding.getRoot().findViewById(R.id.textView_compextId);
        final TextView gps = binding.getRoot().findViewById(R.id.textView_gps);
        final TextView gpsLat = binding.getRoot().findViewById(R.id.textView_gpsLat);
        final TextView name = binding.getRoot().findViewById(R.id.textView_compname);

        compno.setText(location.getCompextId());
        name.setText(location.getLocationName());
        gpsLat.setText(location.getLatitude());
        gps.setText(location.getLongitude());


        /*//Button showDialogButton = view.findViewById(R.id.button_pregnant);
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_pregnant);

        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                PregnancyDialogFragment.newInstance(individual, residency, location,socialgroup, pregnancy)
                        .show(getChildFragmentManager(), "PregnancyDialogFragment");
            }
        });*/


        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_household);
        final IndividualViewAdapter adapter = new IndividualViewAdapter(this, residency,location, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Individuals in locations
        adapter.filter("", individualViewModel);

        // Locate the EditText in listview_main.xml
        final SearchView editSearch = binding.getRoot().findViewById(R.id.individual_search);
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
                adapter.filter(newText, individualViewModel);
                return false;
            }
        });

        /*
        final ExtendedFloatingActionButton addvisit = binding.getRoot().findViewById(R.id.add_visit);
        addvisit.setOnClickListener(v -> {

            final Visit visit = new Visit();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyFragment.newInstance(individual,residency,location, socialgroup,caseItem,eventForm)).commit();
        });*/


        final FloatingActionButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
        add_individual.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance(individual,residency,location, socialgroup,caseItem)).commit();
        });




        View view = binding.getRoot();
        return view;

    }


}