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
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutmigrationBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutmigrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutmigrationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String OUTMIGRATION_ID = "OUTMIGRATION_ID";
    private final String TAG = "OUTMIGRATION.TAG";

    private Location location;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentOutmigrationBinding binding;;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Outmigration outmigration;
    private Visit visit;

    public OutmigrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param location Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @param caseItem Parameter 6.
     * @param eventForm Parameter 7.
     * @return A new instance of fragment OutmigrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutmigrationFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        OutmigrationFragment fragment = new OutmigrationFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, location);
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
            location = getArguments().getParcelable(LOC_LOCATION_IDS);
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
        binding = FragmentOutmigrationBinding.inflate(inflater, container, false);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((OutmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(OutmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.omgInsertDate.setText(result);

            }

            if (bundle.containsKey((OutmigrationFragment.DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(OutmigrationFragment.DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.omgDate.setText(result);
            }
        });

        binding.buttonOmgInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(OutmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonOmgImgDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(OutmigrationFragment.DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        //LOAD SPINNERS
        loadCodeData(binding.reasonOut, "reason");
        loadCodeData(binding.destination, "whereoutside");
        loadCodeData(binding.imgComplete, "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {
            final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);

            final Outmigration outmigration = binding.getOutmigration();
            outmigration.setIndividual_uuid(this.individual.getIndividual_uuid());

            boolean isExists = false;
            binding.omgExtid.setError(null);
            binding.omgDate.setError(null);
            binding.omgVisitid.setError(null);

            if(outmigration.individual_uuid==null){
                isExists = true;
                binding.omgExtid.setError("Individual Id is Required");
            }

            if(outmigration.insertDate==null){
                isExists = true;
                binding.omgDate.setError("Date of Outmigration is Required");

            }

            if(outmigration.fw_uuid==null){
                isExists = true;
                binding.omgFw.setError("Fieldworker is Required");
            }



        });

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        Handler.colorLayouts(requireContext(), binding.OUTMIGRATIONLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Outmigration finalData = binding.getOutmigration();


            if (finalData.complete != null) {

            }

            OutmigrationViewModel viewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    EventsFragment.newInstance(individual,residency,location, socialgroup,caseItem)).commit();
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
        INSERTDATE ("INSERTDATE"),
        RECORDDATE ("RECORDDATE");

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