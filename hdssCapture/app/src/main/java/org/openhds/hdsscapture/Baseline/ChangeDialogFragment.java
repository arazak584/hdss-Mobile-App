package org.openhds.hdsscapture.Baseline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.SocialgroupAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentChangeDialogBinding;
import org.openhds.hdsscapture.databinding.FragmentHouseholdDialogBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeDialogFragment extends DialogFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentChangeDialogBinding binding;



    public ChangeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment HouseholdDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeDialogFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        ChangeDialogFragment fragment = new ChangeDialogFragment();
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
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_dialog, container, false);

        final TextView compno = view.findViewById(R.id.textViewhouse_compextId);
        if (BaseFragment.selectedLocation != null) {
            compno.setText(BaseFragment.selectedLocation.getCompno());
        } else {
            // Handle the case where locations is null
            compno.setText("Error loading locations data");
        }

        Button closeButton = view.findViewById(R.id.button_hhclose);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        //Load Socialgroup Data
        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view_house);
        final ChhouseholdAdapter adapter = new ChhouseholdAdapter(this,locations, individual);
        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(requireActivity()).get(SocialgroupViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of socialgroup in locations
        adapter.filter("", socialgroupViewModel);

        return view;
    }
}