package org.openhds.hdsscapture.Dialog;

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

import org.openhds.hdsscapture.Adapter.PregnancyAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.databinding.FragmentPregnancyDialogBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PregnancyDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PregnancyDialogFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String PREGNANCY_ID = "PREGNANCY_ID";
    private final String TAG = "PREGNANCY.TAG";

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private Pregnancy pregnancy;
    private FragmentPregnancyDialogBinding binding;

    public PregnancyDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment PregnancyDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyDialogFragment newInstance(Locations locations, Socialgroup socialgroup) {
        PregnancyDialogFragment fragment = new PregnancyDialogFragment();
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
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            pregnancy = getArguments().getParcelable(PREGNANCY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pregnancy_dialog, container, false);

        final TextView compno = view.findViewById(R.id.preg_compextId);
        if (ClusterFragment.selectedLocation != null) {
            compno.setText(ClusterFragment.selectedLocation.getCompno());
        } else {
            // Handle the case where locations is null
            compno.setText("Error loading locations data");
        }

        Button closeButton = view.findViewById(R.id.button_pregclose);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        //Load Pregnancy Data
        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_pregnancy);
        final PregnancyAdapter adapter = new PregnancyAdapter(this, individual, residency, locations, socialgroup );
        final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(requireActivity()).get(PregnancyViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Pregnancy in locations
        adapter.filter("", pregnancyViewModel);


        return view;
    }


}