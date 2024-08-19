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

import org.openhds.hdsscapture.Adapter.DthAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentDthAdapterBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DthAdapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DthAdapterFragment extends DialogFragment {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentDthAdapterBinding binding;
    private Individual individual;

    public DthAdapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment DtheditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DthAdapterFragment newInstance(Locations locations, Socialgroup socialgroup, Individual individual) {
        DthAdapterFragment fragment = new DthAdapterFragment();
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
        //return inflater.inflate(R.layout.fragment_dthedit, container, false);
        binding = FragmentDthAdapterBinding.inflate(inflater, container, false);

        Button closeButton = binding.getRoot().findViewById(R.id.button_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_dth);
        final DthAdapter adapter = new DthAdapter(this, locations, socialgroup);
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //Load Individual Deaths
        adapter.dth(individualViewModel);

        View view = binding.getRoot();
        return view;
    }


}