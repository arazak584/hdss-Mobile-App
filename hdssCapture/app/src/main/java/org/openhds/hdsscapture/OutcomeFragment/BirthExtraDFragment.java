package org.openhds.hdsscapture.OutcomeFragment;

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
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentBirthDBinding;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BirthExtraDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthExtraDFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "OUTCOME.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentBirthDBinding binding;

    public BirthExtraDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment BirthAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BirthExtraDFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        BirthExtraDFragment fragment = new BirthExtraDFragment();
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
        binding = FragmentBirthDBinding.inflate(inflater, container, false);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        try {
            Pregnancyoutcome datas = viewModel.findsloc(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.compno);
            if (datas != null) {
                binding.setPregoutcome(datas);

                try {
                    final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD8 + 0 + roundData.roundNumber;
                    Outcome data = outcomeViewModel.find(child_id,ClusterFragment.selectedLocation.uuid);
                    if (data != null) {
                        data.preg_uuid = binding.getPregoutcome().getUuid();
                        binding.setPregoutcome4(data);

                        if (data.childuuid == null){
                            String uuid = UUID.randomUUID().toString();
                            String uuidString = uuid.replaceAll("-", "");

                            data.childuuid = uuidString;
                            data.individual_uuid = uuidString;
                        }

                        // Generate ID if extId is null
                        String indid = data.extId;
                        if (binding.getPregoutcome4().extId == null || indid.length() != 12) {
                            String id = UniqueIDGen.generateUniqueId(individualViewModel, ClusterFragment.selectedLocation.compextId);
                            binding.getPregoutcome4().extId = id; // set the generated ID to the extId property of the Individual object
                        }else{
                            binding.getPregoutcome4().extId = data.extId;
                        }

                    } else {
                        data = new Outcome();

                        //Additions
                        String uuid = UUID.randomUUID().toString();
                        String uuidString = uuid.replaceAll("-", "");

                        String rs = UUID.randomUUID().toString();
                        String rsi = rs.replaceAll("-", "");

                        data.individual_uuid = uuidString;
                        data.childuuid = uuidString;
                        //data.mother_uuid = HouseMembersFragment.selectedIndividual.uuid;
                        data.residency_uuid = rsi;
                        data.location = ClusterFragment.selectedLocation.uuid;

                        data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                        data.child_idx = AppConstants.CHILD8;

                        data.vis_number = 0;

                        data.child_screen = data.mother_uuid + data.child_idx;
                        data.uuid = data.child_screen+data.vis_number+ roundData.getRoundNumber() ;
                        data.complete = 1;
                        data.preg_uuid = binding.getPregoutcome().getUuid();


                        binding.setPregoutcome4(data);
                        binding.getPregoutcome4().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                        // Generate ID if extId is null
                        if (binding.getPregoutcome4().extId == null) {
                            String id = UniqueIDGen.generateUniqueId(individualViewModel, ClusterFragment.selectedLocation.compextId);
                            binding.getPregoutcome4().extId = id;
                        }

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.out1Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.gender, codeBookViewModel, "gender");
        loadCodeData(binding.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.chdSize, codeBookViewModel, "size");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, outcomeViewModel,individualViewModel,residencyViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, outcomeViewModel,individualViewModel,residencyViewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, OutcomeViewModel outcomeViewModel, IndividualViewModel individualViewModel, ResidencyViewModel residencyViewModel) {

        if (save) {
            Outcome data = binding.getPregoutcome4();

            Pregnancyoutcome finalData = binding.getPregoutcome();

            final Intent j = getActivity().getIntent();
            final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);


            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            int count = 0; // Initialize a count variable to keep track of the number of occurrences where 'type' is 1

            if (binding.getPregoutcome4().type !=null && binding.getPregoutcome4().type == 1) {
                count++;
            }

            if (binding.getPregoutcome().numberofBirths != null) {

                if (finalData.numberofBirths >= 1) {
                    hasErrors = hasErrors || new HandlerSelect().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome4();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 5.0) {
                            weight = true;
                            binding.weigHcard.setError("Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    if (finalData.outcomeDate != null && finalData.conceptionDate != null) {
                        Date outcomeDate = finalData.outcomeDate;
                        Date conceptionDate = finalData.conceptionDate;

                        // Calculate the difference in milliseconds
                        long diffInMillis = outcomeDate.getTime() - conceptionDate.getTime();

                        // Convert milliseconds to days
                        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                        // Convert days to weeks
                        int totalWeeks = (int) (diffInDays / 7);

                        if (totalWeeks < 20 && (inf.type == 1 || inf.type == 2)) {
                            // Display message for Live Birth or Still Birth
                            Toast.makeText(getActivity(), "Outcome cannot be Live Birth or Still Birth for " + totalWeeks + " Weeks Pregnancy", Toast.LENGTH_LONG).show();
                            return;
                        } else if (totalWeeks > 19 && inf.type == 3) {
                            // Display message for Miscarriage
                            Toast.makeText(getActivity(), "Outcome cannot be Miscarriage for " + totalWeeks + " Weeks Pregnancy", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    inf.complete = 1;
                    if (inf.type != 1) {
                        inf.childuuid = null;
                        inf.extId = null;
                    }
                    outcomeViewModel.add(inf);

                    DeathViewModel dth = new ViewModelProvider(this).get(DeathViewModel.class);
                    if (binding.getPregoutcome4().type==1){
                        Individual ind = new Individual();
                        Outcome prg = binding.getPregoutcome4();
                        ind.uuid= prg.individual_uuid;
                        ind.gender = prg.gender;
                        ind.mother_uuid= prg.mother_uuid;
                        ind.father_uuid= binding.getPregoutcome().father_uuid;
                        ind.firstName= prg.firstName;
                        ind.lastName= prg.lastName;
                        ind.extId= prg.extId;
                        ind.insertDate=prg.insertDate;
                        ind.fw_uuid= binding.getPregoutcome().fw_uuid;
                        ind.dob = binding.getPregoutcome().outcomeDate;
                        ind.complete = 1;
                        ind.dobAspect = 1;
                        ind.hohID = socialgroup.extId;
                        ind.compno = ClusterFragment.selectedLocation.compno;
                        //ind.endType = 1;
                        ind.village = level6Data.getName();
                        try {
                            Death dta = dth.finds(prg.individual_uuid);
                            if (dta != null){
                                ind.endType = 3;
                            }else{
                                ind.endType = 1;
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        individualViewModel.add(ind);

                    }

                    if (binding.getPregoutcome4().type==1){
                        Residency res = new Residency();
                        Outcome prg = binding.getPregoutcome4();
                        res.uuid= prg.residency_uuid;
                        res.individual_uuid = prg.individual_uuid;
                        res.startDate= binding.getPregoutcome().outcomeDate;
                        //res.endType= 1;
                        res.startType= 2;
                        res.insertDate= prg.insertDate;
                        res.location_uuid= ClusterFragment.selectedLocation.uuid;
                        res.socialgroup_uuid = socialgroup.uuid;
                        res.fw_uuid= binding.getPregoutcome().fw_uuid;
                        res.rltn_head = prg.rltn_head;
                        res.complete = 1;
                        try {
                            Death dta = dth.finds(prg.individual_uuid);
                            if (dta != null){
                                res.endType = 3;
                            }else{
                                res.endType = 1;
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        residencyViewModel.add(res);
                    }


                }

                if (!binding.individual1FirstName.getText().toString().trim().isEmpty()) {
                    boolean val = false;
                    String firstName = binding.individual1FirstName.getText().toString();
                    if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                        binding.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    } else {
                        binding.individual1FirstName.setError(null); // Clear the error if the input is valid
                    }

                    boolean vals = false;
                    String lastName = binding.individual1LastName.getText().toString();
                    if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                        binding.individual1LastName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        vals = true;
                        return;
                    } else {
                        binding.individual1LastName.setError(null); // Clear the error if the input is valid
                    }
                }

            }


            data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
            data.complete=1;
            outcomeViewModel.add(data);
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_SHORT).show();

        }
        if (save) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BirthExtraSFragment.newInstance(individual, locations, socialgroup)).commit();
        }else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    BirthExtraCFragment.newInstance(individual, locations, socialgroup)).commit();
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

    private void loadCodeData(Spinner spinner, CodeBookViewModel viewModel, final String codeFeature) {
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