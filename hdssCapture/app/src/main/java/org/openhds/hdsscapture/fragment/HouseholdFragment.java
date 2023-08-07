package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentHouseholdBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseholdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseholdFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLUSTER_IDS = "ARG_CLUSTER_IDS";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "LOCATION.TAG";

    //private Cluster cluster_id;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Individual individual;
    private FragmentHouseholdBinding binding;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Pregnancy pregnancy;

    public HouseholdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param cluster_id  Parameter 1.
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment HouseholdFragment.
     */

    public static HouseholdFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {

        HouseholdFragment fragment = new HouseholdFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
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
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
            caseItem = getArguments().getParcelable(CASE_ID);
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHouseholdBinding.inflate(inflater, container, false);
        //View view = inflater.inflate(R.layout.fragment_house_visit, container, false);
        binding.setSocialgroup(socialgroup);


        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            Socialgroup data = binding.getSocialgroup();
            if (data != null) {
                binding.selectGroupType.setEnabled(false);
                if (data.complete==null){
                    data.complete = 2;
                }

                if (socialgroup.groupName!= null && "UNK".equals(socialgroup.groupName)){
                    data.complete = 2;
                }

                if (data.visit_uuid==null){

                    String visits = UUID.randomUUID().toString();
                    String visituuid = visits.toString().replaceAll("-", "");
                    data.visit_uuid = visituuid;
                    //data.complete = 2;
                }

            }

        PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        try {
            List<Pregnancy> pregnancyList = pregnancyViewModel.retrievePregnancy(locations.getCompextId());
            if (pregnancyList != null && !pregnancyList.isEmpty()) {
                showPregnancyDialog();
            }

        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //LOAD SPINNERS
        loadCodeData(binding.selectGroupType, "groupType");
        loadCodeData(binding.residencyComplete, "submit");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });


        Handler.colorLayouts(requireContext(), binding.HOUSEHOLDLAYOUT);
        View v = binding.getRoot();
        return v;
    }


    private void save(boolean save, boolean close) {

        if (save) {
            Socialgroup finalData = binding.getSocialgroup();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.HOUSEHOLDLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }


            SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            viewModel.add(finalData);
        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    VisitFragment.newInstance(individual, residency, locations, socialgroup)).commit();
        }else{
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BlankFragment.newInstance(individual, residency, locations, socialgroup)).commit();
        }
    }

    private void showPregnancyDialog() {
        PregnancyDialogFragment.newInstance(individual, residency, locations, socialgroup)
                .show(getChildFragmentManager(), "PregnancyDialogFragment");
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