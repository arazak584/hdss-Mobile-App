package org.openhds.hdsscapture.fragment;

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
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

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

    private Location location;
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
     * @param location Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @param pregnancy Parameter 5.
     * @return A new instance of fragment PregnancyDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PregnancyDialogFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup,Pregnancy pregnancy) {
        PregnancyDialogFragment fragment = new PregnancyDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, location);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(PREGNANCY_ID, pregnancy);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable(LOC_LOCATION_IDS);
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
        if (location != null) {
            compno.setText(location.getCompno());
        } else {
            // Handle the case where location is null
            compno.setText("Error loading location data");
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
        final PregnancyAdapter adapter = new PregnancyAdapter(this, individual, residency,location, socialgroup );
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