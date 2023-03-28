package org.openhds.hdsscapture.Activity;

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

    private Hierarchy level5Data;
    private ArrayAdapter<Hierarchy> level5Adapter;
    private List<Hierarchy> level5List = new ArrayList<>();
    public static final String LEVEL5_DATA = "org.openhds.hdsscapture.activity.RemainderActivity.LEVEL5_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);

        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);
        final Spinner level5Spinner = findViewById(R.id.spinnerRVillage);
        level5Spinner.setAdapter(level5Adapter);

        level5Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level5Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level5Spinner.setAdapter(level5Adapter);

        // Load level 1 data
        try {
            List<Hierarchy> level5Data = hierarchyViewModel.retrieveLevel7();
            level5Adapter.addAll(level5Data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }

        // Set listener for level 5 spinner
        level5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level5Data = level5Adapter.getItem(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ExtendedFloatingActionButton start = findViewById(R.id.btn_remain_location);
        start.setOnClickListener(v -> {

            final Intent i = new Intent(this, ListActivity.class);

            i.putExtra(LEVEL5_DATA, level5Data);
            startActivity(i);
        });

    }


}