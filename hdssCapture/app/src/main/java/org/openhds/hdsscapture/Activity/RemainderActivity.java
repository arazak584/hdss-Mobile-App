package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Adapter.RemainderAdapter;
import org.openhds.hdsscapture.Adapter.ReportAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RemainderActivity extends AppCompatActivity {

    private Hierarchy level6Data;
    private ArrayAdapter<Hierarchy> level6Adapter;
    private ProgressDialog progress;
    private RemainderAdapter remainderAdapter;
    private final List<Hierarchy> level6List = new ArrayList<>();
    public static final String LEVEL6_DATA = "org.openhds.hdsscapture.activity.RemainderActivity.LEVEL5_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);
        //final Spinner level6Spinner = findViewById(R.id.spinnerRVillage);
        AutoCompleteTextView level6Spinner = findViewById(R.id.autoCompleteRVillage);
        level6Spinner.setAdapter(level6Adapter);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_remainder);
        final RemainderAdapter adapter = new RemainderAdapter(this, level6Data);
        final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        level6Adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        level6Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level6Spinner.setAdapter(level6Adapter);

        // Load level 1 data
        try {
            List<Hierarchy> level6Data = hierarchyViewModel.retrieveLevel7();
            level6Adapter.addAll(level6Data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }

        level6Spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                level6Data = level6Adapter.getItem(position);
            }
        });

        final ExtendedFloatingActionButton areas = findViewById(R.id.btn_areas);
        areas.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),ScheduleActivity.class);
            //i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        final ExtendedFloatingActionButton remainder = findViewById(R.id.btn_remain_location);
        remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                // Simulate long operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        AutoCompleteTextView level6Spinner = findViewById(R.id.autoCompleteRVillage);
                        String charText = level6Spinner.getText().toString();

                        // Perform search based on the selected item and search text
                        List<Locations> searchResults = null;
                        try {
                            searchResults = locationViewModel.retrieveByVillage(charText);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Pass the search results to the adapter
                        adapter.filter(charText, locationViewModel);
                        // Dismiss the progress dialog
                        progress.dismiss();
                        hideLoadingDialog(); // Dismiss the progress dialog after generating the report
                    }
                }, 500);

            }

            public void showLoadingDialog() {
                if (progress == null) {
                    progress = new ProgressDialog(RemainderActivity.this);
                    progress.setTitle("Generating Remaining Compounds...");
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


    }

    public void dismissLoadingDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.exit_confirmation_title))
//                .setMessage(getString(R.string.exiting_lbl))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        try{
//                            RemainderActivity.this.finish();
//                        }
//                        catch(Exception e){}
//                    }
//                })
//                .setNegativeButton(getString(R.string.no), null)
//                .show();
//    }

}