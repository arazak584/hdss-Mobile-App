package org.openhds.hdsscapture;

import static org.openhds.hdsscapture.AppConstants.DATA_CAPTURE;
import static org.openhds.hdsscapture.AppConstants.DATA_DOWNLOAD;
import static org.openhds.hdsscapture.AppConstants.DATA_QUERY;
import static org.openhds.hdsscapture.AppConstants.DATA_REPORT;
import static org.openhds.hdsscapture.AppConstants.DATA_SYNC;
import static org.openhds.hdsscapture.AppConstants.DATA_VIEWS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.Activity.QueryActivity;
import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.Utilities.SimpleDialog;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.ListingViewModel;
import org.openhds.hdsscapture.Viewmodel.OutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Outcome;
import org.openhds.hdsscapture.entity.Visit;
import org.openhds.hdsscapture.fragment.InfoFragment;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ListingViewModel listingViewModel;
    private DeathViewModel deathViewModel;
    private IndividualViewModel individualViewModel;
    //private OutcomeViewModel outcomeViewModel;
    private Button send;

    private void showDialogInfo(String message, String codeFragment) {
        SimpleDialog simpleDialog = SimpleDialog.newInstance(message, codeFragment);
        simpleDialog.show(getSupportFragmentManager(), SimpleDialog.INFO_DIALOG_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent f = getIntent();
        final Fieldworker fieldworkerDatas = f.getParcelableExtra(LoginActivity.FIELDWORKER_DATAS);
        //Toast.makeText(MainActivity.this, "Welcome " + fieldworkerDatas.firstName + " " + fieldworkerDatas.lastName, Toast.LENGTH_LONG).show();

        send = findViewById(R.id.btnpush);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        //outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);

        final Button update = findViewById(R.id.btnupdate);
        update.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),HierarchyActivity.class);
            i.putExtra(LoginActivity.FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);
        });

        send.setOnClickListener(v -> {
            if (send.isEnabled()) {
                Intent i = new Intent(getApplicationContext(), PushActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(MainActivity.this, "Resolve Queries", Toast.LENGTH_SHORT).show();
            }
        });

        final Button pull = findViewById(R.id.btnpull);
        pull.setOnClickListener(v -> {
            if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 2) {
                Intent i = new Intent(getApplicationContext(), PullActivity.class);
                startActivity(i);
            } else {
                // Display a message or take appropriate action when the condition is not met
                Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        });

//        final Button remainder = findViewById(R.id.btncensus);
//        remainder.setOnClickListener(v -> {
//            Intent i = new Intent(getApplicationContext(),RemainderActivity.class);
//            startActivity(i);
//        });

        final Button control = findViewById(R.id.btnreport);
        control.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ReportActivity.class);
            startActivity(i);
        });

        final Button query = findViewById(R.id.btnquerry);
        query.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), QueryActivity.class);
            startActivity(i);
        });

        final Button views = findViewById(R.id.btnloc);
        views.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), NewActivity.class);
            startActivity(i);
        });

        final Button info = findViewById(R.id.btninfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldworkerDatas != null && fieldworkerDatas.status != null && fieldworkerDatas.status == 2) {
                    InfoFragment dialogFragment = new InfoFragment();
                    dialogFragment.show(getSupportFragmentManager(), "InfoFragment");
                } else {
                    Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        try {
            List<Listing> data = listingViewModel.error();
            if (data != null && !data.isEmpty()) {
                send.setEnabled(false);
            } else {
                send.setEnabled(true);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        deathViewModel = new ViewModelProvider(this).get(DeathViewModel.class);
        try {
            List<Death> data = deathViewModel.error();
                if (data != null) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        try {
            List<Individual> data = individualViewModel.error();
            if (data != null) {
                send.setEnabled(false);
            } else {
                send.setEnabled(true);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        individualViewModel = new ViewModelProvider(this).get(IndividualViewModel.class);
        try {
            List<Individual> data = individualViewModel.errors();
            if (data != null) {
                send.setEnabled(false);
            } else {
                send.setEnabled(true);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        outcomeViewModel = new ViewModelProvider(this).get(OutcomeViewModel.class);
//        try {
//            List<Outcome> data = outcomeViewModel.error();
//            if (data != null) {
//                send.setEnabled(false);
//            } else {
//                send.setEnabled(true);
//            }
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            List<Listing> data = listingViewModel.error();
            List<Death> dth = deathViewModel.error();
            //List<Outcome> out = outcomeViewModel.error();
            List<Individual> ind = individualViewModel.error();
            List<Individual> inds = individualViewModel.errors();
            if ((data != null && !data.isEmpty()) || (dth != null && !dth.isEmpty())
                    || (ind != null && !ind.isEmpty()) || (inds != null && !inds.isEmpty())) {
                send.setEnabled(false);
            } else {
                send.setEnabled(true);
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void startAppInfo(View view) {
        showDialogInfo(null, DATA_CAPTURE);
    }
    public void startSyncInfo(View view) {
        showDialogInfo(null, DATA_SYNC);
    }
    public void startReportInfo(View view) {
        showDialogInfo(null, DATA_REPORT);
    }
    public void startQueryInfo(View view) {
        showDialogInfo(null, DATA_QUERY);
    }
    public void startViewInfo(View view) {
        showDialogInfo(null, DATA_VIEWS);
    }
    public void startDownloadInfo(View view) {
        showDialogInfo(null, DATA_DOWNLOAD);
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
                            MainActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}