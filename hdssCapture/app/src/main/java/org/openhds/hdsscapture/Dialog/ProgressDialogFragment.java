package org.openhds.hdsscapture.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Use the {@link ProgressDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressDialogFragment extends DialogFragment {


    private static final String ARG_TOTAL_RECORDS = "arg_total_records";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_SYNC_DATE = "arg_sync_date";

    public ProgressDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProgressDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressDialogFragment newInstance(long totalRecords, String files, String syncDate) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
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
        View view = inflater.inflate(R.layout.fragment_progress_dialog, container, false);

//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
//        String lastSyncDatetime = sharedPreferences.getString("lastSyncDatetime", "");


        Button closeButton = view.findViewById(R.id.btnOk);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView cntloc = view.findViewById(R.id.cntLoc);
        TextView cntsoc = view.findViewById(R.id.cntSoc);
        TextView cntind = view.findViewById(R.id.cntInd);
        TextView cntres = view.findViewById(R.id.cntRes);
        TextView cntpreg = view.findViewById(R.id.cntPreg);
        TextView cntrel = view.findViewById(R.id.cntRel);
        TextView cntdem = view.findViewById(R.id.cntDem);
        TextView cntses = view.findViewById(R.id.cntSes);
        TextView cntvac = view.findViewById(R.id.cntVac);
        TextView locDate = view.findViewById(R.id.LocDate);
        TextView indDate = view.findViewById(R.id.IndDate);
        TextView resDate = view.findViewById(R.id.ResDate);
        TextView socDate = view.findViewById(R.id.SocDate);
        TextView pregDate = view.findViewById(R.id.PregDate);
        TextView relDate = view.findViewById(R.id.RelDate);
        TextView demDate = view.findViewById(R.id.DemDate);
        TextView sesDate = view.findViewById(R.id.SesDate);
        TextView vacDate = view.findViewById(R.id.VacDate);

        // Inside your fragment class
        long locationCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("location");
        long individualCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("individual");
        long socialgroupCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("socialgroup");
        long residencyCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("residency");
        long pregnancyCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("pregnancy");
        long relationshipCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("relationship");
        long demographicCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("demographic");
        long sesCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("ses");
        long vaccinationCount = ((PullActivity) requireActivity()).getCountFromSharedPreferences("vaccination");
        // Inside your fragment
        String IndSyncDatetime = ((PullActivity) requireActivity()).getIndSyncDatetime();
        String EventsDatetime = ((PullActivity) requireActivity()).getEventsSyncDatetime();
        String SesDatetime = ((PullActivity) requireActivity()).getSesSyncDatetime();


        cntloc.setText(String.valueOf(locationCount));
        cntind.setText(String.valueOf(individualCount));
        cntsoc.setText(String.valueOf(socialgroupCount));
        cntres.setText(String.valueOf(residencyCount));
        cntpreg.setText(String.valueOf(pregnancyCount));
        cntrel.setText(String.valueOf(relationshipCount));
        cntdem.setText(String.valueOf(demographicCount));
        cntses.setText(String.valueOf(sesCount));
        cntvac.setText(String.valueOf(vaccinationCount));
        locDate.setText(IndSyncDatetime);
        indDate.setText(IndSyncDatetime);
        resDate.setText(IndSyncDatetime);
        socDate.setText(IndSyncDatetime);
        pregDate.setText(EventsDatetime);
        relDate.setText(EventsDatetime);
        demDate.setText(EventsDatetime);
        sesDate.setText(SesDatetime);
        vacDate.setText(SesDatetime);


        return view;
    }
}