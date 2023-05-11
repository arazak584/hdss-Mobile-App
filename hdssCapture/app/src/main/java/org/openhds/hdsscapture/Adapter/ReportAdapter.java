package org.openhds.hdsscapture.Adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<Pair<String, LiveData<Integer>>> reportData;

    public ReportAdapter(List<Pair<String,LiveData<Integer>>> reportData) {
        this.reportData = reportData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reportData.get(position));
    }

    @Override
    public int getItemCount() {
        return reportData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView reportTitleTextView;
        private TextView reportCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTextView = itemView.findViewById(R.id.report_title);
            reportCountTextView = itemView.findViewById(R.id.report_count);
        }

        public void bind(Pair<String, LiveData<Integer>> reportItem) {
            reportTitleTextView.setText(reportItem.first);
            reportItem.second.observe((LifecycleOwner) itemView.getContext(), count -> reportCountTextView.setText(String.valueOf(count)));
        }
    }
}


