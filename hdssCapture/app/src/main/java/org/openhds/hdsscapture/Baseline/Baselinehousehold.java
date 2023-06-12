package org.openhds.hdsscapture.Baseline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentBaselinehouseholdBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Baselinehousehold#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Baselinehousehold extends Fragment {

    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "SOCIAL.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private Pregnancy pregnancy;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Visit visit;
    private FragmentBaselinehouseholdBinding binding;
    private ProgressDialog progress;
    private Button buttonHousehold;
    private ProgressDialog progressDialog;

    public Baselinehousehold() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param individual Parameter 5.
     * @param residency Parameter 4.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment Baselinehousehold.
     */
    // TODO: Rename and change types and number of parameters
    public static Baselinehousehold newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        Baselinehousehold fragment = new Baselinehousehold();
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
        binding = FragmentBaselinehouseholdBinding.inflate(inflater, container, false);

        final TextView compno = binding.getRoot().findViewById(R.id.textView_compound);
        final TextView gps = binding.getRoot().findViewById(R.id.gpsLng);
        final TextView gpsLat = binding.getRoot().findViewById(R.id.gpsLat);
        final TextView name = binding.getRoot().findViewById(R.id.textView_locationName);

        compno.setText(locations.getCompextId());
        name.setText(locations.getLocationName());
        gpsLat.setText(locations.getLatitude());
        gps.setText(locations.getLongitude());

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_householdbase);
        final HouseAdapter adapter = new HouseAdapter(this, residency, locations, individual, visit);
        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(requireActivity()).get(SocialgroupViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        buttonHousehold = binding.getRoot().findViewById(R.id.button_household);
        buttonHousehold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.search(socialgroupViewModel);
                    }
                }, 500); // change delay time as needed
            }

            public void showLoadingDialog() {

                if (progress == null) {
                    progress = new ProgressDialog(requireContext());
                    //progress.setTitle(getString(R.string.loading_lbl));
                    //progress.setProgressStyle();
                    progress.setMessage("Loading Households...");
                    //progress.setMessage("Loading...");
                    progress.setCancelable(false);
                }
                progress.show();
            }
        });

        final ExtendedFloatingActionButton add_household = binding.getRoot().findViewById(R.id.button_newhousehold);
        add_household.setOnClickListener(v -> {

            final Individual individual = new Individual();
            final Residency residency = new Residency();
            final Socialgroup socialgroup = new Socialgroup();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    NewhouseholdFragment.newInstance(individual, residency, locations,socialgroup)).commit();
        });

        View view = binding.getRoot();
        return view;
    }

    public void dismissLoadingDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }



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