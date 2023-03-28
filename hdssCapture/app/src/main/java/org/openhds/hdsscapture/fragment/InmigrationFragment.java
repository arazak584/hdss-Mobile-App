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
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.databinding.FragmentInmigrationBinding;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Location;
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
 * Use the {@link InmigrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InmigrationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String INMIGRATION_ID = "INMIGRATION_ID";
    private final String TAG = "INMIGRATION.TAG";

    private Location location;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentInmigrationBinding binding;;
    private CaseItem caseItem;
    private EventForm eventForm;
    private Inmigration inmigration;
    private Visit visit;

    public InmigrationFragment() {
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
     * @return A new instance of fragment InmigrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InmigrationFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup, CaseItem caseItem, EventForm eventForm) {
        InmigrationFragment fragment = new InmigrationFragment();
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
        binding = FragmentInmigrationBinding.inflate(inflater, container, false);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((InmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey()))) {
                final String result = bundle.getString(InmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey());
                binding.imgInsertDate.setText(result);

            }

            if (bundle.containsKey((DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.imgDate.setText(result);
            }
        });

        binding.buttonImgInsertDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(InmigrationFragment.DATE_BUNDLES.INSERTDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonImgImgDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        //LOAD SPINNERS
        loadCodeData(binding.reason, "reason");
        loadCodeData(binding.origin, "whereoutside");
        loadCodeData(binding.imgComplete, "complete");
        loadCodeData(binding.migtype, "migType");

        binding.buttonSaveClose.setOnClickListener(v -> {
            final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);

            final Inmigration inmigration = binding.getInmigration();
            inmigration.setIndividual_uuid(this.individual.getIndividual_uuid());

            boolean isExists = false;
            binding.imgExtid.setError(null);
            binding.imgDate.setError(null);
            binding.imgVisitid.setError(null);

            if(inmigration.individual_uuid==null){
                isExists = true;
                binding.imgExtid.setError("Individual Id is Required");
            }

            if(inmigration.insertDate==null){
                isExists = true;
                binding.imgDate.setError("Date of Inmigration is Required");

            }

            if(inmigration.fw_uuid==null){
                isExists = true;
                binding.imgFw.setError("Fieldworker is Required");
            }



        });

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        Handler.colorLayouts(requireContext(), binding.INMIGRATIONLAYOUT);
        View view = binding.getRoot();

        return view;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Inmigration finalData = binding.getInmigration();


            if (finalData.complete != null) {

            }

            InmigrationViewModel viewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
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