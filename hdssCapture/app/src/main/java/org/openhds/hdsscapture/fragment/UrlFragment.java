package org.openhds.hdsscapture.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.openhds.hdsscapture.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class UrlFragment extends DialogFragment {

    private EditText editText;
    private OnUrlUpdatedListener urlUpdatedListener;

    // Define a callback interface to notify AppJson when the URL is updated
    public interface OnUrlUpdatedListener {
        void onUrlUpdated(String newUrl);
    }

    public void setOnUrlUpdatedListener(OnUrlUpdatedListener listener) {
        urlUpdatedListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_url, null);
        editText = view.findViewById(R.id.text_url);
        editText.setText(getBaseUrl());
        builder.setView(view)
                .setTitle("Set Server URL")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUrl = editText.getText().toString();
                        saveBaseUrl(newUrl);
                        if (urlUpdatedListener != null) {
                            urlUpdatedListener.onUrlUpdated(newUrl);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        return builder.create();
    }

    public String getBaseUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString("BASE_URL", "http://localhost.org:8080");
    }

    public void saveBaseUrl(String baseUrl) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BASE_URL", baseUrl);
        editor.apply();
    }
}

