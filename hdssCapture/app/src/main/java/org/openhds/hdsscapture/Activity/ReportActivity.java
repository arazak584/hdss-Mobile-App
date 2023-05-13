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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ReportAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

    private IndividualViewModel individualViewModel;
    private VisitViewModel visitViewModel;
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
    private ReportAdapter reportAdapter;

    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private Visit visit;

    private EditText startDateEditText, endDateEditText, usernameEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Report...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

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
                        String selectedDate = year + "-" + (month+1) + "-" + dayOfMonth;
                        startDateEditText.setText(selectedDate);
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
                        String selectedDate = year + "-" + (month+1) + "-" + dayOfMonth;
                        endDateEditText.setText(selectedDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        visitViewModel = new ViewModelProvider(this).get(VisitViewModel.class);
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

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view_report);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this);
        recyclerView.setAdapter(reportAdapter);

        Button generateReportButton = findViewById(R.id.bt_report);
        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                // Simulate long operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 10);
                report();
            }
        });
    }

    private void report() {
        Date startDate = null;
        Date endDate = null;
        //String username = null;

        // Retrieve the text entered in the start and end date EditText views
        String startDateText = startDateEditText.getText().toString().trim();
        String endDateText = endDateEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        // Parse the text into Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = dateFormat.parse(startDateText);
            endDate = dateFormat.parse(endDateText);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        reportAdapter.report(startDate, endDate, username, individualViewModel, visitViewModel, locationViewModel,socialgroupViewModel,inmigrationViewModel,
                outmigrationViewModel,pregnancyViewModel,pregnancyoutcomeViewModel,deathViewModel,demographicViewModel,hdssSociodemoViewModel,relationshipViewModel);

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
