package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ErrorAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Queries;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ErrorActivity extends AppCompatActivity {

    private SocialgroupViewModel socialgroupViewModel;
    private DeathViewModel deathViewModel;
    private IndividualViewModel individualViewModel;
    private DemographicViewModel demographicViewModel;
    private ProgressDialog progress;

    private ErrorAdapter errorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);

        Button generateQueryButton = findViewById(R.id.btn_query);
        generateQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

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
                    progress = new ProgressDialog(ErrorActivity.this);
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

            int c=1;
            for (Socialgroup e : socialgroupViewModel.error()) {
                String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = "Visit " ;
                r1.extid = "" + e.extId;
                r1.date = "" + e.groupName;
                r1.error = "UNK as Respondent";
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

            int g=1;
            for (Individual e : individualViewModel.error()) {
                String formattedDate = f.format(e.insertDate);
                Queries r1 = new Queries();
                r1.name = "Socialgroup " ;
                r1.extid = "" + e.compextId + " - " +e.firstName + " " + e.lastName;
                r1.date = "" + e.houseExtId;
                r1.error = "Household Head is a Minor";
                r1.index = g;

                list.add(r1);

            }

//            int h=1;
//            for (Demographic e : demographicViewModel.error()) {
//                String formattedDate = f.format(e.insertDate);
//                Queries r1 = new Queries();
//                r1.name = "Demo " ;
//                r1.extid = "" + e.individual_uuid;
//                r1.date = "" + formattedDate;
//                r1.error = "Demo";
//                r1.index = h;
//
//                list.add(r1);
//
//            }

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