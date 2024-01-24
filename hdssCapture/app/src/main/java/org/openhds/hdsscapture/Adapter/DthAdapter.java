package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.DeathFragment;
import org.openhds.hdsscapture.fragment.DthFragment;
import org.openhds.hdsscapture.fragment.DtheditFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DthAdapter extends RecyclerView.Adapter<DthAdapter.ViewHolder> {

    DtheditFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Death death;
    private Individual individual;
    private final List<Death> deathList;

    public DthAdapter(DtheditFragment activity, Locations locations, Socialgroup socialgroup,Death death) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.death = death;
        deathList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, dod, insertDate;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.dth_permid);
            this.firstname = view.findViewById(R.id.dth_firstname);
            this.lastname = view.findViewById(R.id.dth_lastname);
            this.dod = view.findViewById(R.id.text_dod);
            this.insertDate = view.findViewById(R.id.text_insertdate);
            this.cardView = view.findViewById(R.id.deadIndividual);
        }
    }

    @NonNull
    @Override
    public DthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.dth_itemlist, parent, false);
        DthAdapter.ViewHolder viewHolder = new DthAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull DthAdapter.ViewHolder holder, int position) {
        final Death dth = deathList.get(position);


        holder.permid.setText(dth.getExtId());
        holder.firstname.setText(dth.getFirstName());
        holder.lastname.setText(dth.getLastName());
        holder.dod.setText(dth.getDeathDate());
        holder.insertDate.setText(dth.getInsertDate());

        holder.cardView.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    DthFragment.newInstance( individual,locations, socialgroup, death )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return deathList.size();
    }

    public void dth(DeathViewModel deathViewModel) {
        deathList.clear();
        if(socialgroup != null)
            try {
                List<Death> list = deathViewModel.retrieveDth(socialgroup.getUuid());

                if (list != null) {
                    deathList.addAll(list);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No Death Recorded", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
