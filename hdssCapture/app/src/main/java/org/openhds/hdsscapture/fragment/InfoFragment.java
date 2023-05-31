package org.openhds.hdsscapture.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends DialogFragment {



    public InfoFragment() {
        // Required empty public constructor
    }


    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Button buttonUrl = view.findViewById(R.id.button_url);
        buttonUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/arazak584/hdss_apk/blob/main/app-hdss_v2023_1.apk"); // replace with your URL
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button buttonReset = view.findViewById(R.id.button_resetDatabase);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the context of the application
                final Context context = requireContext().getApplicationContext();

                // Reset the AppDatabase
                AppDatabase appDatabase = AppDatabase.getDatabase(context);
                appDatabase.resetDatabase(context, new AppDatabase.ResetCallback() {
                    @Override
                    public void onResetComplete() {
                        // Show toast message when all entities are reset
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Database reset successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });





        return view;
    }
}