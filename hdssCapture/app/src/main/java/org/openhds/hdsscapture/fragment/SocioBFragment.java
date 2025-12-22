package org.openhds.hdsscapture.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocioBBinding;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocioBFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocioBFragment extends KeyboardFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentSocioBBinding binding;

    public SocioBFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment SocioFragment.
     */
    //
    public static SocioBFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        SocioBFragment fragment = new SocioBFragment();
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
        //return inflater.inflate(R.layout.fragment_socio_b, container, false);
        binding = FragmentSocioBBinding.inflate(inflater, container, false);

        HdssSociodemoViewModel viewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        try {
            HdssSociodemo data = viewModel.findses(socialgroup.uuid);
            if (data != null) {
                binding.setSociodemo(data);

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.H2OFCORRES, "H2O_FCORRES");
        loadCodeData(binding.TOILETFCORRES, "TOILET_FCORRES");
        loadCodeData(binding.TOILETLOCFCORRES, "TOILET_LOC_FCORRES");
        loadCodeData(binding.TOILETSHARENUMFCORRES, "TOILET_SHARE_NUM_FCORRES");
        loadCodeData(binding.EXTWALLFCORRES, "EXT_WALL_FCORRES");
        loadCodeData(binding.FLOORFCORRES, "FLOOR_FCORRES");
        loadCodeData(binding.ROOFFCORRES, "ROOF_FCORRES");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }


    private void save(boolean save, boolean close, HdssSociodemoViewModel viewModel) {

        if (save) {
            final HdssSociodemo data = binding.getSociodemo();

            final boolean validateOnComplete = true;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            viewModel.add(data);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
        }

        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioCFragment.newInstance(individual,locations, socialgroup)).commit();

        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    SocioAFragment.newInstance(individual,locations, socialgroup)).commit();
        }


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

    private enum DATE_BUNDLES {
        INT_DAT("INT_DAT");

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