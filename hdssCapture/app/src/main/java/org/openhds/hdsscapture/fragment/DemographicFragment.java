package org.openhds.hdsscapture.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.databinding.FragmentDemographicBinding;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemographicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemographicFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "INDIVIDUAL.TAG";

    // TODO: Rename and change types of parameters
    private Location location;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDemographicBinding binding;;

    public DemographicFragment() {
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
     * @return A new instance of fragment DemographicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemographicFragment newInstance(Individual individual, Residency residency, Location location, Socialgroup socialgroup) {
        DemographicFragment fragment = new DemographicFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, location);
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
            location = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDemographicBinding.inflate(inflater, container, false);
        binding.setIndividual(individual);

        final TextView compno = binding.getRoot().findViewById(R.id.textView2_extid);
        final TextView compname = binding.getRoot().findViewById(R.id.textView2_firstname);
        final TextView cluster = binding.getRoot().findViewById(R.id.textView2_lastname);

        compno.setText(location.getExtId());
        compname.setText(location.getLocationName());
        cluster.setText(location.getClusterId());

        binding.buttonSaveClose.setOnClickListener(v -> {
            final DemographicViewModel demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);

            final Demographic demographic = binding.getDemographic();
            individual.setExtId(this.individual.getExtId());

            boolean isExists = false;
            binding.demographicExtid.setError(null);

            if(demographic.extId==null){
                isExists = true;
                binding.demographicExtid.setError("Individual Id is Required");
            }



        });

        //LOAD SPINNERS
        loadCodeData(binding.tribe, "tribe");
        loadCodeData(binding.religion,  "religion");
        loadCodeData(binding.education, "education");
        loadCodeData(binding.occupation, "occupation");
        loadCodeData(binding.marital, "marital");
        loadCodeData(binding.demoComplete, "yn");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true);
        });

        Handler.colorLayouts(requireContext(), binding.DEMOLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close) {

        if (save) {
            Demographic finalData = binding.getDemographic();


            if (finalData.complete != null) {

            }

            DemographicViewModel viewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
            viewModel.add(finalData);
        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_main,
                    HouseVisitFragment.newInstance(individual,residency,location, socialgroup)).commit();
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