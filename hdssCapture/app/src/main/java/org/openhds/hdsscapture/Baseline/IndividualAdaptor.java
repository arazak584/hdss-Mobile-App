package org.openhds.hdsscapture.Baseline;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndividualAdaptor extends RecyclerView.Adapter<IndividualAdaptor.ViewHolder>{

    IndividualSummaryFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Residency residency;
    private final List<Individual> individualList;
    private ProgressDialog progress;
    private Context context;
    private final Fragment fragment;

    public IndividualAdaptor(Fragment fragment, Residency residency, Locations locations, Socialgroup socialgroup) {
        this.fragment = fragment;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
//        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, nickname, permid, dob, compno, gender,status;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.text_permid);
            this.firstname = view.findViewById(R.id.text_firstname);
            this.lastname = view.findViewById(R.id.text_lastname);
            this.nickname = view.findViewById(R.id.text_nickname);
            this.dob = view.findViewById(R.id.text_dob);
            this.gender = view.findViewById(R.id.text_gender);
            //this.compno = view.findViewById(R.id.text_compno);
            this.status = view.findViewById(R.id.text_status);
            this.cardView = view.findViewById(R.id.searchedIindividual);
        }
    }


    @NonNull
    @Override
    public IndividualAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.baseline_itemlist, parent, false);
        IndividualAdaptor.ViewHolder viewHolder = new IndividualAdaptor.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull IndividualAdaptor.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);


        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());
        holder.dob.setText(individual.getDob());
        //holder.compno.setText(individual.compextId);
        //holder.age.setText(String.valueOf(individual.getAge()));
        String otherName = individual.getOtherName();
        if (otherName == null || otherName.isEmpty()) {
            holder.nickname.setText("");
        } else {
            holder.nickname.setText("(" + otherName + ")");
        }
        Integer gender = individual.gender;
        if (gender == 1){
            holder.gender.setText("Male");
        }else{
            holder.gender.setText("Female");
        }
        Integer status = individual.endType;
        if (status == 1) {
            holder.status.setText("(" + "Active" + ")");
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        } else {
            holder.status.setText("(" + "Outmigrated" + ")");
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
        }
        Integer st = individual.complete;
        if (st != null) {
            holder.permid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            holder.firstname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            holder.lastname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        }else {
            holder.permid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
            holder.firstname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
            holder.lastname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
        }



        holder.cardView.setOnClickListener(v -> {
            fragment.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_baseline,
                    BaselineFragment.newInstance( individual, residency, locations, socialgroup)).commit();
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

//    public void pull(IndividualViewModel individualViewModel) {
//        individualList.clear();
//        if(socialgroup != null)
//            try {
//                List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getExtId());
//
//                if (list != null) {
//                    individualList.addAll(list);
//                }
//
//                if (list.isEmpty()) {
//                    Toast.makeText(activity.getActivity(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        notifyDataSetChanged();
//        activity.dismissLoadingDialog();
//    }

    public void filter(String charText,IndividualViewModel individualViewModel) {
        individualList.clear();
        if(socialgroup != null)
            try {
                List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getUuid());

                if (list != null) {
                    individualList.addAll(list);
                }

                if (list.isEmpty()) {
                    Toast.makeText(fragment.requireContext(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
