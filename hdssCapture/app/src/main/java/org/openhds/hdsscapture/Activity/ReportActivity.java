package org.openhds.hdsscapture.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Adapter.ReportAdapter;
import org.openhds.hdsscapture.Dao.ReportDao;
import org.openhds.hdsscapture.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private EditText date_picker_date1;
    private EditText date_picker_date2;
    private Calendar calendar1;
    private Calendar calendar2;
    private Button bt_report;

    // Initialize the ReportDao instance
    private ReportDao reportDao;
    private ReportAdapter reportAdapter;
    private RecyclerView my_recycler_view_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        bt_report = findViewById(R.id.bt_report);
        date_picker_date1 = findViewById(R.id.date_picker_date1);
        date_picker_date2 = findViewById(R.id.date_picker_date2);

        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        my_recycler_view_report = findViewById(R.id.my_recycler_view_report);


        bt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fetch data from the database based on the selected date range

                String startDateStr = date_picker_date1.getText().toString();
                String endDateStr = date_picker_date2.getText().toString();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


                try {
                    Date startDate = dateFormat.parse(startDateStr);
                    Date endDate = dateFormat.parse(endDateStr);

                    // Call the appropriate ReportDao methods to get the counts
                    LiveData<Integer> visitsLiveData = reportDao.countVisits(startDate, endDate);
                    LiveData<Integer> deathsLiveData = reportDao.countDeaths(startDate, endDate);
                    LiveData<Integer> socialgroupsLiveData = reportDao.countSocialgroups(startDate, endDate);
                    LiveData<Integer> individualsLiveData = reportDao.countIndividuals(startDate, endDate);

                    // Observe the LiveData objects to get the counts
                    visitsLiveData.observe(ReportActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            // Create a report string for the visits count
                            String report = "Visits count: " + count;

                            // Add the report string and LiveData object to the list
                            List<Pair<String, LiveData<Integer>>> reportList = new ArrayList<>();
                            Pair<String, LiveData<Integer>> reportPair = new Pair<>(report, visitsLiveData);
                            reportList.add(reportPair);

                            // Create a ReportAdapter and set it on the RecyclerView
                            ReportAdapter reportAdapter = new ReportAdapter(reportList);
                            my_recycler_view_report.setAdapter(reportAdapter);
                        }
                    });;

                    deathsLiveData.observe(ReportActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            // Create a report string for the deaths count
                            String report = "Deaths count: " + count;

                            // Add the report string and LiveData object to the list
                            List<Pair<String, LiveData<Integer>>> reportList = new ArrayList<>();
                            Pair<String, LiveData<Integer>> reportPair = new Pair<>(report, deathsLiveData);
                            reportList.add(reportPair);

                            // Create a ReportAdapter and set it on the RecyclerView
                            ReportAdapter reportAdapter = new ReportAdapter(reportList);
                            my_recycler_view_report.setAdapter(reportAdapter);
                        }
                    });

                    socialgroupsLiveData.observe(ReportActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            // Create a report string for the deaths count
                            String report = "Social groups count: " + count;

                            // Add the report string and LiveData object to the list
                            List<Pair<String, LiveData<Integer>>> reportList = new ArrayList<>();
                            Pair<String, LiveData<Integer>> reportPair = new Pair<>(report, socialgroupsLiveData);
                            reportList.add(reportPair);

                            // Create a ReportAdapter and set it on the RecyclerView
                            ReportAdapter reportAdapter = new ReportAdapter(reportList);
                            my_recycler_view_report.setAdapter(reportAdapter);
                        }
                    });

                    individualsLiveData.observe(ReportActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            // Create a report string for the deaths count
                            String report = "Individual count: " + count;

                            // Add the report string and LiveData object to the list
                            List<Pair<String, LiveData<Integer>>> reportList = new ArrayList<>();
                            Pair<String, LiveData<Integer>> reportPair = new Pair<>(report, individualsLiveData);
                            reportList.add(reportPair);

                            // Create a ReportAdapter and set it on the RecyclerView
                            ReportAdapter reportAdapter = new ReportAdapter(reportList);
                            my_recycler_view_report.setAdapter(reportAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar(date_picker_date1, calendar1);
            }
        };

        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar(date_picker_date2, calendar2);
            }
        };

        date_picker_date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReportActivity.this, date1, calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MONTH),calendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date_picker_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReportActivity.this, date2, calendar2.get(Calendar.YEAR),
                        calendar2.get(Calendar.MONTH),calendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateCalendar(EditText editText, Calendar calendar){
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
        editText.setText(sdf.format(calendar.getTime()));
    }


}