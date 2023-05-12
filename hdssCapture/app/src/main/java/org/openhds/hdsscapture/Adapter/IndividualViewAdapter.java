package org.openhds.hdsscapture.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class IndividualViewAdapter extends RecyclerView.Adapter<IndividualViewAdapter.ViewHolder> {

    HouseMembersFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;
    private CaseItem caseItem;
    private ProgressDialog progress;
    private Context context;

    public IndividualViewAdapter(HouseMembersFragment activity, Residency residency, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, nickname, permid, dob, compno, gender,status;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.text_permid);
            this.firstname = view.findViewById(R.id.text_firstname);
            this.lastname = view.findViewById(R.id.text_lastname);
            this.nickname = view.findViewById(R.id.text_nickname);
            this.dob = view.findViewById(R.id.text_dob);
            this.gender = view.findViewById(R.id.text_gender);
            this.compno = view.findViewById(R.id.text_compno);
            this.status = view.findViewById(R.id.text_status);
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
        holder.dob.setText(individual.getDob());
        holder.compno.setText(individual.compextId);
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
            holder.status.setTextColor(Color.MAGENTA);
        } else {
            holder.status.setText("(" + "Outmigrated" + ")");
            holder.status.setTextColor(Color.RED);
        }
        Integer st = individual.complete;
        if (st != null) {
            holder.permid.setTextColor(Color.MAGENTA);
        }



        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance( individual, residency, locations, socialgroup,caseItem )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }


    public void search(String searchText, IndividualViewModel individualViewModel) {

        individualList.clear();
        if (searchText != null) {
            searchText = searchText.toLowerCase(Locale.getDefault());

            try {
                List<Individual> list = individualViewModel.retrieveBySearch(searchText);

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
        activity.dismissLoadingDialog();
    }

    public void pull(IndividualViewModel individualViewModel) {
        individualList.clear();
        if(socialgroup != null)
            try {
                List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getHouseExtId());

                if (list != null) {
                    individualList.addAll(list);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
        activity.dismissLoadingDialog();
    }



}
