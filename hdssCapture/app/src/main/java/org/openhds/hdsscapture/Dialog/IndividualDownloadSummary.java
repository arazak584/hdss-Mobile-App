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
 * Use the {@link IndividualDownloadSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualDownloadSummary extends DialogFragment {

    private static final String ARG_TOTAL_RECORDS = "arg_total_records";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_SYNC_DATE = "arg_sync_date";

    public IndividualDownloadSummary() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static IndividualDownloadSummary newInstance(long totalRecords, String files, String syncDate) {
        IndividualDownloadSummary fragment = new IndividualDownloadSummary();
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
        View view = inflater.inflate(R.layout.individual_download_summary, container, false);

        Button closeButton = view.findViewById(R.id.btnOk);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView cntres = view.findViewById(R.id.syncResSize);
        TextView cntind = view.findViewById(R.id.syncIndSize);
        TextView res = view.findViewById(R.id.syncTxtRes);
        TextView ind = view.findViewById(R.id.syncTxtInd);
        TextView syncDate = view.findViewById(R.id.syncDate);

        res.setText("Total Residency");
        ind.setText("Total Individuals");

        long individualCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("individual");
        long residencyCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("residency");
        String IndSyncDatetime = ((PullActivity) requireActivity()).getIndSyncDatetime();

        cntind.setText(String.valueOf(individualCount));
        cntres.setText(String.valueOf(residencyCount));
        syncDate.setText(IndSyncDatetime);

        return view;
    }
}