package org.openhds.hdsscapture.Baseline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualSummaryBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.fragment.VisitFragment;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualSummaryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "LOCATION.TAG";

    private Hierarchy level6Data;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private FragmentIndividualSummaryBinding binding;
    private ProgressDialog progress;

    public IndividualSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param individual Parameter 5.
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @return A new instance of fragment IndividualSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualSummaryFragment newInstance(Locations locations, Socialgroup socialgroup,Individual individual) {
        IndividualSummaryFragment fragment = new IndividualSummaryFragment();
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
            this.residency = getArguments().getParcelable(RESIDENCY_ID);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIndividualSummaryBinding.inflate(inflater, container, false);

        final TextView hh = binding.getRoot().findViewById(R.id.textView_compextId);
        final TextView name = binding.getRoot().findViewById(R.id.textView_compname);

        hh.setText(socialgroup.getExtId());
        name.setText(socialgroup.getGroupName());

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_baseline);
        final IndividualAdaptor adapter = new IndividualAdaptor(this, residency, locations, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

//        binding.buttonIndividuals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLoadingDialog();
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.pull(individualViewModel);
//                    }
//                }, 500); // change delay time as needed
//            }
//
//            public void showLoadingDialog() {
//                if (progress == null) {
//                    progress = new ProgressDialog(requireContext());
//                    progress.setTitle("Loading Individuals...");
//                    progress.setMessage(getString(R.string.please_wait_lbl));
//                    progress.setCancelable(false);
//                }
//                progress.show();
//            }
//        });

        //initial loading of Individuals in locations
        adapter.filter("", individualViewModel);

        final AppCompatButton addback = binding.getRoot().findViewById(R.id.add_back);
        addback.setOnClickListener(v -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BaseFragment.newInstance(level6Data,locations,socialgroup)).commit();
        });

        final AppCompatButton visit = binding.getRoot().findViewById(R.id.button_visit);
        visit.setOnClickListener(v -> {
            BasevisitFragment.newInstance(locations, socialgroup)
                    .show(getChildFragmentManager(), "BasevisitFragment");
        });


        final AppCompatButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
        add_individual.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BaselineFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

        VisitViewModel vmodel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit data = vmodel.find(socialgroup.uuid);
            if (data != null) {
                add_individual.setEnabled(true);
            }else{
                add_individual.setEnabled(false);
                add_individual.setText("Unvisited");

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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