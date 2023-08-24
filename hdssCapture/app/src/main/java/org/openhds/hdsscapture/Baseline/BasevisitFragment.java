package org.openhds.hdsscapture.Baseline;

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
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentBasevisitBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasevisitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasevisitFragment extends Fragment {

    private Visit visit;
    private FragmentBasevisitBinding binding;
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private EventForm eventForm;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String VISIT_ID = "VISIT_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "VISIT.TAG";

    public BasevisitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment BasevisitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasevisitFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        BasevisitFragment fragment = new BasevisitFragment();
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
            eventForm = getArguments().getParcelable(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBasevisitBinding.inflate(inflater, container, false);


        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);


        VisitViewModel viewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit data = viewModel.find(socialgroup.uuid);
            if (data != null) {
                binding.setVisit(data);
                data.visitDate = new Date();
                if (socialgroup.groupName!= null && "UNK".equals(socialgroup.groupName)){
                    data.respondent = "UNK";
                }
            } else {
                data = new Visit();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.toString().replaceAll("-", "");


                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.location_uuid = locations.getUuid();
                data.roundNumber = roundData.getRoundNumber();
                data.uuid = socialgroup.getVisit_uuid();
                data.complete = 1;
                data.houseExtId = socialgroup.extId;
                data.socialgroup_uuid =socialgroup.uuid;
                data.extId = data.houseExtId + "000";

                if (socialgroup.groupName!= null && "UNK".equals(socialgroup.groupName)){
                    data.respondent = "UNK";
                }

                binding.setVisit(data);
                binding.getVisit().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.getVisit().setVisitDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        loadCodeData(binding.visitcomplete, "submit");
        loadCodeData(binding.realVisit, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {


            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });


        Handler.colorLayouts(requireContext(), binding.VISITLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, VisitViewModel viewModel) {

        if (save) {
            final Visit finalData = binding.getVisit();


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.VISITLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }
            if (finalData.respondent == "UNK"){
                finalData.complete =2;
            }else {
                finalData.complete =1;
            }
            viewModel.add(finalData);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    IndividualSummaryFragment.newInstance(individual,residency, locations, socialgroup)).commit();
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

    private enum DATE_BUNDLES {
        INSERTDATE ("INSERTDATE");
        //DOB ("DOB");

        private final String bundleKey;

        DATE_BUNDLES(String bundleKey) {
            this.bundleKey = bundleKey;

        }

        public String getBundleKey() {
            return bundleKey;
        }

        @Override
        public String toString() {
            return bundleKey;
        }
    }
}