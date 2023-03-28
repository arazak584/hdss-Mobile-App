package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.fragment.RemainderFragment;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final Intent i = getIntent();
        final Hierarchy level5Data = i.getParcelableExtra(RemainderActivity.LEVEL5_DATA);

        final TextView villageInfo = findViewById(R.id.text_Villname);
        villageInfo.setText(level5Data.getName());

        loadFragment(RemainderFragment.newInstance(level5Data));

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