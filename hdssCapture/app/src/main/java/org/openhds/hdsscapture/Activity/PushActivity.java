package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
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

        AppJson api = AppJson.getInstance();
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
                buttonSendLocationdata.setEnabled(false);
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
                buttonSendVisit.setEnabled(false);
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



        //PUSH INDIVIDUAL DATA (INDIVIDUAL, SOCIALGROUP, RESIDENCY)
        final Button buttonSendIndividual = findViewById(R.id.buttonSendIndividual);
        final TextView textViewSendIndividual = findViewById(R.id.textViewSendIndividual);
        final IndividualViewModel individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        final SocialgroupViewModel socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        final ResidencyViewModel residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

        final List<Individual> individualList = new ArrayList<>();
        final List<Socialgroup> socialgroupList = new ArrayList<>();
        final List<Residency> residencyList = new ArrayList<>();
        try {
            individualList.addAll(individualViewModel.findToSync());
            socialgroupList.addAll(socialgroupViewModel.findToSync());
            residencyList.addAll(residencyViewModel.findToSync());
            textViewSendIndividual.setText(
                    "Individual(" + individualList.size() + ")" +
                            ", Socialgroup(" + socialgroupList.size() + ")" +
                            ", Residency(" + residencyList.size() + ") to send" );
            textViewSendIndividual.setTextColor(Color.rgb(0, 114, 133));
            if (individualList.isEmpty() && socialgroupList.isEmpty() && residencyList.isEmpty()) {
                buttonSendIndividual.setEnabled(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendIndividual.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            final Individual[][] dS = new Individual[1][1];
            final Socialgroup[][] dC = new Socialgroup[1][1];
            final Residency[][] dU = new Residency[1][1];

            final DataWrapper<Individual> dataIndividual = new DataWrapper<>(individualList);
            if (dataIndividual.getData() != null && !dataIndividual.getData().isEmpty()) {
                progress.setMessage("Sending " + dataIndividual.getData().size() + " Individual record(s)...");

                for (Individual elem : dataIndividual.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Individual>> c_callable = dao.sendIndividualdata(dataIndividual);
                c_callable.enqueue(new Callback<DataWrapper<Individual>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Individual>> call, Response<DataWrapper<Individual>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dS[0] = response.body().getData().toArray(new Individual[0]);
                            individualViewModel.add(dS[0]);
                            progress.dismiss();
                            textViewSendIndividual.setText("Individual(" + dS[0].length + "of" + individualList.size() + ") sent" +
                                    ", Socialgroup(" + socialgroupList.size() + ")" +
                                    ", Residency(" + residencyList.size() + ") to send"

                            );
                            textViewSendIndividual.setTextColor(Color.rgb(0, 114, 133));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Individual>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Individual Data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Socialgroup> dataSocialgroup = new DataWrapper<>(socialgroupList);
            if (dataSocialgroup.getData() != null && !dataSocialgroup.getData().isEmpty()) {
                progress.setMessage("Sending " + dataSocialgroup.getData().size() + " Socialgroup Data...");

                for (Socialgroup elem : dataSocialgroup.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Socialgroup>> c_callable = dao.sendSocialgroupdata(dataSocialgroup);
                c_callable.enqueue(new Callback<DataWrapper<Socialgroup>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Socialgroup>> call, Response<DataWrapper<Socialgroup>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dC[0] = response.body().getData().toArray(new Socialgroup[0]);
                            socialgroupViewModel.add(dC[0]);
                            progress.dismiss();
                            textViewSendIndividual.setText("Individual(" + dS[0].length + " of " + individualList.size() + ") sent," +
                                    ", Socialgroup(" + dC[0].length  + socialgroupList.size() + ") sent"+
                                    " Residency(" + " of " + residencyList.size() + ") to send"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Socialgroup>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Socialgroup data ", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }


            final DataWrapper<Residency> dataResidency = new DataWrapper<>(residencyList);
            if (dataResidency.getData() != null && !dataResidency.getData().isEmpty()) {
                progress.setMessage("Sending " + dataResidency.getData().size() + " Residency data...");

                for (Residency elem : dataResidency.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Residency>> c_callable = dao.sendResidencydata(dataResidency);
                c_callable.enqueue(new Callback<DataWrapper<Residency>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Residency>> call, Response<DataWrapper<Residency>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dU[0] = response.body().getData().toArray(new Residency[0]);
                            residencyViewModel.add(dU[0]);
                            progress.dismiss();
                            textViewSendIndividual.setText("Individual(" + dS[0].length + " of " + individualList.size() + ") sent" +
                                    ", Socialgroup(" + dC[0].length + " of " + socialgroupList.size() + ") sent" +
                                    ", Residency(" + dU[0].length + " of " + residencyList.size() + ") sent"
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


        });



        //PUSH EVENTS DATA (INMIGRATION, OUTMIGRATION, DEATH)
        final Button buttonSendEvent = findViewById(R.id.buttonSendEvent);
        final TextView textViewSendEvent = findViewById(R.id.textViewSendEvent);
        final DeathViewModel deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);

        final List<Inmigration> inmigrations = new ArrayList<>();
        final List<Outmigration> outmigrations = new ArrayList<>();
        final List<Death> deathList = new ArrayList<>();
        try {
            inmigrations.addAll(inmigrationViewModel.findimgToSync());
            outmigrations.addAll(outmigrationViewModel.findomgToSync());
            deathList.addAll(deathViewModel.findToSync());
            textViewSendEvent.setText(
                    "Inmigration(" + inmigrations.size() + ")" +
                            ", Outmigration(" + outmigrations.size() + ")" +
                            ", Death(" + deathList.size() + ") to send" );
            textViewSendEvent.setTextColor(Color.rgb(0, 114, 133));
            if (inmigrations.isEmpty() && outmigrations.isEmpty() && deathList.isEmpty()) {
                buttonSendEvent.setEnabled(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendEvent.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            final Inmigration[][] dA = new Inmigration[1][1];
            final Outmigration[][] dB = new Outmigration[1][1];
            final Death[][] dE = new Death[1][1];

            final DataWrapper<Inmigration> dataImg = new DataWrapper<>(inmigrations);
            if (dataImg.getData() != null && !dataImg.getData().isEmpty()) {
                progress.setMessage("Sending " + dataImg.getData().size() + " Inmigration record(s)...");

                for (Inmigration elem : dataImg.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Inmigration>> c_callable = dao.sendInmigrationdata(dataImg);
                c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dA[0] = response.body().getData().toArray(new Inmigration[0]);
                            inmigrationViewModel.add(dA[0]);
                            progress.dismiss();
                            textViewSendEvent.setText("Inmigration(" + dA[0].length + "of" + inmigrations.size() + ") sent" +
                                    ", Outmigration(" + outmigrations.size() + ") " +
                                    ", Death(" + deathList.size() + ") to send"
                            );
                            textViewSendEvent.setTextColor(Color.rgb(0, 114, 133));

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Inmigration>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Inmigration Data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Outmigration> dataOmg = new DataWrapper<>(outmigrations);
            if (dataOmg.getData() != null && !dataOmg.getData().isEmpty()) {
                progress.setMessage("Sending " + dataOmg.getData().size() + " Outmigration Data...");

                for (Outmigration elem : dataOmg.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Outmigration>> c_callable = dao.sendOutmigrationdata(dataOmg);
                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dB[0] = response.body().getData().toArray(new Outmigration[0]);
                            outmigrationViewModel.add(dB[0]);
                            progress.dismiss();
                            textViewSendEvent.setText("Inmigration(" + dA[0].length + " of " + inmigrations.size() + ") sent," +
                                    ", Outmigration(" + dB[0].length + outmigrations.size() + ") sent"+
                                    " Death(" + " of " + deathList.size() + ") to send"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Outmigration>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Outmigration data ", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }


            final DataWrapper<Death> dataDeath = new DataWrapper<>(deathList);
            if (dataDeath.getData() != null && !dataDeath.getData().isEmpty()) {
                progress.setMessage("Sending " + dataDeath.getData().size() + " Death data...");

                for (Death elem : dataDeath.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Death>> c_callable = dao.sendDeathdata(dataDeath);
                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            dE[0] = response.body().getData().toArray(new Death[0]);
                            deathViewModel.add(dE[0]);
                            progress.dismiss();
                            textViewSendEvent.setText("Inmigration(" + dA[0].length + " of " + inmigrations.size() + ") sent" +
                                    ", Outmigration(" + dB[0].length + " of " + outmigrations.size() + ") sent" +
                                    ", Death(" + dE[0].length + " of " + deathList.size() + ") sent"
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


        });


        //PUSH DATA (PREGNANCY, OUTCOME, RELATIONSHIP, VPM)
        final Button buttonSendEvents = findViewById(R.id.buttonSendEvents);
        final TextView textViewSendEvents = findViewById(R.id.textViewSendEvents);
        final PregnancyoutcomeViewModel pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        final RelationshipViewModel relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        final DeathViewModel vpm = new ViewModelProvider(this).get(DeathViewModel.class);

        final List<Pregnancyoutcome> listOutcome = new ArrayList<>();
        final List<Pregnancy> listPregnancy = new ArrayList<>();
        final List<Relationship> listRelationship = new ArrayList<>();
        final List<Death> listVpm = new ArrayList<>();
        try {
            listOutcome.addAll(pregnancyoutcomeViewModel.findToSync());
            listPregnancy.addAll(pregnancyViewModel.findToSync());
            listRelationship.addAll(relationshipViewModel.findToSync());
            listVpm.addAll(vpm.findToSync());
            textViewSendEvents.setText(
                    "Outcome(" + listOutcome.size() + ")" +
                            ", Pregnancy(" + listPregnancy.size() + ")" +
                            ", Relationship(" + listRelationship.size() + ") to send"+
                            ", VPM(" + listVpm.size() + ") to send");
            textViewSendEvents.setTextColor(Color.rgb(0, 114, 133));
            if (listOutcome.isEmpty() && listPregnancy.isEmpty() && listRelationship.isEmpty() && listVpm.isEmpty()) {
                buttonSendEvents.setEnabled(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        buttonSendEvents.setOnClickListener(v -> {
            progress.setMessage(getResources().getString(R.string.init_syncing));
            progress.show();

            final Pregnancyoutcome[][] pS = new Pregnancyoutcome[1][1];
            final Pregnancy[][] pC = new Pregnancy[1][1];
            final Relationship[][] pU = new Relationship[1][1];
            final Death[][] vA = new Death[1][1];

            final DataWrapper<Pregnancyoutcome> dataOutcome = new DataWrapper<>(listOutcome);
            if (dataOutcome.getData() != null && !dataOutcome.getData().isEmpty()) {
                progress.setMessage("Sending " + dataOutcome.getData().size() + " Pregnancy outcome(s)...");

                for (Pregnancyoutcome elem : dataOutcome.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.sendPregoutcomedata(dataOutcome);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            pS[0] = response.body().getData().toArray(new Pregnancyoutcome[0]);
                            pregnancyoutcomeViewModel.add(pS[0]);
                            progress.dismiss();
                            textViewSendEvents.setText("Outcome(" + pS[0].length + "of" + listOutcome.size() + ") sent" +
                                    ", Pregnancy(" + listPregnancy.size() + ")" +
                                    ", Relationship(" + listRelationship.size() + ") to send"+
                                    ", VPM(" + listVpm.size() + ") to send"
                            );
                            textViewSendEvents.setTextColor(Color.rgb(0, 114, 133));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Pregnancyoutcome>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Outcome", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Pregnancy> dataPregnancy = new DataWrapper<>(listPregnancy);
            if (dataPregnancy.getData() != null && !dataPregnancy.getData().isEmpty()) {
                progress.setMessage("Sending " + dataPregnancy.getData().size() + " Pregnancy data...");

                for (Pregnancy elem : dataPregnancy.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Pregnancy>> c_callable = dao.sendPregnancydata(dataPregnancy);
                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            pC[0] = response.body().getData().toArray(new Pregnancy[0]);
                            pregnancyViewModel.add(pC[0]);
                            progress.dismiss();
                            textViewSendEvents.setText("Outcome(" + pS[0].length + " of " + listOutcome.size() + ") sent," +
                                    " Pregnancy(" + pC[0].length + " of " + listPregnancy.size() + ") sent," +
                                    " Relationship(" + listRelationship.size() + ") to send"+
                                    ", VPM(" + listVpm.size() + ") to send"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Pregnancy>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Pregnancy data ", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Relationship> dataRelationship = new DataWrapper<>(listRelationship);
            if (dataRelationship.getData() != null && !dataRelationship.getData().isEmpty()) {
                progress.setMessage("Sending " + dataRelationship.getData().size() + " Relationship data...");

                for (Relationship elem : dataRelationship.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Relationship>> c_callable = dao.sendRelationshipdata(dataRelationship);
                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            pU[0] = response.body().getData().toArray(new Relationship[0]);
                            relationshipViewModel.add(pU[0]);
                            progress.dismiss();
                            textViewSendEvents.setText("Outcome(" + pS[0].length + " of " + listOutcome.size() + ") sent" +
                                    ", Pregnancy(" + pC[0].length + " of " + listPregnancy.size() + ") sent" +
                                    ", Relationship(" + pU[0].length + " of " + listRelationship.size() + ") sent" +
                                    ", VPM(" + listVpm.size() + ") to send"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Relationship>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send Relationship data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }

            final DataWrapper<Death> dataVpm = new DataWrapper<>(listVpm);
            if (dataVpm.getData() != null && !dataVpm.getData().isEmpty()) {
                progress.setMessage("Sending " + dataVpm.getData().size() + " VPM data...");

                for (Death elem : dataVpm.getData()) {
                    elem.complete = 0;
                }

                final Call<DataWrapper<Death>> c_callable = dao.sendVpmdata(dataVpm);
                c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                    @Override
                    public void onResponse(@NonNull Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                        if (response != null && response.body() != null && response.isSuccessful()
                                && response.body().getData() != null && !response.body().getData().isEmpty()) {

                            vA[0] = response.body().getData().toArray(new Death[0]);
                            deathViewModel.add(vA[0]);
                            progress.dismiss();
                            textViewSendEvents.setText("Outcome(" + pS[0].length + " of " + listOutcome.size() + ") sent" +
                                    ", Pregnancy(" + pC[0].length + " of " + listPregnancy.size() + ") sent" +
                                    ", Relationship(" + pU[0].length + " of " + listRelationship.size() + ") sent" +
                                    ", VPM(" + vA[0].length + " of " + listVpm.size() + ") sent"
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DataWrapper<Death>> call, @NonNull Throwable t) {
                        progress.dismiss();
                        Toast.makeText(PushActivity.this, "Failed to send VPM data", Toast.LENGTH_LONG).show();
                        Log.e(TAG, t.getMessage());

                    }
                });

            } else {
                progress.dismiss();
            }


        });

    }
}