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

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder>{

    QueryActivity activity;
    LayoutInflater inflater;
    private List<Queries> queries;

    private List<Queries> filter;

    private final Context context;

    public QueryAdapter(Context context) {
        this.context = context;
        this.queries = new ArrayList<>();
    }


    public QueryAdapter(QueryActivity activity) {
        this.activity = activity;
        this.queries = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchNotes(List<Queries> filterName) {
        if (filterName != null) {
            this.filter = filterName;
        } else {
            this.filter = new ArrayList<>(); // Reset to an empty list if null
        }
        notifyDataSetChanged();
    }


    public List<Queries> getQueries() {
        return queries;
    }

    public void setQueries(List<Queries> queries)
    {
        this.queries = queries;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,edate , extid,error;
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
        QueryAdapter.ViewHolder viewHolder = new QueryAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QueryAdapter.ViewHolder holder, int position) {
        final Queries item = queries.get(position);
        holder.title.setText(item.name);
        holder.edate.setText(item.date);
        holder.extid.setText(item.extid);
        holder.error.setText(item.error);
        holder.error.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return queries.size();
    }
}
