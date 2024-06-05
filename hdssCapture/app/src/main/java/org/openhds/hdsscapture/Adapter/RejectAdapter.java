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

import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.Activity.RejectionsActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.Newloc;
import org.openhds.hdsscapture.entity.subqueries.RejectEvent;

import java.util.ArrayList;
import java.util.List;

public class RejectAdapter extends RecyclerView.Adapter<RejectAdapter.ViewHolder>{

    RejectionsActivity activity;
    LayoutInflater inflater;
    CardView cardView;
    private List<RejectEvent> rejectEvents;

    private List<RejectEvent> filter;

    private final Context context;

    public RejectAdapter(RejectionsActivity activity) {
        this.activity = activity;
        this.rejectEvents = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public void searchNotes(List<RejectEvent> filterName) {
        if (filterName != null) {
            this.filter = filterName;
        } else {
            this.filter = new ArrayList<>(); // Reset to an empty list if null
        }
        notifyDataSetChanged();
    }

    public List<RejectEvent> getRejectEvents() {
        return rejectEvents;
    }

    public void setRejectEvents(List<RejectEvent> rejectEvents)
    {
        this.rejectEvents = rejectEvents;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,id2 , id3,id4,id5;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.txt_id1);
            this.id2 = view.findViewById(R.id.txt_id2);
            this.id3 = view.findViewById(R.id.txt_id3);
            this.id4 = view.findViewById(R.id.txt_id4);
            this.id5 = view.findViewById(R.id.txt_id5);
            this.cardView = view.findViewById(R.id.newLoc);

        }
    }

    @NonNull
    @Override
    public RejectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.reject_item, parent, false);
        RejectAdapter.ViewHolder viewHolder = new RejectAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RejectAdapter.ViewHolder holder, int position) {
        final RejectEvent item = rejectEvents.get(position);
        holder.title.setText(item.id1);
        holder.id2.setText(item.id2);
        holder.id3.setText(item.id3);
        holder.id4.setText(item.id4);
        holder.id5.setText(item.id5);
        holder.title.setTextColor(Color.parseColor("#32CD32"));

    }

    @Override
    public int getItemCount() {
        return rejectEvents.size();
    }
}
