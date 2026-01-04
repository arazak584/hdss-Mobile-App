package org.openhds.hdsscapture.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Duplicate.DuplicateActivity;
import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.ConfigViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyLevelViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;
import org.openhds.hdsscapture.entity.Round;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HierarchyActivity extends AppCompatActivity {


    private Round roundData;
    private Hierarchy level6Data;
    private Hierarchy level5Data;
    private Fieldworker fieldworkerData;
    private int status;

//    private ArrayAdapter<Hierarchy> level1Adapter;
//    private ArrayAdapter<Hierarchy> level2Adapter;
//    private ArrayAdapter<Hierarchy> level3Adapter;
//    private ArrayAdapter<Hierarchy> level4Adapter;
//    private ArrayAdapter<Hierarchy> level5Adapter;
//    private ArrayAdapter<Hierarchy> level6Adapter;

    private String username;
    // Dynamic arrays for adapters and data
    private ArrayAdapter<Hierarchy>[] levelAdapters;
    private List<Hierarchy>[] levelLists;
    private Spinner[] levelSpinners;
    private TextView[] levelTextViews;
    private LinearLayout[] levelLayouts;
    // Store hierarchy levels configuration
    private List<HierarchyLevel> hierarchyLevels;
    private int maxHierarchyLevel = 4; // Default minimum
    // Map to store selected hierarchy data at each level
    private Map<Integer, Hierarchy> selectedHierarchyData = new HashMap<>();


    public static final String ROUND_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.ROUND_DATA";
    public static final String LEVEL5_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LEVEL5_DATA";
    public static final String LEVEL6_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LEVEL6_DATA";
    public static final String FIELDWORKER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.FIELDWORKER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hierarchy);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize hierarchy levels first
        initializeHierarchyLevels();
        // Initialize dynamic arrays based on max hierarchy level
        initializeDynamicArrays();

        // Setup UI components
        setupHierarchyUI();

        // Setup other components
        setupLoginAndRound();
        setupButtons();
        setupConfiguration();


    }

    private void initializeHierarchyLevels() {
        HierarchyLevelViewModel hModel = new ViewModelProvider(this).get(HierarchyLevelViewModel.class);

        try {
            hierarchyLevels = hModel.retrieve();
            if (hierarchyLevels != null && !hierarchyLevels.isEmpty()) {
                // Find the maximum hierarchy level
                maxHierarchyLevel = 4; // Start with minimum
                for (HierarchyLevel level : hierarchyLevels) {
                    if (level.getKeyIdentifier() > maxHierarchyLevel) {
                        maxHierarchyLevel = level.getKeyIdentifier();
                    }
                }
                // Ensure we don't exceed 8 levels
                maxHierarchyLevel = Math.min(maxHierarchyLevel, 8);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            // Fallback to minimum 4 levels
            maxHierarchyLevel = 4;
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeDynamicArrays() {
        levelAdapters = new ArrayAdapter[maxHierarchyLevel];
        levelLists = new List[maxHierarchyLevel];
        levelSpinners = new Spinner[maxHierarchyLevel];
        levelTextViews = new TextView[maxHierarchyLevel];
        levelLayouts = new LinearLayout[maxHierarchyLevel];

        // Initialize lists
        for (int i = 0; i < maxHierarchyLevel; i++) {
            levelLists[i] = new ArrayList<>();
        }
    }

    private void setupHierarchyUI() {
        // Map resource IDs to array indices
        int[] spinnerIds = {R.id.spCountry, R.id.spRegion, R.id.spDistrict,
                R.id.spSubdistrict, R.id.spVillage, R.id.spCluster,
                R.id.spLevel7, R.id.spLevel8}; // Add IDs for level 7 and 8 if needed

        int[] textViewIds = {R.id.textView3, R.id.textView4, R.id.textView5,
                R.id.textView6, R.id.textView7, R.id.textView8,
                R.id.textView9, R.id.textView10}; // Add IDs for level 7 and 8 if needed

        int[] layoutIds = {R.id.layout_level1, R.id.layout_level2, R.id.layout_level3,
                R.id.layout_level4, R.id.layout_level5, R.id.layout_level6,
                R.id.layout_level7, R.id.layout_level8}; // Add layout IDs if needed

        // Initialize UI components for active levels
        for (int i = 0; i < maxHierarchyLevel; i++) {
            if (i < spinnerIds.length) {
                levelSpinners[i] = findViewById(spinnerIds[i]);
                levelTextViews[i] = findViewById(textViewIds[i]);

                // Show/hide layouts if they exist
                if (i < layoutIds.length) {
                    levelLayouts[i] = findViewById(layoutIds[i]);
                    if (levelLayouts[i] != null) {
                        levelLayouts[i].setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        // Hide unused levels (levels beyond maxHierarchyLevel)
        for (int i = maxHierarchyLevel; i < 8; i++) {
            if (i < layoutIds.length) {
                LinearLayout layout = findViewById(layoutIds[i]);
                if (layout != null) {
                    layout.setVisibility(View.GONE);
                }
            }
        }

        // Set hierarchy level names from database
        setHierarchyLevelNames();

        // Initialize adapters and set up listeners
        initializeAdapters();
        setupSpinnerListeners();

        // Load first level data
        loadFirstLevelData();
    }

    private void setHierarchyLevelNames() {
        if (hierarchyLevels != null) {
            for (HierarchyLevel level : hierarchyLevels) {
                int index = level.getKeyIdentifier() - 1; // Convert to 0-based index
                if (index >= 0 && index < maxHierarchyLevel && levelTextViews[index] != null) {
                    levelTextViews[index].setText(level.getName());
                }
            }
        }
    }

    private void initializeAdapters() {
        for (int i = 0; i < maxHierarchyLevel; i++) {
            if (levelSpinners[i] != null) {
                levelAdapters[i] = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
                levelAdapters[i].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                levelSpinners[i].setAdapter(levelAdapters[i]);
            }
        }
    }

    private void setupSpinnerListeners() {
        for (int i = 0; i < maxHierarchyLevel; i++) {
            final int level = i + 1; // Convert to 1-based level
            final int index = i; // Keep 0-based index for arrays

            if (levelSpinners[i] != null) {
                levelSpinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        handleLevelSelection(level, index, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }
    }

    private void handleLevelSelection(int level, int index, int position) {
        if (position == 0) {
            // Clear selection and reset lower levels
            selectedHierarchyData.remove(level);
            clearLowerLevels(index + 1);

            // Clear level5Data and level6Data if they correspond to cleared levels
            updateLevelData();
            return;
        }

        Hierarchy selectedHierarchy = levelAdapters[index].getItem(position);
        if (selectedHierarchy == null) {
            showErrorToast("Selected hierarchy item is null");
            return;
        }

        selectedHierarchyData.put(level, selectedHierarchy);

        // Update level5Data and level6Data to represent the last two levels
        updateLevelData();

        // Clear and load next level data
        clearLowerLevels(index + 1);
        loadNextLevelData(selectedHierarchy.getUuid(), level);
    }

    // Add this new method to update level5Data and level6Data dynamically
    private void updateLevelData() {
        // Calculate the last two levels based on maxHierarchyLevel
        int secondLastLevel = maxHierarchyLevel - 1;  // e.g., if max is 4, then 3
        int lastLevel = maxHierarchyLevel;            // e.g., if max is 4, then 4

        // Update level5Data to represent the second-to-last level
        level5Data = selectedHierarchyData.get(secondLastLevel);

        // Update level6Data to represent the last level
        level6Data = selectedHierarchyData.get(lastLevel);
    }

    // Also update the clearLowerLevels method to call updateLevelData
    private void clearLowerLevels(int fromIndex) {
        for (int i = fromIndex; i < maxHierarchyLevel; i++) {
            if (levelAdapters[i] != null) {
                levelAdapters[i].clear();
            }
            if (levelSpinners[i] != null) {
                levelSpinners[i].setSelection(0);
            }
            selectedHierarchyData.remove(i + 1);
        }

        // Update level5Data and level6Data after clearing
        updateLevelData();
    }

    private void loadFirstLevelData() {
        if (levelAdapters[0] == null) return;

        HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);

        try {
            List<Hierarchy> level1Data = hierarchyViewModel.retrieveLevel1();
            String textMessage = getString(R.string.select);
            level1Data.add(0, new Hierarchy("", textMessage));
            levelAdapters[0].addAll(level1Data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNextLevelData(String parentUuid, int currentLevel) {
        // Generate the levelUuid based on the current level number
        String levelUuid = getLevelUuid(currentLevel + 1);

        int nextLevelIndex = currentLevel; // Next level index (0-based)
        if (nextLevelIndex >= maxHierarchyLevel) {
            //showErrorToast("No more levels to load");
            return;
        }

        if (levelAdapters[nextLevelIndex] == null) {
            showErrorToast("Adapter not initialized for level " + (currentLevel + 1));
            return;
        }

        HierarchyViewModel hierarchyViewModel = new ViewModelProvider(this).get(HierarchyViewModel.class);
        String textMessage = getString(R.string.select);

        try {
            List<Hierarchy> nextLevelData;

            // Use appropriate method based on level and user status
            switch (currentLevel + 1) { // Next level (1-based)
                case 2:
                    nextLevelData = (status == 2) ?
                            hierarchyViewModel.retrieveLevel2(parentUuid) :
                            hierarchyViewModel.retrieveLevel2i(parentUuid, username);
                    break;
                case 3:
                    nextLevelData = (status == 2) ?
                            hierarchyViewModel.retrieveLevel3(parentUuid) :
                            hierarchyViewModel.retrieveLevel3i(parentUuid, username);
                    break;
                case 4:
                    nextLevelData = (status == 2) ?
                            hierarchyViewModel.retrieveLevel4(parentUuid) :
                            hierarchyViewModel.retrieveLevel4i(parentUuid, username);
                    break;
                case 5:
                    nextLevelData = (status == 2) ?
                            hierarchyViewModel.retrieveLevel5(parentUuid) :
                            hierarchyViewModel.retrieveLevel5i(parentUuid, username);
                    break;
                case 6:
                    nextLevelData = hierarchyViewModel.retrieveLevel6(parentUuid);
                    // Apply filtering for non-supervisors
                    if (status != 2) {
                        nextLevelData = filterDataForNonSupervisors(nextLevelData);
                    }
                    break;
                default:
                    // For levels beyond 6, use generic retrieval method
                    nextLevelData = hierarchyViewModel.retrieveLevel(parentUuid, levelUuid);
                    break;
            }

            nextLevelData.add(0, new Hierarchy("", textMessage));
            levelAdapters[nextLevelIndex].clear();
            levelAdapters[nextLevelIndex].addAll(nextLevelData);

        } catch (ExecutionException | InterruptedException e) {
            showErrorToast("Error loading data for level " + (currentLevel + 1));
            e.printStackTrace();
        }
    }

    private String getLevelUuid(int levelNumber) {
        return "hierarchyLevelId" + levelNumber;
    }


    private void setupLoginAndRound() {
        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);

        // Retrieve login details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        status = sharedPreferences.getInt(LoginActivity.FW_STATUS, 0);
        username = sharedPreferences.getString(LoginActivity.FW_USERNAME_KEY, null);

        final RoundViewModel roundViewModel = new ViewModelProvider(this).get(RoundViewModel.class);
        final Spinner roundSpinner = findViewById(R.id.spRound);
        final EditText usrname = findViewById(R.id.login_username);

        if (username != null) {
            usrname.setText(username);
        }

        int ccSize = loadRoundData(roundSpinner, roundViewModel);
        if (ccSize > 1) {
            roundSpinner.setSelection(1);
        }

        roundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    roundData = null;
                } else {
                    final Round data = (Round) parent.getItemAtPosition(position);
                    roundData = data;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupButtons() {
        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);
        final EditText usrname = findViewById(R.id.login_username);

        final Button start = findViewById(R.id.btn_location);
        start.setOnClickListener(v -> {
            if (!validateSelection()) return;
            if (!validateUsername(usrname, fieldworkerViewModel)) return;

            Intent intent = new Intent(HierarchyActivity.this, LocationActivity.class);
            intent.putExtra(ROUND_DATA, roundData);
            intent.putExtra(LEVEL5_DATA, level5Data);
            intent.putExtra(LEVEL6_DATA, level6Data);
            intent.putExtra(FIELDWORKER_DATA, fieldworkerData);
            startActivity(intent);
        });

//        final Button base = findViewById(R.id.btn_baseline);
//        base.setOnClickListener(v -> {
//            if (!validateSelection()) return;
//            if (!validateUsername(usrname, fieldworkerViewModel)) return;
//
//            Intent intent = new Intent(HierarchyActivity.this, BaselineActivity.class);
//            intent.putExtra(ROUND_DATA, roundData);
//            intent.putExtra(LEVEL5_DATA, level5Data);
//            intent.putExtra(LEVEL6_DATA, level6Data);
//            intent.putExtra(FIELDWORKER_DATA, fieldworkerData);
//            startActivity(intent);
//        });

        final Button dups = findViewById(R.id.btn_dup);
        dups.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), DuplicateActivity.class);
            startActivity(i);
        });
    }

    // Update the validateSelection method to be more flexible
    private boolean validateSelection() {
        // Check if we have selections for at least the last two levels
        int secondLastLevel = maxHierarchyLevel - 1;
        int lastLevel = maxHierarchyLevel;

        // Check if the last level spinner has a valid selection
        int lastLevelIndex = maxHierarchyLevel - 1;
        if (levelSpinners[lastLevelIndex] == null ||
                levelSpinners[lastLevelIndex].getSelectedItemPosition() == 0 ||
                levelSpinners[lastLevelIndex].getAdapter().isEmpty()) {
            Toast.makeText(this, "Please Select All Fields", Toast.LENGTH_LONG).show();
            return false;
        }

        // Ensure we have data for the last two levels
        if (selectedHierarchyData.get(secondLastLevel) == null ||
                selectedHierarchyData.get(lastLevel) == null) {
            Toast.makeText(this, "Please complete all hierarchy selections", Toast.LENGTH_LONG).show();
            return false;
        }

        if (roundData == null) {
            Toast.makeText(this, "Round Not Selected", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private boolean validateUsername(EditText usrname, FieldworkerViewModel fieldworkerViewModel) {
        if (usrname.getText().toString() == null || usrname.getText().toString().trim().isEmpty()) {
            usrname.setError("Invalid Username");
            Toast.makeText(this, "Please provide a valid Username", Toast.LENGTH_LONG).show();
            return false;
        }

        final String myuser = usrname.getText().toString();

        try {
            fieldworkerData = fieldworkerViewModel.finds(myuser);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, "Something terrible went wrong", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        if (fieldworkerData == null) {
            usrname.setError("Invalid Username");
            Toast.makeText(this, "Please provide a valid Username", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setupConfiguration() {
        ConfigViewModel viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        List<Configsettings> configsettings = null;
        try {
            configsettings = viewModel.findAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        boolean upda = configsettings != null && !configsettings.isEmpty() && configsettings.get(0).updates;
        boolean enu = configsettings != null && !configsettings.isEmpty() && configsettings.get(0).enumeration;

        RadioGroup radioGroup = findViewById(R.id.task);
        RadioButton btnUpdate = findViewById(R.id.Update);
        RadioButton btnEnumerate = findViewById(R.id.Enumerate);
        Button start = findViewById(R.id.btn_location);
        Button base = findViewById(R.id.btn_baseline);

        btnUpdate.setEnabled(false);
        btnEnumerate.setEnabled(false);

        if (upda) {
            start.setVisibility(View.VISIBLE);
            btnUpdate.setEnabled(true);
            if (btnUpdate.isEnabled()) {
                btnUpdate.setChecked(true);
                Log.d("RadioButton", "Update RadioButton is enabled and checked");
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
            }
        } else {
            base.setVisibility(View.GONE);
            btnEnumerate.setEnabled(false);
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private List<Hierarchy> filterDataForNonSupervisors(List<Hierarchy> data) {
        List<Hierarchy> filteredData = new ArrayList<>();
        for (Hierarchy item : data) {
            String fwName = item.getFw_name();
            if (fwName != null && fwName.equals(username)) {
                filteredData.add(item);
            }
        }
        return filteredData;
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
                        // Start MainActivity
                        Intent intent = new Intent(HierarchyActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        // Finish the current activity
                        HierarchyActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}