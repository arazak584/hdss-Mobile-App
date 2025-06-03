package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Adapter.RejectAdapter;
import org.openhds.hdsscapture.Adapter.ViewsAdapter;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.HdssSociodemoRepository;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.MorbidityViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Vaccination;
import org.openhds.hdsscapture.entity.subqueries.Newloc;
import org.openhds.hdsscapture.entity.subqueries.RejectEvent;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectionsActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progres;

    private DeathViewModel deathViewModel;
    private InmigrationViewModel inmigrationViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private DemographicViewModel demographicViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
    private PregnancyViewModel pregnancyViewModel;
    private RelationshipViewModel relationshipViewModel;
    private VaccinationViewModel vaccinationViewModel;
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private MorbidityViewModel morbidityViewModel;

    private RejectAdapter viewsAdapter;
    private List<RejectEvent> filterAll;
    private SearchView searchView;

    private  SharedPreferences preferences;
    private String authorizationHeader;
    private String fw;

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejections);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
        //String fw = fieldworkerDatas.getFw_uuid();

        progres = new ProgressDialog(RejectionsActivity.this);
        progres.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progres.setCancelable(false);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        authorizationHeader = preferences.getString("authorizationHeader", null);

        // Retrieve fw_uuid from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        fw = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        if (fw != null) {
            // Use the fwUuid as needed
            Log.d("SomeFragment", "Retrieved fw_uuid: " + fw);
        } else {
            // Handle the case where fwUuid is not found
            Log.e("SomeFragment", "fw_uuid not found in SharedPreferences");
        }

        searchView = findViewById(R.id.searchloc);

        // Set a query hint
        searchView.setQueryHint(getString(R.string.search));

        inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        morbidityViewModel = new ViewModelProvider(this).get(MorbidityViewModel.class);

        //Sync LocationHierarchy
        final ExtendedFloatingActionButton dwd = findViewById(R.id.btn_reject);
        dwd.setOnClickListener(v -> {
            startDownloadProcess();
        });

        query();
    }


    private void query() {
        List<RejectEvent> list = new ArrayList<>();

//        final Intent i = getIntent();
//        final Fieldworker fieldworkerDatas = i.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
//        String username = fieldworkerDatas.getFw_uuid();

        try {

            //final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            //final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            int c = 1;
            for (Inmigration e : inmigrationViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = c + ". Inmigration" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.reason_oth;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = c;

                list.add(r1);

                // Increment the counter for the next item
                c++;
            }


            int d=1;
            for (Outmigration e : outmigrationViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = d + ". Outmigration" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.reason_oth;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = d;

                list.add(r1);
                d++;
            }

            int g=1;
            for (Death e : deathViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = g + ". Death" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.deathPlace_oth;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = g;

                list.add(r1);
                g++;
            }

            int h=1;
            for (Relationship e : relationshipViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = h + ". Relationship" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.individualB_uuid;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = h;

                list.add(r1);
                h++;
            }

            int j=1;
            for (Pregnancy e : pregnancyViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = j + ". Pregnancy" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.bnet_loc_other;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = j;

                list.add(r1);
                j++;
            }

            int k=1;
            for (Pregnancyoutcome e : pregnancyoutcomeViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = k + ". Outcome" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.father_uuid;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.getComment();
                r1.index = k;

                list.add(r1);
                k++;
            }

            int l=1;
            for (Demographic e : demographicViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = l + ". Demographic" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.occupation_oth;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = l;

                list.add(r1);
                l++;
            }

            int m=1;
            for (Vaccination e : vaccinationViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = m + ". Vaccination" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " " + e.edtime;
                r1.id3 = "" + e.visit_uuid+ " - " + e.socialgroup_uuid;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = m;

                list.add(r1);
                m++;
            }

            int n=1;
            for (HdssSociodemo e : hdssSociodemoViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = n + ". SES - Profile" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.sttime + " - " + e.edtime;
                r1.id3 = "" + e.visit_uuid;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = n;

                list.add(r1);
                n++;
            }

            int p=1;
            for (Morbidity e : morbidityViewModel.reject(fw)) {
                String formattedDate = f.format(e.approveDate);
                RejectEvent r1 = new RejectEvent();
                r1.id1 = p + ". Morbidity" + " (" + e.supervisor + ")";
                r1.id2 = "" + e.ind_name;
                r1.id3 = "" + e.compno + " - " + e.fw_name;
                r1.id4 = "" + formattedDate;
                r1.id5 = "" + e.comment;
                r1.index = p;

                list.add(r1);
                p++;
            }




            filterAll = new ArrayList<>(list);

            viewsAdapter = new RejectAdapter(this);
            viewsAdapter.setRejectEvents(list);
            RecyclerView recyclerView = findViewById(R.id.recyclerView_rejections);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(viewsAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query changes here
                sFilter(newText);
                return true;
            }
        });
    }

    private void sFilter(String s) {
        ArrayList<RejectEvent> filterNames = new ArrayList<>();
        String searchQuery = s.toLowerCase(); // Convert the search query to lowercase for case-insensitive search
        for (RejectEvent rejectEvent : filterAll) {
            // Convert extid, name, and error to lowercase before comparison
            if (rejectEvent.id1.toLowerCase().contains(searchQuery) ||
                    rejectEvent.id2.toLowerCase().contains(searchQuery) ||
                    rejectEvent.id3.toLowerCase().contains(searchQuery) ||
                    rejectEvent.id4.toLowerCase().contains(searchQuery)) {
                filterNames.add(rejectEvent);
            }
        }
        viewsAdapter.setRejectEvents(filterNames); // Update the adapter with filtered data
    }

    @Override
    protected void onResume() {
        super.onResume();

        query();
    }


    private void startDownloadProcess() {
        if (!isInternetAvailable()) {
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
            return;
        }
        progres.setMessage("Downloading...");
        progres.show();

//        final Intent f = getIntent();
//        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
//        String fw = fieldworkerDatas.getFw_uuid();

        final InmigrationViewModel inmigrationViewModel = new ViewModelProvider(RejectionsActivity.this).get(InmigrationViewModel.class);
        Call<DataWrapper<Inmigration>> c_callable = dao.getImg(authorizationHeader, fw);
        c_callable.enqueue(new Callback<DataWrapper<Inmigration>>() {
            @Override
            public void onResponse(Call<DataWrapper<Inmigration>> call, Response<DataWrapper<Inmigration>> response) {
                Inmigration[] img = response.body().getData().toArray(new Inmigration[0]);
                inmigrationViewModel.add(img);

                // Next Step: Outmigration
                progres.setMessage("Downloading...");
                final OutmigrationViewModel outmigrationViewModel = new ViewModelProvider(RejectionsActivity.this).get(OutmigrationViewModel.class);
                Call<DataWrapper<Outmigration>> c_callable = dao.getOmg(authorizationHeader, fw);
                c_callable.enqueue(new Callback<DataWrapper<Outmigration>>() {
                    @Override
                    public void onResponse(Call<DataWrapper<Outmigration>> call, Response<DataWrapper<Outmigration>> response) {
                        Outmigration[] i = response.body().getData().toArray(new Outmigration[0]);
                        for (Outmigration item : i) {
                            item.edit = 1;
                            Log.d("Omg", "Edit Omg: " + item.edit + "Uuid: " +item.uuid);
                        }
                        outmigrationViewModel.add(i);

                        // Next Step: Death
                        progres.setMessage("Downloading...");
                        final DeathViewModel deathViewModel = new ViewModelProvider(RejectionsActivity.this).get(DeathViewModel.class);
                        Call<DataWrapper<Death>> c_callable = dao.getDth(authorizationHeader, fw);
                        c_callable.enqueue(new Callback<DataWrapper<Death>>() {
                            @Override
                            public void onResponse(Call<DataWrapper<Death>> call, Response<DataWrapper<Death>> response) {
                                Death[] co = response.body().getData().toArray(new Death[0]);
                                for (Death item : co) {
                                    item.edit = 1;
                                    Log.d("Dth", "Edit Dth: " + item.edit);
                                }
                                deathViewModel.add(co);

                                // Next Step: Pregnancy
                                progres.setMessage("Downloading...");
                                final PregnancyViewModel pregnancyViewModel = new ViewModelProvider(RejectionsActivity.this).get(PregnancyViewModel.class);
                                Call<DataWrapper<Pregnancy>> c_callable = dao.getPreg(authorizationHeader, fw);
                                c_callable.enqueue(new Callback<DataWrapper<Pregnancy>>() {
                                    @Override
                                    public void onResponse(Call<DataWrapper<Pregnancy>> call, Response<DataWrapper<Pregnancy>> response) {
                                        try {
                                        if (response.body() != null && response.body().getData() != null) {
                                            Pregnancy[] newPregs = response.body().getData().toArray(new Pregnancy[0]);
                                            for (Pregnancy newPregnancy : newPregs) {
                                                // Fetch the existing pregnancy record by UUID
                                                Pregnancy existingPregnancy = pregnancyViewModel.ins(newPregnancy.uuid);
                                                if (existingPregnancy != null) {
                                                    // Preserve location and id fields from the existing record
                                                    newPregnancy.extra = existingPregnancy.extra;
                                                    newPregnancy.outcome = existingPregnancy.outcome;
                                                    newPregnancy.id = existingPregnancy.id;
                                                    newPregnancy.complete = 0;

                                                    Log.d("Insertion", "Pregnancy insert outcome: " + existingPregnancy.outcome);
                                                }
                                            }
                                            pregnancyViewModel.add(newPregs);
                                        } else {
                                            Log.e("Error", "Response body or data is null");
                                        }

                                        } catch (ExecutionException e) {
                                            Log.e("Error", "ExecutionException occurred while fetching existing pregnancy: " + e.getMessage());
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            Log.e("Error", "InterruptedException occurred while fetching existing pregnancy: " + e.getMessage());
                                            e.printStackTrace();
                                            Thread.currentThread().interrupt(); // Restore interrupted status
                                        } catch (Exception e) {
                                            Log.e("Error", "An unexpected error occurred: " + e.getMessage());
                                            e.printStackTrace();
                                        }

                                        // Next Step: Demographic
                                        progres.setMessage("Downloading...");
                                        final DemographicViewModel demographicViewModel = new ViewModelProvider(RejectionsActivity.this).get(DemographicViewModel.class);
                                        Call<DataWrapper<Demographic>> c_callable = dao.getDemo(authorizationHeader, fw);
                                        c_callable.enqueue(new Callback<DataWrapper<Demographic>>() {
                                            @Override
                                            public void onResponse(Call<DataWrapper<Demographic>> call, Response<DataWrapper<Demographic>> response) {
                                                Demographic[] dm = response.body().getData().toArray(new Demographic[0]);
                                                demographicViewModel.add(dm);

                                                // Next Step: Relationship
                                                progres.setMessage("Downloading...");
                                                final RelationshipViewModel relationshipViewModel = new ViewModelProvider(RejectionsActivity.this).get(RelationshipViewModel.class);
                                                Call<DataWrapper<Relationship>> c_callable = dao.getRel(authorizationHeader, fw);
                                                c_callable.enqueue(new Callback<DataWrapper<Relationship>>() {
                                                    @Override
                                                    public void onResponse(Call<DataWrapper<Relationship>> call, Response<DataWrapper<Relationship>> response) {
                                                        Relationship[] rel = response.body().getData().toArray(new Relationship[0]);
                                                        relationshipViewModel.add(rel);

                                                        // Next Step: Vaccination
                                                        progres.setMessage("Downloading...");
                                                        final VaccinationViewModel vaccinationViewModel = new ViewModelProvider(RejectionsActivity.this).get(VaccinationViewModel.class);
                                                        Call<DataWrapper<Vaccination>> c_callable = dao.getVac(authorizationHeader, fw);
                                                        c_callable.enqueue(new Callback<DataWrapper<Vaccination>>() {
                                                        @Override
                                                        public void onResponse(Call<DataWrapper<Vaccination>> call, Response<DataWrapper<Vaccination>> response) {
                                                        Vaccination[] vac = response.body().getData().toArray(new Vaccination[0]);
                                                        vaccinationViewModel.add(vac);

                                                            // Next Step: Ses
                                                            progres.setMessage("Downloading...");
                                                            final HdssSociodemoViewModel hdssSociodemoViewModel = new ViewModelProvider(RejectionsActivity.this).get(HdssSociodemoViewModel.class);
                                                            Call<DataWrapper<HdssSociodemo>> c_callable = dao.getSes(authorizationHeader, fw);
                                                            c_callable.enqueue(new Callback<DataWrapper<HdssSociodemo>>() {
                                                                @Override
                                                                public void onResponse(Call<DataWrapper<HdssSociodemo>> call, Response<DataWrapper<HdssSociodemo>> response) {
                                                                HdssSociodemo[] ses = response.body().getData().toArray(new HdssSociodemo[0]);
                                                                hdssSociodemoViewModel.add(ses);
                                                                    // Next Step: Ses

                                                                    progres.setMessage("Downloading...");
                                                                    final MorbidityViewModel morbidityViewModel = new ViewModelProvider(RejectionsActivity.this).get(MorbidityViewModel.class);
                                                                    Call<DataWrapper<Morbidity>> c_callable = dao.getMor(authorizationHeader, fw);
                                                                    c_callable.enqueue(new Callback<DataWrapper<Morbidity>>() {
                                                                        @Override
                                                                        public void onResponse(Call<DataWrapper<Morbidity>> call, Response<DataWrapper<Morbidity>> response) {
                                                                            Morbidity[] mor = response.body().getData().toArray(new Morbidity[0]);
                                                                            morbidityViewModel.add(mor);

                                                        // Final Step: Pregnancy Outcome
                                                        progres.setMessage("Downloading...");
                                                        final PregnancyoutcomeViewModel pregout = new ViewModelProvider(RejectionsActivity.this).get(PregnancyoutcomeViewModel.class);
                                                        Call<DataWrapper<Pregnancyoutcome>> c_callable = dao.getOut(authorizationHeader, fw);
                                                        c_callable.enqueue(new Callback<DataWrapper<Pregnancyoutcome>>() {
                                                            @Override
                                                            public void onResponse(Call<DataWrapper<Pregnancyoutcome>> call, Response<DataWrapper<Pregnancyoutcome>> response) {
                                                                try {
                                                                if (response.body() != null && response.body().getData() != null) {
                                                                    Pregnancyoutcome[] newPregs = response.body().getData().toArray(new Pregnancyoutcome[0]);
                                                                    for (Pregnancyoutcome newPregnancy : newPregs) {
                                                                        // Fetch the existing outcome record by ID
                                                                        Pregnancyoutcome existingPregnancy = pregout.ins(newPregnancy.uuid);
                                                                        if (existingPregnancy != null) {
                                                                            // Preserve location and id fields from the existing record
                                                                            newPregnancy.location = existingPregnancy.location;
                                                                            newPregnancy.id = existingPregnancy.id;
                                                                            newPregnancy.complete = 0;
                                                                            newPregnancy.extra = existingPregnancy.extra;
                                                                            Log.d("Insertion", "Outcome Location: " + existingPregnancy.location);
                                                                        }
                                                                    }
                                                                    pregout.add(newPregs);
                                                                } else {
                                                                    Log.e("Error", "Response body or data is null");
                                                                }
                                                                } catch (ExecutionException e) {
                                                                    Log.e("Error", "ExecutionException occurred while fetching existing pregnancy: " + e.getMessage());
                                                                    e.printStackTrace();
                                                                } catch (InterruptedException e) {
                                                                    Log.e("Error", "InterruptedException occurred while fetching existing pregnancy: " + e.getMessage());
                                                                    e.printStackTrace();
                                                                    Thread.currentThread().interrupt(); // Restore interrupted status
                                                                } catch (Exception e) {
                                                                    Log.e("Error", "An unexpected error occurred: " + e.getMessage());
                                                                    e.printStackTrace();
                                                                }


                                                                progres.dismiss();
                                                                refreshActivity();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<DataWrapper<Pregnancyoutcome>> call, Throwable t) {
                                                                progres.dismiss();
                                                                Toast.makeText(RejectionsActivity.this, "Outcome Download Error", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }

                                                                        @Override
                                                                        public void onFailure(Call<DataWrapper<Morbidity>> call, Throwable t) {
                                                                            progres.dismiss();
                                                                            Toast.makeText(RejectionsActivity.this, "Morbidity Download Error", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onFailure(Call<DataWrapper<HdssSociodemo>> call, Throwable t) {
                                                                    progres.dismiss();
                                                                    Toast.makeText(RejectionsActivity.this, "SES Download Error", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }

                                                            @Override
                                                            public void onFailure(Call<DataWrapper<Vaccination>> call, Throwable t) {
                                                                progres.dismiss();
                                                                Toast.makeText(RejectionsActivity.this, "Vac Download Error", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(Call<DataWrapper<Relationship>> call, Throwable t) {
                                                        progres.dismiss();
                                                        Toast.makeText(RejectionsActivity.this, "Rel Download Error", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<DataWrapper<Demographic>> call, Throwable t) {
                                                progres.dismiss();
                                                Toast.makeText(RejectionsActivity.this, "Dem Download Error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<DataWrapper<Pregnancy>> call, Throwable t) {
                                        progres.dismiss();
                                        Toast.makeText(RejectionsActivity.this, "Preg Download Error", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<DataWrapper<Death>> call, Throwable t) {
                                progres.dismiss();
                                Toast.makeText(RejectionsActivity.this, "Death Download Error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DataWrapper<Outmigration>> call, Throwable t) {
                        progres.dismiss();
                        Toast.makeText(RejectionsActivity.this, "Omg Download Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<DataWrapper<Inmigration>> call, Throwable t) {
                progres.dismiss();
                Toast.makeText(RejectionsActivity.this, "Img Download Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshActivity() {
        finish(); // Finish the current activity
        startActivity(getIntent()); // Restart the activity
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
                        Intent intent = new Intent(RejectionsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        // Finish the current activity
                        RejectionsActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


}