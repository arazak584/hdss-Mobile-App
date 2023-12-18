package org.openhds.hdsscapture.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Baseline.BaselineActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HierarchyActivity extends AppCompatActivity {


    private Round roundData;
    private Hierarchy level6Data;
    private Hierarchy level5Data;
    private Fieldworker fieldworkerData;
    private ArrayAdapter<Hierarchy> level1Adapter;
    private ArrayAdapter<Hierarchy> level2Adapter;
    private ArrayAdapter<Hierarchy> level3Adapter;
    private ArrayAdapter<Hierarchy> level4Adapter;
    private ArrayAdapter<Hierarchy> level5Adapter;
    private ArrayAdapter<Hierarchy> level6Adapter;

    private final List<Hierarchy> level1List = new ArrayList<>();
    private final List<Hierarchy> level2List = new ArrayList<>();
    private final List<Hierarchy> level3List = new ArrayList<>();
    private final List<Hierarchy> level4List = new ArrayList<>();
    private final List<Hierarchy> level5List = new ArrayList<>();
    private final List<Hierarchy> level6List = new ArrayList<>();



    public static final String ROUND_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.ROUND_DATA";
    public static final String LEVEL5_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LEVEL5_DATA";
    public static final String LEVEL6_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LEVEL6_DATA";
    public static final String FIELDWORKER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.FIELDWORKER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hierarchy);


        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

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
        final EditText username = findViewById(R.id.login_username);

        if (fieldworkerDatas != null) {

            username.setText(fieldworkerDatas.username);
        }

        // set adapters to spinners
        level1Spinner.setAdapter(level1Adapter);
        level2Spinner.setAdapter(level2Adapter);
        level3Spinner.setAdapter(level3Adapter);
        level4Spinner.setAdapter(level4Adapter);
        level5Spinner.setAdapter(level5Adapter);
        level6Spinner.setAdapter(level6Adapter);

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


        int ccSize = loadRoundData(roundSpinner, roundViewModel);
        if(ccSize > 1) {
            roundSpinner.setSelection(1);
        }

        roundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0) {
                    roundData = null;
                }else
                {
                    final Round data = (Round) parent.getItemAtPosition(position);
                    roundData = data;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //roundSpinner.setEnabled(false);

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
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hierarchy selectedLevel1 = level1Adapter.getItem(position);

                // Reset level 3 spinner
                level2Adapter.clear();
                level3Adapter.clear();
                level4Adapter.clear();
                level5Adapter.clear();
                level6Adapter.clear();

                try {
                    List<Hierarchy> level2Data = hierarchyViewModel.retrieveLevel2(selectedLevel1.getUuid());
                    level2Data.add(0,new Hierarchy("","Select Region"));
                    level2Adapter.clear();
                    level2Adapter.addAll(level2Data);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(HierarchyActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }

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

                // Reset level 3 spinner
                level3Adapter.clear();
                level4Adapter.clear();
                level5Adapter.clear();
                level6Adapter.clear();

                level3Spinner.setSelection(0);
                level4Spinner.setSelection(0);
                level5Spinner.setSelection(0);
                level6Spinner.setSelection(0);

                if (position == 0) {
                    // If the first item is selected in level3Spinner, reset the lower-level spinners to position 0
                    return;
                }

                // Load level 3 data
                try {
                    List<Hierarchy> level3Data = hierarchyViewModel.retrieveLevel3(selectedLevel2.getUuid());
                    level3Data.add(0,new Hierarchy("","Select District"));
                    level3Adapter.addAll(level3Data);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(HierarchyActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }


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

                level4Adapter.clear();
                level5Adapter.clear();
                level6Adapter.clear();

                // Reset level 4 spinner to position 0
                level4Spinner.setSelection(0);
                // Reset level 5 spinner to position 0
                level5Spinner.setSelection(0);
                // Reset level 6 spinner to position 0
                level6Spinner.setSelection(0);

                if (position == 0) {
                    // If the first item ("Select District") is selected in level 3 spinner,
                    // do not load lower-level data and simply return
                    return;
                }

                // Load level 4 data
                try {
                    List<Hierarchy> level4Data = hierarchyViewModel.retrieveLevel4(selectedLevel3.getUuid());
                    level4Data.add(0, new Hierarchy("", "Select SubDistrict"));
                    level4Adapter.clear();
                    level4Adapter.addAll(level4Data);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(HierarchyActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
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

                level5Adapter.clear();
                level6Adapter.clear();

                // Reset level 5 spinner to position 0
                level5Spinner.setSelection(0);
                // Reset level 6 spinner to position 0
                level6Spinner.setSelection(0);

                if (position == 0) {
                    // If the first item ("Select District") is selected in level 3 spinner,
                    // do not load lower-level data and simply return
                    return;
                }

                // Load level 5 data
                try {
                    List<Hierarchy> level5Data = hierarchyViewModel.retrieveLevel5(selectedLevel4.getUuid());
                    level5Data.add(0, new Hierarchy("", "Select Village"));
                    level5Adapter.clear();
                    level5Adapter.addAll(level5Data);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(HierarchyActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set listener for level 5 spinner
        level5Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level5Data = level5Adapter.getItem(position);

                // Get the username and status from the Intent
                final Intent f = getIntent();
                final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
                String username = fieldworkerDatas.getUsername();
                Integer status = fieldworkerDatas.getStatus();

                // Reset level 6 spinner to position 0
                level6Spinner.setSelection(0);

                if (position == 0) {
                    // If the first item ("Select District") is selected in level 3 spinner,
                    // do not load lower-level data and simply return
                    return;
                }

                // Load level 6 data
                try {
                    List<Hierarchy> level6Data = hierarchyViewModel.retrieveLevel6(level5Data.getUuid());

                    // Check if status is a supervisor
                    if (status == 2) {
                        // If status is 3, use level6Data directly
                        level6Data.add(0, new Hierarchy("", "Select Sub Village"));
                        level6Adapter.clear();
                        level6Adapter.addAll(level6Data);
                    } else {
                        // Filter the data based on fw_name matching the username
                        List<Hierarchy> filteredData = new ArrayList<>();
                        for (Hierarchy item : level6Data) {
                            String fwName = item.getFw_name();
                            if (fwName != null && fwName.equals(username)) {
                                filteredData.add(item);
                            }
                        }

                        // Add "Select Sub Village" as the first item to the filtered data
                        filteredData.add(0, new Hierarchy("", "Select Sub Village"));

                        // Clear and update the adapter with the filtered data
                        level6Adapter.clear();
                        level6Adapter.addAll(filteredData);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(HierarchyActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set listener for level 6 spinner
        level6Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level6Data = level6Adapter.getItem(position);
                //level6Adapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        final Button start = findViewById(R.id.btn_location);
        start.setOnClickListener(v -> {
            if (level6Spinner.getSelectedItemPosition() == 0 || level6Spinner.getAdapter().isEmpty()) {
                Toast.makeText(this, "Please Select All Fields", Toast.LENGTH_LONG).show();
                return;
            }

            if (roundData == null) {
                Toast.makeText(this, "Round Not Selected", Toast.LENGTH_LONG).show();
                return;
            }

            if(username.getText().toString()==null || username.getText().toString().trim().isEmpty()){
                username.setError("Invalid Username");
                Toast.makeText(this,"Please provide a valid Username", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = username.getText().toString();

            try {
                fieldworkerData = fieldworkerViewModel.finds(myuser);

            } catch (ExecutionException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(fieldworkerData == null){
                username.setError("Invalid Username");
                Toast.makeText(this,"Please provide a valid Username", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(HierarchyActivity.this, LocationActivity.class);
            intent.putExtra(ROUND_DATA, roundData);
            intent.putExtra(LEVEL5_DATA, level5Data);
            intent.putExtra(LEVEL6_DATA, level6Data);
            intent.putExtra(FIELDWORKER_DATA, fieldworkerData);
            startActivity(intent);
        });




        final Button base = findViewById(R.id.btn_baseline);
        base.setOnClickListener(v -> {
            if (level6Spinner.getSelectedItemPosition() == 0 || level6Spinner.getAdapter().isEmpty()) {
                Toast.makeText(this, "Please Select All Fields", Toast.LENGTH_LONG).show();
                return;
            }

            if (roundData == null) {
                Toast.makeText(this, "Round Not Selected", Toast.LENGTH_LONG).show();
                return;
            }

            if(username.getText().toString()==null || username.getText().toString().trim().isEmpty()){
                username.setError("Invalid Username");
                Toast.makeText(this,"Please provide a valid Username", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = username.getText().toString();

            try {
                fieldworkerData = fieldworkerViewModel.finds(myuser);

            } catch (ExecutionException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(fieldworkerData == null){
                username.setError("Invalid Username");
                Toast.makeText(this,"Please provide a valid Username", Toast.LENGTH_LONG).show();
                return;
            }

            //Toast.makeText(this,"Enumeration Disabled", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(HierarchyActivity.this, BaselineActivity.class);
            intent.putExtra(ROUND_DATA, roundData);
            intent.putExtra(LEVEL5_DATA, level5Data);
            intent.putExtra(LEVEL6_DATA, level6Data);
            intent.putExtra(FIELDWORKER_DATA, fieldworkerData);
            startActivity(intent);
        });

        final Button remainder = findViewById(R.id.btn_visit);
        remainder.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),RemainderActivity.class);
            startActivity(i);
        });

        ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = viewModel.findAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        boolean upda = configsettings != null && !configsettings.isEmpty() && configsettings.get(0).updates;
        boolean enu = configsettings != null && !configsettings.isEmpty() && configsettings.get(0).enumeration;

       // RadioGroup radioGroup = findViewById(R.id.task);

        RadioGroup radioGroup = findViewById(R.id.task);
        RadioButton btnUpdate = findViewById(R.id.Update);
        RadioButton btnEnumerate = findViewById(R.id.Enumerate);

        btnUpdate.setEnabled(false);
        btnEnumerate.setEnabled(false);

        if (upda) {
            start.setVisibility(View.VISIBLE);
            btnUpdate.setEnabled(true);
            if (btnUpdate.isEnabled()) {
                btnUpdate.setChecked(true);
                Log.d("RadioButton", "Update RadioButton is enabled and checked");
            } else {
                Log.d("RadioButton", "Update RadioButton is not enabled");
            }
        } else {
            start.setVisibility(View.GONE);
            btnUpdate.setEnabled(false);
        }

        if (enu) {
            base.setVisibility(View.VISIBLE);
            btnEnumerate.setEnabled(true);
            if (btnEnumerate.isEnabled()) {
                btnEnumerate.setChecked(true);
                Log.d("RadioButton", "Enumerate RadioButton is enabled and checked");
            } else {
                Log.d("RadioButton", "Enumerate RadioButton is not enabled");
            }
        } else {
            base.setVisibility(View.GONE);
            btnEnumerate.setEnabled(false);
        }



//        final Button odk = findViewById(R.id.btn_odk);
//        odk.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setType("vnd.android.cursor.dir/vnd.odk.form");
//            startActivity(intent);
//        });


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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            HierarchyActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}