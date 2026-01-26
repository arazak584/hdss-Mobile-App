package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.DemographicRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentDemographicBinding;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualPhone;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.KeyboardFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemoViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemoViewFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    public static final String DENO_INFO = "file:///android_asset/deno_views.html";

    private FragmentDemographicBinding binding;
    private Demographic demographic;
    private DemographicRepository repository;

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog simpleDialog = SimpleDialog.newInstance(message, codeFragment);
        simpleDialog.show(getChildFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }


    public DemoViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PregnancyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemoViewFragment newInstance(String uuid) {
        DemoViewFragment fragment = new DemoViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new DemographicRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.demographic = new Demographic();  // Initialize placeholder
            this.demographic.individual_uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDemographicBinding.inflate(inflater, container, false);
        binding.setDemographic(demographic);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        DemographicViewModel viewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        viewModel.getView(demographic.individual_uuid).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.setDemographic(data);

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

                // Fetch Individual Age
                IndividualViewModel indage = new ViewModelProvider(this).get(IndividualViewModel.class);
                try {
                    Individual datae = indage.find(data.individual_uuid);
                    if (datae != null) {
                        binding.setIndividual(datae);

                        AppCompatEditText age = binding.getRoot().findViewById(R.id.age);
                        age.setText(String.valueOf(datae.getAge()));
                    }
                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions properly
                    e.printStackTrace();
                    // Optionally, show an error message to the user
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //LOAD SPINNERS
        loadCodeData(binding.tribe, "tribe");
        loadCodeData(binding.religion,  "religion");
        loadCodeData(binding.education, "education");
        loadCodeData(binding.occupation, "occupation");
        loadCodeData(binding.marital, "marital");
        loadCodeData(binding.demoPhone, "complete");
        loadCodeData(binding.akan, "akan");
        loadCodeData(binding.denomination, "denomination");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.DEMOGRAPHICLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, DemographicViewModel viewModel) {

        if (save) {
            Demographic finalData = binding.getDemographic();

            boolean compyrs = false;
            if (!binding.compYrs.getText().toString().trim().isEmpty()) {
                int yrs = Integer.parseInt(binding.compYrs.getText().toString().trim());
                if (yrs < 0 || yrs > 6) {
                    compyrs = true;
                    binding.compYrs.setError("Cannot be less than 1 or More than 6");
                    Toast.makeText(getActivity(), "Cannot be less than 1 or More than 6", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean ph = false;
            if (!binding.phone1.getText().toString().trim().isEmpty()) {
                String input = binding.phone1.getText().toString().trim();
                String regex = "[0-9]{10}";

                if (!input.matches(regex)) {
                    ph = true;
                    Toast.makeText(getActivity(), "Phone Number is incorrect", Toast.LENGTH_LONG).show();
                    binding.phone1.setError("Phone Number is incorrect");
                    return;
                }
            }

            // Nullify fields for individuals younger than 11
            if (!binding.age.getText().toString().trim().isEmpty() && binding.getIndividual() != null && binding.getIndividual().age < 12) {
                Log.d("Demo", "Set Demographic to null");
                finalData.phone1 = null;
                finalData.occupation = null;
                finalData.marital = null;
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.DEMOGRAPHICLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);

            try {

                Individual data = iview.find(binding.getDemographic().individual_uuid);
                String phone1 = binding.getDemographic().phone1;
                if (data != null && phone1 != null && phone1.length() == 10) {
                    IndividualPhone cnt = new IndividualPhone();
                    cnt.uuid = finalData.individual_uuid;
                    cnt.phone1 = binding.getDemographic().phone1;
                    iview.contact(cnt);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finalData.complete=1;
            viewModel.add(finalData);

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    ViewFragment.newInstance()).commit();
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