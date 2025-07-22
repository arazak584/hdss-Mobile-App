package org.openhds.hdsscapture.Adapter;

import android.util.Log;
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
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.OmgFragment;
import org.openhds.hdsscapture.fragment.OmgAdapterFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class OmgAdapter extends RecyclerView.Adapter<OmgAdapter.ViewHolder>{

    OmgAdapterFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final List<Individual> individualList;
    private final String compno;

    public OmgAdapter(OmgAdapterFragment activity, Locations locations, Socialgroup socialgroup,String compno) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.compno = compno;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, insertDate;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.omg_permid);
            this.firstname = view.findViewById(R.id.omg_firstname);
            this.lastname = view.findViewById(R.id.omg_lastname);
            this.insertDate = view.findViewById(R.id.text_insertdate);
            this.cardView = view.findViewById(R.id.omgIndividual);
        }
    }

    @NonNull
    @Override
    public OmgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.omg_itemlist, parent, false);
        OmgAdapter.ViewHolder viewHolder = new OmgAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull OmgAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);
        Log.d("Outmigration", "OUT ID" + individual.uuid);
        Date insertDate = individual.insertDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedInsertDate = sdf.format(insertDate);

        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());
        holder.insertDate.setText(formattedInsertDate);
        //holder.insertDate.setText(individual.insertDate);

        holder.cardView.setOnClickListener(v -> {
            if (individual != null) {
                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                        OmgFragment.newInstance(locations, socialgroup,individual)).commit();
                Log.d("Outmigration", "OMG ID" + individual.uuid);
            }else {
                Toast.makeText(activity.getActivity(), "Error: Some data is null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void omg(IndividualViewModel individualViewModel) {
        individualList.clear();
        if(socialgroup != null)
            try {
                List<Individual> list = individualViewModel.retrieveOmg(compno);

                if (list != null) {
                    individualList.addAll(list);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No Outmigration Recorded", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }

}
