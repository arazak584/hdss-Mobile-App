package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.HouseVisitFragment;

import java.util.List;

public class HouseholdViewAdapter extends RecyclerView.Adapter<HouseholdViewAdapter.ViewHolder> {

    private final Location location;
    private final Residency residency;
    private final List<Socialgroup> socialgroups;
    private final HouseVisitFragment houseVisitFragment;

    public HouseholdViewAdapter(Socialgroup socialgroup, Location location, Individual individual, Residency residency, List<Socialgroup> socialgroups, HouseVisitFragment houseVisitFragment) {

        this.location = location;
        this.residency = residency;
        this.socialgroups = socialgroups;
        this.houseVisitFragment = houseVisitFragment;
    }

    public List<Socialgroup> getSocialgroups() {
        return socialgroups;
    }

    @NonNull
    @Override
    public HouseholdViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_households, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Socialgroup socialgroup = socialgroups.get(position);

        holder.textView_id.setText(socialgroup.extId);
        //holder.textView_label.setText(message);
    }

    @Override
    public int getItemCount() {

        return socialgroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_label, textView_id;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView_id = (TextView) itemView.findViewById(R.id.textView_id);
            this.textView_label = (TextView) itemView.findViewById(R.id.textView_label);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_lastevent);
        }
    }
}
