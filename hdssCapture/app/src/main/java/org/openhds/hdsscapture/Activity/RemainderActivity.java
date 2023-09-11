package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RemainderActivity extends AppCompatActivity {

    private Hierarchy level6Data;
    private ArrayAdapter<Hierarchy> level6Adapter;
    private List<Hierarchy> level6List = new ArrayList<>();
    public static final String LEVEL6_DATA = "org.openhds.hdsscapture.activity.RemainderActivity.LEVEL5_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);

        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);
        final Spinner level6Spinner = findViewById(R.id.spinnerRVillage);
        level6Spinner.setAdapter(level6Adapter);

        level6Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
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

        // Set listener for level 6 spinner
        level6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level6Data = level6Adapter.getItem(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ExtendedFloatingActionButton start = findViewById(R.id.btn_remain_location);
        start.setOnClickListener(v -> {

            final Intent i = new Intent(this, ListActivity.class);

            i.putExtra(LEVEL6_DATA, level6Data);
            startActivity(i);
        });

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