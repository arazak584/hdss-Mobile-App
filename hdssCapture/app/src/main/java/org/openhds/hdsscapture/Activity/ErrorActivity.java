package org.openhds.hdsscapture.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ErrorAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Queries;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ErrorActivity extends AppCompatActivity {

    private SocialgroupViewModel socialgroupViewModel;
    private DeathViewModel deathViewModel;

    private ErrorAdapter errorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);

        query();
    }
    private void query() {
        List<Queries> list = new ArrayList<>();

        try {

            final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

            int c=1;
            for (Socialgroup e : socialgroupViewModel.error()) {
                String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = "Socialgroup " ;
                r1.extid = "" + e.extId;
                r1.date = "" + formattedDate;
                r1.error = "Incomplete Socialgroup";
                r1.index = c;

                list.add(r1);

            }

            int d=1;
            for (Death e : deathViewModel.error()) {
                String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = "Death " ;
                r1.extid = "" + e.compno + " - " +e.firstName + " " + e.lastName;
                r1.date = "" + formattedDate;
                r1.error = "Change Head of Household";
                r1.index = d;

                list.add(r1);

            }

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
    }

}