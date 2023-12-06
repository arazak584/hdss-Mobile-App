package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Adapter.SearchAdapter;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseMembersBinding;
import org.openhds.hdsscapture.databinding.FragmentSearchBinding;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends DialogFragment {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentSearchBinding binding;
    private ProgressDialog progress;
    private ProgressDialog progres;
    private Residency residency;
    private Individual individual;
    private Hierarchy level6Data;
    private static Locations selectedLocation;
    private ArrayAdapter<Hierarchy> level6Adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(Locations locations, Socialgroup socialgroup) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_search, container, false);
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        Button closeButton = binding.getRoot().findViewById(R.id.button_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final AppCompatButton dup = binding.getRoot().findViewById(R.id.button_newindividual);
        dup.setOnClickListener(v -> {
            final Individual individual1 = new Individual();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DupFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_individual);
        final SearchAdapter adapter = new SearchAdapter(this, locations, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //Call Village Data
        AutoCompleteTextView level6Spinner = binding.getRoot().findViewById(R.id.autoVillage);
        level6Spinner.setAdapter(level6Adapter);

        level6Adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        level6Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level6Spinner.setAdapter(level6Adapter);

        try {
            List<Hierarchy> level6Data = hierarchyViewModel.retrieveLevel7();
            level6Adapter.addAll(level6Data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }

        level6Spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                level6Data = level6Adapter.getItem(position);
            }
        });


        binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialogs();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Get the selected item from the spinner
//                        TextInputEditText searchSpinner = binding.getRoot().findViewById(R.id.searchVillage);
//                        String selectedSpinnerItem = searchSpinner.getText().toString();

                        //String selectedSpinnerItem = level6Data.getText().toString();
                        AutoCompleteTextView level6Spinner = binding.getRoot().findViewById(R.id.autoVillage);
                        String selectedText = level6Spinner.getText().toString();

                        // Get the search text from the search input field
//                        TextInputEditText searchInput = binding.getRoot().findViewById(R.id.search_indivdual);
                        EditText searchInput = binding.getRoot().findViewById(R.id.search_indivdual);
                        String searchText = searchInput.getText().toString();

                        // Perform search based on the selected item and search text
                        List<Individual> searchResults = null;
                        try {
                            searchResults = individualViewModel.retrieveBySearch(selectedText, searchText);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Pass the search results to the adapter
                        adapter.search(selectedText, searchText, individualViewModel);


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

        //Outmigrated
        adapter.omg(individualViewModel);

        final AppCompatButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
        add_individual.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance(individual,residency, locations, socialgroup)).commit();
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
}