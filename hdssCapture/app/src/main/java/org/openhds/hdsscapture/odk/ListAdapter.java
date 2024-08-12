package org.openhds.hdsscapture.odk;

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

import org.openhds.hdsscapture.Adapter.IndividualViewAdapter;
import org.openhds.hdsscapture.Adapter.LocationAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private final IndClickListener indClickListener;
    LayoutInflater inflater;
    private final List<Individual> individualList;
    private Socialgroup socialgroup;
    private Locations locations;
    private final Fragment fragment;

    public interface IndClickListener {
        void onIndClick(Individual selectedInd);
    }

    public ListAdapter(Fragment fragment, Locations locations, Socialgroup socialgroup, IndClickListener listener) {
        this.fragment = fragment;
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.indClickListener = listener;
        individualList = new ArrayList<>();
        //inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, nickname, permid, dob, compno, gender,status, hhid;
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
            this.compno = view.findViewById(R.id.text_compno);
            this.status = view.findViewById(R.id.text_status);
            this.hhid = view.findViewById(R.id.text_hhid);
            this.cardView = view.findViewById(R.id.odkIndividual);
        }
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.indodk_itemlist, parent, false);
        ListAdapter.ViewHolder viewHolder = new ListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);


        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());
        holder.dob.setText(individual.getDob());
        holder.compno.setText(individual.getCompno());
        //holder.hhid.setText(individual.getHohID());
        //holder.hhid.setText(String.valueOf(individual.age));
        String hh  = individual.getGhanacard();
        if (hh == null || hh.isEmpty()){
            holder.hhid.setText("No Ghana Card");
            holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pop));
        }else{
            holder.hhid.setText(individual.getGhanacard());
        }

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
        String ph = individual.phone1;
        if (ph != null && ph.length() == 10) {
            holder.status.setText("(" + ph + ")");
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        } else {
            holder.status.setText("(" + "No Contact" + ")");
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
            if (indClickListener != null) {
                indClickListener.onIndClick(individual);
                //Log.d("IndividualViewAdapter", "Person clicked: " + individual.getExtId());
                //Toast.makeText(fragment.requireContext(), "Individual" + individual.age, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void filters(String charText, IndividualViewModel individualViewModel) {
        individualList.clear();
        if(socialgroup != null)
            try {
                List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getExtId());

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
