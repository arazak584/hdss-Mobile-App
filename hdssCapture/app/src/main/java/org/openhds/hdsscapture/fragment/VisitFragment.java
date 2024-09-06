package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Utilities.UniqueIDGen;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentVisitBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.HouseholdAmendment;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitFragment extends DialogFragment {

    private Visit visit;
    private FragmentVisitBinding binding;
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private EventForm eventForm;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private final String TAG = "VISIT.TAG";




    public VisitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.

     * @return A new instance of fragment VisitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        VisitFragment fragment = new VisitFragment();
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
        binding = FragmentVisitBinding.inflate(inflater, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        SocialgroupViewModel vmodel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        VisitViewModel viewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        try {
            Visit data = viewModel.find(socialgroup.uuid);
            if (data != null) {
                binding.setVisit(data);

//                data.houseExtId = socialgroup.extId;
//                data.socialgroup_uuid =socialgroup.uuid;
//                if(roundData.roundNumber < 10) {
//                    data.extId = data.houseExtId + "00" + roundData.getRoundNumber();
//                }else {
//                    data.extId = data.houseExtId + "0" + roundData.getRoundNumber();
//                }

                binding.getVisit().setVisitDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            } else {
                data = new Visit();

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                data.uuid = uuidString;
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.location_uuid = ClusterFragment.selectedLocation.getUuid();
                data.roundNumber = roundData.getRoundNumber();
                //data.uuid = socialgroup.getVisit_uuid();
                data.complete = 1;
                data.houseExtId = socialgroup.extId;
                data.socialgroup_uuid =socialgroup.uuid;
                if(roundData.roundNumber < 10) {
                    data.extId = data.houseExtId + "00" + roundData.getRoundNumber();
                }else {
                    data.extId = data.houseExtId + "0" + roundData.getRoundNumber();
                }
                Date currentDate = new Date(); // Get the current date and time
                // Create a Calendar instance and set it to the current date and time
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                // Extract the hour, minute, and second components
                int hh = cal.get(Calendar.HOUR_OF_DAY);
                int mm = cal.get(Calendar.MINUTE);
                int ss = cal.get(Calendar.SECOND);
                // Format the components into a string with leading zeros
                String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
                data.sttime = timeString;

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
            SocialgroupViewModel vmodel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            IndividualViewModel imodel = new ViewModelProvider(this).get(IndividualViewModel.class);
            String id = UniqueIDGen.generateHouseholdId(vmodel, ClusterFragment.selectedLocation.compextId);
            String originalHouseExtId = finalData.houseExtId;

                final boolean validateOnComplete = true;//finalData.complete == 1;
                boolean hasErrors = new Handler().hasInvalidInput(binding.VISITLAYOUT, validateOnComplete, false);

                if (hasErrors) {
                    Toast.makeText(requireContext(), R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                    return;
                }
            finalData.complete =1;

            Date end = new Date(); // Get the current date and time
            // Create a Calendar instance and set it to the current date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            // Extract the hour, minute, and second components
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            // Format the components into a string with leading zeros
            String endtime = String.format("%02d:%02d:%02d", hh, mm, ss);

            //Update hohid in individual if hohid length!=11
            try {
                List<Individual> individuals = imodel.hoh(ClusterFragment.selectedLocation.compno, originalHouseExtId);
                Log.d("Individual", "HOHID LENGTH 2: " + originalHouseExtId + " " + ClusterFragment.selectedLocation.compno);
                Log.d("Individual", "Individuals List Size: " + (individuals != null ? individuals.size() : "null"));
                // Iterate over each Individual record
                for (Individual data : individuals) {
                    Log.d("Individual", "HOHID LENGTH 3: " + originalHouseExtId);

                    if (data != null && originalHouseExtId != null && originalHouseExtId.length() != 11) {
                        IndividualResidency items = new IndividualResidency();
                        items.uuid = data.uuid;
                        items.hohID = id;
                        imodel.updateres(items);
                        Log.d("IndividualResidency", "Updated Individual UUID: " + data.uuid + " with new HOHID: " + id);
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }



            if(finalData.houseExtId.length() !=11){
                Log.d("Individual", "HOHID LENGTH 1: " + finalData.houseExtId);

                finalData.houseExtId = id;
                if(finalData.roundNumber < 10) {
                    finalData.extId = id + "00" + finalData.getRoundNumber();
                }else {
                    finalData.extId = id + "0" + finalData.getRoundNumber();
                }
            }

            if (finalData.sttime !=null && finalData.edtime==null){
                finalData.edtime = endtime;
            }

            viewModel.add(finalData);


            try {
                Socialgroup data = vmodel.find(socialgroup.uuid);
                if (data != null && originalHouseExtId.length() !=11) {
                    HouseholdAmendment item = new HouseholdAmendment();
                    item.complete = 1;
                    item.uuid = data.uuid;
                    item.extId = id;
                    vmodel.update(item);

                    Log.d("Print", "New HOHID: " + finalData.houseExtId + " " + item.extId);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Socialgroup data = vmodel.visit(socialgroup.uuid);
                if (data != null) {
                    HvisitAmendment visit = new HvisitAmendment();
                    visit.uuid = binding.getVisit().socialgroup_uuid;
                    visit.complete = 2;
                    vmodel.visited(visit);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();

        }
        if (close) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    HouseMembersFragment.newInstance(locations, socialgroup,individual)).commit();
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