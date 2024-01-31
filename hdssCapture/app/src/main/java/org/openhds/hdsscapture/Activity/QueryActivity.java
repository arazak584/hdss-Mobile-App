package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ErrorAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.Queries;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Outcome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class QueryActivity extends AppCompatActivity {

    private SocialgroupViewModel socialgroupViewModel;
    private DeathViewModel deathViewModel;
    private IndividualViewModel individualViewModel;
    private DemographicViewModel demographicViewModel;
    private OutcomeViewModel outcomeViewModel;
    private ResidencyViewModel residencyViewModel;
    private ListingViewModel listingViewModel;
    private ProgressDialog progress;
    private RecyclerView recyclerView;

    private ErrorAdapter errorAdapter;
    private List<Queries> filterAll;
    private SearchView searchView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        searchView = findViewById(R.id.search);

        // Set a query hint
        searchView.setQueryHint(getString(R.string.search));

        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        Button generateQueryButton = findViewById(R.id.btn_query);

        generateQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

//                query();
//                hideLoadingDialog();

                // Simulate long operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        query();
                        hideLoadingDialog(); // Dismiss the progress dialog after generating the report
                    }
                }, 500);

            }

            public void showLoadingDialog() {
                if (progress == null) {
                    progress = new ProgressDialog(QueryActivity.this);
                    progress.setTitle("Generating Query...");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setMessage(getString(R.string.please_wait_lbl));
                    progress.setCancelable(false);
                }
                progress.show();
            }

            public void hideLoadingDialog() {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });

    }
    private void query() {
        List<Queries> list = new ArrayList<>();

        try {

            final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            final SimpleDateFormat z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

//            int c=1;
//            for (Socialgroup e : socialgroupViewModel.error()) {
//                //String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = c + ". Household ID: " + e.extId;
//                r1.extid = "Compno: " + e.visit_uuid + " - Household Head: " + e.groupName;
//                r1.date = "";
//                r1.error = "UNK as Respondent";
//                r1.index = c;
//
//                list.add(r1);
//                c++;
//            }

//            int n=1;
//            for (Socialgroup e : socialgroupViewModel.error()) {
//                //String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = "Household " ;
//                r1.extid = "Cluster: " + e.extId + " - Household Head: " + e.groupName;
//                r1.date = "Household ID" + e.complete;
//                r1.error = "New Households Created ";
//                r1.index = n;
//
//                list.add(r1);
//
//            }

            int d=1;
            for (Death e : deathViewModel.error()) {
                //String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = d + ". Compno: " + e.compno ;
                r1.extid = "Household ID: " + e.lastName + " - Household Head: " +e.firstName;
                r1.date = "";
                r1.error = "Change Head of Household [HOH is Dead]";
                r1.index = d;

                list.add(r1);
                d++;

            }

            int g=1;
            for (Individual e : individualViewModel.error()) {
                //String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = g + ". Household ID: " + e.getHohID();
                r1.extid = "" + e.compno + " - " +e.firstName + " " + e.lastName;
                r1.date = "";
                r1.error = "Household Head is a Minor";
                r1.index = g;
                list.add(r1);
                g++;

            }

            int k=1;
            for (Individual e : individualViewModel.errors()) {
                //String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = k + ". Household ID: " +  e.getHohID();
                r1.extid = "Compno: " + e.compno;
                r1.date = "";
                r1.error = "Only Minors Left in Household";
                r1.index = k;
                list.add(r1);
                k++;
            }

//            int h=1;
//            for (Individual e : individualViewModel.err()) {
//                //String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = h + ". Household ID: " +  e.getHohID();
//                r1.extid = "Compno: " + e.compno + " - Househead: " + e.lastName;
//                r1.date = "";
//                r1.error = "Head of Household is Unknown";
//                r1.index = h;
//                list.add(r1);
//                h++;
//
//            }

            int i=1;
            for (Outcome e : outcomeViewModel.error()) {
                //String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = i + ". Compno: " + e.childuuid;
                r1.extid = "PermID" + " " + e.extId + " - " + e.firstName + " " + e.lastName;
//                if (e.extId != null && e.extId.length() >= 9) {
//                    r1.date = "Compno" + " " + e.extId.substring(0, 9); // Extract the first nine characters of extId
//                } else {
//                    r1.date = "";
//                }
                r1.date = "";
                r1.error = "Outcome Error (Pregnancy Outcome incomplete)";
                r1.index = i;
                list.add(r1);
                i++;

            }

            int l=1;
            for (Listing e : listingViewModel.error()) {
                //String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = l + ". Compno: " + e.compno;
                r1.extid = "Cluster: " + e.compextId + " - Compound Name: " +e.locationName;
                r1.date = "";
                r1.error = "Listing Not Picked";
                r1.index = l;
                list.add(r1);
                l++;

            }

//            int o=1;
//            for (Individual e : individualViewModel.merror()) {
//                //String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = o + ". Household ID: " +  e.houseExtId;
//                r1.extid = "Compno: " + e.compextId + " - " + e.firstName + " " + e.lastName;
//                r1.date = "";
//                r1.error = "Non existent mother";
//                r1.index = o;
//                list.add(r1);
//                o++;
//
//            }
//
//            int n=1;
//            for (Individual e : individualViewModel.ferror()) {
//                //String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = n + ". Household ID: " +  e.houseExtId;
//                r1.extid = "Compno: " + e.compextId + " - " + e.firstName + " " + e.lastName;
//                r1.date = "";
//                r1.error = "Non existent father";
//                r1.index = n;
//                list.add(r1);
//                n++;
//
//            }
            

//            int h=1;
//            for (Demographic e : demographicViewModel.error()) {
//                String formattedDate = z.format(e.sttime);
//                Queries r1 = new Queries();
//                r1.name = "Demo " + e.sttime;
//                r1.extid = "" + e.individual_uuid;
//                r1.date = "" ;
//                r1.error = "Demo" + formattedDate;
//                r1.index = h;
//
//                list.add(r1);
//
//            }

//            int m=1;
//            for (Residency e : residencyViewModel.error()) {
////                String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = "Residency " + " - " + e.socialgroup_uuid;
//                r1.extid = "" + e.uuid + " - " +e.location_uuid;
//                r1.date = "" + e.socialgroup_uuid;
//                r1.error = "No Socialgroup";
//                r1.index = m;
//                list.add(r1);
//
//            }

            filterAll = new ArrayList<>(list);

            errorAdapter = new ErrorAdapter(this);
            errorAdapter.setQueries(list);
            RecyclerView recyclerView = findViewById(R.id.my_recycler_view_query);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(errorAdapter);

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search, menu);
//        MenuItem item = menu.findItem(R.id.app_bar_search);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setQueryHint("Search Notes Here...");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                sFilter(s);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    private void sFilter(String s) {
        ArrayList<Queries> filterNames = new ArrayList<>();
        String searchQuery = s.toLowerCase(); // Convert the search query to lowercase for case-insensitive search
        for (Queries query : filterAll) {
            // Convert extid, name, and error to lowercase before comparison
            if (query.extid.toLowerCase().contains(searchQuery) ||
                    query.name.toLowerCase().contains(searchQuery) ||
                    query.date.toLowerCase().contains(searchQuery) ||
                    query.error.toLowerCase().contains(searchQuery)) {
                filterNames.add(query);
            }
        }
        errorAdapter.setQueries(filterNames); // Update the adapter with filtered data
    }



}