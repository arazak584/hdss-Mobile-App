package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ReportAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.ReportCounter;
import org.openhds.hdsscapture.Viewmodel.AmendmentViewModel;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.ResidencyViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VaccinationViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ReportActivity extends AppCompatActivity {

    private IndividualViewModel individualViewModel;
    private VisitViewModel visitViewModel;
    private VisitViewModel locsvisitViewModel;
    private LocationViewModel locationViewModel;
    private SocialgroupViewModel socialgroupViewModel;
    private InmigrationViewModel inmigrationViewModel;
    private OutmigrationViewModel outmigrationViewModel;
    private PregnancyViewModel pregnancyViewModel;
    private PregnancyoutcomeViewModel pregnancyoutcomeViewModel;
    private DeathViewModel deathViewModel;
    private DemographicViewModel demographicViewModel;
    private HdssSociodemoViewModel hdssSociodemoViewModel;
    private RelationshipViewModel relationshipViewModel;
    private ListingViewModel listingViewModel;
    private AmendmentViewModel amendmentViewModel;
    private VaccinationViewModel vaccinationViewModel;
    private ResidencyViewModel residencyViewModel;
    private ReportAdapter reportAdapter;

    private EditText startDateEditText, endDateEditText, usernameEditText;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        startDateEditText = findViewById(R.id.startDate);
        endDateEditText = findViewById(R.id.endDate);
        usernameEditText = findViewById(R.id.Fusername);

        Button startDateButton = findViewById(R.id.btStart);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open DatePickerDialog for start date selection
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date on EditText
                        calendar.set(year, month, dayOfMonth);

                        startDateEditText.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        Button endDateButton = findViewById(R.id.btEnd);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open DatePickerDialog for end date selection
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date on EditText
                        calendar.set(year, month, dayOfMonth);

                        endDateEditText.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        locsvisitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        socialgroupViewModel = new ViewModelProvider(this).get(SocialgroupViewModel.class);
        inmigrationViewModel = new ViewModelProvider(this).get(InmigrationViewModel.class);
        outmigrationViewModel = new ViewModelProvider(this).get(OutmigrationViewModel.class);
        pregnancyViewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        pregnancyoutcomeViewModel = new ViewModelProvider(this).get(PregnancyoutcomeViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        demographicViewModel = new ViewModelProvider(this).get(DemographicViewModel.class);
        hdssSociodemoViewModel = new ViewModelProvider(this).get(HdssSociodemoViewModel.class);
        relationshipViewModel = new ViewModelProvider(this).get(RelationshipViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        amendmentViewModel = new ViewModelProvider(this).get(AmendmentViewModel.class);
        vaccinationViewModel = new ViewModelProvider(this).get(VaccinationViewModel.class);
        residencyViewModel = new ViewModelProvider(this).get(ResidencyViewModel.class);

        Button generateReportButton = findViewById(R.id.bt_report);
        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                // Simulate long operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        report();
                        hideLoadingDialog(); // Dismiss the progress dialog after generating the report
                    }
                }, 500);

            }

            public void showLoadingDialog() {
                if (progress == null) {
                    progress = new ProgressDialog(ReportActivity.this);
                    progress.setTitle("Generating Report...");
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

    private void report() {
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Retrieve the text entered in the start and end date EditText views
        String startDateText = startDateEditText.getText().toString().trim();
        String endDateText = endDateEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        // Parse the text into Date objects
        try {
            startDate = f.parse(startDateText);
            endDate = f.parse(endDateText);

           // Toast.makeText(this, "executed " + startDateText, Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            List<ReportCounter> list = new ArrayList<>();



            ReportCounter individualCounter = new ReportCounter();
            individualCounter.name = "Individual";
            individualCounter.count = individualViewModel.countIndividuals(startDate, endDate, username);
            individualCounter.index = 0;
            list.add(0, individualCounter);

            ReportCounter memCounter = new ReportCounter();
            memCounter.name = "Membership";
            memCounter.count = residencyViewModel.count(startDate, endDate, username);
            memCounter.index = 1;
            list.add(1, memCounter);

            ReportCounter visitCounter = new ReportCounter();
            visitCounter.name = "Household Visit";
            visitCounter.count = visitViewModel.countVisits(startDate, endDate, username);
            visitCounter.index = 2;
            list.add(2, visitCounter);


            ReportCounter locsCounter = new ReportCounter();
            locsCounter.name = "New Compound";
            locsCounter.count = locationViewModel.count(startDate, endDate, username);
            locsCounter.index = 3;
            list.add(3, locsCounter);

            ReportCounter listCounter = new ReportCounter();
            listCounter.name = "Listing";
            listCounter.count = listingViewModel.count(startDate, endDate, username);
            listCounter.index = 4;
            list.add(4, listCounter);

            ReportCounter nhseCounter = new ReportCounter();
            nhseCounter.name = "New Household";
            nhseCounter.count = socialgroupViewModel.count(startDate, endDate, username);
            nhseCounter.index = 5;
            list.add(5, nhseCounter);

//            int c=1;
//            for(Socialgroup e: socialgroupViewModel.findAll(startDate, endDate)){
//                ReportCounter r1 = new ReportCounter();
//                r1.name = "e-date "+e.insertDate;
//                r1.count = c++;
//                r1.index = c;
//
//                list.add(r1);
//
//            }

            ReportCounter imgCounter = new ReportCounter();
            imgCounter.name = "Inmigration";
            imgCounter.count = inmigrationViewModel.count(startDate, endDate, username);
            imgCounter.index = 6;
            list.add(6, imgCounter);

            ReportCounter omgCounter = new ReportCounter();
            omgCounter.name = "Outmigration";
            omgCounter.count = outmigrationViewModel.count(startDate, endDate, username);
            omgCounter.index = 7;
            list.add(7, omgCounter);

            ReportCounter pregCounter = new ReportCounter();
            pregCounter.name = "Pregnancy";
            pregCounter.count = pregnancyViewModel.count(startDate, endDate, username);
            pregCounter.index = 8;
            list.add(8, pregCounter);

            ReportCounter outcomeCounter = new ReportCounter();
            outcomeCounter.name = "Pregnancy Outcome";
            outcomeCounter.count = pregnancyoutcomeViewModel.count(startDate, endDate, username);
            outcomeCounter.index = 9;
            list.add(9, outcomeCounter);

            ReportCounter demoCounter = new ReportCounter();
            demoCounter.name = "Demographic";
            demoCounter.count = demographicViewModel.count(startDate, endDate, username);
            demoCounter.index = 10;
            list.add(10, demoCounter);

            ReportCounter sesCounter = new ReportCounter();
            sesCounter.name = "Household Profile";
            sesCounter.count = hdssSociodemoViewModel.count(startDate, endDate, username);
            sesCounter.index = 11;
            list.add(11, sesCounter);

            ReportCounter dthCounter = new ReportCounter();
            dthCounter.name = "Death";
            dthCounter.count = deathViewModel.count(startDate, endDate, username);
            dthCounter.index = 12;
            list.add(12, dthCounter);

            ReportCounter amendCounter = new ReportCounter();
            amendCounter.name = "Amendment";
            amendCounter.count = amendmentViewModel.count(startDate, endDate, username);
            amendCounter.index = 13;
            list.add(13, amendCounter);

            ReportCounter relationshipCounter = new ReportCounter();
            relationshipCounter.name = "Relationship";
            relationshipCounter.count = relationshipViewModel.count(startDate, endDate, username);
            relationshipCounter.index = 14;
            list.add(14, relationshipCounter);

            ReportCounter vacCounter = new ReportCounter();
            vacCounter.name = "Vaccination";
            vacCounter.count = vaccinationViewModel.count(startDate, endDate, username);
            vacCounter.index = 15;
            list.add(15, vacCounter);


            reportAdapter = new ReportAdapter(this);
            reportAdapter.setReportCounter(list);
            RecyclerView recyclerView = findViewById(R.id.my_recycler_view_report);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(reportAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            ReportActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}
