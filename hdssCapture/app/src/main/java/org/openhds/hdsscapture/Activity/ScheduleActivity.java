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
import java.util.concurrent.Executors;

public class ScheduleActivity extends AppCompatActivity {


    private ArrayAdapter<Fieldworker> fieldworkerAdapter;
    private Hierarchy level6Data;
    private Fieldworker fw;
    private ProgressDialog progress;
    private WorkAdapter workAdapter;
    private LocationViewModel locationViewModel;
    private ListingViewModel listingViewModel;
    private HierarchyViewModel hierarchyViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);

//        searchView = findViewById(R.id.searcharea);
        workAdapter = new WorkAdapter(this,level6Data, locationViewModel, listingViewModel);

        //Pull All Data Collectors
        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);
        //final Spinner level6Spinner = findViewById(R.id.spinnerRVillage);
        AutoCompleteTextView fwSpinner = findViewById(R.id.autoCompleteUser);

        fieldworkerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        fieldworkerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fwSpinner.setAdapter(fieldworkerAdapter);

        // Load fieldworker data
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
        workAdapter = new WorkAdapter(this, level6Data, locationViewModel, listingViewModel);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(workAdapter);

        ExtendedFloatingActionButton areaButton = findViewById(R.id.btn_area);
        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWorkingArea(); // Calls the method that now runs in the background
            }
        });


    }


    private void generateWorkingArea() {
        showProgressDialog();

        // Simulate long operation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get search text from AutoCompleteTextView
                AutoCompleteTextView fwSpinner = findViewById(R.id.autoCompleteUser);
                String searchText = fwSpinner.getText().toString();

                // Filter and update RecyclerView data
                workAdapter.filter(searchText, hierarchyViewModel);

                // Dismiss the progress dialog
                dismissProgressDialog();
            }
        }, 500);
    }

    private void showProgressDialog() {
        if (progress == null) {
            progress = new ProgressDialog(ScheduleActivity.this);
            progress.setTitle("Generating Working Area...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage(getString(R.string.please_wait_lbl));
            progress.setCancelable(false);
        }
        progress.show();
    }

    private void dismissProgressDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

}