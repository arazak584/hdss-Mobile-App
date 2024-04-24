package org.openhds.hdsscapture.Adapter;

import android.graphics.Color;
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

public class IndividualViewAdapter extends RecyclerView.Adapter<IndividualViewAdapter.ViewHolder> {

    HouseMembersFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private Residency residency;
    private final List<Individual> individualList;
    private final Fragment fragment;
    private final IndividualClickListener individualClickListener;


    public interface IndividualClickListener {
        void onIndividualClick(Individual selectedIndividual);
    }

    public IndividualViewAdapter(Fragment fragment, Locations locations, Socialgroup socialgroup,IndividualClickListener listener) {
        this.fragment = fragment;
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.individualClickListener = listener;
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
            this.cardView = view.findViewById(R.id.searchedIindividual);
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
            if (individualClickListener != null) {
                individualClickListener.onIndividualClick(individual);
                //Log.d("IndividualViewAdapter", "Person clicked: " + individual.getExtId());
                //Toast.makeText(fragment.requireContext(), "Individual" + individual.age, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }


//    public void search(String selectedSpinnerItem, String searchText, IndividualViewModel individualViewModel) {
//        individualList.clear();
//        if (searchText != null && selectedSpinnerItem!= null) {
//            searchText = searchText.toLowerCase(Locale.getDefault());
//
//            try {
//                List<Individual> searchResults = individualViewModel.retrieveBySearch(selectedSpinnerItem, searchText);
//                individualList.addAll(searchResults);
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//                Toast.makeText(activity.getActivity(), "Error searching individuals", Toast.LENGTH_SHORT).show();
//            }
//        }
//        notifyDataSetChanged();
//        activity.dismissLoadingDialog();
//    }

//    public void search(String selectedSpinnerItem, String searchText, IndividualViewModel individualViewModel) {
//        individualList.clear();
//
//        // Check if either selectedSpinnerItem or searchText is not empty (non-null and non-empty) before proceeding with the search
//        if ((selectedSpinnerItem != null && !selectedSpinnerItem.isEmpty()) && (searchText != null && !searchText.isEmpty())) {
//            if (searchText != null) {
//                searchText = searchText.toLowerCase(Locale.getDefault());
//            }
//
//            try {
//                List<Individual> searchResults = individualViewModel.retrieveBySearch(selectedSpinnerItem, searchText);
//                individualList.addAll(searchResults);
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//                Toast.makeText(activity.getActivity(), "Error searching individuals", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        notifyDataSetChanged();
//        activity.dismissLoadingDialog();
//    }
//
//
//
//
//    public void pull(IndividualViewModel individualViewModel) {
//        individualList.clear();
//        if(socialgroup != null)
//            try {
//                List<Individual> list = individualViewModel.retrieveByLocationId(socialgroup.getUuid());
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
//        activity.dismissLoadingDialogs();
//    }

    public void filter(String charText,IndividualViewModel individualViewModel) {
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
