package org.openhds.hdsscapture.Adapter;

import android.graphics.Color;
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
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;
import org.openhds.hdsscapture.fragment.IndividualFragment;
import org.openhds.hdsscapture.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    SearchFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private Residency residency;
    private final List<Individual> individualList;

    public SearchAdapter(SearchFragment activity, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
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
            this.cardView = view.findViewById(R.id.searchIndividual);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.search_itemlist, parent, false);
        SearchAdapter.ViewHolder viewHolder = new SearchAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);


        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());
        holder.dob.setText(individual.getDob());
        holder.compno.setText(individual.getCompno());
        Integer end = individual.getEndType();
        String hh = individual.getHohID();
        if(end != null && end!=2){
            holder.hhid.setText(individual.getHohID());
            holder.hhid.setVisibility(View.VISIBLE);
        }else{
            holder.hhid.setText(null);
            holder.hhid.setVisibility(View.GONE);
        }
        //holder.hhid.setText(String.valueOf(individual.age));
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
            holder.status.setTextColor(Color.parseColor("#32CD32"));
        } else if (status == 2){
            holder.status.setText("(" + "Outmigrated" + ")");
            holder.status.setTextColor(Color.RED);
        } else{
            holder.status.setText("(" + "Duplicate" + ")");
            holder.status.setTextColor(Color.RED);
        }


        holder.cardView.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    IndividualFragment.newInstance( individual,residency, locations, socialgroup )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }
//
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

    public void search(String selectedSpinnerItem, String searchText, IndividualViewModel individualViewModel) {
        individualList.clear();
        // Check if searchText is not empty (non-null and non-empty) before proceeding with the search
        if (searchText != null && !searchText.isEmpty()) {
            if (searchText != null) {
                searchText = searchText.toLowerCase(Locale.getDefault());
            }
            try {
                // Perform a search using the IndividualViewModel
                List<Individual> searchResults = individualViewModel.retrieveBySearch(selectedSpinnerItem, searchText);
                // Add the search results to the adapter's data list
                individualList.addAll(searchResults);
            } catch (ExecutionException | InterruptedException e) {
                // Handle exceptions that may occur during the search operation
                e.printStackTrace();
                Toast.makeText(activity.getActivity(), "Error searching individuals", Toast.LENGTH_SHORT).show();
            }
        }
        // Notify the adapter that the underlying data has changed
        notifyDataSetChanged();
        // Dismiss the loading dialog associated with the activity
        activity.dismissLoadingDialog();
    }

    public void omg(IndividualViewModel individualViewModel) {
        individualList.clear();
        if(socialgroup != null)
            try {
                List<Individual> list = individualViewModel.retrieveReturn(ClusterFragment.selectedLocation.getCompno());

                if (list != null) {
                    individualList.addAll(list);
                }

                if (list.isEmpty()) {
                    //Toast.makeText(activity.getActivity(), "No Active Individual Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
