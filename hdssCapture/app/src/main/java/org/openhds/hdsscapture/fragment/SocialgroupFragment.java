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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.ChangeHohFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocialgroupBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialgroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialgroupFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";

    //private Cluster cluster_id;
    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentSocialgroupBinding binding;
    private ProgressDialog progressDialog;

    public SocialgroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param cluster_id  Parameter 1.
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSocialgroupBinding.inflate(inflater, container, false);
        //View view = inflater.inflate(R.layout.fragment_house_visit, container, false);
        binding.setSocialgroup(socialgroup);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        // Find the button view
        Button showDialogButton = binding.getRoot().findViewById(R.id.button_change_hoh);

        // Set a click listener on the button for mother
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
                ChangeHohFragment.newInstance(individual, locations,socialgroup)
                        .show(getChildFragmentManager(), "ChangeHohFragment");
            }
        });

//        SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
//            try {
//            Socialgroup data = viewModel.findhse(socialgroup.uuid);
//            if (data != null) {
//
//                binding.setSocialgroup(data);
//
//                if (HouseMembersFragment.selectedIndividual.firstName != null && "UNK".equals(data.groupName)){
//
//                    data.groupName = HouseMembersFragment.selectedIndividual.firstName +' '+ HouseMembersFragment.selectedIndividual.lastName;
//                    data.individual_uuid = HouseMembersFragment.selectedIndividual.uuid;
//                }
//
//            }
//           } catch (ExecutionException | InterruptedException e) {
//               e.printStackTrace();
//           }



        //LOAD SPINNERS
        loadCodeData(binding.selectGroupType, "groupType");
        loadCodeData(binding.residencyComplete, "submit");


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

    private void save(boolean save, boolean close) {

        if (save) {
            Socialgroup finalData = binding.getSocialgroup();

            finalData.individual_uuid = binding.getSocialgroup().individual_uuid;
            finalData.groupName = binding.getSocialgroup().groupName;
            finalData.complete=1;
            SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
        }
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