package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Dialog.FilterDialogFragment;
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
    private Fieldworker fieldworkerDatas;
    private ProgressBar progressBar;
    private AppJson appJson;

    public static final String FIELDWORKER_DATA = "org.openhds.hdsscapture.activity.HierarchyActivity.FIELDWORKER_DATA";
    public static final String FIELDWORKER_DATAS = "org.openhds.hdsscapture.activity.MainActivity.FIELDWORKER_DATAS";


    public static boolean isScreenSizeGreaterThanEqual7Inch(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);

            // Calculate the physical screen size in inches
            double physicalWidth = metrics.widthPixels / (double) metrics.xdpi;
            double physicalHeight = metrics.heightPixels / (double) metrics.ydpi;
            double screenSizeInInches = Math.sqrt(physicalWidth * physicalWidth + physicalHeight * physicalHeight);

            return screenSizeInInches >= 7.0;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check the screen size before proceeding
        if (!isScreenSizeGreaterThanEqual7Inch(this)) {
            // Display a message for small screens
            Toast.makeText(this, "Enabled on Tablet Only", Toast.LENGTH_LONG).show();
            finish(); // Finish the activity and prevent the user from proceeding
            return;
        }

        appJson = AppJson.getInstance(this);

        final FieldworkerViewModel fieldworkerViewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

        final EditText username = findViewById(R.id.text_username);
        final EditText password = findViewById(R.id.text_password);

        progress = new ProgressDialog(LoginActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressBar = findViewById(R.id.login_progress);

        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        final Button button_SyncFw = findViewById(R.id.button_SyncFieldworkerData);
        button_SyncFw.setOnClickListener(v -> {
            final TextView textView_SyncFw = findViewById(R.id.textView_SyncFieldworkerData);
            textView_SyncFw.setText("");

            // Show the ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            final FieldworkerViewModel viewModel = new ViewModelProvider(this).get(FieldworkerViewModel.class);

            Call<DataWrapper<Fieldworker>> c_callable = dao.getFw();
            c_callable.enqueue(new Callback<DataWrapper<Fieldworker>>() {
                @Override
                public void onResponse(Call<DataWrapper<Fieldworker>> call, Response<DataWrapper<Fieldworker>> response) {
                    Fieldworker[] d = response.body().getData().toArray(new Fieldworker[0]);
                    viewModel.add(d);
                    progressBar.setVisibility(View.GONE);
                    textView_SyncFw.setText("USER ACCESS UPDATED!");
                    textView_SyncFw.setTextColor(Color.GREEN);
                }

                @Override
                public void onFailure(Call<DataWrapper<Fieldworker>> call, Throwable t) {

                    progressBar.setVisibility(View.GONE);
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
                fieldworkerDatas = fieldworkerViewModel.find(myuser, mypass);

            } catch (ExecutionException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(fieldworkerDatas == null){
                username.setError("Invalid user or PIN");
                Toast.makeText(this,"Please provide a valid user and PIN", Toast.LENGTH_LONG).show();
                return;
            }

            username.setError(null);
            password.setText(null);
            final Intent i = new Intent(this, MainActivity.class);
            i.putExtra(FIELDWORKER_DATAS, fieldworkerDatas);
            startActivity(i);

        });

        final Button send = findViewById(R.id.apiSettings);
        send.setOnClickListener(v -> {
            // Create an instance of the UrlFragment
            UrlFragment dialogFragment = new UrlFragment();

            // Show the dialog fragment using the FragmentManager
            dialogFragment.show(getSupportFragmentManager(), "UrlFragment");
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