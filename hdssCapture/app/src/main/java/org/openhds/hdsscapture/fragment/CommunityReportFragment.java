package org.openhds.hdsscapture.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LocationActivity;
import org.openhds.hdsscapture.Adapter.CommunityAdapter;
import org.openhds.hdsscapture.Adapter.SearchAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.databinding.FragmentCommunityReportBinding;
import org.openhds.hdsscapture.databinding.FragmentSearchBinding;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityReportFragment extends Fragment {

    private static final String COM_ID = "COM_ID";
    private Hierarchy level5Data;
    private FragmentCommunityReportBinding binding;
    private CommunityAdapter adapter;
    private CommunityViewModel communityViewModel;

    public CommunityReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param level5Data Parameter 1.
     * @return A new instance of fragment CommunityReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityReportFragment newInstance(Hierarchy level5Data) {
        CommunityReportFragment fragment = new CommunityReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(COM_ID, level5Data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.level5Data = getArguments().getParcelable(COM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_community_report, container, false);
        binding = FragmentCommunityReportBinding.inflate(inflater, container, false);

        final Intent j = getActivity().getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final TextView com = binding.getRoot().findViewById(R.id.community);
        com.setText(level5Data.name);


        final RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView_com);
        adapter = new CommunityAdapter(this, level5Data);
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(adapter);

        //List All
        adapter.com(communityViewModel);



        final FloatingActionButton add_community= binding.getRoot().findViewById(R.id.buttonAdd);
        add_community.setOnClickListener(v -> {
            final CommunityReport communityReport = new CommunityReport();
            CommunityFragment.newInstance()
                    .show(getChildFragmentManager(), "CommunityFragment");
        });


        View view = binding.getRoot();
        return view;
    }


}