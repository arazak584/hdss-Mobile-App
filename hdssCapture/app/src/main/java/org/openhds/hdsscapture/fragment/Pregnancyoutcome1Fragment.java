package org.openhds.hdsscapture.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.Dialog.FatherOutcomeDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Handler;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutcomeBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
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
 * Use the {@link Pregnancyoutcome1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Pregnancyoutcome1Fragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String PREGNANCY_ID = "PREGNANCY_ID";
    private final String TAG = "OUTCOME.TAG";

    private Locations locations;
    private Socialgroup socialgroup;
    private Individual individual;
    private FragmentOutcomeBinding binding;
    private Pregnancyoutcome pregnancyoutcome;
    private ProgressDialog progressDialog;
    private EventForm eventForm;

    public Pregnancyoutcome1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations Parameter 1.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment PregnancyoutcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Pregnancyoutcome1Fragment newInstance(Individual individual, Locations locations, Socialgroup socialgroup) {
        Pregnancyoutcome1Fragment fragment = new Pregnancyoutcome1Fragment();
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
        binding = FragmentOutcomeBinding.inflate(inflater, container, false);
        binding.setPregoutcome(pregnancyoutcome);

        final TextView ind = binding.getRoot().findViewById(R.id.ind);
        ind.setText(HouseMembersFragment.selectedIndividual.firstName + " " + HouseMembersFragment.selectedIndividual.lastName);

        final TextView title = binding.getRoot().findViewById(R.id.preg);
        title.setText("Pregnancy Outcome 2");

        final TextView ex = binding.getRoot().findViewById(R.id.exts);
        final Spinner extra = binding.getRoot().findViewById(R.id.extras);

        ex.setVisibility(View.GONE);
        extra.setVisibility(View.GONE);

        final Intent intent = getActivity().getIntent();
        final Round roundData = intent.getParcelableExtra(HierarchyActivity.ROUND_DATA);

        Button showDialogButtons = binding.getRoot().findViewById(R.id.button_outcome_father);

        // Set a click listener on the button for mother
        showDialogButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading Fathers...");
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
                FatherOutcomeDialogFragment.newInstance(individual, locations,socialgroup)
                        .show(getChildFragmentManager(), "FatherOutcomeDialogFragment");
            }
        });

//        // Find the button view
//        Button showDialogButton = binding.getRoot().findViewById(R.id.button_out1_uuid);
//        Button showDialogButton1 = binding.getRoot().findViewById(R.id.button_out2_uuid);
//        Button showDialogButton2 = binding.getRoot().findViewById(R.id.button_out3_uuid);
//        Button showDialogButton3 = binding.getRoot().findViewById(R.id.button_out4_uuid);
//
//        // Set a click listener on the button for mother
//        showDialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show the dialog fragment
//                ChildDialogFragment.newInstance(individual, residency, locations,socialgroup)
//                        .show(getChildFragmentManager(), "ChildDialogFragment");
//            }
//        });
//
//
//        // Set a click listener on the button for mother
//        showDialogButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show the dialog fragment
//                ChildDialogFragment.newInstance(individual, residency, locations,socialgroup)
//                        .show(getChildFragmentManager(), "ChildDialogFragment");
//            }
//        });
//
//        showDialogButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show the dialog fragment
//                ChildDialogFragment.newInstance(individual, residency, locations,socialgroup)
//                        .show(getChildFragmentManager(), "ChildDialogFragment");
//            }
//        });
//
//        showDialogButton3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show the dialog fragment
//                ChildDialogFragment.newInstance(individual, residency, locations,socialgroup)
//                        .show(getChildFragmentManager(), "ChildDialogFragment");
//            }
//        });


        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle.containsKey((DATE_BUNDLES.RECORDDATE.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.RECORDDATE.getBundleKey());
                binding.editTextOutcomeDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.CONCEPTION.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.CONCEPTION.getBundleKey());
                binding.editTextConception.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DOD.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DOD.getBundleKey());
                binding.vpm.dthDeathDate.setText(result);
            }

            if (bundle.containsKey((DATE_BUNDLES.DOB.getBundleKey()))) {
                final String result = bundle.getString(DATE_BUNDLES.DOB.getBundleKey());
                binding.vpm.dthDob.setText(result);
            }

        });

        binding.buttonOutcomeStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.RECORDDATE.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.buttonOutcomeConception.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.CONCEPTION.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.vpm.buttonDeathDod.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DOD.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        binding.vpm.buttonDeathDob.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.DOB.getBundleKey(), c);
            newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });

        final Intent i = getActivity().getIntent();
        final Fieldworker fieldworkerData = i.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level6Data = j.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        PregnancyoutcomeViewModel viewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
        IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancyoutcome data = viewModel.finds(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setPregoutcome(data);
                binding.extras.setVisibility(View.GONE);
                binding.exts.setVisibility(View.GONE);
                binding.buttonOutcomeConception.setEnabled(false);
                binding.buttonOutcomeStartDate.setEnabled(false);

                Pregnancy dts = pregnancyViewModel.out2(HouseMembersFragment.selectedIndividual.uuid);
                if (dts != null){

                    data.outcomeDate = dts.outcome_date;
                    data.conceptionDate = dts.recordedDate;
                    data.rec_anc = dts.anteNatalClinic;
                    data.month_pg = dts.first_rec;
                    data.who_anc = dts.attend_you;
                    data.num_anc = dts.anc_visits;
                    data.pregnancy_uuid = dts.uuid;
                }

            } else {
                data = new Pregnancyoutcome();

                Pregnancy dts = pregnancyViewModel.out2(HouseMembersFragment.selectedIndividual.uuid);
                if (dts != null){

                    data.outcomeDate = dts.outcome_date;
                    data.conceptionDate = dts.recordedDate;
                    data.rec_anc = dts.anteNatalClinic;
                    data.month_pg = dts.first_rec;
                    data.who_anc = dts.attend_you;
                    data.num_anc = dts.anc_visits;
                    data.pregnancy_uuid = dts.uuid;
                }
                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dta = visitViewModel.find(socialgroup.uuid);
                if (dta != null){
                    data.visit_uuid = dta.uuid;
                }
                if(data.pregnancy_uuid ==null){
                    Toast.makeText(getContext(), "Kindly Pick the Pregnancy Before you pick the Outcome", Toast.LENGTH_LONG).show();
                }

                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
//                data.complete = 1;
                data.extra = 2;
                data.id = 2;

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
                binding.numberOfLiveBirths.setVisibility(View.GONE);
                binding.extras.setVisibility(View.GONE);
                binding.exts.setVisibility(View.GONE);
                binding.buttonOutcomeConception.setEnabled(false);
                binding.buttonOutcomeStartDate.setEnabled(false);

                binding.setPregoutcome(data);
                binding.getPregoutcome().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Pregnancyoutcome datas = viewModel.findpreg(HouseMembersFragment.selectedIndividual.uuid);
            if (datas != null) {
                binding.setPreg(datas);

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD5 + 0 + roundData.roundNumber;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                data.preg_uuid = binding.getPregoutcome().getUuid();
                binding.setPregoutcome1(data);

                if (data.childuuid == null){
                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");

                    data.childuuid = uuidString;
                    data.individual_uuid = uuidString;
                }

                if (binding.getPregoutcome1().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    int sequenceNumber = 1;
                    String id = ClusterFragment.selectedLocation.compextId + String.format("%03d", sequenceNumber); // generate ID with sequence number padded with zeros
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } // check if ID already exists in ViewModel
                        sequenceNumber++; // increment sequence number if ID exists
                        id = ClusterFragment.selectedLocation.compextId + String.format("%03d", sequenceNumber); // generate new ID with updated sequence number
                    }
                    binding.getPregoutcome1().extId = id; // set the generated ID to the extId property of the Individual object
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

                data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.child_idx = AppConstants.CHILD5;

                data.vis_number = 0;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number+ roundData.getRoundNumber();
                data.complete = 1;
                data.preg_uuid = binding.getPregoutcome().getUuid();


                binding.setPregoutcome1(data);
                binding.getPregoutcome1().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                // Generate ID if extId is null
                if (binding.getPregoutcome1().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    int sequenceNumber = 1;
                    String id = ClusterFragment.selectedLocation.compextId + String.format("%03d", sequenceNumber); // generate ID with sequence number padded with zeros
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } // check if ID already exists in ViewModel
                        sequenceNumber++; // increment sequence number if ID exists
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber); // generate new ID with updated sequence number
                    }
                    binding.getPregoutcome1().extId = id; // set the generated ID to the extId property of the Individual object
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD6 + 0 + roundData.roundNumber;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                data.preg_uuid = binding.getPregoutcome().getUuid();
                binding.setPregoutcome2(data);

                if (data.childuuid == null){
                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");

                    data.childuuid = uuidString;
                    data.individual_uuid = uuidString;
                }

                if (binding.getPregoutcome2().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 1; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 1;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome2().extId = id;
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

                data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.child_idx = AppConstants.CHILD6;

                data.vis_number = 0;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number+ roundData.getRoundNumber();
                data.complete = 1;
                data.preg_uuid = binding.getPregoutcome().getUuid();

                binding.setPregoutcome2(data);
                binding.getPregoutcome2().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                // Generate ID if extId is null
                if (binding.getPregoutcome2().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 1; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 1;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome2().extId = id;
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD7 + 0 + roundData.roundNumber;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                data.preg_uuid = binding.getPregoutcome().getUuid();
                binding.setPregoutcome3(data);

                if (data.childuuid == null){
                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");

                    data.childuuid = uuidString;
                    data.individual_uuid = uuidString;
                }

                if (binding.getPregoutcome3().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 2; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 2;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome3().extId = id;
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

                data.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.child_idx = AppConstants.CHILD7;

                data.vis_number = 0;

                data.child_screen = data.mother_uuid + data.child_idx;
                data.uuid = data.child_screen+data.vis_number+ roundData.getRoundNumber();
                data.complete = 1;
                data.preg_uuid = binding.getPregoutcome().getUuid();

                binding.setPregoutcome3(data);
                binding.getPregoutcome3().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


                // Generate ID if extId is null
                if (binding.getPregoutcome3().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 2; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 2;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome3().extId = id;
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            final String child_id = HouseMembersFragment.selectedIndividual.uuid + AppConstants.CHILD8 + 0 + roundData.roundNumber;
            Outcome data = outcomeViewModel.find(child_id);
            if (data != null) {
                data.preg_uuid = binding.getPregoutcome().getUuid();
                binding.setPregoutcome4(data);

                if (data.childuuid == null){
                    String uuid = UUID.randomUUID().toString();
                    String uuidString = uuid.replaceAll("-", "");

                    data.childuuid = uuidString;
                    data.individual_uuid = uuidString;
                }

                if (binding.getPregoutcome4().extId == null) {
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 3; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 3;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome4().extId = id;
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
                    final IndividualViewModel individualViewModels = new ViewModelProvider(this).get(IndividualViewModel.class);
                    String pregoutcome1Id = binding.getPregoutcome1().extId;
                    int sequenceNumber = Integer.parseInt(pregoutcome1Id.substring(pregoutcome1Id.length() - 3));
                    sequenceNumber += 3; // Increment the sequence number by 2

                    String id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    while (true) {
                        try {
                            if (individualViewModels.findAll(id) == null) break;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sequenceNumber += 3;
                        id = ClusterFragment.selectedLocation.compextId  + String.format("%03d", sequenceNumber);
                    }
                    binding.getPregoutcome4().extId = id;
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Vpm data = vpmViewModel.find(HouseMembersFragment.selectedIndividual.uuid);
            if (data != null) {
                binding.setDeath(data);
            } else {
                data = new Vpm();

                VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
                Visit dts = visitViewModel.find(socialgroup.uuid);
                if (dts != null){
                    data.visit_uuid = dts.uuid;
                }

                String uuid = UUID.randomUUID().toString();
                String uuidString = uuid.replaceAll("-", "");
                data.fw_uuid = fieldworkerData.getFw_uuid();
                data.uuid = uuidString;
                data.insertDate = new Date();
                data.firstName = "Still";
                data.lastName = "Birth";
                data.gender = 3;
                data.compno = ClusterFragment.selectedLocation.getCompno();
                data.extId = "ST-"+ HouseMembersFragment.selectedIndividual.getExtId();
                data.compname = ClusterFragment.selectedLocation.getLocationName();
                data.individual_uuid = HouseMembersFragment.selectedIndividual.getUuid();
                data.villname = level6Data.getName();
                data.villcode = level6Data.getExtId();
                data.respondent = HouseMembersFragment.selectedIndividual.getFirstName() +" "+ HouseMembersFragment.selectedIndividual.getLastName();
                data.househead = HouseMembersFragment.selectedIndividual.getFirstName() +" "+ HouseMembersFragment.selectedIndividual.getLastName();
                data.deathCause = 77;
                data.complete=1;

                binding.setDeath(data);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        ConfigViewModel configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;

        try {
            configsettings = configViewModel.findAll();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = configsettings != null && !configsettings.isEmpty() ? configsettings.get(0).earliestDate : null;
            AppCompatEditText editText = binding.getRoot().findViewById(R.id.earliest);
            if (dt != null) {
                // Format the Date to a String in "yyyy-MM-dd" format
                String formattedDate = dateFormat.format(dt);
                //System.out.println("EARLIEST-DATE: " + formattedDate);
                editText.setText(formattedDate);
            } else {
                System.out.println("EARLIEST-DATE: null");
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        final CodeBookViewModel codeBookViewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        loadCodeData(binding.childFetus1.out1Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus2.out2Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus3.out3Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus4.out4Type, codeBookViewModel, "outcometype");
        loadCodeData(binding.childFetus1.gender, codeBookViewModel, "gender");
        loadCodeData(binding.childFetus1.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.childFetus2.gender, codeBookViewModel, "gender");
        loadCodeData(binding.childFetus2.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.childFetus3.gender, codeBookViewModel, "gender");
        loadCodeData(binding.childFetus3.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.childFetus4.gender, codeBookViewModel, "gender");
        loadCodeData(binding.childFetus4.rltnHead, codeBookViewModel,  "rltnhead");
        loadCodeData(binding.birthplace, codeBookViewModel, "birthPlace");
        loadCodeData(binding.notDel, codeBookViewModel, "notdel");
        loadCodeData(binding.whyNoAnc, codeBookViewModel, "notdel");
        loadCodeData(binding.firstNb, codeBookViewModel, "complete");
        loadCodeData(binding.recAnc, codeBookViewModel, "yn_anc");
        loadCodeData(binding.recIpt, codeBookViewModel, "complete");
        loadCodeData(binding.assDel, codeBookViewModel, "assist");
        loadCodeData(binding.howDel, codeBookViewModel, "howdel");
        loadCodeData(binding.whereAnc, codeBookViewModel, "birthPlace");
        loadCodeData(binding.whoAnc, codeBookViewModel, "assist");
        loadCodeData(binding.individualComplete, codeBookViewModel, "submit");
        loadCodeData(binding.vpm.dthDeathPlace, codeBookViewModel, "deathPlace");
        loadCodeData(binding.vpm.dthDeathCause, codeBookViewModel, "deathCause");
        loadCodeData(binding.father, codeBookViewModel, "complete");
        loadCodeData(binding.childFetus1.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.childFetus1.chdSize, codeBookViewModel, "size");
        loadCodeData(binding.childFetus2.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.childFetus2.chdSize, codeBookViewModel, "size");
        loadCodeData(binding.childFetus3.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.childFetus3.chdSize, codeBookViewModel, "size");
        loadCodeData(binding.childFetus4.chdWeight, codeBookViewModel, "complete");
        loadCodeData(binding.childFetus4.chdSize, codeBookViewModel, "size");


        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel, outcomeViewModel,vpmViewModel,individualViewModel,residencyViewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel, outcomeViewModel,vpmViewModel,individualViewModel,residencyViewModel);
        });

//        binding.setEventname(eventForm.event_name);
        Handler.colorLayouts(requireContext(), binding.OUTCOMELAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, PregnancyoutcomeViewModel viewModel, OutcomeViewModel outcomeViewModel,VpmViewModel vpmViewModel,IndividualViewModel individualViewModel,ResidencyViewModel residencyViewModel) {

        if (save) {
            Pregnancyoutcome finalData = binding.getPregoutcome();

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.editTextConception.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.editTextConception.setError("Conception Date Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Conception Date Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextConception.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.editTextConception.getText().toString().trim());
                    Date edate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    if (edate.after(currentDate)) {
                        binding.editTextOutcomeDate.setError("Date of Delivery Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date of Delivery Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.before(stdate)) {
                        binding.editTextConception.setError("Delivery Date Cannot Be Less than Conception Date");
                        Toast.makeText(getActivity(), "Delivery Date Cannot Be Less than Conception Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextConception.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            boolean total = false;
            if (finalData.first_nb!=null && finalData.first_nb == 2 && !binding.lBirth.getText().toString().trim().isEmpty()) {
                int totalbirth = Integer.parseInt(binding.lBirth.getText().toString().trim());
                if (totalbirth < 1 || totalbirth > 15) {
                    total = true;
                    binding.lBirth.setError("Cannot be less than 1");
                    Toast.makeText(getActivity(), "Previous Live Births Cannot be less than 1", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean month = false;
            if (finalData.rec_anc == 1 && !binding.monthPg.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.monthPg.getText().toString().trim());
                if (totalmth < 1 || totalmth > 12) {
                    month = true;
                    binding.monthPg.setError("Months Pregnant Before ANC Cannot be More than 12");
                    Toast.makeText(getActivity(), "Months Pregnant Before ANC Cannot be More than 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean anc = false;
            if (finalData.rec_anc == 1 && !binding.numAnc.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.numAnc.getText().toString().trim());
                if (totalmth < 1 || totalmth > 15) {
                    anc = true;
                    binding.numAnc.setError("Maximum Number of ANC Visit is 15");
                    Toast.makeText(getActivity(), "Maximum Number of ANC Visit is 15", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean ipt = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
                if (totalmth < 1 || totalmth > 12) {
                    ipt = true;
                    binding.firstRec.setError("Months Pregnant for IPT Cannot be More than 12");
                    Toast.makeText(getActivity(), "Months Pregnant for IPT Cannot be More than 12", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean iptt = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.manyIpt.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.manyIpt.getText().toString().trim());
                if (totalmth < 1 || totalmth > 7) {
                    iptt = true;
                    binding.manyIpt.setError("Number of IPT taken Cannot be More than 7");
                    Toast.makeText(getActivity(), "Number of IPT taken Cannot be More than 7", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean iptm = false;
            if (finalData.rec_anc == 1 && finalData.rec_ipt == 1 && !binding.firstRec.getText().toString().trim().isEmpty()) {
                int totalmth = Integer.parseInt(binding.firstRec.getText().toString().trim());
                if (totalmth < 4) {
                    iptm = true;
                    binding.firstRec.setError("IPT is given at 16 weeks (4 Months)");
                    Toast.makeText(getActivity(), "IPT is given at 16 weeks (4 Months)", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            boolean nurse = false;
            if (finalData.ass_del != 1 && finalData.how_del == 2) {
                nurse = true;
                Toast.makeText(getActivity(), "Only Doctors Perform Caesarian Section", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (!binding.lastPreg.getText().toString().trim().isEmpty() && !binding.editTextOutcomeDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.lastPreg.getText().toString().trim());
                    Date edate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                    String formattedDate = f.format(stdate);
                    if (edate.before(stdate)) {
                        binding.editTextOutcomeDate.setError("Outcome with a later Date exist " + formattedDate);
                        Toast.makeText(getActivity(), "Outcome with a later Date exist " + formattedDate, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.editTextOutcomeDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.OUTCOMELAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            int count = 0; // Initialize a count variable to keep track of the number of occurrences where 'type' is 1

            if (binding.getPregoutcome1().type !=null && binding.getPregoutcome1().type == 1) {
                count++;
            }

            if (binding.getPregoutcome2().type !=null && binding.getPregoutcome2().type == 1) {
                count++;
            }

            if (binding.getPregoutcome3().type !=null && binding.getPregoutcome3().type == 1) {
                count++;
            }

            if (binding.getPregoutcome4().type !=null && binding.getPregoutcome4().type == 1) {
                count++;
            }

            // Set finalData.numberOfLiveBirths based on the count value
            if (count == 4) {
                finalData.numberOfLiveBirths = 4;
            } else if (count == 3) {
                finalData.numberOfLiveBirths = 3;
            } else if (count == 2) {
                finalData.numberOfLiveBirths = 2;
            } else if (count == 1) {
                finalData.numberOfLiveBirths = 1;
            } else {
                finalData.numberOfLiveBirths = 0;
            }


            if (finalData.numberofBirths != null) {

                if (finalData.numberofBirths >= 1) {


                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus1.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome1();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.childFetus1.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.childFetus1.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 5.0) {
                            weight = true;
                            binding.childFetus1.weigHcard.setError("Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    try {
                        if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                            Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

                            Calendar startCalendar = Calendar.getInstance();
                            startCalendar.setTime(recordedDate);

                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTime(outcomeDate);

                            int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                            int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                            int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                            // Adjust the difference based on the day component
                            if (dayDiff < 0) {
                                monthDiff--;
                            }

                            // Calculate the total difference in months
                            int totalDiffMonths = yearDiff * 12 + monthDiff;

                            if (totalDiffMonths <= 5 && inf.type==1 || inf.type==2) {
                                Toast.makeText(getActivity(), "Outcome cannot be Live Birth or Still Birth for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }else if (totalDiffMonths > 5 && inf.type==3){
                                Toast.makeText(getActivity(), "Outcome cannot be Miscarriage for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    inf.complete = 1;
                    if (inf.type != 1) {
                        inf.childuuid = null;
                        inf.extId = null;
                    }
                    outcomeViewModel.add(inf);

                    if (binding.getPregoutcome1().type==1){
                        Individual ind = new Individual();
                        Outcome prg = binding.getPregoutcome1();
                        ind.uuid= prg.individual_uuid;
                        ind.gender = prg.gender;
                        ind.mother_uuid= prg.mother_uuid;
                        ind.father_uuid= finalData.father_uuid;
                        ind.firstName= prg.firstName;
                        ind.lastName= prg.lastName;
                        ind.extId= prg.extId;
                        ind.insertDate=prg.insertDate;
                        ind.fw_uuid= finalData.fw_uuid;
                        ind.dob = finalData.outcomeDate;
                        ind.complete = 1;
                        ind.dobAspect = 1;
                        ind.hohID = socialgroup.extId;
                        ind.compno = ClusterFragment.selectedLocation.compno;
                        ind.socialgroup = socialgroup.uuid;
                        ind.startDate = finalData.outcomeDate;
                        ind.endType = 1;
                        ind.residency = binding.getPregoutcome1().residency_uuid;

                        individualViewModel.add(ind);

                    }

                    if (binding.getPregoutcome1().type==1){
                        Residency res = new Residency();
                        Outcome prg = binding.getPregoutcome1();
                        res.uuid= prg.residency_uuid;
                        res.individual_uuid = prg.individual_uuid;
                        res.startDate= finalData.outcomeDate;
                        res.endType= 1;
                        res.startType= 2;
                        res.insertDate= prg.insertDate;
                        res.location_uuid= ClusterFragment.selectedLocation.getUuid() ;
                        res.socialgroup_uuid = socialgroup.uuid;
                        res.fw_uuid= finalData.fw_uuid;
                        res.rltn_head = prg.rltn_head;
                        res.complete = 1;

                        residencyViewModel.add(res);
                    }

                }

                if (finalData.numberofBirths >= 2) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus2.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome2();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.childFetus2.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.childFetus2.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 5.0) {
                            weight = true;
                            binding.childFetus2.weigHcard.setError("Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    try {
                        if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                            Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

                            Calendar startCalendar = Calendar.getInstance();
                            startCalendar.setTime(recordedDate);

                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTime(outcomeDate);

                            int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                            int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                            int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                            // Adjust the difference based on the day component
                            if (dayDiff < 0) {
                                monthDiff--;
                            }

                            // Calculate the total difference in months
                            int totalDiffMonths = yearDiff * 12 + monthDiff;

                            if (totalDiffMonths <= 5 && inf.type==1 || inf.type==2) {
                                Toast.makeText(getActivity(), "Outcome cannot be Live Birth or Still Birth for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }else if (totalDiffMonths > 5 && inf.type==3){
                                Toast.makeText(getActivity(), "Outcome cannot be Miscarriage for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    inf.complete = 1;
                    if (inf.type != 1) {
                        inf.childuuid = null;
                        inf.extId = null;
                    }
                    outcomeViewModel.add(inf);

                    if (binding.getPregoutcome2().type==1){
                        Individual ind = new Individual();
                        Outcome prg = binding.getPregoutcome2();
                        ind.uuid= prg.individual_uuid;
                        ind.gender = prg.gender;
                        ind.mother_uuid= prg.mother_uuid;
                        ind.father_uuid= finalData.father_uuid;
                        ind.firstName= prg.firstName;
                        ind.lastName= prg.lastName;
                        ind.extId= prg.extId;
                        ind.insertDate=prg.insertDate;
                        ind.fw_uuid= finalData.fw_uuid;
                        ind.dob = finalData.outcomeDate;
                        ind.complete = 1;
                        ind.dobAspect = 1;
                        ind.hohID = socialgroup.extId;
                        ind.compno = ClusterFragment.selectedLocation.compno;
                        ind.socialgroup = socialgroup.uuid;
                        ind.startDate = finalData.outcomeDate;
                        ind.endType = 1;
                        ind.residency = binding.getPregoutcome2().residency_uuid;

                        individualViewModel.add(ind);

                    }

                    if (binding.getPregoutcome2().type==1){
                        Residency res = new Residency();
                        Outcome prg = binding.getPregoutcome2();
                        res.uuid= prg.residency_uuid;
                        res.individual_uuid = prg.individual_uuid;
                        res.startDate= finalData.outcomeDate;
                        res.endType= 1;
                        res.startType= 2;
                        res.insertDate= prg.insertDate;
                        res.location_uuid= ClusterFragment.selectedLocation.getUuid() ;
                        res.socialgroup_uuid = socialgroup.uuid;
                        res.fw_uuid= finalData.fw_uuid;
                        res.rltn_head = prg.rltn_head;
                        res.complete = 1;

                        residencyViewModel.add(res);
                    }

                }

                if (finalData.numberofBirths >= 3) {


                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus3.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome3();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.childFetus3.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.childFetus3.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 5.0) {
                            weight = true;
                            binding.childFetus3.weigHcard.setError("Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    try {
                        if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                            Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

                            Calendar startCalendar = Calendar.getInstance();
                            startCalendar.setTime(recordedDate);

                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTime(outcomeDate);

                            int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                            int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                            int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                            // Adjust the difference based on the day component
                            if (dayDiff < 0) {
                                monthDiff--;
                            }

                            // Calculate the total difference in months
                            int totalDiffMonths = yearDiff * 12 + monthDiff;

                            if (totalDiffMonths <= 5 && inf.type==1 || inf.type==2) {
                                Toast.makeText(getActivity(), "Outcome cannot be Live Birth or Still Birth for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }else if (totalDiffMonths > 5 && inf.type==3){
                                Toast.makeText(getActivity(), "Outcome cannot be Miscarriage for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    inf.complete = 1;
                    if (inf.type != 1) {
                        inf.childuuid = null;
                        inf.extId = null;
                    }
                    outcomeViewModel.add(inf);

                    if (binding.getPregoutcome3().type==1){
                        Individual ind = new Individual();
                        Outcome prg = binding.getPregoutcome3();
                        ind.uuid= prg.individual_uuid;
                        ind.gender = prg.gender;
                        ind.mother_uuid= prg.mother_uuid;
                        ind.father_uuid= finalData.father_uuid;
                        ind.firstName= prg.firstName;
                        ind.lastName= prg.lastName;
                        ind.extId= prg.extId;
                        ind.insertDate=prg.insertDate;
                        ind.fw_uuid= finalData.fw_uuid;
                        ind.dob = finalData.outcomeDate;
                        ind.complete = 1;
                        ind.dobAspect = 1;
                        ind.hohID = socialgroup.extId;
                        ind.compno = ClusterFragment.selectedLocation.compno;
                        ind.socialgroup = socialgroup.uuid;
                        ind.startDate = finalData.outcomeDate;
                        ind.endType = 1;
                        ind.residency = binding.getPregoutcome3().residency_uuid;

                        individualViewModel.add(ind);

                    }

                    if (binding.getPregoutcome3().type==1){
                        Residency res = new Residency();
                        Outcome prg = binding.getPregoutcome3();
                        res.uuid= prg.residency_uuid;
                        res.individual_uuid = prg.individual_uuid;
                        res.startDate= finalData.outcomeDate;
                        res.endType= 1;
                        res.startType= 2;
                        res.insertDate= prg.insertDate;
                        res.location_uuid= ClusterFragment.selectedLocation.getUuid() ;
                        res.socialgroup_uuid = socialgroup.uuid;
                        res.fw_uuid= finalData.fw_uuid;
                        res.rltn_head = prg.rltn_head;
                        res.complete = 1;

                        residencyViewModel.add(res);
                    }

                }

                if (finalData.numberofBirths >= 4) {

                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.childFetus4.OUTCOMELAYOUT, validateOnComplete, false);

                    final Outcome inf = binding.getPregoutcome4();

                    boolean weight = false;
                    if (inf.chd_weight!=null && inf.chd_weight == 1 && !binding.childFetus4.weigHcard.getText().toString().trim().isEmpty()) {
                        double childWeight = Double.parseDouble(binding.childFetus4.weigHcard.getText().toString().trim());
                        if (childWeight < 1.0 || childWeight > 5.0) {
                            weight = true;
                            binding.childFetus4.weigHcard.setError("Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0");
                            Toast.makeText(getContext(), "Child Weight Cannot be More than 5.0 Kilograms or Less than 1.0", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    try {
                        if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                            Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

                            Calendar startCalendar = Calendar.getInstance();
                            startCalendar.setTime(recordedDate);

                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTime(outcomeDate);

                            int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                            int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                            int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                            // Adjust the difference based on the day component
                            if (dayDiff < 0) {
                                monthDiff--;
                            }

                            // Calculate the total difference in months
                            int totalDiffMonths = yearDiff * 12 + monthDiff;

                            if (totalDiffMonths <= 5 && inf.type==1 || inf.type==2) {
                                Toast.makeText(getActivity(), "Outcome cannot be Live Birth or Still Birth for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }else if (totalDiffMonths > 5 && inf.type==3){
                                Toast.makeText(getActivity(), "Outcome cannot be Miscarriage for " + totalDiffMonths + " Months Pregnancy", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                    inf.complete = 1;
                    if (inf.type != 1) {
                        inf.childuuid = null;
                        inf.extId = null;
                    }
                    outcomeViewModel.add(inf);

                    if (binding.getPregoutcome4().type==1){
                        Individual ind = new Individual();
                        Outcome prg = binding.getPregoutcome4();
                        ind.uuid= prg.individual_uuid;
                        ind.gender = prg.gender;
                        ind.mother_uuid= prg.mother_uuid;
                        ind.father_uuid= finalData.father_uuid;
                        ind.firstName= prg.firstName;
                        ind.lastName= prg.lastName;
                        ind.extId= prg.extId;
                        ind.insertDate=prg.insertDate;
                        ind.fw_uuid= finalData.fw_uuid;
                        ind.dob = finalData.outcomeDate;
                        ind.complete = 1;
                        ind.dobAspect = 1;
                        ind.hohID = socialgroup.extId;
                        ind.compno = ClusterFragment.selectedLocation.compno;
                        ind.socialgroup = socialgroup.uuid;
                        ind.startDate = finalData.outcomeDate;
                        ind.endType = 1;
                        ind.residency = binding.getPregoutcome4().residency_uuid;

                        individualViewModel.add(ind);

                    }

                    if (binding.getPregoutcome4().type==1){
                        Residency res = new Residency();
                        Outcome prg = binding.getPregoutcome4();
                        res.uuid= prg.residency_uuid;
                        res.individual_uuid = prg.individual_uuid;
                        res.startDate= finalData.outcomeDate;
                        res.endType= 1;
                        res.startType= 2;
                        res.insertDate= prg.insertDate;
                        res.location_uuid= ClusterFragment.selectedLocation.getUuid();
                        res.socialgroup_uuid = socialgroup.uuid;
                        res.fw_uuid= finalData.fw_uuid;
                        res.rltn_head = prg.rltn_head;
                        res.complete = 1;

                        residencyViewModel.add(res);
                    }

                }

                if (!binding.childFetus1.individual1FirstName.getText().toString().trim().isEmpty()) {
                    boolean val = false;
                    String firstName = binding.childFetus1.individual1FirstName.getText().toString();
                    if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                        binding.childFetus1.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    } else {
                        binding.childFetus1.individual1FirstName.setError(null); // Clear the error if the input is valid
                    }

                    boolean vals = false;
                    String lastName = binding.childFetus1.individual1LastName.getText().toString();
                    if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                        binding.childFetus1.individual1LastName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        vals = true;
                        return;
                    } else {
                        binding.childFetus1.individual1LastName.setError(null); // Clear the error if the input is valid
                    }
                }

                if (!binding.childFetus2.individual1FirstName.getText().toString().trim().isEmpty()) {
                    boolean val = false;
                    String firstName = binding.childFetus2.individual1FirstName.getText().toString();
                    if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                        binding.childFetus2.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    } else {
                        binding.childFetus2.individual1FirstName.setError(null); // Clear the error if the input is valid
                    }

                    boolean vals = false;
                    String lastName = binding.childFetus2.individual1LastName.getText().toString();
                    if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                        binding.childFetus2.individual1LastName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        vals = true;
                        return;
                    } else {
                        binding.childFetus2.individual1LastName.setError(null); // Clear the error if the input is valid
                    }
                }

                if (!binding.childFetus3.individual1FirstName.getText().toString().trim().isEmpty()) {
                    boolean val = false;
                    String firstName = binding.childFetus3.individual1FirstName.getText().toString();
                    if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                        binding.childFetus3.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    } else {
                        binding.childFetus3.individual1FirstName.setError(null); // Clear the error if the input is valid
                    }

                    boolean vals = false;
                    String lastName = binding.childFetus3.individual1LastName.getText().toString();
                    if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                        binding.childFetus3.individual1LastName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        vals = true;
                        return;
                    } else {
                        binding.childFetus3.individual1LastName.setError(null); // Clear the error if the input is valid
                    }
                }

                if (!binding.childFetus4.individual1FirstName.getText().toString().trim().isEmpty()) {
                    boolean val = false;
                    String firstName = binding.childFetus4.individual1FirstName.getText().toString();
                    if (firstName.charAt(0) == ' ' || firstName.charAt(firstName.length() - 1) == ' ') {
                        binding.childFetus4.individual1FirstName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        val = true;
                        return;
                    } else {
                        binding.childFetus4.individual1FirstName.setError(null); // Clear the error if the input is valid
                    }

                    boolean vals = false;
                    String lastName = binding.childFetus4.individual1LastName.getText().toString();
                    if (lastName.charAt(0) == ' ' || lastName.charAt(lastName.length() - 1) == ' ') {
                        binding.childFetus4.individual1LastName.setError("Spaces are not allowed before or after the Name");
                        Toast.makeText(getContext(), "Spaces are not allowed before or after the Name", Toast.LENGTH_LONG).show();
                        vals = true;
                        return;
                    } else {
                        binding.childFetus4.individual1LastName.setError(null); // Clear the error if the input is valid
                    }
                }

                try {
                    if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.editTextConception.getText().toString().trim().isEmpty()) {
                        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date outcomeDate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                        Date recordedDate = f.parse(binding.editTextConception.getText().toString().trim());

                        Calendar startCalendar = Calendar.getInstance();
                        startCalendar.setTime(recordedDate);

                        Calendar endCalendar = Calendar.getInstance();
                        endCalendar.setTime(outcomeDate);

                        int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                        int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                        int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                        // Adjust the difference based on the day component
                        if (dayDiff < 0) {
                            monthDiff--;
                        }

                        // Calculate the total difference in months
                        int totalDiffMonths = yearDiff * 12 + monthDiff;

                        if (totalDiffMonths < 1 || totalDiffMonths > 12) {
                            binding.editTextConception.setError("The difference between outcome and conception Date should be between 1 and 12 months");
                            Toast.makeText(getActivity(), "The difference between outcome and conception Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Clear error if validation passes
                        binding.editTextConception.setError(null);
                    }
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                if (finalData.stillbirth == 1) {

                    try {
                        if (!binding.editTextOutcomeDate.getText().toString().trim().isEmpty() && !binding.vpm.dthDob.getText().toString().trim().isEmpty()
                                && !binding.vpm.dthDeathDate.getText().toString().trim().isEmpty()) {
                            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date stdate = f.parse(binding.editTextOutcomeDate.getText().toString().trim());
                            Date dob = f.parse(binding.vpm.dthDob.getText().toString().trim());
                            Date edate = f.parse(binding.vpm.dthDeathDate.getText().toString().trim());
                            if (!edate.equals(stdate)) {
                                binding.vpm.dthDeathDate.setError("Date of Outcome Not Equal to Date of Birth");
                                Toast.makeText(getActivity(), "Date of Outcome Not Equal to Date of Birth", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (!dob.equals(stdate)) {
                                binding.vpm.dthDob.setError("Date of Death Not Equal to Date of Birth");
                                Toast.makeText(getActivity(), "Date of Death Not Equal to Date of Birth", Toast.LENGTH_LONG).show();
                                return;
                            }
                            // clear error if validation passes
                            binding.vpm.dthDob.setError(null);
                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                    hasErrors = hasErrors || new Handler().hasInvalidInput(binding.vpm.OUTCOMELAYOUT, validateOnComplete, false);

                    final Vpm vpm = binding.getDeath();
                    vpm.complete = 1;
                    vpmViewModel.add(vpm);

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

            if (finalData.sttime !=null && finalData.edtime==null){
                finalData.edtime = endtime;
            }
            finalData.mother_uuid = HouseMembersFragment.selectedIndividual.getUuid();
            finalData.visit_uuid = binding.getPregoutcome().visit_uuid;
            finalData.complete=1;
            viewModel.add(finalData);
            IndividualViewModel iview = new ViewModelProvider(this).get(IndividualViewModel.class);
            try {
                Individual data = iview.visited(HouseMembersFragment.selectedIndividual.uuid);
                if (data != null) {
                    IndividualVisited visited = new IndividualVisited();
                    visited.uuid = finalData.mother_uuid;
                    visited.complete = 2;
                    iview.visited(visited);
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

    private enum DATE_BUNDLES {
        CONCEPTION ("CONCEPTION"),
        DOB ("DOB"),
        DOD ("DOD"),
        RECORDDATE ("RECORDDATE");

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