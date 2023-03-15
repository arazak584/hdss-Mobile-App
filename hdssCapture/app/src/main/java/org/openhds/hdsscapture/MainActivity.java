package org.openhds.hdsscapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.openhds.hdsscapture.Activity.HierarchyActivity;
import org.openhds.hdsscapture.Activity.LoginActivity;
import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.Activity.PushActivity;
import org.openhds.hdsscapture.Activity.RemainderActivity;

public class MainActivity extends AppCompatActivity {

    Button btnupdate, btnpush, btnpull, btncensus, btnamendment, btninfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final TextView textView_Username = findViewById(R.id.text_fieldextid);
        //final Intent intent = getIntent();
        //String name = intent.getStringExtra("username");
        //Toast.makeText(MainActivity.this, "User Name "+name, Toast.LENGTH_LONG).show();
        //textView_Username.setText("Welcome " + name);

        btnupdate=findViewById(R.id.btnupdate);
        btnpush=findViewById(R.id.btnpush);
        btnpull=findViewById(R.id.btnpull);
        btncensus=findViewById(R.id.btncensus);
        btnamendment=findViewById(R.id.btnamendment);
        btninfo=findViewById(R.id.btninfo);

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, HierarchyActivity.class);
                startActivity(intent);
            }
        });

        btnpush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PushActivity.class);
                startActivity(intent);
            }
        });

        btnpull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PullActivity.class);
                startActivity(intent);
            }
        });

        btncensus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RemainderActivity.class);
                startActivity(intent);
            }
        });

        btnamendment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(MainActivity.this, Expenseslist.class);
                //startActivity(intent);
            }
        });

        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}