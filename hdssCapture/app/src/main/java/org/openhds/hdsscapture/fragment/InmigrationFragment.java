package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.databinding.FragmentInmigrationBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.OmgUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyUpdateEndDate;
import org.openhds.hdsscapture.entity.subentity.SocialgroupAmendment;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

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
 * Use the {@link InmigrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InmigrationFragment extends Fragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private final String TAG = "IMG.TAG";
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

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (bundle.containsKey((InmigrationFragment.DATE_BUNDLES.DATE.getBundleKey()))) {
                final String result = bundle.getString(InmigrationFragment.DATE_BUNDLES.DATE.getBundleKey());
                binding.imgDate.setText(result);
            }
        });

        binding.buttonImgImgDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.imgDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.imgDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(InmigrationFragment.DATE_BUNDLES.DATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(InmigrationFragment.DATE_BUNDLES.DATE.getBundleKey(), c);
                newFragment.show(requireActivity().getSupportFragmentManager(), TAG);
            }
        });

        InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        ResidencyViewModel viewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);

        Spinner mySpinner = binding.getRoot().findViewById(R.id.migtype);
        mySpinner.setEnabled(false);

        try {
            Inmigration dataimg = inmigrationViewModel.find(HouseMembersFragment.selectedIndividual.uuid, ClusterFragment.selectedLocation.uuid);
            if (dataimg != null) {
                binding.setInmigration(dataimg);
                dataimg.location_uuid = ClusterFragment.selectedLocation.getUuid();
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

            binding.setInmigration(dataimg);
            binding.getInmigration().setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        } catch(ExecutionException | InterruptedException e) {
        e.printStackTrace(); }

        //Get End Date for the last moveout
        TextInputLayout end = binding.getRoot().findViewById(R.id.edate);
        AppCompatEditText ends = binding.getRoot().findViewById(R.id.endDate);
        try {
            Residency datas = viewModel.finds(HouseMembersFragment.selectedIndividual.uuid);
            if (datas != null) {
                binding.setRes(datas);
                end.setVisibility(View.VISIBLE);
                ends.setVisibility(View.VISIBLE);
            }else{
                end.setVisibility(View.GONE);
                ends.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Find residency_uuid for last but one record where endtype=2(OMG)
        AppCompatEditText old_res = binding.getRoot().findViewById(R.id.old_residency_uuid);
        try {
            Residency datae = viewModel.findLastButOne(HouseMembersFragment.selectedIndividual.uuid);
            if (datae != null && datae.endType==2) {
                binding.setResidency(datae);
                old_res.setVisibility(View.VISIBLE);
                Log.d("Old", "Old Residency Data: " + datae.getUuid());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Get Earliest Event Date
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
        HandlerSelect.colorLayouts(requireContext(), binding.INMIGRATIONLAYOUT);
        View view = binding.getRoot();
        return view;

    }

    private void save(boolean save, boolean close, InmigrationViewModel inmigrationViewModel) {

        if (save) {
            Inmigration finalData = binding.getInmigration();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.INMIGRATIONLAYOUT, validateOnComplete, false);
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

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.imgDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.imgDate.getText().toString().trim());
                    Date currentDate = new Date();
                    if (edate.before(stdate)) {
                        binding.imgDate.setError("Date of Inmigration Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Date of Inmigration  Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.after(currentDate)) {
                        binding.imgDate.setError("Date of Outmigration Cannot Be Future Date");
                        Toast.makeText(getActivity(), "Date of Outmigration Cannot Be Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.imgDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            //Validate the number of months the individual moved in
            try {
                String hlngStr = binding.howLng.getText().toString().trim();
                String imgDateStr = binding.imgDate.getText().toString().trim();

                if (!hlngStr.isEmpty() && !imgDateStr.isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                    Date insertDate = finalData.insertDate; // Assume this is the current or reference date
                    Date imgDate = f.parse(imgDateStr);     // Date of immigration entered by user
                    int hlng = Integer.parseInt(hlngStr);   // Number of months entered

                    // Calculate the difference in months
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(imgDate);

                    Calendar endCal = Calendar.getInstance();
                    endCal.setTime(insertDate);

                    int yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
                    int monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
                    int dayDiff = endCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH);

                    // Adjust monthDiff if day is negative
                    if (dayDiff < 0) {
                        monthDiff--;
                    }

                    int totalDiffMonths = yearDiff * 12 + monthDiff;

                    // If it doesn't match, calculate the correct immigration date
                    if (hlng != totalDiffMonths) {
                        Calendar possibleCal = Calendar.getInstance();
                        possibleCal.setTime(insertDate);
                        possibleCal.add(Calendar.MONTH, -hlng); // Go back by hlng months
                        String possibleDate = f.format(possibleCal.getTime());

                        binding.howLng.setError("Date does not match number of months. Suggested Date: " + possibleDate);
                        Toast.makeText(getActivity(), "Date does not match number of months. Suggested: " + possibleDate, Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Clear any previous errors if valid
                    binding.imgDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            try {
                if (!binding.endDate.getText().toString().trim().isEmpty() && !binding.imgDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = f.parse(f.format(new Date()));
                    Date stdate = f.parse(binding.endDate.getText().toString().trim());
                    Date edate = f.parse(binding.imgDate.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.imgDate.setError("Date of Inmigration Cannot Be Less than Last OMG Date");
                        Toast.makeText(getActivity(), "Date of Inmigration  Cannot Be Less than Last OMG Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.equals(currentDate)) {
                        String errorMessage = getString(R.string.startdateerr);
                        binding.imgDate.setError(errorMessage);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.imgDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            finalData.complete = 1;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                //Update Outmigration
                OutmigrationViewModel omgModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
                //String old_res = binding.getResidency().uuid;
                String old_res = binding.getResidency().getUuid();
                Log.d("Omg", "Old Residency ID: " + old_res);
                try {
                    Outmigration data = omgModel.findLast(old_res);
                    if (data !=null) {
                    OmgUpdate omg = new OmgUpdate();
                    omg.residency_uuid = binding.getResidency().getUuid();
                    omg.destination = binding.getInmigration().origin;
                    omg.reason = binding.getInmigration().reason;
                    omg.reason_oth = binding.getInmigration().reason_oth;
                    omg.complete = 1;
                    omg.edit = 1;
                    if (data.status != null && data.status == 2) {
                        omg.status = 3;
                    }
                    // Subtract one day from the recordedDate
                    Date recordedDate = binding.getInmigration().recordedDate;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(recordedDate);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    omg.recordedDate = calendar.getTime();

                    Log.d("Omg", "Recorded Date: " + calendar.getTime());

                        omgModel.update(omg, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("OutmigrationFragment", "Outmigration Update successful!");
                                    } else {
                                        Log.d("OutmigrationFragment", "Outmigration Update Failed!");
                                    }
                                })
                        );

                    }

            } catch (Exception e) {
                Log.e("OutmigrationFragment", "Error in update", e);
                e.printStackTrace();
            }

                //Update StartDate For Residency
                ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
                String res = finalData.getResidency_uuid();
                try {
                    Residency data = resModel.updateres(res);
                    if (data != null) {
                        ResidencyUpdate item = new ResidencyUpdate();
                        item.uuid = binding.getInmigration().residency_uuid;
                        item.startDate = binding.getInmigration().recordedDate;
                        item.complete = 1;
                        //resModel.update(item);

                        resModel.updates(item, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("ResidencyFragment", "Residency Update successful!");
                                    } else {
                                        Log.d("ResidencyFragment", "Residency Update Failed!");
                                    }
                                })
                        );

                    }
                } catch (Exception e) {
                    Log.e("ResidencyFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //Update Previous Residency
                try {
                    Residency data = resModel.updateres(old_res);
                    if (data != null) {
                        ResidencyUpdateEndDate items = new ResidencyUpdateEndDate();
                        items.uuid = binding.getResidency().getUuid();
                        items.complete = 1;
                        // Subtract one day from the recordedDate
                        Date recordedDate = binding.getInmigration().recordedDate;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(recordedDate);
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        items.endDate = calendar.getTime();
                        //resModel.update(item);
                        Log.d("Omg", "Residency End Date: " + calendar.getTime());

                        resModel.updatez(items, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("ResidencyFragment", "Old Residency Update successful!");
                                    } else {
                                        Log.d("ResidencyFragment", "Old Residency Update Failed!");
                                    }
                                })
                        );

                    }
                } catch (Exception e) {
                    Log.e("ResidencyFragment", "Error in update", e);
                    e.printStackTrace();
                }



            });
            executor.shutdown();

            inmigrationViewModel.add(finalData);

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