package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.ClusterDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.databinding.FragmentListingBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.LocationAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String LISTING_ID = "LISTING_ID";

    private Locations locations;
    private Listing listing;
    private FragmentListingBinding binding;
    private Individual individual;
    private Socialgroup socialgroup;
    private Residency residency;
    private Hierarchy hierarchy;
    private ProgressDialog progressDialog;

    public ListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @return A new instance of fragment ListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingFragment newInstance(Locations locations) {
        ListingFragment fragment = new ListingFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListingBinding.inflate(inflater, container, false);
        binding.setListing(listing);

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_cluster);

        // Set a click listener on the button for mother
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog fragment
                ClusterDialogFragment.newInstance(hierarchy, locations)
                        .show(getChildFragmentManager(), "ClusterDialogFragment");
            }
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent intent = getActivity().getIntent();
        final Hierarchy level5Data = intent.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        ListingViewModel viewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        try {
            Listing data = viewModel.find(locations.compno);
            if (data != null) {
                binding.setListing(data);
                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);
            } else {
                data = new Listing();

                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.complete = 1;
                data.compextId = locations.getCompextId();
                data.compno = locations.getCompno();
                data.status = locations.getStatus();
                data.village = level6Data.getName();
                data.fw_name = fieldworkerData.getFirstName() + ' ' + fieldworkerData.lastName ;
                data.locationName = locations.locationName;
                data.location_uuid = locations.uuid;
                data.vill_extId = locations.getExtId();
                data.cluster_id = locations.locationLevel_uuid;

                binding.locationName.setEnabled(false);
                binding.clusterCode.setEnabled(false);
                binding.villcode.setEnabled(false);

                binding.setListing(data);
                binding.getListing().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.locationstatus, "status");
        loadCodeData(binding.complete, "submit");
        loadCodeData(binding.correct, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {


            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, ListingViewModel viewModel) {

        if (save) {
            final Listing finalData = binding.getListing();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

            LocationAmendment location = new LocationAmendment();
            location.uuid = finalData.location_uuid;

            if (!binding.repllocationName.getText().toString().trim().isEmpty()) {
                location.locationName = binding.getListing().repl_locationName;
            }else {
                location.locationName = finalData.locationName;
            }
            location.status = finalData.status;
            location.locationLevel_uuid = finalData.cluster_id;
            location.extId = finalData.vill_extId;

            location.complete = 1;
            LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
            locationViewModel.update(location);

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BlankFragment.newInstance(individual,residency, locations, socialgroup)).commit();
        }
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