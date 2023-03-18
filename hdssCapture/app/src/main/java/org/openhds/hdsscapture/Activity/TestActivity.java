package org.openhds.hdsscapture.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Hierarchy;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Intent intent = getIntent();

        final Hierarchy selectedLevel5 = intent.getParcelableExtra(HierarchyActivity.VILLAGE_DATA);

        final TextView villageInfo = findViewById(R.id.text_Vi);
        villageInfo.setText(selectedLevel5.getName());
    }
}