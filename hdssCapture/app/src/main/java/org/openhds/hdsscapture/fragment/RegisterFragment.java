package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Adapter.RegistryAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.databinding.FragmentRegisterBinding;
import org.openhds.hdsscapture.databinding.FragmentSocioBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Locations locations;
    private Individual individual;
    private Socialgroup socialgroup;
    private Fieldworker fieldworkerData;
    private FragmentRegisterBinding binding;
    private RegistryViewModel registryViewModel;
    private IndividualViewModel individualViewModel; // Add IndividualViewModel
    private RegistryAdapter registryAdapter;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(Locations locations, Socialgroup socialgroup, Individual individual) {
        RegisterFragment fragment = new RegisterFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        // Initialize ViewModels
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        final Intent i = getActivity().getIntent();
        fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        // Setup RecyclerView and Adapter
        RecyclerView recyclerView = binding.recyclerView;
        registryAdapter = new RegistryAdapter(getContext(), new ArrayList<>(), registryViewModel, individualViewModel, socialgroup);
        recyclerView.setAdapter(registryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load Registry data
        loadRegistryData();

        binding.buttonSave.setOnClickListener(v -> save(true, true));
        binding.buttonClose.setOnClickListener(v -> save(false, true));

        Handler.colorLayouts(requireContext(), binding.REGISTERLAYOUT);
        return binding.getRoot();
    }

    private void loadRegistryData() {
        new Thread(() -> {
            try {
                Registry registry = registryViewModel.finds(socialgroup.getUuid());
                if (registry != null) {
                    binding.setRegistry(registry);
                    // Load associated individuals into the adapter
                    //Log.d("Date", "InsertRegistry 1 : "+  registry.insertDate);
                    registryAdapter.filter();
                } else {
                    Registry newRegistry = new Registry();
//                    newRegistry.setIndividual_uuid(individual.uuid);
//                    newRegistry.setFw_uuid(fieldworkerData.getFw_uuid());
//                    newRegistry.setSocialgroup_uuid(socialgroup.getUuid());
//                    newRegistry.setLocation_uuid(ClusterFragment.selectedLocation.getUuid());

                    registryAdapter.filter();
                    binding.setRegistry(newRegistry);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading registry data", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void save(boolean save, boolean close) {
        if (save) {
            new Thread(() -> {
                // Iterate through all individuals in the adapter
                for (Individual individual : registryAdapter.getIndividualList()) {
                    Registry registry = new Registry();
                    registry.setIndividual_uuid(individual.getUuid());
                    registry.setName(individual.getFirstName() + " " + individual.getLastName());
                    registry.setSocialgroup_uuid(socialgroup.getUuid());
                    registry.setLocation_uuid(ClusterFragment.selectedLocation.getUuid());
                    registry.setFw_uuid(fieldworkerData.getFw_uuid());
                    //registry.insertDate = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());
                    registry.setInsertDate(formattedDate);
                    Log.d("Date", "InsertRegistry: "+  registry.insertDate);
                    Log.d("Date", "Insert Date Format: "+  formattedDate);
                    // Retrieve the status from the adapter or set a default value
                    boolean isChecked = registryAdapter.isChecked(individual.getUuid());
                    registry.setStatus(isChecked ? 1 : 2);

                    // Set other required fields
                    registry.setComplete(1);

                    // Add the registry record to the ViewModel
                    registryViewModel.add(registry);
                }
//                if (isAdded()) {
//                    requireActivity().runOnUiThread(() ->
//                            Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT).show()
//                    );
//                }
            }).start();
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_cluster, HouseMembersFragment.newInstance(locations, socialgroup, individual))
                    .commit();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}