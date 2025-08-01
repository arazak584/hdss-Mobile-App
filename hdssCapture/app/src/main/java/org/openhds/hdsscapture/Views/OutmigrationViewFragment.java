package org.openhds.hdsscapture.Views;

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

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.OutmigrationRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.databinding.FragmentOutmigrationBinding;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
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
 * Use the {@link OutmigrationViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutmigrationViewFragment extends DialogFragment {

    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "OMG.TAG";
    private FragmentOutmigrationBinding binding;
    private Outmigration outmigration;
    private OutmigrationRepository repository;
    public OutmigrationViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OutmigrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutmigrationViewFragment newInstance(String uuid) {
        OutmigrationViewFragment fragment = new OutmigrationViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new OutmigrationRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.outmigration = new Outmigration();  // Initialize placeholder
            this.outmigration.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOutmigrationBinding.inflate(inflater, container, false);

        //CHOOSING THE DATE
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (bundle.containsKey((OutmigrationViewFragment.DATE_BUNDLES.DATE.getBundleKey()))) {
                final String result = bundle.getString(OutmigrationViewFragment.DATE_BUNDLES.DATE.getBundleKey());
                binding.omgDate.setText(result);
            }
        });

        binding.buttonOmgImgDate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.omgDate.getText())) {
                // If Date is not empty, parse the date and use it as the initial date
                String currentDate = binding.omgDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = sdf.parse(currentDate);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    // Create DatePickerFragment with the parsed date
                    DialogFragment newFragment = new DatePickerFragment(OutmigrationViewFragment.DATE_BUNDLES.DATE.getBundleKey(), selectedDate);
                    newFragment.show(requireActivity().getSupportFragmentManager(), TAG);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                final Calendar c = Calendar.getInstance();
                DialogFragment newFragment = new DatePickerFragment(OutmigrationViewFragment.DATE_BUNDLES.DATE.getBundleKey(), c);
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

        ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        OutmigrationViewModel viewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        viewModel.getView(outmigration.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setOutmigration(data);

            try {
                Residency dataRes = resModel.updateres(data.uuid);
                if (dataRes != null) {
                    data.startDate = dataRes.startDate;
                    data.residency_uuid = dataRes.uuid;

                } } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }

        });

        loadCodeData(binding.reasonOut,  "reasonForOutMigration");
        loadCodeData(binding.destination,  "whereoutside");

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

    private void save(boolean save, boolean close, OutmigrationViewModel viewModel) {

        if (save) {
            Outmigration finalData = binding.getOutmigration();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.MAINLAYOUT, validateOnComplete, false);
            if (hasErrors) {
                Toast.makeText(requireContext(), "All fields are Required", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (!binding.earliest.getText().toString().trim().isEmpty() && !binding.omgDate.getText().toString().trim().isEmpty()
                        && !binding.startdate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.earliest.getText().toString().trim());
                    Date edate = f.parse(binding.omgDate.getText().toString().trim());
                    Date start = f.parse(binding.startdate.getText().toString().trim());
                    Date currentDate = new Date();
                    if (edate.before(stdate)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Less than Earliest Event Date");
                        Toast.makeText(getActivity(), "Date of Outmigration  Cannot Be Less than Earliest Event Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.before(start)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "Date of Outmigration Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (edate.after(currentDate)) {
                        binding.omgDate.setError("Date of Outmigration Cannot Be Future Date");
                        Toast.makeText(getActivity(), "Date of Outmigration Cannot Be Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // clear error if validation passes
                    binding.omgDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            finalData.complete = 1;

            ResidencyViewModel resModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                //1==No
                //End Residency In residency entity
                try {
                    Residency data = resModel.updateres(binding.getOutmigration().residency_uuid);
                    if (data != null) {
                        ResidencyAmendment residencyAmendment = new ResidencyAmendment();
                        residencyAmendment.endType = 2;
                        residencyAmendment.endDate = binding.getOutmigration().recordedDate;
                        residencyAmendment.uuid = binding.getOutmigration().residency_uuid;
                        residencyAmendment.complete = 1;

                        resModel.update(residencyAmendment, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("OmgFragment", "Residency Update successful!");
                                    } else {
                                        Log.d("OmgFragment", "Residency Update Failed!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("OmgFragment", "Error in update", e);
                    e.printStackTrace();
                }

                //End Residency In individual entity
                try {
                    Individual data = individualViewModel.find(binding.getOutmigration().individual_uuid);
                    if (data != null) {
                        IndividualEnd endInd = new IndividualEnd();
                        endInd.endType = 2;
                        endInd.uuid = binding.getOutmigration().individual_uuid;
                        endInd.complete = 1;

                        individualViewModel.dthupdate(endInd, result ->
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (result > 0) {
                                        Log.d("OmgFragment", "Death Update successful!");
                                    } else {
                                        Log.d("OmgFragment", "Death Update Failed!");
                                    }
                                })
                        );
                    }

                } catch (Exception e) {
                    Log.e("OmgFragment", "Error in update", e);
                    e.printStackTrace();
                }




            });

            executor.shutdown();

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