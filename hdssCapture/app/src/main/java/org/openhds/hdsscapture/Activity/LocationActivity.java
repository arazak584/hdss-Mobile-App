package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

public class LocationActivity extends AppCompatActivity {

    public static Locations TOP_LOCATION = new Locations();
    private ImageView home;
    private Locations locations;
    private Socialgroup socialgroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final Intent j = getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final Intent i = getIntent();
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);

        final Intent f = getIntent();
        final Fieldworker fieldworkerData = f.getParcelableExtra(HierarchyActivity.FIELDWORKER_DATA);

        //Toast.makeText(LocationActivity.this, "Welcome " + fieldworkerData.firstName + " " + fieldworkerData.lastName, Toast.LENGTH_LONG).show();


        final TextView level5 = findViewById(R.id.level5);
        final TextView level6 = findViewById(R.id.level6);
        final TextView fw = findViewById(R.id.fw_loc);

        if (level5Data != null) {
            level5.setText(level5Data.getName());
        } else {
            // Handle the case where location is null
            level6.setText("Error loading Village data");
        }

        if (level6Data != null) {
            level6.setText(level6Data.getName());
        } else {
            // Handle the case where location is null
            level6.setText("Error loading Locations data");
        }

        if (fw != null) {
            fw.setText(fieldworkerData.firstName + " " + fieldworkerData.lastName);
        } else {
            // Handle the case where location is null
            fw.setText("Error loading Fieldworker data");
        }



        loadFragment(ClusterFragment.newInstance(level6Data,locations,socialgroup));

        final Button home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            loadFragment(ClusterFragment.newInstance(level6Data,locations,socialgroup));
        });

    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.container_cluster, fragment);
        fragmentTransaction.commit(); // save the changes
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
                            LocationActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}