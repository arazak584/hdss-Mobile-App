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
 * Use the {@link OtherDownloadSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherDownloadSummary extends DialogFragment {

    private static final String ARG_TOTAL_RECORDS = "arg_total_records";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_SYNC_DATE = "arg_sync_date";

    public OtherDownloadSummary() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static OtherDownloadSummary newInstance(long totalRecords, String files, String syncDate) {
        OtherDownloadSummary fragment = new OtherDownloadSummary();
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
        View view = inflater.inflate(R.layout.other_download_summary, container, false);

        Button closeButton = view.findViewById(R.id.btnOk);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView cntses = view.findViewById(R.id.syncSesSize);
        TextView cntvac = view.findViewById(R.id.syncVacSize);
        TextView ses = view.findViewById(R.id.syncTxtSes);
        TextView vac = view.findViewById(R.id.syncTxtVac);
        TextView syncDate = view.findViewById(R.id.syncDate);

        ses.setText("Total SES");
        vac.setText("Total Vaccination");

        long sesCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("ses");
        long vaccinationCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("vaccination");
        String SesDatetime = ((PullActivity) requireActivity()).getSesSyncDatetime();

        cntses.setText(String.valueOf(sesCount));
        cntvac.setText(String.valueOf(vaccinationCount));
        syncDate.setText(SesDatetime);


        return view;
    }
}