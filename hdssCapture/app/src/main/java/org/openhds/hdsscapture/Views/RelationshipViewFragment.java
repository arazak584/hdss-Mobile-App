package org.openhds.hdsscapture.Views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.RelationshipRepository;
import org.openhds.hdsscapture.Utilities.HandlerSelect;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.databinding.FragmentRelationshipBinding;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.Utilities.DatePickerFragment;
import org.openhds.hdsscapture.fragment.KeyboardFragment;
import org.openhds.hdsscapture.fragment.RelationshipFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelationshipViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationshipViewFragment extends KeyboardFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    private final String TAG = "RELATIONSHIP.TAG";

    private Relationship relationship;
    private RelationshipRepository repository;
    private FragmentRelationshipBinding binding;
    private ProgressDialog progressDialog;

    public RelationshipViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RelationshipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RelationshipViewFragment newInstance(String uuid) {
        RelationshipViewFragment fragment = new RelationshipViewFragment();
        Bundle args = new Bundle();
        args.putString(INDIVIDUAL_ID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new RelationshipRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            String uuid = getArguments().getString(INDIVIDUAL_ID); // Correct key
            this.relationship = new Relationship();  // Initialize placeholder
            this.relationship.uuid = uuid;        // Assign UUID to fetch from DB
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRelationshipBinding.inflate(inflater, container, false);

        // Setup keyboard hiding for all views in the layout
        setupKeyboardHiding(binding.getRoot());

        //CHOOSING THE DATE
        setupDatePickers();


        final TextView cmt = binding.getRoot().findViewById(R.id.txt_comment);
        final TextView rsv = binding.getRoot().findViewById(R.id.resolve);
        final RadioGroup rsvd = binding.getRoot().findViewById(R.id.status);
        RelationshipViewModel viewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        viewModel.getView(relationship.uuid).observe(getViewLifecycleOwner(), data -> {
            binding.setRelationship(data);

            if(data.status!=null && data.status==2){
                cmt.setVisibility(View.VISIBLE);
                rsv.setVisibility(View.VISIBLE);
                rsvd.setVisibility(View.VISIBLE);
            }else{
                cmt.setVisibility(View.GONE);
                rsv.setVisibility(View.GONE);
                rsvd.setVisibility(View.GONE);
            }

        });


        //SPINNERS
        loadCodeData(binding.relComplete,  "submit");
        loadCodeData(binding.relStarttype,  "relationshipType");
        loadCodeData(binding.relEndtype,  "relendType");
        loadCodeData(binding.mar,  "complete");
        loadCodeData(binding.polygamous,  "complete");
        loadCodeData(binding.lcow,  "complete");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });

        HandlerSelect.colorLayouts(requireContext(), binding.RELATIONSHIPLAYOUT);
        View view = binding.getRoot();
        return view;
    }

    private void save(boolean save, boolean close, RelationshipViewModel viewModel) {

        if (save) {
            Relationship finalData = binding.getRelationship();

            boolean dob = false;
            boolean val = false;
            boolean mwive = false;
            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new HandlerSelect().hasInvalidInput(binding.RELATIONSHIPLAYOUT, validateOnComplete, false);

            if (!binding.tnbch.getText().toString().trim().isEmpty() && !binding.nchdm.getText().toString().trim().isEmpty()) {
                int totalBiolChildren = Integer.parseInt(binding.tnbch.getText().toString().trim());
                int childrenFromMarriage = Integer.parseInt(binding.nchdm.getText().toString().trim());
                if (totalBiolChildren < childrenFromMarriage) {
                    val = true;
                    binding.nchdm.setError("Number of Biological Children Cannot be Less Children from this Marriage");
                    return;
                }
            }

            if (!binding.nwive.getText().toString().trim().isEmpty()) {
                int totalwive = Integer.parseInt(binding.nwive.getText().toString().trim());
                if (totalwive < 2) {
                    mwive = true;
                    binding.nwive.setError("Cannot be less than 2");
                    return;
                }
            }

            try {
                if (!binding.womanDob.getText().toString().trim().isEmpty() && !binding.relStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.relStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.womanDob.getText().toString().trim());
                    if (edate.after(stdate)) {
                        binding.relStartDate.setError("Start Date Cannot Be Less than Date of Birth");
                        Toast.makeText(getActivity(), "Start Date Cannot Be Less than Date of Birth", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (stdate.after(currentDate)) {
                        binding.relStartDate.setError("Date Cannot Be a Future Date");
                        Toast.makeText(getActivity(), "Date Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.relStartDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            try {
                if (!binding.relEndDate.getText().toString().trim().isEmpty() && !binding.relStartDate.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date stdate = f.parse(binding.relStartDate.getText().toString().trim());
                    Date edate = f.parse(binding.relEndDate.getText().toString().trim());
                    if (edate.before(stdate)) {
                        binding.relEndDate.setError("End Date Cannot Be Less than Start Date");
                        Toast.makeText(getActivity(), "End Date Cannot Be Less than Start Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.relEndDate.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            if (hasErrors) {
                Toast.makeText(requireContext(), "Some fields are Missing", Toast.LENGTH_LONG).show();
                return;
            }

            finalData.complete=1;
            viewModel.add(finalData);


            //Toast.makeText(requireActivity(), R.string.completesaved, Toast.LENGTH_LONG).show();
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
        STARTDATE ("STARTDATE"),
        ENDDATE ("ENDDATE");

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

    private void setupDatePickers() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            handleDateResult(bundle, RelationshipViewFragment.DATE_BUNDLES.STARTDATE, binding.relStartDate);
            handleDateResult(bundle, RelationshipViewFragment.DATE_BUNDLES.ENDDATE, binding.relEndDate);
        });

        binding.buttonRelStartDate.setEndIconOnClickListener(v ->
                showDatePicker(RelationshipViewFragment.DATE_BUNDLES.STARTDATE, binding.relStartDate));

        binding.buttonRelEndDate.setEndIconOnClickListener(v ->
                showDatePicker(RelationshipViewFragment.DATE_BUNDLES.ENDDATE, binding.relEndDate));

    }

    private void handleDateResult(Bundle bundle, RelationshipViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        if (bundle.containsKey(dateType.getBundleKey())) {
            String result = bundle.getString(dateType.getBundleKey());
            editText.setText(result);
        }
    }

    private void showDatePicker(RelationshipViewFragment.DATE_BUNDLES dateType, TextInputEditText editText) {
        Calendar calendar = parseCurrentDate(editText.getText().toString());
        DialogFragment datePickerFragment = new DatePickerFragment(
                dateType.getBundleKey(),
                calendar
        );
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), TAG);
    }

    private Calendar parseCurrentDate(String dateString) {
        Calendar calendar = Calendar.getInstance();

        if (!TextUtils.isEmpty(dateString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date date = sdf.parse(dateString);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }
}