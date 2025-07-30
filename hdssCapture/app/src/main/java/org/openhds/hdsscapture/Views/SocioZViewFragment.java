package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocioZBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.SocioFFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocioZViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocioZViewFragment extends Fragment {


    private static final String SOCIAL_ID = "SOCIAL_ID";

    private FragmentSocioZBinding binding;
    private HdssSociodemo hdssSociodemo;
    private HdssSociodemoRepository repository;

    public SocioZViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocioFragment.
     */
    //
    public static SocioZViewFragment newInstance(String uuid) {
        SocioZViewFragment fragment = new SocioZViewFragment();
        Bundle args = new Bundle();
        args.putString(SOCIAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new HdssSociodemoRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(SOCIAL_ID); // Correct key
            this.hdssSociodemo = new HdssSociodemo();  // Initialize placeholder
            this.hdssSociodemo.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_socio_b, container, false);
        binding = FragmentSocioZBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);
        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);

        HdssSociodemoViewModel viewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        viewModel.getView(hdssSociodemo.uuid).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setSociodemo(data);

                if(data.status!=null && data.status==2){
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                    cmt.setVisibility(View.VISIBLE);
                }else{
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                    cmt.setVisibility(View.GONE);
                }

            }
        });



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

            data.formcompldate = new Date();
            data.complete = 1;

            viewModel.add(data);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
        }

        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    SocioFViewFragment.newInstance(hdssSociodemo.uuid)).commit();
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