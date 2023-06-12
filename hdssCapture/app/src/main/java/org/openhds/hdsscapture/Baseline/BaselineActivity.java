package org.openhds.hdsscapture.Baseline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Locations;

public class BaselineActivity extends AppCompatActivity {

    public static Locations TOP_LOCATION = new Locations();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline);

        final Intent j = getIntent();
        final Hierarchy level5Data = j.getParcelableExtra(HierarchyActivity.LEVEL5_DATA);

        final Intent i = getIntent();
        final Hierarchy level6Data = i.getParcelableExtra(HierarchyActivity.LEVEL6_DATA);


        final TextView level5 = findViewById(R.id.level5);
        final TextView level6 = findViewById(R.id.level6);

        if (level5Data != null) {
            level5.setText(level5Data.getName());
        } else {
            // Handle the case where location is null
            level6.setText("Error loading Village data");
        }

        if (level6Data != null) {
            level6.setText(level6Data.getExtId());
        } else {
            // Handle the case where location is null
            level6.setText("Error loading Locations data");
        }



        loadFragment(BaseFragment.newInstance(level6Data));

        final ExtendedFloatingActionButton home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            loadFragment(BaseFragment.newInstance(level6Data));
        });
    }


    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.container_baseline, fragment);
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
                            BaselineActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}