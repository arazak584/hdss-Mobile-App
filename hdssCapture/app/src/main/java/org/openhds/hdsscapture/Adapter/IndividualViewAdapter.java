package org.openhds.hdsscapture.Adapter;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import org.openhds.hdsscapture.Viewmodel.ClusterSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualSharedViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IndividualViewAdapter extends RecyclerView.Adapter<IndividualViewAdapter.ViewHolder> {

    HouseMembersFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private Residency residency;
    private final List<Individual> individualList;
    private final Fragment fragment;
    private final IndividualClickListener individualClickListener;

    private final IndividualSharedViewModel individualSharedViewModel;
    private String selectedUuid;


    public interface IndividualClickListener {
        void onIndividualClick(Individual selectedIndividual);
    }

    public IndividualViewAdapter(Fragment fragment, Locations locations, Socialgroup socialgroup,IndividualClickListener listener,IndividualSharedViewModel individualSharedViewModel) {
        this.fragment = fragment;
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.individualClickListener = listener;
        this.individualSharedViewModel = individualSharedViewModel;
        individualList = new ArrayList<>();
        //inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, nickname, permid, dob, compno, gender,status, hhid, age;
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
        holder.compno.setText(individual.getCompno());
        //holder.hhid.setText(individual.getHohID());
        //holder.hhid.setText(String.valueOf(individual.age));
        String hh  = individual.getGhanacard();
        if (hh == null || hh.isEmpty()){
            holder.hhid.setText("No National ID");
            holder.hhid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
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
        holder.gender.setText(gender == 1 ? "Male" : "Female");
        String ph = individual.phone1;
        if (ph != null && ph.length() > 5) {
            holder.status.setText("(" + ph + ")");
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        } else {
            holder.status.setText("(" + "No Contact" + ")");
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
        }
        Integer st = individual.complete;
        if (st != null) {
            holder.permid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            holder.firstname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
            holder.lastname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LimeGreen));
        }else {
            holder.permid.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
            holder.firstname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
            holder.lastname.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
        }

        // Check if this location is currently selected
        Individual currentSelected = individualSharedViewModel.getCurrentSelectedIndividual();
        boolean isSelected = selectedUuid != null && selectedUuid.equals(individual.getUuid());


        // Apply selection styling
        if (isSelected) {
            // Selected location styling
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LightGrey));
            // You can also add other visual indicators like border, different text color, etc.
        } else {
            // Default styling
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
        }


        holder.linearLayout.setOnClickListener(v -> {
            if (individualClickListener != null) {
                individualClickListener.onIndividualClick(individual);
                selectedUuid = individual.getUuid(); // ✅ set directly
                individualSharedViewModel.setSelectedIndividual(individual);
                notifyDataSetChanged(); // update UI
            }
        });

    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

//    public void filter(String charText,IndividualViewModel individualViewModel) {
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
//                    Toast.makeText(fragment.requireContext(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        notifyDataSetChanged();
//    }

//    public void filterAsync(String charText, IndividualViewModel individualViewModel) {
//        individualList.clear();
//
//        if (socialgroup != null) {
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            Handler handler = new Handler(Looper.getMainLooper());
//
//            executor.execute(() -> {
//                try {
//                    List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getExtId());
//
//                    handler.post(() -> {
//                        if (list != null && !list.isEmpty()) {
//                            individualList.addAll(list);
//                        } else {
//                            Toast.makeText(fragment.requireContext(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
//                        }
//
//                        notifyDataSetChanged();
//                    });
//
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }

//    public void setIndividualList(List<Individual> individuals) {
//        individualList.clear();
//        individualList.addAll(individuals);
//        notifyDataSetChanged();
//    }

    public void setIndividualList(List<Individual> individuals) {
        individualList.clear();
        individualList.addAll(individuals);

        // Re-check selected UUID from sharedViewModel
        Individual selected = individualSharedViewModel.getCurrentSelectedIndividual();
        if (selected != null) {
            selectedUuid = selected.getUuid();
        } else {
            selectedUuid = null;
        }
        notifyDataSetChanged();
    }


    public void refreshSelection() {
        notifyDataSetChanged();
    }

}
