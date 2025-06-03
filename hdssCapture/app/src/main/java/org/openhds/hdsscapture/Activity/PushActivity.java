package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.CommunityViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RegistryViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.Viewmodel.VpmViewModel;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.CommunityReport;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Registry;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.entity.Vpm;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private final String TAG = "Syncing Data Errors: ";

    private ListingViewModel listingViewModel;
    private Button buttonSendList , buttonSendVisit, buttonSendLocationdata, buttonSendIndividualdata, buttonSendSocialgroupdata,
    buttonSendRelationshipdata,buttonSendPregnancydata,buttonSendOutcomedata,buttonSendOutcomesdata,buttonSendDemographicdata,buttondth,
    buttonvpm,buttonSendSocio,buttonSendRes,buttonSendImg,buttonSendOmg,buttonSendAmend,buttonSendVac,buttonSendDup,buttonSendcom,buttonSendreg,buttonSendmor;
    private String authorizationHeader;
    private SharedPreferences preferences;

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        buttonSendList = findViewById(R.id.buttonSendList);
        buttonSendVisit = findViewById(R.id.buttonSendVisit);
        buttonSendmor = findViewById(R.id.buttonSendMor);
        buttonSendLocationdata = findViewById(R.id.buttonSendLocation);
        buttonSendIndividualdata = findViewById(R.id.buttonSendIndividual);
        buttonSendSocialgroupdata = findViewById(R.id.buttonSendSocialgroup);
        buttonSendRelationshipdata = findViewById(R.id.buttonSendRelationship);
        buttonSendPregnancydata = findViewById(R.id.buttonSendPregnancy);
        buttonSendOutcomedata = findViewById(R.id.buttonSendOutcome);
        buttonSendOutcomesdata = findViewById(R.id.buttonSendOutcomes);
        buttonSendDemographicdata = findViewById(R.id.buttonSendDemography);
        buttondth = findViewById(R.id.buttondth);
        buttonvpm = findViewById(R.id.buttonvpm);
        buttonSendSocio = findViewById(R.id.buttonSendSocio);
        buttonSendRes = findViewById(R.id.buttonSendResidency);
        buttonSendImg = findViewById(R.id.buttonSendImg);
        buttonSendOmg = findViewById(R.id.buttonSendOmg);
        buttonSendAmend = findViewById(R.id.buttonSendAmend);
        buttonSendVac = findViewById(R.id.buttonSendVac);
        buttonSendDup = findViewById(R.id.buttonSendDup);
        buttonSendcom = findViewById(R.id.buttonSendCom);
        buttonSendreg = findViewById(R.id.buttonSendReg);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);


        progress = new ProgressDialog(PushActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        // Retrieve authorizationHeader from SharedPreferences
        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        //SEND DATA
            initializeMorbidityReport();
            initializeLocationReport();
            initializeVisitReport();
            initializeListingReport();
            initializeIndividualReport();
            initializeSocialgroupReport();
            initializeRelationshipReport();
            initializePregnancyReport();
            initializePregnancyoutcomeReport();
            initializeOutcomeReport();
            initializeDemographicReport();
            initializeDeathReport();
            initializeVpmReport();
            initializeSesReport();
            initializeResidencyReport();
            initializeInmigrationReport();
            initializeOutmigrationReport();
            initializeAmendmentReport();
            initializeVaccinationReport();
            initializeDuplicateReport();
            initializeCommunityReport();
            initializeRegistryReport();

//        //PUSH LOCATION
//        //final TextView textViewSendLocationdata = findViewById(R.id.textViewSendLocation);
//        final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
//        //GET MODIFIED DATA
//        final List<Locations> locationsList = new ArrayList<>();
//        try {
//            locationsList.addAll(locationViewModel.findToSync());
//            buttonSendLocationdata.setText("Locations(" + locationsList.size() + ") to send");
//            buttonSendLocationdata.setTextColor(Color.WHITE);
//            if (locationsList.isEmpty()) {
//                buttonSendLocationdata.setVisibility(View.GONE);
//                //textViewSendLocationdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendLocationdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Locations> data = new DataWrapper<>(locationsList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Locations>> c_callable = dao.sendLocationdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Locations>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Locations>> call, Response<DataWrapper<Locations>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Locations[] d = data.getData().toArray(new Locations[0]);
//                            for (Locations elem : d) {
//                                elem.setComplete(0);
//                                Log.e("PUSH.tag", "Has value " + elem.getCompno());
//                            }
//                            locationViewModel.add(d);
//                            progress.dismiss();
//                            buttonSendLocationdata.setText("Sent " + d.length + " record(s)");
//                            //textViewSendLocationdata.setTextColor(Color.rgb(0, 114, 133));
//                            buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendLocationdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Locations>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendLocationdata.setText("Failed to Send Location Data");
//                        buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Location Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH VISIT DATA
//        final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
//        //GET MODIFIED DATA
//        final List<Visit> visitList = new ArrayList<>();
//        try {
//            visitList.addAll(visitViewModel.findToSync());
//            buttonSendVisit.setText("Visit(" + visitList.size() + ") to send");
//            buttonSendVisit.setTextColor(Color.WHITE);
//            if (visitList.isEmpty()) {
//                buttonSendVisit.setVisibility(View.GONE);
//               // textViewSendVisit.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendVisit.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Visit> data = new DataWrapper<>(visitList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Visit>> c_callable = dao.sendVisitdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Visit>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Visit>> call, Response<DataWrapper<Visit>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Visit[] d = data.getData().toArray(new Visit[0]);
//
//                            for (Visit elem : d) {
//                                elem.complete = 0;
//                            }
//                            visitViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendVisit.setText("Sent " + d.length + " record(s)");
//                            buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                            //buttonSendVisit.setTextColor(Color.parseColor("#FFFFFFFF"));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendVisit.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Visit>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendVisit.setText("Failed to Send Visit Data");
//                        buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to send Visit data", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, t.getMessage());
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });

        //GET MODIFIED DATA
//        final List<Listing> listingList = new ArrayList<>();
//        try {
//            listingList.addAll(listingViewModel.findToSync());
//            buttonSendList.setText("Listing(" + listingList.size() + ") to send");
//            buttonSendList.setTextColor(Color.WHITE);
//            if (listingList.isEmpty()) {
//                buttonSendList.setVisibility(View.GONE);
//                //textViewSendList.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendList.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Listing> data = new DataWrapper<>(listingList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Listing>> c_callable = dao.sendListing(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Listing>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Listing>> call, Response<DataWrapper<Listing>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Listing[] d = data.getData().toArray(new Listing[0]);
//
//                            for (Listing elem : d) {
//                                elem.complete = 0;
//                            }
//                            listingViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendList.setText("Sent " + d.length + " record(s)");
//                            buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendList.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Listing>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendList.setText("Failed to Send Listing Data");
//                        buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to send Listing", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, t.getMessage());
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });

        //PUSH INDIVIDUAL
        //final TextView textViewSendIndividualdata = findViewById(R.id.textViewSendIndividual);
//        final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Individual> individualList = new ArrayList<>();
//        try {
//            individualList.addAll(individualViewModel.findToSync());
//            buttonSendIndividualdata.setText("Individuals(" + individualList.size() + ") to send");
//            buttonSendIndividualdata.setTextColor(Color.WHITE);
//            if (individualList.isEmpty()) {
//                buttonSendIndividualdata.setVisibility(View.GONE);
//                //textViewSendIndividualdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendIndividualdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Individual> data = new DataWrapper<>(individualList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Individual>> c_callable = dao.sendIndividualdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Individual>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Individual>> call, Response<DataWrapper<Individual>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Individual[] d = data.getData().toArray(new Individual[0]);
//                            for (Individual elem : d) {
//                                elem.complete = 0;
//                            }
//                            individualViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendIndividualdata.setText("Sent " + d.length + " Individual record(s)");
//                            buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendIndividualdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Individual>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendIndividualdata.setText("Failed to Send Individual Data");
//                        buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Individual Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH SOCIALGROUP
//        //final TextView textViewSendSocialgroupdata = findViewById(R.id.textViewSendSocialgroup);
//        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Socialgroup> socialgroupList = new ArrayList<>();
//        try {
//            socialgroupList.addAll(socialgroupViewModel.findToSync());
//            buttonSendSocialgroupdata.setText("Socialgroup (" + socialgroupList.size() + ") to send");
//            buttonSendSocialgroupdata.setTextColor(Color.WHITE);
//            if (socialgroupList.isEmpty()) {
//                buttonSendSocialgroupdata.setVisibility(View.GONE);
//                //textViewSendSocialgroupdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendSocialgroupdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Socialgroup> data = new DataWrapper<>(socialgroupList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Socialgroup>> c_callable = dao.sendSocialgroupdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Socialgroup>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Socialgroup>> call, Response<DataWrapper<Socialgroup>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Socialgroup[] d = data.getData().toArray(new Socialgroup[0]);
//
//
//                            for (Socialgroup elem : d) {
//                                elem.complete = 0;
//                            }
//                            socialgroupViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendSocialgroupdata.setText("Sent " + d.length + " Socialgroup record(s)");
//                            buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendSocialgroupdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Socialgroup>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendSocialgroupdata.setText("Failed to Send Socialgroup Data");
//                        buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Socialgroup Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Relationship
//        final RelationshipViewModel relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
//        //GET MODIFIED DATA
//        final List<Relationship> relationshipList = new ArrayList<>();
//        try {
//            relationshipList.addAll(relationshipViewModel.findToSync());
//            buttonSendRelationshipdata.setText("Relationship (" + relationshipList.size() + ") to send");
//            buttonSendRelationshipdata.setTextColor(Color.WHITE);
//            if (relationshipList.isEmpty()) {
//                buttonSendRelationshipdata.setVisibility(View.GONE);
//                //textViewSendRelationshipdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendRelationshipdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Relationship> data = new DataWrapper<>(relationshipList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Relationship>> c_callable = dao.sendRelationshipdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Relationship[] d = data.getData().toArray(new Relationship[0]);
//
//                            for (Relationship elem : d) {
//                                elem.complete = 0;
//                            }
//                            relationshipViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendRelationshipdata.setText("Sent " + d.length + " Relationship record(s)");
//                            buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendRelationshipdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendRelationshipdata.setText("Failed to Send Relationship Data");
//                        buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Relationship Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Pregnancy
//        final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
//        //GET MODIFIED DATA
//        final List<Pregnancy> pregnancyList = new ArrayList<>();
//        try {
//            pregnancyList.addAll(pregnancyViewModel.findToSync());
//            buttonSendPregnancydata.setText("Pregnancy (" + pregnancyList.size() + ") to send");
//            buttonSendPregnancydata.setTextColor(Color.WHITE);
//            if (pregnancyList.isEmpty()) {
//                buttonSendPregnancydata.setVisibility(View.GONE);
//                //textViewSendPregnancydata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendPregnancydata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Pregnancy> data = new DataWrapper<>(pregnancyList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Pregnancy>> c_callable = dao.sendPregnancydata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Pregnancy[] d = data.getData().toArray(new Pregnancy[0]);
//
//                            for (Pregnancy elem : d) {
//                                elem.complete = 0;
//                            }
//                            pregnancyViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendPregnancydata.setText("Sent " + d.length + " Pregnancy record(s)");
//                            buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendPregnancydata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendPregnancydata.setText("Failed to Send Pregnancy Data");
//                        buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Pregnancy Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Pregnancyoutcome
//        final PregnancyoutcomeViewModel pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
//        //GET MODIFIED DATA
//        final List<Pregnancyoutcome> pregnancyoutcomeList = new ArrayList<>();
//        try {
//            pregnancyoutcomeList.addAll(pregnancyoutcomeViewModel.findToSync());
//            buttonSendOutcomedata.setText("Pregnancy Outcome (" + pregnancyoutcomeList.size() + ") to send");
//            buttonSendOutcomedata.setTextColor(Color.WHITE);
//            if (pregnancyoutcomeList.isEmpty()) {
//                buttonSendOutcomedata.setVisibility(View.GONE);
//                //textViewSendOutcomedata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendOutcomedata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Pregnancyoutcome> data = new DataWrapper<>(pregnancyoutcomeList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.sendPregoutcomedata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Pregnancyoutcome[] d = data.getData().toArray(new Pregnancyoutcome[0]);
//
//                            for (Pregnancyoutcome elem : d) {
//                                elem.complete = 0;
//                            }
//                            pregnancyoutcomeViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendOutcomedata.setText("Sent " + d.length + " Pregnancy Outcome record(s)");
//                            buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendOutcomedata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendOutcomedata.setText("Failed to Send Pregnancy Outcome Data");
//                        buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Pregnancy Outcome Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });

//        //PUSH Pregnancyoutcomes
//        final OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
//        //GET MODIFIED DATA
//        final List<Outcome> outcomeList = new ArrayList<>();
//        try {
//            outcomeList.addAll(outcomeViewModel.findToSync());
//            buttonSendOutcomesdata.setText("Outcome (" + outcomeList.size() + ") to send");
//            buttonSendOutcomesdata.setTextColor(Color.WHITE);
//            if (outcomeList.isEmpty()) {
//                buttonSendOutcomesdata.setVisibility(View.GONE);
//                //textViewSendOutcomesdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendOutcomesdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Outcome> data = new DataWrapper<>(outcomeList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Outcome>> c_callable = dao.sendOutcomedata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Outcome>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Outcome>> call, Response<DataWrapper<Outcome>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Outcome[] d = data.getData().toArray(new Outcome[0]);
//
//                            for (Outcome elem : d) {
//                                elem.complete = 0;
//                            }
//                            outcomeViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendOutcomesdata.setText("Sent " + d.length + " Outcome record(s)");
//                            buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendOutcomesdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Outcome>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendOutcomesdata.setText("Failed to Send Outcome Data");
//                        buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Outcome Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Demographic
//        final DemographicViewModel demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
//        //GET MODIFIED DATA
//        final List<Demographic> demographicList = new ArrayList<>();
//        try {
//            demographicList.addAll(demographicViewModel.findToSync());
//            buttonSendDemographicdata.setText("Demographic (" + demographicList.size() + ") to send");
//            buttonSendDemographicdata.setTextColor(Color.WHITE);
//            if (demographicList.isEmpty()) {
//                buttonSendDemographicdata.setVisibility(View.GONE);
//               // textViewSendDemographicdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendDemographicdata.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Demographic> data = new DataWrapper<>(demographicList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Demographic>> c_callable = dao.sendDemographicdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Demographic[] d = data.getData().toArray(new Demographic[0]);
//
//                            for (Demographic elem : d) {
//                                elem.complete = 0;
//                                //Log.e("PUSH.tag", "Has value " + elem.edtime);
//                            }
//                            demographicViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendDemographicdata.setText("Sent " + d.length + " Demographic record(s)");
//                            buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendDemographicdata.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendDemographicdata.setText("Failed to Send Demographic Data");
//                        buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Demographic Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH DEATH
//        final DeathViewModel deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
//        //GET MODIFIED DATA
//        final List<Death> deathList = new ArrayList<>();
//        try {
//            deathList.addAll(deathViewModel.findToSync());
//            buttondth.setText("Death (" + deathList.size() + ") to send");
//            buttondth.setTextColor(Color.WHITE);
//            if (deathList.isEmpty()) {
//                buttondth.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttondth.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Death> data = new DataWrapper<>(deathList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Death>> c_callable = dao.sendDeathdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Death[] d = data.getData().toArray(new Death[0]);
//
//                            for (Death elem : d) {
//                                elem.complete = 0;
//                                //Log.e("PUSH.tag", "Has value " + elem.edtime);
//                            }
//                            deathViewModel.add(d);
//                            progress.dismiss();
//                            buttondth.setText("Sent " + d.length + " Death record(s)");
//                            buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttondth.setText("Failed to Send Data: Error "+ response.code());
//                            buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttondth.setText("Failed to Send Death Data");
//                        buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Death Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH VPM
//        final VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
//        //GET MODIFIED DATA
//        final List<Vpm> vpmList = new ArrayList<>();
//        try {
//            vpmList.addAll(vpmViewModel.retrieveToSync());
//            buttonvpm.setText("VPM (" + vpmList.size() + ") to send");
//            buttonvpm.setTextColor(Color.WHITE);
//            if (vpmList.isEmpty()) {
//                buttonvpm.setVisibility(View.GONE);
//                // textViewSendDemographicdata.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonvpm.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Vpm> data = new DataWrapper<>(vpmList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Vpm>> c_callable = dao.sendVpmdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Vpm>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Vpm>> call, Response<DataWrapper<Vpm>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Vpm[] d = data.getData().toArray(new Vpm[0]);
//
//                            for (Vpm elems : d) {
//                                elems.complete = 0;
//                                //Log.e("PUSH.tag", "Has value " + elem.edtime);
//                            }
//                            vpmViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonvpm.setText("Sent " + d.length + " VPM record(s)");
//                            buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonvpm.setText("Failed to Send Data: Error "+ response.code());
//                            buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Vpm>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonvpm.setText("Failed to Send VPM Data");
//                        buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });

//        //PUSH SES DATA
//        final HdssSociodemoViewModel hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<HdssSociodemo> hdssSociodemoList = new ArrayList<>();
//        try {
//            hdssSociodemoList.addAll(hdssSociodemoViewModel.findToSync());
//            buttonSendSocio.setText("Profiles[SES] (" + hdssSociodemoList.size() + ") to send");
//            buttonSendSocio.setTextColor(Color.WHITE);
//            if (hdssSociodemoList.isEmpty()) {
//                buttonSendSocio.setVisibility(View.GONE);
//                //textViewSendSocio.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendSocio.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<HdssSociodemo> data = new DataWrapper<>(hdssSociodemoList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<HdssSociodemo>> c_callable = dao.sendSociodata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, Response<DataWrapper<HdssSociodemo>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            HdssSociodemo[] d = data.getData().toArray(new HdssSociodemo[0]);
//
//                            for (HdssSociodemo elem : d) {
//                                elem.complete = 0;
//                            }
//                            hdssSociodemoViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendSocio.setText("Sent " + d.length + " record(s)");
//                            buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendSocio.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendSocio.setText("Failed to Send SES Data");
//                        buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to send SES Data", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, t.getMessage());
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Residency
//        final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
//        //GET MODIFIED DATA
//        final List<Residency> residencyList = new ArrayList<>();
//        try {
//            residencyList.addAll(residencyViewModel.findToSync());
//            buttonSendRes.setText("Residency (" + residencyList.size() + ") to send");
//            buttonSendRes.setTextColor(Color.WHITE);
//            if (residencyList.isEmpty()) {
//                buttonSendRes.setVisibility(View.GONE);
//                //textViewSendRes.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendRes.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Residency> data = new DataWrapper<>(residencyList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Residency>> c_callable = dao.sendResidencydata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Residency[] d = data.getData().toArray(new Residency[0]);
//
//                            for (Residency elem : d) {
//                                elem.complete = 0;
//                            }
//                            residencyViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendRes.setText("Sent " + d.length + " Residency record(s)");
//                            buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendRes.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        Toast.makeText(PushActivity.this, "Failed to Send Residency Data", Toast.LENGTH_LONG).show();
//                        buttonSendRes.setText("Failed to Send Residency Data");
//                        buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Inmigration
//        final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
//        //GET MODIFIED DATA
//        final List<Inmigration> inmigrationList = new ArrayList<>();
//        try {
//            inmigrationList.addAll(inmigrationViewModel.findToSync());
//            buttonSendImg.setText("Inmigration (" + inmigrationList.size() + ") to send");
//            buttonSendImg.setTextColor(Color.WHITE);
//            if (inmigrationList.isEmpty()) {
//                buttonSendImg.setVisibility(View.GONE);
//                //textViewSendImg.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendImg.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Inmigration> data = new DataWrapper<>(inmigrationList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Inmigration>> c_callable = dao.sendInmigrationdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Inmigration[] d = data.getData().toArray(new Inmigration[0]);
//
//                            for (Inmigration elem : d) {
//                                elem.complete = 0;
//                            }
//                            inmigrationViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendImg.setText("Sent " + d.length + " Inmigration record(s)");
//                            buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendImg.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendImg.setText("Failed to Send Inmigration Data");
//                        buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Inmigration Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Outmigration
//        final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Outmigration> outmigrationList = new ArrayList<>();
//        try {
//            outmigrationList.addAll(outmigrationViewModel.findToSync());
//            buttonSendOmg.setText("Outmigration (" + outmigrationList.size() + ") to send");
//            buttonSendOmg.setTextColor(Color.WHITE);
//            if (outmigrationList.isEmpty()) {
//                buttonSendOmg.setVisibility(View.GONE);
//                //textViewSendOmg.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendOmg.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Outmigration> data = new DataWrapper<>(outmigrationList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Outmigration>> c_callable = dao.sendOutmigrationdata(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Outmigration[] d = data.getData().toArray(new Outmigration[0]);
//
//                            for (Outmigration elem : d) {
//                                elem.complete = 0;
//                            }
//                            outmigrationViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendOmg.setText("Sent " + d.length + " Outmigration record(s)");
//                            buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendOmg.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendOmg.setText("Failed to Send Outmigration Data");
//                        buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Outmigration Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Amendment
//        final AmendmentViewModel amendmentViewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Amendment> amendmentList = new ArrayList<>();
//        try {
//            amendmentList.addAll(amendmentViewModel.findToSync());
//            buttonSendAmend.setText("Amendment (" + amendmentList.size() + ") to send");
//            buttonSendAmend.setTextColor(Color.WHITE);
//            if (amendmentList.isEmpty()) {
//                buttonSendAmend.setVisibility(View.GONE);
//                //textViewSendAmend.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendAmend.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Amendment> data = new DataWrapper<>(amendmentList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Amendment>> c_callable = dao.sendAmendment(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Amendment>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Amendment>> call, Response<DataWrapper<Amendment>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Amendment[] d = data.getData().toArray(new Amendment[0]);
//
//                            for (Amendment elem : d) {
//                                elem.complete = 0;
//                            }
//                            amendmentViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendAmend.setText("Sent " + d.length + " Amendment record(s)");
//                            buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendAmend.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Amendment>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendAmend.setText("Failed to Send Amendment Data");
//                        buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Amendment Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Vaccination
//        final VaccinationViewModel vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Vaccination> vaccinationList = new ArrayList<>();
//        try {
//            vaccinationList.addAll(vaccinationViewModel.findToSync());
//            buttonSendVac.setText("Vaccination (" + vaccinationList.size() + ") to send");
//            buttonSendVac.setTextColor(Color.WHITE);
//            if (vaccinationList.isEmpty()) {
//                buttonSendVac.setVisibility(View.GONE);
//                //textViewSendVac.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendVac.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Vaccination> data = new DataWrapper<>(vaccinationList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Vaccination>> c_callable = dao.sendVaccination(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Vaccination>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Vaccination>> call, Response<DataWrapper<Vaccination>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Vaccination[] d = data.getData().toArray(new Vaccination[0]);
//
//                            for (Vaccination elem : d) {
//                                elem.complete = 0;
//                            }
//                            vaccinationViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendVac.setText("Sent " + d.length + " Vaccination record(s)");
//                            buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendVac.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendVac.setText("Failed to Send Vaccination Data");
//                        buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Vaccination Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });

//        //PUSH Duplicates
//        final DuplicateViewModel duplicateViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);
//        //GET MODIFIED DATA
//        final List<Duplicate> duplicateList = new ArrayList<>();
//        try {
//            duplicateList.addAll(duplicateViewModel.findToSync());
//            buttonSendDup.setText("Duplicate (" + duplicateList.size() + ") to send");
//            buttonSendDup.setTextColor(Color.WHITE);
//            if (duplicateList.isEmpty()) {
//                buttonSendDup.setVisibility(View.GONE);
//                //textViewSendDup.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendDup.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Duplicate> data = new DataWrapper<>(duplicateList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Duplicate>> c_callable = dao.sendDup(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Duplicate>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Duplicate>> call, Response<DataWrapper<Duplicate>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Duplicate[] d = data.getData().toArray(new Duplicate[0]);
//
//                            for (Duplicate elem : d) {
//                                elem.complete = 0;
//                            }
//                            duplicateViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendDup.setText("Sent " + d.length + " Duplicate record(s)");
//                            buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendDup.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendDup.setText("Failed to Send Vaccination Data");
//                        buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Vaccination Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Community
//        final CommunityViewModel communityViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
//        //GET MODIFIED DATA
//        final List<CommunityReport> communityReportList = new ArrayList<>();
//        try {
//            communityReportList.addAll(communityViewModel.retrieveToSync());
//            buttonSendcom.setText("Community Report (" + communityReportList.size() + ") to send");
//            buttonSendcom.setTextColor(Color.WHITE);
//            if (communityReportList.isEmpty()) {
//                buttonSendcom.setVisibility(View.GONE);
//                //textViewSendDup.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendcom.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<CommunityReport> data = new DataWrapper<>(communityReportList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<CommunityReport>> c_callable = dao.sendCommunity(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<CommunityReport>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<CommunityReport>> call, Response<DataWrapper<CommunityReport>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            CommunityReport[] d = data.getData().toArray(new CommunityReport[0]);
//
//                            for (CommunityReport elem : d) {
//                                elem.complete = 0;
//                            }
//                            communityViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendcom.setText("Sent " + d.length + " Community record(s)");
//                            buttonSendcom.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendcom.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendcom.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<CommunityReport>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendcom.setText("Failed to Send Community Report Data");
//                        buttonSendcom.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Community Report Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });



//        //PUSH Registry
//        final RegistryViewModel registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Registry> registryList = new ArrayList<>();
//        try {
//            registryList.addAll(registryViewModel.findToSync());
//            buttonSendreg.setText("Registry Report (" + registryList.size() + ") to send");
//            buttonSendreg.setTextColor(Color.WHITE);
//            if (registryList.isEmpty()) {
//                buttonSendreg.setVisibility(View.GONE);
//                //textViewSendDup.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendreg.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Registry> data = new DataWrapper<>(registryList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Registry>> c_callable = dao.sendRegistry(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Registry>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Registry>> call, Response<DataWrapper<Registry>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            // Log the entire response body for debugging
//                            String responseBodyJson = new Gson().toJson(response.body());
//                            Log.d("API Response", "Response Body: " + responseBodyJson);
//
//                            Registry[] d = data.getData().toArray(new Registry[0]);
//
//                            for (Registry elem : d) {
//                                elem.complete = 0;
//                                Log.d("Sync", "Registry Insert Date: " + elem.insertDate);
//                            }
//                            registryViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendreg.setText("Sent " + d.length + " Registry record(s)");
//                            buttonSendreg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendreg.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendreg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Registry>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendreg.setText("Failed to Send Registry Report Data");
//                        buttonSendreg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Registry Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });


//        //PUSH Morbidity
//        final Button buttonSendmor = findViewById(R.id.buttonSendMor);
//        //final TextView textViewSendDup = findViewById(R.id.textViewSendDup);
//        final MorbidityViewModel morbidityViewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);
//
//        //GET MODIFIED DATA
//        final List<Morbidity> morbidityList = new ArrayList<>();
//        try {
//            morbidityList.addAll(morbidityViewModel.retrieveToSync());
//            buttonSendmor.setText("Morbidity Report (" + morbidityList.size() + ") to send");
//            buttonSendmor.setTextColor(Color.WHITE);
//            if (morbidityList.isEmpty()) {
//                buttonSendmor.setVisibility(View.GONE);
//                //textViewSendDup.setVisibility(View.GONE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendmor.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            //WRAP THE DATA
//            final DataWrapper<Morbidity> data = new DataWrapper<>(morbidityList);
//
//            //SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//
//                final Call<DataWrapper<Morbidity>> c_callable = dao.sendMorbidity(authorizationHeader,data);
//                c_callable.enqueue(new Callback<DataWrapper<Morbidity>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Morbidity>> call, Response<DataWrapper<Morbidity>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Morbidity[] d = data.getData().toArray(new Morbidity[0]);
//
//                            for (Morbidity elem : d) {
//                                elem.complete = 0;
//
//                            }
//                            morbidityViewModel.add(d);
//
//                            progress.dismiss();
//                            buttonSendmor.setText("Sent " + d.length + " Morbidity record(s)");
//                            buttonSendmor.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        }else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendmor.setText("Failed to Send Data: Error "+ response.code());
//                            buttonSendmor.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendmor.setText("Failed to Send Morbidity Report Data");
//                        buttonSendmor.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to Send Morbidity Report Data", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//
//        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        // Perform heavy tasks in the background
        new Thread(() -> {
            // Perform heavy tasks (e.g., retrieving data in the background)
            if (!checkInternetAndNotify()) {
                return;  // Exit the thread if no internet
            }
            initializeMorbidityReport();
            initializeLocationReport();
            initializeVisitReport();
            initializeListingReport();
            initializeIndividualReport();
            initializeSocialgroupReport();
            initializeRelationshipReport();
            initializePregnancyReport();
            initializePregnancyoutcomeReport();
            initializeOutcomeReport();
            initializeDemographicReport();
            initializeDeathReport();
            initializeVpmReport();
            initializeSesReport();
            initializeResidencyReport();
            initializeInmigrationReport();
            initializeOutmigrationReport();
            initializeAmendmentReport();
            initializeVaccinationReport();
            initializeDuplicateReport();
            initializeCommunityReport();
            initializeRegistryReport();

            // Now update the UI on the main thread after the heavy tasks are complete
            runOnUiThread(() -> {
                // UI updates go here, such as refreshing views, showing results, etc.
                //refreshUIAfterDataFetch();
            });
        }).start();
    }

    private boolean checkInternetAndNotify() {
        if (!isInternetAvailable()) {
            // Ensure the Toast runs on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
            });
            return false;  // Internet is not available
        }
        return true;  // Internet is available
    }

    //SEND LOCATION DATA
    private void initializeLocationReport() {
        // Check for internet connection on the main thread
        if (!isInternetAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        // Move the heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel in the background
            final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
            final List<Locations> locationsList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                locationsList.addAll(locationViewModel.findToSync());

                // Update the UI after fetching the data
                runOnUiThread(() -> {
                    buttonSendLocationdata.setText("Locations(" + locationsList.size() + ") to send");
                    //buttonSendLocationdata.setTextColor(Color.WHITE);

                    if (locationsList.isEmpty()) {
                        buttonSendLocationdata.setVisibility(View.GONE);
                    } else {
                        buttonSendLocationdata.setVisibility(View.VISIBLE);
                    }

                    buttonSendLocationdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Locations> data = new DataWrapper<>(locationsList);

                        // Perform the network operation in the background
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                Call<DataWrapper<Locations>> c_callable = dao.sendLocationdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Locations>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Locations>> call, @NonNull Response<DataWrapper<Locations>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Locations[] d = data.getData().toArray(new Locations[0]);
                                                for (Locations elem : d) {
                                                    elem.setComplete(0);
                                                }
                                                locationViewModel.add(d);

                                                buttonSendLocationdata.setText("Sent " + d.length + " record(s)");
                                                buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendLocationdata.setText("Failed to Send Location Data: Error " + response.code());
                                                buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Locations>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendLocationdata.setText("Failed to Send Location Data");
                                            buttonSendLocationdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Location Data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                progress.dismiss();
                            }
                        }).start();  // Start the network operation in a new background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start the data fetching in a new background thread
    }


    //SEND VISIT DATA
//    private void initializeVisitReport() {
//        // Initialize the ViewModel
//        final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
//        // GET MODIFIED DATA
//        final List<Visit> visitList = new ArrayList<>();
//
//        try {
//            visitList.addAll(visitViewModel.findToSync());
//            buttonSendVisit.setText("Visit(" + visitList.size() + ") to send");
//            buttonSendVisit.setTextColor(Color.WHITE);
//
//            if (visitList.isEmpty()) {
//                buttonSendVisit.setVisibility(View.GONE);
//                // Optional: Hide any other associated views if necessary
//            }else{
//                buttonSendVisit.setVisibility(View.VISIBLE);
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        buttonSendVisit.setOnClickListener(v -> {
//            progress.setMessage(getResources().getString(R.string.init_syncing));
//            progress.show();
//
//            // WRAP THE DATA
//            final DataWrapper<Visit> data = new DataWrapper<>(visitList);
//
//            // SEND THE DATA
//            if (data.getData() != null && !data.getData().isEmpty()) {
//                progress.setMessage("Sending " + data.getData().size() + " record(s)...");
//
//                final Call<DataWrapper<Visit>> c_callable = dao.sendVisitdata(authorizationHeader, data);
//                c_callable.enqueue(new Callback<DataWrapper<Visit>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<DataWrapper<Visit>> call, Response<DataWrapper<Visit>> response) {
//                        if (response != null && response.body() != null && response.isSuccessful()
//                                && response.body().getData() != null && !response.body().getData().isEmpty()) {
//
//                            Visit[] d = data.getData().toArray(new Visit[0]);
//
//                            for (Visit elem : d) {
//                                elem.complete = 0;  // Set the complete status
//                            }
//                            visitViewModel.add(d);  // Add visits to the ViewModel
//
//                            progress.dismiss();
//                            buttonSendVisit.setText("Sent " + d.length + " record(s)");
//                            buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
//                        } else {
//                            // Handle the case where the server responds with an error
//                            progress.dismiss();
//                            buttonSendVisit.setText("Failed to Send Visit Data: Error " + response.code());
//                            buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                            Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<DataWrapper<Visit>> call, @NonNull Throwable t) {
//                        progress.dismiss();
//                        buttonSendVisit.setText("Failed to Send Visit Data");
//                        buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
//                        Toast.makeText(PushActivity.this, "Failed to send Visit data", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, t.getMessage());
//                    }
//                });
//            } else {
//                progress.dismiss();
//            }
//        });
//    }
    private void initializeVisitReport() {
        if (!isInternetAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        // Move the heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel in the background
            final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
            final List<Visit> visitList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                visitList.addAll(visitViewModel.findToSync());

                // Update the UI after fetching the data
                runOnUiThread(() -> {
                    buttonSendVisit.setText("Visit(" + visitList.size() + ") to send");
                    //buttonSendVisit.setTextColor(Color.WHITE);

                    if (visitList.isEmpty()) {
                        buttonSendVisit.setVisibility(View.GONE);
                    } else {
                        buttonSendVisit.setVisibility(View.VISIBLE);
                    }

                    buttonSendVisit.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Visit> data = new DataWrapper<>(visitList);

                        // Perform the network operation in the background
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                Call<DataWrapper<Visit>> c_callable = dao.sendVisitdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Visit>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Visit>> call, @NonNull Response<DataWrapper<Visit>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Visit[] d = data.getData().toArray(new Visit[0]);
                                                for (Visit elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                visitViewModel.add(d);  // Add visits to the ViewModel

                                                buttonSendVisit.setText("Sent " + d.length + " record(s)");
                                                buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendVisit.setText("Failed to Send Visit Data: Error " + response.code());
                                                buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Visit>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendVisit.setText("Failed to Send Visit Data");
                                            buttonSendVisit.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to send Visit data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation in a new background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start the data fetching in a new background thread
    }


    //    SEND LISTING DATA
    private void initializeListingReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        // Move the heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel in the background
            final ListingViewModel listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
            final List<Listing> listingList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                listingList.addAll(listingViewModel.findToSync());

                // Update the UI after fetching the data
                runOnUiThread(() -> {
                    buttonSendList.setText("Listing(" + listingList.size() + ") to send");
                    //buttonSendList.setTextColor(Color.WHITE);

                    if (listingList.isEmpty()) {
                        buttonSendList.setVisibility(View.GONE);
                    } else {
                        buttonSendList.setVisibility(View.VISIBLE);
                    }

                    buttonSendList.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Listing> data = new DataWrapper<>(listingList);

                        // Perform the network operation in the background
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                Call<DataWrapper<Listing>> c_callable = dao.sendListing(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Listing>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Listing>> call, @NonNull Response<DataWrapper<Listing>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Listing[] d = data.getData().toArray(new Listing[0]);
                                                for (Listing elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                listingViewModel.add(d);  // Add listings to the ViewModel

                                                buttonSendList.setText("Sent " + d.length + " record(s)");
                                                buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendList.setText("Failed to Send Listing Data: Error " + response.code());
                                                buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Listing>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendList.setText("Failed to Send Listing Data");
                                            buttonSendList.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to send Listing data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation in a new background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start the data fetching in a new background thread
    }


    //PUSH INDIVIDUAL
    private void initializeIndividualReport() {
        if (!isInternetAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        // Move the heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel in the background
            final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
            final List<Individual> individualList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                individualList.addAll(individualViewModel.findToSync());

                // Update the UI after fetching the data
                runOnUiThread(() -> {
                    buttonSendIndividualdata.setText("Individuals(" + individualList.size() + ") to send");
                    //buttonSendIndividualdata.setTextColor(Color.WHITE);

                    if (individualList.isEmpty()) {
                        buttonSendIndividualdata.setVisibility(View.GONE);
                    } else {
                        buttonSendIndividualdata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendIndividualdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Individual> data = new DataWrapper<>(individualList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                final Call<DataWrapper<Individual>> c_callable = dao.sendIndividualdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Individual>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Individual>> call, @NonNull Response<DataWrapper<Individual>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Individual[] d = data.getData().toArray(new Individual[0]);
                                                for (Individual elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                individualViewModel.add(d);  // Add individuals to the ViewModel

                                                buttonSendIndividualdata.setText("Sent " + d.length + " Individual record(s)");
                                                buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendIndividualdata.setText("Failed to Send Individual Data: Error " + response.code());
                                                buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Individual>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendIndividualdata.setText("Failed to Send Individual Data");
                                            buttonSendIndividualdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Individual Data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start network operation in a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a new background thread
    }

    //PUSH SOCIALGROUP
    private void initializeSocialgroupReport() {
        if (!isInternetAvailable()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
            final List<Socialgroup> socialgroupList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                socialgroupList.addAll(socialgroupViewModel.findToSync());

                // Update UI after fetching data
                runOnUiThread(() -> {
                    buttonSendSocialgroupdata.setText("Socialgroups(" + socialgroupList.size() + ") to send");
                    //buttonSendSocialgroupdata.setTextColor(Color.WHITE);

                    if (socialgroupList.isEmpty()) {
                        buttonSendSocialgroupdata.setVisibility(View.GONE);
                    } else {
                        buttonSendSocialgroupdata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendSocialgroupdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Socialgroup> data = new DataWrapper<>(socialgroupList);

                        // Move the network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                final Call<DataWrapper<Socialgroup>> c_callable = dao.sendSocialgroupdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Socialgroup>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Socialgroup>> call, @NonNull Response<DataWrapper<Socialgroup>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Socialgroup[] d = data.getData().toArray(new Socialgroup[0]);
                                                for (Socialgroup elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                socialgroupViewModel.add(d);

                                                buttonSendSocialgroupdata.setText("Sent " + d.length + " Socialgroup record(s)");
                                                buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendSocialgroupdata.setText("Failed to Send Socialgroup Data: Error " + response.code());
                                                buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Socialgroup>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendSocialgroupdata.setText("Failed to Send Socialgroup Data");
                                            buttonSendSocialgroupdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Socialgroup Data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //SEND RELATIONSHIP
    private void initializeRelationshipReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final RelationshipViewModel relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
            final List<Relationship> relationshipList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                relationshipList.addAll(relationshipViewModel.findToSync());

                // Update the UI based on data fetched
                runOnUiThread(() -> {
                    buttonSendRelationshipdata.setText("Relationship (" + relationshipList.size() + ") to send");
                    //buttonSendRelationshipdata.setTextColor(Color.WHITE);

                    if (relationshipList.isEmpty()) {
                        buttonSendRelationshipdata.setVisibility(View.GONE);
                    } else {
                        buttonSendRelationshipdata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendRelationshipdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Relationship> data = new DataWrapper<>(relationshipList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                final Call<DataWrapper<Relationship>> c_callable = dao.sendRelationshipdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Response<DataWrapper<Relationship>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Relationship[] d = data.getData().toArray(new Relationship[0]);

                                                for (Relationship elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                relationshipViewModel.add(d);  // Add relationships to the ViewModel

                                                buttonSendRelationshipdata.setText("Sent " + d.length + " Relationship record(s)");
                                                buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendRelationshipdata.setText("Failed to Send Relationship Data: Error " + response.code());
                                                buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendRelationshipdata.setText("Failed to Send Relationship Data");
                                            buttonSendRelationshipdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Relationship Data", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH PREGNANCY
    private void initializePregnancyReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
            final List<Pregnancy> pregnancyList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                pregnancyList.addAll(pregnancyViewModel.findToSync());

                // Update the UI based on data fetched
                runOnUiThread(() -> {
                    buttonSendPregnancydata.setText("Pregnancy (" + pregnancyList.size() + ") to send");
                    //buttonSendPregnancydata.setTextColor(Color.WHITE);

                    if (pregnancyList.isEmpty()) {
                        buttonSendPregnancydata.setVisibility(View.GONE);
                    } else {
                        buttonSendPregnancydata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendPregnancydata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Pregnancy> data = new DataWrapper<>(pregnancyList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                final Call<DataWrapper<Pregnancy>> c_callable = dao.sendPregnancydata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Response<DataWrapper<Pregnancy>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Pregnancy[] d = data.getData().toArray(new Pregnancy[0]);

                                                for (Pregnancy elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                pregnancyViewModel.add(d);  // Add pregnancies to the ViewModel

                                                buttonSendPregnancydata.setText("Sent " + d.length + " Pregnancy record(s)");
                                                buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendPregnancydata.setText("Failed to Send Pregnancy Data: Error " + response.code());
                                                buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendPregnancydata.setText("Failed to Send Pregnancy Data");
                                            buttonSendPregnancydata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Pregnancy Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH PREGNANCY OUTCOME
    private void initializePregnancyoutcomeReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final PregnancyoutcomeViewModel pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
            final List<Pregnancyoutcome> pregnancyoutcomeList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                pregnancyoutcomeList.addAll(pregnancyoutcomeViewModel.findToSync());

                // Update the UI based on data fetched
                runOnUiThread(() -> {
                    buttonSendOutcomedata.setText("Pregnancy Outcome (" + pregnancyoutcomeList.size() + ") to send");
                    //buttonSendOutcomedata.setTextColor(Color.WHITE);

                    if (pregnancyoutcomeList.isEmpty()) {
                        buttonSendOutcomedata.setVisibility(View.GONE);
                    } else {
                        buttonSendOutcomedata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendOutcomedata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Pregnancyoutcome> data = new DataWrapper<>(pregnancyoutcomeList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                                // Network call via Retrofit
                                final Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.sendPregoutcomedata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Response<DataWrapper<Pregnancyoutcome>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Pregnancyoutcome[] d = data.getData().toArray(new Pregnancyoutcome[0]);

                                                for (Pregnancyoutcome elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                pregnancyoutcomeViewModel.add(d);  // Add pregnancy outcomes to the ViewModel

                                                buttonSendOutcomedata.setText("Sent " + d.length + " Pregnancy Outcome record(s)");
                                                buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendOutcomedata.setText("Failed to Send Pregnancy Outcome Data: Error " + response.code());
                                                buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendOutcomedata.setText("Failed to Send Pregnancy Outcome Data");
                                            buttonSendOutcomedata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Pregnancy Outcome Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }



    //PUSH OUTCOME
    private void initializeOutcomeReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
            final List<Outcome> outcomeList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                outcomeList.addAll(outcomeViewModel.findToSync());

                // Update the UI based on data fetched
                runOnUiThread(() -> {
                    buttonSendOutcomesdata.setText("Outcome (" + outcomeList.size() + ") to send");
                    //buttonSendOutcomesdata.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (outcomeList.isEmpty()) {
                        buttonSendOutcomesdata.setVisibility(View.GONE);
                    } else {
                        buttonSendOutcomesdata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendOutcomesdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Outcome> data = new DataWrapper<>(outcomeList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Outcome>> c_callable = dao.sendOutcomedata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Outcome>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Outcome>> call, Response<DataWrapper<Outcome>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Outcome[] d = data.getData().toArray(new Outcome[0]);

                                                for (Outcome elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                outcomeViewModel.add(d);  // Add outcomes to the ViewModel

                                                buttonSendOutcomesdata.setText("Sent " + d.length + " Outcome record(s)");
                                                buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendOutcomesdata.setText("Failed to Send Outcome Data: Error " + response.code());
                                                buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Outcome>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendOutcomesdata.setText("Failed to Send Outcome Data");
                                            buttonSendOutcomesdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Outcome Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH Demographic
    private void initializeDemographicReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final DemographicViewModel demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
            final List<Demographic> demographicList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                demographicList.addAll(demographicViewModel.findToSync());

                // Update the UI based on data fetched
                runOnUiThread(() -> {
                    buttonSendDemographicdata.setText("Demographic (" + demographicList.size() + ") to send");
                    //buttonSendDemographicdata.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (demographicList.isEmpty()) {
                        buttonSendDemographicdata.setVisibility(View.GONE);
                    } else {
                        buttonSendDemographicdata.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendDemographicdata.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Demographic> data = new DataWrapper<>(demographicList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Demographic>> c_callable = dao.sendDemographicdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Demographic[] d = data.getData().toArray(new Demographic[0]);

                                                for (Demographic elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                demographicViewModel.add(d);  // Add demographics to the ViewModel

                                                buttonSendDemographicdata.setText("Sent " + d.length + " Demographic record(s)");
                                                buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendDemographicdata.setText("Failed to Send Demographic Data: Error " + response.code());
                                                buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendDemographicdata.setText("Failed to Send Demographic Data");
                                            buttonSendDemographicdata.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Demographic Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH Death
    private void initializeDeathReport() {
        // Check for internet connectivity
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final DeathViewModel deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
            final List<Death> deathList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                deathList.addAll(deathViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttondth.setText("Death (" + deathList.size() + ") to send");
                    //buttondth.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (deathList.isEmpty()) {
                        buttondth.setVisibility(View.GONE);
                    } else {
                        buttondth.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttondth.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Death> data = new DataWrapper<>(deathList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Death>> c_callable = dao.sendDeathdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null) {
                                                Death[] d = data.getData().toArray(new Death[0]);

                                                for (Death elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                deathViewModel.add(d);  // Add death records to the ViewModel

                                                buttondth.setText("Sent " + d.length + " Death record(s)");
                                                buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttondth.setText("Failed to Send Death Data: Error " + response.code());
                                                buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttondth.setText("Failed to Send Death Data");
                                            buttondth.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Death Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH Vpm
    private void initializeVpmReport() {
        // Check for internet connectivity
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final VpmViewModel vpmViewModel = new ViewModelProvider(this).get(VpmViewModel.class);
            final List<Vpm> vpmList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                vpmList.addAll(vpmViewModel.retrieveToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonvpm.setText("Vpm(" + vpmList.size() + ") to send");
                    //buttonvpm.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (vpmList.isEmpty()) {
                        buttonvpm.setVisibility(View.GONE);
                    } else {
                        buttonvpm.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonvpm.setOnClickListener(v1 -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Vpm> data = new DataWrapper<>(vpmList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Vpm>> c_callable = dao.sendVpmdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Vpm>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Vpm>> call, Response<DataWrapper<Vpm>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null) {
                                                Vpm[] d = data.getData().toArray(new Vpm[0]);

                                                for (Vpm elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                vpmViewModel.add(d);  // Add Vpm records to the ViewModel

                                                buttonvpm.setText("Sent " + d.length + " Vpm record(s)");
                                                buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonvpm.setText("Failed to Send Vpm Data: Error " + response.code());
                                                buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Vpm>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonvpm.setText("Failed to Send Vpm Data");
                                            buttonvpm.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Vpm Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //PUSH HdssSociodemo
    private void initializeSesReport() {
        // Check for internet connectivity
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final HdssSociodemoViewModel hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
            final List<HdssSociodemo> hdssSociodemoList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                hdssSociodemoList.addAll(hdssSociodemoViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendSocio.setText("Profiles[SES](" + hdssSociodemoList.size() + ") to send");
                    //buttonSendSocio.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (hdssSociodemoList.isEmpty()) {
                        buttonSendSocio.setVisibility(View.GONE);
                    } else {
                        buttonSendSocio.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendSocio.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<HdssSociodemo> data = new DataWrapper<>(hdssSociodemoList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<HdssSociodemo>> c_callable = dao.sendSociodata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, Response<DataWrapper<HdssSociodemo>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                HdssSociodemo[] d = data.getData().toArray(new HdssSociodemo[0]);

                                                for (HdssSociodemo elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                hdssSociodemoViewModel.add(d);  // Add profiles to the ViewModel

                                                buttonSendSocio.setText("Sent " + d.length + " Profiles[SES] record(s)");
                                                buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendSocio.setText("Failed to Send Profiles[SES] Data: Error " + response.code());
                                                buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendSocio.setText("Failed to Send Profiles[SES] Data");
                                            buttonSendSocio.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Profiles[SES] Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }

    //PUSH RESIDENCY
    private void initializeResidencyReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
            final List<Residency> residencyList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                residencyList.addAll(residencyViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendRes.setText("Residency (" + residencyList.size() + ") to send");
                    //buttonSendRes.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (residencyList.isEmpty()) {
                        buttonSendRes.setVisibility(View.GONE);
                    } else {
                        buttonSendRes.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendRes.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Residency> data = new DataWrapper<>(residencyList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Residency>> c_callable = dao.sendResidencydata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Residency[] d = data.getData().toArray(new Residency[0]);

                                                for (Residency elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                residencyViewModel.add(d);  // Add residencies to the ViewModel

                                                buttonSendRes.setText("Sent " + d.length + " Residency record(s)");
                                                buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendRes.setText("Failed to Send Residency Data: Error " + response.code());
                                                buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendRes.setText("Failed to Send Residency Data");
                                            buttonSendRes.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Residency Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //    SEND INMIGRATION
    private void initializeInmigrationReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
            final List<Inmigration> inmigrationList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                inmigrationList.addAll(inmigrationViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendImg.setText("Inmigration (" + inmigrationList.size() + ") to send");
                    //buttonSendImg.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (inmigrationList.isEmpty()) {
                        buttonSendImg.setVisibility(View.GONE);
                    } else {
                        buttonSendImg.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendImg.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Inmigration> data = new DataWrapper<>(inmigrationList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Inmigration>> c_callable = dao.sendInmigrationdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Inmigration[] d = data.getData().toArray(new Inmigration[0]);

                                                for (Inmigration elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                inmigrationViewModel.add(d);  // Add inmigrations to the ViewModel

                                                buttonSendImg.setText("Sent " + d.length + " Inmigration record(s)");
                                                buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendImg.setText("Failed to Send Data: Error " + response.code());
                                                buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendImg.setText("Failed to Send Inmigration Data");
                                            buttonSendImg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Inmigration Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //SEND OUTMIGRATION
    private void initializeOutmigrationReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Move heavy operations to a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
            final List<Outmigration> outmigrationList = new ArrayList<>();

            try {
                // GET MODIFIED DATA
                outmigrationList.addAll(outmigrationViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendOmg.setText("Outmigration (" + outmigrationList.size() + ") to send");
                    //buttonSendOmg.setTextColor(Color.WHITE);

                    // Handle button visibility
                    if (outmigrationList.isEmpty()) {
                        buttonSendOmg.setVisibility(View.GONE);
                    } else {
                        buttonSendOmg.setVisibility(View.VISIBLE);
                    }

                    // Set up the button click listener for sending data
                    buttonSendOmg.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Outmigration> data = new DataWrapper<>(outmigrationList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Outmigration>> c_callable = dao.sendOutmigrationdata(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null) {
                                                Outmigration[] d = data.getData().toArray(new Outmigration[0]);

                                                for (Outmigration elem : d) {
                                                    elem.complete = 0;  // Set the complete status
                                                }
                                                outmigrationViewModel.add(d);  // Add outmigrations to the ViewModel

                                                buttonSendOmg.setText("Sent " + d.length + " Outmigration record(s)");
                                                buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendOmg.setText("Failed to Send Data: Error " + response.code());
                                                buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendOmg.setText("Failed to Send Outmigration Data");
                                            buttonSendOmg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Outmigration Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //SEND AMENDMENT
    private void initializeAmendmentReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Fetch modified data in a background thread
        new Thread(() -> {
            // Initialize the ViewModel
            final AmendmentViewModel amendmentViewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
            final List<Amendment> amendmentList = new ArrayList<>();

            try {
                amendmentList.addAll(amendmentViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendAmend.setText("Amendment (" + amendmentList.size() + ") to send");
                    //buttonSendAmend.setTextColor(Color.WHITE);

                    // Handle button visibility
                    buttonSendAmend.setVisibility(amendmentList.isEmpty() ? View.GONE : View.VISIBLE);

                    // Set up the button click listener for sending data
                    buttonSendAmend.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Amendment> data = new DataWrapper<>(amendmentList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Amendment>> c_callable = dao.sendAmendment(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Amendment>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Amendment>> call, Response<DataWrapper<Amendment>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Amendment[] d = data.getData().toArray(new Amendment[0]);

                                                for (Amendment elem : d) {
                                                    elem.complete = 0;  // Mark the record as complete
                                                }
                                                amendmentViewModel.add(d);  // Add the records back to the ViewModel

                                                buttonSendAmend.setText("Sent " + d.length + " Amendment record(s)");
                                                buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendAmend.setText("Failed to Send Data: Error " + response.code());
                                                buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Amendment>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendAmend.setText("Failed to Send Amendment Data");
                                            buttonSendAmend.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Amendment Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }



    //SEND VACCINATION
    private void initializeVaccinationReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Initialize the ViewModel
        final VaccinationViewModel vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        // Fetch modified data in a background thread
        new Thread(() -> {
            final List<Vaccination> vaccinationList = new ArrayList<>();

            try {
                vaccinationList.addAll(vaccinationViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendVac.setText("Vaccination (" + vaccinationList.size() + ") to send");
                    //buttonSendVac.setTextColor(Color.WHITE);

                    // Handle button visibility
                    buttonSendVac.setVisibility(vaccinationList.isEmpty() ? View.GONE : View.VISIBLE);

                    // Set up the button click listener for sending data
                    buttonSendVac.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Vaccination> data = new DataWrapper<>(vaccinationList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Vaccination>> c_callable = dao.sendVaccination(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Vaccination>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Vaccination>> call, Response<DataWrapper<Vaccination>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Vaccination[] d = data.getData().toArray(new Vaccination[0]);

                                                for (Vaccination elem : d) {
                                                    elem.complete = 0;  // Mark the record as complete
                                                }
                                                vaccinationViewModel.add(d);  // Add the records back to the ViewModel

                                                buttonSendVac.setText("Sent " + d.length + " Vaccination record(s)");
                                                buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                buttonSendVac.setText("Failed to Send Data: Error " + response.code());
                                                buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                                Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + response.code(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendVac.setText("Failed to Send Vaccination Data");
                                            buttonSendVac.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Vaccination Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }


    //SEND DUPLICATES
    private void initializeDuplicateReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Initialize the ViewModel
        final DuplicateViewModel duplicateViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);

        // Fetch modified data in a background thread
        new Thread(() -> {
            final List<Duplicate> duplicateList = new ArrayList<>();

            try {
                duplicateList.addAll(duplicateViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendDup.setText("Duplicate (" + duplicateList.size() + ") to send");
                    //buttonSendDup.setTextColor(Color.WHITE);

                    // Manage button visibility
                    buttonSendDup.setVisibility(duplicateList.isEmpty() ? View.GONE : View.VISIBLE);

                    // Set up the button click listener for sending data
                    buttonSendDup.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<Duplicate> data = new DataWrapper<>(duplicateList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<Duplicate>> c_callable = dao.sendDup(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<Duplicate>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<Duplicate>> call, Response<DataWrapper<Duplicate>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                Duplicate[] d = data.getData().toArray(new Duplicate[0]);

                                                for (Duplicate elem : d) {
                                                    elem.complete = 0;  // Mark as complete
                                                }
                                                duplicateViewModel.add(d);  // Add the updated records back to the ViewModel

                                                buttonSendDup.setText("Sent " + d.length + " Duplicate record(s)");
                                                buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                handleErrorResponse(response.code());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendDup.setText("Failed to Send Duplicate Data");
                                            buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Duplicate Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }

    // Helper method to handle error responses
    private void handleErrorResponse(int errorCode) {
        progress.dismiss();
        buttonSendDup.setText("Failed to Send Data: Error " + errorCode);
        buttonSendDup.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
        Toast.makeText(PushActivity.this, "Server Error: Failed to send data: Error " + errorCode, Toast.LENGTH_LONG).show();
    }



    //SEND COMMUNITY REPORT
    private void initializeCommunityReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show());
            return;
        }

        // Initialize the ViewModel
        final CommunityViewModel communityViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);

        // Fetch modified data in a background thread
        new Thread(() -> {
            final List<CommunityReport> communityReportList = new ArrayList<>();

            try {
                communityReportList.addAll(communityViewModel.retrieveToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendcom.setText("Community Report (" + communityReportList.size() + ") to send");
                    //buttonSendcom.setTextColor(Color.WHITE);

                    // Manage button visibility
                    buttonSendcom.setVisibility(communityReportList.isEmpty() ? View.GONE : View.VISIBLE);

                    // Set up the button click listener for sending data
                    buttonSendcom.setOnClickListener(v -> {
                        progress.setMessage(getResources().getString(R.string.init_syncing));
                        progress.show();

                        // WRAP THE DATA
                        final DataWrapper<CommunityReport> data = new DataWrapper<>(communityReportList);

                        // Move network operation to a background thread
                        new Thread(() -> {
                            if (data.getData() != null && !data.getData().isEmpty()) {
                                runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                                // Network call via Retrofit
                                final Call<DataWrapper<CommunityReport>> c_callable = dao.sendCommunity(authorizationHeader, data);
                                c_callable.enqueue(new Callback<DataWrapper<CommunityReport>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<DataWrapper<CommunityReport>> call, Response<DataWrapper<CommunityReport>> response) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                                CommunityReport[] d = data.getData().toArray(new CommunityReport[0]);

                                                for (CommunityReport elem : d) {
                                                    elem.complete = 0;  // Mark as complete
                                                }
                                                communityViewModel.add(d);  // Add the updated records back to the ViewModel

                                                buttonSendcom.setText("Sent " + d.length + " Community record(s)");
                                                buttonSendcom.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                            } else {
                                                // Handle server error using the existing method
                                                handleErrorResponse(response.code());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<DataWrapper<CommunityReport>> call, @NonNull Throwable t) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            buttonSendcom.setText("Failed to Send Community Report Data");
                                            buttonSendcom.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                            Toast.makeText(PushActivity.this, "Failed to Send Community Report Data", Toast.LENGTH_LONG).show();
                                            Log.e(TAG, t.getMessage());
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(progress::dismiss);
                            }
                        }).start();  // Start the network operation on a background thread
                    });
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }



    //SEND REGISTRY
    private void initializeRegistryReport() {
        // Check internet availability and show a toast if unavailable
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show());
            return;
        }

        // Initialize the ViewModel
        final RegistryViewModel registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);

        // Fetch modified data in a background thread
        new Thread(() -> {
            final List<Registry> registryList = new ArrayList<>();

            try {
                registryList.addAll(registryViewModel.findToSync());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    buttonSendreg.setText("Registry (" + registryList.size() + ") to send");
                    //buttonSendreg.setTextColor(Color.WHITE);

                    // Manage button visibility
                    buttonSendreg.setVisibility(registryList.isEmpty() ? View.GONE : View.VISIBLE);
                });

                // Set up the button click listener for sending data
                buttonSendreg.setOnClickListener(v -> {
                    progress.setMessage(getResources().getString(R.string.init_syncing));
                    progress.show();

                    // WRAP THE DATA
                    final DataWrapper<Registry> data = new DataWrapper<>(registryList);

                    // Move network operation to a background thread
                    new Thread(() -> {
                        if (data.getData() != null && !data.getData().isEmpty()) {
                            runOnUiThread(() -> progress.setMessage("Sending " + data.getData().size() + " record(s)..."));

                            // Network call via Retrofit
                            final Call<DataWrapper<Registry>> c_callable = dao.sendRegistry(authorizationHeader, data);
                            c_callable.enqueue(new Callback<DataWrapper<Registry>>() {
                                @Override
                                public void onResponse(@NonNull Call<DataWrapper<Registry>> call, Response<DataWrapper<Registry>> response) {
                                    runOnUiThread(() -> {
                                        progress.dismiss();
                                        if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                                            Registry[] d = data.getData().toArray(new Registry[0]);

                                            for (Registry elem : d) {
                                                elem.complete = 0;  // Set the complete status
                                            }
                                            registryViewModel.add(d);  // Add the updated records back to the ViewModel

                                            buttonSendreg.setText("Sent " + d.length + " Registry record(s)");
                                            buttonSendreg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
                                        } else {
                                            // Handle server error using the existing method
                                            handleErrorResponse(response.code());
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(@NonNull Call<DataWrapper<Registry>> call, @NonNull Throwable t) {
                                    runOnUiThread(() -> {
                                        progress.dismiss();
                                        buttonSendreg.setText("Failed to Send Registry Data");
                                        buttonSendreg.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
                                        Toast.makeText(PushActivity.this, "Failed to Send Registry Data", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, t.getMessage());
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(progress::dismiss);
                        }
                    }).start();  // Start the network operation on a background thread
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();  // Start data fetching in a background thread
    }



    //SEND MORBIDITY DATA
    private void initializeMorbidityReport() {
        // Check internet connection on the main thread
        if (!isInternetAvailable()) {
            runOnUiThread(() -> Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show());
            return;
        }

        // Move data retrieval to a background thread
        new Thread(() -> {
            MorbidityViewModel morbidityViewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);
            List<Morbidity> morbidityList = new ArrayList<>();

            try {
                morbidityList.addAll(morbidityViewModel.retrieveToSync());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // Update UI after fetching data
            runOnUiThread(() -> {
                updateUIForMorbidity(morbidityList);

                buttonSendmor.setOnClickListener(v -> {
                    if (!morbidityList.isEmpty()) {
                        sendMorbidityData(morbidityList, morbidityViewModel);
                    }
                });
            });
        }).start();
    }

    private void updateUIForMorbidity(List<Morbidity> morbidityList) {
        buttonSendmor.setText("Morbidity Report (" + morbidityList.size() + ") to send");
        //buttonSendmor.setTextColor(Color.WHITE);
        buttonSendmor.setVisibility(morbidityList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void sendMorbidityData(List<Morbidity> morbidityList, MorbidityViewModel morbidityViewModel) {
        progress.setMessage(getResources().getString(R.string.init_syncing));
        progress.show();

        DataWrapper<Morbidity> data = new DataWrapper<>(morbidityList);

        if (data.getData() != null && !data.getData().isEmpty()) {
            progress.setMessage("Sending " + data.getData().size() + " record(s)...");

            Call<DataWrapper<Morbidity>> c_callable = dao.sendMorbidity(authorizationHeader, data);
            c_callable.enqueue(new Callback<DataWrapper<Morbidity>>() {
                @Override
                public void onResponse(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Response<DataWrapper<Morbidity>> response) {
                    runOnUiThread(() -> {
                        progress.dismiss();
                        if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                            for (Morbidity elem : morbidityList) {
                                elem.complete = 0;
                            }
                            morbidityViewModel.add(morbidityList.toArray(new Morbidity[0]));
                            updateSendButtonUI(true, morbidityList.size());
                        } else {
                            updateSendButtonUI(false, response.code());
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<DataWrapper<Morbidity>> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        progress.dismiss();
                        updateSendButtonUI(false, -1);
                    });
                }
            });
        } else {
            progress.dismiss();
        }
    }

    private void updateSendButtonUI(boolean success, int recordCount) {
        if (success) {
            buttonSendmor.setText("Sent " + recordCount + " Morbidity record(s)");
            buttonSendmor.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.LimeGreen));
        } else {
            buttonSendmor.setText("Failed to Send Morbidity Report Data");
            buttonSendmor.setTextColor(ContextCompat.getColor(PushActivity.this, R.color.Brunette));
            Toast.makeText(PushActivity.this, "Failed to Send Morbidity Report Data", Toast.LENGTH_LONG).show();
        }
    }


}