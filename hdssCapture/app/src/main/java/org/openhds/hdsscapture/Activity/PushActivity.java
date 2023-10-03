package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Amendment;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.Visit;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        progress = new ProgressDialog(PushActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();


        //PUSH LOCATION
        final Button buttonSendLocationdata = findViewById(R.id.buttonSendLocation);
        //final TextView textViewSendLocationdata = findViewById(R.id.textViewSendLocation);
        final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //GET MODIFIED DATA
        final List<Locations> locationsList = new ArrayList<>();
        try {
            locationsList.addAll(locationViewModel.findToSync());
            buttonSendLocationdata.setText("Locations(" + locationsList.size() + ") to send");
            buttonSendLocationdata.setTextColor(Color.BLACK);
            if (locationsList.isEmpty()) {
                buttonSendLocationdata.setVisibility(View.GONE);
                //textViewSendLocationdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendLocationdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Locations> data = new DataWrapper<>(locationsList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Locations>> c_callable = dao.sendLocationdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Locations>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Locations>> call, Response<DataWrapper<Locations>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Locations[] d = data.getData().toArray(new Locations[0]);
                            for (Locations elem : d) {
                                elem.setComplete(2);
                                Log.e("PUSH.tag", "Has value " + elem.getCompno());
                            }
                            locationViewModel.add(d);


                            progress.dismiss();
                            buttonSendLocationdata.setText("Sent " + d.length + " record(s)");
                            //textViewSendLocationdata.setTextColor(Color.rgb(0, 114, 133));
                            buttonSendLocationdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Locations>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH VISIT DATA
        final Button buttonSendVisit = findViewById(R.id.buttonSendVisit);
        //final TextView textViewSendVisit = findViewById(R.id.textViewSendVisit);
        final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);

        //GET MODIFIED DATA
        final List<Visit> visitList = new ArrayList<>();
        try {
            visitList.addAll(visitViewModel.findToSync());
            buttonSendVisit.setText("Visit(" + visitList.size() + ") to send");
            buttonSendVisit.setTextColor(Color.BLACK);
            if (visitList.isEmpty()) {
                buttonSendVisit.setVisibility(View.GONE);
               // textViewSendVisit.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendVisit.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Visit> data = new DataWrapper<>(visitList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Visit>> c_callable = dao.sendVisitdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Visit>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Visit>> call, Response<DataWrapper<Visit>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Visit[] d = data.getData().toArray(new Visit[0]);

                            for (Visit elem : d) {
                                elem.complete = 2;
                            }
                            visitViewModel.add(d);

                            progress.dismiss();
                            buttonSendVisit.setText("Sent " + d.length + " record(s)");
                            buttonSendVisit.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Visit>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Visitdata", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });
            } else {
                progress.dismiss();
            }

        });

        //PUSH LISTING DATA
        final Button buttonSendList = findViewById(R.id.buttonSendList);
        //final TextView textViewSendList = findViewById(R.id.textViewSendList);
        final ListingViewModel listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        //GET MODIFIED DATA
        final List<Listing> listingList = new ArrayList<>();
        try {
            listingList.addAll(listingViewModel.findToSync());
            buttonSendList.setText("Listing(" + listingList.size() + ") to send");
            buttonSendList.setTextColor(Color.BLACK);
            if (listingList.isEmpty()) {
                buttonSendList.setVisibility(View.GONE);
                //textViewSendList.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendList.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Listing> data = new DataWrapper<>(listingList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Listing>> c_callable = dao.sendListing(data);
                c_callable.enqueue(new Callback<DataWrapper<Listing>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Listing>> call, Response<DataWrapper<Listing>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Listing[] d = data.getData().toArray(new Listing[0]);

                            for (Listing elem : d) {
                                elem.complete = 2;
                            }
                            listingViewModel.add(d);

                            progress.dismiss();
                            buttonSendList.setText("Sent " + d.length + " record(s)");
                            buttonSendList.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Listing>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Listing", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });
            } else {
                progress.dismiss();
            }

        });

        //PUSH INDIVIDUAL
        final Button buttonSendIndividualdata = findViewById(R.id.buttonSendIndividual);
        //final TextView textViewSendIndividualdata = findViewById(R.id.textViewSendIndividual);
        final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        //GET MODIFIED DATA
        final List<Individual> individualList = new ArrayList<>();
        try {
            individualList.addAll(individualViewModel.findToSync());
            buttonSendIndividualdata.setText("Individuals(" + individualList.size() + ") to send");
            buttonSendIndividualdata.setTextColor(Color.BLACK);
            if (individualList.isEmpty()) {
                buttonSendIndividualdata.setVisibility(View.GONE);
                //textViewSendIndividualdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendIndividualdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Individual> data = new DataWrapper<>(individualList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

//                for (Individual elem : data.getData()) {
//                    elem.complete = 0;
//                }

                final Call<DataWrapper<Individual>> c_callable = dao.sendIndividualdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Individual>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Individual>> call, Response<DataWrapper<Individual>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Individual[] d = data.getData().toArray(new Individual[0]);
                            for (Individual elem : d) {
                                elem.complete = 2;
                            }
                            individualViewModel.add(d);

                            progress.dismiss();
                            buttonSendIndividualdata.setText("Sent " + d.length + " Individual record(s)");
                            buttonSendIndividualdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Individual>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH SOCIALGROUP
        final Button buttonSendSocialgroupdata = findViewById(R.id.buttonSendSocialgroup);
        //final TextView textViewSendSocialgroupdata = findViewById(R.id.textViewSendSocialgroup);
        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);

        //GET MODIFIED DATA
        final List<Socialgroup> socialgroupList = new ArrayList<>();
        try {
            socialgroupList.addAll(socialgroupViewModel.findToSync());
            buttonSendSocialgroupdata.setText("Socialgroup (" + socialgroupList.size() + ") to send");
            buttonSendSocialgroupdata.setTextColor(Color.BLACK);
            if (socialgroupList.isEmpty()) {
                buttonSendSocialgroupdata.setVisibility(View.GONE);
                //textViewSendSocialgroupdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendSocialgroupdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Socialgroup> data = new DataWrapper<>(socialgroupList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Socialgroup>> c_callable = dao.sendSocialgroupdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Socialgroup>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Socialgroup>> call, Response<DataWrapper<Socialgroup>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Socialgroup[] d = data.getData().toArray(new Socialgroup[0]);


                            for (Socialgroup elem : d) {
                                elem.complete = 2;
                            }
                            socialgroupViewModel.add(d);

                            progress.dismiss();
                            buttonSendSocialgroupdata.setText("Sent " + d.length + " Socialgroup record(s)");
                            buttonSendSocialgroupdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Socialgroup>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Relationship
        final Button buttonSendRelationshipdata = findViewById(R.id.buttonSendRelationship);
        //final TextView textViewSendRelationshipdata = findViewById(R.id.textViewSendRelationship);
        final RelationshipViewModel relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);

        //GET MODIFIED DATA
        final List<Relationship> relationshipList = new ArrayList<>();
        try {
            relationshipList.addAll(relationshipViewModel.findToSync());
            buttonSendRelationshipdata.setText("Relationship (" + relationshipList.size() + ") to send");
            buttonSendRelationshipdata.setTextColor(Color.BLACK);
            if (relationshipList.isEmpty()) {
                buttonSendRelationshipdata.setVisibility(View.GONE);
                //textViewSendRelationshipdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendRelationshipdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Relationship> data = new DataWrapper<>(relationshipList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Relationship>> c_callable = dao.sendRelationshipdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Relationship[] d = data.getData().toArray(new Relationship[0]);

                            for (Relationship elem : d) {
                                elem.complete = 0;
                            }
                            relationshipViewModel.add(d);

                            progress.dismiss();
                            buttonSendRelationshipdata.setText("Sent " + d.length + " Relationship record(s)");
                            buttonSendRelationshipdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Pregnancy
        final Button buttonSendPregnancydata = findViewById(R.id.buttonSendPregnancy);
        //final TextView textViewSendPregnancydata = findViewById(R.id.textViewSendPregnancy);
        final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        //GET MODIFIED DATA
        final List<Pregnancy> pregnancyList = new ArrayList<>();
        try {
            pregnancyList.addAll(pregnancyViewModel.findToSync());
            buttonSendPregnancydata.setText("Pregnancy (" + pregnancyList.size() + ") to send");
            buttonSendPregnancydata.setTextColor(Color.BLACK);
            if (pregnancyList.isEmpty()) {
                buttonSendPregnancydata.setVisibility(View.GONE);
                //textViewSendPregnancydata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendPregnancydata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Pregnancy> data = new DataWrapper<>(pregnancyList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Pregnancy>> c_callable = dao.sendPregnancydata(data);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Pregnancy[] d = data.getData().toArray(new Pregnancy[0]);

                            for (Pregnancy elem : d) {
                                elem.complete = 0;
                            }
                            pregnancyViewModel.add(d);

                            progress.dismiss();
                            buttonSendPregnancydata.setText("Sent " + d.length + " Pregnancy record(s)");
                            buttonSendPregnancydata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Pregnancyoutcome
        final Button buttonSendOutcomedata = findViewById(R.id.buttonSendOutcome);
        //final TextView textViewSendOutcomedata = findViewById(R.id.textViewSendOutcome);
        final PregnancyoutcomeViewModel pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);

        //GET MODIFIED DATA
        final List<Pregnancyoutcome> pregnancyoutcomeList = new ArrayList<>();
        try {
            pregnancyoutcomeList.addAll(pregnancyoutcomeViewModel.findToSync());
            buttonSendOutcomedata.setText("Pregnancy Outcome (" + pregnancyoutcomeList.size() + ") to send");
            buttonSendOutcomedata.setTextColor(Color.BLACK);
            if (pregnancyoutcomeList.isEmpty()) {
                buttonSendOutcomedata.setVisibility(View.GONE);
                //textViewSendOutcomedata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendOutcomedata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Pregnancyoutcome> data = new DataWrapper<>(pregnancyoutcomeList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.sendPregoutcomedata(data);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Pregnancyoutcome[] d = data.getData().toArray(new Pregnancyoutcome[0]);

                            for (Pregnancyoutcome elem : d) {
                                elem.complete = 0;
                            }
                            pregnancyoutcomeViewModel.add(d);

                            progress.dismiss();
                            buttonSendOutcomedata.setText("Sent " + d.length + " Pregnancy Outcome record(s)");
                            buttonSendOutcomedata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });

        //PUSH Pregnancyoutcomes
        final Button buttonSendOutcomesdata = findViewById(R.id.buttonSendOutcomes);
        //final TextView textViewSendOutcomesdata = findViewById(R.id.textViewSendOutcomes);
        final OutcomeViewModel outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);

        //GET MODIFIED DATA
        final List<Outcome> outcomeList = new ArrayList<>();
        try {
            outcomeList.addAll(outcomeViewModel.findToSync());
            buttonSendOutcomesdata.setText("Outcome (" + outcomeList.size() + ") to send");
            buttonSendOutcomesdata.setTextColor(Color.BLACK);
            if (outcomeList.isEmpty()) {
                buttonSendOutcomesdata.setVisibility(View.GONE);
                //textViewSendOutcomesdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendOutcomesdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Outcome> data = new DataWrapper<>(outcomeList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Outcome>> c_callable = dao.sendOutcomedata(data);
                c_callable.enqueue(new Callback<DataWrapper<Outcome>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Outcome>> call, Response<DataWrapper<Outcome>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Outcome[] d = data.getData().toArray(new Outcome[0]);

                            for (Outcome elem : d) {
                                elem.complete = 0;
                            }
                            outcomeViewModel.add(d);

                            progress.dismiss();
                            buttonSendOutcomesdata.setText("Sent " + d.length + " Outcome record(s)");
                            buttonSendOutcomesdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Outcome>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Demographic
        final Button buttonSendDemographicdata = findViewById(R.id.buttonSendDemography);
        //final TextView textViewSendDemographicdata = findViewById(R.id.textViewSendDemography);
        final DemographicViewModel demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);

        //GET MODIFIED DATA
        final List<Demographic> demographicList = new ArrayList<>();
        try {
            demographicList.addAll(demographicViewModel.findToSync());
            buttonSendDemographicdata.setText("Demographic (" + demographicList.size() + ") to send");
            buttonSendDemographicdata.setTextColor(Color.BLACK);
            if (demographicList.isEmpty()) {
                buttonSendDemographicdata.setVisibility(View.GONE);
               // textViewSendDemographicdata.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendDemographicdata.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Demographic> data = new DataWrapper<>(demographicList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Demographic>> c_callable = dao.sendDemographicdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Demographic[] d = data.getData().toArray(new Demographic[0]);

                            for (Demographic elem : d) {
                                elem.complete = 0;
                                //Log.e("PUSH.tag", "Has value " + elem.edtime);
                            }
                            demographicViewModel.add(d);

                            progress.dismiss();
                            buttonSendDemographicdata.setText("Sent " + d.length + " Demographic record(s)");
                            buttonSendDemographicdata.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Demographic>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH DEATH DATA (DEATH, VPM)
        final Button buttonSendEnd = findViewById(R.id.buttonSendEnd);
        //final TextView textViewSendEnd = findViewById(R.id.textViewSendEnd);

        final DeathViewModel death = new ViewModelProvider(this).get(DeathViewModel.class);
        final DeathViewModel vpms = new ViewModelProvider(this).get(DeathViewModel.class);
        final List<Death> listDeath = new ArrayList<>();
        final List<Death> listVpm = new ArrayList<>();

        try {
            listDeath.addAll(death.findToSync());
            listVpm.addAll(vpms.retrieveVpmSync());

            buttonSendEnd.setText(
                    "Death (" + listDeath.size() + ")" +
                            ", VPM(" + listVpm.size() + ")" +
                            " to send"
            );
            buttonSendEnd.setTextColor(Color.BLACK);
            if (listDeath.isEmpty() && listVpm.isEmpty()) {
                buttonSendEnd.setVisibility(View.GONE);
                //textViewSendEnd.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendEnd.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            final Death[][] d23 = new Death[1][1];
            final Death[][] d24 = new Death[1][1];

            final DataWrapper<Death> dataDeath = new DataWrapper<>(listDeath);
            if (dataDeath.getData() != null && !dataDeath.getData().isEmpty()) {
                progress.setMessage("Sending " + dataDeath.getData().size() + " of Death record(s)...");

                for (Death elem : dataDeath.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Death>> c_callable = dao.sendDeathdata(dataDeath);
                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d23[0] = dataDeath.getData().toArray(new Death[0]);
                            death.add(d23[0]);
                            progress.dismiss();
                            buttonSendEnd.setText(
                                    "Death(" + d23[0].length + " of " + listDeath.size() + ")" +
                                            ", VPM(" + d24[0].length + " of " + listVpm.size() + ")" +
                                            " sent"
                            );
                            buttonSendEnd.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Death data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Death> datavpm = new DataWrapper<>(listVpm);
            if (datavpm.getData() != null && !datavpm.getData().isEmpty()) {
                progress.setMessage("Sending " + datavpm.getData().size() + " VPM record(s)...");

                for (Death elem : datavpm.getData()) {
                    elem.vpmcomplete = 0;
                }

                final Call<DataWrapper<Death>> c_callable = dao.sendVpmdata(datavpm);
                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d24[0] = response.body().getData().toArray(new Death[0]);
                            vpms.add(d24[0]);
                            progress.dismiss();
                            buttonSendEnd.setText(
                                    "Death(" + d23[0].length + " of " + listDeath.size() + ")" +
                                            ", VPM(" + d24[0].length + " of " + listVpm.size() + ")" +
                                            " sent"
                            );
                            buttonSendEnd.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send VPM", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }


        });

        //PUSH SES DATA
        final Button buttonSendSocio = findViewById(R.id.buttonSendSocio);
        //final TextView textViewSendSocio = findViewById(R.id.textViewSendSocio);
        final HdssSociodemoViewModel hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);

        //GET MODIFIED DATA
        final List<HdssSociodemo> hdssSociodemoList = new ArrayList<>();
        try {
            hdssSociodemoList.addAll(hdssSociodemoViewModel.findToSync());
            buttonSendSocio.setText("Profiles[SES] (" + hdssSociodemoList.size() + ") to send");
            buttonSendSocio.setTextColor(Color.BLACK);
            if (hdssSociodemoList.isEmpty()) {
                buttonSendSocio.setVisibility(View.GONE);
                //textViewSendSocio.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendSocio.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<HdssSociodemo> data = new DataWrapper<>(hdssSociodemoList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<HdssSociodemo>> c_callable = dao.sendSociodata(data);
                c_callable.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, Response<DataWrapper<HdssSociodemo>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            HdssSociodemo[] d = data.getData().toArray(new HdssSociodemo[0]);

                            for (HdssSociodemo elem : d) {
                                elem.complete = 0;
                            }
                            hdssSociodemoViewModel.add(d);

                            progress.dismiss();
                            buttonSendSocio.setText("Sent " + d.length + " record(s)");
                            buttonSendSocio.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<HdssSociodemo>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send SES Data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Residency
        final Button buttonSendRes = findViewById(R.id.buttonSendResidency);
        //final TextView textViewSendRes = findViewById(R.id.textViewSendResidency);
        final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

        //GET MODIFIED DATA
        final List<Residency> residencyList = new ArrayList<>();
        try {
            residencyList.addAll(residencyViewModel.findToSync());
            buttonSendRes.setText("Residency (" + residencyList.size() + ") to send");
            buttonSendRes.setTextColor(Color.BLACK);
            if (residencyList.isEmpty()) {
                buttonSendRes.setVisibility(View.GONE);
                //textViewSendRes.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendRes.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Residency> data = new DataWrapper<>(residencyList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Residency>> c_callable = dao.sendResidencydata(data);
                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Residency[] d = data.getData().toArray(new Residency[0]);

                            for (Residency elem : d) {
                                elem.complete = 0;
                            }
                            residencyViewModel.add(d);

                            progress.dismiss();
                            buttonSendRes.setText("Sent " + d.length + " Residency record(s)");
                            buttonSendRes.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Inmigration
        final Button buttonSendImg = findViewById(R.id.buttonSendImg);
        //final TextView textViewSendImg = findViewById(R.id.textViewSendImg);
        final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);

        //GET MODIFIED DATA
        final List<Inmigration> inmigrationList = new ArrayList<>();
        try {
            inmigrationList.addAll(inmigrationViewModel.findToSync());
            buttonSendImg.setText("Inmigration (" + inmigrationList.size() + ") to send");
            buttonSendImg.setTextColor(Color.BLACK);
            if (inmigrationList.isEmpty()) {
                buttonSendImg.setVisibility(View.GONE);
                //textViewSendImg.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendImg.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Inmigration> data = new DataWrapper<>(inmigrationList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");

                final Call<DataWrapper<Inmigration>> c_callable = dao.sendInmigrationdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Inmigration[] d = data.getData().toArray(new Inmigration[0]);

                            for (Inmigration elem : d) {
                                elem.complete = 0;
                            }
                            inmigrationViewModel.add(d);

                            progress.dismiss();
                            buttonSendImg.setText("Sent " + d.length + " Inmigration record(s)");
                            buttonSendImg.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });


        //PUSH Outmigration
        final Button buttonSendOmg = findViewById(R.id.buttonSendOmg);
        //final TextView textViewSendOmg = findViewById(R.id.textViewSendOmg);
        final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);

        //GET MODIFIED DATA
        final List<Outmigration> outmigrationList = new ArrayList<>();
        try {
            outmigrationList.addAll(outmigrationViewModel.findToSync());
            buttonSendOmg.setText("Outmigration (" + outmigrationList.size() + ") to send");
            buttonSendOmg.setTextColor(Color.BLACK);
            if (outmigrationList.isEmpty()) {
                buttonSendOmg.setVisibility(View.GONE);
                //textViewSendOmg.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendOmg.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Outmigration> data = new DataWrapper<>(outmigrationList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Outmigration>> c_callable = dao.sendOutmigrationdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Outmigration[] d = data.getData().toArray(new Outmigration[0]);

                            for (Outmigration elem : d) {
                                elem.complete = 0;
                            }
                            outmigrationViewModel.add(d);

                            progress.dismiss();
                            buttonSendOmg.setText("Sent " + d.length + " Outmigration record(s)");
                            buttonSendOmg.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });




        //PUSH Amendment
        final Button buttonSendAmend = findViewById(R.id.buttonSendAmend);
        //final TextView textViewSendAmend = findViewById(R.id.textViewSendAmend);
        final AmendmentViewModel amendmentViewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);

        //GET MODIFIED DATA
        final List<Amendment> amendmentList = new ArrayList<>();
        try {
            amendmentList.addAll(amendmentViewModel.findToSync());
            buttonSendAmend.setText("Amendment (" + amendmentList.size() + ") to send");
            buttonSendAmend.setTextColor(Color.BLACK);
            if (amendmentList.isEmpty()) {
                buttonSendAmend.setVisibility(View.GONE);
                //textViewSendAmend.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendAmend.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Amendment> data = new DataWrapper<>(amendmentList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Amendment>> c_callable = dao.sendAmendment(data);
                c_callable.enqueue(new Callback<DataWrapper<Amendment>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Amendment>> call, Response<DataWrapper<Amendment>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Amendment[] d = data.getData().toArray(new Amendment[0]);

                            for (Amendment elem : d) {
                                elem.complete = 0;
                            }
                            amendmentViewModel.add(d);

                            progress.dismiss();
                            buttonSendAmend.setText("Sent " + d.length + " Amendment record(s)");
                            buttonSendAmend.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Amendment>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });



        //PUSH Vaccination
        final Button buttonSendVac = findViewById(R.id.buttonSendVac);
        //final TextView textViewSendVac = findViewById(R.id.textViewSendVac);
        final VaccinationViewModel vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);

        //GET MODIFIED DATA
        final List<Vaccination> vaccinationList = new ArrayList<>();
        try {
            vaccinationList.addAll(vaccinationViewModel.findToSync());
            buttonSendVac.setText("Vaccination (" + vaccinationList.size() + ") to send");
            buttonSendVac.setTextColor(Color.BLACK);
            if (vaccinationList.isEmpty()) {
                buttonSendVac.setVisibility(View.GONE);
                //textViewSendVac.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendVac.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Vaccination> data = new DataWrapper<>(vaccinationList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Vaccination>> c_callable = dao.sendVaccination(data);
                c_callable.enqueue(new Callback<DataWrapper<Vaccination>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Vaccination>> call, Response<DataWrapper<Vaccination>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Vaccination[] d = data.getData().toArray(new Vaccination[0]);

                            for (Vaccination elem : d) {
                                elem.complete = 0;
                            }
                            vaccinationViewModel.add(d);

                            progress.dismiss();
                            buttonSendVac.setText("Sent " + d.length + " Vaccination record(s)");
                            buttonSendVac.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Vaccination>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

        });

        //PUSH Duplicates
        final Button buttonSendDup = findViewById(R.id.buttonSendDup);
        //final TextView textViewSendDup = findViewById(R.id.textViewSendDup);
        final DuplicateViewModel duplicateViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);

        //GET MODIFIED DATA
        final List<Duplicate> duplicateList = new ArrayList<>();
        try {
            duplicateList.addAll(duplicateViewModel.findToSync());
            buttonSendDup.setText("Duplicate (" + duplicateList.size() + ") to send");
            buttonSendDup.setTextColor(Color.BLACK);
            if (duplicateList.isEmpty()) {
                buttonSendDup.setVisibility(View.GONE);
                //textViewSendDup.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendDup.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            //WRAP THE DATA
            final DataWrapper<Duplicate> data = new DataWrapper<>(duplicateList);

            //SEND THE DATA
            if (data.getData() != null && !data.getData().isEmpty()) {

                progress.setMessage("Sending " + data.getData().size() + " record(s)...");


                final Call<DataWrapper<Duplicate>> c_callable = dao.sendDup(data);
                c_callable.enqueue(new Callback<DataWrapper<Duplicate>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Duplicate>> call, Response<DataWrapper<Duplicate>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Duplicate[] d = data.getData().toArray(new Duplicate[0]);

                            for (Duplicate elem : d) {
                                elem.complete = 0;
                            }
                            duplicateViewModel.add(d);

                            progress.dismiss();
                            buttonSendDup.setText("Sent " + d.length + " Duplicate record(s)");
                            buttonSendDup.setTextColor(Color.parseColor("#FF4500"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Duplicate>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                progress.dismiss();
            }

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
//                            PushActivity.this.finish();
//                        }
//                        catch(Exception e){}
//                    }
//                })
//                .setNegativeButton(getString(R.string.no), null)
//                .show();
//    }
}