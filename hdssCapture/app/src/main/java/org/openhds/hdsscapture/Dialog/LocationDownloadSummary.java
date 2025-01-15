package org.openhds.hdsscapture.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationDownloadSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDownloadSummary extends DialogFragment {

    private static final String ARG_TOTAL_RECORDS = "arg_total_records";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_SYNC_DATE = "arg_sync_date";

    public LocationDownloadSummary() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationDownloadSummary newInstance(long totalRecords, String files, String syncDate) {
        LocationDownloadSummary fragment = new LocationDownloadSummary();
        Bundle args = new Bundle();
        args.putLong(ARG_TOTAL_RECORDS, totalRecords);
        args.putString(ARG_FILES, files);
        args.putString(ARG_SYNC_DATE, syncDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            long totalRecords = args.getLong(ARG_TOTAL_RECORDS, 0);
            String files = args.getString(ARG_FILES, "");
            String syncDate = getArguments().getString(ARG_SYNC_DATE, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.location_download_summary, container, false);

        Button closeButton = view.findViewById(R.id.btnOk);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView cntloc = view.findViewById(R.id.syncLocSize);
        TextView cntsoc = view.findViewById(R.id.syncSocSize);
        TextView loc = view.findViewById(R.id.syncTxtLoc);
        TextView soc = view.findViewById(R.id.syncTxtSoc);
        TextView syncDate = view.findViewById(R.id.syncDate);

        loc.setText("Total Locations");
        soc.setText("Total Socialgroup");

        long locationCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("location");
        long socialgroupCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("socialgroup");
        String LocSyncDatetime = ((PullActivity) requireActivity()).getLastLocDatetime();

        cntloc.setText(String.valueOf(locationCount));
        cntsoc.setText(String.valueOf(socialgroupCount));
        syncDate.setText(LocSyncDatetime);

        return view;
    }
}