package org.openhds.hdsscapture.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.ReportCounter;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    ReportActivity activity;
    LayoutInflater inflater;
    private List<ReportCounter> reportCounter;

    private Context context;

    public ReportAdapter(ReportActivity activity) {
        this.activity = activity;
        this.reportCounter = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public List<ReportCounter> getReportCounter() {
        return reportCounter;
    }

    public void setReportCounter(List<ReportCounter> reportCounter) {
        this.reportCounter = reportCounter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView indcnt, title;
        LinearLayout linearLayout;

        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.indcnt = view.findViewById(R.id.report_count);
            this.title = view.findViewById(R.id.report_title);
            this.cardView = view.findViewById(R.id.hdssReport);

        }
    }


    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.report_item, parent, false);
        ReportAdapter.ViewHolder viewHolder = new ReportAdapter.ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        final ReportCounter item = reportCounter.get(position);
        holder.title.setText(item.name);
        holder.indcnt.setText(""+item.count);

    }


    @Override
    public int getItemCount() {
        return reportCounter.size();
    }



}
