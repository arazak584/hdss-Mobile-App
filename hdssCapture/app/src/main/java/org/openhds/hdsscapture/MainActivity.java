package org.openhds.hdsscapture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.openhds.hdsscapture.Activity.ErrorActivity;
import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.Activity.RemainderActivity;
import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.fragment.InfoFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button update = findViewById(R.id.btnupdate);
        update.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),HierarchyActivity.class);
            startActivity(i);
        });

        final Button send = findViewById(R.id.btnpush);
        send.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),PushActivity.class);
            startActivity(i);
        });

        final Button pull = findViewById(R.id.btnpull);
        pull.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),PullActivity.class);
            startActivity(i);
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
            Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
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

                InfoFragment dialogFragment = new InfoFragment();

                // Show the dialog fragment
                dialogFragment.show(getSupportFragmentManager(), "InfoFragment");

            }
        });
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