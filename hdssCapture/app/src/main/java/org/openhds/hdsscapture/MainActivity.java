package org.openhds.hdsscapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
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

        final Button remainder = findViewById(R.id.btncensus);
        remainder.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),RemainderActivity.class);
            startActivity(i);
        });

        final Button control = findViewById(R.id.btnreport);
        control.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ReportActivity.class);
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
}