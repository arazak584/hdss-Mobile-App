package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.QueryAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.*;
import org.openhds.hdsscapture.entity.*;
import org.openhds.hdsscapture.entity.subqueries.Queries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QueryActivity extends AppCompatActivity {

    private SocialgroupViewModel socialgroupViewModel;
    private DeathViewModel deathViewModel;
    private IndividualViewModel individualViewModel;
    private DemographicViewModel demographicViewModel;
    private OutcomeViewModel outcomeViewModel;
    private ResidencyViewModel residencyViewModel;
    private ListingViewModel listingViewModel;
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;

    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private QueryAdapter errorAdapter;
    private List<Queries> filterAll;
    private SearchView searchView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        recyclerView = findViewById(R.id.my_recycler_view_query);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterAll = new ArrayList<>();

        // Initialize the adapter with the QueryActivity instance
        errorAdapter = new QueryAdapter(this);

        // Set the list of queries to the adapter
        errorAdapter.setQueries(filterAll);
        recyclerView.setAdapter(errorAdapter);

        searchView = findViewById(R.id.search);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                errorAdapter.searchNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                errorAdapter.searchNotes(newText);
                return true;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        // Initialize ViewModels
        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);

        Button generateQueryButton = findViewById(R.id.btn_query);
        generateQueryButton.setOnClickListener(v -> generateQueries());

        // Automatically load queries on startup
        new Handler().postDelayed(this::generateQueries, 300);
    }

    private void generateQueries() {
        showLoadingDialog();

        new Thread(() -> {
            try {
                List<Queries> list = fetchQueries();
                runOnUiThread(() -> {
                    filterAll.clear();
                    filterAll.addAll(list);
                    errorAdapter.setQueries(filterAll);
                    hideLoadingDialog();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideLoadingDialog();
                    e.printStackTrace();
                    // Optional: show a toast or dialog with error info
                });
            }
        }).start();
    }

    private List<Queries> fetchQueries() throws Exception {
        List<Queries> list = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        int c = 1;
        for (Pregnancyoutcome e : pregnancyoutcomeViewModel.error(username)) {
            Queries q = new Queries();
            q.name = c + ". PerM ID: " + e.pregnancy_uuid;
            q.extid = "Compno: " + e.location + " - Name: " + e.mother_uuid + " " + e.father_uuid;
            q.error = "Incomplete Pregnancy Outcome Form";
            q.index = c++;
            list.add(q);
        }

        int o = 1;
        for (Individual e : individualViewModel.nulls()) {
            Queries q = new Queries();
            q.name = o + ". Household ID: " + e.hohID;
            q.extid = "Compno: " + e.compno + " - " + e.firstName + " " + e.lastName;
            q.date = "PerID: " + e.extId;
            q.error = "NULL IDs";
            q.index = o++;
            list.add(q);
        }

        int n = 1;
        for (HdssSociodemo e : hdssSociodemoViewModel.error()) {
            Queries q = new Queries();
            q.name = "Socio-Economic";
            q.extid = "Compno: " + e.id0021 + " - Household Head: " + e.visit_uuid;
            q.date = "Household ID: " + e.form_comments_txt;
            q.error = "Incomplete SES Form";
            q.index = n++;
            list.add(q);
        }

        int l = 1;
        for (Listing e : listingViewModel.error()) {
            Queries q = new Queries();
            q.name = l + ". Compno: " + e.compno;
            q.extid = "Cluster: " + e.compextId + " - Compound Name: " + e.locationName;
            q.error = "Listing Not Picked";
            q.index = l++;
            list.add(q);
        }

        int d = 1;
        for (Death e : deathViewModel.error()) {
            Queries q = new Queries();
            q.name = d + ". Compno: " + e.compno;
            q.extid = "Household ID: " + e.lastName + " - Household Head: " + e.firstName;
            q.error = "Change Head of Household [HOH is Dead]";
            q.index = d++;
            list.add(q);
        }

        int g = 1;
        for (Individual e : individualViewModel.error()) {
            Queries q = new Queries();
            q.name = g + ". Household ID: " + e.getHohID();
            q.extid = e.compno + " - " + e.firstName + " " + e.lastName;
            q.error = "Household Head is a Minor";
            q.index = g++;
            list.add(q);
        }

        int h = 1;
        for (Individual e : individualViewModel.err()) {
            Queries q = new Queries();
            q.name = h + ". Household ID: " + e.getHohID();
            q.extid = "Compno: " + e.compno;
            q.error = "Head of Household is Unknown";
            q.index = h++;
            list.add(q);
        }

        int i = 1;
        for (Outcome e : outcomeViewModel.error(username)) {
            Queries q = new Queries();
            q.name = i + ". Compno: " + e.childuuid;
            q.extid = "PermID: " + e.extId + " - " + e.firstName + " " + e.lastName;
            q.error = "Outcome Error (Pregnancy Outcome incomplete)";
            q.index = i++;
            list.add(q);
        }

        int k = 1;
        for (Individual e : individualViewModel.errors()) {
            Queries q = new Queries();
            q.name = k + ". Household ID: " + e.getHohID();
            q.extid = "Compno: " + e.compno + " - " + e.firstName + " " + e.lastName;
            q.error = "Head of Household Not Updated";
            q.index = k++;
            list.add(q);
        }

        return list;
    }

    private void showLoadingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Generating Query...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage(getString(R.string.please_wait_lbl));
            progress.setCancelable(false);
        }
        progress.show();
    }

    private void hideLoadingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}