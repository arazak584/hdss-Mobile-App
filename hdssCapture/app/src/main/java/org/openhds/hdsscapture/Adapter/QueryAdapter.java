package org.openhds.hdsscapture.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.QueryActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.Queries;

import java.util.ArrayList;
import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private QueryActivity activity;
    private LayoutInflater inflater;
    private List<Queries> queries;
    private List<Queries> filter;

    public QueryAdapter(QueryActivity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.queries = new ArrayList<>();
        this.filter = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchNotes(String query) {
        if (query == null || query.isEmpty()) {
            filter.clear();
            filter.addAll(queries);
        } else {
            filter.clear();
            for (Queries item : queries) {
                if (item.name.toLowerCase().contains(query.toLowerCase()) ||
                        item.date.toLowerCase().contains(query.toLowerCase()) ||
                        item.extid.toLowerCase().contains(query.toLowerCase()) ||
                        item.error.toLowerCase().contains(query.toLowerCase())) {
                    filter.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Queries> getQueries() {
        return queries;
    }

    public void setQueries(List<Queries> queries) {
        this.queries = queries;
        filter.clear();
        filter.addAll(queries);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, edate, extid, error;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.query_title);
            this.edate = view.findViewById(R.id.txt_date);
            this.extid = view.findViewById(R.id.txt_extid);
            this.error = view.findViewById(R.id.txt_query);
            this.linearLayout = view.findViewById(R.id.hdssQuery);
        }
    }

    @NonNull
    @Override
    public QueryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.query_item, parent, false);
        return new QueryAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull QueryAdapter.ViewHolder holder, int position) {
        Queries item = filter.get(position);
        holder.title.setText(item.name);
        holder.edate.setText(item.date);
        holder.extid.setText(item.extid);
        holder.error.setText(item.error);
        holder.error.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return filter.size();
    }
}