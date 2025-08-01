package org.openhds.hdsscapture.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentDeathBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeathViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeathViewFragment extends DialogFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "DEATH.TAG";
    private Death death;
    private FragmentDeathBinding binding;
    private DeathRepository repository;

    public DeathViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeathFragment.
     */
    public static DeathViewFragment newInstance(String uuid) {
        DeathViewFragment fragment = new DeathViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new DeathRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.death = new Death();  // Initialize placeholder
            this.death.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeathBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
                    if (bundle.containsKey((DeathViewFragment.DATE_BUNDLES.DEATHDATE.getBundleKey()))) {
                        final String result = bundle.getString(DeathViewFragment.DATE_BUNDLES.DEATHDATE.getBundleKey());
                        binding.dthDeathDate.setText(result);
                    }
                });

        binding.buttonDeathDod.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.dthDeathDate.getText())) {
                // If replDob is not empty, parse the date and use it as the initial date
                String currentDate = binding.dthDeathDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(DeathViewFragment.DATE_BUNDLES.DEATHDATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(DeathViewFragment.DATE_BUNDLES.DEATHDATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = configViewModel.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                String formattedDate = dateFormat.format(dt);
                editText.setText(formattedDate);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        DeathViewModel viewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        viewModel.getView(death.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setDeath(data);

            try {
            Residency dataRes = resModel.restore(data.individual_uuid);
            if (dataRes != null) {
                    //data.dob = dataRes.startDate;
                    binding.setRes(dataRes);
                    data.residency_uuid = dataRes.uuid;

            } } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }

        });

        loadCodeData(binding.dthDeathPlace, "deathPlace");
        loadCodeData(binding.dthDeathCause, "deathCause");

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

    private void save(boolean save, boolean close, DeathViewModel viewModel) {

        if (save) {
            Death finalData = binding.getDeath();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.dthDeathDate.getText().toString().trim().isEmpty()
                        && !binding.dthDob.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.dthDeathDate.getText().toString().trim());
                    Date dob = f.parse(binding.dthDob.getText().toString().trim());
                    Date currentDate = new Date();
                    if (edate.before(stdate)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.before(dob)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.after(currentDate)) {
                        binding.dthDeathDate.setError("Death Date Cannot Be Future Date");
                        Toast.makeText(getActivity(), "Death Date Cannot Be Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.dthDeathDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
            Vpm v = new Vpm();
            v.complete = 1;
            v.uuid = binding.getDeath().uuid;
            v.individual_uuid = binding.getDeath().individual_uuid;
            v.deathDate = binding.getDeath().deathDate;
            v.dob = binding.getDeath().dob;
            v.insertDate =binding.getDeath().insertDate;
            v.firstName = binding.getDeath().firstName;
            v.lastName = binding.getDeath().lastName;
            v.gender = binding.getDeath().gender;
            v.compno = binding.getDeath().compno;
            v.visit_uuid = binding.getDeath().visit_uuid;
            v.deathCause = binding.getDeath().deathCause;
            v.deathPlace = binding.getDeath().deathPlace;
            v.deathPlace_oth = binding.getDeath().deathPlace_oth;
            v.respondent = binding.getDeath().respondent;
            v.fw_uuid = binding.getDeath().fw_uuid;
            v.extId = binding.getDeath().extId;
            v.househead = binding.getDeath().househead;
            v.compname = binding.getDeath().compname;
            v.villname = binding.getDeath().villname;
            v.villcode = binding.getDeath().villcode;
            vpmViewModel.add(v);

            RelationshipViewModel relModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                //Set Relationship to Widowed
                try {
                    // Second block - visited update (with different variable name)
                    Relationship reldata = relModel.finds(binding.getDeath().individual_uuid);
                    if (reldata != null) {
                        RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                        relationshipUpdate.endType = 2;
                        relationshipUpdate.endDate = binding.getDeath().deathDate;
                        relationshipUpdate.individualA_uuid = binding.getDeath().individual_uuid;
                        relationshipUpdate.complete = 1;

                        relModel.update(relationshipUpdate, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Relationship Update successful (widowed)!");
                                    } else {
                                        Log.d("DeathFragment", "Relationship Update Failed (widowed)!");
                                    }
                                })
                        );
                    }
                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //Set Relationship to Dead
                try {
                    Relationship dthdata = relModel.find(binding.getDeath().individual_uuid);
                    if (dthdata != null && !binding.dthDeathDate.getText().toString().trim().isEmpty()) {

                        RelationshipUpdate relationshipUpdate = new RelationshipUpdate();
                        relationshipUpdate.endType = 4;
                        relationshipUpdate.endDate = binding.getDeath().deathDate;
                        relationshipUpdate.individualA_uuid = binding.getDeath().individual_uuid;
                        relationshipUpdate.complete = 1;

                        relModel.update(relationshipUpdate, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Relationship Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Relationship Update Failed (dead)!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //End Residency In residency entity
                try {
                    Residency resdata = resModel.restore(binding.getDeath().individual_uuid);
                    if (resdata != null) {
                        ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                        residencyAmendment.endType = 3;
                        residencyAmendment.endDate = binding.getDeath().deathDate;
                        residencyAmendment.uuid = binding.getDeath().residency_uuid;
                        residencyAmendment.complete = 1;

                        resModel.update(residencyAmendment, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Residency Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Residency Update Failed (dead)!");
                                    }
                                })
                        );

                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }


                //End Residency In individual entity
                try {
                    Individual inddata = individualViewModel.find(binding.getDeath().individual_uuid);
                    if (inddata != null) {
                        IndividualEnd endInd = new IndividualEnd();
                        endInd.endType = 3;
                        endInd.uuid = binding.getDeath().individual_uuid;
                        endInd.complete = 1;

                        individualViewModel.dthupdate(endInd, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("DeathFragment", "Individual Update successful (dead)!");
                                    } else {
                                        Log.d("DeathFragment", "Individual Update Failed (dead)!");
                                    }
                                })
                        );

                    }

                } catch (Exception e) {
                    Log.e("DeathFragment", "Error in update", e);
                    e.printStackTrace();
                }


            });

            executor.shutdown();


            finalData.complete = 1;
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
    
    private enum DATE_BUNDLES {
        DEATHDATE("DEATHDATE");

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