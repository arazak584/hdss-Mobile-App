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

import org.openhds.hdsscapture.Activity.ErrorActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Queries;

import java.util.ArrayList;
import java.util.List;

public class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ViewHolder>{

    ErrorActivity activity;
    LayoutInflater inflater;
    private List<Queries> queries;

    private List<Queries> filter;

    private Context context;

    public ErrorAdapter(Context context) {
        this.context = context;
        this.queries = new ArrayList<>();
    }


    public ErrorAdapter(ErrorActivity activity) {
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
    public ErrorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.query_item, parent, false);
        ErrorAdapter.ViewHolder viewHolder = new ErrorAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ErrorAdapter.ViewHolder holder, int position) {
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
