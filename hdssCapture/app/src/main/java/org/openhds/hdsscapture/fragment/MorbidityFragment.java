package org.openhds.hdsscapture.fragment;

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
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.databinding.FragmentMorbidityBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MorbidityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MorbidityFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private Morbidity morbidity;
    private FragmentMorbidityBinding binding;

    public MorbidityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment MorbidityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MorbidityFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        MorbidityFragment fragment = new MorbidityFragment();
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
        //return inflater.inflate(R.layout.fragment_morbidity, container, false);
        binding = FragmentMorbidityBinding.inflate(inflater, container, false);
        binding.setMorbidity(morbidity);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);


        MorbidityViewModel viewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);

        try {
            Morbidity data = viewModel.finds(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setMorbidity(data);

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

            } else {
                data = new Morbidity();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                data.uuid = uuidString;
                String fname = HouseMembersFragment.selectedIndividual.getFirstName() + " " + HouseMembersFragment.selectedIndividual.getLastName();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.location_uuid = ClusterFragment.selectedLocation.getUuid();
                data.socialgroup_uuid = socialgroup.getUuid();
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.fw_name = fieldworkerData.getFirstName() + " " + fieldworkerData.getLastName();
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.ind_name = fname;


                binding.setMorbidity(data);
                binding.getMorbidity().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadCodeData(binding.feverTreat, "submit");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        Handler.colorLayouts(requireContext(), binding.MAINLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, MorbidityViewModel viewModel) {

        if (save) {
            final Morbidity finalData = binding.getMorbidity();

            final boolean validateOnComplete = true;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            boolean feverdays = false;
            if (!binding.feverDays.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.feverDays.getText().toString().trim());
                if (totals <0 || totals>14) {
                    feverdays = true;
                    binding.feverDays.setError("Should Be Between 0 to 14 days");
                    //Toast.makeText(getActivity(), "Should Be Between 0 to 14 days", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean hyp = false;
            boolean dia = false;
            boolean hea = false;
            boolean str = false;
            boolean sic = false;
            boolean ast = false;
            boolean epi = false;

            if (!binding.hypertensionDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.hypertensionDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    hyp = true;
                    binding.hypertensionDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.diabetesDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.diabetesDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    dia = true;
                    binding.diabetesDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.heartDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.heartDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    hea = true;
                    binding.heartDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.strokeDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.strokeDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    str = true;
                    binding.strokeDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.sickleDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.sickleDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    sic = true;
                    binding.sickleDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.asthmaDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.asthmaDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    ast = true;
                    binding.asthmaDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            if (!binding.epilepsyDur.getText().toString().trim().isEmpty()) {
                int totals = Integer.parseInt(binding.epilepsyDur.getText().toString().trim());
                if (totals <0 || totals>99) {
                    epi = true;
                    binding.epilepsyDur.setError("Out of Range (0-99)");
                    return;
                }
            }

            finalData.complete=1;
            viewModel.add(finalData);


            //Update Individual
            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual data = iview.visited(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.individual_uuid;
                    visited.complete = 2;
                    iview.visited(visited);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup, individual)).commit();
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
}