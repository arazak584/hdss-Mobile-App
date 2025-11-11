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
    private SearchView searchView;
    private String username;
    private Handler handler;

    // Reusable date formatter to avoid creating multiple instances
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handler = new Handler();

        recyclerView = findViewById(R.id.my_recycler_view_query);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        errorAdapter = new QueryAdapter(this);
        errorAdapter.setQueries(new ArrayList<>());
        recyclerView.setAdapter(errorAdapter);

        setupSearchView();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LoginActivity.FW_UUID_KEY, null);

        initializeViewModels();

        Button generateQueryButton = findViewById(R.id.btn_query);
        generateQueryButton.setOnClickListener(v -> generateQueries());

        // Automatically load queries on startup
        handler.postDelayed(this::generateQueries, 300);
    }

    private void setupSearchView() {
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
    }

    private void initializeViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        socialgroupViewModel = provider.get(SocialgroupViewModel.class);
        deathViewModel = provider.get(DeathViewModel.class);
        individualViewModel = provider.get(IndividualViewModel.class);
        demographicViewModel = provider.get(DemographicViewModel.class);
        outcomeViewModel = provider.get(OutcomeViewModel.class);
        residencyViewModel = provider.get(ResidencyViewModel.class);
        listingViewModel = provider.get(ListingViewModel.class);
        hdssSociodemoViewModel = provider.get(HdssSociodemoViewModel.class);
        pregnancyoutcomeViewModel = provider.get(PregnancyoutcomeViewModel.class);
    }

    private void generateQueries() {
        showLoadingDialog();

        new Thread(() -> {
            try {
                List<Queries> list = fetchQueries();
                runOnUiThread(() -> {
                    try {
                        errorAdapter.setQueries(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        hideLoadingDialog();
                    }
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

    private List<Queries> fetchQueries() {
        // Use initial capacity estimate to reduce reallocations
        List<Queries> list = new ArrayList<>(100);

        // Process each query type and immediately add to list
        addPregnancyOutcomeQueries(list);
        addNullIdQueries(list);
        addSocioEconomicQueries(list);
        addListingQueries(list);
        addDeathQueries(list);
        addMinorHohQueries(list);
        addUnknownHohQueries(list);
        addOutcomeErrorQueries(list);
        addNotUpdatedHohQueries(list);

        return list;
    }

    private void addPregnancyOutcomeQueries(List<Queries> list) {
        try {
            List<Pregnancyoutcome> errors = pregnancyoutcomeViewModel.error(username);
            int index = list.size() + 1;

            for (Pregnancyoutcome e : errors) {
                Queries q = new Queries();
                q.name = index + ". PerM ID: " + e.pregnancy_uuid;
                q.extid = "Compno: " + e.location + " - Name: " + e.mother_uuid + " " + e.father_uuid;
                q.error = "Incomplete Pregnancy Outcome Form";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNullIdQueries(List<Queries> list) {
        try {
            List<Individual> nulls = individualViewModel.nulls();
            int index = list.size() + 1;

            for (Individual e : nulls) {
                Queries q = new Queries();
                q.name = index + ". Household ID: " + e.hohID;
                q.extid = "Compno: " + e.compno + " - " + e.firstName + " " + e.lastName;
                q.date = "PerID: " + e.extId;
                q.error = "NULL IDs";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSocioEconomicQueries(List<Queries> list) {
        try {
            List<HdssSociodemo> errors = hdssSociodemoViewModel.error();
            int index = list.size() + 1;

            for (HdssSociodemo e : errors) {
                Queries q = new Queries();
                q.name = "Socio-Economic";
                q.extid = "Compno: " + e.id0021 + " - Household Head: " + e.visit_uuid;
                q.date = "Household ID: " + e.form_comments_txt;
                q.error = "Incomplete SES Form";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addListingQueries(List<Queries> list) {
        try {
            List<Listing> errors = listingViewModel.error();
            int index = list.size() + 1;

            for (Listing e : errors) {
                Queries q = new Queries();
                q.name = index + ". Compno: " + e.compno;
                q.extid = "Cluster: " + e.compextId + " - Compound Name: " + e.locationName;
                q.error = "Listing Not Picked";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDeathQueries(List<Queries> list) {
        try {
            List<Death> errors = deathViewModel.error();
            int index = list.size() + 1;

            for (Death e : errors) {
                Queries q = new Queries();
                q.name = index + ". Compno: " + e.compno;
                q.extid = "Household ID: " + e.lastName + " - Household Head: " + e.firstName;
                q.error = "Change Head of Household [HOH is Dead]";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMinorHohQueries(List<Queries> list) {
        try {
            List<Individual> errors = individualViewModel.error();
            int index = list.size() + 1;

            for (Individual e : errors) {
                Queries q = new Queries();
                q.name = index + ". Household ID: " + e.getHohID();
                q.extid = e.compno + " - " + e.firstName + " " + e.lastName;
                q.error = "Household Head is a Minor";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUnknownHohQueries(List<Queries> list) {
        try {
            List<Individual> errors = individualViewModel.err();
            int index = list.size() + 1;

            for (Individual e : errors) {
                Queries q = new Queries();
                q.name = index + ". Household ID: " + e.getHohID();
                q.extid = "Compno: " + e.compno;
                q.error = "Head of Household is Unknown";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOutcomeErrorQueries(List<Queries> list) {
        try {
            List<Outcome> errors = outcomeViewModel.error(username);
            int index = list.size() + 1;

            for (Outcome e : errors) {
                Queries q = new Queries();
                q.name = index + ". Compno: " + e.childuuid;
                q.extid = "PermID: " + e.extId + " - " + e.firstName + " " + e.lastName;
                q.error = "Outcome Error (Pregnancy Outcome incomplete)";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotUpdatedHohQueries(List<Queries> list) {
        try {
            List<Individual> errors = individualViewModel.errors();
            int index = list.size() + 1;

            for (Individual e : errors) {
                Queries q = new Queries();
                q.name = index + ". Household ID: " + e.getHohID();
                q.extid = "Compno: " + e.compno + " - " + e.firstName + " " + e.lastName;
                q.error = "Head of Household Not Updated";
                q.index = index++;
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }
}