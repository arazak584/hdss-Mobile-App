package org.openhds.hdsscapture.Baseline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualSummaryBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualSummaryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

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
    private ExtendedFloatingActionButton finish;
    private IndividualViewModel individualViewModel;
    private boolean isAllFabsVisible;

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

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        final TextView hh = binding.getRoot().findViewById(R.id.textView_compextId);
        final TextView name = binding.getRoot().findViewById(R.id.textView_compname);
        finish = binding.getRoot().findViewById(R.id.add_back);

        hh.setText(socialgroup.getExtId());
        name.setText(socialgroup.getGroupName());
        updateButtonState();

        isAllFabsVisible = false;
        binding.addAdhocFab.setOnClickListener(view -> {

            if (!isAllFabsVisible) {

                // when isAllFabsVisible becomes
                // true make all the action name
                // texts and FABs VISIBLE.
                binding.buttonVisit.show();
                binding.buttonChoh.show();
                binding.buttonNewindividual.show();

                // make the boolean variable true as
                // we have set the sub FABs
                // visibility to GONE
                isAllFabsVisible = true;

            } else {

                // when isAllFabsVisible becomes
                // true make all the action name
                // texts and FABs GONE.
                binding.buttonVisit.hide();
                binding.buttonChoh.hide();
                binding.buttonNewindividual.hide();

                // make the boolean variable false
                // as we have set the sub FABs
                // visibility to GONE
                isAllFabsVisible = false;
            }
        });

        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_baseline);
        final IndividualAdaptor adapter = new IndividualAdaptor(this, residency, locations, socialgroup );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Individuals in locations
        adapter.filter("", individualViewModel);


        finish.setOnClickListener(v -> {

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BaseFragment.newInstance(level6Data,locations,socialgroup)).commit();
        });

        binding.buttonVisit.setOnClickListener(v -> {
            BasevisitFragment.newInstance(locations, socialgroup)
                    .show(getChildFragmentManager(), "BasevisitFragment");
        });


        final AppCompatButton change_hoh = binding.getRoot().findViewById(R.id.button_choh);
        final AppCompatButton add_individual = binding.getRoot().findViewById(R.id.button_newindividual);
        binding.buttonNewindividual.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BaselineFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        });

        binding.buttonChoh.setOnClickListener(v -> {

            final Individual individual = new Individual();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    HoHFragment.newInstance(individual, locations, socialgroup)).commit();
        });

        VisitViewModel vmodel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit data = vmodel.find(socialgroup.uuid);
            if (data != null) {
                binding.buttonNewindividual.setEnabled(true);
                binding.buttonChoh.setEnabled(true);
            }else{
                binding.buttonNewindividual.setEnabled(false);
                binding.buttonChoh.setEnabled(false);
                binding.buttonNewindividual.setText("Disabled");
                binding.buttonChoh.setText("Disabled");

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View view = binding.getRoot();
        return view;
    }

    private void updateButtonState() {
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);
        TextView toastMessage = customToastView.findViewById(R.id.toast_message);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Perform database operations in the background
                long err = (individualViewModel != null) ? individualViewModel.err(socialgroup != null ? socialgroup.extId : "", BaseFragment.selectedLocation != null ? BaseFragment.selectedLocation.compno : "") : 0;
                long errs = (individualViewModel != null) ? individualViewModel.errs(socialgroup != null ? socialgroup.extId : "", BaseFragment.selectedLocation != null ? BaseFragment.selectedLocation.compno : "") : 0;

                handler.post(() -> {
                    if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

                    try {
                        if (errs > 0) {
                            showToast("Household Head is a Minor", customToastView, toastMessage);
                        } else if (err > 0) {
                            showToast("Only Minors Left in Household", customToastView, toastMessage);
                        } else {
                            enableFinishButton();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });
    }

    // Helper method to show a custom toast message
    private void showToast(String message, View customToastView, TextView toastMessage) {
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home));
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_border_lightgray));
        finish.setOnClickListener(v -> {
            toastMessage.setText(message);
            Toast customToast = new Toast(requireContext());
            customToast.setDuration(Toast.LENGTH_LONG);
            customToast.setView(customToastView);
            customToast.show();
        });
    }

    // Helper method to enable the finish button with default functionality
    private void enableFinishButton() {
        if (getActivity() == null || !isAdded()) return; // Ensure fragment is still attached

        finish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home)); // Original color
        finish.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // Original text color
        finish.setEnabled(true);
        finish.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_baseline, BaseFragment.newInstance(level6Data, locations, socialgroup))
                    .commit();
        });
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