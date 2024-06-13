package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.databinding.FragmentIndividualBinding;
import org.openhds.hdsscapture.databinding.FragmentInmigrationBinding;
import org.openhds.hdsscapture.databinding.FragmentOutmigrationBinding;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.OmgUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InmigrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InmigrationFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentInmigrationBinding binding;

    public InmigrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations   Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual  Parameter 4.
     * @return A new instance of fragment InmigrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InmigrationFragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        InmigrationFragment fragment = new InmigrationFragment();
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
        //return inflater.inflate(R.layout.fragment_inmigration, container, false);
        binding = FragmentInmigrationBinding.inflate(inflater, container, false);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        try {
            Inmigration dataimg = inmigrationViewModel.find(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.uuid);
            if (dataimg != null) {
                binding.setInmigration(dataimg);

                if (dataimg.status != null && dataimg.status == 2) {
                    cmt.setVisibility(View.VISIBLE);
                    rsv.setVisibility(View.VISIBLE);
                    rsvd.setVisibility(View.VISIBLE);
                } else {
                    cmt.setVisibility(View.GONE);
                    rsv.setVisibility(View.GONE);
                    rsvd.setVisibility(View.GONE);
                }

            } else {
                dataimg = new Inmigration();

            }

            Date currentDate = new Date(); // Get the current date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            // Extract the hour, minute, and second components
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            int mm = cal.get(Calendar.MINUTE);
            int ss = cal.get(Calendar.SECOND);
            // Format the components into a string with leading zeros
            String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);
            dataimg.sttime = timeString;


            binding.setInmigration(dataimg);
            binding.getInmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        } catch(ExecutionException | InterruptedException e) {
        e.printStackTrace(); }

        //LOAD SPINNERS
        loadCodeData(binding.reason,  "reason");
        loadCodeData(binding.origin,  "comingfrom");
        loadCodeData(binding.migtype,  "migType");
        loadCodeData(binding.farm,  "farm");
        loadCodeData(binding.livestock,  "livestock");
        loadCodeData(binding.cashCrops,  "cashcrops");
        loadCodeData(binding.foodCrops,  "food");
        loadCodeData(binding.livestockYn, "submit");
        loadCodeData(binding.cashYn, "submit");
        loadCodeData(binding.foodYn, "submit");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, inmigrationViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, inmigrationViewModel);
        });

        binding.setEventname(AppConstants.EVENT_MIND00S);
        Handler.colorLayouts(requireContext(), binding.INMIGRATIONLAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, InmigrationViewModel inmigrationViewModel) {

        if (save) {
            Inmigration finalData = binding.getInmigration();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.INMIGRATIONLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            boolean missedout = false;

            if (finalData.migType!=null && finalData.migType==2){
                if (finalData.reason!=null && finalData.reason==19) {
                    missedout = true;
                    Toast.makeText(getActivity(), "Reason cannot be missed out for Internal Inmigration", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            finalData.complete = 1;
            inmigrationViewModel.add(finalData);

            OutmigrationViewModel omgModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
            try {
                Outmigration data = omgModel.finds(individual.uuid);
//                if (data != null && !binding.omgg.oldLoc.getText().toString().trim().equals(binding.currentLoc.getText().toString().trim()))
                if (data != null) {
                    OmgUpdate omg = new OmgUpdate();

                    omg.residency_uuid = binding.getInmigration().residency_uuid;
                    omg.destination = binding.getInmigration().origin;
                    omg.reason = binding.getInmigration().reason;
                    omg.reason_oth = binding.getInmigration().reason_oth;
                    omg.complete = 1;
                    omg.edit = 1;

                    omgModel.update(omg);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
        DATE("DATE");

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