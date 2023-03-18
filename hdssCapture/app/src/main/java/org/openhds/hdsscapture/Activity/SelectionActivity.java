package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SelectionActivity extends AppCompatActivity {

    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";
    public static final String VILLAGE_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.VILLAGE_DATA";
    public static final String CLUSTER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.CLUSTER_DATA";
    private Hierarchy hierarchyData;
    private Round round;
    private ArrayAdapter<Hierarchy> level1Adapter;
    private ArrayAdapter<Hierarchy> level2Adapter;
    private ArrayAdapter<Hierarchy> level3Adapter;
    private ArrayAdapter<Hierarchy> level4Adapter;
    private ArrayAdapter<Hierarchy> level5Adapter;
    private ArrayAdapter<Hierarchy> level6Adapter;
    private ArrayAdapter<Hierarchy> level7Adapter;

    private List<Hierarchy> level1List = new ArrayList<>();
    private List<Hierarchy> level2List = new ArrayList<>();
    private List<Hierarchy> level3List = new ArrayList<>();
    private List<Hierarchy> level4List = new ArrayList<>();
    private List<Hierarchy> level5List = new ArrayList<>();
    private List<Hierarchy> level6List = new ArrayList<>();
    private List<Hierarchy> level7List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);
        final RoundViewModel roundViewModel = new ViewModelProvider(this).get(RoundViewModel.class);
        final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);

        final Spinner level1Spinner = findViewById(R.id.spCountry);
        final Spinner level2Spinner = findViewById(R.id.spRegion);
        final Spinner level3Spinner = findViewById(R.id.spDistrict);
        final Spinner level4Spinner = findViewById(R.id.spSubdistrict);
        final Spinner level5Spinner = findViewById(R.id.spVillage);
        final Spinner level6Spinner = findViewById(R.id.spCluster);
        //final Spinner level7Spinner = findViewById(R.id.spCluster);
        final Spinner roundSpinner = findViewById(R.id.spRound);


        final EditText username = findViewById(R.id.editUsername);

        // set adapters to spinners
        level1Spinner.setAdapter(level1Adapter);
        level2Spinner.setAdapter(level2Adapter);
        level3Spinner.setAdapter(level3Adapter);
        level4Spinner.setAdapter(level4Adapter);
        level4Spinner.setAdapter(level5Adapter);
        level4Spinner.setAdapter(level6Adapter);
        level4Spinner.setAdapter(level7Adapter);

        // Initialize adapters
        level1Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level1Spinner.setAdapter(level1Adapter);

        level2Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level2Spinner.setAdapter(level2Adapter);

        level3Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level3Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level3Spinner.setAdapter(level3Adapter);

        level4Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level4Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level4Spinner.setAdapter(level4Adapter);

        level5Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level5Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level5Spinner.setAdapter(level5Adapter);

        level6Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        level6Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level6Spinner.setAdapter(level6Adapter);

        //level7Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        //level7Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //level7Spinner.setAdapter(level7Adapter);

        int ccSize = loadRoundData(roundSpinner, roundViewModel);
        if(ccSize > 1) {
            roundSpinner.setSelection(1);
        }

        roundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    final Round data = (Round) parent.getItemAtPosition(position);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Load level 1 data
        try {
            List<Hierarchy> level1Data = hierarchyViewModel.retrieveLevel1();
            level1Adapter.addAll(level1Data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }

        // Set listener for level 1 spinner
        level1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel1 = level1Adapter.getItem(position);

                // Load level 2 data
                if(position==0)
                try {
                    List<Hierarchy> level2Data = hierarchyViewModel.retrieveLevel2(selectedLevel1.getExtId());
                    level2Data.add(0,new Hierarchy("","Select Region"));
                    level2Adapter.clear();
                    level2Adapter.addAll(level2Data);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectionActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }

                // Reset level 3 spinner
                level3Adapter.clear();
                level4Adapter.clear();
                level5Adapter.clear();
                level6Adapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set listener for level 2 spinner
        level2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel2 = level2Adapter.getItem(position);
                // Load level 3 data
                try {
                    List<Hierarchy> level3Data = hierarchyViewModel.retrieveLevel3(selectedLevel2.getExtId());
                    if(position == 0){ level3Data = null;} else{
                    level3Data.add(0,new Hierarchy("","Select District"));
                    level3Adapter.clear();
                    level3Adapter.addAll(level3Data);}
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectionActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }

                // Reset level 3 spinner
                level4Adapter.clear();
                level5Adapter.clear();
                level6Adapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Set listener for level 3 spinner
        level3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel3 = level3Adapter.getItem(position);

                // Load level 4 data
                try {
                    List<Hierarchy> level4Data = hierarchyViewModel.retrieveLevel4(selectedLevel3.getExtId());
                    if(position == 0){ level4Data = null;} else{
                    level4Data.add(0,new Hierarchy("","Select SubDistrict"));
                    level4Adapter.clear();
                    level4Adapter.addAll(level4Data);}
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectionActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
                level5Adapter.clear();
                level6Adapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set listener for level 4 spinner
        level4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel4 = level4Adapter.getItem(position);

                // Load level 5 data
                try {
                    List<Hierarchy> level5Data = hierarchyViewModel.retrieveLevel5(selectedLevel4.getExtId());
                    if(position == 0){ level5Data = null;} else{
                    level5Data.add(0,new Hierarchy("","Select Village"));
                    level5Adapter.clear();
                    level5Adapter.addAll(level5Data);}
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectionActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
                level6Adapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set listener for level 5 spinner
        level5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel5 = level5Adapter.getItem(position);

                // Load level 6 data
                try {
                    List<Hierarchy> level6Data = hierarchyViewModel.retrieveLevel6(selectedLevel5.getExtId());
                    if(position == 0){ level6Data = null;} else{
                    level6Data.add(0,new Hierarchy("","Select Cluster"));
                    level6Adapter.clear();
                    level6Adapter.addAll(level6Data);}
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectionActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ExtendedFloatingActionButton start = findViewById(R.id.btn_location);
        start.setOnClickListener(v -> {
            if (level6Adapter == null || level6Spinner.getAdapter().isEmpty()) {
                Toast.makeText(this, "Please Select All Fields", Toast.LENGTH_LONG).show();
                return;
            }

            if(username.getText().toString()==null || username.getText().toString().trim().isEmpty()){
                username.setError("Invalid username");
                Toast.makeText(this,"Please provide a valid username", Toast.LENGTH_LONG).show();
                return;
            }


            final String myuser = username.getText().toString();

            Fieldworker fieldworker = null;
            try {
                fieldworker = fieldworkerViewModel.finds(myuser);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(fieldworker == null){
                username.setError("Invalid username");
                Toast.makeText(this,"Please provide a valid username", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            Bundle extras = new Bundle();

            Hierarchy selectedLevel1 = (Hierarchy) level1Spinner.getSelectedItem();
            Hierarchy selectedLevel2 = (Hierarchy) level2Spinner.getSelectedItem();
            Hierarchy selectedLevel3 = (Hierarchy) level3Spinner.getSelectedItem();
            Hierarchy selectedLevel4 = (Hierarchy) level4Spinner.getSelectedItem();
            Hierarchy selectedLevel5 = (Hierarchy) level5Spinner.getSelectedItem();
            Hierarchy selectedLevel6 = (Hierarchy) level6Spinner.getSelectedItem();

            username.setError(null);
            final Intent i = new Intent(this, TestActivity.class);
            //locationData.setClusterId(clusterData.getClusterId());
            //i.putExtra(LOCATION_DATA, locationData);
            String usname = username.getText().toString();
            i.putExtra("username", usname);
            //extras.putParcelable(LOCATION_DATA, selectedLevel1);
            extras.putParcelable(VILLAGE_DATA, selectedLevel5);
            extras.putParcelable(CLUSTER_DATA, selectedLevel6);
            extras.putString("ROUND_NUMBER", String.valueOf(round));
            extras.putString("USERNAME", username.getText().toString().trim());
            startActivity(i);
        });


    }

    private <T> void callable(Spinner spinner, T[] array){

        final ArrayAdapter<T> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    private int loadRoundData(Spinner spinner, RoundViewModel viewModel){
        int listSize = 0;
        try {
            List<Round> list = viewModel.findAll();
            list.add(0,new Round("","Select Round"));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Round[0]));
                listSize = list.size();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listSize;
    }
}