package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Adapter.HouseholdAdapter;
import org.openhds.hdsscapture.Adapter.LocationAdapter;
import org.openhds.hdsscapture.Dialog.FilterDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClusterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterFragment extends Fragment implements LocationAdapter.LocationClickListener {

    private static final String ARG_CLUSTER_ID = "ARG_CLUSTER_ID";
    private Hierarchy level6Data;
    private Locations locations;
    private Socialgroup socialgroup;
    public static  Locations selectedLocation;
    private SocialgroupViewModel socialgroupViewModel;
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private HouseholdAdapter householdAdapter;
    private View view;
    private Button button;
    private ProgressBar progressBar;

    public interface LocationClickListener {
        void onLocationClick(Locations selectedLocation);
    }

    public ClusterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param level6Data Parameter 1.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment ClusterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClusterFragment newInstance(Hierarchy level6Data,Locations locations, Socialgroup socialgroup) {
        ClusterFragment fragment = new ClusterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUSTER_ID, level6Data);
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level6Data = getArguments().getParcelable(ARG_CLUSTER_ID);
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
        socialgroupViewModel = new ViewModelProvider(requireActivity()).get(SocialgroupViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cluster, container, false);

        //TextView done = view.findViewById(R.id.compl);
        final Intent i = getActivity().getIntent();
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progress_bar);

        //Household Adapter
        final RecyclerView recyclerViewHousehold = view.findViewById(R.id.recyclerView_householdids);
        DividerItemDecoration dividerItemDecorations = new DividerItemDecoration(recyclerViewHousehold.getContext(),
                RecyclerView.VERTICAL);
        recyclerViewHousehold.addItemDecoration(dividerItemDecorations);
        recyclerViewHousehold.setLayoutManager(new LinearLayoutManager(view.getContext()));
        householdAdapter = new HouseholdAdapter(requireContext(),this, locations, socialgroup, socialgroupViewModel);
        recyclerViewHousehold.setAdapter(householdAdapter);

        // Instantiate the SocialgroupViewModel
        socialgroupViewModel = new ViewModelProvider(requireActivity()).get(SocialgroupViewModel.class);

        Button showDialogButton = view.findViewById(R.id.button_filter);
        // Set a click listener on the button for pregnancies
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the dialog fragment
                FilterDialogFragment.newInstance(level6Data)
                        .show(getChildFragmentManager(), "FilterDialogFragment");

            }
        });

        //Location Adapter
        final RecyclerView recyclerView = view.findViewById(R.id.compoundsview);
        final LocationAdapter adapter = new LocationAdapter(this, level6Data, this);
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
        final SearchView editSearch = view.findViewById(R.id.comp_search);
        // below line is to call set on query text listener method.
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, locationViewModel);
                return false;
            }
        });

//        final ImageView refresh = view.findViewById(R.id.refresh);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                        adapter.refresh(locationViewModel);
//
//            }
//
//        });

//        try {
//            // Call the counts method on locationViewModel to retrieve data
//            long count = locationViewModel.counts(level6Data.uuid);
//            long counts = individualViewModel.counts(level6Data.name);
//                // Update UI with the count (assuming done is a TextView)
//                //done.setText(String.valueOf(count));
//                done.setText("(" + String.valueOf(count) + ", " + String.valueOf(counts) + ")");
//                done.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Green));
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        final AppCompatButton add_location = view.findViewById(R.id.button_new_location);
        add_location.setOnClickListener(v -> {
            final Locations locations = new Locations();
            LocationFragment.newInstance(level6Data, locations)
                    .show(getChildFragmentManager(), "LocationFragment");
        });

        final AppCompatButton add_com = view.findViewById(R.id.button_new_community);
        add_com.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    CommunityReportFragment.newInstance(level5Data)).commit();
        });

        final AppCompatButton add_listing = view.findViewById(R.id.button_listing);
        add_listing.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    ListingFragment.newInstance(locations)).commit();
        });
//        add_listing.setOnClickListener(v -> {
//            final Listing listing = new Listing();
//            ListingFragment.newInstance(locations)
//                    .show(getChildFragmentManager(), "ListingFragment");
//        });

        final AppCompatButton add_household = view.findViewById(R.id.button_newhousehold);
            add_household.setOnClickListener(v -> {
                final Individual individual = new Individual();
                final Residency residency = new Residency();
                final Socialgroup socialgroup = new Socialgroup();
                NewSocialgroupFragment.newInstance(individual, residency, locations, socialgroup)
                        .show(getChildFragmentManager(), "NewSocialgroupFragment");
            });







//        add_household.setOnClickListener(v -> {
//            final Individual individual = new Individual();
//            final Residency residency = new Residency();
//            final Socialgroup socialgroup = new Socialgroup();
//
//            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                    NewSocialgroupFragment.newInstance(individual, residency, locations, socialgroup)).commit();
//        });


        return view;


    }

//    @Override
//    public void onLocationClick(Locations selectedLocation) {
//        this.selectedLocation = selectedLocation; // Assign the selected location to the class-level variable
//        Log.d("ClusterFragment", "onLocationClick called for location: " + selectedLocation.getCompno());
//        Toast.makeText(requireContext(), "Location clicked: " + selectedLocation.getCompno(), Toast.LENGTH_SHORT).show();
//
//        // Update the householdAdapter with the selected location
//        if (householdAdapter != null) {
//            householdAdapter.setSelectedLocation(this.selectedLocation); // Use the class-level variable instead of the local variable
//        } else {
//            Log.d("ClusterFragment", "selectedLocation or householdAdapter is null in onLocationClick");
//        }
//    }

    @Override
    public void onLocationClick(Locations selectedLocation) {
        ClusterFragment.selectedLocation = selectedLocation; // Always update the selectedLocation variable
        //Log.d("ClusterFragment", "onLocationClick called for location: " + selectedLocation.getCompno());
        //Toast.makeText(requireContext(), "Location clicked: " + selectedLocation.getCompno(), Toast.LENGTH_SHORT).show();
        // Show ProgressBar when location is clicked
//        showProgressBar();

        // Update the householdAdapter with the selected location
        if (householdAdapter != null) {
            householdAdapter.setSelectedLocation(ClusterFragment.selectedLocation);
            //TextView compno = view.findViewById(R.id.textView_compounds);
            //compno.setText(selectedLocation.compno);
            AppCompatButton add_household = view.findViewById(R.id.button_newhousehold);
            final AppCompatButton add_listing = view.findViewById(R.id.button_listing);
            add_household.setVisibility(View.VISIBLE);
            add_listing.setVisibility(View.VISIBLE);

        } else {
            AppCompatButton add_household = view.findViewById(R.id.button_newhousehold);
            final AppCompatButton add_listing = view.findViewById(R.id.button_listing);
            add_household.setVisibility(View.GONE);
            add_listing.setVisibility(View.GONE);

        }

        //hideProgressBar();
    }

    public void showProgressBar() {
        if (progressBar == null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }



    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


}