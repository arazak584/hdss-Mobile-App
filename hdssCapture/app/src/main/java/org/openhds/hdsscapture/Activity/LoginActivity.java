package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.MainActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.FieldworkerViewModel;
import org.openhds.hdsscapture.entity.Fieldworker;
import org.openhds.hdsscapture.fragment.UrlFragment;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity  {

    private ApiDao dao;
    private ProgressDialog progress;
    private Fieldworker fieldworkerData;
    private AppJson appJson;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appJson = AppJson.getInstance(this);

        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

        final EditText username = findViewById(R.id.text_username);
        final EditText password = findViewById(R.id.text_password);

        progress = new ProgressDialog(LoginActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        final Button button_SyncFw = findViewById(R.id.button_SyncFieldworkerData);
        button_SyncFw.setOnClickListener(v -> {
            final TextView textView_SyncFw = findViewById(R.id.textView_SyncFieldworkerData);
            textView_SyncFw.setText("");
            progress.show();

            progress.setMessage("Updating User Access...");
            final FieldworkerViewModel viewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

            Call<DataWrapper<Fieldworker>> c_callable = dao.getFw();
            c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                @Override
                public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                    Fieldworker[] d = response.body().getData().toArray(new Fieldworker[0]);
                    viewModel.add(d);
                    progress.dismiss();
                    textView_SyncFw.setText("USER ACCESS Updated!");
                    textView_SyncFw.setTextColor(Color.GREEN);
                }

                @Override
                public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {

                    progress.dismiss();
                    textView_SyncFw.setText("Fieldworker Sync Error!");
                    textView_SyncFw.setTextColor(Color.RED);
                }
            });
        });


        final Button start = findViewById(R.id.btnLogin);
        start.setOnClickListener(v -> {

            if(username.getText().toString()==null || username.getText().toString().trim().isEmpty()){
                username.setError("Invalid user or PIN");
                Toast.makeText(this,"Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            if(password.getText().toString()==null || password.getText().toString().trim().isEmpty()){
                password.setError("Invalid user or PIN");
                Toast.makeText(this,"Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = username.getText().toString();
            final String mypass = password.getText().toString();

            try {
                fieldworkerData = fieldworkerViewModel.find(myuser, mypass);

            } catch (ExecutionException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(fieldworkerData == null){
                username.setError("Invalid user or PIN");
                Toast.makeText(this,"Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            username.setError(null);
            password.setText(null);
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);



        });
    }

    public AppJson getAppJson() {
        return appJson;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_url) { // replace menu_item_id with your actual menu item ID
            // create and open the dialog fragment
            UrlFragment dialogFragment = new UrlFragment();
            dialogFragment.show(getSupportFragmentManager(), "UrlFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                            LoginActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


}