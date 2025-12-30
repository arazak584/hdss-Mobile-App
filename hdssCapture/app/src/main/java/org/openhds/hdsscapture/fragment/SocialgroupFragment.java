package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Adapter.HouseholdMemberAdapter;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.ChangeHohFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocialgroupBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.ResidencyRelationshipUpdate;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialgroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialgroupFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentSocialgroupBinding binding;
    private ProgressDialog progressDialog;
    private Individual selectedIndividual;
    private HouseholdMemberAdapter adapter;
    private List<HouseholdMemberData> householdMembers;
    private List<KeyValuePair> relationshipOptions;
    private Locations selectedLocation;

    // Inner class to hold combined data
    public static class HouseholdMemberData {
        public Residency residency;
        public Individual individual;

        public HouseholdMemberData(Residency residency, Individual individual) {
            this.residency = residency;
            this.individual = individual;
        }
    }

    public SocialgroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment HouseholdFragment.
     */
    public static SocialgroupFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        SocialgroupFragment fragment = new SocialgroupFragment();
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
        householdMembers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSocialgroupBinding.inflate(inflater, container, false);
        binding.setSocialgroup(socialgroup);

        IndividualSharedViewModel sharedModel = new ViewModelProvider(requireActivity()).get(IndividualSharedViewModel.class);
        selectedIndividual = sharedModel.getCurrentSelectedIndividual();

        ClusterSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(ClusterSharedViewModel.class);
        selectedLocation = sharedViewModel.getCurrentSelectedLocation();

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        // Find the button view
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_hoh);

        // Set a click listener on the button for changing household head
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Household Members...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);

                // Show the dialog fragment
                ChangeHohFragment.newInstance(individual, locations, socialgroup)
                        .show(getChildFragmentManager(), "ChangeHohFragment");
            }
        });

        // Setup RecyclerView for household members
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.household_members_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HouseholdMemberAdapter(requireContext(), householdMembers, relationshipOptions);
        recyclerView.setAdapter(adapter);

        // Load relationship options
        loadRelationshipOptions();

        // Load household members
        loadHouseholdMembers();

        // LOAD SPINNERS
        loadCodeData(binding.selectGroupType, "groupType");

        binding.buttonSaveClose.setOnClickListener(v -> {
            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {
            save(false, true);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.SOCIALGROUPSLAYOUT);
        View v = binding.getRoot();
        return v;
    }

    private void loadRelationshipOptions() {
        final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        try {
            relationshipOptions = viewModel.findCodesOfFeature("rltnhead");
            if (relationshipOptions == null) {
                relationshipOptions = new ArrayList<>();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            relationshipOptions = new ArrayList<>();
        }
    }

    private void loadHouseholdMembers() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading household members...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
                IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

                // Get all residencies for this socialgroup
                List<Residency> residencies = residencyViewModel.findResidenciesBySocialgroup(selectedLocation.uuid,socialgroup.uuid);
                List<HouseholdMemberData> memberDataList = new ArrayList<>();

                if (residencies != null) {
                    for (Residency residency : residencies) {
                        try {
                            // Get individual by UUID
                            Individual individual = individualViewModel.find(residency.individual_uuid);
                            if (individual != null) {
                                memberDataList.add(new HouseholdMemberData(residency, individual));
                            }
                        } catch (Exception e) {
                            Log.e("SocialgroupFragment", "Error loading individual: " + residency.individual_uuid, e);
                        }
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    progressDialog.dismiss();
                    if (!memberDataList.isEmpty()) {
                        householdMembers.clear();
                        householdMembers.addAll(memberDataList);
                        adapter.updateData(householdMembers, relationshipOptions);
                    } else {
                        Toast.makeText(requireContext(), "No household members found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e("SocialgroupFragment", "Error loading members", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Error loading household members", Toast.LENGTH_SHORT).show();
                });
            }
        });
        executor.shutdown();
    }

    private void save(boolean save, boolean close) {
        if (save) {
            // Validate all relationships are selected
            boolean allValid = true;
            int headOfHouseholdCount = 0;

            for (HouseholdMemberData memberData : householdMembers) {
                if (memberData.residency.rltn_head == 0 || memberData.residency.rltn_head == -1) {
                    allValid = false;
                    break;
                }
                // Count how many are marked as head of household
                if (memberData.residency.rltn_head == 1) {
                    headOfHouseholdCount++;
                }
            }

            if (!allValid) {
                Toast.makeText(requireContext(), "Please select relationship for all household members", Toast.LENGTH_LONG).show();
                return;
            }

            if (headOfHouseholdCount != 1) {
                Toast.makeText(requireContext(), "Exactly one member must be marked as Head of Household (relationship = 1)", Toast.LENGTH_LONG).show();
                return;
            }

            // Save socialgroup
            Socialgroup finalData = binding.getSocialgroup();
            finalData.individual_uuid = binding.getSocialgroup().individual_uuid;
            finalData.groupName = binding.getSocialgroup().groupName;
            finalData.complete = 1;

            SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            socialgroupViewModel.add(finalData);

            // Update all household members' relationships
            updateAllRelationships();
        }

        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
        }
    }

    private void updateAllRelationships() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

            for (HouseholdMemberData memberData : householdMembers) {
                try {
                    ResidencyRelationshipUpdate amendment = new ResidencyRelationshipUpdate();
                    amendment.uuid = memberData.residency.uuid;
                    amendment.rltn_head = memberData.residency.rltn_head;
                    amendment.complete = 1;

                    viewModel.update(amendment, result ->
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (result > 0) {
                                    Log.d("SocialgroupFragment", "Updated relationship for: " + memberData.individual.firstName + " " + memberData.individual.lastName);
                                } else {
                                    Log.e("SocialgroupFragment", "Failed to update relationship for: " + memberData.individual.firstName + " " + memberData.individual.lastName);
                                }
                            })
                    );
                } catch (Exception e) {
                    Log.e("SocialgroupFragment", "Error updating relationship", e);
                }
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                // Check if fragment is still attached before showing toast
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "All relationships updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("SocialgroupFragment", "All relationships updated successfully");
                }
            });
        });
        executor.shutdown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private <T> void callable(Spinner spinner, T[] array) {
        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(requireActivity(),
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadCodeData(Spinner spinner, final String codeFeature) {
        final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        try {
            List<KeyValuePair> list = viewModel.findCodesOfFeature(codeFeature);
            KeyValuePair kv = new KeyValuePair();
            kv.codeValue = AppConstants.NOSELECT;
            kv.codeLabel = AppConstants.SPINNER_NOSELECT;
            if (list != null && !list.isEmpty()) {
                list.add(0, kv);
                callable(spinner, list.toArray(new KeyValuePair[0]));
            } else {
                list = new ArrayList<KeyValuePair>();
                list.add(kv);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}