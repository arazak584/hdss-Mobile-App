package org.openhds.hdsscapture.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.openhds.hdsscapture.Adapter.OmgAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentOmgAdapterBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OmgAdapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OmgAdapterFragment extends DialogFragment {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentOmgAdapterBinding binding;
    private Individual individual;

    public OmgAdapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment OmgeditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OmgAdapterFragment newInstance(Locations locations, Socialgroup socialgroup, Individual individual) {
        OmgAdapterFragment fragment = new OmgAdapterFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_omgedit, container, false);
        binding = FragmentOmgAdapterBinding.inflate(inflater, container, false);

        Button closeButton = binding.getRoot().findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_omg);
        final OmgAdapter adapter = new OmgAdapter(this, locations, socialgroup);
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //Load Individual Deaths
        adapter.omg(individualViewModel);

        View view = binding.getRoot();
        return view;
    }
}