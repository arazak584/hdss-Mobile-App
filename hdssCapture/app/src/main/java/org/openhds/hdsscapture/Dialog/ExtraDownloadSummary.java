package org.openhds.hdsscapture.Dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.openhds.hdsscapture.Activity.PullActivity;
import org.openhds.hdsscapture.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExtraDownloadSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtraDownloadSummary extends DialogFragment {

    private static final String ARG_TOTAL_RECORDS = "arg_total_records";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_SYNC_DATE = "arg_sync_date";

    public ExtraDownloadSummary() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ExtraDownloadSummary newInstance(long totalRecords, String files, String syncDate) {
        ExtraDownloadSummary fragment = new ExtraDownloadSummary();
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
        View view = inflater.inflate(R.layout.extra_download_summary, container, false);

        Button closeButton = view.findViewById(R.id.btnOk);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView cntpreg = view.findViewById(R.id.syncPregSize);
        TextView cntrel = view.findViewById(R.id.syncRelSize);
        TextView cntdem = view.findViewById(R.id.syncDemSize);
        TextView preg = view.findViewById(R.id.syncTxtPreg);
        TextView rel = view.findViewById(R.id.syncTxtRel);
        TextView dem = view.findViewById(R.id.syncTxtDem);
        TextView syncDate = view.findViewById(R.id.syncDate);

        preg.setText("Total Pregnancy");
        rel.setText("Total Relationship");
        dem.setText("Total Demographics");

        long pregnancyCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("pregnancy");
        long relationshipCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("relationship");
        long demographicCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("demographic");
        String EventsDatetime = ((PullActivity) requireActivity()).getEventsSyncDatetime();

        cntpreg.setText(String.valueOf(pregnancyCount));
        cntrel.setText(String.valueOf(relationshipCount));
        cntdem.setText(String.valueOf(demographicCount));
        syncDate.setText(EventsDatetime);


        return view;
    }
}