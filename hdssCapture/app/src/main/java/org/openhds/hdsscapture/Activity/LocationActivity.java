package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.Village;
import org.openhds.hdsscapture.fragment.BlankFragment;

public class LocationActivity extends AppCompatActivity {

    public static final String LOCATION_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.LOCATION_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final TextView textView_Username = findViewById(R.id.text_Fieldworker);
        final Intent p = getIntent();
        String name = p.getStringExtra("username");
        textView_Username.setText(" "+ name);


        final Intent intent = getIntent();
        final Cluster clusterData = intent.getParcelableExtra(HierarchyActivity.CLUSTER_DATA);

        final Intent i = getIntent();
        final Village villageData = i.getParcelableExtra(HierarchyActivity.VILLAGE_DATA);


        final TextView cluster = findViewById(R.id.text_Cluster);
        cluster.setText(clusterData.getExtId());

        final TextView villageInfo = findViewById(R.id.text_Village);
        villageInfo.setText(villageData.getName());

        loadFragment(BlankFragment.newInstance(clusterData));

        final ExtendedFloatingActionButton home = findViewById(R.id.homeAction);
        home.setOnClickListener(view -> {
            loadFragment(BlankFragment.newInstance(clusterData));
        });
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.container_main, fragment);
        fragmentTransaction.commit(); // save the changes
    }


}