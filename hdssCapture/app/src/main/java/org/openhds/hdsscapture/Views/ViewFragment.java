package org.openhds.hdsscapture.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;

import java.util.ArrayList;

public class ViewFragment extends Fragment {

    private View view;
    private SocialgroupViewModel socialgroupViewModel;
    private LocationViewModel locationViewModel;
    private DeathViewModel deathViewModel;
    private IndividualViewModel individualViewModel;
    private CompletedFormsViewModel completedFormsViewModel;
    private CompletedFormsAdapter adapter;
    private SearchView searchView;
    private View loadingIndicator;
    private TextView emptyView;
    private RecyclerView recyclerView;

    public ViewFragment() {
        // Required empty public constructor
    }

    public static ViewFragment newInstance() {
        ViewFragment fragment = new ViewFragment();
        Bundle args = new Bundle();
        // You can add uuid to args if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ViewFragment", "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("ViewFragment", "onCreateView started");

        view = inflater.inflate(R.layout.fragment_view, container, false);

        // Initialize ViewModels
        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        completedFormsViewModel = new ViewModelProvider(this).get(CompletedFormsViewModel.class);

        // Initialize views
        searchView = view.findViewById(R.id.view_search);
        recyclerView = view.findViewById(R.id.recyclerview_completed);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new CompletedFormsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        setupSearchView();
        observeCompletedForms();

        Log.d("ViewFragment", "onCreateView finished");
        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return false;
            }
        });
    }

    private void observeCompletedForms() {
        showLoading();

        completedFormsViewModel.getAllCompletedForms().observe(getViewLifecycleOwner(), completedForms -> {
            hideLoading();
            if (completedForms != null && !completedForms.isEmpty()) {
                adapter.updateData(completedForms);
                showRecyclerView();
            } else {
                showEmptyView();
            }
        });
    }

    private void showLoading() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private void showRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }
}
