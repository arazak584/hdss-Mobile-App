package org.openhds.hdsscapture.Duplicate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentDupDialogBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dup1DialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dup1DialogFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDupDialogBinding binding;

    public Dup1DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment DupDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Dup1DialogFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        Dup1DialogFragment fragment = new Dup1DialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_dup_dialog, container, false);

        final TextView compno = view.findViewById(R.id.textViewman_compextId);
        if (ClusterFragment.selectedLocation.compno != null) {
            compno.setText(ClusterFragment.selectedLocation.getCompno());
        } else {
            // Handle the case where locations is null
            compno.setText("Error loading locations data");
        }

        Button closeButton = view.findViewById(R.id.button_rclose);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Load Father Data
        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view_duplicate);
        final Dup1Adapter adapter = new Dup1Adapter(this, residency, locations, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);


        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Individuals in locations
        adapter.filter("", individualViewModel);

        // Locate the EditText in listview_main.xml
        final SearchView editSearch = view.findViewById(R.id.dup_search);
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

        return view;
    }
}