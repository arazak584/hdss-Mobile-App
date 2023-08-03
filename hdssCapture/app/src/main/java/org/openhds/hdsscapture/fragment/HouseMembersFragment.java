package org.openhds.hdsscapture.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseMembersBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseMembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseMembersFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "LOCATION.TAG";


    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private Pregnancy pregnancy;
    private CaseItem caseItem;
    private EventForm eventForm;
    private FragmentHouseMembersBinding binding;
    private ProgressDialog progress;
    private ProgressDialog progres;


    public HouseMembersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param cluster_id  Parameter 1.
     * @param individual Parameter 5.
     * @param residency Parameter 4.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     *
     * @return A new instance of fragment HouseMembersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseMembersFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        HouseMembersFragment fragment = new HouseMembersFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
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
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHouseMembersBinding.inflate(inflater, container, false);

        // Get the Context object
//        Context context = requireContext();


        final TextView hh = binding.getRoot().findViewById(R.id.textView_compextId);
        final TextView name = binding.getRoot().findViewById(R.id.textView_compname);

        hh.setText(socialgroup.getExtId());
        name.setText(socialgroup.getGroupName());

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_household);
        final IndividualViewAdapter adapter = new IndividualViewAdapter(this, residency, locations, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        // In your onCreateView() or onViewCreated() method
        binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialogs();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Get the selected item from the spinner
                        TextInputEditText searchSpinner = binding.getRoot().findViewById(R.id.searchVillage);
                        String selectedSpinnerItem = searchSpinner.getText().toString();

                        // Get the search text from the search input field
                        TextInputEditText searchInput = binding.getRoot().findViewById(R.id.search_indivdual);
                        String searchText = searchInput.getText().toString();

                        // Perform search based on the selected item and search text
                        List<Individual> searchResults = null;
                        try {
                            searchResults = individualViewModel.retrieveBySearch(selectedSpinnerItem, searchText);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Pass the search results to the adapter
                        adapter.search(selectedSpinnerItem, searchText, individualViewModel);


                        // Dismiss the progress dialog
                        progress.dismiss();
                    }
                }, 500); // Change delay time as needed
            }

            public void showLoadingDialogs() {
                if (progress == null) {
                    progress = new ProgressDialog(requireContext());
                    progress.setTitle("Searching...");
                    progress.setMessage(getString(R.string.please_wait_lbl));
                }
                progress.show();
            }
        });



        binding.buttonIndividuals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.pull(individualViewModel);
                        progres.dismiss();
                    }
                }, 500); // change delay time as needed
            }

            public void showLoadingDialog() {
                if (progres == null) {
                    progres = new ProgressDialog(requireContext());
                    progres.setTitle("Loading Individuals...");
                    progres.setMessage(getString(R.string.please_wait_lbl));
                    progres.setCancelable(false);
                }
                progres.show();
            }
        });

        //initial loading of Individuals in locations
        //adapter.filter("", individualViewModel);

        final ExtendedFloatingActionButton addback = binding.getRoot().findViewById(R.id.add_back);
        addback.setOnClickListener(v -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BlankFragment.newInstance(individual,residency,locations, socialgroup)).commit();
        });


        final ExtendedFloatingActionButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
        add_individual.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance(individual,residency, locations, socialgroup,caseItem)).commit();
        });




        View view = binding.getRoot();
        return view;

    }

    public void dismissLoadingDialog() {
        if (progres != null) {
            progres.dismiss();
        }
    }

    public void dismissLoadingDialogs() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    // Override the onBackPressed() method in the hosting activity
    //@Override
    public void onBackPressed() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            getActivity().finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

}