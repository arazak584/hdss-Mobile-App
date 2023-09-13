package org.openhds.hdsscapture.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.SearchView;

import org.openhds.hdsscapture.Adapter.ErrorAdapter;
import org.openhds.hdsscapture.Adapter.ViewsAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Newloc;
import org.openhds.hdsscapture.Utilities.Queries;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class NewActivity extends AppCompatActivity {

    private SocialgroupViewModel socialgroupViewModel;
    private LocationViewModel locationViewModel;
    private ProgressDialog progress;
    private ViewsAdapter viewsAdapter;
    private List<Newloc> filterAll;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        searchView = findViewById(R.id.searchloc);

        // Set a query hint
        searchView.setQueryHint(getString(R.string.search));

        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        query();
    }

    private void query() {
        List<Newloc> list = new ArrayList<>();

        try {

            final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

            int c = 1;
            for (Socialgroup e : socialgroupViewModel.repo()) {
                String formattedDate = f.format(e.insertDate);
                Newloc r1 = new Newloc();
                r1.id1 = c + ". New Household";
                r1.id2 = "" + e.extId;
                r1.id3 = "" + e.groupName;
                r1.id4 = "" + formattedDate;
                r1.index = c;

                list.add(r1);

                // Increment the counter for the next item
                c++;
            }


            int d=1;
            for (Locations e : locationViewModel.repo()) {
                String formattedDate = f.format(e.insertDate);
                Newloc r1 = new Newloc();
                r1.id1 = d + ". New Location";
                r1.id2 = "" + e.compno;
                r1.id3 = "" + e.locationName;
                r1.id4 = "" + formattedDate;
                r1.index = d;

                list.add(r1);
                d++;
            }



            filterAll = new ArrayList<>(list);

            viewsAdapter = new ViewsAdapter(this);
            viewsAdapter.setNewlocs(list);
            RecyclerView recyclerView = findViewById(R.id.my_recycler_view_all);
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
        ArrayList<Newloc> filterNames = new ArrayList<>();
        String searchQuery = s.toLowerCase(); // Convert the search query to lowercase for case-insensitive search
        for (Newloc newloc : filterAll) {
            // Convert extid, name, and error to lowercase before comparison
            if (newloc.id1.toLowerCase().contains(searchQuery) ||
                    newloc.id2.toLowerCase().contains(searchQuery) ||
                    newloc.id3.toLowerCase().contains(searchQuery) ||
                    newloc.id4.toLowerCase().contains(searchQuery)) {
                filterNames.add(newloc);
            }
        }
        viewsAdapter.setNewlocs(filterNames); // Update the adapter with filtered data
    }
}