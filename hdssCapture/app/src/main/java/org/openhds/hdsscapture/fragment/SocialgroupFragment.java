package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocialgroupBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialgroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialgroupFragment extends Fragment {

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
    private FragmentSocialgroupBinding binding;
    private CaseItem caseItem;
    private EventForm eventForm;

    public SocialgroupFragment() {
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
     * @param caseItem Parameter 6.
     * @param eventForm Parameter 7.
     * @return A new instance of fragment SocialgroupFragment.
     */

    public static SocialgroupFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {

        SocialgroupFragment fragment = new SocialgroupFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
        args.putParcelable(RESIDENCY_ID, residency);
        args.putParcelable(SOCIAL_ID, socialgroup);
        args.putParcelable(INDIVIDUAL_ID, individual);
        args.putParcelable(CASE_ID, caseItem);
        args.putParcelable(EVENT_ID, eventForm);
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
        binding = FragmentSocialgroupBinding.inflate(inflater, container, false);
        //View view = inflater.inflate(R.layout.fragment_house_visit, container, false);
        binding.setSocialgroup(socialgroup);

        binding.buttonSaveClose.setOnClickListener(v -> {
            final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);

            final Socialgroup socialgroup = binding.getSocialgroup();
            socialgroup.setHouseExtId(this.socialgroup.getHouseExtId());

            boolean isExists = false;
            binding.sociagroupExtid.setError(null);
            binding.editTextInsertDate.setError(null);
            binding.groupName.setError(null);
            binding.headExtid.setError(null);

            if(socialgroup.houseExtId==null){
                isExists = true;
                binding.sociagroupExtid.setError("HouseholdID is Required");
            }

            if(socialgroup.insertDate==null){
                isExists = true;
                binding.editTextInsertDate.setError("Date of visit is Required");

            }

            if(socialgroup.groupName==null){
                isExists = true;
                binding.groupName.setError("Social Group Name is Required");
            }

            if(socialgroup.individual_uuid==null){
                isExists = true;
                binding.headExtid.setError("Head ID is Required");
            }

        });


        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey(SocialgroupFragment.DATE_BUNDLES.INSERTDATE.getBundleKey())) {
                final String result = bundle.getString((SocialgroupFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()));
                binding.editTextInsertDate.setText(result);
            }

        });

        //LOAD SPINNERS
        loadCodeData(binding.selectGroupType, "groupType");
        //loadCodeData(binding., "complete");

        binding.buttonSocialInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            final String curinsertdate = binding.editTextInsertDate.getText().toString();
            if(curinsertdate!=null){
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
                try {
                    c.setTime(Objects.requireNonNull(f.parse(curinsertdate)));
                } catch (ParseException e) {
                }
            }
            DialogFragment newFragment = new DatePickerFragment(SocialgroupFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });


        Handler.colorLayouts(requireContext(), binding.SOCIALGROUPLAYOUT);
        View v = binding.getRoot();
        return v;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Socialgroup finalData = binding.getSocialgroup();


            if (finalData.complete != null) {

            }

            SocialgroupViewModel viewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual, residency, locations, socialgroup, caseItem)).commit();
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