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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Utilities.Newloc;

import java.util.ArrayList;
import java.util.List;

public class ViewsAdapter extends RecyclerView.Adapter<ViewsAdapter.ViewHolder>{

    NewActivity activity;
    LayoutInflater inflater;
    CardView cardView;
    private List<Newloc> newlocs;

    private List<Newloc> filter;

    private final Context context;

    public ViewsAdapter(NewActivity activity) {
        this.activity = activity;
        this.newlocs = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchNotes(List<Newloc> filterName) {
        if (filterName != null) {
            this.filter = filterName;
        } else {
            this.filter = new ArrayList<>(); // Reset to an empty list if null
        }
        notifyDataSetChanged();
    }

    public List<Newloc> getNewlocs() {
        return newlocs;
    }

    public void setNewlocs(List<Newloc> newlocs)
    {
        this.newlocs = newlocs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,id2 , id3,id4;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.txt_id1);
            this.id2 = view.findViewById(R.id.txt_id2);
            this.id3 = view.findViewById(R.id.txt_id3);
            this.id4 = view.findViewById(R.id.txt_id4);
            this.cardView = view.findViewById(R.id.newLoc);

        }
    }

    @NonNull
    @Override
    public ViewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.newloc_item, parent, false);
        ViewsAdapter.ViewHolder viewHolder = new ViewsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsAdapter.ViewHolder holder, int position) {
        final Newloc item = newlocs.get(position);
        holder.title.setText(item.id1);
        holder.id2.setText(item.id2);
        holder.id3.setText(item.id3);
        holder.id4.setText(item.id4);
        holder.title.setTextColor(Color.parseColor("#32CD32"));

    }

    @Override
    public int getItemCount() {
        return newlocs.size();
    }
}
