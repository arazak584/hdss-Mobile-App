package org.openhds.hdsscapture.odk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OdkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odk);

        final OdkViewModel odkViewModel = new ViewModelProvider(this).get(OdkViewModel.class);

        // In your OdkActivity onCreate method
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Form> forms = null;
        try {
            forms = odkViewModel.find();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Move the adapter creation after obtaining the list of forms
        OdkFormAdapter odkFormAdapter = new OdkFormAdapter(forms);
        recyclerView.setAdapter(odkFormAdapter);

        // Now you can proceed with creating buttons if needed
        for (Form form : forms) {
            Button button = new Button(this);
            button.setText("Open Form " + form.formID);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOdkForm(form.formID);
                }
            });

            LinearLayout buttonsContainer = findViewById(R.id.buttonsContainer);
            buttonsContainer.addView(button);
        }
    }

    private void openOdkForm(String formID) {
        // Create a new Intent with the action ACTION_VIEW
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Set the data type of the intent to indicate that it is a specific ODK form
        intent.setType("vnd.android.cursor.dir/vnd.odk.form");
        // Set the data (form ID) in the intent
        Uri specificFormUri = Uri.withAppendedPath(FormsProviderAPI.FormsColumns.CONTENT_URI, formID);
        intent.setData(specificFormUri);
        // Start the activity with the created intent
        startActivity(intent);
    }
}