package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.ClusterViewModel;
import org.openhds.hdsscapture.Viewmodel.CountryViewModel;
import org.openhds.hdsscapture.Viewmodel.DistrictViewModel;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.Viewmodel.RegionViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.SubdistrictViewModel;
import org.openhds.hdsscapture.Viewmodel.VillageViewModel;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.Country;
import org.openhds.hdsscapture.entity.District;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Region;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Subdistrict;
import org.openhds.hdsscapture.entity.Village;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HierarchyActivity extends AppCompatActivity {

    ExtendedFloatingActionButton addfab;
    private Cluster clusterData;
    private Village villageData;
    private Location locationData;
    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";
    public static final String VILLAGE_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.VILLAGE_DATA";
    public static final String CLUSTER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.CLUSTER_DATA";

    Button addlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hierarchy);

        final CountryViewModel countryViewModel = new ViewModelProvider(this).get(CountryViewModel.class);
        final RegionViewModel regionViewModel = new ViewModelProvider(this).get(RegionViewModel.class);
        final DistrictViewModel districtViewModel = new ViewModelProvider(this).get(DistrictViewModel.class);
        final SubdistrictViewModel subdistrictViewModel = new ViewModelProvider(this).get(SubdistrictViewModel.class);
        final VillageViewModel villageViewModel = new ViewModelProvider(this).get(VillageViewModel.class);
        final ClusterViewModel clusterViewModel = new ViewModelProvider(this).get(ClusterViewModel.class);
        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);
        final RoundViewModel roundViewModel = new ViewModelProvider(this).get(RoundViewModel.class);

        final Spinner countrySpinner = findViewById(R.id.spinnerCountry);
        final Spinner regionSpinner = findViewById(R.id.spinnerRegion);
        final Spinner districtSpinner = findViewById(R.id.spinnerDistrict);
        final Spinner subdistrictSpinner = findViewById(R.id.spinnerSubdistrict);
        final Spinner villageSpinner = findViewById(R.id.spinnerVillage);
        final Spinner clusterSpinner = findViewById(R.id.spinnerCluster);
        final Spinner roundSpinner = findViewById(R.id.spinnerRound);

        final EditText username = findViewById(R.id.editTextUsername);

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

        int cSize = loadCountryData(countrySpinner, countryViewModel);
        if(cSize > 1) {
            countrySpinner.setSelection(1);
        }

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    callable(regionSpinner, new Region[0]);
                } else {
                    final Country data = (Country) parent.getItemAtPosition(position);
                    int rSize = loadRegionData(regionSpinner, regionViewModel, data.getExtId());
                    if(rSize > 1) {
                        regionSpinner.setSelection(1);
                    }
                }
                callable(districtSpinner, new District[0]);
                callable(subdistrictSpinner, new Subdistrict[0]);
                callable(villageSpinner, new Village[0]);
                callable(clusterSpinner, new Cluster[0]);
                //callable(locationSpinner, new Location[0]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Region Filter
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    callable(districtSpinner, new District[0]);
                } else {
                    final Region data = (Region) parent.getItemAtPosition(position);
                    loadDistrictData(districtSpinner, districtViewModel, data.getExtId());
                    if(districtSpinner.getAdapter()!=null && !districtSpinner.getAdapter().isEmpty()){
                    }
                }
                callable(subdistrictSpinner, new Subdistrict[0]);
                callable(villageSpinner, new Village[0]);
                callable(clusterSpinner, new Cluster[0]);
                //callable(locationSpinner, new Location[0]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //District
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    callable(subdistrictSpinner, new Subdistrict[0]);
                }else {
                    final District data = (District) parent.getItemAtPosition(position);
                    loadSubdistrictData(subdistrictSpinner, subdistrictViewModel, data.getExtId());
                }
                callable(villageSpinner, new Village[0]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //SubDistrict Filter
        subdistrictSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    callable(villageSpinner, new Village[0]);
                } else {
                    final Subdistrict data = (Subdistrict) parent.getItemAtPosition(position);
                    loadVillageData(villageSpinner, villageViewModel, data.getExtId());
                }
                callable(clusterSpinner, new Cluster[0]);
                //callable(locationSpinner, new Location[0]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Village Spinner
        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    villageData = null;
                    callable(clusterSpinner, new Cluster[0]);
                }else {
                    final Village data = (Village) parent.getItemAtPosition(position);
                    villageData = data;
                    loadClusterData(clusterSpinner, clusterViewModel, data.getExtId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Cluster Spinner
        clusterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    clusterData = null;
                    //callable(locationSpinner, new Location[0]);
                }else {
                    final Cluster data = (Cluster) parent.getItemAtPosition(position);
                    clusterData = data;
                    //loadLocationData(locationSpinner, locationViewModel, data.getClusterId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ExtendedFloatingActionButton start = findViewById(R.id.btn_select_location);
        start.setOnClickListener(v -> {
            if (clusterData == null || clusterSpinner.getAdapter().isEmpty()) {
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

            username.setError(null);
            final Intent i = new Intent(this, LocationActivity.class);
            //locationData.setClusterId(clusterData.getClusterId());
            //i.putExtra(LOCATION_DATA, locationData);
            String usname = username.getText().toString();
            i.putExtra("username", usname);
            i.putExtra(VILLAGE_DATA, villageData);
            i.putExtra(CLUSTER_DATA, clusterData);
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

    private int loadCountryData(Spinner spinner, CountryViewModel viewModel){
        int listSize = 0;
        try {
            List<Country> list = viewModel.findAll();
            list.add(0,new Country("","Select Country"));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Country[0]));
                listSize = list.size();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listSize;
    }

    private int loadRegionData(Spinner spinner, RegionViewModel viewModel, String parent_uuid){
        int listSize = 0;
        try {
            List<Region> list = viewModel.findRegionsOfCountry(parent_uuid);
            list.add(0,new Region("","Select Region",parent_uuid));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Region[0]));
                listSize = list.size();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listSize;
    }

    private void loadDistrictData(Spinner spinner, DistrictViewModel viewModel, String regionId){
        try {
            List<District> list = viewModel.findDistrictsOfRegion(regionId);
            list.add(0,new District("", "Select District", regionId));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new District[0]));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void loadSubdistrictData(Spinner spinner, SubdistrictViewModel viewModel, String districtId){
        try {
            List<Subdistrict> list = viewModel.findSubdistrictsOfDistrict(districtId);
            list.add(0,new Subdistrict("", "Select Subdistrict", districtId));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Subdistrict[0]));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void loadVillageData(Spinner spinner, VillageViewModel viewModel, String subdistrictId){
        try {
            List<Village> list = viewModel.findVillagesOfSubdistrict(subdistrictId);
            list.add(0,new Village("", "Select Village", subdistrictId));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Village[0]));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void loadClusterData(Spinner spinner, ClusterViewModel viewModel, String villageId){
        try {
            List<Cluster> list = viewModel.findClustersOfVillage(villageId);
            list.add(0,new Cluster("","Select Cluster", villageId));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Cluster[0]));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}