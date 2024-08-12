package org.openhds.hdsscapture.odk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.databinding.FragmentOdkBinding;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OdkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OdkFragment extends Fragment implements ListAdapter.IndClickListener {

    private static final String LOC_LOCATION_IDS = "LOC_LOCATION_IDS";
    private static final String SOCIAL_ID = "SOCIAL_ID";
    private static final String INDIVIDUAL_ID = "INDIVIDUAL_ID";
    public static final int ODK_REQUEST_CODE = 1;
    private Locations locations;
    private Socialgroup socialgroup;
    private FragmentOdkBinding binding;
    private Individual individual;
    private Hierarchy level6Data;
    public static  Individual selectedInd;
    private OdkViewModel viewModel;
    private OdkFormAdapter odkFormAdapter;
    private View view;

    public interface IndClickListener {
        void onIndClick(Individual selectedInd);
    }

    public OdkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locations    Parameter 2.
     * @param socialgroup Parameter 3.
     * @param individual Parameter 4.
     * @return A new instance of fragment OdkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OdkFragment newInstance(Locations locations, Socialgroup socialgroup, Individual individual) {
        OdkFragment fragment = new OdkFragment();
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
            this.socialgroup = getArguments().getParcelable(SOCIAL_ID);
            this.locations = getArguments().getParcelable(LOC_LOCATION_IDS);
            this.individual = getArguments().getParcelable(INDIVIDUAL_ID);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(OdkViewModel.class);
        requestPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_odk, container, false);

        final Intent i = getActivity().getIntent();
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent j = getActivity().getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_indview);
        final ListAdapter adapter = new ListAdapter(this, locations, socialgroup,this );
        final IndividualViewModel individualViewModel = new ViewModelProvider(requireActivity()).get(IndividualViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //initial loading of Individuals in locations
        adapter.filters("", individualViewModel);

        //ODK FORMS
        //Household Adapter
        final RecyclerView recyclerViewOdk = view.findViewById(R.id.recyclerView_odk);
        DividerItemDecoration dividerItemDecorations = new DividerItemDecoration(recyclerViewOdk.getContext(),
                RecyclerView.VERTICAL);
        recyclerViewOdk.addItemDecoration(dividerItemDecorations);
        recyclerViewOdk.setLayoutManager(new LinearLayoutManager(view.getContext()));
        odkFormAdapter = new OdkFormAdapter(requireContext(),this, individual, socialgroup, viewModel);
        recyclerViewOdk.setAdapter(odkFormAdapter);

        // Instantiate the SocialgroupViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(OdkViewModel.class);
        checkContentProvider();

        return view;
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                checkContentProvider();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkContentProvider() {
        Uri formsUri = Uri.parse("content://org.odk.collect.android.provider.odk.forms/forms");
        Cursor cursor = getActivity().getContentResolver().query(formsUri, null, null, null, null);
        if (cursor != null) {
            Toast.makeText(getContext(), "Content Provider Accessible", Toast.LENGTH_SHORT).show();
            cursor.close();
        } else {
            Toast.makeText(getContext(), "Failed to Access Content Provider", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIndClick(Individual selectedInd) {
        OdkFragment.selectedInd = selectedInd;
        if (odkFormAdapter != null) {
            odkFormAdapter.setSelectedInd(OdkFragment.selectedInd);
            TextView inds = view.findViewById(R.id.ind_name);
            inds.setText(selectedInd.getFirstName() + " " + selectedInd.getLastName());
        }
    }
}