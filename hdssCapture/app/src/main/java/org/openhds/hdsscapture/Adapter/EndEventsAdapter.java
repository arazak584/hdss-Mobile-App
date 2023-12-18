package org.openhds.hdsscapture.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.EndEvents;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.util.ArrayList;
import java.util.List;

public class EndEventsAdapter extends RecyclerView.Adapter<EndEventsAdapter.ViewHolder>{

    HouseMembersFragment activity;
    LayoutInflater inflater;
    private List<EndEvents> queries;

    private List<EndEvents> filter;

    //private final Context context;

    public EndEventsAdapter(Context context,HouseMembersFragment activity) {
        this.activity = activity;
        this.queries = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public List<EndEvents> getQueries() {
        return queries;
    }

    public void setQueries(List<EndEvents> queries)
    {
        this.queries = queries;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,edate , extid,error;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.query_title);
            this.edate = view.findViewById(R.id.txt_date);
            this.extid = view.findViewById(R.id.txt_extid);
            this.error = view.findViewById(R.id.txt_query);
            this.cardView = view.findViewById(R.id.hdssQuery);

        }
    }

    @NonNull
    @Override
    public EndEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.endevents_item, parent, false);
        EndEventsAdapter.ViewHolder viewHolder = new EndEventsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EndEventsAdapter.ViewHolder holder, int position) {
        final EndEvents item = queries.get(position);
        holder.title.setText(item.eventName);
        holder.edate.setText(item.eventDate);
        holder.extid.setText(item.eventId);
        holder.error.setText(item.eventError);
        holder.error.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return queries.size();
    }
}
