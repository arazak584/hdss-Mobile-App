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
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.databinding.FragmentSocioBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
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
 * Use the {@link SocioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocioFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentSocioBinding binding;

    public SocioFragment() {
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
    public static SocioFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        SocioFragment fragment = new SocioFragment();
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
        binding = FragmentSocioBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        HdssSociodemoViewModel viewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);

        try {
            HdssSociodemo data = viewModel.findses(socialgroup.uuid);
            if (data != null) {
                binding.setSociodemo(data);

                if(data.status!=null && data.status==2){
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                }else{
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

                data.fw_uuid = fieldworkerData.getFw_uuid();
                binding.getSociodemo().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            } else {
                data = new HdssSociodemo();

                //binding.formcompldate.setVisibility(View.GONE);

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");

                data.uuid = uuidString;
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.location_uuid = ClusterFragment.selectedLocation.getUuid();
                data.socialgroup_uuid = socialgroup.getUuid();
                data.fw_uuid = fieldworkerData.getFw_uuid();

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


                binding.setSociodemo(data);
                binding.getSociodemo().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                binding.getSociodemo().setFormcompldate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        loadCodeData(binding.MARITALSCORRES, "MARITAL_SCORRES");
        loadCodeData(binding.RELIGIONSCORRES, "religion");
        loadCodeData(binding.CETHNIC, "tribe");
        loadCodeData(binding.HEADHHFCORRES, "rltnhead");
        loadCodeData(binding.H2OFCORRES, "H2O_FCORRES");
        loadCodeData(binding.TOILETFCORRES, "TOILET_FCORRES");
        loadCodeData(binding.TOILETLOCFCORRES, "TOILET_LOC_FCORRES");
        loadCodeData(binding.TOILETSHARENUMFCORRES, "TOILET_SHARE_NUM_FCORRES");
        loadCodeData(binding.EXTWALLFCORRES, "EXT_WALL_FCORRES");
        loadCodeData(binding.FLOORFCORRES, "FLOOR_FCORRES");
        loadCodeData(binding.ROOFFCORRES, "ROOF_FCORRES");
        loadCodeData(binding.MOBILEACCESSFCORRES, "MOBILE_ACCESS_FCORRES");
        loadCodeData(binding.OWNRENTSCORRES, "OWN_RENT_SCORRES");
        loadCodeData(binding.socioe.JOBSCORRES, "JOB");
        loadCodeData(binding.socioe.PTRSCORRES, "JOB");
        loadCodeData(binding.sociof.STOVEFCORRES, "STOVE_FCORRES");
        loadCodeData(binding.sociof.COOKINGLOCFCORRES, "COOKING_LOC_FCORRES");
        loadCodeData(binding.sociog.SMOKEINOECDOSFRQ, "FRQ");
        loadCodeData(binding.sociog.SMOKEHHOLDINOECDOSFRQ, "FRQ");
        loadCodeData(binding.socioz.socioComplete, "submit");
        loadCodeData(binding.sociod.petVac, "pet_vac");
//        loadCodeData(binding.id0001, "salt");
//        loadCodeData(binding.id0002, "ynd");
        loadCodeData(binding.id0003, "nhis");
        loadCodeData(binding.id0004, "submit");
        loadCodeData(binding.id0005, "nhis_no");
        loadCodeData(binding.sociod.id00061Option, "crop");
        loadCodeData(binding.sociod.id00071Option, "crop");
        loadCodeData(binding.sociod.id00081Option, "crop");
        loadCodeData(binding.sociod.id00091Option, "crop");
        loadCodeData(binding.sociod.id00101Option, "crop");
        loadCodeData(binding.sociod.id00111Option, "crop");
        loadCodeData(binding.sociod.id00111Option, "crop");
        loadCodeData(binding.sociod.id00121Option, "crop");
        loadCodeData(binding.sociod.id00131Option, "crop");
        loadCodeData(binding.sociod.id00141Option, "crop");
        loadCodeData(binding.sociod.id00151Option, "crop");
        loadCodeData(binding.sociod.id00161Option, "crop");
        loadCodeData(binding.sociod.id00171Option, "crop");
        loadCodeData(binding.sociod.id00181Option, "crop");
        loadCodeData(binding.sociod.id00191Option, "crop");


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

    private void save(boolean save, boolean close, HdssSociodemoViewModel viewModel) {

        if (save) {
            final HdssSociodemo data = binding.getSociodemo();

            final Intent i = getActivity().getIntent();
            final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

                final boolean validateOnComplete = true;//finaldata.mnh01_form_complete == 1;
                boolean hasErrors = new Handler().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
//                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.sociob.MAINLAYOUT, validateOnComplete, false);
//                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.socioc.MAINLAYOUT, validateOnComplete, false);
                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.sociod.MAINLAYOUT, validateOnComplete, false);
                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.socioe.MAINLAYOUT, validateOnComplete, false);
                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.sociof.MAINLAYOUT, validateOnComplete, false);
                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.sociog.MAINLAYOUT, validateOnComplete, false);
                hasErrors = hasErrors || new Handler().hasInvalidInput(binding.socioz.MAINLAYOUT, validateOnComplete, false);
                if (hasErrors) {
                    Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                    return;
                }

            data.formcompldate = new Date();
            data.complete = 1;
            data.fw_uuid = fieldworkerData.getFw_uuid();

            boolean mar = false;
            boolean val = false;
            if (data.marital_scorres == 1 && !binding.MARITALAGE.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.MARITALAGE.getText().toString().trim());
                if (totalmth < 10) {
                    mar = true;
                    binding.MARITALAGE.setError("Maximum Age Allowed is 10");
                    Toast.makeText(getActivity(), "Maximum Age Allowed is 10", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (!binding.HOUSEOCCTOTFCORRES.getText().toString().trim().isEmpty() && !binding.HOUSEOCCLT5FCORRES.getText().toString().trim().isEmpty()) {
                int totalpeople = Integer.parseInt(binding.HOUSEOCCTOTFCORRES.getText().toString().trim());
                int total5 = Integer.parseInt(binding.HOUSEOCCLT5FCORRES.getText().toString().trim());
                if (totalpeople < total5) {
                    val = true;
                    binding.HOUSEOCCLT5FCORRES.setError("Children aged five cannot be more than total Individuals in household");
                    Toast.makeText(getActivity(), "Children aged five cannot be more than total Individuals in household", Toast.LENGTH_LONG).show();
                    return;
                }
            }


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

            if (data.sttime !=null && data.edtime==null){
                data.edtime = endtime;
            }
            viewModel.add(data);
            Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
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