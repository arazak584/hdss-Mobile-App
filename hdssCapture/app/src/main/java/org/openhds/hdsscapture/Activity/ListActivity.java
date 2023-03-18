package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Village;
import org.openhds.hdsscapture.fragment.BlankFragment;
import org.openhds.hdsscapture.fragment.RemainderFragment;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final Intent i = getIntent();
        final Village villageData = i.getParcelableExtra(HierarchyActivity.VILLAGE_DATA);

        final TextView villageInfo = findViewById(R.id.text_Villname);
        villageInfo.setText(villageData.getName());

        loadFragment(RemainderFragment.newInstance(villageData));

    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.container_remainder, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}