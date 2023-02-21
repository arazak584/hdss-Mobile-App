package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.HouseVisitFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class IndividualViewAdapter extends RecyclerView.Adapter<IndividualViewAdapter.ViewHolder> {

    HouseVisitFragment activity;
    LayoutInflater inflater;
    private Location location;
    //private Cluster clusterData;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;

    public IndividualViewAdapter(HouseVisitFragment activity, Residency residency, Location location, Socialgroup socialgroup) {
        this.activity = activity;
        this.location = location;
        this.residency = residency;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, nickname, permid, dob, gender, hhid;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.text_permid);
            this.firstname = view.findViewById(R.id.text_firstname);
            this.lastname = view.findViewById(R.id.text_lastname);
            this.nickname = view.findViewById(R.id.text_nickname);
            this.dob = view.findViewById(R.id.text_dob);
            this.hhid = view.findViewById(R.id.text_hhid);
            this.linearLayout = view.findViewById(R.id.searchedIindividual);
        }
    }

    @NonNull
    @Override
    public IndividualViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.individual_itemlist, parent, false);
        IndividualViewAdapter.ViewHolder viewHolder = new IndividualViewAdapter.ViewHolder(listItem);


        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);


        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());
        holder.nickname.setText(individual.getNickName());
        holder.dob.setText(individual.getDob());
        holder.hhid.setText(individual.socialgroup);

        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_main,
                    IndividualFragment.newInstance(individual, residency, location, socialgroup )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void filter(String charText, IndividualViewModel individualViewModel) {
        individualList.clear();
        if (charText != null && charText.length() > 4) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Individual> list = individualViewModel.retrieveBySearch(charText);

                if (list != null) {
                    individualList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(location != null)
                try {
                    List<Individual> list = individualViewModel.retrieveByLocationId(location.getExtId());

                    if (list != null) {
                        individualList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
        notifyDataSetChanged();
    }
}
