package org.openhds.hdsscapture.Duplicate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentDupBinding;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.HvisitAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DupFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String RESIDENCY_ID = "RESIDENCY_ID";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String CASE_ID = "CASE_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private final String TAG = "DUP.TAG";
    private String fw;

    private Locations locations;
    private Residency residency;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentDupBinding binding;
    private EventForm eventForm;
    private ProgressDialog progressDialog;

    public DupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param residency Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment DupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DupFragment newInstance(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup) {
        DupFragment fragment = new DupFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOC_LOCATION_IDS, locations);
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
            locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            residency = getArguments().getParcelable(RESIDENCY_ID);
            socialgroup = getArguments().getParcelable(SOCIAL_ID);
            individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDupBinding.inflate(inflater, container, false);

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        // Retrieve fw_uuid from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        Button showDialogButton = binding.getRoot().findViewById(R.id.button_dup);
        Button showDialogButton1 = binding.getRoot().findViewById(R.id.button1_dup);
        Button showDialogButton2 = binding.getRoot().findViewById(R.id.button2_dup);
        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        // Set a click listener on the button for partner
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Individuals...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);


                // Show the dialog fragment
                DupDialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "DupDialogFragment");
            }
        });

        showDialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Individuals...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);


                // Show the dialog fragment
                Dup1DialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "Dup1DialogFragment");
            }
        });

        showDialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Individuals...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Simulate long operation
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);


                // Show the dialog fragment
                Dup2DialogFragment.newInstance(individual, residency, locations,socialgroup)
                        .show(getChildFragmentManager(), "Dup2DialogFragment");
            }
        });

        DuplicateViewModel viewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
        try {
            Duplicate data = viewModel.find(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setDup(data);
                binding.fname.setEnabled(false);
                binding.lname.setEnabled(false);
                binding.dupFname.setEnabled(false);
                binding.dupLname.setEnabled(false);
            } else {
                data = new Duplicate();

                data.fw_uuid = fw;
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.fname = HouseMembersFragment.selectedIndividual.getFirstName();
                data.lname = HouseMembersFragment.selectedIndividual.getLastName();
                data.dob = HouseMembersFragment.selectedIndividual.dob;

                binding.fname.setEnabled(false);
                binding.lname.setEnabled(false);
                binding.dupFname.setEnabled(false);
                binding.dupLname.setEnabled(false);

                binding.setDup(data);
                binding.getDup().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //SPINNERS
        loadCodeData(binding.relComplete,  "submit");

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


    private void save(boolean save, boolean close, DuplicateViewModel viewModel) {

        if (save) {
            Duplicate finalData = binding.getDup();

            boolean sameID = false;
            boolean dup1 = false;
            boolean dup2 = false;
            boolean dup3 = false;

            if (binding.uuid.getText().toString().trim().equals(binding.dupUuid.getText().toString().trim())) {
                sameID = true;
                binding.dupFname.setError("Same Individual Selected as Duplicate");
                Toast.makeText(requireContext(), "Same Individual Selected as Duplicate", Toast.LENGTH_LONG).show();
                return;
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);


            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }

            if (finalData.numberofdup != null) {

                if (finalData.numberofdup >= 1) {

                    if (binding.uuid.getText().toString().trim().equals(binding.dupUuid.getText().toString().trim())) {
                        dup1 = true;
                        binding.dupFname.setError("Same Individual Selected as Duplicate");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate", Toast.LENGTH_LONG).show();
                        return;
                    }

                }

                if (finalData.numberofdup >= 2) {

                    if (binding.uuid.getText().toString().trim().equals(binding.dup1Uuid.getText().toString().trim())) {
                        dup1 = true;
                        binding.dup1Fname.setError("Same Individual Selected as Duplicate");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (binding.dupUuid.getText().toString().trim().equals(binding.dup1Uuid.getText().toString().trim())) {
                        dup2 = true;
                        binding.dup1Fname.setError("Same Individual Selected as Duplicate 1");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate 1", Toast.LENGTH_LONG).show();
                        return;
                    }

                }

                if (finalData.numberofdup >= 3) {

                    if (binding.uuid.getText().toString().trim().equals(binding.dup2Uuid.getText().toString().trim())) {
                        dup1 = true;
                        binding.dup2Fname.setError("Same Individual Selected as Duplicate");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (binding.dupUuid.getText().toString().trim().equals(binding.dup2Uuid.getText().toString().trim())) {
                        dup2 = true;
                        binding.dup2Fname.setError("Same Individual Selected as Duplicate 1");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate 1", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (binding.dup1Uuid.getText().toString().trim().equals(binding.dup2Uuid.getText().toString().trim())) {
                        dup3 = true;
                        binding.dup2Fname.setError("Same Individual Selected as Duplicate 2");
                        Toast.makeText(requireContext(), "Same Individual Selected as Duplicate 2", Toast.LENGTH_LONG).show();
                        return;
                    }


                }


            }

            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                if (binding.getDup().numberofdup>=1 && finalData.complete ==1) {
                    IndividualEnd endInd = new IndividualEnd();
                    endInd.endType = 4;
                    endInd.uuid = binding.getDup().dup_uuid;
                    endInd.complete = 1;

                    individualViewModel.dthupdate(endInd, result ->
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (result > 0) {
                                    Log.d("DuplicateFragment", "Dup Update successful!");
                                } else {
                                    Log.d("BaselineFragment", "Dup Update Failed!");
                                }
                            })
                    );
                }

                if (binding.getDup().numberofdup>=2 && finalData.complete ==1) {
                    IndividualEnd endInd = new IndividualEnd();
                    endInd.endType = 4;
                    endInd.uuid = binding.getDup().dup1_uuid;
                    endInd.complete = 1;
                    individualViewModel.dthupdate(endInd, result ->
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (result > 0) {
                                    Log.d("DuplicateFragment", "Dup Update successful!");
                                } else {
                                    Log.d("BaselineFragment", "Dup Update Failed!");
                                }
                            })
                    );
                }

                if (binding.getDup().numberofdup>=3 && finalData.complete ==1) {
                    IndividualEnd endInd = new IndividualEnd();
                    endInd.endType = 4;
                    endInd.uuid = binding.getDup().dup2_uuid;
                    endInd.complete = 1;
                    individualViewModel.dthupdate(endInd, result ->
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (result > 0) {
                                    Log.d("DuplicateFragment", "Dup Update successful!");
                                } else {
                                    Log.d("BaselineFragment", "Dup Update Failed!");
                                }
                            })
                    );
                }


            });

            executor.shutdown();

            viewModel.add(finalData);

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

}