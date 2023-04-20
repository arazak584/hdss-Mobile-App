package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
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
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
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
        final TextView textViewSendLocationdata = findViewById(R.id.textViewSendLocation);
        final LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //GET MODIFIED DATA
        final List<Locations> locationsList = new ArrayList<>();
        try {
            locationsList.addAll(locationViewModel.findToSync());
            textViewSendLocationdata.setText("Locations(" + locationsList.size() + ") to send");
            textViewSendLocationdata.setTextColor(Color.rgb(0, 114, 133));
            if (locationsList.isEmpty()) {
                buttonSendLocationdata.setVisibility(View.GONE);
                textViewSendLocationdata.setVisibility(View.GONE);
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

                for (Locations elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getCompno());
                }

                final Call<DataWrapper<Locations>> c_callable = dao.sendLocationdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Locations>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Locations>> call, Response<DataWrapper<Locations>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Locations[] d = response.body().getData().toArray(new Locations[0]);
                            locationViewModel.add(d);
                            progress.dismiss();
                            textViewSendLocationdata.setText("Sent " + d.length + " record(s)");
                            textViewSendLocationdata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendVisit = findViewById(R.id.textViewSendVisit);
        final VisitViewModel visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);

        //GET MODIFIED DATA
        final List<Visit> visitList = new ArrayList<>();
        try {
            visitList.addAll(visitViewModel.findToSync());
            textViewSendVisit.setText("Visit(" + visitList.size() + ") to send");
            textViewSendVisit.setTextColor(Color.rgb(0, 114, 133));
            if (visitList.isEmpty()) {
                buttonSendVisit.setVisibility(View.GONE);
                textViewSendVisit.setVisibility(View.GONE);
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

                for (Visit elem : data.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Visit>> c_callable = dao.sendVisitdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Visit>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Visit>> call, Response<DataWrapper<Visit>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Visit[] d = response.body().getData().toArray(new Visit[0]);
                            visitViewModel.add(d);
                            progress.dismiss();
                            textViewSendVisit.setText("Sent " + d.length + " record(s)");
                            textViewSendVisit.setTextColor(Color.rgb(0, 114, 133));
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

        //PUSH INDIVIDUAL
        final Button buttonSendIndividualdata = findViewById(R.id.buttonSendIndividual);
        final TextView textViewSendIndividualdata = findViewById(R.id.textViewSendIndividual);
        final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);

        //GET MODIFIED DATA
        final List<Individual> individualList = new ArrayList<>();
        try {
            individualList.addAll(individualViewModel.findToSync());
            textViewSendIndividualdata.setText("Individuals(" + individualList.size() + ") to send");
            textViewSendIndividualdata.setTextColor(Color.rgb(0, 114, 133));
            if (individualList.isEmpty()) {
                buttonSendIndividualdata.setVisibility(View.GONE);
                textViewSendIndividualdata.setVisibility(View.GONE);
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

                for (Individual elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getExtId());
                }

                final Call<DataWrapper<Individual>> c_callable = dao.sendIndividualdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Individual>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Individual>> call, Response<DataWrapper<Individual>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Individual[] d = response.body().getData().toArray(new Individual[0]);
                            individualViewModel.add(d);
                            progress.dismiss();
                            textViewSendIndividualdata.setText("Sent " + d.length + " Individual record(s)");
                            textViewSendIndividualdata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendSocialgroupdata = findViewById(R.id.textViewSendSocialgroup);
        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);

        //GET MODIFIED DATA
        final List<Socialgroup> socialgroupList = new ArrayList<>();
        try {
            socialgroupList.addAll(socialgroupViewModel.findToSync());
            textViewSendSocialgroupdata.setText("Socialgroup (" + individualList.size() + ") to send");
            textViewSendSocialgroupdata.setTextColor(Color.rgb(0, 114, 133));
            if (socialgroupList.isEmpty()) {
                buttonSendSocialgroupdata.setVisibility(View.GONE);
                textViewSendSocialgroupdata.setVisibility(View.GONE);
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

                for (Socialgroup elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getHouseExtId());
                }

                final Call<DataWrapper<Socialgroup>> c_callable = dao.sendSocialgroupdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Socialgroup>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Socialgroup>> call, Response<DataWrapper<Socialgroup>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Socialgroup[] d = response.body().getData().toArray(new Socialgroup[0]);
                            socialgroupViewModel.add(d);
                            progress.dismiss();
                            textViewSendSocialgroupdata.setText("Sent " + d.length + " Socialgroup record(s)");
                            textViewSendSocialgroupdata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendRelationshipdata = findViewById(R.id.textViewSendRelationship);
        final RelationshipViewModel relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);

        //GET MODIFIED DATA
        final List<Relationship> relationshipList = new ArrayList<>();
        try {
            relationshipList.addAll(relationshipViewModel.findToSync());
            textViewSendRelationshipdata.setText("Relationship (" + relationshipList.size() + ") to send");
            textViewSendRelationshipdata.setTextColor(Color.rgb(0, 114, 133));
            if (relationshipList.isEmpty()) {
                buttonSendRelationshipdata.setVisibility(View.GONE);
                textViewSendRelationshipdata.setVisibility(View.GONE);
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

                for (Relationship elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getIndividual_uuid());
                }

                final Call<DataWrapper<Relationship>> c_callable = dao.sendRelationshipdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Relationship[] d = response.body().getData().toArray(new Relationship[0]);
                            relationshipViewModel.add(d);
                            progress.dismiss();
                            textViewSendRelationshipdata.setText("Sent " + d.length + " Relationship record(s)");
                            textViewSendRelationshipdata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendPregnancydata = findViewById(R.id.textViewSendPregnancy);
        final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        //GET MODIFIED DATA
        final List<Pregnancy> pregnancyList = new ArrayList<>();
        try {
            pregnancyList.addAll(pregnancyViewModel.findToSync());
            textViewSendPregnancydata.setText("Pregnancy (" + pregnancyList.size() + ") to send");
            textViewSendPregnancydata.setTextColor(Color.rgb(0, 114, 133));
            if (pregnancyList.isEmpty()) {
                buttonSendPregnancydata.setVisibility(View.GONE);
                textViewSendPregnancydata.setVisibility(View.GONE);
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

                for (Pregnancy elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getObs_uuid());
                }

                final Call<DataWrapper<Pregnancy>> c_callable = dao.sendPregnancydata(data);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Pregnancy[] d = response.body().getData().toArray(new Pregnancy[0]);
                            pregnancyViewModel.add(d);
                            progress.dismiss();
                            textViewSendPregnancydata.setText("Sent " + d.length + " Relationship record(s)");
                            textViewSendPregnancydata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendOutcomedata = findViewById(R.id.textViewSendOutcome);
        final PregnancyoutcomeViewModel pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);

        //GET MODIFIED DATA
        final List<Pregnancyoutcome> pregnancyoutcomeList = new ArrayList<>();
        try {
            pregnancyoutcomeList.addAll(pregnancyoutcomeViewModel.findToSync());
            textViewSendOutcomedata.setText("Outcome (" + pregnancyoutcomeList.size() + ") to send");
            textViewSendOutcomedata.setTextColor(Color.rgb(0, 114, 133));
            if (pregnancyoutcomeList.isEmpty()) {
                buttonSendOutcomedata.setVisibility(View.GONE);
                textViewSendOutcomedata.setVisibility(View.GONE);
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

                for (Pregnancyoutcome elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getPreg_uuid());
                }

                final Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.sendPregoutcomedata(data);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Pregnancyoutcome[] d = response.body().getData().toArray(new Pregnancyoutcome[0]);
                            pregnancyoutcomeViewModel.add(d);
                            progress.dismiss();
                            textViewSendOutcomedata.setText("Sent " + d.length + " Outcome record(s)");
                            textViewSendOutcomedata.setTextColor(Color.rgb(0, 114, 133));
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



        //PUSH Demographic
        final Button buttonSendDemographicdata = findViewById(R.id.buttonSendDemography);
        final TextView textViewSendDemographicdata = findViewById(R.id.textViewSendDemography);
        final DemographicViewModel demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);

        //GET MODIFIED DATA
        final List<Demographic> demographicList = new ArrayList<>();
        try {
            demographicList.addAll(demographicViewModel.findToSync());
            textViewSendDemographicdata.setText("Demographic (" + demographicList.size() + ") to send");
            textViewSendDemographicdata.setTextColor(Color.rgb(0, 114, 133));
            if (demographicList.isEmpty()) {
                buttonSendDemographicdata.setVisibility(View.GONE);
                textViewSendDemographicdata.setVisibility(View.GONE);
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

                for (Demographic elem : data.getData()) {
                    elem.complete = 0;
                    Log.e("PUSH.tag", "Has value " + elem.getIndividual_uuid());
                }

                final Call<DataWrapper<Demographic>> c_callable = dao.sendDemographicdata(data);
                c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            Demographic[] d = response.body().getData().toArray(new Demographic[0]);
                            demographicViewModel.add(d);
                            progress.dismiss();
                            textViewSendDemographicdata.setText("Sent " + d.length + " Demographic record(s)");
                            textViewSendDemographicdata.setTextColor(Color.rgb(0, 114, 133));
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
        final TextView textViewSendEnd = findViewById(R.id.textViewSendEnd);

        final DeathViewModel death = new ViewModelProvider(this).get(DeathViewModel.class);
        final DeathViewModel vpms = new ViewModelProvider(this).get(DeathViewModel.class);

        final List<Death> listDeath = new ArrayList<>();
        final List<Death> listVpm = new ArrayList<>();

        try {
            listDeath.addAll(death.findToSync());
            listVpm.addAll(vpms.retrieveVpmSync());

            textViewSendEnd.setText(
                    "Death (" + listDeath.size() + ")" +
                            ", VPM(" + listVpm.size() + ")" +
                            " to send"
            );
            if (listDeath.isEmpty() && listVpm.isEmpty()) {
                buttonSendEnd.setVisibility(View.GONE);
                textViewSendEnd.setVisibility(View.GONE);
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

                            d23[0] = response.body().getData().toArray(new Death[0]);
                            death.add(d23[0]);
                            progress.dismiss();
                            textViewSendEnd.setText(
                                    "Deaht(" + d23[0].length + " of " + listDeath.size() + ")" +
                                            ", VPM(" + d24[0].length + " of " + listVpm.size() + ")" +
                                            " sent"
                            );
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

            final DataWrapper<Death> dataMnh24 = new DataWrapper<>(listVpm);
            if (dataMnh24.getData() != null && !dataMnh24.getData().isEmpty()) {
                progress.setMessage("Sending " + dataMnh24.getData().size() + " VPM record(s)...");

                for (Death ele : dataMnh24.getData()) {
                    ele.vpmcomplete = 0;
                }

                final Call<DataWrapper<Death>> c_callable = dao.sendVpmdata(dataMnh24);
                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d24[0] = response.body().getData().toArray(new Death[0]);
                            vpms.add(d24[0]);
                            progress.dismiss();
                            textViewSendEnd.setText(
                                    "Death(" + d23[0].length + " of " + listDeath.size() + ")" +
                                            ", VPM(" + d24[0].length + " of " + listVpm.size() + ")" +
                                            " sent"
                            );
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


        //PUSH END DATA (Residency, Outmigration)
        final Button buttonSendEvents = findViewById(R.id.buttonSendEvents);
        final TextView textViewSendEvents = findViewById(R.id.textViewSendEvents);

        final ResidencyViewModel residency = new ViewModelProvider(this).get(ResidencyViewModel.class);
        final ResidencyViewModel outmigration = new ViewModelProvider(this).get(ResidencyViewModel.class);
        final ResidencyViewModel inmigration = new ViewModelProvider(this).get(ResidencyViewModel.class);

        final List<Residency> listResidency = new ArrayList<>();
        final List<Residency> listOutmigration = new ArrayList<>();
        final List<Residency> listInmigration = new ArrayList<>();

        try {
            listResidency.addAll(residency.findToSync());
            listOutmigration.addAll(outmigration.findOmgSync());
            listInmigration.addAll(inmigration.findImgSync());

            textViewSendEvents.setText(
                    "Residency (" + listResidency.size() + ")" +
                            ", Outmigration(" + listOutmigration.size() + ")" +
                            ", Inmigration(" + listInmigration.size() + ")" +
                            " to send"
            );
            if (listResidency.isEmpty() && listOutmigration.isEmpty() && listInmigration.isEmpty()) {
                buttonSendEvents.setVisibility(View.GONE);
                textViewSendEvents.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendEvents.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            final Residency[][] d01 = new Residency[1][1];
            final Residency[][] d02 = new Residency[1][1];
            final Residency[][] d03 = new Residency[1][1];

            final DataWrapper<Residency> dataResidency = new DataWrapper<>(listResidency);
            if (dataResidency.getData() != null && !dataResidency.getData().isEmpty()) {
                progress.setMessage("Sending " + dataResidency.getData().size() + " of Membership record(s)...");

                for (Residency elem : dataResidency.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Residency>> c_callable = dao.sendResidencydata(dataResidency);
                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d01[0] = response.body().getData().toArray(new Residency[0]);
                            residency.add(d01[0]);
                            progress.dismiss();
                            textViewSendEvents.setText(
                                    "Membership(" + d01[0].length + " of " + listResidency.size() + ")" +
                                            ", Outmigration(" + d02[0].length + " of " + listOutmigration.size() + ")" +
                                            ", Inmigration(" + d03[0].length + " of " + listInmigration.size() + ")" +
                                            " sent"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Residency data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Residency> dataOutmigration = new DataWrapper<>(listOutmigration);
            if (dataOutmigration.getData() != null && !dataOutmigration.getData().isEmpty()) {
                progress.setMessage("Sending " + dataOutmigration.getData().size() + " Outmigration record(s)...");

                for (Residency ele : dataOutmigration.getData()) {
                    ele.omgcomplete = 0;
                }

                final Call<DataWrapper<Residency>> c_callable = dao.sendOutmigrationdata(dataOutmigration);
                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d02[0] = response.body().getData().toArray(new Residency[0]);
                            outmigration.add(d02[0]);
                            progress.dismiss();
                            textViewSendEvents.setText(
                                    "Membership(" + d01[0].length + " of " + listResidency.size() + ")" +
                                            ", Outmigration(" + d02[0].length + " of " + listOutmigration.size() + ")" +
                                            ", Inmigration(" + d03[0].length + " of " + listInmigration.size() + ")" +
                                            " sent"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Outmigration", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Residency> dataInmigration = new DataWrapper<>(listInmigration);
            if (dataInmigration.getData() != null && !dataInmigration.getData().isEmpty()) {
                progress.setMessage("Sending " + dataInmigration.getData().size() + " Inmigration record(s)...");

                for (Residency eles : dataInmigration.getData()) {
                    eles.imgcomplete = 0;
                }

                final Call<DataWrapper<Residency>> c_callable = dao.sendInmigrationdata(dataInmigration);
                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            d03[0] = response.body().getData().toArray(new Residency[0]);
                            inmigration.add(d03[0]);
                            progress.dismiss();
                            textViewSendEvents.setText(
                                    "Membership(" + d01[0].length + " of " + listResidency.size() + ")" +
                                            ", Outmigration(" + d02[0].length + " of " + listOutmigration.size() + ")" +
                                            ", Inmigration(" + d03[0].length + " of " + listInmigration.size() + ")" +
                                            " sent"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Residency>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Inmigration", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }


        });



        //PUSH SES DATA
        final Button buttonSendSocio = findViewById(R.id.buttonSendSocio);
        final TextView textViewSendSocio = findViewById(R.id.textViewSendSocio);
        final HdssSociodemoViewModel hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);

        //GET MODIFIED DATA
        final List<HdssSociodemo> hdssSociodemoList = new ArrayList<>();
        try {
            hdssSociodemoList.addAll(hdssSociodemoViewModel.findToSync());
            textViewSendSocio.setText("Visit(" + hdssSociodemoList.size() + ") to send");
            textViewSendSocio.setTextColor(Color.rgb(0, 114, 133));
            if (hdssSociodemoList.isEmpty()) {
                buttonSendSocio.setVisibility(View.GONE);
                textViewSendSocio.setVisibility(View.GONE);
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

                for (HdssSociodemo elem : data.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<HdssSociodemo>> c_callable = dao.sendSociodata(data);
                c_callable.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<HdssSociodemo>> call, Response<DataWrapper<HdssSociodemo>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            HdssSociodemo[] d = response.body().getData().toArray(new HdssSociodemo[0]);
                            hdssSociodemoViewModel.add(d);
                            progress.dismiss();
                            textViewSendSocio.setText("Sent " + d.length + " record(s)");
                            textViewSendSocio.setTextColor(Color.rgb(0, 114, 133));
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

    }
}