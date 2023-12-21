package org.openhds.hdsscapture.Duplicate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.NewActivity;
import org.openhds.hdsscapture.Adapter.ViewsAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.DupList;
import org.openhds.hdsscapture.entity.subqueries.Newloc;

import java.util.ArrayList;
import java.util.List;

public class DupListAdapter extends RecyclerView.Adapter<DupListAdapter.ViewHolder>{


    DuplicateActivity activity;
    LayoutInflater inflater;
    CardView cardView;
    private List<DupList> dupLists;

    private final Context context;

    public DupListAdapter(DuplicateActivity activity) {
        this.activity = activity;
        this.dupLists = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public List<DupList> getDupList() {
        return dupLists;
    }

    public void setDupLists(List<DupList> dupLists)
    {
        this.dupLists = dupLists;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,id1,id2 , id3,id4,id5,id6,id7,id8;
        TableRow tab1, tab2, tab3, tab4;
        LinearLayout linearLayout;
        CardView cardView;
        TableRow row;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.compno);
            this.id1 = view.findViewById(R.id.id1);
            this.id2 = view.findViewById(R.id.id2);
            this.id3 = view.findViewById(R.id.id3);
            this.id4 = view.findViewById(R.id.id4);
            this.id5 = view.findViewById(R.id.id5);
            this.id6 = view.findViewById(R.id.id6);
            this.id7 = view.findViewById(R.id.id7);
            this.id8 = view.findViewById(R.id.id8);
            this.tab1 = view.findViewById(R.id.tab1);
            this.tab2 = view.findViewById(R.id.tab2);
            this.tab3 = view.findViewById(R.id.tab3);
            this.tab4 = view.findViewById(R.id.tab4);
            this.cardView = view.findViewById(R.id.DupList);

        }
    }

    @NonNull
    @Override
    public DupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.duplist_item, parent, false);
        DupListAdapter.ViewHolder viewHolder = new DupListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DupListAdapter.ViewHolder holder, int position) {
        final DupList item = dupLists.get(position);
        holder.title.setText(item.title);
        holder.id1.setText(item.id1);
        holder.id2.setText(item.id2);
        holder.id3.setText(item.id3);
        holder.id4.setText(item.id4);
        holder.id5.setText(item.id5);
        holder.id6.setText(item.id6);
        holder.id7.setText(item.id7);
        holder.id8.setText(item.id8);
        holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        holder.id1.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        holder.id2.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        holder.id3.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));
        holder.id4.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));
        holder.id5.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));
        holder.id6.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));
        holder.id7.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));
        holder.id8.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Crimson));

        holder.tab1.setVisibility(View.VISIBLE);
        holder.tab2.setVisibility(View.VISIBLE);

        if (item.isTab1Visible) {
            holder.tab3.setVisibility(View.VISIBLE);
            setTextAndVisibility(holder.id5, item.id5);
            setTextAndVisibility(holder.id6, item.id6);
        } else {
            holder.tab3.setVisibility(View.GONE);
        }

        if (item.isTab2Visible) {
            holder.tab4.setVisibility(View.VISIBLE);
            setTextAndVisibility(holder.id7, item.id7);
            setTextAndVisibility(holder.id8, item.id8);
        } else {
            holder.tab4.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return dupLists.size();
    }

    private void setTextAndVisibility(TextView textView, String text) {
        if (text != null && !text.isEmpty()) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);  // Hide the TextView if text is empty or null
        }
    }
}
