package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Adapter.RemainderAdapter;
import org.openhds.hdsscapture.Adapter.ViewsAdapter;
import org.openhds.hdsscapture.Adapter.WorkAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.subqueries.Newloc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScheduleActivity extends AppCompatActivity {


    private ArrayAdapter<Fieldworker> fieldworkerAdapter;
    private Hierarchy level6Data;
    private Fieldworker fw;
    private ProgressDialog progress;
    private SearchView searchView;
    private List<Hierarchy> filterAll;
    private WorkAdapter workAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        final ListingViewModel listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        searchView = findViewById(R.id.searcharea);
        workAdapter = new WorkAdapter(this,level6Data, locationViewModel, listingViewModel);

        //Pull All Data Collectors
        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);
        //final Spinner level6Spinner = findViewById(R.id.spinnerRVillage);
        AutoCompleteTextView fwSpinner = findViewById(R.id.autoCompleteUser);
        fwSpinner.setAdapter(fieldworkerAdapter);

        fieldworkerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        fieldworkerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fwSpinner.setAdapter(fieldworkerAdapter);

        // Load level 1 data
        try {
            List<Fieldworker> fw = fieldworkerViewModel.fw();
            fieldworkerAdapter.addAll(fw);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }

        fwSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fw = fieldworkerAdapter.getItem(position);
            }
        });

        //Adapter Recycler

        final RecyclerView recyclerView = findViewById(R.id.my_recycler_area);
        final WorkAdapter adapter = new WorkAdapter(this, level6Data, locationViewModel, listingViewModel);
        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final ExtendedFloatingActionButton area = findViewById(R.id.btn_area);
        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                // Simulate long operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        AutoCompleteTextView fwSpinner = findViewById(R.id.autoCompleteUser);
                        String charText = fwSpinner.getText().toString();

                        // Perform search based on the selected item and search text
                        List<Hierarchy> searchResults = null;
                        try {
                            searchResults = hierarchyViewModel.repo(charText);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Pass the search results to the adapter
                        adapter.filter(charText, hierarchyViewModel);
                        // Dismiss the progress dialog
                        progress.dismiss();
                        hideLoadingDialog(); // Dismiss the progress dialog after generating the report
                    }
                }, 500);

            }

            public void showLoadingDialog() {
                if (progress == null) {
                    progress = new ProgressDialog(ScheduleActivity.this);
                    progress.setTitle("Generating Working Area...");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setMessage(getString(R.string.please_wait_lbl));
                    progress.setCancelable(false);
                }
                progress.show();
            }

            public void hideLoadingDialog() {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query changes here
                sFilter(newText);
                return true;
            }
        });


    }

    private void sFilter(String s) {
        ArrayList<Hierarchy> filterNames = new ArrayList<>();
        String searchQuery = s.toLowerCase(); // Convert the search query to lowercase for case-insensitive search
        for (Hierarchy hierarchy : filterAll) {
            // Convert extid, name, and error to lowercase before comparison
            if (hierarchy.name.toLowerCase().contains(searchQuery) ||
                    hierarchy.area.toLowerCase().contains(searchQuery) ||
                    hierarchy.town.toLowerCase().contains(searchQuery) ||
                    hierarchy.parent_uuid.toLowerCase().contains(searchQuery)
            ) {
                filterNames.add(hierarchy);
            }
        }
        workAdapter.setHierarchyList(filterNames); // Update the adapter with filtered data
    }
}